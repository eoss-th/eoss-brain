package com.eoss.brain.ext;

import com.eoss.brain.Session;
import com.jayway.jsonpath.JsonPath;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class JSONExpression extends Expression {

    public JSONExpression(Session session, String[] arguments) {
        super(session, arguments);
    }

    @Override
    public String execute() {

        if (arguments.length==3) {
            String jsonString = arguments[1].replace("`", "");
            return jsonPath(jsonString, arguments[2]);
        }

        return super.execute();
    }

    protected final String jsonPath(String json, String path) {

        try {
            Object result = JsonPath.parse(json).read(path);
            if (result instanceof List) {

                List list = (List)result;
                StringBuilder sb = new StringBuilder();

                Map<String, Object> map;
                Collection values;
                for (Object obj:list) {
                    if (obj instanceof String) {
                        sb.append(obj + " ");
                    }

                    if (obj instanceof Map) {
                        map = (Map<String, Object>) obj;
                        values = map.values();
                        for (Object o:values) {
                            sb.append(o + " ");
                        }
                    }
                }
                return sb.toString().trim();
            }

            if (result instanceof String) {
                return (String) result;
            }

            return result.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }
}
