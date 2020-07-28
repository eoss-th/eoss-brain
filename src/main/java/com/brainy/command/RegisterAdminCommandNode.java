package com.brainy.command;

import com.brainy.Session;
import com.brainy.MessageObject;
import com.brainy.NodeEvent;
import com.brainy.net.Hook;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class RegisterAdminCommandNode extends CommandNode {

    public RegisterAdminCommandNode(Session session, String [] hooks) {
        super(session, hooks, Hook.Match.Head);
    }

    @Override
    public String execute(MessageObject messageObject) {

        if (session.listener !=null) {

            String displayName = clean(messageObject.toString());
            session.listener.callback(
                    new NodeEvent(this,
                            MessageObject.build(messageObject, displayName),
                            NodeEvent.Event.RegisterAdmin));
        }

        return "";
    }
}
