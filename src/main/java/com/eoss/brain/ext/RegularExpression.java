package com.eoss.brain.ext;

import com.eoss.brain.Session;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularExpression extends Expression {

    public RegularExpression(Session session, String[] arguments) {
        super(session, arguments);
    }

    @Override
    public String execute(String input) {

        String [] args = parameterized(input, arguments);

        if (args.length==3) {

            String text = args[1].replace("`", "");
            return regx(text, args[2]);
        }

        return super.execute(input);

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
