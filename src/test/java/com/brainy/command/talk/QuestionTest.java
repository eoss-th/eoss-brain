package com.brainy.command.talk;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class QuestionTest {

    @Test
    public void toString1() {

        String id = "@123";

        List<Choice> choiceList = new ArrayList<>();

        choiceList.add(new Choice(id, "A", "https://a.jpg", "https://a.html"));
        choiceList.add(new Choice(id, "B", null, "https://b.html"));
        choiceList.add(new Choice(id, "C", "https://c.jpg", null));
        choiceList.add(new Choice(id, "D", null, null));

        Question question = new Question(id, "How are you?", "https://wayobot.com/image.png", choiceList);

        System.out.println(question);

        Question q = Question.build("Question: How are you?\n" +
                "Id: @123\n" +
                "Image: https://wayobot.com/image.png\n" +
                "\n" +
                "\tA\thttps://a.jpg\thttps://a.html\n" +
                "\tB\t\thttps://b.html\n" +
                "\tC\thttps://c.jpg\t\n" +
                "\tD\t");

        System.out.println(q);
    }
}