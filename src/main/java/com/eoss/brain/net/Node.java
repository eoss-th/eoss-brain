package com.eoss.brain.net;

import com.eoss.brain.MessageObject;
import java.util.*;



/**
 * Created by eossth on 7/14/2017 AD.
 */
public class Node {

    public enum Mode {
        MatchWhole(1.0f),
        MatchHead(1.0f),
        MatchBody(0.9f),
        MatchTail(0.95f),
        MatchMode(1.0f);

        public final float initWeight;
        Mode(float initWeight) {
            this.initWeight = initWeight;
        }
    }

    public static class Hook {

        public final Mode mode;

        public final String text;

        private transient float active;

        Hook(String text) {
            this(text, Mode.MatchBody);
        }

        Hook(String text, Mode mode) {
            this.text = text;
            this.mode = mode;
        }

        boolean matched(MessageObject messageObject) {

            String input = messageObject.toString();
            Object modeObject = messageObject.attributes.get("mode");

            if (mode == Mode.MatchWhole)
                return input.equalsIgnoreCase(text);
            if (mode == Mode.MatchHead)
                return input.startsWith(text);
            if (mode == Mode.MatchTail)
                return input.endsWith(text);
            if (mode == Mode.MatchBody)
                return input.contains(text);

            return modeObject!=null && modeObject.toString().equalsIgnoreCase(text);
        }

        @Override
        public String toString() {
            if (mode == Mode.MatchWhole)
                return text;
            if (mode == Mode.MatchHead)
                return text+"*";
            if (mode == Mode.MatchTail)
                return "*"+text;
            if (mode == Mode.MatchBody)
                return "*"+text+"*";
            return "["+text+"]";
        }

        @Override
        public int hashCode() {
            return Objects.hash(mode, text);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Hook) {
                Hook another = (Hook)obj;
                return mode == another.mode && Objects.equals(text, another.text);
            }
            return false;
        }
    }

    public class Response {

        public final String text;

        public transient float active;

        public final Map<Hook, Float> hookWeight;

        public Response(String text) {

            this.text = Node.encode(text);

            hookWeight = new HashMap<>();
            for (Map.Entry<String, Hook> entry:hookMap.entrySet()) {
                hookWeight.put(entry.getValue(), entry.getValue().mode.initWeight);
            }

        }

        private Response(Response response) {
            text = response.text;
            active = response.active;
            hookWeight = new HashMap<>(response.hookWeight);
        }

        float totalWeight() {

            float totalWeight = 0;

            for (Map.Entry<Hook, Float> entry:hookWeight.entrySet()) {
                totalWeight += entry.getValue();
            }

            return totalWeight;
        }

        @Override
        public String toString() {
            return text + hookWeight + active;
        }

        public void clear() {
            active = 0;
        }

        public Node owner() {
            return Node.this;
        }

        public void feedback(MessageObject messageObject, float feedback) {

            List<Hook> matchedHookList = new ArrayList<>();
            for (Hook hook:hookMap.values()) {
                if (hook.matched(messageObject)) {
                    matchedHookList.add(hook);
                }
            }

            float weight;
            for (Hook hook:matchedHookList) {
                weight = hookWeight.get(hook);
                weight += feedback*weight;
                if (weight<0) weight = 0;
                hookWeight.put(hook, weight);
            }

        }

        private void feedback(float feedback) {

            float weight;

            for (Map.Entry<Hook, Float> entry:hookWeight.entrySet()) {
                weight = entry.getValue();
                weight += feedback*weight;
                if (weight<0) weight = 0;
                hookWeight.put(entry.getKey(), weight);
            }

        }

        @Override
        public int hashCode() {
            return Objects.hash(text, hookWeight);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Response) {
                Response another = (Response)obj;
                return Objects.equals(text, another.text) && Objects.equals(hookWeight, another.hookWeight);
            }
            return false;
        }

    }

    public final Map<String, Hook> hookMap;

    public final Set<Response> responseSet;

    public Response maxActiveResponse;

    public Node() {
        this (null, null);
    }

    private Node(Node node) {
        hookMap = new LinkedHashMap<>(node.hookMap);
        responseSet = new HashSet<>();
        Response newResponse;
        for (Response response:node.responseSet) {
            newResponse = new Response(response);
            responseSet.add(newResponse);
            if (node.maxActiveResponse!=null && node.maxActiveResponse==response)
                maxActiveResponse = newResponse;
        }
    }

    public Node(String [] hooks, Mode mode) {
        this(hooks, null, mode);
    }

    public Node(String [] hooks, String [] responses, Mode mode) {

        hookMap = new LinkedHashMap<>();

        if (hooks!=null) {

            String hook;
            if (hooks.length==1) {

                hook = encode(hooks[0].trim());
                if (!hook.isEmpty()) {
                    if (mode!=null)
                        hookMap.put(hook, new Hook(hook, mode));
                    else
                        hookMap.put(hook, new Hook(hook, Mode.MatchHead));
                }

            } else {

                for (int i=0; i<hooks.length; i++) {
                    hook = encode(hooks[i].trim());
                    if (!hook.isEmpty()) {
                        if (mode!=null) {
                            hookMap.put(hook, new Hook(hook, mode));
                        } else {
                            if (i==0)
                                hookMap.put(hook, new Hook(hook, Mode.MatchHead));
                            else if (i==hooks.length-1)
                                hookMap.put(hook, new Hook(hook, Mode.MatchTail));
                            else
                                hookMap.put(hook, new Hook(hook, Mode.MatchBody));
                        }
                    }
                }

            }
        }

        responseSet = new HashSet<>();

        if (responses!=null) {
            for (String response:responses) {
                responseSet.add(new Response(response));
            }
        }

    }

    public boolean coverHooks(Node fromNode) {
        Collection<Hook> hookCollection = hookMap.values();
        return hookCollection.containsAll(fromNode.hookMap.values());
    }

    public boolean sameHooks(Node anotherNode) {
        boolean result = hookMap.equals(anotherNode.hookMap);
        return result;
    }

    public void addHook(Node fromNode) {
        Hook newHook;
        for (Map.Entry<String, Hook> fromHook:fromNode.hookMap.entrySet()) {
            if (!hookMap.keySet().contains(fromHook.getKey())) {
                newHook = new Hook(fromHook.getKey(), fromHook.getValue().mode);
                hookMap.put(fromHook.getKey(), newHook);
                for (Response response:responseSet) {
                    response.hookWeight.put(newHook, newHook.mode.initWeight);
                }
            }
        }
    }

    public void addHook(String input) {
        Hook newHook = new Hook(input);
        hookMap.put(newHook.toString(), newHook);
        for (Response response:responseSet) {
            response.hookWeight.put(newHook, newHook.mode.initWeight);
        }
    }

    public void addHook(String input, Mode mode) {
        Hook newHook = new Hook(input, mode);
        hookMap.put(newHook.toString(), newHook);
        for (Response response:responseSet) {
            response.hookWeight.put(newHook, newHook.mode.initWeight);
        }
    }

    public Response addResponse(String input) {
        for (Response response:responseSet) {
            if (response.text.equals(input)) {
                response.feedback(0.01f);
                return response;
            }
        }
        Response newResponse = new Response(input);
        responseSet.add(newResponse);
        return newResponse;
    }

    public boolean matched(MessageObject messageObject) {
        for (Hook hook:hookMap.values()) {
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
        for (Hook hook:hookMap.values()) {
            if (hook.matched(messageObject)) {
                hook.active+=score;
                matchedCount ++;
            }
        }

        float totalResponseActive;

        for (Response response:responseSet) {

            totalResponseActive = 0;
            for (Map.Entry<Hook, Float> entry:response.hookWeight.entrySet()) {
                totalResponseActive += entry.getKey().active * entry.getValue();
            }

            response.active += totalResponseActive / (hookMap.size() + wordCount - matchedCount);
        }

        for (Hook hook:hookMap.values()) {
            hook.active = 0;
        }

        maxActiveResponse = findMaxActiveResponse();
    }

    public void clear() {
        for (Response response:responseSet) {
            response.clear();
        }
        maxActiveResponse = null;
    }

    public String maxActiveResponseText() {

        if (maxActiveResponse!=null)
            return Node.decode(maxActiveResponse.text);
        return null;
    }

    /**
     * 2 Steps for response
     * 1. Find Max Confidence Node
     * 2. Find Max Response (if there are same weight; use randomize)
     */
    private Response findMaxActiveResponse() {
        //Group Response by Weight
        Map<Float, List<Response>> responseMap = new TreeMap<>();
        List<Response> responseList;
        for (Response response:responseSet) {
            responseList = responseMap.get(response.active);
            if (responseList==null) {
                responseList = new ArrayList<>();
            }
            responseList.add(response);
            responseMap.put(response.active, responseList);
        }

        //Find Max Response weight (At the last entry item)
        responseList = null;
        for (Map.Entry<Float, List<Response>> entry:responseMap.entrySet()) {
            responseList = entry.getValue();
        }

        //Randomize response for the same weight
        if (responseList!=null) {
            Random random = new Random();
            Response response = responseList.get(random.nextInt(responseList.size()));
            return response;
        }

        return null;
    }

    public float totalActiveHook() {
        float totalActiveHook = 0;
        for (Hook hook:hookMap.values()) {
            totalActiveHook += hook.active;
        }
        return totalActiveHook;
    }

    @Override
    public String toString() {
        return super.toString() + ":" + hookMap.values() + "=>" + responseSet;
    }

    public String clean(String input) {
        String cleanInput = input;

        for (String hookName:hookMap.keySet()) {
            hookName = hookName.replace("*", "");
            cleanInput = cleanInput.replace(hookName, "");
        }

        return cleanInput.trim();
    }

    public String shortenHooks() {

        StringBuilder hookString = new StringBuilder();
        for (Map.Entry<String, Hook> entry:hookMap.entrySet()) {
            if (entry.getValue().mode==Mode.MatchWhole) {
                hookString.append(entry.getKey());
                break;
            }
        }

        if (hookString.length()>0) return hookString.toString();

        for (Map.Entry<String, Hook> entry:hookMap.entrySet()) {
            if (entry.getValue().mode==Mode.MatchHead) {
                hookString.append(entry.getKey());
                break;
            }
        }

        int maxLen = Integer.MIN_VALUE;
        String maxLenHook = "";
        for (Map.Entry<String, Hook> entry:hookMap.entrySet()) {
            if (entry.getValue().mode==Mode.MatchBody) {
                if (entry.getKey().length() > maxLen) {
                    maxLen = entry.getKey().length();
                    maxLenHook = entry.getKey();
                }
            }
        }

        hookString.append(maxLenHook);

        for (Map.Entry<String, Hook> entry:hookMap.entrySet()) {
            if (entry.getValue().mode==Mode.MatchTail) {
                hookString.append(entry.getKey());
                break;
            }
        }

        return hookString.toString().replace("*", "").trim();
    }

    @Override
    public int hashCode() {
        return Objects.hash(hookMap, responseSet);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            Node another = (Node)obj;
            return Objects.equals(hookMap, another.hookMap) && Objects.equals(responseSet, another.responseSet);
        }
        return false;
    }

    public static String encode(String text) {

        text = text.replace("\t", " ");
        text = text.replace("\n", "<br>");
        text = text.replace("*", "<mark>");
        text = text.replace("[", "<open>");
        text = text.replace("]", "</open>");
        text = text.replace(":", "<colon>");
        text = text.replace("=", "<eq>");

        return text;
    }

    public static String decode(String text) {
        text = text.replace("<br>", "\n");
        text = text.replace("<mark>", "*");
        text = text.replace("<open>", "[");
        text = text.replace("</open>", "]");
        text = text.replace("<colon>", ":");
        text = text.replace("<eq>", "=");
        return text;
    }

    public Node copy() {
        return new Node(this);
    }

    public String hooksString(){

        StringBuilder sb = new StringBuilder();
                Set<String> hookSet = hookMap.keySet();
                for (String h:hookSet) {
                    if(h.matches("^[A-Za-z].*$")){
                        h = " " + h + " ";
                    }
                    sb.append(h);
                }
                String [] tokens = sb.toString().trim().replace("*", "").split("\\s+");
                sb = new StringBuilder();
                for (String h:tokens) {
                    sb.append(h+" ");
                }

        return sb.toString().trim();

    }
}
