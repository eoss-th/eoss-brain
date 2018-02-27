package com.eoss.brain;

import com.eoss.brain.command.wakeup.WakeupCommandNode;
import com.eoss.brain.context.FileContext;
import com.eoss.brain.net.*;

import java.util.*;

public class ContextTestCaseNoom {

    public static void main(String[]args) throws Exception {
        Locale.setDefault(new Locale("th", "TH"));

        List<String> adminIdList = new ArrayList<>(Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd"));

        Context context = new FileContext("noom")
                .callback(new ContextListener() {
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
                        }
                    }
                }).admin(adminIdList);

        Session session = new Session(context);
        new WakeupCommandNode(session).execute(null);

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
