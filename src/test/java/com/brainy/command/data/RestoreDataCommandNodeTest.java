package com.brainy.command.data;

import com.brainy.Session;
import com.brainy.MessageObject;
import com.brainy.command.wakeup.WakeupCommandNode;
import com.brainy.context.FileContext;
import com.brainy.net.Context;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by eossth on 8/29/2017 AD.
 */
public class RestoreDataCommandNodeTest {
    @Test
    public void execute() throws Exception {


        File backupFile = new File("backuptest.context");
        if (backupFile.exists()) {
            System.out.println("Found " + backupFile);
            if (backupFile.delete()) {
                System.out.println(backupFile + " is deleted");
            }
        }

        Context context = new FileContext("backuptest");
        Session session = new Session(context);

        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject, "กู้ข้อมูล")));

        assertEquals("meeting.doc", session.parse(MessageObject.build("เอกสาร")));

    }

}