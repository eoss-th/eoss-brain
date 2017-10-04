package com.eoss.brain;

import com.eoss.brain.command.line.BizWakeupCommandNode;
import com.eoss.brain.net.Context;
import com.eoss.brain.net.ContextListener;
import com.eoss.brain.net.GAEStorageContext;
import com.eoss.brain.net.GAEWebIndexSupportContext;

import java.util.*;

public class ContextTestCase {

    public static void main(String[]args) throws Exception {

        List<String> adminIdList = new ArrayList<>(Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd"));

        Context context = new GAEWebIndexSupportContext(new GAEStorageContext("bothoi"))
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
        new BizWakeupCommandNode(session).execute(null);

        Scanner scanner = new Scanner(System.in, "UTF-8");

        MessageObject template = MessageObject.build();
        template.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");
        template.attributes.put("senderId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        while(true) {
            System.out.print("You:>>");
            System.out.println("Bot:>>" + session.parse(MessageObject.build(template, scanner.nextLine())));
        }

    }
}
