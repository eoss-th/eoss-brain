package com.brainy.command.data;

import com.brainy.MessageObject;
import com.brainy.Session;
import com.brainy.command.CommandNode;
import com.brainy.net.Hook;

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
