package com.eoss.brain.command.talk;

import com.eoss.brain.MessageObject;
import com.eoss.brain.Session;
import com.eoss.brain.command.wakeup.WakeupCommandNode;

public class AnswerResponseCommandNode extends ResponseCommandNode {

    public AnswerResponseCommandNode(Session session, String question) {
        super(session, question);
    }

    @Override
    public boolean matched(MessageObject messageObject) {
        session.solved(true);
        return true;
    }

    @Override
    public String execute(MessageObject messageObject) {
        MessageObject questionMessageObject = MessageObject.build(messageObject, responseText + " " + messageObject.toString());
        questionMessageObject.split();
        return new TalkCommandNode(session, WakeupCommandNode.KEY).execute(questionMessageObject);
    }
}
