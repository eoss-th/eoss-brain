package com.eoss.brain;

import com.eoss.brain.command.wakeup.BizWakeupCommandNode;
import com.eoss.brain.command.wakeup.WakeupCommandNode;
import com.eoss.brain.context.FileContext;
import com.eoss.brain.net.*;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class ContextTestCaseNoom {

    public static void main(String[]args) throws Exception {
        Locale.setDefault(new Locale("th", "TH"));

        List<String> adminIdList = new ArrayList<>(Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd"));

        Context context = new FileContext("raw")
                .callback(new ContextListener() {
                    @Override
                    public void callback(NodeEvent nodeEvent) {
                        if (nodeEvent.event == NodeEvent.Event.Recursive) {
                            System.out.println(nodeEvent.messageObject);
                            return;
                        }
                        if (nodeEvent.event == NodeEvent.Event.LateReply) {
                            String [] responses = nodeEvent.messageObject.toString().split(System.lineSeparator());
                            for (String response:responses) {
                                System.out.println("Bot:>>" + response);
                            }
                            return;
                        }
                    }
                }).admin(adminIdList);

        //        .domain("hopidea.com");

        Session session = new Session(context);
        //Session session = new Session(new MemoryContext("test"));
        new WakeupCommandNode(session).execute(null);

        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("raw.ser"));
        os.writeObject(session);

        Scanner scanner = new Scanner(System.in, "UTF-8");

        MessageObject template = MessageObject.build();
        template.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");
        template.attributes.put("senderId", "Uee73cf96d1dbe69a260d46fc03393cfd");
        session.learning=true;
        while(true) {
            System.out.print("You:>>");
            System.out.println("Bot:>>" + session.parse(MessageObject.build(template, scanner.nextLine())));
        }

    }
}