package com.brainy.context;

import com.brainy.net.Context;
import com.brainy.net.Node;

import java.util.List;

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
