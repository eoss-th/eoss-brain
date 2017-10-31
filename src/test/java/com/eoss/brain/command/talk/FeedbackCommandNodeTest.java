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

public class FeedbackCommandNodeTest {

    @Test
    public void testFeedbackCommand() {
        Locale.setDefault(new Locale("th", "TH"));

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new MemoryContext("test").admin(adminIdList);

        Session session = new Session(context);
        session.learning = true;
        new BizWakeupCommandNode(session).execute(null);

        Node node = new Node(new String[]{"สวัสดี"}, new String[]{"ดีครับ"}, null);

        context.add(node);

        float positiveFeedback = 0.01f;
        float negativeFeedback = -0.01f;

        /*
        node.responseSet.stream().findFirst().ifPresent((response)->{
            response.hookWeight.values().stream().findFirst().ifPresent((weight)->{
                assertEquals(Node.Mode.MatchHead.initWeight, weight, 0.001f);
            });
        });
        */

        for (Node.Response response:node.responseSet) {
            for (Float weight:response.weightList) {
                assertEquals(Node.Mode.MatchHead.initWeight, weight, 0.001f);
                break;
            }
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
                assertEquals(Node.Mode.MatchHead.initWeight+Node.Mode.MatchHead.initWeight*positiveFeedback, weight, 0.001f);
            });
        });
        */

        for (Node.Response response:node.responseSet) {
            for (Float weight:response.weightList) {
                assertEquals(Node.Mode.MatchHead.initWeight+Node.Mode.MatchHead.initWeight*positiveFeedback, weight, 0.001f);
                break;
            }
            break;
        }

        List<String> rejectKeys = Arrays.asList("ไม่", "เข้าใจละ", "พอ", "ก็แล้วแต่");

        feedbackCommandNode = new FeedbackCommandNode(session, new String[]{"ผิด"}, "แล้วจะให้ตอบว่า?", negativeFeedback, rejectKeys);

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
                assertEquals((Node.Mode.MatchHead.initWeight+Node.Mode.MatchHead.initWeight*positiveFeedback) + (Node.Mode.MatchHead.initWeight+Node.Mode.MatchHead.initWeight*positiveFeedback)*negativeFeedback, weight, 0.001f);
            });
        });
        */

        for (Node.Response response:node.responseSet) {
            for (Float weight:response.weightList) {
                assertEquals((Node.Mode.MatchHead.initWeight+Node.Mode.MatchHead.initWeight*positiveFeedback) + (Node.Mode.MatchHead.initWeight+Node.Mode.MatchHead.initWeight*positiveFeedback)*negativeFeedback, weight, 0.001f);
                break;
            }
            break;
        }

        feedbackCommandNode = new FeedbackCommandNode(session, new String[]{"เก่ง"}, "อิอิ", positiveFeedback);

        assertFalse(feedbackCommandNode.matched(MessageObject.build("เก่ง")));

        assertEquals("หมายถึง สวัสดี รึป่าวคะ?", session.parse(MessageObject.build("สวัสดีครับ")));

        assertEquals("ดีครับ", session.parse(MessageObject.build("ใช่")));

        assertFalse(node.hookList.contains(new Node.Hook("ครับ", Node.Mode.MatchTail)));

        assertEquals("อิอิ", feedbackCommandNode.execute(MessageObject.build("ใช่")));

        assertFalse(feedbackCommandNode.matched(MessageObject.build("เก่ง")));

        assertTrue(node.hookList.contains(new Node.Hook("ครับ", Node.Mode.MatchTail)));
    }
}