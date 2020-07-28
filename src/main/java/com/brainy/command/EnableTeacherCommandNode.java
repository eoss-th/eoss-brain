package com.brainy.command;

import com.brainy.Session;
import com.brainy.MessageObject;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class EnableTeacherCommandNode extends CommandNode {

    public EnableTeacherCommandNode(Session session, String [] hooks) {
        super(session, hooks);
    }

    @Override
    public String execute(MessageObject messageObject) {

        session.learning = true;

        return successMsg();
    }
}
