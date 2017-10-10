package com.eoss.brain;

import com.eoss.brain.net.Context;

import java.util.HashMap;
import java.util.Map;

public class MessageObject {

    public final Map<String, Object> attributes;

    private int wordCount;

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

        MessageObject newMessageObject = new MessageObject(attributes);
        newMessageObject.wordCount = Context.splitToList(text).size();

        return newMessageObject;
    }

    public static MessageObject build(Map<String, Object> attributes) {
        return new MessageObject(new HashMap<>(attributes));
    }

    public static MessageObject build(MessageObject fromMessageObject, String overrideText) {

        Map<String, Object> newAttributes = new HashMap<>(fromMessageObject.attributes);
        newAttributes.put("text", overrideText);

        return new MessageObject(newAttributes);
    }

    public MessageObject copy() {
        return new MessageObject(new HashMap<>(attributes));
    }

    @Override
    public String toString() {
        Object text = attributes.get("text");
        return text!=null?text.toString():"";
    }

    public int wordCount() {
        return wordCount;
    }
}
