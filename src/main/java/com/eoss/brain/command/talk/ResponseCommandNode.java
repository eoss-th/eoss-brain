package com.eoss.brain.command.talk;

import com.eoss.brain.MessageObject;
import com.eoss.brain.Session;
import com.eoss.brain.ext.Expression;

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

        Pattern pattern = Pattern.compile("\\`.*?\\`");
        Matcher matcher = pattern.matcher(responseText);

        String evaluatedText = responseText;
        String expression;
        while (matcher.find()) {
            expression = matcher.group();
            evaluatedText = evaluatedText.replace(expression, Expression.build(session, expression).execute(messageObject));
        }

        return session.parameterized(messageObject, evaluatedText);
    }

}
