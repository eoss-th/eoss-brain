package com.brainy.command;

import com.brainy.Session;
import com.brainy.MessageObject;
import com.brainy.NodeEvent;
import com.brainy.net.Hook;

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

        if (session.listener !=null) {
            session.listener.callback(
                    new NodeEvent(this,
                            MessageObject.build(messageObject,
                                    leaveMsg),
                            NodeEvent.Event.Leave));
        }

        return "";
    }
}
