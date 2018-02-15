package com.eoss.brain.context;

import com.eoss.brain.context.WebContext;
import com.eoss.brain.net.Context;
import org.junit.Test;

/**
 * Created by eossth on 9/4/2017 AD.
 */
public class WebContextTest {
    @Test
    public void doLoad() throws Exception {
        Context context = new WebContext("http://www.sanook.com/men/13177/");
        context.load();
    }

}