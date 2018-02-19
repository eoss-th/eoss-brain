package com.eoss.brain;

import com.eoss.brain.net.Node;

public class NodeEvent {

    public enum Event {
        SuperConfidence,
        LowConfidence,
        LateReply,
        Leave,
        RegisterAdmin,
        Recursive,
        Matched,
        Wakeup,
        ReservedWords
    }

    public final Node node;
    public final Event event;
    public final MessageObject messageObject;

    public NodeEvent(Node node, MessageObject messageObject, Event event) {
        this.node = node;
        this.messageObject = messageObject;
        this.event = event;
    }
}
