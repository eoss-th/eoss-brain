package com.eoss.brain;

import com.eoss.brain.command.line.BizWakeupCommandNode;
import com.eoss.brain.net.MemoryContext;
import com.eoss.brain.net.Context;
import org.junit.Test;

import static org.junit.Assert.*;

public class SessionTest {

    @Test
    public void testRelatedEntry() {

        Context context = new MemoryContext("test");
        Session session = new Session(context);
        session.learning = true;
        new BizWakeupCommandNode(session).execute(null);

        assertEquals("หึหึ คือ?", session.parse(MessageObject.build("หึหึ")));
        assertEquals("เข้าใจละ", session.parse(MessageObject.build("หุหุ")));

        assertEquals("ทักทาย คือ?", session.parse(MessageObject.build("ทักทาย")));
        assertEquals("เข้าใจละ", session.parse(MessageObject.build("ว่าไง")));

        assertEquals("ว่าไง คือ?", session.parse(MessageObject.build("ว่าไง")));
        assertEquals("เข้าใจละ", session.parse(MessageObject.build("ไม่ว่าไง")));

        assertEquals("ว่าไง", session.parse(MessageObject.build("ทักทาย ว่าไง")));
        assertEquals("แล้วจะให้ตอบว่า?", session.parse(MessageObject.build("ไม่")));
        assertEquals("เข้าใจละ", session.parse(MessageObject.build("สบายดี")));

        /**
         * Related Talking
         */
        assertEquals("ว่าไง", session.parse(MessageObject.build("ทักทาย")));
        assertEquals("สบายดี", session.parse(MessageObject.build("ว่าไง")));

        /**
         * Reset Stack
         */
        session.clearPool();

        assertEquals("ไม่ว่าไง", session.parse(MessageObject.build("ว่าไง")));

    }

}