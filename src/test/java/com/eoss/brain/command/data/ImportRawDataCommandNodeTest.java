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
        Locale.setDefault(new Locale("th", "TH"));

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new FileContext("raw").admin(adminIdList);
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject,"ใส่ข้อมูลดิบ\n" +
                "มีให้แค่เท่านั้น\n" +
                "มีเพียงแค่เท่านี้\n" +
                "ไม่มีมากมาย\n" +
                "ดังใครเขามี\n" +
                "มีให้เพียงนิดน้อย\n" +
                "แต่ให้ไปเกินร้อย\n" +
                "ไม่มีเงินตรา\n" +
                "มงกุฏชฎาแม่เนื้อกลอย\n" +
                "ไม่มีรถเก๋ง\n" +
                "แอร์เย็นอย่างใครเขา\n" +
                "มีไมตี้เอ็กซ์\n" +
                "ท่อดังและคันเก่า\n" +
                "พี่ไร้หน้าตา\n" +
                "ในสังคมไฮโซ\n" +
                "และจะเจอพี่ได้\n" +
                "ก็แค่วงไฮโล")));

        assertEquals("มีเพียงแค่เท่านี้", session.parse(MessageObject.build("มีให้แค่เท่านั้น")));
        assertEquals("ไม่มีมากมาย", session.parse(MessageObject.build("Next")));
        assertEquals("ดังใครเขามี", session.parse(MessageObject.build("Next")));
        assertEquals("มีให้เพียงนิดน้อย", session.parse(MessageObject.build("Next")));
        assertEquals("แต่ให้ไปเกินร้อย", session.parse(MessageObject.build("Next")));
        assertEquals("ไม่มีเงินตรา", session.parse(MessageObject.build("Next")));
        assertEquals("มงกุฏชฎาแม่เนื้อกลอย", session.parse(MessageObject.build("Next")));
        assertEquals("ไม่มีรถเก๋ง", session.parse(MessageObject.build("Next")));
        assertEquals("แอร์เย็นอย่างใครเขา", session.parse(MessageObject.build("Next")));
        assertEquals("มีไมตี้เอ็กซ์", session.parse(MessageObject.build("Next")));
        assertEquals("ท่อดังและคันเก่า", session.parse(MessageObject.build("Next")));
        assertEquals("พี่ไร้หน้าตา", session.parse(MessageObject.build("Next")));
        assertEquals("ในสังคมไฮโซ", session.parse(MessageObject.build("Next")));
        assertEquals("และจะเจอพี่ได้", session.parse(MessageObject.build("Next")));
        assertEquals("ก็แค่วงไฮโล", session.parse(MessageObject.build("Next")));

        System.out.println(session.parse(MessageObject.build(messageObject,"ดูข้อมูลดิบ")));

    }

}