package com.eoss.brain.command.talk;

import com.eoss.brain.MessageObject;
import com.eoss.brain.NodeEvent;
import com.eoss.brain.Session;
import com.eoss.brain.command.CommandNode;
import com.eoss.brain.net.Context;
import com.eoss.brain.net.ContextListener;
import com.eoss.brain.net.Node;

import java.util.*;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class MenuTalkCommandNode extends CommandNode {

    private final Key lowConfidenceKey;

    public MenuTalkCommandNode(Session session, Key lowConfidenceKey) {
        super(session);
        this.lowConfidenceKey = lowConfidenceKey;
    }

    @Override
    public boolean matched(MessageObject messageObject) {
        return true;
    }

    @Override
    public String execute(MessageObject messageObject) {

        if (session.mode!=null && !session.mode.trim().isEmpty()) {
            messageObject.attributes.put("mode", session.mode.trim());
        }

        if (!messageObject.isSplitted()) {
            messageObject.split(session.context);
        }

        final Set<Node> activeNodeSet = new HashSet<>();

        List<Node> alreadyRoutedNodeList = new ArrayList<>();

        session.context.matched(messageObject, new ContextListener() {
            @Override
            public void callback(NodeEvent nodeEvent) {
                /**
                 * Protect from Cyclic Forwarding
                 */
                if (session.canRoute(nodeEvent.node)) {
                    nodeEvent.node.feed(messageObject);
                    activeNodeSet.add(nodeEvent.node);
                } else {
                    alreadyRoutedNodeList.add(nodeEvent.node);
                }
            }
        });

        List<Node> maxActiveNodes = Context.findMaxActiveNodes(activeNodeSet);

        final float confidenceRate;
        String responseText;
        Node maxActiveNode;
        if (maxActiveNodes==null) {
            maxActiveNode = null;
            confidenceRate = 0.0f;
            responseText = "";
        } else {

            //Could it be Question Menu?
            if (maxActiveNodes.size()>1) {

                List<Node> questionNodes = new ArrayList<>();
                for (Node node:maxActiveNodes) {
                    if (node.response().endsWith("?")) {
                        questionNodes.add(node);
                    }
                }

                //Generate Questions
                if (!questionNodes.isEmpty()) {

                    List<AnswerResponseCommandNode.Question> questionList = new ArrayList<>();

                    String title, params;
                    List<AnswerResponseCommandNode.Choice> choiceList;
                    for (Node node:questionNodes) {

                        title = node.response();
                        int lastIndexOfComma = title.lastIndexOf(",");
                        if (lastIndexOfComma!=-1) {
                            params = title.substring(lastIndexOfComma + 1, title.length()-1).trim();
                            title = title.substring(0, lastIndexOfComma);
                            questionList.add(new AnswerResponseCommandNode.Question(session.context.nodeList, title, params));
                        }

                    }

                    if (session.listener != null) {
                        messageObject.attributes.put("Question", questionList);
                        session.listener.callback(new NodeEvent(null, messageObject, NodeEvent.Event.Question));
                    }

                    return "";
                }

                //Random Pickup!
                maxActiveNode = maxActiveNodes.get(session.random.nextInt(maxActiveNodes.size()));

            } else {
                maxActiveNode = maxActiveNodes.get(0);
            }

            confidenceRate = maxActiveNode.active();
            responseText = maxActiveNode.response();
        }

        final float UPPER_BOUND = 0.5f;
        final float LOWER_BOUND = 0.05f;

        if (session.learning && confidenceRate <= LOWER_BOUND) {

            responseText = messageObject + " " + lowConfidenceKey.questMsg;
            session.insert(new LowConfidenceProblemCommandNode(session, messageObject, lowConfidenceKey));
            session.clearPool();
            return responseText;
        }

        //Super Confidence
        if (confidenceRate > 1) {

            if (session.listener != null) {
                session.listener.callback(new NodeEvent(maxActiveNode, messageObject, NodeEvent.Event.SuperConfidence));
            }

        } else if (confidenceRate > UPPER_BOUND) {

            //Nothing TO DO; Just Answer

        } else if (confidenceRate > LOWER_BOUND) {

            //hesitation
            if (session.listener != null) {
                session.listener.callback(new NodeEvent(maxActiveNode, messageObject, NodeEvent.Event.HesitateConfidence));
            }

        } else if (!alreadyRoutedNodeList.isEmpty()) {

            responseText = session.lastEntry().node.response();

        } else {

            responseText = "";
            if (session.listener!=null) {
                //Warning! maxActiveNode may be null
                session.listener.callback(new NodeEvent(maxActiveNode, messageObject, NodeEvent.Event.LowConfidence));
            }

        }

        if (maxActiveNode!=null) {

            session.setLastEntry(messageObject, maxActiveNode);

            session.route(maxActiveNode);

            //Clean MessageObject
            String input = messageObject.toString();
            StringBuilder forwardInput = new StringBuilder(maxActiveNode.clean(input));
            MessageObject forwardMessageObject = MessageObject.build(messageObject, forwardInput.toString().trim());
            return ResponseCommandNode.build(session, responseText).execute(forwardMessageObject);
        }

        return responseText;
    }
}
