package com.eoss.brain.command;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class DebugCommandNode extends CommandNode {

    public DebugCommandNode(Session session, String [] hooks) {
        super(session, hooks);
    }

    @Override
    public String execute(MessageObject messageObject) {
        return session.context.toString();
    }
}
