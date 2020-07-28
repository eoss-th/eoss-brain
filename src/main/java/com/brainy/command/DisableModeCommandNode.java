package com.brainy.command;

import com.brainy.Session;
import com.brainy.MessageObject;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class DisableModeCommandNode extends CommandNode {

    public DisableModeCommandNode(Session session, String [] hooks) {
        super(session, hooks);
    }

    @Override
    public String execute(MessageObject messageObject) {

        session.mode = null;

        return successMsg();
    }
}
