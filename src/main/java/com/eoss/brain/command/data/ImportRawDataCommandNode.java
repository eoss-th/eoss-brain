package com.eoss.brain.command.data;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.net.Node;
import com.eoss.brain.command.CommandNode;
import com.eoss.brain.net.Context;
import com.eoss.util.GAEWebStream;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class ImportRawDataCommandNode extends CommandNode {

    public ImportRawDataCommandNode(Session session, String [] hooks) {
        super(session, hooks, Mode.MatchHead);
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

            List<String> sentenceList = new ArrayList<>();
            String line;
            while ((line = br.readLine())!=null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (anotherContextName==null && line.startsWith("#")) {
                    anotherContextName = line.substring(1);
                } else {
                    sentenceList.add(line);
                }
            }

            Node newNode = null;

            for (String sentence:sentenceList) {

                if (newNode!=null) {
                    newNode.addResponse(sentence);
                    session.context.add(newNode);
                }

                newNode = new Node(session.context.splitToList(sentence).toArray(new String[0]), null);
            }

        } finally {
            try { br.close(); } catch (Exception e) {}
        }

        return anotherContextName;
    }

}
