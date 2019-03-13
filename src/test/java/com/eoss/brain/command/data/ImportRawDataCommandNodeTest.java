package com.eoss.brain.command.data;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.command.wakeup.WakeupCommandNode;
import com.eoss.brain.context.FileContext;
import com.eoss.brain.context.MemoryContext;
import com.eoss.brain.net.Context;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Created by eossth on 8/29/2017 AD.
 */
public class ImportRawDataCommandNodeTest {

    @Test
    public void execute() throws Exception {


        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new FileContext("raw").admin(adminIdList);
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject,"ใส่ข้อมูลดิบ\n" +
                "Hello\n" +
                "How\n" +
                "are\n" +
                "you"
        )));

        assertEquals("How", session.parse(MessageObject.build("Hello")));
        assertEquals("are", session.parse(MessageObject.build("Next")));
        assertEquals("you", session.parse(MessageObject.build("Next")));

        System.out.println(session.parse(MessageObject.build(messageObject,"ดูข้อมูลดิบ")));

    }

}