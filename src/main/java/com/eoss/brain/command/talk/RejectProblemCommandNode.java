package com.eoss.brain.command.talk;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.net.Node;
import com.eoss.brain.NodeEvent;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class RejectProblemCommandNode extends ProblemCommandNode {

    public final String key;

    public final String successMsg;

    public final String cancelKey;

    public final String cancelMsg;


    String cancelReason;

    Session.Entry problemEntry;

    RejectProblemCommandNode(Session session, Session.Entry problemEntry, String key, String successMsg, String cancelKey, String cancelMsg ) {

        super(session);
        this.problemEntry = problemEntry;
        this.key = key;
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
                        cancelReason = protectedFromNode.response();
                        protectedFromNode.release();
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

        Node newNode = session.context.build(problemEntry.messageObject);

        if (newNode.sameHooks(problemEntry.node)) {
            newNode = problemEntry.node;
        } else {
            session.context.add(newNode);
        }

        newNode.setResponse(messageObject.toString());
        session.setLastEntry(problemEntry.messageObject, newNode);

        return successMsg;
    }
}
