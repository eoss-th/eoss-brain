package com.eoss.brain.command.talk;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.net.Node;
import com.eoss.brain.NodeEvent;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class RejectProblemCommandNode extends ProblemCommandNode {

    public final String successMsg;

    public final String cancelKey;

    public final String cancelMsg;

    String cancelReason;

    Session.Entry problemEntry;

    RejectProblemCommandNode(Session session, Session.Entry problemEntry, String successMsg, String cancelKey, String cancelMsg ) {

        super(session);
        this.problemEntry = problemEntry;
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
                        protectedFromNode.feed(messageObject, 1);
                        cancelReason = protectedFromNode.response();
                        protectedFromNode.release();
                        if (session.listener !=null) {
                            session.listener.callback(new NodeEvent(this, messageObject, NodeEvent.Event.ReservedWords));
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

        //Same Hook; just change the response
        if (newNode.sameHooks(problemEntry.node)) {

            newNode = session.context.get(newNode.hookList());
            newNode.setResponse(messageObject.toString());

        } else {

            newNode.setResponse(messageObject.toString());
            session.context.add(newNode);

            if (session.listener!=null) {
                session.listener.callback(new NodeEvent(newNode, problemEntry.messageObject, NodeEvent.Event.NewNodeAdded));
            }
        }

        session.context.save();

        session.setLastEntry(problemEntry.messageObject, newNode);

        return successMsg;
    }
}
