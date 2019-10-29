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
        int lastIndexOfComma = generatedOutput.lastIndexOf(", ");
        if ( lastIndexOfComma!=-1 && lastIndexOfComma<generatedOutput.length()-1 ) {
            forwardMessage = generatedOutput.substring(lastIndexOfComma + 1).trim();
            String previousMessage = generatedOutput.substring(0, lastIndexOfComma);
            generatedOutput = previousMessage.isEmpty()?"":previousMessage;
        } else {
            generatedOutput = "";
        }

        if (!generatedOutput.isEmpty()) {
            generatedOutput += ", ";
        }

        MessageObject forwardMessageObject = MessageObject.build(messageObject, forwardMessage);
        forwardMessageObject.split();
        return generatedOutput + new TalkCommandNode(session, WakeupCommandNode.KEY).execute(forwardMessageObject);
    }
}
