package com.eoss.brain.ext;

import com.eoss.brain.MessageObject;
import com.eoss.brain.Session;

import java.text.DecimalFormat;
import java.text.NumberFormat;

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
            session.setVariable("#" + name, optValue(name, value));
        }
        return "";
    }

    public static boolean isNumeric(String strNum) {
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

    private String optValue(String name, String newValue) {

        String oldValue = session.getVariable("#" + name);

        /**
         * Check there is prefix operand or not?
         */
        if (newValue.startsWith("+") || newValue.startsWith("-") || newValue.startsWith("*") || newValue.startsWith("/")) {

            String opt = newValue.substring(0, 1);
            newValue = newValue.substring(1).trim();


            /**
             * If new variable, just set
             */
            if (oldValue==null) return newValue;

            NumberFormat formatter = NumberFormat.getInstance(session.context.locale());

            if (isNumeric(oldValue) && isNumeric(newValue)) {

                /**
                 * Supports + - * /
                 */

                double oldNumber = Double.parseDouble(oldValue);
                double newNumber = Double.parseDouble(newValue);

                if (opt.equals("+"))
                    return formatter.format(oldNumber + newNumber);

                if (opt.equals("-"))
                    return formatter.format(oldNumber - newNumber);

                if (opt.equals("*"))
                    return formatter.format(oldNumber * newNumber);

                if (opt.equals("/"))
                    return formatter.format(oldNumber / newNumber);

            } else if (isNumeric(oldValue)) {

                /**
                 * Support + - *
                 */

                if (opt.equals("+"))
                    return oldValue + newValue;

                if (opt.equals("-"))
                    return oldValue.replace(newValue, "");

                if (opt.equals("*")) {
                    double oldNumber = Double.parseDouble(oldValue);
                    int round = (int) Math.round(oldNumber);
                    StringBuilder result = new StringBuilder();
                    for (int i=0;i<round;i++) {
                        result.append(newValue);
                        result.append(System.lineSeparator());
                    }
                    return result.toString().trim();
                }

                return newValue;

            } else if (isNumeric(newValue)) {

                /**
                 * Support + - *
                 */

                if (opt.equals("+"))
                    return oldValue + newValue;

                if (opt.equals("-"))
                    return oldValue.replace(newValue, "");

                if (opt.equals("*")) {
                    double newNumber = Double.parseDouble(newValue);
                    int round = (int) Math.round(newNumber);
                    StringBuilder result = new StringBuilder();
                    for (int i=0;i<round;i++) {
                        result.append(oldValue);
                        result.append(System.lineSeparator());
                    }
                    return result.toString().trim();
                }

                return newValue;

            } else {

                /**
                 * Only support +
                 */
                if (opt.equals("+"))
                    return oldValue + newValue;

                return newValue;
            }

        }

        return newValue;
    }
}
