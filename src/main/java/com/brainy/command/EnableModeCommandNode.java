package com.brainy.command;

import com.brainy.Session;
import com.brainy.MessageObject;
import com.brainy.net.Hook;

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
