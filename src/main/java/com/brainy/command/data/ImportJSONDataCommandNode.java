package com.brainy.command.data;

import com.brainy.MessageObject;
import com.brainy.Session;
import com.brainy.command.CommandNode;
import com.brainy.net.Hook;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class ImportJSONDataCommandNode extends CommandNode {

    public ImportJSONDataCommandNode(Session session, String [] hooks) {
        super(session, hooks, Hook.Match.Head);
    }

    @Override
    public String execute(MessageObject messageObject) {

        try {

            session.context.loadJSON(clean(messageObject.toString()));
            session.context.save();

            return successMsg();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return failMsg();
    }

}
