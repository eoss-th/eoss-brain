package com.eoss.brain.command.data;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.command.line.BizWakeupCommandNode;
import com.eoss.brain.net.MemoryContext;
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
        Context.setLocale(new Locale("th", "TH"));

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new MemoryContext("test").admin(adminIdList);
        Session session = new Session(context);
        new BizWakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject,"ใส่ข้อมูลดิบ\n" +
                "มีให้แค่เท่านี้\n" +
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

        assertEquals("มีเพียงแค่เท่านี้", session.parse(MessageObject.build("มีให้แค่เท่านี้")));
        assertEquals("ไม่มีมากมาย", session.parse(MessageObject.build("แล้ว")));
        assertEquals("ดังใครเขามี", session.parse(MessageObject.build("แล้ว")));
        assertEquals("มีให้เพียงนิดน้อย", session.parse(MessageObject.build("แล้ว")));
        assertEquals("แต่ให้ไปเกินร้อย", session.parse(MessageObject.build("แล้ว")));
        assertEquals("ไม่มีเงินตรา", session.parse(MessageObject.build("แล้ว")));
        assertEquals("มงกุฏชฎาแม่เนื้อกลอย", session.parse(MessageObject.build("แล้ว")));
        assertEquals("ไม่มีรถเก๋ง", session.parse(MessageObject.build("แล้ว")));
        assertEquals("แอร์เย็นอย่างใครเขา", session.parse(MessageObject.build("แล้ว")));
        assertEquals("มีไมตี้เอ็กซ์", session.parse(MessageObject.build("แล้ว")));
        assertEquals("ท่อดังและคันเก่า", session.parse(MessageObject.build("แล้ว")));
        assertEquals("พี่ไร้หน้าตา", session.parse(MessageObject.build("แล้ว")));
        assertEquals("ในสังคมไฮโซ", session.parse(MessageObject.build("แล้ว")));
        assertEquals("และจะเจอพี่ได้", session.parse(MessageObject.build("แล้ว")));
        assertEquals("ก็แค่วงไฮโล", session.parse(MessageObject.build("แล้ว")));

    }

}