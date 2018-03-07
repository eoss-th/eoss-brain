package com.eoss.brain;

import com.eoss.brain.net.Node;

public class NodeEvent {

    public enum Event {
        SuperConfidence,
        HesitateConfidence,
        LowConfidence,
        LateReply,
        Leave,
        RegisterAdmin,
        Recursive,
        Matched,
        Wakeup,
        NewNodeAdded,
        ContextSaved,
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
