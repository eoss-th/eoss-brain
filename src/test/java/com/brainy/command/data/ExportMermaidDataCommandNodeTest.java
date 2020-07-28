package com.brainy.command.data;

import com.brainy.MessageObject;
import com.brainy.Session;
import com.brainy.command.wakeup.WakeupCommandNode;
import com.brainy.context.FileContext;
import com.brainy.net.Context;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ExportMermaidDataCommandNodeTest {

    public void test() {
        Context context = new FileContext("hot");
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        //assertEquals("", session.parse(MessageObject.build(messageObject, "ดูข้อมูลกราฟ")));

    }

}