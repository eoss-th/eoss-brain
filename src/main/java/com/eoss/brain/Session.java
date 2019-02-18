package com.eoss.brain;

import com.eoss.brain.command.*;
import com.eoss.brain.command.talk.ProblemCommandNode;
import com.eoss.brain.net.Context;
import com.eoss.brain.net.ContextListener;
import com.eoss.brain.net.Node;
import com.eoss.brain.net.SessionListener;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by eossth on 7/14/2017 AD.
 */
public class Session implements Serializable {

    public static class Entry implements Serializable {
        public final MessageObject messageObject;
        public final Node node;
        public Entry(MessageObject messageObject, Node node) {
            this.messageObject = messageObject;
            this.node = node;
        }
    }

    public Context context;

    public final Random random = new Random();

    private boolean problemSolved = false;

    public String mode = null;

    public boolean silent = false;

    public boolean learning = false;

    private Entry lastEntry;

    private final Set<Node> activeNodePool = new HashSet<>();

    private final Set<Node> routeSet = new HashSet<>();

    public final Map<String, String> variableMap = new HashMap<>();

    public final List<Node> protectedList = new ArrayList<>();

    public final List<AdminCommandNode> adminCommandList = new ArrayList<>();

    public final List<CommandNode> commandList = new ArrayList<>();

    protected ReadWriteLock lock = new ReentrantReadWriteLock();

    public SessionListener listener;

    public Session() { }

    public Session(Context context) {
        this.context = context;
    }

    public Session context(Context context) {
        this.context = context;
        return this;
    }

    public Session callback(SessionListener callback) {
        this.listener = callback;
        return this;
    }

    public String parse(MessageObject messageObject) {

        clearRoute();

        boolean isAdminCommandExecuted = false;
        String result = null;

        for (AdminCommandNode node : adminCommandList) {
            if (node.matched(messageObject)) {
                result = node.execute(messageObject);
                isAdminCommandExecuted = true;
                break;
            }
        }

        if (isAdminCommandExecuted) {

            clearProblem();
            clearPool();
            clearLastEntry();

        } else {
            for (CommandNode node : commandList) {
                if (node.matched(messageObject)) {
                    result = node.execute(messageObject);
                    break;
                }
            }

            if (problemSolved) {
                clearProblem();
            }
        }

        return silent||result==null?"":result;

    }

    public void solved(boolean problemSolved) {
        this.problemSolved = problemSolved;
    }

    public boolean hasProblem() {
        return !commandList.isEmpty() && commandList.get(0) instanceof ProblemCommandNode;
    }

    public void clearProblem() {
        while (hasProblem()) {
            commandList.remove(0);
        }
    }

    @Deprecated
    public void releaseLastActive() {
        if (lastEntry!=null) {
            if (lastEntry.node!=null)
                lastEntry.node.release();
        }
    }

    @Deprecated
    public Set<Node> activeNodePool() {
        lock.readLock().lock();
        try {
            return new HashSet<>(activeNodePool);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Deprecated
    public void merge(Set<Node> newActiveNodeSet) {
        lock.writeLock().lock();
        try {
            for (Node newActiveNode:newActiveNodeSet) {
                if (!activeNodePool.add(newActiveNode)) {
                    activeNodePool.remove(newActiveNode);
                    activeNodePool.add(newActiveNode);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Deprecated
    public void release(float rate) {

        Set<Node> deadList = new HashSet<>();

        lock.readLock().lock();
        try {
            for (Node activeNode:activeNodePool) {
                activeNode.release(rate);
                if (activeNode.active()<0.25f) {
                    deadList.add(activeNode);
                }
            }
        } finally {
            lock.readLock().unlock();
        }

        lock.writeLock().lock();
        try {
            for (Node deadNode:deadList) {
                deadNode.release();
                activeNodePool.remove(deadNode);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Deprecated
    public void clearPool() {
        lock.writeLock().lock();
        try {
            for (Node activeNode: activeNodePool) {
                activeNode.release();
            }
            activeNodePool.clear();
        } finally {
            lock.writeLock().unlock();
        }

    }

    public void setLastEntry(MessageObject messageObject, Node node) {
        lastEntry = new Entry(messageObject, node);
    }

    public void clearLastEntry() {
        lastEntry = null;
    }

    public Entry lastEntry() {
        return lastEntry;
    }

    public void insert(ProblemCommandNode problemCommandNode) {
        commandList.add(0, problemCommandNode);
        problemSolved = false;
    }

    private void clearRoute() {
        routeSet.clear();
    }

    public boolean route(Node node) {
        return routeSet.add(node);
    }

    public boolean canRoute(Node node) {
        return !routeSet.contains(node);
    }

    public void setVariable(String name, String value) {
        variableMap.put(name, value);
    }

    public String getVariable(String name) {
        String value = variableMap.get(name);
        if (value==null) value = "";
        return value;
    }

    private final Map<String, String> paramMap(MessageObject messageObject) {

        String input = messageObject.toString();

        String [] params = context.split(input);

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("##", input);

        for (int i=0;i<params.length;i++) {
            paramMap.put("#" + (i+1), params[i]);
        }

        List<String> parameters = (List<String>) messageObject.attributes.get("parameters");
        if (parameters!=null) {
            for (int i=0;i<parameters.size();i++) {
                paramMap.put("%" + (i+1), parameters.get(i));
            }
        }

        return paramMap;
    }

    private final String parameterized(Map<String, String> paramMap, String text) {
        String output = text;
        for (Map.Entry<String, String> entry:paramMap.entrySet()) {
            output = output.replace(entry.getKey(), entry.getValue());
        }
        return output;
    }

    public final String parameterized(MessageObject messageObject, String responseText) {
        /**
         * ## #1 #2..
         */
        String parameterizedText = parameterized(paramMap(messageObject), responseText);

        /**
         * Session Variables
         */
        parameterizedText = parameterized(variableMap, parameterizedText);
        return parameterizedText.trim();
    }

}
