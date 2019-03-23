package com.eoss.brain;

import org.junit.Test;

import static org.junit.Assert.*;

public class MessageObjectTest {

    @Test
    public void splitTest() {

        MessageObject messageObject = MessageObject.build("@555 Hello world @123");
        messageObject.split();
        assertEquals(4, messageObject.attributes.get("wordCount"));
    }

}