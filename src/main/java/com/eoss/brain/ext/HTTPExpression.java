package com.eoss.brain.ext;

import com.eoss.brain.MessageObject;
import com.eoss.brain.Session;

import java.util.HashMap;
import java.util.Map;

public abstract class HTTPExpression extends Expression {

    private final String unsecureURLOption = "-nonsecure";

    public HTTPExpression(Session session, String[] arguments) {
        super(session, arguments);
    }

    protected final Map<String, String> signatureMap(MessageObject messageObject) {
        Map<String, String> signatureMap = new HashMap<>();

        String signatureType = (String) messageObject.attributes.get("signatureType");
        if (signatureType!=null) {
            String signatureValue = (String) messageObject.attributes.get("signatureValue");
            signatureMap.put(signatureType, signatureValue);
        }

        return signatureMap;
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

    protected final String unsecureURLSupport(String uri) {

        /**
         * Convert to http and remove unsecure option
         */
        if (uri.endsWith(unsecureURLOption)) {
            return "http://" + uri.replace(unsecureURLOption, "");
        }

        return "https://" + uri;
    }

}
