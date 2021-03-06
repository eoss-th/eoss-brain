package com.brainy.net;

import com.brainy.MessageObject;
import com.brainy.context.MemoryContext;
import com.brainy.context.FileContext;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class NodeTest {

    @Test
    public void testClone() {


        Node a = new Node(Hook.build(new String[]{"ดี"}, Hook.Match.All), "ครับ");
        Node b = new Node(a);
        Node c = new Node(b);

        c.feed(MessageObject.build("ดี"));

        assertEquals(c.active(), Hook.Match.All.initWeight, 0.001);

        b.feed(MessageObject.build("ดี"));

        assertEquals(b.active(), Hook.Match.All.initWeight, 0.001);
        assertEquals(c.active(), Hook.Match.All.initWeight, 0.001);

        a.feed(MessageObject.build("ดี"));

        assertEquals(a.active(), Hook.Match.All.initWeight, 0.001);
        assertEquals(b.active(), Hook.Match.All.initWeight, 0.001);
        assertEquals(c.active(), Hook.Match.All.initWeight, 0.001);

        a.feed(MessageObject.build("ดี"));

        assertEquals(a.active(), Hook.Match.All.initWeight * 1, 0.001);
        assertEquals(b.active(), Hook.Match.All.initWeight, 0.001);
        assertEquals(c.active(), Hook.Match.All.initWeight, 0.001);

        b.feed(MessageObject.build("ดี"));

        assertEquals(a.active(), Hook.Match.All.initWeight * 1, 0.001);
        assertEquals(b.active(), Hook.Match.All.initWeight * 1, 0.001);
        assertEquals(c.active(), Hook.Match.All.initWeight, 0.001);

        c.feed(MessageObject.build("ดี"));

        assertEquals(a.active(), Hook.Match.All.initWeight * 1, 0.001);
        assertEquals(b.active(), Hook.Match.All.initWeight * 1, 0.001);
        assertEquals(c.active(), Hook.Match.All.initWeight * 1, 0.001);

        Set<Node> nodeSet = new HashSet<>();
        assertTrue(nodeSet.add(a));
        assertTrue(nodeSet.contains(b));
        assertFalse(nodeSet.add(b));
        assertTrue(nodeSet.remove(b));
        assertTrue(nodeSet.add(b));

        b.feed(MessageObject.build("ดี"));
        assertTrue(new ArrayList<>(nodeSet).get(0).active()==b.active());

        assertEquals(a.active(), Hook.Match.All.initWeight * 1, 0.001);
        assertEquals(c.active(), Hook.Match.All.initWeight * 1, 0.001);
    }

    @Test
    public void testMatchWhole() {

        Node node = new Node(Hook.build(new String[]{"สวัสดีครับ"}, Hook.Match.All));

        assertTrue(node.matched(MessageObject.build("สวัสดีครับ")));

        assertFalse(node.matched(MessageObject.build("สวัสดีครับนาย")));

        assertFalse(node.matched(MessageObject.build("นายสวัสดีครับ")));

        assertFalse(node.matched(MessageObject.build("สวัสดี")));
    }

    @Test
    public void testMatchBody() {

        Node node = new Node(Hook.build(new String[]{"สวัสดีครับ"}, Hook.Match.Body));

        assertTrue(node.matched(MessageObject.build("สวัสดีครับ")));

        assertTrue(node.matched(MessageObject.build("สวัสดีครับนาย")));

        assertTrue(node.matched(MessageObject.build("นายสวัสดีครับ")));

        assertTrue(node.matched(MessageObject.build("นายสวัสดีครับนาย")));

        assertFalse(node.matched(MessageObject.build("ดี")));

        Node node1 = new Node(Hook.build(new String[]{"ถาม", "ไม่,no"}, Hook.Match.Body));
        Node node2 = new Node(Hook.build(new String[]{"basic", "ไม่"}, Hook.Match.Body));

        MessageObject msg;
        msg = MessageObject.build("ถาม ไม่");
        msg.attributes.put("wordCount", 2);

        node1.feed(msg);
        System.out.println(node1.active());

        node2.feed(msg);

        System.out.println(node2.active());
    }

    @Test
    public void testMatchHead() {

        Node node = new Node(Hook.build(new String[]{"สวัสดีครับ"}, Hook.Match.Head));

        assertTrue(node.matched(MessageObject.build("สวัสดีครับ")));

        assertTrue(node.matched(MessageObject.build("สวัสดีครับนาย")));

        assertFalse(node.matched(MessageObject.build("นายสวัสดีครับ")));

        assertFalse(node.matched(MessageObject.build("นายสวัสดีครับนาย")));

        assertFalse(node.matched(MessageObject.build("สวัสดี")));
    }

    @Test
    public void testMatchTail() {

        Node node = new Node(Hook.build(new String[]{"สวัสดีครับ"}, Hook.Match.Tail));

        assertTrue(node.matched(MessageObject.build("สวัสดีครับ")));

        assertTrue(node.matched(MessageObject.build("นายสวัสดีครับ")));

        assertFalse(node.matched(MessageObject.build("สวัสดีครับนาย")));

        assertFalse(node.matched(MessageObject.build("นายสวัสดีครับนาย")));

        assertFalse(node.matched(MessageObject.build("สวัสดี")));
    }

    @Test
    public void testMatchMode() {

        Node node = new Node(Hook.build(new String[]{"สวัสดีครับ"}, Hook.Match.Mode));

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("mode", "สวัสดีครับ");

        assertTrue(node.matched(messageObject));

        messageObject.attributes.put("mode", "นายสวัสดีครับ");

        assertFalse(node.matched(messageObject));

        messageObject.attributes.put("mode", "สวัสดีครับนาย");

        assertFalse(node.matched(messageObject));

        messageObject.attributes.put("mode", "สวัสดี");

        assertFalse(node.matched(messageObject));

        messageObject.attributes.put("mode", "สวัสดีครับ");

        assertTrue(node.matched(messageObject));

        node.addHook("เอกสาร", Hook.Match.Head);
        node.addHook("ประชุม", Hook.Match.Body);
        node.setResponse("meeting.doc");

        Context context = new FileContext("test");
        context.add(node);
        context.save();

    }

    @Test
    public void testMatchNull() {

        Context context = new MemoryContext("test");
        context.locale(new Locale("th"));

        Node node = Node.build(new String[]{"กิน", "ข้าว", "ที่", "ไหน", "ดี"});

        assertTrue(node.matched(MessageObject.build("กินไหนข้าวที่ดี").split(context)));

        assertTrue(node.matched(MessageObject.build("กินที่ข้าวไหนดี").split(context)));

        assertTrue(node.matched(MessageObject.build("กินข้าวไหนที่ดี").split(context)));

        assertTrue(node.matched(MessageObject.build("กินข้าวที่ไหนดี").split(context)));

        assertTrue(node.matched(MessageObject.build("ข้าวที่ไหนดี").split(context)));

        assertTrue(node.matched(MessageObject.build("กินข้าวที่ไหน").split(context)));

        assertTrue(node.matched(MessageObject.build("กินดี").split(context)));

        assertTrue(node.matched(MessageObject.build("ดีกิน").split(context)));
    }
    @Test
    public void testHookString(){
        String hook = "ฟฟhellobot";
        System.out.println(hook.matches("^[A-Za-z].*$"));
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

        Node node = Node.build(new String[]{"กิน", "ข้าว", "ที่", "ไหน", "ดี"});
        node.setResponse("หึหึ");

        double delta = 0.001;

        MessageObject messageObject;

        messageObject = MessageObject.build("กิน");
        messageObject.split();
        node.feed(messageObject);
        assertEquals(Hook.Match.Head.initWeight / (node.hookList().size() + 1 - 1), node.active(), delta);

        node.release();
        messageObject = MessageObject.build("กิน ข้าว");
        messageObject.split();
        node.feed(messageObject);
        assertEquals((Hook.Match.Head.initWeight + Hook.Match.Body.initWeight) / (node.hookList().size() + 2 - 2), node.active(), delta);

        node.release();
        messageObject = MessageObject.build("กิน ข้าว ที่");
        messageObject.split();
        node.feed(messageObject);
        assertEquals((Hook.Match.Head.initWeight + Hook.Match.Body.initWeight + Hook.Match.Body.initWeight) / (node.hookList().size() + 3 - 3), node.active(), delta);

        node.release();
        messageObject = MessageObject.build("กิน ข้าว ที่ ไหน");
        messageObject.split();
        node.feed(messageObject);
        assertEquals((Hook.Match.Head.initWeight + Hook.Match.Body.initWeight + Hook.Match.Body.initWeight + Hook.Match.Body.initWeight) / (node.hookList().size() + 4 - 4), node.active(), delta);

        node.release();
        messageObject = MessageObject.build("กิน ข้าว ที่ ไหน ดี");
        messageObject.split();
        node.feed(messageObject);
        assertEquals((Hook.Match.Head.initWeight + Hook.Match.Body.initWeight + Hook.Match.Body.initWeight + Hook.Match.Body.initWeight + Hook.Match.Tail.initWeight) / (node.hookList().size() + 5 - 5), node.active(), delta);

    }

    @Test
    public void testResponseMode() {
        Node node = Node.build(new String[]{"กิน", "ข้าว", "ที่", "ไหน", "ดี"});
        node.setResponse("หึหึ");
        node.addHook("เฮฮา", Hook.Match.Mode);

        double delta = 0.001;

        MessageObject messageObject = MessageObject.build("");
        node.feed(messageObject);
        assertTrue(0 == node.active());

        messageObject.attributes.put("mode", "เฮฮา");

        node.release();
        messageObject = MessageObject.build(messageObject, "");
        node.feed(messageObject);
        assertEquals(Hook.Match.Mode.initWeight / (node.hookList().size() + 1 - 1), node.active(), delta);

        node.release();
        messageObject = MessageObject.build(messageObject, "กิน ดี");
        messageObject.split();
        node.feed(messageObject);
        assertEquals((Hook.Match.Mode.initWeight + Hook.Match.Head.initWeight + Hook.Match.Tail.initWeight) / (node.hookList().size() + 2 - 3), node.active(), delta);

    }

}