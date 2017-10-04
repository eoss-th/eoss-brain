package com.eoss.brain.command;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.command.talk.TalkCommandNode;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class ForwardCommandNode extends CommandNode {

    public ForwardCommandNode(Session session, String [] hooks) {
        super(session, hooks, Mode.MatchWhole);
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
            response = lastActiveEntry.response.text;
            if (!response.equals("?"))
                return new TalkCommandNode(session).execute(MessageObject.build(messageObject, response));
        }

        return response;
    }

}
