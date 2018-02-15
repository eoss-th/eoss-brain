package com.eoss.brain.command;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.net.Hook;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class EnableModeCommandNode extends CommandNode {

    public EnableModeCommandNode(Session session, String [] hooks) {
        super(session, hooks, Hook.Match.Head);
    }

    @Override
    public String execute(MessageObject messageObject) {

        session.mode = clean(messageObject.toString());

        return successMsg();
    }
}
