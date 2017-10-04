package com.eoss.brain.command.talk;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.command.line.BizWakeupCommandNode;
import com.eoss.brain.net.Context;
import com.eoss.brain.net.MemoryContext;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class LowConfidenceProblemCommandNodeTest {

    @Test
    public void testInputProblemCommand() {

        List<String> lowConfidenceKeys = Arrays.asList("เข้าใจละ", "ไม่", "ไม่ก็ไม่");

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new MemoryContext("test");
        context.admin(adminIdList);
        Session session = new Session(context);
        new BizWakeupCommandNode(session).execute(null);

        LowConfidenceProblemCommandNode lowConfidenceProblemCommandNode = new LowConfidenceProblemCommandNode(session,
                MessageObject.build("สวัสดี"), lowConfidenceKeys.get(0), lowConfidenceKeys.get(1), lowConfidenceKeys.get(2));

        assertTrue(lowConfidenceProblemCommandNode.matched(MessageObject.build("ตอบ")));

        session.insert(lowConfidenceProblemCommandNode);

        assertTrue(session.hasProblem());

        session.clearProblem();

        assertFalse(session.hasProblem());

        session.insert(lowConfidenceProblemCommandNode);

        assertEquals("ไม่ก็ไม่", session.parse(MessageObject.build("ไม่")));

        assertFalse(session.hasProblem());

        session.insert(lowConfidenceProblemCommandNode);

        assertEquals("เข้าใจละ", session.parse(MessageObject.build("สวีดัส")));

        assertFalse(session.hasProblem());

        assertEquals("สวีดัส", session.parse(MessageObject.build("สวัสดี")));

    }
}