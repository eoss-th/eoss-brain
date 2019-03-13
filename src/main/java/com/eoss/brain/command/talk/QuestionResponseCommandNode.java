package com.eoss.brain.command.talk;

import com.eoss.brain.MessageObject;
import com.eoss.brain.Session;

import java.util.Arrays;
import java.util.List;

public class QuestionResponseCommandNode extends ResponseCommandNode {

    public QuestionResponseCommandNode(Session session, String responseText) {
        super(session, responseText);
    }

    @Override
    public String execute(MessageObject messageObject) {
        String generatedOutput = super.execute(messageObject);

        //Override Question
        String forwardMessage = generatedOutput;
        int lastIndexOfComma = generatedOutput.lastIndexOf(",");
        if (lastIndexOfComma!=-1 && lastIndexOfComma<generatedOutput.length()-1) {
            forwardMessage = generatedOutput.substring(lastIndexOfComma + 1).trim();
            generatedOutput = generatedOutput.substring(0, lastIndexOfComma);
        }

        session.insert(new AnswerResponseCommandNode(session, forwardMessage));
        return generatedOutput;
    }

}
