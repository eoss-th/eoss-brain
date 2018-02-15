package com.eoss.brain.command;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.NodeEvent;
import com.eoss.brain.net.Hook;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class LeaveCommandNode extends CommandNode {

    public final String leaveMsg;

    public LeaveCommandNode(Session session, String [] hooks, String leaveMsg) {
        super(session, hooks, Hook.Match.All);
        this.leaveMsg = leaveMsg;
    }

    @Override
    public String execute(MessageObject messageObject) {

        if (session.context.listener !=null) {
            session.context.listener.callback(
                    new NodeEvent(this,
                            MessageObject.build(messageObject,
                                    leaveMsg),
                            NodeEvent.Event.Leave));
        }

        return "";
    }
}
