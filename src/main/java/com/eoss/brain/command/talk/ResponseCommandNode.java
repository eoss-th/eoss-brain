package com.eoss.brain.command.talk;

import com.eoss.brain.MessageObject;
import com.eoss.brain.Session;
import com.eoss.brain.ext.Expression;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResponseCommandNode extends ProblemCommandNode {

    public final String responseText;

    public ResponseCommandNode(Session session, String responseText) {
        super(session);
        this.responseText = responseText;
    }

    public static ResponseCommandNode build(Session session, String responseText) {
        if (responseText.endsWith("?")) {
            return new QuestionResponseCommandNode(session, responseText.substring(0, responseText.length()-1));
        }
        if (responseText.endsWith("!")) {
            return new ForwardResponseCommandNode(session, responseText.substring(0, responseText.length()-1));
        }
        return new ResponseCommandNode(session, responseText);
    }

    @Override
    public String execute(MessageObject messageObject) {

        /**
         * ## #1 #2..
         */
        Map<String, String> paramMap = paramMap(messageObject);
        String parameterizedText = parameterized(paramMap, responseText);

        /**
         * Session Variables
         */
        parameterizedText = parameterized(session.variableMap, parameterizedText);

        Pattern pattern = Pattern.compile("\\(.*?\\)");
        Matcher matcher = pattern.matcher(parameterizedText);

        String expression;
        while (matcher.find()) {
            expression = matcher.group();
            parameterizedText = parameterizedText.replace(expression, Expression.build(session, expression).execute());
        }

        return parameterizedText.trim();
    }

    protected final Map<String, String> paramMap(MessageObject messageObject) {

        String input = messageObject.toString();

        String [] params = session.context.split(input);

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("##", input);

        for (int i=0;i<params.length;i++) {
            paramMap.put("#" + (i+1), params[i]);
        }

        return paramMap;
    }

    protected final String parameterized(Map<String, String> paramMap, String text) {
        String output = text;
        for (Map.Entry<String, String> entry:paramMap.entrySet()) {
            output = output.replace(entry.getKey(), entry.getValue());
        }
        return output;
    }
}
