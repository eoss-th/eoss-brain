package com.eoss.brain.net;

import java.util.Set;

/**
 * Created by eoss-th on 8/15/17.
 */
public class MemoryContext extends Context {

    public MemoryContext(String name) {
        super(name);
    }

    @Override
    public void doLoad(String name) throws Exception {
    }

    @Override
    public void doSave(String name, Set<Node> dataSet) {
    }

}
