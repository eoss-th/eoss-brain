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

public class ExpressionTest {

    @Test
    public void test() {

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new MemoryContext("qa").locale(new Locale("th"));
        context.admin(adminIdList);
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject,"ใส่ข้อมูลถามตอบ\n" +
                "Q:course tensorflow\n" +
                "A:สามารถดูรายละเอียดได้ที่ https://www.eoss-th.com/p/deep-learning.html เลยค่ะ\n" +
                "Q:course\n" +
                "A:ขอโทษนะคะตอนนี้ ## ยังไม่เปิดสอนค่ะ\n" +
                "Q:สนใจ เนื้อหา อะไร คะ ?\n" +
                "A:course ##!\n" +
                "Q:อยาก เรียน\n" +
                "A:สนใจเนื้อหาอะไรคะ?\n" +
                "Q:course react native\n" +
                "A:สามารถดูรายละเอียดได้ที่ https://www.eoss-th.com/p/react-native-training.html เลยค่ะ\n" +
                "Q:ถาม เรียน\n" +
                "A:สนใจเนื้อหาอะไรคะ?\n" +
                "Q:เรื่อง เรียน\n" +
                "A:สนใจเนื้อหาอะไรคะ?\n" +
                "Q:สอบ ถาม เรื่อง เรียน\n" +
                "A:สนใจเนื้อหาอะไรคะ?\n" +
                "Q:ฝาก ดูแล\n" +
                "A:ยินดีค่ะ\n" +
                "Q:ดี ค่ะ มิ น นี่ ยินดี รับ ใช้ สนใจ เนื้อหา อะไร คะ ?\n" +
                "A:course ##!\n" +
                "Q:เรียน\n" +
                "A:สนใจเนื้อหาอะไรคะ?\n" +
                "Q:สอน\n" +
                "A:สนใจเนื้อหาอะไรคะ?\n" +
                "Q:ไง\n" +
                "A:ดี!\n" +
                "Q:อยาก\n" +
                "A:สนใจเนื้อหาอะไรคะ?\n" +
                "Q:ดี\n" +
                "A:ดีค่ะ มินนี่ยินดีรับใช้ สนใจเนื้อหาอะไรคะ?\n" +
                "Q:ขอโทษ ค่ะ\n" +
                "A:ขอโทษนะคะ กรุณาติดต่อ contact@eoss-th.com เพื่อสอบถามข้อมูลเพิ่มเติมค่ะ\n" +
                "Q:ขอโทษ ค่ะ\n" +
                "A:สนใจเนื้อหาอะไรคะ?\n" +
                "Q:course javascript\n" +
                "A:สามารถดูรายละเอียดได้ที่ https://www.eoss-th.com/p/javascript-javascript-ecmascript-2015.html เลยค่ะ\n" +
                "Q:อยาก เรียน ทาง ด้าน ไหน คะ ?\n" +
                "A:course ##!\n" +
                "Q:ขอโทษ ค่ะ\n" +
                "A:อยากเรียนทางด้านไหนคะ?\n" +
                "Q:course basic\n" +
                "A:สามารถดูรายละเอียดได้ที่ https://www.eoss-th.com/p/javascript-javascript-ecmascript-2015.html เลยค่ะ"
        )));

        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
        assertEquals("ขอโทษนะคะตอนนี้ java ยังไม่เปิดสอนค่ะ", session.parse(MessageObject.build("course java")));
    }

}