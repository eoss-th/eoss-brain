package com.eoss.brain.net;

import com.eoss.brain.MessageObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;



/**
 * Created by eossth on 7/14/2017 AD.
 */
public class Node {

    private List<Hook> hookList;

    private String response;

    private float active;

    public Node() {
        this(new ArrayList<Hook>(), "");
    }

    public Node(Node node) {
        this(new ArrayList<>(node.hookList), node.response);
    }

    public Node(List<Hook> hookList) {
        this(hookList, "");
    }

    public Node(List<Hook> hookList, String response) {
        this.hookList = hookList;
        this.response = response;
    }

    public boolean coverHooks(Node fromNode) {
        return hookList.containsAll(fromNode.hookList);
    }

    public boolean sameHooks(Node anotherNode) {
        return hookList.equals(anotherNode.hookList);
    }

    public void addHook(Node fromNode) {
        Hook newHook;
        for (Hook fromHook:fromNode.hookList) {
            if (!hookList.contains(fromHook)) {
                newHook = new Hook(fromHook.text, fromHook.match);
                hookList.add(newHook);
            }
        }
    }

    public void addHook(String input, Hook.Match match) {
        Hook newHook = new Hook(input, match);
        hookList.add(newHook);
    }

    public void setResponse(String text) {
        response = text;
    }

    public boolean matched(MessageObject messageObject) {
        for (Hook hook:hookList) {
            if (hook.matched(messageObject)) {
                return true;
            }
        }
        return false;
    }

    public void feed(MessageObject messageObject) {
        feed(messageObject, 1);
    }

    public void feed(MessageObject messageObject, float score) {

        int wordCount;
        if (messageObject.attributes.get("wordCount")!=null) {
            wordCount = (Integer) messageObject.attributes.get("wordCount");
        } else if (!messageObject.toString().trim().isEmpty()) {
            wordCount = 1;
        } else {
            wordCount = 0;
        }

        int matchedCount = 0;
        float totalResponseActive = 0;
        for (Hook hook:hookList) {
            if (hook.matched(messageObject)) {
                totalResponseActive += score * hook.weight;
                matchedCount ++;
            }
        }

        active += totalResponseActive / (hookList.size() + wordCount - matchedCount);
    }

    public void feedback(MessageObject messageObject, float feedback) {

        for (Hook hook:hookList) {
            if (hook.matched(messageObject)) {
                hook.feedback(feedback);
            }
        }
    }

    public List<Hook> hookList() {
        return hookList;
    }

    public void release() {
        active = 0;
    }

    public void release (float rate) {
        active *= rate;
    }

    public String response() {
        return response;
    }

    public float active() {
        return active;
    }

    @Override
    public String toString() {
        return Hook.toString(hookList) + System.lineSeparator() + response;
    }

    public String clean(String input) {
        String cleanInput = input;
        for (Hook hook:hookList) {
            cleanInput = cleanInput.replace(hook.text, "");
        }
        return cleanInput.trim();
    }

    @Override
    public int hashCode() {
        return Objects.hash(hookList, response);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            Node another = (Node)obj;
            return Objects.equals(hookList, another.hookList) && Objects.equals(response, another.response);
        }
        return false;
    }

    public static Node build(JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("hooks");
        List<Hook> hookList = new ArrayList<>();
        for (int i=0;i<jsonArray.length();i++) {
            hookList.add(Hook.build(jsonArray.getJSONObject(i)));
        }
        String response = jsonObject.getString("response");
        return new Node(hookList, response);
    }

    public static JSONObject json(Node node) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (Hook hook:node.hookList) {
            jsonArray.put(Hook.json(hook));
        }
        jsonObject.put("hooks", jsonArray);
        jsonObject.put("response", node.response);
        return jsonObject;
    }

}
