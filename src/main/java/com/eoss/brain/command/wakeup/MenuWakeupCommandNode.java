package com.eoss.brain.command.wakeup;

import com.eoss.brain.MessageObject;
import com.eoss.brain.Session;
import com.eoss.brain.command.*;
import com.eoss.brain.command.data.*;
import com.eoss.brain.command.talk.FeedbackCommandNode;
import com.eoss.brain.command.talk.Key;
import com.eoss.brain.command.talk.TalkCommandNode;
import com.eoss.brain.command.talk.menu.MenuTalkCommandNode;

import java.util.Arrays;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class MenuWakeupCommandNode extends CommandNode {

    public static final Key KEY = new Key("\uD83D\uDE0A", "?", Arrays.asList("\uD83D\uDC4D", "\uD83D\uDC4E", "ไม่"));

    public MenuWakeupCommandNode(Session session) {
        this (session, null);
    }

    public MenuWakeupCommandNode(Session session, String [] hooks) {
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

        /**
         * Command list is Ordered by Priority
         */
        session.adminCommandList.clear();
        session.adminCommandList.add(new AdminCommandNode(new RegisterAdminCommandNode(session, new String[]{"ลงทะเบียนผู้ดูแล"})));
        session.adminCommandList.add(new AdminCommandNode(new DebugCommandNode(session, new String[]{"ดูทั้งหมด"})));
        session.adminCommandList.add(new AdminCommandNode(new LoadDataCommandNode(session, new String[]{"โหลดข้อมูล"})));
        session.adminCommandList.add(new AdminCommandNode(new SaveDataCommandNode(session, new String[]{"บันทึกข้อมูล"})));
        session.adminCommandList.add(new AdminCommandNode(new BackupDataCommandNode(session, new String[]{"สำรองข้อมูล"})));
        session.adminCommandList.add(new AdminCommandNode(new RestoreDataCommandNode(session, new String[]{"กู้ข้อมูล"})));
        session.adminCommandList.add(new AdminCommandNode(new ClearDataCommandNode(session, new String[]{"ล้างข้อมูล"})));
        session.adminCommandList.add(new AdminCommandNode(new ImportRawDataFromWebCommandNode(session, new String[]{"ใส่ข้อมูลดิบจากเวป"})));
        session.adminCommandList.add(new AdminCommandNode(new ImportRawDataCommandNode(session, new String[]{"ใส่ข้อมูลดิบ"})));
        session.adminCommandList.add(new AdminCommandNode(new ExportRawDataCommandNode(session, new String[]{"ดูข้อมูลดิบ"})));
        session.adminCommandList.add(new AdminCommandNode(new ImportQADataCommandNode(session, new String[]{"ใส่ข้อมูลถามตอบ"}, "Q:", "A:")));
        session.adminCommandList.add(new AdminCommandNode(new ExportQADataCommandNode(session, new String[]{"ดูข้อมูลถามตอบ"}, "Q:", "A:")));
        session.adminCommandList.add(new AdminCommandNode(new ExportMermaidDataCommandNode(session, new String[]{"ดูข้อมูลกราฟ"})));
        session.adminCommandList.add(new AdminCommandNode(new CreateWebIndexCommandNode(session, new String[]{"ใส่ข้อมูลสารบัญจากเวป"})));
        session.adminCommandList.add(new AdminCommandNode(new EnableTeacherCommandNode(session, new String[]{"เปิดโหมดเรียนรู้"})));
        session.adminCommandList.add(new AdminCommandNode(new DisableTeacherCommandNode(session, new String[]{"ปิดโหมดเรียนรู้"})));

        session.commandList.clear();
        session.commandList.add(new FeedbackCommandNode(session, new String[]{"\uD83D\uDC4D"}, "\uD83D\uDE0A", 0.1f));
        session.commandList.add(new FeedbackCommandNode(session, new String[]{"\uD83D\uDC4E"}, "\uD83D\uDE1F", -0.1f, KEY));
        session.commandList.add(new ForwardCommandNode(session, new String[]{"Next"}, KEY));
        session.commandList.add(new MenuTalkCommandNode(session, KEY));

        return "...";
    }
}
