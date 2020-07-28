package com.brainy.command.talk.menu;

import com.brainy.MessageObject;
import com.brainy.NodeEvent;
import com.brainy.Session;
import com.brainy.command.CommandNode;
import com.brainy.command.talk.Key;
import com.brainy.command.talk.LowConfidenceProblemCommandNode;
import com.brainy.command.talk.Question;
import com.brainy.command.wakeup.WakeupCommandNode;
import com.brainy.net.Context;
import com.brainy.net.ContextListener;
import com.brainy.net.Node;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class MenuTalkCommandNode extends CommandNode {

    protected ReadWriteLock lock = new ReentrantReadWriteLock();

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

        session.context.matched(messageObject, new ContextListener() {
            @Override
            public void callback(NodeEvent nodeEvent) {

                nodeEvent.node.feed(messageObject);
                activeNodeSet.add(nodeEvent.node);

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

                    List<Question> questionList = new ArrayList<>();

                    String title, params;
                    for (Node node:questionNodes) {

                        title = node.response();
                        int lastIndexOfComma = title.lastIndexOf(",");
                        if (lastIndexOfComma!=-1) {
                            params = title.substring(lastIndexOfComma + 1, title.length()-1).trim();
                            title = title.substring(0, lastIndexOfComma);
                            questionList.add(new Question(session, title, params));
                        }

                    }

                    return Question.toString(questionList);
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

        //Low confidence
        if (confidenceRate <= LOWER_BOUND) {

            String unknownConfig = session.context.properties.get("unknown");

            if (unknownConfig==null) {
                return "";
            } else if (!unknownConfig.endsWith("!")) {
                return unknownConfig;
            }

            messageObject.setText(unknownConfig);

            return messageObject.headIncluded() + new MenuTalkCommandNode(session, WakeupCommandNode.KEY).execute(messageObject.forward());
        }

        if (maxActiveNode!=null) {

            session.setLastEntry(messageObject, maxActiveNode);

            if (session.reachMaximumRoute()) {

                return "Too many forwarding :(, Please review your graph. [Round=" + session.getRoundCount() + "]";
            }

            //Clean MessageObject
            String input = messageObject.toString();
            StringBuilder forwardInput = new StringBuilder(maxActiveNode.clean(input));
            MessageObject forwardMessageObject = MessageObject.build(messageObject, forwardInput.toString().trim());
            return MenuResponseCommandNode.build(session, responseText).execute(forwardMessageObject);
        }

        return responseText;
    }
}
