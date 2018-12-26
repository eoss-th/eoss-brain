package com.eoss.brain.command.talk;

import com.eoss.brain.MessageObject;
import com.eoss.brain.Session;
import com.eoss.brain.command.wakeup.WakeupCommandNode;

import java.util.HashMap;
import java.util.Map;

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
        return new TalkCommandNode(session, WakeupCommandNode.KEY).execute(questionMessageObject);
    }
}
