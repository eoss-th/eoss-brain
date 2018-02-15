package com.eoss.brain.command;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.NodeEvent;
import com.eoss.brain.net.Hook;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class RegisterAdminCommandNode extends CommandNode {

    public RegisterAdminCommandNode(Session session, String [] hooks) {
        super(session, hooks, Hook.Match.Head);
    }

    @Override
    public String execute(MessageObject messageObject) {

        if (session.context.listener !=null) {

            String displayName = clean(messageObject.toString());
            session.context.listener.callback(
                    new NodeEvent(this,
                            MessageObject.build(messageObject, displayName),
                            NodeEvent.Event.RegisterAdmin));
        }

        return "";
    }
}
