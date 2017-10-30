package com.eoss.brain.command.line;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.MessageTemplate;
import com.eoss.brain.command.*;
import com.eoss.brain.command.data.*;
import com.eoss.brain.command.http.GetCommandNode;
import com.eoss.brain.command.http.GoogleCommandNode;
import com.eoss.brain.command.http.ReadCommandNode;
import com.eoss.brain.command.talk.FeedbackCommandNode;
import com.eoss.brain.command.talk.BizTalkCommandNode;
import com.eoss.brain.net.Node;

import java.util.Arrays;
import java.util.List;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class BizWakeupCommandNode extends CommandNode {

    public BizWakeupCommandNode(Session session) {
        this (session, null);
    }

    public BizWakeupCommandNode(Session session, String [] hooks) {
        super(session, hooks);
    }

    @Override
    public String execute(MessageObject messageObject) {

        try {
            session.context.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * Protected from bad words
         */
        session.protectedList.clear();
        session.protectedList.add(new Node(new String[]{"เหี้ย", "สัส", "แสรด","สัด", "หมอย", "ควย", "เงี่ยน", "หี", "แตด", "แตรด", "เย็ด", "จิ๋ม", "รูตูด", "รูดาก", "รูขี้", "พ่อง", "อีดอก", "fuck", "Fuck", "bitch", "Bitch", "yed", "kvy", "Kvy", "YED", "Yed", "FUCK", "KVY"}, new String[]{"ไม่เอาไม่หยาบสิ"}, Mode.MatchBody));
        session.protectedList.add(new Node(new String[]{"ในหลวง", "เสี่ยโอ", "พระบรม", "นายก", "ประยุทธ์", "ประยุด", "ประยุต", "ประยุทต์", "ทหาร", "รัฐบาล", "ตำรวจ", "ราชินี", "รัชกาลที่"}, new String[]{"ไม่เอาไม่การเมืองนะ"}, Mode.MatchBody));

        /**
         * Command list is Ordered by Priority
         */

        session.commandList.clear();
        session.commandList.add(new AdminCommandNode(new RegisterAdminCommandNode(session, new String[]{"ลงทะเบียนผู้ดูแล"})));
        session.commandList.add(new AdminCommandNode(new DebugCommandNode(session, new String[]{"ดูทั้งหมด"})));
        session.commandList.add(new AdminCommandNode(new LoadDataCommandNode(session, new String[]{"โหลดข้อมูล"})));
        session.commandList.add(new AdminCommandNode(new SaveDataCommandNode(session, new String[]{"บันทึกข้อมูล"})));
        session.commandList.add(new AdminCommandNode(new BackupDataCommandNode(session, new String[]{"สำรองข้อมูล"})));
        session.commandList.add(new AdminCommandNode(new RestoreDataCommandNode(session, new String[]{"กู้ข้อมูล"})));
        session.commandList.add(new AdminCommandNode(new ClearDataCommandNode(session, new String[]{"ล้างข้อมูล"})));
        session.commandList.add(new AdminCommandNode(new ImportRawDataFromWebCommandNode(session, new String[]{"ใส่ข้อมูลดิบจากเวป"})));
        session.commandList.add(new AdminCommandNode(new ImportRawDataCommandNode(session, new String[]{"ใส่ข้อมูลดิบ"})));
        session.commandList.add(new AdminCommandNode(new ImportQADataCommandNode(session, new String[]{"ใส่ข้อมูลถามตอบ"}, "ถาม:", "ตอบ:")));
        session.commandList.add(new AdminCommandNode(new CreateWebIndexCommandNode(session, new String[]{"ใส่ข้อมูลสารบัญจากเวป"})));
        session.commandList.add(new AdminCommandNode(new EnableTeacherCommandNode(session, new String[]{"เปิดโหมดผู้ช่วยสอน"})));
        session.commandList.add(new AdminCommandNode(new DisableTeacherCommandNode(session, new String[]{"ปิดโหมดผู้ช่วยสอน"})));

        List<String> rejectKeys = Arrays.asList("ไม่", "เข้าใจละ", "ไม่", "ก็แล้วแต่");

        //Negative Feedback
        session.commandList.add(new FeedbackCommandNode(session, new String[]{"ผิด", "ไม่ใช่", "ม่าย", "หืม"}, "กำ", -0.2f));

        //Feedback for learning mode
        session.commandList.add(new FeedbackCommandNode(session, new String[]{"ไม่"}, "แล้วจะให้ตอบว่า?", -0.1f, rejectKeys));
        session.commandList.add(new FeedbackCommandNode(session, new String[]{"แก้"}, "แล้วจะให้ตอบว่า?", 0, rejectKeys));

        //Positive Feedback
        session.commandList.add(new FeedbackCommandNode(session, new String[]{"เชร้ด", "เยี่ยม", "แจ่ม", "แจ๋ว", "ใช่เลย", "ถูกต้อง", "เก่งมาก", "ดีมาก", "555", "ขอบคุณ"}, "เขิลลล", 0.2f));
        session.commandList.add(new FeedbackCommandNode(session, new String[]{"ใช่", "ถูก", "เออ", "ช่าย", "เก่ง", "55", "ขอบใจ"}, "อิอิ", 0.1f));

        session.commandList.add(new ForwardCommandNode(session, new String[]{"แล้ว", "ยังไง", "เหรอ", "ต่อต่อ"}));
        session.commandList.add(new LeaveCommandNode(session, new String[]{"อีดอก", "ควย", "เหี้ย", "สัส", "สัด", "แสด", "เหียก", "สัตว์", "พ่อง", "เย็ด", "แม่ง", "หี", "แสรด", "แตด", "ไป๊", "ออกไป๊", "ไสหัวไป", "ไปซะ", "มึงออกไป", "ออกไป"}, "ใจร้ายย เผ่นดีกว่า\nกดตรงนี้เพื่อเป็นเพื่อนกับเราน้า\n"));

        session.commandList.add(new SleepCommandNode(session, new String[]{"หลับ"}, new WakeupCommandNode(session, new String[]{"ตื่น"})));
        session.commandList.add(new SpeakCommandNode(session, new String[]{"พูด"}));
        session.commandList.add(new SilentCommandNode(session, new String[]{"เงียบ"}));

        session.commandList.add(new EnableModeCommandNode(session, new String[]{"โหมด"}));
        session.commandList.add(new DisableModeCommandNode(session, new String[]{"ออกจากโหมด"}));

        session.commandList.add(new EnableTeacherCommandNode(session, new String[]{"เปิดโหมดเรียนรู้"}));
        session.commandList.add(new DisableTeacherCommandNode(session, new String[]{"ปิดโหมดเรียนรู้"}));

        session.commandList.add(new GetCommandNode(session, new String[]{"เรียก"}, ""));
        session.commandList.add(new ReadCommandNode(session, new String[]{"อ่าน"}, ""));
        session.commandList.add(new GoogleCommandNode(session, new String[]{"ค้นหา"}, 1));

        List<String> lowConfidenceKeys = Arrays.asList("เข้าใจละ", "ไม่", "ก็แล้วแต่", "คือ?");

        List<String> confirmKeys = Arrays.asList("ใช่", "เออ", "อืม", "อือ");

        List<String> cancelKeys = Arrays.asList("ไม่", "ไม่ใช่", "หึ");

        List<String> confirmMsg = Arrays.asList("หมายถึง", "หรือว่า", "รึป่าวค่ะ?");

        session.commandList.add(new BizTalkCommandNode(session, lowConfidenceKeys, confirmKeys, cancelKeys, "หาไม่เจอคำถามที่ถามมาค่ะ ช่วยบอกกรุณาบอกให้ละเอียดิีกครั้งค่ะ", confirmMsg));
        //session.commandList.add(new TalkCommandNode(session, lowConfidenceKeys));

        return MessageTemplate.STICKER + "1:405";
    }
}
