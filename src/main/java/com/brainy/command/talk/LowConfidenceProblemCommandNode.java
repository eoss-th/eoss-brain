package com.brainy.command.talk;

import com.brainy.Session;
import com.brainy.MessageObject;
import com.brainy.net.Node;
import com.brainy.NodeEvent;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class LowConfidenceProblemCommandNode extends ProblemCommandNode {

    public final Key key;

    String cancelReason;

    MessageObject problemMessage;

    public LowConfidenceProblemCommandNode(Session session, MessageObject problemMessage, Key key) {

        super(session);
        this.problemMessage = problemMessage;
        this.key = key;
    }

    @Override
    public boolean matched(MessageObject messageObject) {

        try {
            if (key.cancelKeys.contains(messageObject.toString())) {
                cancelReason = key.doneMsg;
            } else {
                for (Node protectedFromNode: session.protectedList) {
                    if (protectedFromNode.matched(messageObject)) {
                        protectedFromNode.feed(messageObject);
                        cancelReason = protectedFromNode.response();
                        protectedFromNode.release();

                        if (session.listener!=null) {
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

        Node newNode = session.context.build(problemMessage);
        newNode.setResponse(messageObject.toString());
        session.context.add(newNode);
        
        session.context.save();

        if (session.listener!=null) {
            session.listener.callback(new NodeEvent(newNode, problemMessage, NodeEvent.Event.NewNodeAdded));
        }

        session.setLastEntry(problemMessage, newNode);

        return key.doneMsg;
    }
}
