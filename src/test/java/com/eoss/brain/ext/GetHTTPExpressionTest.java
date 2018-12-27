package com.eoss.brain.ext;

import com.eoss.brain.MessageObject;
import com.eoss.brain.Session;
import com.eoss.brain.command.wakeup.WakeupCommandNode;
import com.eoss.brain.context.MemoryContext;
import com.eoss.brain.net.Context;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

public class GetHTTPExpressionTest {

    @Test
    public void getTest() {

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new MemoryContext("qa").locale(new Locale("th"));
        context.admin(adminIdList);
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject,"ใส่ข้อมูลถามตอบ\n" +
                "Q:ฝัน\n" +
                "A:ฝันว่าอะไร?\n" +
                "Q:ทำนายฝัน\n" +
                "A:เลขท้ายสองตัวคือ `jsoup://##://p[class=red_txt]`\n" +
                "Q:ฝันว่าอะไร\n" +
                "A:ทำนายฝัน `get://lotto.mthai.com/dream/##.html`!\n" +
                "Q:ดวง\n" +
                "A:เกิดวันอะไร?\n" +
                "Q:เกิดวันอะไร เสาร์\n" +
                "A:ดูดวง เสาร์ `get://horoscope.mthai.com/apps/daily-horo`!\n" +
                "Q:เกิดวันอะไร ศุกร์\n" +
                "A:ดูดวง ศุกร์ `get://horoscope.mthai.com/apps/daily-horo`!\n" +
                "Q:ดูดวง เสาร์\n" +
                "A:`jsoup://##://#content-inner h4` `jsoup://##://#content-inner div:eq(8) p`\n" +
                "Q:ดูดวง ศุกร์\n" +
                "A:`jsoup://##://#content-inner h4` `jsoup://##://#content-inner div:eq(7) p`\n"
        )));

        assertEquals("ฝันว่าอะไร", session.parse(MessageObject.build("ฝัน")));
        assertEquals("เลขท้ายสองตัวคือ 6 7", session.parse(MessageObject.build("ขี้")));
        assertEquals("เกิดวันอะไร", session.parse(MessageObject.build("ดวง")));
        assertEquals("ดูดวงชะตารายวัน ประจำวันพฤหัสบดีที่ 27 ธันวาคม 2561 ดูดวงรายวัน ผู้ที่เกิดวันเสาร์ วันนี้สะเทือนจิตใจมากด้าน แต่ละอย่างยังคงคั่งค้างคาใจยากแก้ไขไหว เรื่องเก่าปัญหาใหม่ก็ไหลเข้ามาหาปวดขมองทั้งวัน ลาภผลเงินทองก็คล่องทั้งรับและจ่าย ยังคงวุ่นวายจิตใจเรื่องของความรักเสมอนั่น", session.parse(MessageObject.build("เสาร์")));
        assertEquals("เกิดวันอะไร", session.parse(MessageObject.build("ดวง")));
        assertEquals("ดูดวงชะตารายวัน ประจำวันพฤหัสบดีที่ 27 ธันวาคม 2561 ดูดวงรายวัน ผู้ที่เกิดวันศุกร์ วันนี้เจอแต่เรื่องยุ่งยากลำบากใจตน เงินทองลาภผลแต่ละจำนวนมีมาสมใจแต่มักเป็นแบบทุกขลาภ เดินทางไปหน้าห่วงหลัง หลายอย่างยังคงสับสนและซ่อนเงื่อน เพื่อนแอบตีท้ายครัวหรือยุแยงให้เรือรั่วเร็วขึ้นน่ะ", session.parse(MessageObject.build("ศุกร์")));

    }

    @Test
    public void getWithHeadersTest() {

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new MemoryContext("qa").locale(new Locale("th"));
        context.admin(adminIdList);
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject,"ใส่ข้อมูลถามตอบ\n" +
                "Q: hello\n" +
                "A: what is your name?\n" +
                "Q: what is your name\n" +
                "A: hi `get://wayobot=best&test://wayobot.com/api/#1`\n"
        )));

        assertEquals("what is your name", session.parse(MessageObject.build("hello")));
        assertEquals("hi wayobot=best/ken", session.parse(MessageObject.build("ken")));

    }

    @Test
    public void getJSONObjectTest() {

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new MemoryContext("qa").locale(new Locale("th"));
        context.admin(adminIdList);
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject,"ใส่ข้อมูลถามตอบ\n" +
                "Q: hello\n" +
                "A: hi `get://wayobot.com/api/jsonObject`!\n" +
                "Q: hi\n" +
                "A: `json-path://#1://$.hotel.name`"
        )));

        assertEquals("Tara", session.parse(MessageObject.build("hello")));

    }

    @Test
    public void getJSONArrayTest() {

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new MemoryContext("qa").locale(new Locale("th"));
        context.admin(adminIdList);
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject,"ใส่ข้อมูลถามตอบ\n" +
                "Q: hello\n" +
                "A: hi `get://wayobot.com/api/jsonArray`!\n" +
                "Q: hi\n" +
                "A: `json-path://#1://$`"
        )));

        assertEquals("{name=Tara}", session.parse(MessageObject.build("hello")));

    }

    @Test
    public void getDOMTest() {

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new MemoryContext("qa").locale(new Locale("th"));
        context.admin(adminIdList);
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject,"ใส่ข้อมูลถามตอบ\n" +
                "Q: hello\n" +
                "A: hi `get://wayobot.com/api/html`!\n" +
                "Q: hi\n" +
                "A: `jsoup://#1://h3[id=test]`"
        )));

        assertEquals("Test", session.parse(MessageObject.build("hello")));

    }
}