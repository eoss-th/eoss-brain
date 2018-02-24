package com.eoss.brain.net;

import com.eoss.brain.MessageObject;
import com.eoss.brain.NodeEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.BreakIterator;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by eoss-th on 8/15/17.
 */
public abstract class Context implements Serializable {

    public static final String SUFFIX = ".context";

    private Locale locale = Locale.getDefault();

    public final String name;

    public ContextListener listener;

    private List<String> adminIdList = new ArrayList<>();

    protected ReadWriteLock lock = new ReentrantReadWriteLock();

    public final Map<String, String> properties = new HashMap();

    public final List<Node> nodeList = new ArrayList<>();

    public Context(String name) {
        this.name = name;
    }

    public Context callback(ContextListener callback) {
        this.listener = callback;
        return this;
    }

    public Context admin(List<String> adminIdList) {
        if (adminIdList!=null)
            this.adminIdList = new ArrayList<>(adminIdList);
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
        try {
            doSave(name, nodeList);
        } finally {
            lock.readLock().unlock();
        }
    }

    protected final void loadJSON(String jsonString) {
        JSONObject object = new JSONObject(jsonString);
        Set<String> propertyNames = object.keySet();
        for (String property:propertyNames) {
            if (property.equals("nodes")) {
                nodeList.clear();
                nodeList.addAll(build(object.getJSONArray(property)));
            } else {
                properties.put(property, object.getString(property));
            }
        }
    }

    protected final String toJSONString() {
        JSONObject object = new JSONObject();
        for (Map.Entry<String, String> entry:properties.entrySet()) {
            object.put(entry.getKey(), entry.getValue());
        }
        object.put("nodes", json(nodeList));
        return object.toString();
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

    public static List<Node> findActiveNodes(Set<Node> activeNodeSet, float minPercentile) {

        Map<Float, Set<Node>> orderedNodeMap = new TreeMap<>();
        Set<Node> maxActiveNodeSet;
        float active;
        for (Node activeNode:activeNodeSet) {
            active = activeNode.active();
            maxActiveNodeSet = orderedNodeMap.get(active);
            if (maxActiveNodeSet==null) {
                maxActiveNodeSet = new HashSet<>();
            }
            maxActiveNodeSet.add(activeNode);
            orderedNodeMap.put(active, maxActiveNodeSet);
        }

        if (activeNodeSet.isEmpty()) return new ArrayList<>();

        List<Node> result = new ArrayList<>();
        List<Map.Entry<Float, Set<Node>>> orderedNodeList = new ArrayList<>(orderedNodeMap.entrySet());
        float minScore = orderedNodeList.get(orderedNodeList.size()-1).getKey() * minPercentile;

        for (int i=orderedNodeList.size()-1;i>=0;i--) {

            if (orderedNodeList.get(i).getKey()<minScore) break;
            result.addAll(new ArrayList<>(orderedNodeList.get(i).getValue()));
        }


        return result;
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
                node.feed(messageObject, matchedScore);
                activeNodeSet.add(node);
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
                listener.callback(new NodeEvent(node, messageObject.copy(), NodeEvent.Event.Matched));
                matched = true;
            }
        }
        return matched;
    }

    public Node build(MessageObject messageObject) {

        String input = messageObject.toString();

        Node node = new Node(Hook.build(split(input)));

        Object mode = messageObject.attributes.get("mode");

        if (mode!=null && !mode.toString().trim().isEmpty()) {
            node.addHook(mode.toString().trim(), Hook.Match.Mode);
        }

        return node;
    }

    public String [] split(String input) {

        List<String> result = new ArrayList<>();

        BreakIterator breakIterator = BreakIterator.getWordInstance(locale);
        breakIterator.setText(input);

        int wordBoundaryIndex = breakIterator.first();
        int prevIndex         = 0;

        String token;
        while(wordBoundaryIndex != BreakIterator.DONE) {
            token = input.substring(prevIndex, wordBoundaryIndex).trim();
            if (!token.isEmpty())
                result.add(token);
            prevIndex = wordBoundaryIndex;
            wordBoundaryIndex = breakIterator.next();
        }

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
