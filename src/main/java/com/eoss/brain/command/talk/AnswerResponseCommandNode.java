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

    public static class Choice {
        public final String parent;
        public final String label;
        public final String imageURL;
        public final String linkURL;

        public Choice(String parent, String label, String imageURL, String linkURL) {
            this.parent = parent;
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

    public static class Question {
        public final String imageURL;
        public final String label;
        public final List<Choice> choices;

        public Question(List<Node> nodeList, String title, String params) {

            String [] titles = title.split(" ");
            String firstTitles = titles[0].toLowerCase();
            if (firstTitles.startsWith("https") && (firstTitles.endsWith("png") || firstTitles.endsWith("jpg") || firstTitles.endsWith("jpeg"))) {
                imageURL = titles[0];
                label = title.replace(imageURL, "").trim();
            } else {
                imageURL = null;
                label = title;
            }

            choices = new ArrayList<>();

            nodeList.forEach(new Consumer<Node>() {
                @Override
                public void accept(Node node) {
                    if (node.hookList().size()>1) {

                        String parent = null;
                        List<Hook> hookList = node.hookList();
                        List<String> paramList = Arrays.asList(params.split(" "));

                        /**
                         * Intersection Matched Check!
                         */
                        boolean matched = false;
                        for (Hook hook:hookList) {
                            if (paramList.contains(hook.text)) {
                                matched = true;
                                break;
                            }
                        }
                        if (!matched) return;

                        String label = "";
                        for (Hook hook:hookList) {
                            if (hook.text.startsWith("@")) {
                                parent = hook.text;
                                continue;
                            }
                            if (hook.text.contains(",")) {
                                continue;
                            }
                            label += hook.text + " ";
                        }
                        label = label.trim();
                        if (label.isEmpty()) return;

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

                        choices.add(new Choice(parent, label, imageURL, linkURL));
                    }
                }
            });

        }

        public boolean hasImage() {
            return imageURL != null;
        }
    }

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public AnswerResponseCommandNode(Session session, String question) {
        super(session, question);
    }

    public Question createQuestion(String title) {
        lock.writeLock().lock();
        try {
            return new Question(new ArrayList<>(session.context.nodeList), title, responseText);
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

        //Replace parent @xyz if any
        String params = messageObject.toString().replace(responseText, "").trim();

        MessageObject questionMessageObject = MessageObject.build(messageObject, responseText + " " + params);
        questionMessageObject.split();
        return new MenuTalkCommandNode(session, WakeupCommandNode.KEY).execute(questionMessageObject);
    }
}
