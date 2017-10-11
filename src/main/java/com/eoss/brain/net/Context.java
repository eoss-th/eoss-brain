package com.eoss.brain.net;

import com.eoss.brain.MessageObject;
import com.eoss.brain.MessageTemplate;
import com.eoss.brain.NodeEvent;

import java.text.BreakIterator;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by eoss-th on 8/15/17.
 */
public abstract class Context {

    public static final String SUFFIX = ".context";

    private Locale locale = Locale.getDefault();

    public final String name;

    public String domain;

    public ContextListener listener;

    private List<String> adminIdList = new ArrayList<>();

    protected ReadWriteLock lock = new ReentrantReadWriteLock();

    public final Set<Node> dataSet = new HashSet<>();

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

    public Context domain(String domain) {
        this.domain = domain;
        return this;
    }

    public Context locale(Locale locale) {
        this.locale = locale;
        return this;
    }

    public boolean isAdmin(String userId) {
        return adminIdList.contains(userId);
    }

    protected abstract void doLoad(String name) throws Exception ;

    protected abstract void doSave(String name, Set<Node> dataSet) ;

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
            doSave(name, new HashSet<Node>(dataSet));
        } finally {
            lock.readLock().unlock();
        }
    }

    public void add(Node newNode) {
        lock.writeLock().lock();
        try {
            dataSet.add(newNode);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void clear() {
        lock.writeLock().lock();
        try {
            dataSet.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public String toString() {
        lock.readLock().lock();
        StringBuilder data = new StringBuilder();
        try {
            for (Node node:dataSet) {
                data.append(node);
                data.append(System.lineSeparator());
            }
        } finally {
            lock.readLock().unlock();
        }
        return data.toString();
    }

    public static String toString(Node node) {

        StringBuilder sb = new StringBuilder();

        StringBuilder hooks = new StringBuilder();
        for (Node.Hook hook:node.hookMap.values()) {
            hooks.append(hook);
            hooks.append(" ");
        }

        sb.append(hooks.toString().trim());
        sb.append(System.lineSeparator());

        StringBuilder responses = new StringBuilder();
        for (Node.Response response:node.responseSet) {
            responses.append(" ");
            responses.append(toString(response));
            responses.append(System.lineSeparator());
        }

        sb.append(responses.toString());

        return sb.toString();
    }

    public static String toString(Node.Response response) {

        StringBuilder sb = new StringBuilder(response.text);
        sb.append("\t");

        StringBuilder weight = new StringBuilder();
        for (Map.Entry<Node.Hook, Float> entry:response.hookWeight.entrySet()) {
            weight.append(entry.getKey());
            weight.append("=");
            weight.append(entry.getValue());
            weight.append(" ");
        }

        sb.append(weight.toString().trim());

        return sb.toString();
    }

    public static Node parse(String lineHooks) {

        Node node = new Node();

        if (lineHooks!=null) {
            String [] hookArray = lineHooks.split(" ");

            Node.Mode mode;
            for (String hook:hookArray) {
                hook = hook.trim();
                if (hook.isEmpty()==false) {
                    if (hook.startsWith("[") && hook.endsWith("]"))
                        mode = Node.Mode.MatchMode;
                    else if (hook.startsWith("*") && hook.endsWith("*"))
                        mode = Node.Mode.MatchBody;
                    else if (hook.startsWith("*"))
                        mode = Node.Mode.MatchTail;
                    else if (hook.endsWith("*"))
                        mode = Node.Mode.MatchHead;
                    else
                        mode = Node.Mode.MatchWhole;

                    hook = hook.replace("*", "");
                    hook = hook.replace("[", "");
                    hook = hook.replace("]", "");

                    node.addHook(hook, mode);
                }
            }
        }
        return node;
    }

    public static void addResponse(Node toNode, String text, String hookWeights) {
        Node.Response response = toNode.new Response(text);
        toNode.responseSet.add(response);

        String [] hookWeightArray = hookWeights.split(" ");
        String [] hw;
        for (String hookWeight:hookWeightArray) {
            hw = hookWeight.split("=");
            response.hookWeight.put(toNode.hookMap.get(hw[0]), Float.parseFloat(hw[1]));
        }
    }

    public static List<Node> findActiveNodes(Set<Node> activeNodeSet, float minPercentile) {

        Map<Float, Set<Node>> orderedNodeMap = new TreeMap<>();
        Set<Node> maxActiveNodeSet;

        float active;
        for (Node activeNode:activeNodeSet) {
            active = activeNode.maxActiveResponse.active;
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
            return feed(messageObject, matchedScore, dataSet);
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
            return matched(messageObject, dataSet, listener);
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

        Node node;
        if (input.startsWith(MessageTemplate.STICKER) ||
                (input.startsWith(MessageTemplate.IMAGE) && input.endsWith(".jpg")) ||
                (input.startsWith(MessageTemplate.AUDIO) && input.endsWith(".mp4")) ||
                (input.startsWith(MessageTemplate.VIDEO) && input.endsWith(".mp4"))
                ) {
            node = new Node(new String[]{input}, Node.Mode.MatchWhole);
        } else if (input.startsWith("#")) {
            node = new Node(new String[]{input.substring(1)}, Node.Mode.MatchWhole);
        } else {
            node = new Node(splitToList(input).toArray(new String[0]), null);
        }

        Object mode = messageObject.attributes.get("mode");

        if (mode!=null && !mode.toString().trim().isEmpty()) {
            node.addHook(mode.toString().trim(), Node.Mode.MatchMode);
        }

        return node;
    }

    public List<String> splitToList(String input) {

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

        return result;
    }

    public boolean isEmpty() {
        lock.readLock().lock();
        try {
            return dataSet.isEmpty();
        } finally {
            lock.readLock().unlock();
        }
    }

}
