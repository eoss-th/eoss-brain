package com.eoss.brain.command.talk;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.net.Hook;
import com.eoss.brain.net.Node;

import java.util.List;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class ConfirmProblemCommandNode extends ProblemCommandNode {

    public final List<String> keys;

    public final List<String> cancelKeys;

    public final String cancelMsg;

    public List<Node> maxActive;

    public List<String> confirmMsg;

    public List<String> lowConfidenceKeys;


    String cancelReason;

    MessageObject problemMessageObject;

/*    List<Response> responseList;*/

    public ConfirmProblemCommandNode(Session session, MessageObject problemMessageObject, List<String> keys, List<String> cancelKeys,List<String> confirmMsg, String cancelMsg, List<Node> maxActive, List<String> lowConfidenceKeys) {
        super(session);
        this.problemMessageObject = problemMessageObject;
/*        this.responseList = responseList;*/
        this.keys = keys;
        this.cancelKeys = cancelKeys;
        this.cancelMsg = cancelMsg;
        this.maxActive = maxActive;
        this.confirmMsg = confirmMsg;
        this.lowConfidenceKeys = lowConfidenceKeys;
    }

    @Override
    public boolean matched(MessageObject messageObject) {

        cancelReason = null;

        try {

            if (keys.contains(messageObject.toString())) {
                return true;
            }

            if (cancelKeys.contains(messageObject.toString())) {

                cancelReason = cancelMsg;
/*                if (session.learning) {
                } else {
                    cancelReason = cancelMsg;
                    String query =problemMessageObject.toString().trim();
                    if (session.context.domain!=null && !session.context.domain.trim().isEmpty()) {
                        query += " site:" + session.context.domain;
                    }
                    new GoogleCommandNode(session, null, 1).execute(MessageObject.build(messageObject,  query));
                }*/
                return true;
            }

        } finally {

            session.solved(true);

        }

        return false;
    }

    @Override
    public String execute(MessageObject messageObject) {

        session.clearPool();

        if (cancelReason!=null) {

            if(!maxActive.isEmpty()){
                maxActive.remove(0);

                if(!maxActive.isEmpty()){
                    Node maxActiveNode = maxActive.get(0);
                    session.insert(new ConfirmProblemCommandNode(session, messageObject.copy(), keys, cancelKeys,confirmMsg ,cancelMsg, maxActive,lowConfidenceKeys));
                    String responseText = Hook.toString(maxActiveNode.hookList());
                    String multiResponse = confirmMsg.get(0) + " " + responseText + " " + confirmMsg.get(2);
                    return multiResponse;
                }
            }

            if(session.learning){
                Session.Entry lastEntry = session.lastEntry();
                session.insert(new LowConfidenceProblemCommandNode(session, lastEntry.messageObject, lowConfidenceKeys.get(0), lowConfidenceKeys.get(1), lowConfidenceKeys.get(2)));
                return "Learning match: " + lastEntry.messageObject + " " + lowConfidenceKeys.get(3);
            }


            return cancelReason;

        }

        if(!maxActive.isEmpty()){
            maxActive.get(0).feed(session.lastEntry().messageObject);
            String response =  maxActive.get(0).response();
            session.setLastEntry(session.lastEntry().messageObject, maxActive.get(0));
            maxActive.get(0).release();
            return response;
        }
        return "!! Empty Response";
/*        if (responseList.size()==1) {
            Response response = responseList.get(0);
            response.feedback(messageObject, 0.1f);
            session.setLastEntry(problemMessageObject, responseList.get(0));
            return Node.decode(responseList.get(0).text);
        }


        for (Response response:responseList) {
            if (response.text.contains(messageObject.toString())) {
                response.feedback(messageObject, 0.1f);
                session.setLastEntry(problemMessageObject, response);
                return Node.decode(response.text);
            }
        }

        return new TalkCommandNode(session).execute(messageObject.copy());*/
    }
}
