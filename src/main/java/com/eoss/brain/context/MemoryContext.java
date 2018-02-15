package com.eoss.brain.context;

import com.eoss.brain.net.Context;
import com.eoss.brain.net.Node;

import java.util.List;
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
    public void doSave(String name, List<Node> nodeList) {
    }

}
