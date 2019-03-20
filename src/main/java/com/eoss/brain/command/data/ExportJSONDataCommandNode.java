package com.eoss.brain.command.data;

import com.eoss.brain.MessageObject;
import com.eoss.brain.Session;
import com.eoss.brain.command.CommandNode;
import com.eoss.brain.net.Hook;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class ExportJSONDataCommandNode extends CommandNode {

    public ExportJSONDataCommandNode(Session session, String [] hooks) {
        super(session, hooks, Hook.Match.Head);
    }

    @Override
    public String execute(MessageObject messageObject) {

        return session.context.toJSONString();
    }

}