package com.eoss.brain.command.data;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.command.CommandNode;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class ClearDataCommandNode extends CommandNode {

    public ClearDataCommandNode(Session session, String [] hooks) {
        super(session, hooks);
    }

    @Override
    public String execute(MessageObject messageObject) {

        session.context.clear();
        session.context.save();

        return successMsg();
    }
}
