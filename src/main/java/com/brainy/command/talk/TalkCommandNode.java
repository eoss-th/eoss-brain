package com.brainy.command.talk;

import com.brainy.MessageObject;
import com.brainy.NodeEvent;
import com.brainy.Session;
import com.brainy.command.CommandNode;
import com.brainy.net.Context;
import com.brainy.net.ContextListener;
import com.brainy.net.Node;

import java.util.*;

/**
 * Created by eossth on 7/31/2017 AD.
 */
@Deprecated
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
    public String execute(MessageObject messageObject) {

        if (session.mode!=null && !session.mode.trim().isEmpty()) {
            messageObject.attributes.put("mode", session.mode.trim());
        }

        if (!messageObject.isSplitted()) {
            messageObject.split(session.context);
        }

        final Set<Node> activeNodeSet = new HashSet<>();

        List<Node> alreadyRoutedNodeList = new ArrayList<>();

        session.context.matched(messageObject, new ContextListener() {
            @Override
            public void callback(NodeEvent nodeEvent) {
                /**
                 * Protect from Cyclic Forwarding
                 */
                if (session.reachMaximumRoute()==false) {
                    nodeEvent.node.feed(messageObject);
                    activeNodeSet.add(nodeEvent.node);
                } else {
                    alreadyRoutedNodeList.add(nodeEvent.node);
                }
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
        if (confidenceRate > 1) {

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

        } else if (!alreadyRoutedNodeList.isEmpty()) {

            responseText = session.lastEntry().node.response();

        } else {

            responseText = "";
            if (session.listener!=null) {
                //Warning! maxActiveNode may be null
                session.listener.callback(new NodeEvent(maxActiveNode, messageObject, NodeEvent.Event.LowConfidence));
            }

        }

        if (maxActiveNode!=null) {

            session.setLastEntry(messageObject, maxActiveNode);

            //Clean MessageObject
            String input = messageObject.toString();
            StringBuilder forwardInput = new StringBuilder(maxActiveNode.clean(input));
            MessageObject forwardMessageObject = MessageObject.build(messageObject, forwardInput.toString().trim());
            return ResponseCommandNode.build(session, responseText).execute(forwardMessageObject);
        }

        return responseText;
    }
}
