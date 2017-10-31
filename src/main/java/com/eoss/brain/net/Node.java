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

        public Hook(String text) {
            this(text, Mode.MatchBody);
        }

        public Hook(String text, Mode mode) {
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

        public final List<Float> weightList;

        public Response(String text) {

            this.text = Node.encode(text);

            weightList = new ArrayList<>();
            for (Hook hook:hookList) {
                weightList.add(hook.mode.initWeight);
            }

        }

        private Response(Response response) {
            text = response.text;
            active = response.active;
            weightList = new ArrayList<>(response.weightList);
        }

        float totalWeight() {
            float totalWeight = 0;
            for (Float weight:weightList) {
                totalWeight += weight;
            }
            return totalWeight;
        }

        @Override
        public String toString() {
            return text + weightList + active;
        }

        public void clear() {
            active = 0;
        }

        public Node owner() {
            return Node.this;
        }

        public void feedback(MessageObject messageObject, float feedback) {

            List<Integer> matchedHookList = new ArrayList<>();
            int matchedHookIndex = 0;
            for (Hook hook:hookList) {
                if (hook.matched(messageObject)) {
                    matchedHookList.add(matchedHookIndex);
                }
                matchedHookIndex ++;
            }

            float weight;
            for (Integer index:matchedHookList) {
                weight = weightList.get(index);
                weight += feedback*weight;
                if (weight<0) weight = 0;
                weightList.set(index, weight);
            }

        }

        private void feedback(float feedback) {

            int index = 0;
            for (Float weight:weightList) {
                weight += feedback*weight;
                if (weight<0) weight = 0f;
                weightList.set(index, weight);
                index ++;
            }

        }

        @Override
        public int hashCode() {
            return Objects.hash(text, weightList);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Response) {
                Response another = (Response)obj;
                return Objects.equals(text, another.text) && Objects.equals(weightList, another.weightList);
            }
            return false;
        }

    }

    public final List<Hook> hookList;

    public final Set<Response> responseSet;

    public Response maxActiveResponse;

    public Node() {
        this (null, null);
    }

    private Node(Node node) {
        hookList = new ArrayList<>(node.hookList);
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

        hookList = new ArrayList<>();

        if (hooks!=null) {

            String hook;
            if (hooks.length==1) {

                hook = encode(hooks[0].trim());
                if (!hook.isEmpty()) {
                    if (mode!=null)
                        hookList.add(new Hook(hook, mode));
                    else
                        hookList.add(new Hook(hook, Mode.MatchHead));
                }

            } else {

                for (int i=0; i<hooks.length; i++) {
                    hook = encode(hooks[i].trim());
                    if (!hook.isEmpty()) {
                        if (mode!=null) {
                            hookList.add(new Hook(hook, mode));
                        } else {
                            if (i==0)
                                hookList.add(new Hook(hook, Mode.MatchHead));
                            else if (i==hooks.length-1)
                                hookList.add(new Hook(hook, Mode.MatchTail));
                            else
                                hookList.add(new Hook(hook, Mode.MatchBody));
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
        return hookList.containsAll(fromNode.hookList);
    }

    public boolean sameHooks(Node anotherNode) {
        boolean result = hookList.equals(anotherNode.hookList);
        return result;
    }

    public void addHook(Node fromNode) {
        Hook newHook;
        for (Hook fromHook:fromNode.hookList) {
            if (!hookList.contains(fromHook)) {
                newHook = new Hook(fromHook.text, fromHook.mode);
                hookList.add(newHook);
                for (Response response:responseSet) {
                    response.weightList.add(newHook.mode.initWeight);
                }
            }
        }
    }

    public void addHook(String input) {
        Hook newHook = new Hook(input);
        hookList.add(newHook);
        for (Response response:responseSet) {
            response.weightList.add(newHook.mode.initWeight);
        }
    }

    public void addHook(String input, Mode mode) {
        Hook newHook = new Hook(input, mode);
        hookList.add(newHook);
        for (Response response:responseSet) {
            response.weightList.add(newHook.mode.initWeight);
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
        for (Hook hook:hookList) {
            if (hook.matched(messageObject)) {
                hook.active+=score;
                matchedCount ++;
            }
        }

        float totalResponseActive;

        for (Response response:responseSet) {

            totalResponseActive = 0;

            int hookIndex = 0;
            for (Float weight:response.weightList) {
                totalResponseActive += hookList.get(hookIndex).active * weight;
                hookIndex ++;
            }

            response.active += totalResponseActive / (hookList.size() + wordCount - matchedCount);
        }

        for (Hook hook:hookList) {
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
        for (Hook hook:hookList) {
            totalActiveHook += hook.active;
        }
        return totalActiveHook;
    }

    @Override
    public String toString() {
        return super.toString() + ":" + hookList + "=>" + responseSet;
    }

    public String clean(String input) {
        String cleanInput = input;

        String hookName;
        for (Hook hook:hookList) {
            hookName = hook.text.replace("*", "");
            cleanInput = cleanInput.replace(hookName, "");
        }

        return cleanInput.trim();
    }

    @Override
    public int hashCode() {
        return Objects.hash(hookList, responseSet);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            Node another = (Node)obj;
            return Objects.equals(hookList, another.hookList) && Objects.equals(responseSet, another.responseSet);
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

    public String hooksString() {

        StringBuilder sb = new StringBuilder();
        String h;
        for (Hook hook:hookList) {
            if (Mode.MatchMode==hook.mode) continue;
            h = hook.text.replace("*","");
            if(h.matches("^[A-Za-z].*$")){
                h = " " + h + " ";
            }
            sb.append(h);
        }

        String [] tokens = sb.toString().trim().split("\\s+");
        sb = new StringBuilder();
        for (String t:tokens) {
            sb.append(t+" ");
        }

        return sb.toString().trim();
    }
}
