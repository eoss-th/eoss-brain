package com.eoss.brain.command.data;

import com.eoss.brain.MessageObject;
import com.eoss.brain.Session;
import com.eoss.brain.command.CommandNode;
import com.eoss.brain.net.Hook;
import com.eoss.brain.net.Node;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class ExportRawDataCommandNode extends CommandNode {

    public ExportRawDataCommandNode(Session session, String [] hooks) {
        super(session, hooks);
    }

    @Override
    public String execute(MessageObject messageObject) {

        StringBuilder sb = new StringBuilder();
        String lastInput;
        String lastResponse = null;

        /**
         * A
         * B
         * B
         * C
         * C
         * D
         * ..
         */
        for (Node node:session.context.nodeList) {
            lastInput = Hook.toString(node.hookList());
            if (lastResponse==null || !Hook.toString(Hook.build(session.context.split(lastResponse))).equals(lastInput)) {
                sb.append(lastInput);
                sb.append(System.lineSeparator());
            }
            lastResponse = node.response();
            sb.append(lastResponse);
            sb.append(System.lineSeparator());
        }

        return sb.toString().trim();
    }
}
