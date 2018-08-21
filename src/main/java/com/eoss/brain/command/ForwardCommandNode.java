package com.eoss.brain.command;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.command.talk.TalkCommandNode;
import com.eoss.brain.net.Hook;

import java.util.List;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class ForwardCommandNode extends CommandNode {

    List<String> lowConfidenceKeys;

    public ForwardCommandNode(Session session, String [] hooks, List<String> lowConfidenceKeys) {
        super(session, hooks, Hook.Match.All);
        this.lowConfidenceKeys = lowConfidenceKeys;
    }

    @Override
    public boolean matched(MessageObject messageObject) {
        return super.matched(messageObject) && session.lastEntry() != null;
    }

    @Override
    public String execute(MessageObject messageObject) {

        //Recall
        String response="";

        if (!session.hasProblem()) {
            session.clearPool();
            Session.Entry lastActiveEntry = session.lastEntry();
            response = lastActiveEntry.node.response();
            if (!response.equals("?"))
                return new TalkCommandNode(session, lowConfidenceKeys).execute(MessageObject.build(messageObject, response));
        }

        return response;
    }

}
