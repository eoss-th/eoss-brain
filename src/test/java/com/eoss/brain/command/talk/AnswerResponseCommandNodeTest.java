package com.eoss.brain.command.talk;

import com.eoss.brain.MessageObject;
import com.eoss.brain.NodeEvent;
import com.eoss.brain.Session;
import com.eoss.brain.command.wakeup.WakeupCommandNode;
import com.eoss.brain.context.MemoryContext;
import com.eoss.brain.net.Context;
import com.eoss.brain.net.SessionListener;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class AnswerResponseCommandNodeTest {

    @Test
    public void generateChoices() {

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new MemoryContext("qa");
        context.admin(adminIdList);
        Session session = new Session(context).callback(new SessionListener() {
            @Override
            public void callback(NodeEvent nodeEvent) {
                if (nodeEvent.event == NodeEvent.Event.Question) {
                    List<AnswerResponseCommandNode.Question> questionList = (List<AnswerResponseCommandNode.Question>) nodeEvent.messageObject.attributes.get("Question");

                    List<AnswerResponseCommandNode.Choice> choices = questionList.get(0).choices;

                    assertNotNull(choices);

                    assertEquals(5, choices.size());

                    AnswerResponseCommandNode.Choice c1 = choices.get(0);

                    AnswerResponseCommandNode.Choice c2 = choices.get(1);

                    AnswerResponseCommandNode.Choice c3 = choices.get(2);

                    AnswerResponseCommandNode.Choice c4 = choices.get(3);

                    AnswerResponseCommandNode.Choice c5 = choices.get(4);
                    /**
                     * Null Image
                     */
                    assertTrue(c1.isLinkLabel());

                    assertNull(c1.imageURL);

                    assertEquals("Web Pantip", c1.label);

                    assertEquals("https://www.pantip.com", c1.linkURL);

                    /**
                     * Full Option
                     */
                    assertTrue(c2.isImageLinkLabel());

                    assertEquals("https://logo.JPG", c2.imageURL);

                    assertEquals("Web Manager", c2.label);

                    assertEquals("https://www.manager.com", c2.linkURL);

                    /**
                     * Image same as Link
                     */
                    assertTrue(c3.isImageLinkLabel());

                    assertEquals("https://wayologo.png", c3.imageURL);

                    assertEquals("Web Wayobot", c3.label);

                    assertEquals("https://wayologo.png", c3.linkURL);

                    /**
                     * Null Link
                     */
                    assertTrue(c4.isImageLabel());

                    assertEquals("https://zoo.jpeg", c4.imageURL);

                    assertEquals("Web Zoo", c4.label);

                    assertNull(c4.linkURL);

                    /**
                     * Null Image & Link
                     */
                    assertTrue(c5.isLabel());

                    assertNull(c5.imageURL);

                    assertEquals("Web XMen", c5.label);

                    assertNull(c5.linkURL);
                    return;
                }

            }
        });
        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject,"ใส่ข้อมูลถามตอบ\n" +
                "Q: hello\n" +
                "A: what is your fav web?, Web?\n" +
                "Q: Web Pantip\n" +
                "A: wow https://www.pantip.com\n" +
                "Q: Web Manager\n" +
                "A: https://logo.JPG wow haha ha https://www.manager.com \n" +
                "Q: Web Wayobot\n" +
                "A: https://wayologo.png \n" +
                "Q: Web Zoo\n" +
                "A: https://zoo.jpeg zoolander \n" +
                "Q: Web XMen\n" +
                "A: xmen \n"
        )));

        assertEquals("what is your fav web?", session.parse(MessageObject.build(messageObject,"hello")));

    }

    @Test
    public void similarTypeTest() {

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new MemoryContext("qa");
        context.admin(adminIdList);
        Session session = new Session(context).callback(new SessionListener() {
            @Override
            public void callback(NodeEvent nodeEvent) {
                if (nodeEvent.event == NodeEvent.Event.Question) {

                    List<AnswerResponseCommandNode.Question> questionList = (List<AnswerResponseCommandNode.Question>) nodeEvent.messageObject.attributes.get("Question");

                    List<AnswerResponseCommandNode.Choice> choices = questionList.get(0).choices;

                    assertNotNull(choices);

                    //assertEquals(2, choices.size());

                    //assertEquals(3, choices.size());

                    return;
                }

            }
        });

        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject, "ใส่ข้อมูลถามตอบ\n" +
                "Q: hello\n" +
                "A: what is your fav web?, rr?\n" +
                "Q: hi\n" +
                "A: what is your fav web?, rrr?\n" +
                "Q: rr Pantip\n" +
                "A: wow https://www.pantip.com\n" +
                "Q: rr Manager\n" +
                "A: https://logo.JPG wow haha ha https://www.manager.com \n" +
                "Q: rrr Wayobot\n" +
                "A: https://wayologo.png \n" +
                "Q: rrr Zoo\n" +
                "A: https://zoo.jpeg zoolander \n" +
                "Q: rrr XMen\n" +
                "A: xmen \n"

        )));

        assertEquals("what is your fav web?", session.parse(MessageObject.build(messageObject, "hello")));

        session.clearProblem();

        assertEquals("what is your fav web?", session.parse(MessageObject.build(messageObject, "hi")));

    }

    @Test
    public void multiQuestionTest() {

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new MemoryContext("qa");
        context.admin(adminIdList);
        Session session = new Session(context).callback(new SessionListener() {
            @Override
            public void callback(NodeEvent nodeEvent) {
                if (nodeEvent.event == NodeEvent.Event.Question) {

                    List<AnswerResponseCommandNode.Question> questionList = (List<AnswerResponseCommandNode.Question>) nodeEvent.messageObject.attributes.get("Question");

                    assertEquals(2, questionList.size());

                    return;
                }

            }
        });

        new WakeupCommandNode(session).execute(null);

        MessageObject messageObject = MessageObject.build();
        messageObject.attributes.put("userId", "Uee73cf96d1dbe69a260d46fc03393cfd");

        assertEquals("Done!", session.parse(MessageObject.build(messageObject, "ใส่ข้อมูลถามตอบ\n" +
                "Q: hello\n" +
                "A: what is your fav web?, rr?\n" +
                "Q: hello\n" +
                "A: what is your fav web?, rrr?\n" +
                "Q: rr Pantip\n" +
                "A: wow https://www.pantip.com\n" +
                "Q: rr Manager\n" +
                "A: https://logo.JPG wow haha ha https://www.manager.com \n" +
                "Q: rrr Wayobot\n" +
                "A: https://wayologo.png \n" +
                "Q: rrr Zoo\n" +
                "A: https://zoo.jpeg zoolander \n" +
                "Q: rrr XMen\n" +
                "A: xmen \n"

        )));

        assertEquals("Cousural", session.parse(MessageObject.build(messageObject, "hello")));

    }

}