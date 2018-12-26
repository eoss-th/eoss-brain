package com.eoss.brain.ext;

import com.eoss.brain.Session;

import java.util.HashMap;
import java.util.Map;

public abstract class HTTPExpression extends Expression {

    public HTTPExpression(Session session, String[] arguments) {
        super(session, arguments);
    }

    protected final String protectHTTPObject(String text) {
        if (
                (text.startsWith("<") && text.endsWith(">")) || //DOM
                        (text.startsWith("{") && text.endsWith("}")) || //JSON Object
                        (text.startsWith("[") && text.endsWith("]")) // JSON Array
        ) {
            return "`" + text + "`";
        }
        return text;
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
