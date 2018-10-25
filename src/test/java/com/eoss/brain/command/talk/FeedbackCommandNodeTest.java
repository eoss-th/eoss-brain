package com.eoss.brain.command.talk;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.command.wakeup.WakeupCommandNode;
import com.eoss.brain.net.Hook;
import com.eoss.brain.net.Node;
import com.eoss.brain.net.Context;
import com.eoss.brain.context.MemoryContext;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

public class FeedbackCommandNodeTest {

    @Test
    public void testFeedbackCommand() {
        Locale.setDefault(new Locale("th", "TH"));

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new MemoryContext("test").admin(adminIdList);

        Session session = new Session(context);
        session.learning = true;
        new WakeupCommandNode(session).execute(null);

        Node node = new Node(Hook.build(new String[]{"สวัสดี"}), "ดีครับ");

        context.add(node);

        float positiveFeedback = 0.01f;
        float negativeFeedback = -0.01f;

        /*
        node.responseSet.stream().findFirst().ifPresent((response)->{
            response.hookWeight.values().stream().findFirst().ifPresent((weight)->{
                assertEquals(Node.Match.Head.initWeight, weight, 0.001f);
            });
        });
        */

        for (Hook hook:node.hookList()) {
            assertEquals(Hook.Match.Head.initWeight, hook.weight, 0.001f);
            break;
        }

        FeedbackCommandNode feedbackCommandNode;

        feedbackCommandNode = new FeedbackCommandNode(session, new String[]{"เก่ง"}, "อิอิ", positiveFeedback);

        assertFalse(feedbackCommandNode.matched(MessageObject.build("เก่ง")));

        session.parse(MessageObject.build("สวัสดี"));

        assertTrue(feedbackCommandNode.matched(MessageObject.build("เก่ง")));

        assertEquals("อิอิ", feedbackCommandNode.execute(MessageObject.build("เก่ง")));

        /*
        node.responseSet.stream().findFirst().ifPresent((response)->{
            response.hookWeight.values().stream().findFirst().ifPresent((weight)->{
                assertEquals(Node.Match.Head.initWeight+Node.Match.Head.initWeight*positiveFeedback, weight, 0.001f);
            });
        });
        */

        for (Hook hook:node.hookList()) {
            assertEquals(Hook.Match.Head.initWeight+ Hook.Match.Head.initWeight*positiveFeedback, hook.weight, 0.001f);
            break;
        }

        List<String> rejectKeys = Arrays.asList("ไม่", "เข้าใจละ", "พอ", "ก็แล้วแต่");

        Key lowConfidenceKey = new Key("เข้าใจละ", "คือ?", Arrays.asList("พอ"));

        feedbackCommandNode = new FeedbackCommandNode(session, new String[]{"ผิด"}, "แล้วจะให้ตอบว่า?", negativeFeedback, lowConfidenceKey);

        assertFalse(feedbackCommandNode.matched(MessageObject.build("ผิด")));

        assertEquals("ดีครับ", session.parse(MessageObject.build("สวัสดี")));

        assertFalse(session.hasProblem());

        assertTrue(feedbackCommandNode.matched(MessageObject.build("ผิด")));

        assertEquals("สวัสดี ?", feedbackCommandNode.execute(MessageObject.build("ผิด")));

        assertTrue(session.hasProblem());

        session.clearProblem();

        /*
        node.responseSet.stream().findFirst().ifPresent((response)->{
            response.hookWeight.values().stream().findFirst().ifPresent((weight)->{
                assertEquals((Node.Match.Head.initWeight+Node.Match.Head.initWeight*positiveFeedback) + (Node.Match.Head.initWeight+Node.Match.Head.initWeight*positiveFeedback)*negativeFeedback, weight, 0.001f);
            });
        });
        */

        /*
        for (Hook hook:node.hookList()) {
            assertEquals((Hook.Match.Head.initWeight+ Hook.Match.Head.initWeight*positiveFeedback) + (Hook.Match.Head.initWeight+ Hook.Match.Head.initWeight*positiveFeedback)*negativeFeedback, hook.weight, 0.001f);
            break;
        }
        */

        feedbackCommandNode = new FeedbackCommandNode(session, new String[]{"เก่ง"}, "อิอิ", positiveFeedback);

        assertFalse(feedbackCommandNode.matched(MessageObject.build("เก่ง")));

        assertEquals("ดีครับ", session.parse(MessageObject.build("สวัสดีครับ")));

        assertFalse(node.hookList().contains(new Hook("ครับ", Hook.Match.Tail)));

        assertEquals("Thanks", session.parse(MessageObject.build("Great")));

        assertTrue(node.hookList().contains(new Hook("ครับ", Hook.Match.Tail)));

    }
}