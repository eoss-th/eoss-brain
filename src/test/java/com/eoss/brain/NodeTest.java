package com.eoss.brain;

import com.eoss.brain.net.Node;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class NodeTest {

    @Test
    public void testClone() {
        Node a = new Node(new String[]{"ดี"}, new String[]{"ครับ"}, Node.Mode.MatchWhole);
        Node b = a.copy();
        Node c = b.copy();

        c.feed(MessageObject.build("ดี"));

        assertTrue(a.maxActiveResponse==null);
        assertTrue(b.maxActiveResponse==null);
        assertEquals(c.maxActiveResponse.active, Node.Mode.MatchWhole.initWeight, 0.001);

        b.feed(MessageObject.build("ดี"));

        assertTrue(a.maxActiveResponse==null);
        assertEquals(b.maxActiveResponse.active, Node.Mode.MatchWhole.initWeight, 0.001);
        assertEquals(c.maxActiveResponse.active, Node.Mode.MatchWhole.initWeight, 0.001);

        a.feed(MessageObject.build("ดี"));

        assertEquals(a.maxActiveResponse.active, Node.Mode.MatchWhole.initWeight, 0.001);
        assertEquals(b.maxActiveResponse.active, Node.Mode.MatchWhole.initWeight, 0.001);
        assertEquals(c.maxActiveResponse.active, Node.Mode.MatchWhole.initWeight, 0.001);

        a.feed(MessageObject.build("ดี"));

        assertEquals(a.maxActiveResponse.active, Node.Mode.MatchWhole.initWeight * 2, 0.001);
        assertEquals(b.maxActiveResponse.active, Node.Mode.MatchWhole.initWeight, 0.001);
        assertEquals(c.maxActiveResponse.active, Node.Mode.MatchWhole.initWeight, 0.001);

        b.feed(MessageObject.build("ดี"));

        assertEquals(a.maxActiveResponse.active, Node.Mode.MatchWhole.initWeight * 2, 0.001);
        assertEquals(b.maxActiveResponse.active, Node.Mode.MatchWhole.initWeight * 2, 0.001);
        assertEquals(c.maxActiveResponse.active, Node.Mode.MatchWhole.initWeight, 0.001);

        c.feed(MessageObject.build("ดี"));

        assertEquals(a.maxActiveResponse.active, Node.Mode.MatchWhole.initWeight * 2, 0.001);
        assertEquals(b.maxActiveResponse.active, Node.Mode.MatchWhole.initWeight * 2, 0.001);
        assertEquals(c.maxActiveResponse.active, Node.Mode.MatchWhole.initWeight * 2, 0.001);

        Set<Node> nodeSet = new HashSet<>();
        assertTrue(nodeSet.add(a));
        assertFalse(nodeSet.add(b));
        assertTrue(nodeSet.remove(b));
        assertTrue(nodeSet.add(b));

        b.feed(MessageObject.build("ดี"));
        assertTrue(new ArrayList<>(nodeSet).get(0).maxActiveResponse.active==b.maxActiveResponse.active);

        assertEquals(a.maxActiveResponse.active, Node.Mode.MatchWhole.initWeight * 2, 0.001);
        assertEquals(c.maxActiveResponse.active, Node.Mode.MatchWhole.initWeight * 2, 0.001);
    }

    @Test
    public void testMatchWhole() {

        Node node = new Node(new String[]{"สวัสดีครับ"}, Node.Mode.MatchWhole);

        assertTrue(node.matched(MessageObject.build("สวัสดีครับ")));

        assertFalse(node.matched(MessageObject.build("สวัสดีครับนาย")));

        assertFalse(node.matched(MessageObject.build("นายสวัสดีครับ")));

        assertFalse(node.matched(MessageObject.build("สวัสดี")));
    }

    @Test
    public void testMatchBody() {

        Node node = new Node(new String[]{"สวัสดีครับ"}, Node.Mode.MatchBody);

        assertTrue(node.matched(MessageObject.build("สวัสดีครับ")));

        assertTrue(node.matched(MessageObject.build("สวัสดีครับนาย")));

        assertTrue(node.matched(MessageObject.build("นายสวัสดีครับ")));

        assertTrue(node.matched(MessageObject.build("นายสวัสดีครับนาย")));

        assertFalse(node.matched(MessageObject.build("สวัสดี")));
    }

    @Test
    public void testMatchHead() {

        Node node = new Node(new String[]{"สวัสดีครับ"}, Node.Mode.MatchHead);

        assertTrue(node.matched(MessageObject.build("สวัสดีครับ")));

        assertTrue(node.matched(MessageObject.build("สวัสดีครับนาย")));

        assertFalse(node.matched(MessageObject.build("นายสวัสดีครับ")));

        assertFalse(node.matched(MessageObject.build("นายสวัสดีครับนาย")));

        assertFalse(node.matched(MessageObject.build("สวัสดี")));
    }

    @Test
    public void testMatchTail() {

        Node node = new Node(new String[]{"สวัสดีครับ"}, Node.Mode.MatchTail);

        assertTrue(node.matched(MessageObject.build("สวัสดีครับ")));

        assertTrue(node.matched(MessageObject.build("นายสวัสดีครับ")));

        assertFalse(node.matched(MessageObject.build("สวัสดีครับนาย")));

        assertFalse(node.matched(MessageObject.build("นายสวัสดีครับนาย")));

        assertFalse(node.matched(MessageObject.build("สวัสดี")));
    }

    @Test
    public void testMatchMode() {

        Node node = new Node(new String[]{"สวัสดีครับ"}, Node.Mode.MatchMode);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("mode", "สวัสดีครับ");

        assertTrue(node.matched(messageObject));

        messageObject.attributes.put("mode", "นายสวัสดีครับ");

        assertFalse(node.matched(messageObject));

        messageObject.attributes.put("mode", "สวัสดีครับนาย");

        assertFalse(node.matched(messageObject));

        messageObject.attributes.put("mode", "สวัสดี");

        assertFalse(node.matched(messageObject));
    }

    @Test
    public void testMatchNull() {

        Node node = new Node(new String[]{"กิน", "ข้าว", "ที่", "ไหน", "ดี"}, null);

        assertTrue(node.matched(MessageObject.build("กินไหนข้าวที่ดี")));

        assertTrue(node.matched(MessageObject.build("กินที่ข้าวไหนดี")));

        assertTrue(node.matched(MessageObject.build("กินข้าวไหนที่ดี")));

        assertTrue(node.matched(MessageObject.build("กินข้าวที่ไหนดี")));

        assertTrue(node.matched(MessageObject.build("ข้าวที่ไหนดี")));

        assertTrue(node.matched(MessageObject.build("กินข้าวที่ไหน")));

        assertTrue(node.matched(MessageObject.build("กินดี")));

        assertFalse(node.matched(MessageObject.build("ดีกิน")));
    }
/*
    @Test
    public void testFeedNull() {
        Node node = new Node("กิน ข้าว ที่ ไหน ดี", null);

        assertTrue(0==node.totalActiveHook());

        node.feed(MessageObject.build("กิน"));
        assertTrue(1==node.totalActiveHook());

        node.clearResponse();
        assertTrue(0==node.totalActiveHook());

        node.clearResponse();
        node.feed(MessageObject.build("กินข้าว"));
        assertTrue(2==node.totalActiveHook());

        node.clearResponse();
        node.feed(MessageObject.build("กินข้าวที่"));
        assertTrue(3==node.totalActiveHook());

        node.clearResponse();
        node.feed(MessageObject.build("กินข้าวที่ไหน"));
        assertTrue(4==node.totalActiveHook());

        node.clearResponse();
        node.feed(MessageObject.build("กินข้าวที่ไหนดี"));
        assertTrue(5==node.totalActiveHook());

        node.clearResponse();
        node.feed(MessageObject.build("กินที่ข้าวไหนดี"));
        assertTrue(5==node.totalActiveHook());

        node.clearResponse();
        node.feed(MessageObject.build("กินไหนที่ข้าวดี"));
        assertTrue(5==node.totalActiveHook());

        node.clearResponse();
        node.feed(MessageObject.build("กินที่ไหนข้าวดี"));
        assertTrue(5==node.totalActiveHook());

        node.clearResponse();
        node.feed(MessageObject.build("ดีที่ไหนข้าวกิน"));
        assertTrue(3==node.totalActiveHook());

        node.clearResponse();
        node.feed(MessageObject.build("กินไหนข้าว"));
        assertTrue(3==node.totalActiveHook());

    }
*/
    @Test
    public void testResponseNull() {
        Node node = new Node(new String[]{"กิน", "ข้าว", "ที่", "ไหน", "ดี"}, new String[]{"หึหึ"}, null);

        double delta = 0.001;
        assertTrue(null==node.maxActiveResponse);

        MessageObject messageObject;

        messageObject = MessageObject.build("กิน");
        node.feed(messageObject);
        assertEquals(Node.Mode.MatchHead.initWeight / (node.hookMap.size() + messageObject.wordCount() - 1), node.maxActiveResponse.active, delta);

        node.clear();
        messageObject = MessageObject.build("กินข้าว");
        node.feed(messageObject);
        assertEquals((Node.Mode.MatchHead.initWeight + Node.Mode.MatchBody.initWeight) / (node.hookMap.size() + messageObject.wordCount() - 2), node.maxActiveResponse.active, delta);

        node.clear();
        messageObject = MessageObject.build("กินข้าวที่");
        node.feed(messageObject);
        assertEquals((Node.Mode.MatchHead.initWeight + Node.Mode.MatchBody.initWeight + Node.Mode.MatchBody.initWeight) / (node.hookMap.size() + messageObject.wordCount() - 3), node.maxActiveResponse.active, delta);

        node.clear();
        messageObject = MessageObject.build("กินข้าวที่ไหน");
        node.feed(messageObject);
        assertEquals((Node.Mode.MatchHead.initWeight + Node.Mode.MatchBody.initWeight + Node.Mode.MatchBody.initWeight + Node.Mode.MatchBody.initWeight) / (node.hookMap.size() + messageObject.wordCount() - 4), node.maxActiveResponse.active, delta);

        node.clear();
        messageObject = MessageObject.build("กินข้าวที่ไหนดี");
        node.feed(messageObject);
        assertEquals((Node.Mode.MatchHead.initWeight + Node.Mode.MatchBody.initWeight + Node.Mode.MatchBody.initWeight + Node.Mode.MatchBody.initWeight + Node.Mode.MatchTail.initWeight) / (node.hookMap.size() + messageObject.wordCount() - 5), node.maxActiveResponse.active, delta);

    }

    @Test
    public void testResponseMode() {
        Node node = new Node(new String[]{"กิน", "ข้าว", "ที่", "ไหน", "ดี"}, new String[]{"หึหึ"}, null);
        node.addHook("เฮฮา", Node.Mode.MatchMode);

        double delta = 0.001;

        MessageObject messageObject = MessageObject.build("");
        node.feed(messageObject);
        assertTrue(0 == node.maxActiveResponse.active);

        messageObject.attributes.put("mode", "เฮฮา");

        node.clear();
        messageObject = MessageObject.build(messageObject, "");
        node.feed(messageObject);
        assertEquals(Node.Mode.MatchMode.initWeight / (node.hookMap.size() + messageObject.wordCount() - 1), node.maxActiveResponse.active, delta);

        node.clear();
        messageObject = MessageObject.build(messageObject, "กินดี");
        node.feed(messageObject);
        assertEquals((Node.Mode.MatchMode.initWeight + Node.Mode.MatchHead.initWeight + Node.Mode.MatchTail.initWeight) / (node.hookMap.size() + messageObject.wordCount() - 3), node.maxActiveResponse.active, delta);
    }

}