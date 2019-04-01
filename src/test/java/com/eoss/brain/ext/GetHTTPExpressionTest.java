package com.eoss.brain.ext;

import com.eoss.brain.MessageObject;
import com.eoss.brain.Session;
import com.eoss.brain.command.wakeup.WakeupCommandNode;
import com.eoss.brain.context.FileContext;
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
        Context context = new MemoryContext("qa").locale(Locale.US);
        context.admin(adminIdList);
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject,"ใส่ข้อมูลถามตอบ\n" +
                "Q:สวัสดี\n" +
                "A:ดีฮะชื่อไร?\n" +
                "Q:ฝัน\n" +
                "A:ฝันว่าอะไร?\n" +
                "Q:ดีฮะชื่อไร\n" +
                "A:ทักทาย #1!\n" +
                "Q:ทักทาย\n" +
                "A:ว่าไง #1\n" +
                "Q:ดวง\n" +
                "A:เกิดวันอะไร?\n" +
                "Q:เกิดวันอะไร ศุกร์\n" +
                "A:ดูดวง ศุกร์ `get://horoscope.mthai.com/apps/daily-horo`!\n" +
                "Q:เกิดวันอะไร เสาร์\n" +
                "A:ดูดวง เสาร์ `get://horoscope.mthai.com/apps/daily-horo`!\n" +
                "Q:ดูดวง อาทิตย์\n" +
                "A:`jsoup://%1://#content-inner h4` `jsoup://%1://#content-inner div:eq(2) p`\n" +
                "Q:ดูดวง จันทร์\n" +
                "A:`jsoup://%1://#content-inner h4` `jsoup://%1://#content-inner div:eq(3) p`\n" +
                "Q:ดูดวง อังคาร\n" +
                "A:`jsoup://%1://#content-inner h4` `jsoup://%1://#content-inner div:eq(4) p`\n" +
                "Q:ดูดวง พุธ\n" +
                "A:`jsoup://%1://#content-inner h4` `jsoup://%1://#content-inner div:eq(5) p`\n" +
                "Q:ดูดวง พฤหัส\n" +
                "A:`jsoup://%1://#content-inner h4` `jsoup://%1://#content-inner div:eq(6) p`\n" +
                "Q:ดูดวง ศุกร์\n" +
                "A:`jsoup://%1://#content-inner h4` `jsoup://%1://#content-inner div:eq(7) p`\n" +
                "Q:ดูดวง เสาร์\n" +
                "A:`jsoup://%1://#content-inner h4` `jsoup://%1://#content-inner div:eq(8) p`\n" +
                "Q:เกิดวันอะไร อาทิตย์\n" +
                "A:ดูดวง อาทิตย์  `get://horoscope.mthai.com/apps/daily-horo`!\n" +
                "Q:เกิดวันอะไร จันทร์\n" +
                "A:ดูดวง จันทร์  `get://horoscope.mthai.com/apps/daily-horo`!\n" +
                "Q:เกิดวันอะไร อังคาร\n" +
                "A:ดูดวง อังคาร  `get://horoscope.mthai.com/apps/daily-horo`!\n" +
                "Q:เกิดวันอะไร พุธ\n" +
                "A:ดูดวง พุธ  `get://horoscope.mthai.com/apps/daily-horo`!\n" +
                "Q:เกิดวันอะไร พฤหัส\n" +
                "A:ดูดวง พฤหัส  `get://horoscope.mthai.com/apps/daily-horo`!\n" +
                "Q:ไง\n" +
                "A:งาย\n" +
                "Q:lotto\n" +
                "A:เลขท้ายสองตัวคือ `jsoup://%1://p[class=red_txt]`\n" +
                "Q:ฝันว่าอะไร\n" +
                "A:lotto `get://lotto.mthai.com/dream/##.html`!"
            )));

        assertEquals("ฝันว่าอะไร", session.parse(MessageObject.build("ฝัน")));
        assertEquals("เลขท้ายสองตัวคือ 4 6 7", session.parse(MessageObject.build("ทอง")));

    }

    @Test
    public void getWithHeadersTest() {

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new MemoryContext("qa");
        context.admin(adminIdList);
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject,"ใส่ข้อมูลถามตอบ\n" +
                "Q: hello\n" +
                "A: what is your name?\n" +
                "Q: what is your name\n" +
                "A: hi `get://wayobot=best&test://wayobot.com/apiMockup/#1`!\n" +
                "Q: hi\n" +
                "A: hi %1\n"
        )));

        assertEquals("what is your name", session.parse(MessageObject.build("hello")));
        assertEquals("hi wayobot=best/ken", session.parse(MessageObject.build("ken")));

    }

    @Test
    public void getJSONObjectTest() {

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new MemoryContext("qa");
        context.admin(adminIdList);
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject,"ใส่ข้อมูลถามตอบ\n" +
                "Q: hello\n" +
                "A: hi `get://wayobot.com/apiMockup/jsonObject`!\n" +
                "Q: hi\n" +
                "A: `json-path://%1://$.hotel.name`"
        )));

        assertEquals("Tara", session.parse(MessageObject.build("hello")));

    }

    @Test
    public void getJSONArrayTest() {

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new MemoryContext("qa");
        context.admin(adminIdList);
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject,"ใส่ข้อมูลถามตอบ\n" +
                "Q: hello\n" +
                "A: hi `get://wayobot.com/apiMockup/jsonArray`!\n" +
                "Q: hi\n" +
                "A: `json-path://%1://$`"
        )));

        assertEquals("{name=Tara}", session.parse(MessageObject.build("hello")));

    }

    @Test
    public void getDOMTest() {

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new MemoryContext("qa");
        context.admin(adminIdList);
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject,"ใส่ข้อมูลถามตอบ\n" +
                "Q: hello\n" +
                "A: hi `get://wayobot.com/apiMockup/html`!\n" +
                "Q: hi\n" +
                "A: `jsoup://%1://h3[id=test]`"
        )));

        assertEquals("Test", session.parse(MessageObject.build("hello")));

    }

    @Test
    public void getWebTest() {
        //

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new MemoryContext("qa");
        context.admin(adminIdList);
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject,"ใส่ข้อมูลถามตอบ\n" +
                "Q: lotto\n" +
                "A: check `get://wayobot.com/apiMockup/html`!\n" +
                "Q: check\n" +
                "A: %1"
        )));

        assertEquals("<html><head></head><body><h3>Hello</h3><h3 id=\"test\">Test</h3></body></html>", session.parse(MessageObject.build("lotto")));
    }
}