package com.eoss.brain.command.talk;

import com.eoss.brain.MessageObject;
import com.eoss.brain.Session;
import com.eoss.brain.command.wakeup.WakeupCommandNode;
import com.eoss.brain.net.Hook;
import com.eoss.brain.net.Node;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class AnswerResponseCommandNode extends ResponseCommandNode {

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public AnswerResponseCommandNode(Session session, String question) {
        super(session, question);
    }

    public List<String> generateChoices() {

        List<String> choices = new ArrayList<>();
        Set<Node> nodeSet;

        lock.writeLock().lock();
        try {
            nodeSet = new HashSet<>(session.context.nodeList);
        } finally {
            lock.writeLock().unlock();
        }

        final String input = responseText.toLowerCase();

        nodeSet.forEach(new Consumer<Node>() {
            @Override
            public void accept(Node node) {
                if (node.hookList().size()>1 && input.contains(node.hookList().get(0).text.toLowerCase())) {
                    String choice = node.hookList().get(1).text;
                    if (choice.contains(",")) {
                        choice = choice.split(",")[0].trim();
                    }
                    choices.add(choice);
                }
            }
        });

        return choices;
    }

    @Override
    public boolean matched(MessageObject messageObject) {
        session.solved(true);
        return true;
    }

    @Override
    public String execute(MessageObject messageObject) {
        MessageObject questionMessageObject = MessageObject.build(messageObject, responseText + " " + messageObject.toString());
        return new TalkCommandNode(session, WakeupCommandNode.KEY).execute(questionMessageObject);
    }
}
