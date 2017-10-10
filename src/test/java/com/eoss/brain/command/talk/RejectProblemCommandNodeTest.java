package com.eoss.brain.command.talk;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.command.line.BizWakeupCommandNode;
import com.eoss.brain.net.Node;
import com.eoss.brain.net.Context;
import com.eoss.brain.net.MemoryContext;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

public class RejectProblemCommandNodeTest {

    @Test
    public void testRejectProblemCommand() {
        Context.setLocale(new Locale("th", "TH"));

        List<String> rejectKeys = Arrays.asList("ไม่", "เข้าใจละ", "ไม่", "ก็แล้วแต่");

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new MemoryContext("test");
        context.admin(adminIdList);
        Session session = new Session(context);
        session.learning = true;
        new BizWakeupCommandNode(session).execute(null);

        Node node = new Node(new String[]{"สวัสดี"}, new String[]{"ดีครับ"}, null);

        context.add(node);

        assertTrue(1 == context.dataSet.size());

        assertEquals("ดีครับ", session.parse(MessageObject.build("สวัสดี")));

        assertEquals("ดีครับ", session.parse(MessageObject.build("สวัสดีครับ")));

        session.insert(new RejectProblemCommandNode(session, session.lastEntry(), rejectKeys.get(0), rejectKeys.get(1), rejectKeys.get(2), rejectKeys.get(3)));

        assertEquals("เข้าใจละ", session.parse(MessageObject.build("ว่าไง")));

        assertFalse(session.hasProblem());

        assertEquals("หมายถึง ว่าไง รึป่าว?", session.parse(MessageObject.build("นายครับ")));

        assertEquals("ช่วยบอกให้ละเอียดหน่อย", session.parse(MessageObject.build("ไม่")));

        assertEquals("นายครับ ?", session.parse(MessageObject.build("ไม่")));

        assertEquals("เข้าใจละ", session.parse(MessageObject.build("ดีครับ")));

        assertFalse(session.hasProblem());

        assertEquals("ดีครับ", session.parse(MessageObject.build("นายครับ")));

        session.insert(new RejectProblemCommandNode(session, session.lastEntry(), rejectKeys.get(0), rejectKeys.get(1), rejectKeys.get(2), rejectKeys.get(3)));

        assertEquals("เข้าใจละ", session.parse(MessageObject.build("อาฮะ")));

        assertFalse(session.hasProblem());

    }
}