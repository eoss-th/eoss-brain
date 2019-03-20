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
        String name, value;
        for (String paramValue:params) {
            if (!paramValue.contains("=")) continue;
            tokens = paramValue.split("=");
            name = tokens[0].trim();
            if (paramValue.trim().endsWith("=")) {
                session.removeVariable("#" + name);
                continue;
            }
            if (tokens.length!=2) continue;
            value = tokens[1].trim();
            session.setVariable("#" + name, value);
        }
        return "";
    }
}
