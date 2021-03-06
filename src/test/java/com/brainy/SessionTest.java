package com.brainy;

import com.brainy.command.wakeup.WakeupCommandNode;
import com.brainy.context.MemoryContext;
import com.brainy.net.Context;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class SessionTest {

    @Test
    public void multiSessionTest() {

        Context context = new MemoryContext("qa");
        Session session1 = new Session(context);
        Session session2 = new Session(context);
        new WakeupCommandNode(session1).execute(null);
        new WakeupCommandNode(session2).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session1.parse(MessageObject.build(messageObject,"ใส่ข้อมูลถามตอบ\n" +
                "Q: hello guy\n" +
                "A: hi\n" +
                "Q: hello guy\n" +
                "A: how\n" +
                "Q: hello\n" +
                "A: hola\n"
        )));

        Set<String> results = new HashSet<>(Arrays.asList("hi", "how"));

        assertEquals("hola", session1.parse(MessageObject.build("hello")));
        assertEquals("hola", session2.parse(MessageObject.build("hello")));

        String response1 = session1.parse(MessageObject.build("hello guy"));
        String response2 = session2.parse(MessageObject.build("hello guy"));

        System.out.println(response1);

        assertTrue(results.contains(response1));
        assertTrue(results.contains(response2));

    }

}