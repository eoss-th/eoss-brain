package com.eoss.brain.command.data;

import com.eoss.brain.MessageObject;
import com.eoss.brain.Session;
import com.eoss.brain.command.wakeup.WakeupCommandNode;
import com.eoss.brain.context.FileContext;
import com.eoss.brain.net.Context;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

public class ExportMermaidDataCommandNodeTest {

    @Test
    public void test() {
        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new FileContext("hot").locale(new Locale("th"));
        context.admin(adminIdList);
        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("", session.parse(MessageObject.build(messageObject, "ดูข้อมูลกราฟ")));

        /**
         Actual   :{"nodes":[{"response":"Good #name","nodeId":-187276454,"hooks":"Hello How are you ?"},{"response":"Hello ## `?name=##` How are you?","nodeId":31307860,"hooks":"Hello what is your name ?"},{"response":"ทักทาย #1!","nodeId":-362935029,"hooks":"ดี ฮะ ชื่อ ไร"},{"response":"ดูดวง ศุกร์ `get://horoscope.mthai.com/apps/daily-horo`!","nodeId":-316101096,"hooks":"เกิด วัน อะไร ศุกร์"},{"response":"ดูดวง เสาร์ `get://horoscope.mthai.com/apps/daily-horo`!","nodeId":-745005910,"hooks":"เกิด วัน อะไร เสาร์"},{"response":"`jsoup://##://#content-inner h4` `jsoup://##://#content-inner div:eq(2) p`","nodeId":-1803723725,"hooks":"ดูด วง อาทิตย์"},{"response":"`jsoup://##://#content-inner h4` `jsoup://##://#content-inner div:eq(3) p`","nodeId":2124355553,"hooks":"ดูด วง จันทร์"},{"response":"`jsoup://##://#content-inner h4` `jsoup://##://#content-inner div:eq(4) p`","nodeId":586000640,"hooks":"ดูด วง อังคาร"},{"response":"`jsoup://##://#content-inner h4` `jsoup://##://#content-inner div:eq(5) p`","nodeId":1110430709,"hooks":"ดูด วง พุธ"},{"response":"`jsoup://##://#content-inner h4` `jsoup://##://#content-inner div:eq(6) p`","nodeId":546450678,"hooks":"ดูด วง พฤหัส"},{"response":"`jsoup://##://#content-inner h4` `jsoup://##://#content-inner div:eq(7) p`","nodeId":850872507,"hooks":"ดูด วง ศุกร์"},{"response":"`jsoup://##://#content-inner h4` `jsoup://##://#content-inner div:eq(8) p`","nodeId":1527426117,"hooks":"ดูด วง เสาร์"},{"response":"ดูดวง อาทิตย์  `get://horoscope.mthai.com/apps/daily-horo`!","nodeId":665543882,"hooks":"เกิด วัน อะไร อาทิตย์"},{"response":"ดูดวง จันทร์  `get://horoscope.mthai.com/apps/daily-horo`!","nodeId":-1692780372,"hooks":"เกิด วัน อะไร จันทร์"},{"response":"ดูดวง อังคาร  `get://horoscope.mthai.com/apps/daily-horo`!","nodeId":2094285164,"hooks":"เกิด วัน อะไร อังคาร"},{"response":"ดูดวง พุธ  `get://horoscope.mthai.com/apps/daily-horo`!","nodeId":656674474,"hooks":"เกิด วัน อะไร พุธ"},{"response":"ดูดวง พฤหัส  `get://horoscope.mthai.com/apps/daily-horo`!","nodeId":-148541718,"hooks":"เกิด วัน อะไร พฤหัส"},{"response":"เลขท้ายสองตัวคือ `jsoup://#1://p[class=red_txt]`","nodeId":45123651,"hooks":"ทำนาย หวย"},{"response":"ทำนายหวย `get://lotto.mthai.com/dream/##.html`!","nodeId":1915717579,"hooks":"ฝัน ว่า อะไร"},{"response":"เกิดวันอะไร?","nodeId":-552490584,"hooks":"เช็ค ดวง"},{"response":"ว่าไงไอ้ผู้ก่อการร้าย","nodeId":1381728627,"hooks":"ทักทาย ด้วง"},{"response":"สัส","nodeId":1498797857,"hooks":"แส รด"},{"response":"ปะป๊า","nodeId":-313777072,"hooks":"พ่อ ง"},{"response":"ทักทาย ##!","nodeId":1214132618,"hooks":"ใคร ?"},{"response":"หึ หนีเมียไปนวดปู๋ล่ะสิ","nodeId":1636840922,"hooks":"ทักทาย นพ"},{"response":"ไอ้บ้ากาม","nodeId":-1460938046,"hooks":"ทักทาย คิม"},{"response":"หึหึ ไอ้คนเจ้าชู้","nodeId":2013815328,"hooks":"ทักทาย อุ้ย"},{"response":"วิ่งเข้าไปไอ้หื่น","nodeId":-1451371624,"hooks":"ทักทาย ดุก"},{"response":"ไงไอ้กาม","nodeId":1217361927,"hooks":"ทักทาย อั๋น"},{"response":"กี่ทีล่ะไอ่สัส","nodeId":-1479880281,"hooks":"ทักทาย ที"},{"response":"ดีฮะชื่อไร?","nodeId":1056022129,"hooks":"สวัสดี"},{"response":"ฝันว่าอะไร?","nodeId":1805872905,"hooks":"ฝัน"},{"response":"เกิดวันอะไร?","nodeId":-767698280,"hooks":"ดวง"},{"response":"Hello what is your name?","nodeId":963669355,"hooks":"hi"},{"response":"ดีฮะชื่อไร?","nodeId":85818597,"hooks":"ไง"},{"response":"ดีฮะชื่อไร?","nodeId":85773895,"hooks":"ดี"},{"response":"ดีฮับ #1","nodeId":-1207675307,"hooks":"ทักทาย"},{"response":"แสรด","nodeId":1884570516,"hooks":"สัส"},{"response":"พ่อง","nodeId":1882415042,"hooks":"ควย"},{"response":"ห่า","nodeId":-2104180369,"hooks":"เหี้ย"},{"response":"หอย","nodeId":1667836851,"hooks":"หี"},{"response":"พ่อง","nodeId":1775529305,"hooks":"หำ"},{"response":"ใคร?","nodeId":-114499351,"hooks":"เงี่ยน"},{"response":"ช่วยพิมพ์คำว่า ดวง เพื่อเช็คดวงประจำวัน หรือ ฝัน เพื่อทำนายหวยเข้าใจมั้ย","nodeId":-1166844357,"hooks":"ไม่ รู้ฮับ"},{"response":"ก็บอกว่าพิมพ์คำว่า ดวง เพื่อเช็คดวงประจำวัน หรือ ฝัน เพื่อทำนายหวยนะฮับ","nodeId":-952056664,"hooks":"ไม่ รู้ฮับ"},{"response":"โอ้ย! พิมพ์คำว่า ดวง เพื่อเช็คดวงประจำวัน หรือ ฝัน เพื่อทำนายหวยสิ","nodeId":1996208470,"hooks":"ไม่ รู้ฮับ"},{"response":"พิมพ์คำว่า ดวง เพื่อเช็คดวงประจำวัน หรือ ฝัน เพื่อทำนายหวยนะ","nodeId":1696917568,"hooks":"ไม่ รู้ฮับ"}],
         "arrows":[
         "Hello what is your name ?:Hello   How are you-->Hello How are you ?",
         "ดี ฮะ ชื่อ ไร:ทักทาย-->ทักทาย",
         "ดี ฮะ ชื่อ ไร:ทักทาย-->ทักทาย ดุก",
         "ดี ฮะ ชื่อ ไร:ทักทาย-->ทักทาย อั๋น",
         "ดี ฮะ ชื่อ ไร:ทักทาย-->ทักทาย ด้วง",
         "ดี ฮะ ชื่อ ไร:ทักทาย-->ทักทาย อุ้ย",
         "ดี ฮะ ชื่อ ไร:ทักทาย-->ทักทาย คิม",
         "ดี ฮะ ชื่อ ไร:ทักทาย-->ทักทาย นพ",
         "ดี ฮะ ชื่อ ไร:ทักทาย-->ทักทาย ที",
         "เกิด วัน อะไร ศุกร์:ดูดวง ศุกร์-->ดูด วง ศุกร์",
         "เกิด วัน อะไร เสาร์:ดูดวง เสาร์-->ดูด วง เสาร์",
         "เกิด วัน อะไร อาทิตย์:ดูดวง อาทิตย์-->ดูด วง อาทิตย์",
         "เกิด วัน อะไร จันทร์:ดูดวง จันทร์-->ดูด วง จันทร์",
         "เกิด วัน อะไร อังคาร:ดูดวง อังคาร-->ดูด วง อังคาร",
         "เกิด วัน อะไร พุธ:ดูดวง พุธ-->ดูด วง พุธ",
         "เกิด วัน อะไร พฤหัส:ดูดวง พฤหัส-->ดูด วง พฤหัส",
         "ฝัน ว่า อะไร:ทำนายหวย-->ทำนาย หวย",
         "เช็ค ดวง:เกิดวันอะไร-->เกิด วัน อะไร ศุกร์",
         "เช็ค ดวง:เกิดวันอะไร-->เกิด วัน อะไร อาทิตย์",
         "เช็ค ดวง:เกิดวันอะไร-->เกิด วัน อะไร เสาร์",
         "เช็ค ดวง:เกิดวันอะไร-->เกิด วัน อะไร จันทร์",
         "เช็ค ดวง:เกิดวันอะไร-->เกิด วัน อะไร อังคาร",
         "เช็ค ดวง:เกิดวันอะไร-->เกิด วัน อะไร พุธ",
         "เช็ค ดวง:เกิดวันอะไร-->เกิด วัน อะไร พฤหัส",
         "ใคร ?:ทักทาย-->ทักทาย",
         "ใคร ?:ทักทาย-->ทักทาย ดุก","ใคร ?:ทักทาย-->ทักทาย อั๋น","ใคร ?:ทักทาย-->ทักทาย ด้วง","ใคร ?:ทักทาย-->ทักทาย อุ้ย","ใคร ?:ทักทาย-->ทักทาย คิม","ใคร ?:ทักทาย-->ทักทาย นพ","ใคร ?:ทักทาย-->ทักทาย ที","สวัสดี:ดีฮะชื่อไร-->ดี ฮะ ชื่อ ไร","ฝัน:ฝันว่าอะไร-->ฝัน ว่า อะไร","ดวง:เกิดวันอะไร-->เกิด วัน อะไร ศุกร์","ดวง:เกิดวันอะไร-->เกิด วัน อะไร อาทิตย์","ดวง:เกิดวันอะไร-->เกิด วัน อะไร เสาร์","ดวง:เกิดวันอะไร-->เกิด วัน อะไร จันทร์","ดวง:เกิดวันอะไร-->เกิด วัน อะไร อังคาร","ดวง:เกิดวันอะไร-->เกิด วัน อะไร พุธ","ดวง:เกิดวันอะไร-->เกิด วัน อะไร พฤหัส","hi:Hello what is your name-->Hello what is your name ?","ไง:ดีฮะชื่อไร-->ดี ฮะ ชื่อ ไร","ดี:ดีฮะชื่อไร-->ดี ฮะ ชื่อ ไร","เงี่ยน:ใคร-->ใคร ?"]}
         */

    }

}