package com.brainy.command.talk;

import com.brainy.Session;
import com.brainy.MessageObject;
import com.brainy.command.wakeup.WakeupCommandNode;
import com.brainy.context.MemoryContext;
import com.brainy.net.Context;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TalkCommandNodeTest {

    @Test
    public void testDataCommand() {


        List<String> lowConfidenceKeys = Arrays.asList("เข้าใจละ", "พอ", "ก็แล้วแต่", "คือ?");

        Key lowConfidenceKey = new Key("เข้าใจละ", "คือ?", Arrays.asList("พอ"));

        Context context = new MemoryContext("test");
        Session session = new Session(context);
        session.learning = true;

        TalkCommandNode talkCommandNode = new TalkCommandNode(session, lowConfidenceKey);

        assertTrue(talkCommandNode.matched(MessageObject.build("dfgsgf")));

        assertEquals("สวัสดี คือ?", talkCommandNode.execute(MessageObject.build("สวัสดี")));

        assertEquals("เข้าใจละ", session.parse(MessageObject.build("สวีดัส")));

        assertEquals("สวีดัส", talkCommandNode.execute(MessageObject.build("สวัสดี")));
    }

    @Test
    public void testMaxActiveNode() {

        Context context = new MemoryContext("qa");
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject,"ใส่ข้อมูลถามตอบ\n" +
                "Q: hello guy\n" +
                "A: hi\n" +
                "Q: hello guy\n" +
                "A: how\n" +
                "Q: hello\n" +
                "A: hola\n"
        )));

        //assertEquals("hi", session.parse(MessageObject.build("hello guy")));

    }

    @Test
    public void testMathConditionNode() {

        Context context = new MemoryContext("qa");
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject,"ใส่ข้อมูลถามตอบ\n" +
                "Q: i am years old\n" +
                "A: age #1!\n" +
                "Q: age >=50\n" +
                "A: you are so old\n" +
                "Q: age <50\n" +
                "A: you are so young\n"
        )));

        assertEquals("you are so young", session.parse(MessageObject.build("i am 20 years old")));
        assertEquals("you are so old", session.parse(MessageObject.build("i am 70 years old")));

    }


}