package com.brainy.command.talk.menu;

import com.brainy.MessageObject;
import com.brainy.Session;
import com.brainy.command.talk.ResponseCommandNode;

public class MenuQuestionResponseCommandNode extends ResponseCommandNode {

    public MenuQuestionResponseCommandNode(Session session, String responseText) {
        super(session, responseText);
    }

    @Override
    public String execute(MessageObject messageObject) {
        String generatedOutput = super.execute(messageObject);

        messageObject.setText(generatedOutput);

        MenuAnswerResponseCommandNode answerResponseCommandNode = new MenuAnswerResponseCommandNode(session, messageObject.tail());
        session.insert(answerResponseCommandNode);

        return answerResponseCommandNode.createQuestion(messageObject.head()).toString();
    }

}
