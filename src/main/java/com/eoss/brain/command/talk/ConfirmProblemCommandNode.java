package com.eoss.brain.command.talk;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.command.http.GoogleCommandNode;
import com.eoss.brain.net.Node;

import java.util.List;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class ConfirmProblemCommandNode extends ProblemCommandNode {

    public final List<String> keys;

    public final List<String> cancelKeys;

    public final String cancelMsg;

    String cancelReason;

    MessageObject problemMessageObject;

    List<Response> responseList;

    public ConfirmProblemCommandNode(Session session, MessageObject problemMessageObject, List<Response> responseList, List<String> keys, List<String> cancelKeys, String cancelMsg) {
        super(session);
        this.problemMessageObject = problemMessageObject;
        this.responseList = responseList;
        this.keys = keys;
        this.cancelKeys = cancelKeys;
        this.cancelMsg = cancelMsg;
    }

    @Override
    public boolean matched(MessageObject messageObject) {

        cancelReason = null;

        try {

            if (keys.contains(messageObject.toString())) {
                return true;
            }

            if (cancelKeys.contains(messageObject.toString())) {

                if (session.learning) {
                    cancelReason = cancelMsg;
                } else {
                    cancelReason = "";
                    String query =problemMessageObject.toString().trim();
                    if (session.context.domain!=null && !session.context.domain.trim().isEmpty()) {
                        query += " site:" + session.context.domain;
                    }
                    new GoogleCommandNode(session, null, 1).execute(MessageObject.build(messageObject,  query));
                }
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

        if (cancelReason!=null) return cancelReason;

        if (responseList.size()==1) {
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

        return new TalkCommandNode(session).execute(messageObject.copy());
    }
}
