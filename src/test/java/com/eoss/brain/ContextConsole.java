package com.eoss.brain;

import com.eoss.brain.command.talk.AnswerResponseCommandNode;
import com.eoss.brain.command.talk.Choice;
import com.eoss.brain.command.talk.Question;
import com.eoss.brain.command.wakeup.MenuWakeupCommandNode;
import com.eoss.brain.command.wakeup.WakeupCommandNode;
import com.eoss.brain.context.FileContext;
import com.eoss.brain.net.*;

import java.util.*;

public class ContextConsole {

    public static void main(String[]args) throws Exception {

        List<String> adminIdList = new ArrayList<>(Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd"));

        Context context = new FileContext("mall").admin(adminIdList).locale(new Locale("th"));

        context.load();

        Session session = new Session(context);

        session.callback(new SessionListener() {
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

                    String unknown = session.context.properties.get("unknown");

                    if (unknown.endsWith("!")) {
                        int startSubIndex;
                        int lastIndexOfComma = unknown.lastIndexOf(",");
                        if (lastIndexOfComma==-1) {
                            startSubIndex = 0;//Start Substring at Zero;
                        } else {
                            startSubIndex = lastIndexOfComma + 1;
                        }

                        unknown = unknown.substring(startSubIndex, unknown.length()-1);
                    } else {
                        System.out.println(unknown);
                        return;
                    }

                    List<String> parameters = new ArrayList<>();
                    parameters.add(nodeEvent.messageObject.toString());
                    nodeEvent.messageObject.attributes.put("parameters", parameters);

                    session.clearProblem();
                    String responseText = session.parse(MessageObject.build(nodeEvent.messageObject, unknown.trim()));

                    System.out.println(responseText);

                    return;
                }
                if (nodeEvent.event == NodeEvent.Event.HesitateConfidence) {
                    //System.out.println("Bot:>>" + Hook.toString(nodeEvent.node.hookList()) + "?");
                    return;
                }
                if (nodeEvent.event == NodeEvent.Event.Question) {

                    List<Question> questionList = (List<Question>) nodeEvent.messageObject.attributes.get("Question");
                    for (Question question:questionList) {
                        if (question.hasImage()) {
                            System.out.println(question.imageURL);
                        }
                        System.out.println(question.label);
                        for (Choice choice: question.choices) {
                            System.out.print("\t");
                            System.out.println(choice.label);
                        }
                    }
                    return;
                }

            }
        });
        new MenuWakeupCommandNode(session).execute(null);

        Scanner scanner = new Scanner(System.in, "UTF-8");

        MessageObject template = MessageObject.build();
        //template.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");
        //template.attributes.put("senderId", "Uee73cf96d1dbe69a260d46fc03393cfd");
        //session.learning=true;

        String reply;
        List<Choice> choiceList;
        while(true) {
            System.out.print("You:>>");
            reply = session.parse(MessageObject.build(template, scanner.nextLine()));
            System.out.println("Bot:>>" + reply);
        }

    }
}
