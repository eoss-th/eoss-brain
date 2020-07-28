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

public class QuestionResponseCommandNodeTest {

    @Test
    public void execute() {

        Context context = new MemoryContext("qa");
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject,"ใส่ข้อมูลถามตอบ\n" +
                "Q: hello\n" +
                "A: what is your name?\n" +
                "Q: what is your name\n" +
                "A: hello ##\n" +
                "Q: hi\n" +
                "A: what is your real name?,what is your name?\n"
        )));

        assertEquals("what is your name", session.parse(MessageObject.build("hello")));
        assertEquals("hello ken", session.parse(MessageObject.build("ken")));
        assertEquals("what is your real name?", session.parse(MessageObject.build("hi")));
        assertEquals("hello wisarut srisawet", session.parse(MessageObject.build("wisarut srisawet")));

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
                "A: what is your name?\n" +
                "Q: what is your name\n" +
                "A: hello ##\n"
        )));

        assertEquals("what is your name", session.parse(MessageObject.build("hello")));
        assertEquals("hello test", session.parse(MessageObject.build("test")));
    }
}