package com.eoss.brain.command.data;

import com.eoss.brain.Session;
import com.eoss.brain.command.line.BizWakeupCommandNode;
import com.eoss.brain.net.Context;
import com.eoss.brain.net.GAEStorageContext;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Created by eossth on 8/29/2017 AD.
 */
public class LoadDataCommandNodeTest {
    @Test
    public void execute() throws Exception {
        Context.setLocale(new Locale("th", "TH"));

        List<String> adminIdList = Arrays.asList("Uee73cf96d1dbe69a260d46fc03393cfd");
        Context context = new GAEStorageContext("test", null);
        context.admin(adminIdList);
        Session session = new Session(context);

        assertEquals(0, context.dataSet.size());
        new BizWakeupCommandNode(session).execute(null);
        assertTrue(context.dataSet.size() > 0);

    }

}