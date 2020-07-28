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

public class LowConfidenceProblemCommandNodeTest {

    @Test
    public void testInputProblemCommand() {

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