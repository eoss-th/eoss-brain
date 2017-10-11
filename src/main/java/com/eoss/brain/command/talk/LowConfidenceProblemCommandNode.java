package com.eoss.brain.command.talk;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.net.Node;
import com.eoss.brain.NodeEvent;
import com.eoss.brain.net.Context;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class LowConfidenceProblemCommandNode extends ProblemCommandNode {

    public final String successMsg;

    public final String cancelKey;

    public final String cancelMsg;

    String cancelReason;

    MessageObject problemMessage;

    public LowConfidenceProblemCommandNode(Session session, MessageObject problemMessage, String successMsg, String cancelKey, String cancelMsg) {

        super(session);
        this.problemMessage = problemMessage;
        this.successMsg = successMsg;
        this.cancelKey = cancelKey;
        this.cancelMsg = cancelMsg;
    }

    @Override
    public boolean matched(MessageObject messageObject) {

        this.cancelReason = null;
        try {
            if (messageObject.toString().equals(cancelKey)) {
                cancelReason = cancelMsg;
            } else {
                for (Node protectedFromNode: session.protectedList) {
                    if (protectedFromNode.matched(messageObject)) {
                        protectedFromNode.feed(messageObject);
                        cancelReason = protectedFromNode.maxActiveResponseText();
                        protectedFromNode.clear();
                        if (session.context.listener !=null) {
                            session.context.listener.callback(new NodeEvent(this, messageObject, NodeEvent.Event.ReservedWords));
                        }
                        break;
                    }
                }
            }
        } finally {
            session.solved(true);
        }

        return true;
    }

    @Override
    public String execute(MessageObject messageObject) {

        if (cancelReason!=null) return cancelReason;

        Node newNode = session.context.build(problemMessage);

        session.context.add(newNode);

        session.setLastEntry(problemMessage, newNode.addResponse(messageObject.toString()));

        return successMsg;
    }
}
