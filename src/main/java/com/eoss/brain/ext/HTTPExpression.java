package com.eoss.brain.ext;

import com.eoss.brain.Session;

import java.util.HashMap;
import java.util.Map;

public abstract class HTTPExpression extends Expression {

    public HTTPExpression(Session session, String[] arguments) {
        super(session, arguments);
    }

    protected final Map<String, String> createParamMapFromQueryString(String queryString) {
        Map<String, String> paramMap = new HashMap<>();
        String [] headers = queryString.split("&");
        String [] keyValue;
        for (String header:headers) {
            keyValue = header.split("=");
            if (keyValue.length!=2) continue;
            paramMap.put(keyValue[0], keyValue[1]);
        }
        return paramMap;
    }

}
