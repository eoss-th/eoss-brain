package com.brainy;

import com.brainy.net.Node;

public class NodeEvent {

    public enum Event {
        @Deprecated SuperConfidence,
        @Deprecated HesitateConfidence,
        @Deprecated LowConfidence,
        @Deprecated LateReply,
        Leave,
        RegisterAdmin,
        Recursive,
        Matched,
        Wakeup,
        NewNodeAdded,
        ContextSaved,
        ReservedWords,
        Authentication,
        @Deprecated Question
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
