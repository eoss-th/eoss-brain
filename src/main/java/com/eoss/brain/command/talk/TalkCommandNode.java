package com.eoss.brain.command.talk;

import com.eoss.brain.MessageObject;
import com.eoss.brain.NodeEvent;
import com.eoss.brain.Session;
import com.eoss.brain.command.CommandNode;
import com.eoss.brain.net.Context;
import com.eoss.brain.net.ContextListener;
import com.eoss.brain.net.Node;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class TalkCommandNode extends CommandNode {

    private final List<String> lowConfidenceKeys;

    public TalkCommandNode(Session session) {
        this(session, null);
    }

    public TalkCommandNode(Session session, List<String> lowConfidenceKeys) {
        super(session);
        if (lowConfidenceKeys!=null && lowConfidenceKeys.size()!=4) throw new IllegalArgumentException("lowConfidenceKeys must have 4 elements");
        this.lowConfidenceKeys = lowConfidenceKeys;
    }

    @Override
    public boolean matched(MessageObject messageObject) {
        return true;
    }

    private Set<Node> think(final MessageObject messageObject, final float score) {

        messageObject.attributes.put("wordCount", session.context.split(messageObject.toString()).length);

        final Set<Node> activeNodeSet = new HashSet<>();

        //Feed Session's nodes
        session.context.matched(messageObject, session.activeNodePool, new ContextListener() {
            @Override
            public void callback(NodeEvent nodeEvent) {

                nodeEvent.node.feed(messageObject, score);

                if (!activeNodeSet.add(nodeEvent.node)) {
                    activeNodeSet.remove(nodeEvent.node);
                    activeNodeSet.add(nodeEvent.node);
                }
            }
        });

        //Not found!Fetch from Context
        if (activeNodeSet.isEmpty()) {

            session.context.matched(messageObject, new ContextListener() {
                @Override
                public void callback(NodeEvent nodeEvent) {
                    nodeEvent.node.feed(messageObject, score);
                    activeNodeSet.add(nodeEvent.node);
                }
            });

        }

        return activeNodeSet;
    }

    @Override
    public String execute(MessageObject messageObject) {

        if (session.mode!=null && !session.mode.trim().isEmpty()) {
            messageObject.attributes.put("mode", session.mode.trim());
        }

        Set<Node> activeNodeSet = think(messageObject, 1);

        Node maxActiveNode = Context.findMaxActiveNode(activeNodeSet);

        final float confidenceRate;
        String responseText;
        if (maxActiveNode==null) {
            confidenceRate = 0.0f;
            responseText = "";
        } else {
            confidenceRate = maxActiveNode.active();
            responseText = maxActiveNode.response();
        }

        final float MIN_LOW = 0.05f;

        if (session.learning && confidenceRate <= MIN_LOW) {
            responseText = messageObject + " " + lowConfidenceKeys.get(3);
            session.insert(new LowConfidenceProblemCommandNode(session, messageObject, lowConfidenceKeys.get(0), lowConfidenceKeys.get(1), lowConfidenceKeys.get(2)));
            session.clearPool();
            return responseText;
        }

        if (confidenceRate < MIN_LOW) {

            responseText = "";
            if (session.context.listener!=null) {
                session.context.listener.callback(new NodeEvent(this, messageObject, NodeEvent.Event.LowConfidence));
            }

        }

        maxActiveNode.release();
        session.setLastEntry(messageObject, maxActiveNode);
        session.merge(activeNodeSet);
        session.merge(think(MessageObject.build(messageObject, responseText), confidenceRate));
        session.release(0.5f);

        //Super Confidence
        if (confidenceRate >= 1) {
            if (session.context.listener!=null) {
                session.context.listener.callback(new NodeEvent(this, messageObject, NodeEvent.Event.SuperConfidence));
            }
        }

        return responseText;
    }
}
