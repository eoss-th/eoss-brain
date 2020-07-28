package com.brainy.command.talk.menu;

import com.brainy.Session;
import com.brainy.command.talk.ResponseCommandNode;

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
