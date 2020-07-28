package com.brainy.command.talk.menu;

import com.brainy.command.wakeup.WakeupCommandNode;
import com.brainy.MessageObject;
import com.brainy.Session;
import com.brainy.command.talk.ResponseCommandNode;

public class MenuForwardResponseCommandNode extends ResponseCommandNode {

    public MenuForwardResponseCommandNode(Session session, String question) {
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

        messageObject.setText(generatedOutput);

        return messageObject.headIncluded() + new MenuTalkCommandNode(session, WakeupCommandNode.KEY).execute(messageObject.forward().split());

        //Insert Message
        /*
        String forwardMessage = generatedOutput;

        int lastIndexOfComma = generatedOutput.lastIndexOf(", ");
        if ( lastIndexOfComma!=-1 && lastIndexOfComma<generatedOutput.length()-1 ) {
            forwardMessage = generatedOutput.substring(lastIndexOfComma + 1).trim();
            String previousMessage = generatedOutput.substring(0, lastIndexOfComma);
            generatedOutput = previousMessage.isEmpty()?"":previousMessage;
        }

        MessageObject forwardMessageObject = MessageObject.build(messageObject, forwardMessage);
        forwardMessageObject.split();
        return generatedOutput + "\n\n\n" + new MenuTalkCommandNode(session, WakeupCommandNode.KEY).execute(forwardMessageObject);
        */
    }
}
