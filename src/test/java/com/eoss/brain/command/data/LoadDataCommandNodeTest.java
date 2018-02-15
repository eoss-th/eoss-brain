package com.eoss.brain.command.data;

import com.eoss.brain.MessageObject;
import com.eoss.brain.Session;
import com.eoss.brain.command.wakeup.WakeupCommandNode;
import com.eoss.brain.net.Context;
import com.eoss.brain.context.GAEStorageContext;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Created by eossth on 8/29/2017 AD.
 */
public class LoadDataCommandNodeTest {
    @Test
    public void execute() throws Exception {
        Locale.setDefault(new Locale("th", "TH"));

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new GAEStorageContext("test", null);
        context.admin(adminIdList);
        Session session = new Session(context);

        assertEquals(0, context.nodeList.size());
        new WakeupCommandNode(session).execute(null);
        assertTrue(context.nodeList.size() > 0);

        assertEquals("ทีเอ็มบี ทัช รองรับระบบปฏิบัติการ iOS Version 7.0 ขึ้นไป และ Android 4.0 ขึ้นไปจ้ะ", session.parse(MessageObject.build("รองรับระบบปฏิบัติการ")));

    }

}