package com.eoss.brain.command.talk;

import com.eoss.brain.MessageObject;
import com.eoss.brain.Session;
import com.eoss.brain.command.wakeup.WakeupCommandNode;

public class ForwardResponseCommandNode extends ResponseCommandNode {

    public ForwardResponseCommandNode(Session session, String question) {
        super(session, question);
    }

    @Override
    public boolean matched(MessageObject messageObject) {
        session.solved(true);
        return true;
    }

    @Override
    public String execute(MessageObject messageObject) {

        String generatedOutput = super.execute(messageObject);

        //Insert Message
        String forwardMessage = generatedOutput;
        int lastIndexOfComma = generatedOutput.lastIndexOf(",");
        if ( lastIndexOfComma!=-1 && lastIndexOfComma<generatedOutput.length()-1 ) {
            forwardMessage = generatedOutput.substring(lastIndexOfComma + 1).trim();
            generatedOutput = generatedOutput.substring(0, lastIndexOfComma) + ", ";
        } else {
            generatedOutput = "";
        }

        MessageObject forwardMessageObject = MessageObject.build(messageObject, forwardMessage);

        return generatedOutput + new TalkCommandNode(session, WakeupCommandNode.KEY).execute(forwardMessageObject);
    }
}
