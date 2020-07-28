package com.brainy.command.data;

import com.brainy.Session;
import com.brainy.MessageObject;
import com.brainy.command.CommandNode;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class SaveDataCommandNode extends CommandNode {

    public SaveDataCommandNode(Session session, String [] hooks) {
        super(session, hooks);
    }

    @Override
    public String execute(MessageObject messageObject) {

        try {
            session.context.save();
            return successMsg();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return failMsg();
    }
}
