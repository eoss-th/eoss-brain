package com.brainy.command.data;

import com.brainy.Session;
import com.brainy.MessageObject;
import com.brainy.command.wakeup.WakeupCommandNode;
import com.brainy.context.FileContext;
import com.brainy.net.Context;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by eossth on 8/29/2017 AD.
 */
public class ImportRawDataCommandNodeTest {

    @Test
    public void execute() throws Exception {

        Context context = new FileContext("raw");
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