package com.eoss.brain.command.talk.menu;

import com.eoss.brain.Session;
import com.eoss.brain.command.talk.ForwardResponseCommandNode;
import com.eoss.brain.command.talk.QuestionResponseCommandNode;
import com.eoss.brain.command.talk.ResponseCommandNode;

public class MenuResponseCommandNode extends ResponseCommandNode {

    public MenuResponseCommandNode(Session session, String responseText) {
        super(session, responseText);
    }

    public static ResponseCommandNode build(Session session, String responseText) {
        if (responseText.endsWith("?")) {
            return new MenuQuestionResponseCommandNode(session, responseText.substring(0, responseText.length()-1));
        }
        if (responseText.endsWith("!")) {
            return new MenuForwardResponseCommandNode(session, responseText.substring(0, responseText.length()-1));
        }
        return new ResponseCommandNode(session, responseText);
    }


}
