package com.brainy.command.data;

import com.brainy.Session;
import com.brainy.command.wakeup.WakeupCommandNode;
import com.brainy.context.FileContext;
import com.brainy.net.Context;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by eossth on 8/29/2017 AD.
 */
public class LoadDataCommandNodeTest {
    @Test
    public void execute() throws Exception {

        Context context = new FileContext("filetest");
        Session session = new Session(context);

        assertEquals(0, context.nodeList.size());
        new WakeupCommandNode(session).execute(null);
        assertTrue(context.nodeList.size() > 0);

    }

}