package com.eoss.brain.command.line;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.MessageTemplate;
import com.eoss.brain.command.CommandNode;

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
