package com.eoss.brain.ext;

import com.eoss.brain.MessageObject;
import com.eoss.brain.Session;

public class VarExpression extends Expression {

    public VarExpression(Session session, String[] arguments) {
        super(session, arguments);
    }

    @Override
    public String execute(MessageObject messageObject) {
        return var(parameterized(messageObject, arguments)[0]);
    }

    protected final String var(String queryParams) {
        String [] params = queryParams.split("&");
        String [] tokens;
        for (String paramValue:params) {
            tokens = paramValue.split("=");
            if (tokens.length!=2) continue;
            session.setVariable("#" + tokens[0], tokens[1]);
        }
        return "";
    }
}
