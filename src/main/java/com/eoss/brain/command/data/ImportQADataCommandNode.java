package com.eoss.brain.command.data;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.net.Node;
import com.eoss.brain.command.CommandNode;
import com.eoss.brain.net.Context;
import com.eoss.util.GAEWebStream;

import java.io.BufferedReader;
import java.io.StringReader;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class ImportQADataCommandNode extends CommandNode {

    public final String qKey;

    public final String aKey;

    public ImportQADataCommandNode(Session session, String [] hooks, String qKey, String aKey) {
        super(session, hooks, Mode.MatchHead);
        this.qKey = qKey;
        this.aKey = aKey;
    }

    @Override
    public String execute(MessageObject messageObject) {

        try {

            String anotherContextName = load(MessageObject.build(messageObject, clean(messageObject.toString())));

            if (anotherContextName==null)
                session.context.save();
            else {
                session.context.save(anotherContextName);
                GAEWebStream gaeWebStream = new GAEWebStream(session.context.name + ".index");
                String indexData = gaeWebStream.read();
                if (!indexData.contains(anotherContextName)) {
                    gaeWebStream.write(indexData + anotherContextName + System.lineSeparator());
                }
            }

            return successMsg();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return failMsg();
    }

    String load(MessageObject messageObject) throws Exception {

        String anotherContextName = null;
        StringReader reader = new StringReader(messageObject.toString());
        BufferedReader br = new BufferedReader(reader);
        try {
            String line;
            StringBuilder sb = new StringBuilder();
            Node newNode = null;
            while (true) {
                line = br.readLine();
                if (line==null) break;
                line = line.trim();
                if (line.isEmpty()) continue;
                if (anotherContextName==null && line.startsWith("#")) {
                    anotherContextName = line.substring(1);
                }
                if (line.startsWith(qKey)) {
                    if (!sb.toString().trim().isEmpty()) {
                        if (newNode!=null)
                            newNode.addResponse(sb.toString().trim());
                    }
                    sb = new StringBuilder(line.replace(qKey, ""));
                } else if (line.startsWith(aKey)) {
                    if (!sb.toString().trim().isEmpty()) {
                        newNode = new Node(session.context.splitToList(sb.toString().trim()).toArray(new String[0]), null);
                        session.context.add(newNode);
                    }
                    sb = new StringBuilder(line.replace(aKey, ""));
                } else {
                    sb.append(line+System.lineSeparator());
                }
            }

            if (!sb.toString().isEmpty()) {
                if (newNode!=null)
                    newNode.addResponse(sb.toString().trim());
            }

        } finally {
            try { br.close(); } catch (Exception e) {}
        }

        return anotherContextName;
    }
}
