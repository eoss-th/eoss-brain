package com.brainy;

import com.brainy.net.Context;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageObject implements Serializable {

    public final Map<String, Object> attributes;

    private MessageObject(Map<String, Object> attributes) {

        if (attributes==null)
            this.attributes = new HashMap<>();
        else
            this.attributes = attributes;

    }

    public static MessageObject build() {
        return build("");
    }

    public static MessageObject build(String text) {
        return new MessageObject(updateAttributes(null, text));
    }

    public static MessageObject build(Map<String, Object> attributes) {
        return new MessageObject(new HashMap<>(attributes));
    }

    public static MessageObject build(MessageObject fromMessageObject, String overrideText) {
        return new MessageObject(updateAttributes(fromMessageObject.attributes, overrideText));
    }

    public boolean isSplitted() {
        return attributes.get("wordList") != null;
    }

    public MessageObject setText(String text) {
        updateAttributes(attributes, text);
        return this;
    }

    public MessageObject split() {
        String text = attributes.get("text").toString();
        List<String> wordList = Arrays.asList(text.toLowerCase().split(" "));
        attributes.put("wordList", wordList);
        attributes.put("wordCount", wordList.size());
        return this;
    }

    public MessageObject split(Context context) {
        String text = attributes.get("text").toString();
        List<String> wordList = Arrays.asList(context.split(text.toLowerCase()));
        attributes.put("wordList", wordList);
        attributes.put("wordCount", wordList.size());
        return this;
    }

    public MessageObject copy() {
        return new MessageObject(new HashMap<>(attributes));
    }

    private static Map<String, Object> updateAttributes(Map<String, Object> attributes, String text) {

        if (attributes==null) {
            attributes = new HashMap<>();
        }

        String head, tail;
        int lastIndexOfComma = text.lastIndexOf(",");

        if (lastIndexOfComma==-1) {
            head = tail = null;
        } else {
            head = text.substring(0, lastIndexOfComma).trim();

            if (text.endsWith("!"))
                tail = text.substring(lastIndexOfComma + 1, text.length()-1).trim();
            else
                tail = text.substring(lastIndexOfComma + 1).trim();
        }

        attributes.put("text", text);
        attributes.put("head", head);
        attributes.put("tail", tail);

        return attributes;
    }

    public String head() {
        String head = (String) attributes.get("head");
        return head==null || head.isEmpty()? "" : head;
    }

    public String headIncluded() {
        String head = (String) attributes.get("head");
        return head==null || head.isEmpty()? "" : head + "\n\n\n";
    }

    public String tail() {
        String tail = (String) attributes.get("tail");
        return tail==null ? "" : tail;
    }

    public MessageObject clean() {

        setText(toString().
                replace(", ", " ").
                replace("!", "").
                replace("?", ""));

        return this;
    }

    public MessageObject forward() {
        updateAttributes(attributes, (String) attributes.get("tail"));
        return this;
    }

    @Override
    public String toString() {
        Object text = attributes.get("text");
        return text!=null?text.toString():"";
    }

}
