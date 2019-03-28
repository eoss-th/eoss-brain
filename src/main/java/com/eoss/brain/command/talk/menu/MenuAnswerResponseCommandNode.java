package com.eoss.brain.command.talk.menu;

import com.eoss.brain.MessageObject;
import com.eoss.brain.NodeEvent;
import com.eoss.brain.Session;
import com.eoss.brain.command.talk.Question;
import com.eoss.brain.command.talk.ResponseCommandNode;
import com.eoss.brain.command.wakeup.WakeupCommandNode;
import com.eoss.brain.net.Context;
import com.eoss.brain.net.ContextListener;
import com.eoss.brain.net.Hook;
import com.eoss.brain.net.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class MenuAnswerResponseCommandNode extends ResponseCommandNode {

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    private Question question;

    public MenuAnswerResponseCommandNode(Session session, String question) {
        super(session, question);
    }

    public Question createQuestion(String title) {
        lock.writeLock().lock();
        try {
            question = new Question(new ArrayList<>(session.context.nodeList), title, responseText);
            return question;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean matched(MessageObject messageObject) {
        session.solved(true);
        return true;
    }

    @Override
    public String execute(MessageObject messageObject) {

        session.clearProblem();

        if (question!=null) {

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
                session.route(maxActiveNode);

                //Clean MessageObject
                String input = messageObject.toString();
                StringBuilder forwardInput = new StringBuilder(maxActiveNode.clean(input));
                MessageObject forwardMessageObject = MessageObject.build(messageObject, forwardInput.toString().trim());
                return MenuResponseCommandNode.build(session, maxActiveNode.response()).execute(forwardMessageObject);
            }

        }

        MessageObject questionMessageObject = MessageObject.build(messageObject, messageObject.toString());
        questionMessageObject.split();

        return new MenuTalkCommandNode(session, WakeupCommandNode.KEY).execute(questionMessageObject);
    }
}