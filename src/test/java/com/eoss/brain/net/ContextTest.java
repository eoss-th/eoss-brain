package com.eoss.brain.net;

import com.eoss.brain.context.MemoryContext;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

public class ContextTest {

    @Test
    public void splitTest() {

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new MemoryContext("qa").locale(new Locale("th"));
        context.admin(adminIdList);

        String [] tokens = context.split("`{\"test\":{['abc']}}`สวัสดีครับคุณครู`{\"test\":{['abc']}}``{\"test\":{['abc']}}`");
        assertEquals(7, tokens.length);
        assertEquals("สวัสดี", tokens[0]);
        assertEquals("ครับ", tokens[1]);
        assertEquals("คุณ", tokens[2]);
        assertEquals( "ครู", tokens[3]);
        assertEquals("`{\"test\":{['abc']}}`", tokens[4]);
        assertEquals("`{\"test\":{['abc']}}`", tokens[5]);
        assertEquals("`{\"test\":{['abc']}}`", tokens[6]);

    }

}