package com.eoss.brain.command.talk;

import com.eoss.brain.MessageObject;
import com.eoss.brain.Session;
import com.eoss.brain.command.wakeup.WakeupCommandNode;

public class ForwardResponseCommandNode extends ResponseCommandNode {

    public ForwardResponseCommandNode(Session session, String question) {
        super(session, question);
    }

    @Override
    public boolean matched(MessageObject messageObject) {
        session.solved(true);
        return true;
    }

    @Override
    public String execute(MessageObject messageObject) {
        MessageObject forwardMessageObject = MessageObject.build(messageObject, super.execute(messageObject));
        return new TalkCommandNode(session, WakeupCommandNode.KEY).execute(forwardMessageObject);
    }
}
