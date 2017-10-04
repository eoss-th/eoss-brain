package com.eoss.brain.command.data;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.command.CommandNode;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class RestoreDataCommandNode extends CommandNode {

    public RestoreDataCommandNode(Session session, String [] hooks) {
        super(session, hooks);
    }

    @Override
    public String execute(MessageObject messageObject) {
        try {

            session.context.load(session.context.name+".backup");
            session.context.save();

            return successMsg();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return failMsg();
    }
}
