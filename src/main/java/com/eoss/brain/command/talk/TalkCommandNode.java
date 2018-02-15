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

    @Override
    public String execute(final MessageObject messageObject) {
        messageObject.attributes.put("wordCount", session.context.split(messageObject.toString()).length);
        if (session.mode!=null && !session.mode.trim().isEmpty()) {
            messageObject.attributes.put("mode", session.mode.trim());
        }

        final Set<Node> activeNodeSet = new HashSet<>();

        session.context.matched(messageObject, session.activeNodePool, new ContextListener() {
            @Override
            public void callback(NodeEvent nodeEvent) {
                nodeEvent.node.feed(messageObject);
                activeNodeSet.add(nodeEvent.node);
            }
        });

        if (activeNodeSet.isEmpty()) {
            session.context.matched(messageObject, new ContextListener() {
                @Override
                public void callback(NodeEvent nodeEvent) {
                    nodeEvent.node.feed(messageObject);
                    if (!activeNodeSet.add(nodeEvent.node)) {
                        activeNodeSet.remove(nodeEvent.node);
                        activeNodeSet.add(nodeEvent.node);
                    }
                }
            });
        }

        List<Node> maxActiveNodeList = Context.findActiveNodes(activeNodeSet, 0.90f);

        Node maxActiveNode;
        float confidenceRate;
        String responseText;
        if (maxActiveNodeList.isEmpty()) {
            maxActiveNode = null;
            confidenceRate = 0.0f;
            responseText = "";
        } else {
            maxActiveNode = maxActiveNodeList.get(0);
            responseText = maxActiveNode.response();

            if (maxActiveNodeList.size()==1) {
                confidenceRate = maxActiveNode.active();
            } else {
                confidenceRate = maxActiveNode.active() / maxActiveNodeList.size();
            }
        }

        final float MIN_LOW = 0.05f;
        if (confidenceRate <= MIN_LOW) {

            if (session.learning && lowConfidenceKeys!=null) {
                session.insert(new LowConfidenceProblemCommandNode(session, messageObject, lowConfidenceKeys.get(0), lowConfidenceKeys.get(1), lowConfidenceKeys.get(2)));
                responseText = messageObject + " " + lowConfidenceKeys.get(3);
            } else {
                /*
                String query = messageObject.toString().trim();
                if (session.context.domain!=null && !session.context.domain.trim().isEmpty()) {
                    query += " site:" + session.context.domain;
                }
                new GoogleCommandNode(session, null, 1).execute(MessageObject.build(messageObject,  query));
                responseText = "";*/
                responseText = "ช่วยอธิบายให้ระเอียดอีกหน่อยได้ไหมคะ";
            }

        } else if (confidenceRate <= 0.75f) {

            if (session.learning && lowConfidenceKeys!=null) {
                responseText += " ?";
            }

        } else if (confidenceRate > 1) {
            //Super Confidence

        }

        if (confidenceRate > MIN_LOW) {
            session.setLastEntry(messageObject, maxActiveNode);
            maxActiveNode.release();
        }

        session.add(activeNodeSet);

        return responseText;
    }
}
