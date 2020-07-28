package com.brainy.ext;

import com.brainy.MessageObject;
import com.brainy.Session;
import com.brainy.command.wakeup.WakeupCommandNode;
import com.brainy.context.MemoryContext;
import com.brainy.net.Context;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class RegularExpressionTest {
    @Test
    public void execute() {

        Context context = new MemoryContext("qa");
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject,"ใส่ข้อมูลถามตอบ\n" +
                "Q: hello\n" +
                "A: hi ##!\n" +
                "Q: hi\n" +
                "A: hi `regx://##://\\{.*?\\}`\n"
        )));

        assertEquals("hi {ken}", session.parse(MessageObject.build("hello wisarut {ken} srisawet")));

    }

}