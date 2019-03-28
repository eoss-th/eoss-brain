package com.eoss.brain.command.talk.menu;

import com.eoss.brain.MessageObject;
import com.eoss.brain.NodeEvent;
import com.eoss.brain.Session;
import com.eoss.brain.command.talk.Question;
import com.eoss.brain.command.talk.ResponseCommandNode;

import java.util.ArrayList;
import java.util.List;

public class MenuQuestionResponseCommandNode extends ResponseCommandNode {

    public MenuQuestionResponseCommandNode(Session session, String responseText) {
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

        MenuAnswerResponseCommandNode answerResponseCommandNode = new MenuAnswerResponseCommandNode(session, forwardMessage);
        session.insert(answerResponseCommandNode);
        if (session.listener != null) {
            List<Question> questionList = new ArrayList<>();
            questionList.add(answerResponseCommandNode.createQuestion(generatedOutput));
            messageObject.attributes.put("Question", questionList);
            session.listener.callback(new NodeEvent(null, messageObject, NodeEvent.Event.Question));
        }

        return generatedOutput;
    }

}
