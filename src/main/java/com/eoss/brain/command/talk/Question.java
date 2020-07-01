package com.eoss.brain.command.talk;

import com.eoss.brain.Session;
import com.eoss.brain.net.Hook;
import com.eoss.brain.net.Node;

import java.util.*;
import java.util.function.Consumer;

public class Question {

    public final String parent;
    public final String label;
    public final String imageURL;
    public final List<Choice> choices;

    public Set<Node> nodeSet;
    public List<Node> defaultChoices;

    public Question(String parent, String label, String imageURL, List<Choice> choices) {
        this.parent = parent;
        this.label = label;
        this.imageURL = imageURL;
        this.choices = choices;
    }

    public Question(Session session, String title, String params) {

        List<Node> nodeList = session.context.nodeList;

        String [] titles = title.split(" ");
        String firstTitles = titles[0].toLowerCase();
        if (firstTitles.startsWith("https") && (firstTitles.endsWith("png") || firstTitles.endsWith("jpg") || firstTitles.endsWith("jpeg"))) {
            imageURL = titles[0];
            label = title.replace(imageURL, "").trim();
        } else {
            imageURL = null;
            label = title;
        }

        List<String> paramList = Arrays.asList(params.split(" "));
        String foundParent = null;
        for(String param:paramList) {
            if (param.startsWith("@")) {
                foundParent = param;
            }
        }
        parent = foundParent;

        choices = new ArrayList<>();
        nodeSet = new HashSet<>();
        defaultChoices = new ArrayList<>();

        nodeList.forEach(new Consumer<Node>() {
            @Override
            public void accept(Node node) {

                List<Hook> hookList = node.hookList();

                /**
                 * Is Child
                 */
                boolean matched = false;
                for (Hook hook:hookList) {
                    if (paramList.contains(hook.text)) {
                        matched = true;
                        break;
                    }
                }

                if (!matched) return;

                boolean isDefaultChoice = true;
                String label = "";
                for (Hook hook:hookList) {
                    if (hook.text.startsWith("@")) {
                        continue;
                    }

                    isDefaultChoice = false;
                    if (hook.text.contains(",")) {
                        continue;
                    }
                    label += hook.text + " ";
                }

                if (isDefaultChoice) {
                    defaultChoices.add(node);
                    return;
                }

                label = label.trim();

                if (label.isEmpty()) return;

                /**
                 * Variable Supported for label
                 */
                label = session.parameterized(null, label);

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

                if ( (!linkURL.startsWith("https://") || !linkURL.contains("://") /*For App*/) && !linkURL.startsWith("tel:") && !linkURL.startsWith("mailto:")) {
                    linkURL = null;
                } else {
                    /**
                     * Variable Supported for Link
                     */
                    linkURL = session.parameterized(null, linkURL);
                }

                choices.add(new Choice(parent, label, imageURL, linkURL));

                //Resplit Hook by Locale
                Node tempNode = Node.build(session.context.split(Hook.toString(hookList)));
                tempNode.setResponse(node.response());
                nodeSet.add(tempNode);

            }
        });

    }

    public boolean hasImage() {
        return imageURL != null;
    }
}