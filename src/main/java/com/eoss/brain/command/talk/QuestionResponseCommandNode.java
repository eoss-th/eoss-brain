package com.eoss.brain.command.talk;

import com.eoss.brain.MessageObject;
import com.eoss.brain.Session;

public class QuestionResponseCommandNode extends ResponseCommandNode {

    public QuestionResponseCommandNode(Session session, String responseText) {
        super(session, responseText);
    }

    @Override
    public String execute(MessageObject messageObject) {
        String generatedOutput = super.execute(messageObject);
        session.insert(new AnswerResponseCommandNode(session, generatedOutput));
        return generatedOutput;
    }

}
