package com.brainy.command.data;

import com.brainy.Session;
import com.brainy.MessageObject;
import com.brainy.command.CommandNode;

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
        try {
            session.context.load();
            session.clearPool();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return successMsg();
    }
}
