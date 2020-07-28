package com.brainy.context;

import com.brainy.Session;
import com.brainy.MessageObject;
import com.brainy.command.wakeup.WakeupCommandNode;
import com.brainy.net.Context;
import com.brainy.net.Hook;
import com.brainy.net.Node;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by eossth on 8/29/2017 AD.
 */
public class FileContextTest {

    @Test
    public void testSaveFileNodeDAO() throws Exception {


        String contextName = "filetest";
        File testFile = new File(contextName + Context.SUFFIX);
        if (testFile.exists())
            assertTrue(testFile.delete());

        Context context = new FileContext(contextName);
        Session session = new Session(context);

        new WakeupCommandNode(session).execute(null);

        Node node = Node.build(new String[]{"สวัสดี", "สบาย", "ดี", "ไหม"});
        node.setResponse("ครับ");

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

        String contextName = "filetest";

        Context context = new FileContext(contextName);
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        assertEquals("ครับ", session.parse(MessageObject.build("ดี")));
    }

}