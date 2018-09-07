package com.eoss.brain.command;

import com.eoss.brain.MessageObject;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class AdminCommandNode extends CommandNode {

    protected final CommandNode commandNode;

    public AdminCommandNode(CommandNode commandNode) {

        super(commandNode.session);
        this.commandNode = commandNode;
    }

    public String execute(MessageObject messageObject) {
        return commandNode.execute(messageObject);
    }

    @Override
    public boolean matched(MessageObject messageObject) {

        return commandNode.matched(messageObject) &&
                messageObject.attributes.get("userId") != null &&
                    session.context.isAdmin(messageObject.attributes.get("userId").toString());
    }
}
