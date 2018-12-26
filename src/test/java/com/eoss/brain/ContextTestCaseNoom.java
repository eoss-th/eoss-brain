package com.eoss.brain;

import com.eoss.brain.command.wakeup.WakeupCommandNode;
import com.eoss.brain.context.FileContext;
import com.eoss.brain.net.*;

import java.util.*;

public class ContextTestCaseNoom {

    public static void main(String[]args) throws Exception {
        Locale.setDefault(new Locale("th", "TH"));
        //Locale.setDefault(new Locale("en", "EN"));

        List<String> adminIdList = new ArrayList<>(Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd"));

        Context context = new FileContext("noom").admin(adminIdList);

        Session session = new Session(context).callback(new SessionListener() {
            @Override
            public void callback(NodeEvent nodeEvent) {
                if (nodeEvent.event == NodeEvent.Event.NewNodeAdded) {
                    System.out.println("Add new node:" + nodeEvent.node);
                    return;
                }
                if (nodeEvent.event == NodeEvent.Event.LateReply) {
                    String [] responses = nodeEvent.messageObject.toString().split(System.lineSeparator());
                    for (String response:responses) {
                        System.out.println("Bot:>>" + response);
                    }
                    return;
                }
                if (nodeEvent.event == NodeEvent.Event.LowConfidence) {
                    System.out.println("Low confidence:" + nodeEvent.messageObject);
                    return;
                }
                if (nodeEvent.event == NodeEvent.Event.HesitateConfidence) {
                    //System.out.println("Bot:>>" + Hook.toString(nodeEvent.node.hookList()) + "?");
                    return;
                }
            }
        });
        new WakeupCommandNode(session).execute(null);

        Scanner scanner = new Scanner(System.in, "UTF-8");

        MessageObject template = MessageObject.build();
        template.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");
        template.attributes.put("senderId", "Uee73cf96d1dbe69a260d46fc03393cfd");
        session.learning=true;

        /*
        session.parse(MessageObject.build(template, "ใส่ข้อมูลถามตอบ\n" +
                "Q: จองโรงแรม\n" +
                "A: จะไปไหนคะ?\n" +
                "Q: จะไปไหนคะ\n" +
                "A: #1!\n" +
                "Q: ลาว\n" +
                "A: ไปจังหวัดไรคะ?\n" +
                "Q: ไปจังหวัดไรคะ\n" +
                "A: ขอให้ไป ## ให้สนุกนะคะ\n" +
                "Q: สวัสดี\n" +
                "A: ดีจ้า #1\n" +
                "Q: เป็นไงบ้าง\n" +
                "A: สวัสดี!\n"
        ));
        */

        while(true) {
            System.out.print("You:>>");
            System.out.println("Bot:>>" + session.parse(MessageObject.build(template, scanner.nextLine())));
        }

    }
}
