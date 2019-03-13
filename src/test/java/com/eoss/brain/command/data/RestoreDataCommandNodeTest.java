package com.eoss.brain.command.data;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.command.wakeup.WakeupCommandNode;
import com.eoss.brain.context.FileContext;
import com.eoss.brain.context.GAEStorageContext;
import com.eoss.brain.net.Context;
import org.junit.Test;

import java.io.File;
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


        File backupFile = new File("backuptest.context");
        if (backupFile.exists()) {
            System.out.println("Found " + backupFile);
            if (backupFile.delete()) {
                System.out.println(backupFile + " is deleted");
            }
        }

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new FileContext("backuptest");
        context.admin(adminIdList);
        Session session = new Session(context);

        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject, "กู้ข้อมูล")));

        assertEquals("meeting.doc", session.parse(MessageObject.build("เอกสาร")));

    }

}