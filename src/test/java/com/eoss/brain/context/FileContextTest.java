package com.eoss.brain.context;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.command.wakeup.WakeupCommandNode;
import com.eoss.brain.context.FileContext;
import com.eoss.brain.net.Context;
import com.eoss.brain.net.Hook;
import com.eoss.brain.net.Node;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Created by eossth on 8/29/2017 AD.
 */
public class FileContextTest {

    @Test
    public void testSaveFileNodeDAO() throws Exception {
        Locale.setDefault(new Locale("th", "TH"));

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        String contextName = "test";
        File testFile = new File(contextName + Context.SUFFIX);
        if (testFile.exists())
            assertTrue(testFile.delete());

        Context context = new FileContext(contextName);
        context.admin(adminIdList);
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

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
    public void testLoadFileNodeDAO() throws Exception {

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        String contextName = "test";

        Context context = new FileContext(contextName);
        context.admin(adminIdList);
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        assertEquals("ครับ", session.parse(MessageObject.build("สวัสดี")));

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("mode", "เฮฮา");
        assertEquals("ครับ", session.parse(messageObject));

    }

}