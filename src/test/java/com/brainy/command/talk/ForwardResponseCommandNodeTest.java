package com.brainy.command.talk;

import com.brainy.MessageObject;
import com.brainy.Session;
import com.brainy.command.wakeup.WakeupCommandNode;
import com.brainy.context.MemoryContext;
import com.brainy.net.Context;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ForwardResponseCommandNodeTest {

    @Test
    public void execute() {

        Context context = new MemoryContext("qa");
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject,"ใส่ข้อมูลถามตอบ\n" +
                "Q: hello\n" +
                "A: hi #1 muay!\n" +
                "Q: hi ken\n" +
                "A: great #1 lee!\n" +
                "Q: great muay\n" +
                "A: #1 is great\n" +
                "Q: whatsup\n" +
                "A: wow, hi #1 muay!\n"
        )));

        assertEquals("lee is great", session.parse(MessageObject.build("hello ken")));
        assertEquals("wow, lee is great", session.parse(MessageObject.build("whatsup ken")));

    }

    @Test
    public void recursiveTest() {

        Context context = new MemoryContext("qa");
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject,"ใส่ข้อมูลถามตอบ\n" +
                "Q: hello\n" +
                "A: hi!\n" +
                "Q: hi\n" +
                "A: haha!\n" +
                "Q: haha\n" +
                "A: hello!\n"
        )));

        assertEquals("hello!", session.parse(MessageObject.build("hello")));
    }

    @Test
    public void cyclicTest() {

        Context context = new MemoryContext("qa");
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject,"ใส่ข้อมูลถามตอบ\n" +
                "Q: hello test\n" +
                "A: test!\n" +
                "Q: hi test\n" +
                "A: haha\n"
        )));

        assertEquals("haha", session.parse(MessageObject.build("hello")));
    }


}