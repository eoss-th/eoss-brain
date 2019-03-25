package com.eoss.brain;

import com.eoss.brain.command.talk.AnswerResponseCommandNode;
import com.eoss.brain.command.wakeup.WakeupCommandNode;
import com.eoss.brain.context.FileContext;
import com.eoss.brain.net.*;

import java.util.*;

public class ContextConsole {

    public static void main(String[]args) throws Exception {

        List<String> adminIdList = new ArrayList<>(Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd"));

        Context context = new FileContext("jook").admin(adminIdList).locale(new Locale("th"));

        context.load();

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
                if (nodeEvent.event == NodeEvent.Event.Question) {

                    List<AnswerResponseCommandNode.Question> questionList = (List<AnswerResponseCommandNode.Question>) nodeEvent.messageObject.attributes.get("Question");
                    for (AnswerResponseCommandNode.Question question:questionList) {
                        System.out.println(question.label);
                        for (AnswerResponseCommandNode.Choice choice: question.choices) {
                            System.out.print("\t");
                            System.out.println(choice.label);
                        }
                    }
                    return;
                }

            }
        });
        new WakeupCommandNode(session).execute(null);

        Scanner scanner = new Scanner(System.in, "UTF-8");

        MessageObject template = MessageObject.build();
        //template.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");
        //template.attributes.put("senderId", "Uee73cf96d1dbe69a260d46fc03393cfd");
        //session.learning=true;

        String reply;
        List<AnswerResponseCommandNode.Choice> choiceList;
        while(true) {
            System.out.print("You:>>");
            reply = session.parse(MessageObject.build(template, scanner.nextLine()));
            System.out.println("Bot:>>" + reply);
        }

    }
}
