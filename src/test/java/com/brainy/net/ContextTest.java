package com.brainy.net;

import com.brainy.context.MemoryContext;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

public class ContextTest {

    @Test
    public void splitTest() {

        Context context = new MemoryContext("qa").locale(new Locale("th"));

        String [] tokens = context.split("สวัสดีครับคุณครู");
        assertEquals(5, tokens.length);
        assertEquals("สวัสดี", tokens[0]);
        assertEquals("ครับ", tokens[1]);
        assertEquals("คุณ", tokens[2]);
        assertEquals( "ครู", tokens[3]);
        assertEquals( "สวัสดีครับคุณครู", tokens[4]);

    }

}