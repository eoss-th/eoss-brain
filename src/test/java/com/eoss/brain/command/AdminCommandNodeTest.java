package com.eoss.brain.command;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.command.line.BizWakeupCommandNode;
import com.eoss.brain.net.Context;
import com.eoss.brain.net.MemoryContext;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class AdminCommandNodeTest {

    @Test
    public void testAdminCommand() {

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new MemoryContext("test");
        context.admin(adminIdList);
        Session session = new Session(context);
        new BizWakeupCommandNode(session).execute(null);

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