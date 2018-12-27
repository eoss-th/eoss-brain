package com.eoss.brain.ext;

import com.eoss.brain.Session;

public class VarExpression extends Expression {

    public VarExpression(Session session, String[] arguments) {
        super(session, arguments);
    }

    @Override
    public String execute(String input) {
        return var(parameterized(input, arguments)[0]);
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
