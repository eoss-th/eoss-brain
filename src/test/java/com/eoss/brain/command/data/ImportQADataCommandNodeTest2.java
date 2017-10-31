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
public class ImportQADataCommandNodeTest2 {
    @Test
    public void execute() throws Exception {

        Locale.setDefault(new Locale("th", "TH"));

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new MemoryContext("test");
        context.admin(adminIdList);
        Session session = new Session(context);
        new BizWakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject,"ใส่ข้อมูลถามตอบ\n" +
                "ถาม: ทีเอ็มบี ทัช รองรับระบบปฏิบัติการเวอร์ชั่นใดบ้าง ?\n" +
                "ตอบ: ทีเอ็มบี ทัช รองรับระบบปฏิบัติการ iOS Version 7.0 ขึ้นไป และ Android 4.0 ขึ้นไป\n" +
                "\n" +
                "ถาม: หากใช้โทรศัพท์มือถือที่ไม่ใช่สมาร์ทโฟนจะสามารถใช้งาน ทีเอ็มบี ทัช ได้หรือไม่ ?\n" +
                "ตอบ: โทรศัพท์มือถือที่ไม่ใช่สมาร์ทโฟนจะไม่สามารถใช้งาน ทีเอ็มบี ทัช ได้\n" +
                "\n" +
                "ถาม: สามารถใช้งาน ทีเอ็มบี ทัช บนแท็บเล็ตได้หรือไม่ ?\n" +
                "ตอบ: สามารถใช้งานทีเอ็มบี ทัช บนไอแพด และ แอนดรอยด์แท็บเล็ตได้\n" +
                "หมายเหตุ : กรณีดาวน์โหลดเพื่อติดตั้งบนไอแพด กรุณาเลือกค้นหา \"TMB Touch\" จากเมนู iPhone Only เท่านั้น (ถ้าเลือก iPad only จะไม่เจอ)\n" +
                "\n" +
                "ถาม: สามารถใช้ ทีเอ็มบี ทัช และ ทีเอ็มบี อินเทอร์เน็ตแบงก์กิ้งกับโทรศัพท์มือถือหรือแท็บเล็ตที่ผ่านการเจลเบรค/รูท(การปรับแต่งการใช้งานที่ไม่ได้รับการอนุญาต)ได้หรือไม ?\n" +
                "ตอบ: โทรศัพท์มือถือหรือแท็บเล็ตที่ผ่านการเจลเบรค/รูท (การปรับแต่งการใช้งานที่ไม่ได้รับการอนุญาต) จะยังคงสามารถใช้งาน ทีเอ็มบี ทัช และ ทีเอ็มบี อินเทอร์เน็ตแบงก์กิ้ง ได้ตามปกติ\n" +
                "\n" +
                "ถาม: สามารถใช้ ทีเอ็มบี ทัช และ ทีเอ็มบี อินเทอร์เน็ตแบงก์กิ้ง กับโทรศัพท์มือถือหรือแท็บเล็ตที่ซื้อมาจากต่างประเทศได้หรือไม่ ?\n" +
                "ตอบ: สามารถใช้ ทีเอ็มบี ทัช และ ทีเอ็มบี อินเทอร์เน็ตแบงก์กิ้ง ได้ตามปกติ โดยโทรศัพท์มือถือหรือแท็บเล็ตที่ใช้งานต้องผ่านการตั้งค่าสำหรับเครือข่ายในประเทศไทย และเชื่อมต่อสัญญานผ่านอินเทอร์เน็ตก่อน\n" +
                "\n" +
                "ถาม: โทรศัพท์รุ่นไหน สามารถใช้งาน ทีเอ็มบี ทัช ได้ ?\n" +
                "ตอบ:ทีเอ็มบี ทัช รองรับการใช้งานบนโทรศัพท์ระบบปฏิบัติการ iOS Version 7.0 และ Android 4.0 ขึ้นไป โดยโทรศัพท์ที่ไม่สามารถรองรับทีเอ็มบี ทัช ได้แก่ ระบบปฏิบัติการ Window Phone และ Symbian ทุกรุ่น\n" +
                "\n" +
                "ถาม: บัญชีใดสามารถใช้งานใน ทีเอ็มบี ทัช และทีเอ็มบี อินเทอร์เน็ตแบงก์กิ้ง ได้บ้าง ?\n" +
                "ตอบ: บัญชีกระแสรายวัน, บัญชีเอกสิทธิ์, บัญชีออมทรัพย์ทั่วไป, บัญชีออมทรัพย์ทั่วไป (บัญชีนักศึกษา), บัญชีออมทรัพย์, ทั่วไป (TMB Payroll Plus), บัญชี โน ฟี, บัญชีฝากไม่ประจำ, บัญชี ธุรกรรมทำฟรี\n" +
                "\n" +
                "ถาม: หากทำ SMS \"รหัสเริ่มใช้งาน\" สูญหายจะต้องดำเนินการอย่างไร ?\n" +
                "ตอบ: จะต้องดำเนินการสมัครใหม่อีกครั้ง เพื่อขอรับรหัสเริ่มใช้งาน\n" +
                "\n" +
                "ถาม: รหัสเริ่มใช้งาน มีวันหมดอายุหรือไม่ ?\n" +
                "ตอบ: ในกรณีทีทำการเปิดใช้บริการครั้งแรก ท่านจะได้รับ \"รหัสเริ่มใช้งาน\" ซึ่งจะหมดอายุภายใน 3 วัน แต่ในกรณีที่ท่านทำการปลดล็อค ท่านจะได้รับ \"รหัสเริ่มใช้งาน\" ซึ่งจะหมดอายุภายใน 3 ชั่วโมง\n" +
                "\n" +
                "ถาม: หากทำการสมัครแล้วแต่ไม่ได้รับรหัสเริ่มใช้งานทาง SMS จะต้องทำอย่างไร ?\n" +
                "ตอบ: สามารถสมัครใช้บริการใหม่ได้ทันที เพื่อรับรหัสเริ่มใช้งานใหม่อีกครั้ง\n" +
                "\n" +
                "ถาม: หากทำการยกเลิกบริการทีเอ็มบี อินเทอร์เน็ตแบงก์กิ้ง และทีเอ็มบี ทัช แล้วต้องการสมัครใหม่จะสามารถสมัครใหม่ได้ทันทีหรือไม่ ?\n" +
                "ตอบ: สามารถสมัครใหม่ได้ทันที\n" +
                "\n" +
                "ถาม: สามารถใช้ ทีเอ็มบี ทัช หรือ ทีเอ็มบี อินเทอร์เน็ตแบงก์กิ้ง ในต่างประเทศได้หรือไม่ ?\n" +
                "ตอบ: กรณีที่เดินทางไปต่างประเทศ ท่านจะต้องทำการเปิดใช้บริการของ ทีเอ็มบี ทัช หรือ ทีเอ็มบี อินเทอร์เน็ตแบงก์กิ้งของท่านให้เรียบร้อยก่อนเดินทาง โดยขณะใช้งาน ท่านจะต้องเชื่อมต่อสัญญาณอินเทอร์เน็ตผ่าน Wi-Fi หรือ ใช้บริการ Data Roaming จากผู้ให้บริการโทรศัพท์มือถือ ที่ท่านใช้บริการอยู่")));

        assertEquals("หมายถึง ทีเอ็มบีทัชรองรับระบบปฏิบัติการเวอร์ชั่นใดบ้าง? รึป่าวคะ?", session.parse(MessageObject.build("รองรับระบบปฏิบัติการ")));
        assertEquals("ทีเอ็มบี ทัช รองรับระบบปฏิบัติการ iOS Version 7.0 ขึ้นไป และ Android 4.0 ขึ้นไป", session.parse(MessageObject.build("ใช่")));

        assertEquals("หมายถึง หากใช้โทรศัพท์มือถือที่ไม่ใช่สมาร์ทโฟนจะสามารถใช้งานทีเอ็มบีทัชได้หรือไม่? รึป่าวคะ?", session.parse(MessageObject.build("หากโทรศัพท์ไม่ใช่สมาร์ทโฟน")));
        assertEquals("โทรศัพท์มือถือที่ไม่ใช่สมาร์ทโฟนจะไม่สามารถใช้งาน ทีเอ็มบี ทัช ได้", session.parse(MessageObject.build("ใช่")));

        /**
         * Short Term Memory Usage
         */
        assertEquals("ใช้งานบนแท็บเล็ต? ช่วยอธิบายเพิ่มเติมหน่อยค่ะ", session.parse(MessageObject.build("ใช้งานบนแท็บเล็ต")));
        assertEquals("หมายถึง สามารถใช้งานทีเอ็มบีทัชบนแท็บเล็ตได้หรือไม่? รึป่าวคะ?", session.parse(MessageObject.build("ใช้งานได้หรือไม่")));
        assertEquals("สามารถใช้งานทีเอ็มบี ทัช บนไอแพด และ แอนดรอยด์แท็บเล็ตได้หมายเหตุ : กรณีดาวน์โหลดเพื่อติดตั้งบนไอแพด กรุณาเลือกค้นหา \"TMB Touch\" จากเมนู iPhone Only เท่านั้น (ถ้าเลือก iPad only จะไม่เจอ)", session.parse(MessageObject.build("ใช่")));

        assertEquals("หมายถึง สามารถใช้ทีเอ็มบีทัชและทีเอ็มบีอินเทอร์เน็ตแบงก์กิ้งกับโทรศัพท์มือถือหรือแท็บเล็ตที่ผ่านการเจลเบรค/รูท(การปรับแต่งการใช้งานที่ไม่ได้รับการอนุญาต)ได้หรือไม? รึป่าวคะ?", session.parse(MessageObject.build("มือถือที่ผ่านการเจลเบรค")));
        assertEquals("โทรศัพท์มือถือหรือแท็บเล็ตที่ผ่านการเจลเบรค/รูท (การปรับแต่งการใช้งานที่ไม่ได้รับการอนุญาต) จะยังคงสามารถใช้งาน ทีเอ็มบี ทัช และ ทีเอ็มบี อินเทอร์เน็ตแบงก์กิ้ง ได้ตามปกติ", session.parse(MessageObject.build("ใช่")));

        assertEquals("หมายถึง สามารถใช้ทีเอ็มบีทัชและทีเอ็มบีอินเทอร์เน็ตแบงก์กิ้งกับโทรศัพท์มือถือหรือแท็บเล็ตที่ซื้อมาจากต่างประเทศได้หรือไม่? รึป่าวคะ?", session.parse(MessageObject.build("โทรศัพท์มือถือที่ซื้อมาจากต่างประเทศ")));
        assertEquals("สามารถใช้ ทีเอ็มบี ทัช และ ทีเอ็มบี อินเทอร์เน็ตแบงก์กิ้ง ได้ตามปกติ โดยโทรศัพท์มือถือหรือแท็บเล็ตที่ใช้งานต้องผ่านการตั้งค่าสำหรับเครือข่ายในประเทศไทย และเชื่อมต่อสัญญานผ่านอินเทอร์เน็ตก่อน", session.parse(MessageObject.build("ใช่")));

        assertEquals("หมายถึง โทรศัพท์รุ่นไหนสามารถใช้งานทีเอ็มบีทัชได้? รึป่าวคะ?", session.parse(MessageObject.build("รุ่นไหนใช้ได้")));
        assertEquals("ทีเอ็มบี ทัช รองรับการใช้งานบนโทรศัพท์ระบบปฏิบัติการ iOS Version 7.0 และ Android 4.0 ขึ้นไป โดยโทรศัพท์ที่ไม่สามารถรองรับทีเอ็มบี ทัช ได้แก่ ระบบปฏิบัติการ Window Phone และ Symbian ทุกรุ่น", session.parse(MessageObject.build("ใช่")));

        assertEquals("หมายถึง บัญชีใดสามารถใช้งานในทีเอ็มบีทัชและทีเอ็มบีอินเทอร์เน็ตแบงก์กิ้งได้บ้าง? รึป่าวคะ?", session.parse(MessageObject.build("บัญชีใดใช้ได้บ้าง")));
        assertEquals("บัญชีกระแสรายวัน, บัญชีเอกสิทธิ์, บัญชีออมทรัพย์ทั่วไป, บัญชีออมทรัพย์ทั่วไป (บัญชีนักศึกษา), บัญชีออมทรัพย์, ทั่วไป (TMB Payroll Plus), บัญชี โน ฟี, บัญชีฝากไม่ประจำ, บัญชี ธุรกรรมทำฟรี", session.parse(MessageObject.build("ใช่")));

        /**
         * Short Term Memory Usage
         */
        assertEquals("SMS รหัสใช้งาน? ช่วยอธิบายเพิ่มเติมหน่อยค่ะ", session.parse(MessageObject.build("SMS รหัสใช้งาน")));
        assertEquals("หมายถึง หากทำ SMS \"รหัสเริ่มใช้งาน\"สูญหายจะต้องดำเนินการอย่างไร? รึป่าวคะ?", session.parse(MessageObject.build("หาย")));
        assertEquals("จะต้องดำเนินการสมัครใหม่อีกครั้ง เพื่อขอรับรหัสเริ่มใช้งาน", session.parse(MessageObject.build("ใช่")));

        /**
         * Short Term Memory Usage
         */
        assertEquals("รหัสวันหมดอายุ? ช่วยอธิบายเพิ่มเติมหน่อยค่ะ", session.parse(MessageObject.build("รหัสวันหมดอายุ")));
        assertEquals("หมายถึง รหัสเริ่มใช้งานมีวันหมดอายุหรือไม่? รึป่าวคะ?", session.parse(MessageObject.build("เริ่มใช้งาน")));
        assertEquals("ในกรณีทีทำการเปิดใช้บริการครั้งแรก ท่านจะได้รับ \"รหัสเริ่มใช้งาน\" ซึ่งจะหมดอายุภายใน 3 วัน แต่ในกรณีที่ท่านทำการปลดล็อค ท่านจะได้รับ \"รหัสเริ่มใช้งาน\" ซึ่งจะหมดอายุภายใน 3 ชั่วโมง", session.parse(MessageObject.build("ใช่")));

        /**
         * Short Term Memory Usage
         */
        assertEquals("หากสมัครแล้วไม่ได้รหัส? ช่วยอธิบายเพิ่มเติมหน่อยค่ะ", session.parse(MessageObject.build("หากสมัครแล้วไม่ได้รหัส")));
        assertEquals("หมายถึง หากทำการสมัครแล้วแต่ไม่ได้รับรหัสเริ่มใช้งานทาง SMS จะต้องทำอย่างไร? รึป่าวคะ?", session.parse(MessageObject.build("ทาง SMS")));
        assertEquals("สามารถสมัครใช้บริการใหม่ได้ทันที เพื่อรับรหัสเริ่มใช้งานใหม่อีกครั้ง", session.parse(MessageObject.build("ใช่")));


        assertEquals("สามารถสมัครใหม่ได้ทันที", session.parse(MessageObject.build("ยกเลิกบริการแล้วสมัครใหม่")));

        /**
         * Reject
         */
        assertEquals("หมายถึง หากใช้โทรศัพท์มือถือที่ไม่ใช่สมาร์ทโฟนจะสามารถใช้งานทีเอ็มบีทัชได้หรือไม่? รึป่าวคะ?", session.parse(MessageObject.build("ใช้ในต่างประเทศ")));
        assertEquals("หมายถึง สามารถใช้ทีเอ็มบีทัชและทีเอ็มบีอินเทอร์เน็ตแบงก์กิ้งกับโทรศัพท์มือถือหรือแท็บเล็ตที่ผ่านการเจลเบรค/รูท(การปรับแต่งการใช้งานที่ไม่ได้รับการอนุญาต)ได้หรือไม? รึป่าวคะ?", session.parse(MessageObject.build("ไม่")));
        assertEquals("ไม่เจอคำดังกล่าวค่ะ กรุณาบอกให้ละเอียดอีกครั้งค่ะ", session.parse(MessageObject.build("ไม่")));
        assertEquals("หมายถึง สามารถใช้ทีเอ็มบีทัชหรือทีเอ็มบีอินเทอร์เน็ตแบงก์กิ้งในต่างประเทศได้หรือไม่? รึป่าวคะ?", session.parse(MessageObject.build("อินเทอร์เน็ตแบงก์กิ้งใช้ในต่างประเทศ")));
        assertEquals("กรณีที่เดินทางไปต่างประเทศ ท่านจะต้องทำการเปิดใช้บริการของ ทีเอ็มบี ทัช หรือ ทีเอ็มบี อินเทอร์เน็ตแบงก์กิ้งของท่านให้เรียบร้อยก่อนเดินทาง โดยขณะใช้งาน ท่านจะต้องเชื่อมต่อสัญญาณอินเทอร์เน็ตผ่าน Wi-Fi หรือ ใช้บริการ Data Roaming จากผู้ให้บริการโทรศัพท์มือถือ ที่ท่านใช้บริการอยู่", session.parse(MessageObject.build("ใช่")));

    }

}