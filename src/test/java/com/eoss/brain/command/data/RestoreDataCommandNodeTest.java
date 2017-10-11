package com.eoss.brain.command.data;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.command.line.BizWakeupCommandNode;
import com.eoss.brain.net.GAEStorageContext;
import com.eoss.brain.net.Context;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Created by eossth on 8/29/2017 AD.
 */
public class RestoreDataCommandNodeTest {
    @Test
    public void execute() throws Exception {
        Locale.setDefault(new Locale("th", "TH"));

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new GAEStorageContext("test", null);
        context.admin(adminIdList);
        Session session = new Session(context);
        session.learning = true;
        new BizWakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject, "กู้ข้อมูล")));

        assertEquals("หมายถึง ครับ รึป่าว?", session.parse(MessageObject.build("สวัสดี")));

        messageObject = MessageObject.build();
        messageObject.attributes.put("mode", "เฮฮา");
        assertEquals("หมายถึง ครับ รึป่าว?", session.parse(messageObject));
    }

}