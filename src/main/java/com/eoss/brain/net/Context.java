package com.eoss.brain.net;

import com.eoss.brain.MessageObject;
import com.eoss.brain.NodeEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.BreakIterator;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by eoss-th on 8/15/17.
 */
public abstract class Context implements Serializable {

    public static final String SUFFIX = ".context";

    private Locale locale = Locale.getDefault();

    public final String name;

    private List<String> adminIdList = new ArrayList<>();

    protected ReadWriteLock lock = new ReentrantReadWriteLock();

    public final Map<String, String> properties = new HashMap();

    public final Map<String, Object> attributes = new HashMap<>();

    public final List<Node> nodeList = new ArrayList<>();

    public ContextListener listener;

    public Context(String name) {
        this.name = name;
    }

    public Context admin(List<String> adminIdList) {
        if (adminIdList!=null)
            this.adminIdList = new ArrayList<>(adminIdList);
        return this;
    }

    public Context callback(ContextListener callback) {
        this.listener = callback;
        return this;
    }

    public Context locale(Locale locale) {
        this.locale = locale;
        return this;
    }

    public Locale locale() {
        return locale;
    }

    public boolean isAdmin(String userId) {
        return adminIdList.contains(userId);
    }

    protected abstract void doLoad(String name) throws Exception ;

    protected abstract void doSave(String name, List<Node> nodeList) ;

    public void load() throws Exception {
        load(name);
    }

    public void load(String name) throws Exception {
        lock.readLock().lock();
        try {
            doLoad(name);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void save() {
        save(name);
    }

    public void save(String name) {
        lock.readLock().lock();
        boolean saved = false;
        try {
            doSave(name, nodeList);
            saved = true;
        } finally {
            lock.readLock().unlock();
            if (saved && listener!=null)
                listener.callback(new NodeEvent(null, MessageObject.build(name), NodeEvent.Event.ContextSaved));
        }
    }

    public final void loadJSON(String jsonString) {
        JSONObject object = new JSONObject(jsonString);
        Set<String> propertyNames = object.keySet();
        for (String property:propertyNames) {
            if (property.equals("nodes")) {
                nodeList.clear();
                nodeList.addAll(build(object.getJSONArray(property)));
            } else if (property.equals("attr")) {
                attributes.putAll(object.getJSONObject("attr").toMap());
            } else {
                try {
                    properties.put(property, object.getString(property));
                } catch (JSONException e) {
                    continue;
                }
            }
        }
    }

    public final String toJSONString() {
        JSONObject object = new JSONObject();
        for (Map.Entry<String, String> entry:properties.entrySet()) {
            object.put(entry.getKey(), entry.getValue());
        }
        object.put("attr", new JSONObject(attributes));
        object.put("nodes", json(nodeList));
        return object.toString();
    }

    public Node get(List<Hook> hookList) {
        lock.readLock().lock();
        try {
            for (Node node:nodeList) {
                if (node.hookList().equals(hookList))
                    return node;
            }
        } finally {
            lock.readLock().unlock();
        }
        return null;
    }

    public void add(Node newNode) {
        lock.writeLock().lock();
        try {
            nodeList.add(newNode);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void clear() {
        lock.writeLock().lock();
        try {
            nodeList.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public String toString() {
        lock.readLock().lock();
        StringBuilder data = new StringBuilder();
        try {
            for (Node node: nodeList) {
                data.append(node);
                data.append(System.lineSeparator());
            }
        } finally {
            lock.readLock().unlock();
        }
        return data.toString();
    }

    public static List<Node> build(JSONArray jsonArray) {
        List<Node> nodeList = new ArrayList<>();
        for (int i=0;i<jsonArray.length();i++) {
            nodeList.add(Node.build(jsonArray.getJSONObject(i)));
        }
        return nodeList;
    }

    public static JSONArray json(List<Node> nodeList) {
        JSONArray jsonArray = new JSONArray();
        for (Node node:nodeList) {
            jsonArray.put(Node.json(node));
        }
        return jsonArray;
    }

    public static Node findMaxActiveNode(Set<Node> activeNodeSet) {
        if (activeNodeSet==null || activeNodeSet.isEmpty()) return null;

        float maxActive = Float.MIN_VALUE;
        Node maxActiveNode = null;

        for (Node node:activeNodeSet) {
            if (node.active()>maxActive) {
                maxActive = node.active();
                maxActiveNode = node;
            }
        }

        return maxActiveNode;
    }

    public static Node findMaxActiveNode(Set<Node> activeNodeSet, Random random) {

        if (activeNodeSet==null || activeNodeSet.isEmpty()) return null;

        TreeMap<Float, List<Node>> nodeMap = new TreeMap<>();

        Float confidence;
        List nodeList;
        for (Node node:activeNodeSet) {
            confidence = node.active();
            nodeList = nodeMap.get(confidence);
            if (nodeList==null) nodeList = new ArrayList();
            nodeList.add(node);
            nodeMap.put(confidence, nodeList);
        }

        Map.Entry<Float, List<Node>> maxActiveEntry = nodeMap.lastEntry();
        List<Node> maxActiveNodeList = maxActiveEntry.getValue();

        return maxActiveNodeList.get(random.nextInt(maxActiveNodeList.size()));
    }

    public static List<Node> findMaxActiveNodes(Set<Node> activeNodeSet) {

        if (activeNodeSet==null || activeNodeSet.isEmpty()) return null;

        TreeMap<Float, List<Node>> nodeMap = new TreeMap<>();

        Float confidence;
        List nodeList;
        for (Node node:activeNodeSet) {
            confidence = node.active();
            nodeList = nodeMap.get(confidence);
            if (nodeList==null) nodeList = new ArrayList();
            nodeList.add(node);
            nodeMap.put(confidence, nodeList);
        }

        Map.Entry<Float, List<Node>> maxActiveEntry = nodeMap.lastEntry();
        return maxActiveEntry.getValue();
    }


    public Set<Node> feed(MessageObject messageObject) {
        return feed(messageObject, 1);
    }

    public Set<Node> feed(MessageObject messageObject, float matchedScore) {
        lock.writeLock().lock();
        try {
            return feed(messageObject, matchedScore, new HashSet<>(nodeList));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Set<Node> feed(MessageObject messageObject, float matchedScore, Set<Node> nodeSet) {
        Set<Node> activeNodeSet = new HashSet<>();

        for (Node node:nodeSet) {
            if (node.matched(messageObject)) {
                node.feed(messageObject);
                if (node.active()>=matchedScore) {
                    activeNodeSet.add(node);
                }
            }
        }

        return activeNodeSet;
    }

    public boolean matched(MessageObject messageObject, ContextListener listener) {
        lock.writeLock().lock();
        try {
            return matched(messageObject, new HashSet<>(nodeList), listener);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean matched(MessageObject messageObject, Set<Node> nodeSet, ContextListener listener) {
        boolean matched = false;
        for (Node node:nodeSet) {
            if (node.matched(messageObject)) {
                listener.callback(new NodeEvent(new Node(node), messageObject.copy(), NodeEvent.Event.Matched));
                matched = true;
            }
        }
        return matched;
    }

    public Node build(MessageObject messageObject) {

        String input = messageObject.toString();

        Node node = Node.build(split(input));

        Object mode = messageObject.attributes.get("mode");

        if (mode!=null && !mode.toString().trim().isEmpty()) {
            node.addHook(mode.toString().trim(), Hook.Match.Mode);
        }

        return node;
    }

    public String [] split(String input) {
        return split(input, locale);
    }

    public String [] split(String input, Locale locale) {

        BreakIterator breakIterator = BreakIterator.getWordInstance(locale);
        List<String> result = new ArrayList<>();

        /**
         * Conditional Hooks
         */
        List<String> conditionHooks = new ArrayList<>();

        List<String> variableHooks = new ArrayList<>();

        List<String> parentHooks = new ArrayList<>();

        String [] tokens = input.split(" ");
        String hook, subHook;
        int wordBoundaryIndex, prevIndex;
        for (String token:tokens) {
            hook = token.trim();
            if (hook.startsWith(">") ||
                    hook.startsWith(">=") ||
                    hook.startsWith("<") ||
                    hook.startsWith("<=")) {
                conditionHooks.add(hook);
            } else if (hook.startsWith("#")) {
                variableHooks.add(hook);
            } else if (hook.startsWith("@")) {
                parentHooks.add(hook);
            } else {
                /**
                 * Sentence
                 */
                breakIterator.setText(hook);

                wordBoundaryIndex = breakIterator.first();
                prevIndex         = 0;
                int subWordCount  = 0;
                while(wordBoundaryIndex != BreakIterator.DONE) {
                    subHook = hook.substring(prevIndex, wordBoundaryIndex).trim();
                    if (!subHook.isEmpty()) {
                        result.add(subHook);
                        subWordCount ++;
                    }
                    prevIndex = wordBoundaryIndex;
                    wordBoundaryIndex = breakIterator.next();
                }

                if (subWordCount>1) {
                    result.add(hook);
                }

            }
        }

        result.addAll(conditionHooks);
        result.addAll(variableHooks);
        result.addAll(parentHooks);

        return result.toArray(new String[result.size()]);
    }

    public boolean isEmpty() {
        lock.readLock().lock();
        try {
            return nodeList.isEmpty();
        } finally {
            lock.readLock().unlock();
        }
    }

}
