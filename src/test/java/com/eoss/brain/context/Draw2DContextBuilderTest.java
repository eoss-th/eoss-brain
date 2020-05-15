package com.eoss.brain.context;

import com.eoss.brain.net.Context;
import org.junit.Test;

import static org.junit.Assert.*;

public class Draw2DContextBuilderTest {

    @Test
    public void buildTest() {

        Context context = new FileContext("programatic");

        Draw2DContextBuilder builder = new Draw2DContextBuilder(context, "Test", 100);

        Draw2DContextBuilder.Entity hello = builder.newEntity(new Draw2DContextBuilder.Entity[] {builder.GREETING, builder.UNKNOWN}, "Hello", "Test", false);

        for (int i=1;i<=10;i++) {

            Draw2DContextBuilder.Entity question = builder.newEntity(new Draw2DContextBuilder.Entity[] {hello}, "Question " + i, "Label " + i, true);

            Draw2DContextBuilder.Entity answer = builder.newEntity(new Draw2DContextBuilder.Entity[] {question}, "Go", "https://www." + i +".com", false);

        }

        context.save();

    }

}