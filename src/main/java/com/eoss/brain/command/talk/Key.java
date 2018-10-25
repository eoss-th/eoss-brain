package com.eoss.brain.command.talk;

import java.util.List;

public class Key {

    public final String doneMsg;
    public final String questMsg;
    public final List<String> cancelKeys;

    public Key(String doneMsg, String questMsg, List<String> cancelKeys) {
        this.doneMsg = doneMsg;
        this.questMsg = questMsg;
        this.cancelKeys = cancelKeys;
    }
}
