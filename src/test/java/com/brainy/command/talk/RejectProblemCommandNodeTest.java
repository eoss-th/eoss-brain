package com.brainy.command.talk;

import com.brainy.Session;
import com.brainy.MessageObject;
import com.brainy.command.wakeup.WakeupCommandNode;
import com.brainy.net.Context;
import com.brainy.context.MemoryContext;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class RejectProblemCommandNodeTest {

    @Test
    public void testRejectProblemCommand() {

        Context context = new MemoryContext("qa");
        Session session = new Session(context);
        session.learning = true;

        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject,"ใส่ข้อมูลถามตอบ\n" +
                "Q: hello how are you\n" +
                "A: fine\n"
        )));

        assertEquals("fine", session.parse(MessageObject.build("hello")));

        assertFalse(session.hasProblem());

        assertEquals("hello ?", session.parse(MessageObject.build("\uD83D\uDC4E")));

        assertTrue(session.hasProblem());

        //Cancel
        assertEquals("\uD83D\uDE0A", session.parse(MessageObject.build("\uD83D\uDC4E")));

        assertFalse(session.hasProblem());

        //Retry
        assertEquals("fine", session.parse(MessageObject.build("hello")));

        assertEquals("hello ?", session.parse(MessageObject.build("\uD83D\uDC4E")));

        assertEquals("\uD83D\uDE0A", session.parse(MessageObject.build("hi")));

        assertEquals("hi", session.parse(MessageObject.build("hello")));
    }
}