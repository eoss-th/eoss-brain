package com.brainy.command.data;

import com.brainy.Session;
import com.brainy.MessageObject;
import com.brainy.command.CommandNode;

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
