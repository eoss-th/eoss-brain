package com.brainy.context;

import org.junit.Test;

import static org.junit.Assert.*;

public class GAEStorageContextTest {

/*
    @Test
    public void testSaveWebNodeDAO() throws Exception {


        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        String contextName = "test";

        Context context = new GAEStorageContext(contextName, null);
        context.admin(adminIdList);
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);
        context.clear();

        Node node = new Node(Hook.build(new String[]{"สวัสดี", "สบาย", "ดี", "ไหม"}), "ครับ");
        node.addHook("เฮฮา", Hook.Match.Mode);

        context.add(node);
        context.save();

        context.clear();
        context.load();
        assertEquals("ครับ", session.parse(MessageObject.build("สวัสดี")));

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("mode", "เฮฮา");
        assertEquals("ครับ", session.parse(messageObject));

    }

    @Test
    public void testLoadWebNodeDAO() throws Exception {

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        String contextName = "test";

        Context context = new GAEStorageContext(contextName, null);
        context.admin(adminIdList);
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        assertEquals("ครับ", session.parse(MessageObject.build("สวัสดี")));

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("mode", "เฮฮา");
        assertEquals("ครับ", session.parse(messageObject));

    }
*/
}