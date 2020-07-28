package com.brainy.command.wakeup;

import com.brainy.Session;
import com.brainy.MessageObject;
import com.brainy.MessageTemplate;
import com.brainy.command.CommandNode;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class SleepCommandNode extends CommandNode {

    private final CommandNode wakeupCommandNode;

    public SleepCommandNode(Session session, String [] hooks, CommandNode wakeupCommandNode) {
        super(session, hooks);
        this.wakeupCommandNode = wakeupCommandNode;
    }

    @Override
    public String execute(MessageObject messageObject) {
        session.commandList.clear();
        session.commandList.add(wakeupCommandNode);
        return MessageTemplate.STICKER + "1:1";
    }
}
