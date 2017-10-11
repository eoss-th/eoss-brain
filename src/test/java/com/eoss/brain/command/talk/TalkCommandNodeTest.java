package com.eoss.brain.command.talk;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.net.MemoryContext;
import com.eoss.brain.net.Context;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

public class TalkCommandNodeTest {

    @Test
    public void testDataCommand() {
        Locale.setDefault(new Locale("th", "TH"));

        List<String> lowConfidenceKeys = Arrays.asList("เข้าใจละ", "พอ", "ก็แล้วแต่", "คือ?");

        Context context = new MemoryContext("test");
        Session session = new Session(context);
        session.learning = true;

        TalkCommandNode talkCommandNode = new TalkCommandNode(session, lowConfidenceKeys);

        assertTrue(talkCommandNode.matched(MessageObject.build("dfgsgf")));

        assertEquals("สวัสดี คือ?", talkCommandNode.execute(MessageObject.build("สวัสดี")));

        assertEquals("เข้าใจละ", session.parse(MessageObject.build("สวีดัส")));

        assertEquals("สวีดัส", talkCommandNode.execute(MessageObject.build("สวัสดี")));
    }

}