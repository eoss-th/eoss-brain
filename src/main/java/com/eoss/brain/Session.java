package com.eoss.brain;

import com.eoss.brain.command.*;
import com.eoss.brain.command.talk.ProblemCommandNode;
import com.eoss.brain.net.Context;
import com.eoss.brain.net.Node;

import java.util.*;

/**
 * Created by eossth on 7/14/2017 AD.
 */
public class Session {

    public static class Entry {
        public final MessageObject messageObject;
        public final Node.Response response;
        public Entry(MessageObject messageObject, Node.Response response) {
            this.messageObject = messageObject;
            this.response = response;
        }
    }

    public final Context context;

    private boolean problemSolved = false;

    public String mode = null;

    public boolean silent = false;

    public boolean learning = false;

    private Entry lastEntry;

    public final Set<Node> activeNodePool = new HashSet<>();

    public final List<Node> protectedList = new ArrayList<>();

    public final List<CommandNode> commandList = new ArrayList<>();

    public Session(Context context) {
        this.context = context;
    }

    public String parse(MessageObject messageObject) {

        String result = null;
        for (CommandNode node : commandList) {
            if (node.matched(messageObject)) {
                result = node.execute(messageObject);
                break;
            }
        }

        if (problemSolved) {
            clearProblem();
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

    public void add(Set<Node> newActiveNodeSet) {
        for (Node newActiveNode:newActiveNodeSet) {
            if (!activeNodePool.add(newActiveNode)) {
                activeNodePool.remove(newActiveNode);
                activeNodePool.add(newActiveNode);
            }
            newActiveNode.maxActiveResponse.active *= 0.25f;
        }
    }

    public void clearPool() {
        for (Node activeNode: activeNodePool) {
            activeNode.clear();
        }
        activeNodePool.clear();
    }

    public void setLastEntry(MessageObject messageObject, Node.Response response) {
        lastEntry = new Entry(messageObject, response);
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
