package com.eoss.brain.command.talk;

import com.eoss.brain.MessageObject;
import com.eoss.brain.Session;
import com.eoss.brain.command.wakeup.WakeupCommandNode;
import com.eoss.brain.net.Node;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class AnswerResponseCommandNode extends ResponseCommandNode {

    public static class Choice {
        public final String label;
        public final String imageURL;
        public final String linkURL;

        public Choice(String label, String imageURL, String linkURL) {
            this.label = label;
            this.imageURL = imageURL;
            this.linkURL = linkURL;
        }

        public boolean isLabel() {
            return imageURL == null && linkURL == null;
        }

        public boolean isImageLabel() {
            return imageURL != null && linkURL == null;
        }

        public boolean isLinkLabel() {
            return imageURL == null && linkURL != null;
        }

        public boolean isImageLinkLabel() {
            return imageURL != null && linkURL != null;
        }
    }

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public AnswerResponseCommandNode(Session session, String question) {
        super(session, question);
    }

    public List<Choice> generateChoices() {

        List<Choice> choices = new ArrayList<>();
        List<Node> nodeList;

        lock.writeLock().lock();
        try {
            nodeList = new ArrayList<>(session.context.nodeList);
        } finally {
            lock.writeLock().unlock();
        }

        final String input = responseText.toLowerCase();

        nodeList.forEach(new Consumer<Node>() {
            @Override
            public void accept(Node node) {
                if (node.hookList().size()>1 && input.contains(node.hookList().get(0).text.toLowerCase())) {

                    String label = node.hookList().get(1).text;
                    if (label.contains(",")) {
                        label = label.split(",")[0].trim();
                    }

                    String [] responses = node.response().split(" ");

                    String imageURL = responses[0].trim().toLowerCase();

                    if (imageURL.startsWith("https://") &&
                            (imageURL.endsWith("png") ||
                                    imageURL.endsWith("jpg") ||
                                        imageURL.endsWith("jpeg"))) {
                        imageURL = responses[0].trim();
                    } else {
                        imageURL = null;
                    }

                    String linkURL = responses[responses.length-1].trim();

                    if (!linkURL.startsWith("https://") && !linkURL.startsWith("tel:")) {
                        linkURL = null;
                    }

                    choices.add(new Choice(label, imageURL, linkURL));
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
