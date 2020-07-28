package com.brainy.command;

import com.brainy.Session;
import com.brainy.MessageObject;
import com.brainy.command.wakeup.WakeupCommandNode;
import com.brainy.net.Context;
import com.brainy.context.MemoryContext;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class AdminCommandNodeTest {

    @Test
    public void testAdminCommand() {

        Context context = new MemoryContext("test");
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        CommandNode dummyCommandNode = new CommandNode(session, new String[]{"dummy"}) {
            @Override
            public String execute(MessageObject messageObject) {
                return "dummyDone";
            }
        };

        AdminCommandNode adminCommandNode = new AdminCommandNode(dummyCommandNode);

        assertTrue(adminCommandNode.session ==dummyCommandNode.session);

        MessageObject messageObject = MessageObject.build("dummy");

        assertFalse(adminCommandNode.matched(messageObject));

        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfXX");

        assertFalse(adminCommandNode.matched(messageObject));

        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertTrue(adminCommandNode.matched(messageObject));
    }
}