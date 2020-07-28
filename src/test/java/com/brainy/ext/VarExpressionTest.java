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

public class VarExpressionTest {

    @Test
    public void varTest() {

        Context context = new MemoryContext("qa");
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject,"ใส่ข้อมูลถามตอบ\n" +
                "Q: hello\n" +
                "A: what is your name?\n" +
                "Q: what is your name?\n" +
                "A: `?name=#1&test` what is your age?\n" +
                "Q: what is your age?\n" +
                "A: `?age=#1&test` greeting!\n" +
                "Q: greeting\n" +
                "A: hi #name #age\n" +
                "Q: clear\n" +
                "A: reset... `?age=&name=`\n"
        )));

        assertEquals("what is your name", session.parse(MessageObject.build("hello")));
        assertEquals("what is your age", session.parse(MessageObject.build("ken")));
        assertEquals("hi ken 20", session.parse(MessageObject.build("20")));
        assertEquals("hi ken 20", session.parse(MessageObject.build("greeting")));
        assertEquals("reset...", session.parse(MessageObject.build("clear")));
        assertEquals("hi #name #age", session.parse(MessageObject.build("greeting")));

    }

}