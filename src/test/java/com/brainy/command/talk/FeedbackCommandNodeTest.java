package com.brainy.command.talk;

import com.brainy.Session;
import com.brainy.MessageObject;
import com.brainy.command.wakeup.WakeupCommandNode;
import com.brainy.hook.KeywordsHook;
import com.brainy.net.Hook;
import com.brainy.net.Node;
import com.brainy.net.Context;
import com.brainy.context.MemoryContext;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

public class FeedbackCommandNodeTest {

    @Test
    public void testFeedbackCommand() {

        Context context = new MemoryContext("test").locale(new Locale("th"));

        Session session = new Session(context);
        session.learning = true;
        new WakeupCommandNode(session).execute(null);

        Node node = Node.build(new String[]{"สวัสดี"});
        node.setResponse("ดีครับ");

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
            if (hook.match == Hook.Match.Head)
                assertEquals(Hook.Match.Head.initWeight, hook.weight, 0.001f);
            if (hook.match == Hook.Match.Body)
                assertEquals(Hook.Match.Body.initWeight, hook.weight, 0.001f);
            if (hook.match == Hook.Match.Tail)
                assertEquals(Hook.Match.Tail.initWeight, hook.weight, 0.001f);
        }

        FeedbackCommandNode feedbackCommandNode;

        feedbackCommandNode = new FeedbackCommandNode(session, new String[]{"เก่ง"}, "อิอิ", positiveFeedback);

        assertTrue(feedbackCommandNode.matched(MessageObject.build("เก่ง")));

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
            if (hook.match == Hook.Match.Head)
                assertEquals(Hook.Match.Head.initWeight + Hook.Match.Head.initWeight*positiveFeedback, hook.weight, 0.001f);
            if (hook.match == Hook.Match.Body)
                assertEquals(Hook.Match.Body.initWeight + Hook.Match.Body.initWeight*positiveFeedback, hook.weight, 0.001f);
            if (hook.match == Hook.Match.Tail)
                assertEquals(Hook.Match.Tail.initWeight + Hook.Match.Tail.initWeight*positiveFeedback, hook.weight, 0.001f);
        }

        //session.learning = true;

        List<String> rejectKeys = Arrays.asList("ไม่", "เข้าใจละ", "พอ", "ก็แล้วแต่");

        Key lowConfidenceKey = new Key("เข้าใจละ", "?", Arrays.asList("พอ"));

        feedbackCommandNode = new FeedbackCommandNode(session, new String[]{"ผิด"}, "แล้วจะให้ตอบว่า?", negativeFeedback, lowConfidenceKey);

        assertTrue(feedbackCommandNode.matched(MessageObject.build("ผิด")));

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

        assertTrue(feedbackCommandNode.matched(MessageObject.build("เก่ง")));

        assertEquals("ดีครับ", session.parse(MessageObject.build("สวัสดีครับ")));

        assertFalse(node.hookList().contains(new KeywordsHook("ครับ", Hook.Match.Words)));

        assertEquals("\uD83D\uDE0A", session.parse(MessageObject.build("\uD83D\uDC4D")));

        assertTrue(node.hookList().contains(new KeywordsHook("ครับ", Hook.Match.Words)));

    }
}