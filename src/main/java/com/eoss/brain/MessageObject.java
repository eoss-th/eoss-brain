package com.eoss.brain;

import com.eoss.brain.net.Context;

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

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("text", text);

        return new MessageObject(attributes);
    }

    public static MessageObject build(Map<String, Object> attributes) {
        return new MessageObject(new HashMap<>(attributes));
    }

    public static MessageObject build(MessageObject fromMessageObject, String overrideText) {

        Map<String, Object> newAttributes = new HashMap<>(fromMessageObject.attributes);
        newAttributes.put("text", overrideText);

        return new MessageObject(newAttributes);
    }

    public boolean isSplitted() {
        return attributes.get("wordList") != null;
    }

    public void split() {
        String text = attributes.get("text").toString();
        List<String> wordList = Arrays.asList(text.toLowerCase().split(" "));
        attributes.put("wordList", wordList);
        attributes.put("wordCount", wordList.size());
    }

    public void split(Context context) {
        String text = attributes.get("text").toString();
        List<String> wordList = Arrays.asList(context.split(text.toLowerCase()));
        attributes.put("wordList", wordList);
        attributes.put("wordCount", wordList.size());
    }

    public MessageObject copy() {
        return new MessageObject(new HashMap<>(attributes));
    }

    @Override
    public String toString() {
        Object text = attributes.get("text");
        return text!=null?text.toString():"";
    }

}
