package com.eoss.brain.command.talk;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.command.wakeup.WakeupCommandNode;
import com.eoss.brain.net.Context;
import com.eoss.brain.context.MemoryContext;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

public class LowConfidenceProblemCommandNodeTest {

    @Test
    public void testInputProblemCommand() {

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new MemoryContext("qa");
        context.admin(adminIdList);
        Session session = new Session(context);
        session.learning = true;

        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject,"ใส่ข้อมูลถามตอบ\n" +
                "Q: hello how are you\n" +
                "A: fine\n"
        )));

        assertEquals("hi ?", session.parse(MessageObject.build("hi")));

        assertTrue(session.hasProblem());

        //Cancel
        assertEquals("\uD83D\uDE0A", session.parse(MessageObject.build("ไม่")));

        assertFalse(session.hasProblem());

        //Retry
        assertEquals("hi ?", session.parse(MessageObject.build("hi")));

        assertTrue(session.hasProblem());

        assertEquals("\uD83D\uDE0A", session.parse(MessageObject.build("hello")));

        assertFalse(session.hasProblem());

        assertEquals("hello", session.parse(MessageObject.build("hi")));

    }
}