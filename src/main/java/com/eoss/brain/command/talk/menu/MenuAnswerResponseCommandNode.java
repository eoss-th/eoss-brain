package com.eoss.brain.command.talk.menu;

import com.eoss.brain.MessageObject;
import com.eoss.brain.NodeEvent;
import com.eoss.brain.Session;
import com.eoss.brain.command.talk.Question;
import com.eoss.brain.command.talk.ResponseCommandNode;
import com.eoss.brain.command.wakeup.WakeupCommandNode;
import com.eoss.brain.net.Context;
import com.eoss.brain.net.ContextListener;
import com.eoss.brain.net.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MenuAnswerResponseCommandNode extends ResponseCommandNode {

    private Question question;

    public MenuAnswerResponseCommandNode(Session session, String question) {
        super(session, question);
    }

    public Question createQuestion(String title) {
        question = new Question(session, title, responseText);
        return question;
    }

    @Override
    public boolean matched(MessageObject messageObject) {
        session.solved(true);
        return true;
    }

    @Override
    public String execute(MessageObject messageObject) {

        if (question!=null) {

            messageObject.split(session.context);

            List<String> parentList = new ArrayList<>();
            List<String> wordList = (List<String>) messageObject.attributes.get("wordList");
            for (String word:wordList) {
                if (word.startsWith("@")) {
                    parentList.add(word);
                }
            }

            boolean isParent = question.parent!=null && parentList.contains(question.parent);

            if (parentList.isEmpty() || isParent) {
                final Set<Node> activeNodeSet = new HashSet<>();
                session.context.matched(messageObject, question.nodeSet, new ContextListener() {
                    @Override
                    public void callback(NodeEvent nodeEvent) {
                        nodeEvent.node.feed(messageObject);
                        activeNodeSet.add(nodeEvent.node);
                    }
                });

                List<Node> maxActiveNodes = Context.findMaxActiveNodes(activeNodeSet);

                //Retry with Default Choices if any
                if (maxActiveNodes==null) {

                    maxActiveNodes = question.defaultChoices;

                }

                if (maxActiveNodes!=null && maxActiveNodes.size()==1) {

                    Node maxActiveNode = maxActiveNodes.get(0);
                    session.setLastEntry(messageObject, maxActiveNode);

                    //Clean MessageObject
                    String input = messageObject.toString();
                    StringBuilder forwardInput = new StringBuilder(maxActiveNode.clean(input));
                    MessageObject forwardMessageObject = MessageObject.build(messageObject, forwardInput.toString().trim());
                    return MenuResponseCommandNode.build(session, maxActiveNode.response()).execute(forwardMessageObject);
                }
            }

        }

        MessageObject questionMessageObject = MessageObject.build(messageObject, messageObject.toString());
        questionMessageObject.split();

        return new MenuTalkCommandNode(session, WakeupCommandNode.KEY).execute(questionMessageObject);
    }
}
