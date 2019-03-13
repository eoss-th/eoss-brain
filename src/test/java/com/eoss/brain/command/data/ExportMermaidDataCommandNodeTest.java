package com.eoss.brain.command.data;

import com.eoss.brain.MessageObject;
import com.eoss.brain.Session;
import com.eoss.brain.command.wakeup.WakeupCommandNode;
import com.eoss.brain.context.FileContext;
import com.eoss.brain.net.Context;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

public class ExportMermaidDataCommandNodeTest {

    public void test() {
        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new FileContext("hot");
        context.admin(adminIdList);
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        //assertEquals("", session.parse(MessageObject.build(messageObject, "ดูข้อมูลกราฟ")));

    }

}