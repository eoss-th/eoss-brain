package com.eoss.brain;

import com.eoss.brain.command.line.BizWakeupCommandNode;
import com.eoss.brain.command.line.WakeupCommandNode;
import com.eoss.brain.net.MemoryContext;
import com.eoss.brain.net.Context;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

public class SessionTest {

    @Test
    public void tesGroupEntry() {
        Locale.setDefault(new Locale("th", "TH"));

        Context context = new MemoryContext("test");
        Session session = new Session(context);
        session.learning = true;
        new WakeupCommandNode(session).execute(null);

        assertEquals("บัตรประชาชน กี่หลัก คือ?", session.parse(MessageObject.build("บัตรประชาชน กี่หลัก")));
        assertEquals("เข้าใจละ", session.parse(MessageObject.build("10")));
        assertEquals("10 ?", session.parse(MessageObject.build("บัตรประชาชน คือ")));

        assertEquals("บัตรประชาชน คือ ?", session.parse(MessageObject.build("ไม่")));
        assertEquals("เข้าใจละ", session.parse(MessageObject.build("9")));

        assertEquals("10 ?", session.parse(MessageObject.build("บัตรทดสอบมีกี่หลัก")));
}

    @Test
    public void testRelatedEntry() {
        Locale.setDefault(new Locale("th", "TH"));

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
        assertEquals("ทักทาย ว่าไง ?", session.parse(MessageObject.build("ไม่")));
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