package com.brainy.context;

import com.brainy.MessageObject;
import com.brainy.net.Context;
import com.brainy.net.ContextListener;
import com.brainy.net.Node;
import com.brainy.util.GAEWebStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GAEWebIndexSupportContext extends Context {

    private Context masterContext;

    private GAEWebStream gaeWebConfig;

    private List<Context> supportContextList;

    public GAEWebIndexSupportContext(Context context) {
        super(context.name);
        masterContext = context;
        gaeWebConfig = new GAEWebStream(context.name + ".index");
        supportContextList = new ArrayList<>();
    }

    @Override
    public Set<Node> feed(MessageObject messageObject) {
        Set<Node> activeNode = masterContext.feed(messageObject);
        for (Context supportContext:supportContextList) {
            activeNode.addAll(supportContext.feed(messageObject));
        }
        return activeNode;
    }

    @Override
    public Set<Node> feed(MessageObject messageObject, float matchedScore) {
        Set<Node> activeNode = masterContext.feed(messageObject, matchedScore);
        for (Context supportContext:supportContextList) {
            activeNode.addAll(supportContext.feed(messageObject, matchedScore));
        }
        return activeNode;
    }

    @Override
    public boolean matched(MessageObject messageObject, ContextListener listener) {

        /*
        if (!masterContext.matched(messageObject, listener)) {
            for (Context context: supportContextList) {
                if (context.matched(messageObject, listener)) return true;
            }
        }

        return false;
        */

        boolean matched;
        matched = masterContext.matched(messageObject, listener);
        for (Context context : supportContextList) {
            matched |= context.matched(messageObject, listener);
        }

        return matched;
    }

    @Override
    protected void doLoad(String name) throws Exception {}

    @Override
    protected void doSave(String name, List<Node> nodeList) {}

    @Override
    public void load() throws Exception {
        masterContext.load();

        String [] lines = gaeWebConfig.read().split(System.lineSeparator());

        Context context;
        for (String line:lines) {
            context = new GAEStorageContext(line);
            supportContextList.add(context);
            context.load();
        }
    }

    @Override
    public void load(String name) throws Exception {
        masterContext.load(name);
    }

    @Override
    public void save() {
        masterContext.save();
    }

    @Override
    public void save(String name) {
        masterContext.save(name);
    }

    @Override
    public void clear() {
        masterContext.clear();
    }

    @Override
    public boolean isEmpty() {
        return masterContext.isEmpty();
    }

    @Override
    public void add(Node newNode) {
        masterContext.add(newNode);
    }

    @Override
    public String toString() {
        return masterContext.toString();
    }

}
