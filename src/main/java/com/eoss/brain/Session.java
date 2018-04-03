package com.eoss.brain;

import com.eoss.brain.command.*;
import com.eoss.brain.command.talk.ProblemCommandNode;
import com.eoss.brain.net.Context;
import com.eoss.brain.net.Node;

import java.io.Serializable;
import java.util.*;

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

    private boolean problemSolved = false;

    public String mode = null;

    public boolean silent = false;

    public boolean learning = false;

    private Entry lastEntry;

    public final Set<Node> activeNodePool = new HashSet<>();

    public final List<Node> protectedList = new ArrayList<>();

    public final List<AdminCommandNode> adminCommandList = new ArrayList<>();

    public final List<CommandNode> commandList = new ArrayList<>();

    public Session() {

    }

    public Session(Context context) {
        this.context = context;
    }

    public Session context(Context context) {
        this.context = context;
        return this;
    }

    public String parse(MessageObject messageObject) {

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

    public void releaseLastActive() {
        if (lastEntry!=null) {
            if (lastEntry.node!=null)
                lastEntry.node.release();
        }
    }

    public void merge(Set<Node> newActiveNodeSet) {
        for (Node newActiveNode:newActiveNodeSet) {
            if (!activeNodePool.add(newActiveNode)) {
                activeNodePool.remove(newActiveNode);
                activeNodePool.add(newActiveNode);
            }
        }
    }

    public void release(float rate) {

        Set<Node> deadList = new HashSet<>();
        for (Node activeNode:activeNodePool) {
            activeNode.release(rate);
            if (activeNode.active()<0.25f) {
                deadList.add(activeNode);
            }
        }

        for (Node deadNode:deadList) {
            deadNode.release();
            activeNodePool.remove(deadNode);
        }
    }

    public void clearPool() {
        for (Node activeNode: activeNodePool) {
            activeNode.release();
        }
        activeNodePool.clear();
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

}
