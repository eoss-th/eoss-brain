package com.eoss.brain.command;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.net.Node;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public abstract class CommandNode extends Node {

    public final Session session;

    public CommandNode(Session session) {
        this (session, null);
    }

    public CommandNode(Session session, String [] hooks) {
        this(session, hooks, Mode.MatchWhole);
    }

    public CommandNode(Session session, String [] hooks, Mode mode) {
        super(hooks, mode);
        this.session = session;
    }

    public abstract String execute(MessageObject messageObject);

    protected String successMsg() {
        return "Done!";
    }

    protected String failMsg() {
        return "Fail!";
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

}
