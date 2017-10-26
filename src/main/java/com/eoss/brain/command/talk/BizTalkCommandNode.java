package com.eoss.brain.command.talk;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.command.CommandNode;
import com.eoss.brain.net.Context;
import com.eoss.brain.net.ContextListener;
import com.eoss.brain.net.Node;
import com.eoss.brain.NodeEvent;
import com.eoss.brain.command.http.GoogleCommandNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class BizTalkCommandNode extends CommandNode {

    private final List<String> lowConfidenceKeys;
    private final List<String> confirmKeys;
    private final List<String> cancelKeys;
    private final String cancelMsg;
    private final List<String> confirmMsg;

    public BizTalkCommandNode(Session session, List<String> lowConfidenceKeys, List<String> confirmKeys, List<String> cancelKeys, String cancelMsg, List<String> confirmMsg) {
        super(session);
        if (lowConfidenceKeys ==null|| lowConfidenceKeys.size()!=4) throw new IllegalArgumentException("lowConfidenceKeys must have 4 elements");
        this.lowConfidenceKeys = lowConfidenceKeys;

        if (confirmKeys ==null|| confirmKeys.isEmpty()) throw new IllegalArgumentException("confirmKeys must have elements");
        this.confirmKeys = confirmKeys;

        if (cancelKeys ==null|| cancelKeys.isEmpty()) throw new IllegalArgumentException("cancelKeys must have elements");
        this.cancelKeys = cancelKeys;

        this.cancelMsg = cancelMsg;

        if (confirmMsg ==null|| confirmMsg.size()!=3) throw new IllegalArgumentException("confirmMsg must have 3 elements");
        this.confirmMsg = confirmMsg;
    }

    @Override
    public boolean matched(MessageObject messageObject) {
        return true;
    }

    @Override
    public String execute(final MessageObject messageObject) {

        messageObject.attributes.put("wordCount", session.context.splitToList(messageObject.toString()).size());

        if (session.mode!=null && !session.mode.trim().isEmpty()) {
            messageObject.attributes.put("mode", session.mode.trim());
        }

        final Set<Node> activeNodeSet = new HashSet<>();

        session.context.matched(messageObject, session.activeNodePool, new ContextListener() {
            @Override
            public void callback(NodeEvent nodeEvent) {
                nodeEvent.node.feed(messageObject);
                activeNodeSet.add(nodeEvent.node);
            }
        });

        if (activeNodeSet.isEmpty()) {
            session.context.matched(messageObject, new ContextListener() {
                @Override
                public void callback(NodeEvent nodeEvent) {
                    nodeEvent.node.feed(messageObject);
                    if (!activeNodeSet.add(nodeEvent.node)) {
                        activeNodeSet.remove(nodeEvent.node);
                        activeNodeSet.add(nodeEvent.node);
                    }
                }
            });
        }

        List<Node> maxActiveNodeList = Context.findActiveNodes(activeNodeSet, 0.70f);
        Node maxActiveNode;
        float confidenceRate;
        String responseText;
        if (maxActiveNodeList.isEmpty()) {
            maxActiveNode = null;
            confidenceRate = 0.0f;
            responseText = cancelMsg;
        } else {

            maxActiveNode = maxActiveNodeList.get(0);
            responseText = maxActiveNode.maxActiveResponseText();

            if (maxActiveNodeList.size()==1) {
                confidenceRate = maxActiveNode.maxActiveResponse.active;
            } else {
                confidenceRate = maxActiveNode.maxActiveResponse.active / maxActiveNodeList.size();
            }
        }

        final float MIN_LOW = 0.05f;
        if (confidenceRate <= MIN_LOW) {

            if (session.learning) {
                session.insert(new LowConfidenceProblemCommandNode(session, messageObject, lowConfidenceKeys.get(0), lowConfidenceKeys.get(1), lowConfidenceKeys.get(2)));
                responseText = "Learning mode: " + messageObject + " " + lowConfidenceKeys.get(3);
            } else {
                String query = messageObject.toString().trim();
                if (session.context.domain!=null && !session.context.domain.trim().isEmpty()) {
                    query += " site:" + session.context.domain;
                }
                new GoogleCommandNode(session, null, 1).execute(MessageObject.build(messageObject,  query));
                responseText = "";
            }

        } else if (confidenceRate < 0.5f) {
/*            System.out.println(messageObject.toString()+"2");
            List<Response> responseList = new ArrayList<>();
            for (Node node:maxActiveNodeList) {
                responseList.add(node.maxActiveResponse);
            }*/

            session.insert(new ConfirmProblemCommandNode(session, messageObject.copy(), confirmKeys,cancelKeys, confirmMsg, cancelMsg, maxActiveNodeList, lowConfidenceKeys));

            String multiResponse;
/*            if (maxActiveNodeList.size()>1) {
                String responseText2 = maxActiveNodeList.get(1).maxActiveResponseText().split("\\s+", 2)[0];
                multiResponse = confirmMsg.get(0) + System.lineSeparator() + responseText.split("\\s+", 2)[0] + System.lineSeparator() + confirmMsg.get(1) + System.lineSeparator() + responseText2;
            } else {*/
                responseText = maxActiveNode.hooksString();
                multiResponse = confirmMsg.get(0) + " " + responseText + " " + confirmMsg.get(2);
           /* }*/

            if (session.context.listener!=null) {
                session.context.listener.callback(new NodeEvent(this, MessageObject.build(messageObject, multiResponse), NodeEvent.Event.LateReply));
                responseText = "";
            } else {
                responseText = multiResponse.replace(System.lineSeparator(), " ");
            }
        }

        else if (confidenceRate <= 0.75f) {

            //responseText += "?";
            session.insert(new ConfirmProblemCommandNode(session, messageObject.copy(), confirmKeys,cancelKeys, confirmMsg, cancelMsg, maxActiveNodeList, lowConfidenceKeys));
            String multiResponse;
            responseText = maxActiveNode.hooksString();
            multiResponse = confirmMsg.get(0) + " " + responseText + " " + confirmMsg.get(2);
            if (session.context.listener!=null) {
                session.context.listener.callback(new NodeEvent(this, MessageObject.build(messageObject, multiResponse), NodeEvent.Event.LateReply));
                responseText = "";
            } else {
                responseText = multiResponse.replace(System.lineSeparator(), " ");
            }
        } /*else if (confidenceRate > 1) {
            //Super Confidence

        }
        */
        if (confidenceRate > MIN_LOW) {

            session.setLastEntry(messageObject, maxActiveNode.maxActiveResponse);
            maxActiveNode.maxActiveResponse.clear();
        }

        session.add(activeNodeSet);

        return responseText;
    }
}
