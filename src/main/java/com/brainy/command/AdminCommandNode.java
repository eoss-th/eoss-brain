package com.brainy.command;

import com.brainy.MessageObject;
import com.brainy.NodeEvent;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class AdminCommandNode extends CommandNode {

    public static class AuthenticationException extends RuntimeException {
        public AuthenticationException(String msg) {
            super(msg);
        }
    }

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

        if (!commandNode.matched(messageObject)) return false;

        try {

            session.context.listener.callback(new NodeEvent(this, messageObject, NodeEvent.Event.Authentication));
            return true;

        } catch (AuthenticationException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(e);

        }

    }
}
