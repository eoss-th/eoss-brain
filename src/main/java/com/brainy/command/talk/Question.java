package com.brainy.command.talk;

import com.brainy.Session;
import com.brainy.net.Hook;
import com.brainy.net.Node;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.function.Consumer;

import static java.util.Optional.ofNullable;

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

    /**
     * @return
     * Question: <Label>
     * Id: <parent>
     * Image: <imageURL>
     *
     * Choice 1 label   imageURL    link
     * Choice 2
     * ...
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(
                String.format("Question: %s\nId: %s\nImage: %s",
                        ofNullable(label).orElse(""),
                        ofNullable(parent).orElse(""),
                        ofNullable(imageURL).orElse("")
                )
        );
        sb.append("\n");
        for (Choice choice: choices) {
            sb.append("\n\t" + choice);
        }

        return sb.toString();
    }

    public JSONObject toJSONObject() {

        JSONObject questionObj = new JSONObject();
        questionObj.put("parent", parent);
        questionObj.put("label", label);
        questionObj.put("imageURL", imageURL);

        JSONArray choiceArray = new JSONArray();
        JSONObject choiceObj;
        for (Choice choice:choices) {
            choiceObj = new JSONObject();
            choiceObj.put("parent", choice.parent);
            choiceObj.put("label", choice.label);
            choiceObj.put("imageURL", choice.imageURL);
            choiceObj.put("linkURL", choice.linkURL);
            choiceArray.put(choiceObj);
        }
        questionObj.put("choices", choiceArray);

        return questionObj;
    }

    public static String toString(List<Question> questionList) {
        StringBuilder sb = new StringBuilder();
        for (Question question:questionList) {
            sb.append(question + "\n\n\n");
        }
        return sb.toString().trim();
    }

    public static Question build(String text) {
        String [] lines = text.split("\n");
        String label = lines[0].replaceFirst("Question: ", "").trim();
        String parentId = lines[1].replaceFirst("Id: ", "").trim();
        String imageURL = lines[2].replaceFirst("Image: ", "").trim();

        List<Choice> choiceList = new ArrayList<>();

        for (int i=4;i<lines.length;i++) {
            choiceList.add(Choice.build(parentId, lines[i].trim()));
        }

        parentId = parentId.isEmpty() ? null : parentId;
        imageURL = imageURL.isEmpty() ? null : imageURL;

        return new Question(parentId, label, imageURL, choiceList);
    }
}