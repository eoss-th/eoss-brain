package com.brainy.ext;

import com.brainy.MessageObject;
import com.brainy.Session;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularExpression extends Expression {

    public RegularExpression(Session session, String[] arguments) {
        super(session, arguments);
    }

    @Override
    public String execute(MessageObject messageObject) {

        String [] args = parameterized(messageObject, arguments);

        if (args.length==3) {

            String text = args[1].replace("`", "");
            return regx(text, args[2]);
        }

        return super.execute(messageObject);

    }

    private String regx(String input, String patternString) {

        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(input);

        StringBuilder result = new StringBuilder();
        String matchedString;
        while (matcher.find()) {
            matchedString = matcher.group();
            result.append(matchedString);
        }

        return result.toString().trim();
    }
}
