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
public class TalkCommandNode extends CommandNode {

    private final Key lowConfidenceKey;

    public TalkCommandNode(Session session, Key lowConfidenceKey) {
        super(session);
        this.lowConfidenceKey = lowConfidenceKey;
    }

    @Override
    public boolean matched(MessageObject messageObject) {
        return true;
    }

    @Override
    public String execute(final MessageObject messageObject) {

        if (session.mode!=null && !session.mode.trim().isEmpty()) {
            messageObject.attributes.put("mode", session.mode.trim());
        }

        messageObject.attributes.put("wordCount", session.context.split(messageObject.toString()).length);

        final Set<Node> activeNodeSet = new HashSet<>();

        session.context.matched(messageObject, new ContextListener() {
            @Override
            public void callback(NodeEvent nodeEvent) {
                nodeEvent.node.feed(messageObject, 1);
                activeNodeSet.add(nodeEvent.node);
            }
        });

        Node maxActiveNode = Context.findMaxActiveNode(activeNodeSet, session.random);

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

        if (maxActiveNode!=null) {

            session.setLastEntry(messageObject, maxActiveNode);

            if (session.route(maxActiveNode)) {
                //Clean MessageObject
                String [] inputs = session.context.split(messageObject.toString());
                MessageObject cleanMessageObject = MessageObject.build(messageObject, maxActiveNode.clean(inputs));

                return ResponseCommandNode.build(session, responseText).execute(cleanMessageObject);
            }
        }

        return responseText;
    }
}
