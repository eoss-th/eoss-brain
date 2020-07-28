package com.brainy.command;

import com.brainy.MessageObject;
import com.brainy.Session;
import com.brainy.command.talk.menu.MenuTalkCommandNode;
import com.brainy.command.wakeup.WakeupCommandNode;

import java.util.Optional;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class GreetingCommandNode extends CommandNode {

    public GreetingCommandNode(Session session, String [] hooks) {
        super(session, hooks);
    }

    @Override
    public String execute(MessageObject messageObject) {

        String greetingConfig = session.context.properties.get("greeting");

        if (greetingConfig==null) {
            return "";
        } else if (!greetingConfig.endsWith("!")) {
            return greetingConfig;
        }

        messageObject.setText(greetingConfig);

        return messageObject.headIncluded() + new MenuTalkCommandNode(session, WakeupCommandNode.KEY).execute(messageObject.forward());
    }
}
