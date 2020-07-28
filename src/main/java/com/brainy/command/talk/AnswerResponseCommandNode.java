package com.brainy.command.talk;

import com.brainy.command.wakeup.WakeupCommandNode;
import com.brainy.MessageObject;
import com.brainy.Session;

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
