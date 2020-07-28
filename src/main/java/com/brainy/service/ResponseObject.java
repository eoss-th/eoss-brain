package com.brainy.service;

import com.brainy.command.talk.Choice;
import com.brainy.command.talk.Question;
import com.oracle.javafx.jmx.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ResponseObject {

    interface JSONAble {
        JSONObject toJSONObject();
    }

    public static class Text implements JSONAble {
        private final String text;
        public Text(String text) {
            this.text = text;
        }
        @Override
        public String toString() {
            return text;
        }
        @Override
        public JSONObject toJSONObject() {
            JSONObject obj = new JSONObject();
            obj.put("text", text);
            return obj;
        }
    }

    public static class Image implements JSONAble {
        public final String url;
        public Image(String url) {
            this.url = url;
        }
        @Override
        public String toString() {
            return url;
        }
        @Override
        public JSONObject toJSONObject() {
            JSONObject obj = new JSONObject();
            obj.put("image", url);
            return obj;
        }
    }

    public static class Audio implements JSONAble {
        public final String url;
        public Audio(String url) {
            this.url = url;
        }
        @Override
        public String toString() {
            return url;
        }
        @Override
        public JSONObject toJSONObject() {
            JSONObject obj = new JSONObject();
            obj.put("url", url);
            return obj;
        }
    }

    public final List<Object> messageList = new ArrayList<>();

    public ResponseObject(String responseText) {

        String [] responses = responseText.split("\n\n\n");
        String [] tokens;

        List<Question> questionList = new ArrayList<>();
        for (String response:responses) {
            tokens = response.split(" ", 2);

            String firstToken = tokens[0].toLowerCase();
            if (firstToken.startsWith("question:")) {

                questionList.add(Question.build(response));

            } else if (firstToken.startsWith("https:")) {

                if (firstToken.endsWith("png") || firstToken.endsWith("jpg") || firstToken.endsWith("jpeg")|| firstToken.endsWith("gif") || firstToken.endsWith("PNG") || firstToken.endsWith("JPG") || firstToken.endsWith("JPEG") || firstToken.endsWith("GIF")) {
                    messageList.add(new Image(tokens[0]));
                } else if (firstToken.endsWith("m4a")) {
                    messageList.add(new Audio(tokens[0]));
                } else {
                    messageList.add(new Text(tokens[0]));
                }

                if (tokens.length==2) {
                    messageList.add(new Text(tokens[1]));
                }

            } else {
                messageList.add(new Text(response));
            }
        }

        boolean hasQuestion = questionList!=null && !questionList.isEmpty();
        if (hasQuestion) {
            messageList.add(questionList);
        }

    }

    @Deprecated
    public ResponseObject(String responseText, List<Question> questionList) {

        boolean hasQuestion = questionList!=null && !questionList.isEmpty();
        String [] lines = responseText.split("\n\n\n");
        List<String> messages = Arrays.asList(lines).stream().filter(new Predicate<String>() {
            @Override
            public boolean test(String t) {
                return !t.trim().isEmpty();
            }
        }).collect(Collectors.toList());

        //Remove Last messsage if there is any question
        if (hasQuestion) {
            if (!messages.isEmpty()) {
                messages.remove(messages.size()-1);
            }
        }

        String [] replyTokens;
        for (String message:messages) {
            replyTokens = message.split(" ", 2);
            String firstToken = replyTokens[0].toLowerCase();
            if (firstToken.startsWith("https")) {

                if (firstToken.endsWith("png") || firstToken.endsWith("jpg") || firstToken.endsWith("jpeg")|| firstToken.endsWith("gif") || firstToken.endsWith("PNG") || firstToken.endsWith("JPG") || firstToken.endsWith("JPEG") || firstToken.endsWith("GIF")) {
                    messageList.add(new Image(replyTokens[0]));
                } else if (firstToken.endsWith("m4a")) {
                    messageList.add(new Audio(replyTokens[0]));
                } else {
                    messageList.add(new Text(replyTokens[0]));
                }

                if (replyTokens.length==2) {
                    messageList.add(new Text(replyTokens[1]));
                }

            } else {
                messageList.add(new Text(message));
            }
        }

        if (hasQuestion) {
            messageList.add(questionList);
        }

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        List<Question> questionList;
        for (Object message:messageList) {
            if (message instanceof List) {

                questionList = (List<Question>) message;
                for (Question q:questionList) {
                    sb.append(q + "\n\n");
                }

            } else {
                sb.append(message);
            }
            sb.append("\n\n");
        }

        return sb.toString().trim();
    }

    @Deprecated
    public static ResponseObject build(String replyJSON) {

        String responseText;
        try {

            JSONObject replyObj = new JSONObject(replyJSON);
            responseText = replyObj.getString("text");
            List<Question> questionList = new ArrayList<>();

            Question question;
            Choice choice;

            JSONArray questionArray = replyObj.getJSONArray("questions");
            JSONArray choiceArray;

            JSONObject questionObj, choiceObj;
            List<Choice> choiceList;

            String parent, label, imageURL, linkURL;
            for (int i=0;i<questionArray.length();i++) {
                questionObj = questionArray.getJSONObject(i);

                choiceList = new ArrayList<>();
                choiceArray = questionObj.getJSONArray("choices");

                for (int j=0;j<choiceArray.length();j++) {
                    choiceObj = choiceArray.getJSONObject(j);

                    try {
                        parent = choiceObj.getString("parent");
                    } catch (JSONException e) {
                        parent = null;
                    }

                    try {
                        label = choiceObj.getString("label");
                    } catch (JSONException e) {
                        label = null;
                    }

                    try {
                        imageURL = choiceObj.getString("imageURL");
                    } catch (JSONException e) {
                        imageURL = null;
                    }

                    try {
                        linkURL = choiceObj.getString("linkURL");
                    } catch (JSONException e) {
                        linkURL = null;
                    }

                    choice = new Choice(parent, label, imageURL, linkURL);
                    choiceList.add(choice);
                }

                try {
                    parent = questionObj.getString("parent");
                } catch (JSONException e) {
                    parent = null;
                }

                try {
                    label = questionObj.getString("label");
                } catch (JSONException e) {
                    label = null;
                }

                try {
                    imageURL = questionObj.getString("imageURL");
                } catch (JSONException e) {
                    imageURL = null;
                }

                question = new Question(parent, label, imageURL, choiceList);
                questionList.add(question);
            }

            return new ResponseObject(responseText, questionList);

        } catch (JSONException e) {
            responseText = replyJSON;
        }

        //Retry with console text
        return new ResponseObject(responseText);
    }

    public String toJSONString() {

        JSONArray array = new JSONArray();

        for (Object obj:messageList) {
            if (obj instanceof Question) {
                array.put((((Question) obj).toJSONObject()));
            } else if (obj instanceof JSONAble) {
                array.put((((JSONAble) obj).toJSONObject()));
            }
        }

        return array.toString();
    }

}