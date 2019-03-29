package com.eoss.brain.command.talk;

import com.eoss.brain.MessageObject;
import com.eoss.brain.net.Hook;
import com.eoss.brain.net.Node;

import java.util.*;
import java.util.function.Consumer;

public class Question {
    public final String imageURL;
    public final String label;
    public final List<Choice> choices;
    public Set<Node> nodeSet;
    public List<Node> defaultChoices;

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

        final String parent;
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
                nodeSet.add(node);

            }
        });

    }

    public boolean hasImage() {
        return imageURL != null;
    }
}