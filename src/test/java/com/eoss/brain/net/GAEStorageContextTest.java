package com.eoss.brain.net;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.command.line.BizWakeupCommandNode;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

public class GAEStorageContextTest {
    @Test
    public void testSaveWebNodeDAO() throws Exception {
        Locale.setDefault(new Locale("th", "TH"));

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        String contextName = "test";

        Context context = new GAEStorageContext(contextName, null);
        context.admin(adminIdList);
        Session session = new Session(context);
        new BizWakeupCommandNode(session).execute(null);
        context.clear();

        Node node = new Node(new String[]{"สวัสดี", "สบาย", "ดี", "ไหม"}, new String[]{"ครับ"}, null);
        node.addHook("เฮฮา", Node.Mode.MatchMode);

        context.add(node);
        context.save();

        context.clear();
        context.load();
        assertEquals("หมายถึง ครับ รึป่าว?", session.parse(MessageObject.build("สวัสดี")));

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("mode", "เฮฮา");
        assertEquals("หมายถึง ครับ รึป่าว?", session.parse(messageObject));

    }

    @Test
    public void testLoadWebNodeDAO() throws Exception {

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        String contextName = "test";

        Context context = new GAEStorageContext(contextName, null);
        context.admin(adminIdList);
        Session session = new Session(context);
        new BizWakeupCommandNode(session).execute(null);

        assertEquals("หมายถึง ครับ รึป่าว?", session.parse(MessageObject.build("สวัสดี")));

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("mode", "เฮฮา");
        assertEquals("หมายถึง ครับ รึป่าว?", session.parse(messageObject));

    }
}