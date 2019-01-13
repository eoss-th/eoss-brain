package com.eoss.brain.command.talk;

import com.eoss.brain.MessageObject;
import com.eoss.brain.NodeEvent;
import com.eoss.brain.Session;
import com.eoss.brain.command.CommandNode;
import com.eoss.brain.net.Context;
import com.eoss.brain.net.ContextListener;
import com.eoss.brain.net.Node;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by eossth on 7/31/2017 AD.
 */
@Deprecated
public class ThinkableTalkCommandNode extends CommandNode {

    private final Key lowConfidenceKey;

    public ThinkableTalkCommandNode(Session session, Key lowConfidenceKey) {
        super(session);
        this.lowConfidenceKey = lowConfidenceKey;
    }

    @Override
    public boolean matched(MessageObject messageObject) {
        return true;
    }

    private Set<Node> think(final MessageObject messageObject) {

        messageObject.attributes.put("wordCount", session.context.split(messageObject.toString()).length);

        final Set<Node> activeNodeSet = new HashSet<>();

        //Feed Session's nodes
        session.context.matched(messageObject, session.activeNodePool(), new ContextListener() {
            @Override
            public void callback(NodeEvent nodeEvent) {

                nodeEvent.node.feed(messageObject);

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
                    nodeEvent.node.feed(messageObject);
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

        Set<Node> activeNodeSet = think(messageObject);

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

        final float UPPER_BOUND = 0.5f;
        final float LOWER_BOUND = 0.05f;

        if (session.learning && confidenceRate <= LOWER_BOUND) {
            responseText = messageObject + " " + lowConfidenceKey.questMsg;
            session.insert(new LowConfidenceProblemCommandNode(session, messageObject, lowConfidenceKey));
            session.clearPool();
            return responseText;
        }

        //Super Confidence
        if (confidenceRate >= 1) {

            if (session.listener != null) {
                session.listener.callback(new NodeEvent(maxActiveNode, messageObject, NodeEvent.Event.SuperConfidence));
            }

        } else if (confidenceRate > UPPER_BOUND) {

            //Nothing TO DO; Just Answer

        } else if (confidenceRate > LOWER_BOUND) {

            //hesitation
            if (session.listener != null) {
                session.listener.callback(new NodeEvent(maxActiveNode, messageObject, NodeEvent.Event.HesitateConfidence));
            }

        } else {

            responseText = "";
            if (session.listener!=null) {
                //Warning! maxActiveNode may be null
                session.listener.callback(new NodeEvent(maxActiveNode, messageObject, NodeEvent.Event.LowConfidence));
            }

        }

        if (confidenceRate >= 0.75) {
            session.clearPool();
        } else {
            session.merge(activeNodeSet);
            session.merge(think(MessageObject.build(messageObject, responseText)));
            session.release(0.5f);
        }

        if (maxActiveNode!=null) {

            session.setLastEntry(messageObject, maxActiveNode);

            if (session.route(maxActiveNode)) {
                //
            }
        }

        return responseText;
    }
}
