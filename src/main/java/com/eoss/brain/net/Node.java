package com.eoss.brain.net;

import com.eoss.brain.MessageObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.*;



/**
 * Created by eossth on 7/14/2017 AD.
 */
public class Node implements Serializable {

    private List<Hook> hookList;

    private String response;

    private float active;

    public final Map<String, Object> attributes = new HashMap<>();

    public Node() {
        this(new ArrayList<Hook>(), "");
    }

    public Node(Node node) {
        this(new ArrayList<>(node.hookList), node.response);
        this.active = node.active;
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
                totalResponseActive += hook.weight;
                matchedCount ++;
            }
        }

        //active += totalResponseActive / (hookList.size() + wordCount - matchedCount);

        /**
         * hooks = hello world
         * words = hello
         * 1 / (2 + 1 - 1) = 0.5
         */

        active = totalResponseActive / (hookList.size() + wordCount - matchedCount);
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

    @Deprecated
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

        for (Hook hook:hookList) {
            if (input.startsWith(hook.text)) {
                input = input.substring(hook.text.length()).trim();
            }
        }

        return input.trim();
    }

    public String clean(String [] inputs) {

        StringBuilder cleanInput = new StringBuilder();
        boolean matched;
        for (String input:inputs) {

            matched = false;
            for (Hook hook:hookList) {
                if (hook.text.equals(input)) {
                    matched = true;
                    break;
                }
            }

            if (!matched) cleanInput.append(input + " ");
        }
        return cleanInput.toString().trim();
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

        Node newNode = new Node(hookList, response);
        if (jsonObject.has("attr")) {
            newNode.attributes.putAll(jsonObject.getJSONObject("attr").toMap());
        }

        return newNode;
    }

    public static JSONObject json(Node node) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (Hook hook:node.hookList) {
            jsonArray.put(Hook.json(hook));
        }
        jsonObject.put("hooks", jsonArray);
        jsonObject.put("response", node.response);
        jsonObject.put("attr", node.attributes);
        return jsonObject;
    }

}
