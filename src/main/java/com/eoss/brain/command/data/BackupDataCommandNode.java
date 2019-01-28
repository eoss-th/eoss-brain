package com.eoss.brain.command.data;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.command.CommandNode;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class BackupDataCommandNode extends CommandNode {

    public BackupDataCommandNode(Session session, String [] hook) {

        super(session, hook);
    }

    @Override
    public String execute(MessageObject messageObject) {
        try {

            session.context.save(session.context.name+".backup");

            return successMsg();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
