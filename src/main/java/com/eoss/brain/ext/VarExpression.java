package com.eoss.brain.ext;

import com.eoss.brain.MessageObject;
import com.eoss.brain.Session;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

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

    public static boolean isNumeric(String strNum, Locale locale) {
        try {
            NumberFormat formatter = NumberFormat.getInstance(locale);
            formatter.parse(strNum);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private String optValue(String name, String newValue) {

        String oldValue = session.getVariable("#" + name);

        /**
         * Check there is prefix operand or not?
         */
        if (newValue.startsWith("+") || newValue.startsWith("-") || newValue.startsWith("*") || newValue.startsWith("/") || newValue.startsWith("^")) {

            try {
                String opt = newValue.substring(0, 1);
                newValue = newValue.substring(1).trim();


                /**
                 * If new variable, just set
                 */
                if (oldValue==null) return newValue;

                NumberFormat formatter = NumberFormat.getInstance(session.context.locale());

                if (isNumeric(oldValue, session.context.locale()) && isNumeric(newValue, session.context.locale())) {

                    /**
                     * Supports + - * /
                     */

                    double oldNumber = formatter.parse(oldValue).doubleValue();
                    double newNumber = formatter.parse(newValue).doubleValue();

                    if (opt.equals("+"))
                        return formatter.format(oldNumber + newNumber);

                    if (opt.equals("-"))
                        return formatter.format(oldNumber - newNumber);

                    if (opt.equals("*"))
                        return formatter.format(oldNumber * newNumber);

                    if (opt.equals("/"))
                        return formatter.format(oldNumber / newNumber);

                    if (opt.equals("^"))
                        return formatter.format(Math.pow(oldNumber, newNumber));

                } else if (isNumeric(oldValue, session.context.locale())) {

                    /**
                     * Support + - *
                     */

                    if (opt.equals("+"))
                        return oldValue + newValue;

                    if (opt.equals("-"))
                        return oldValue.replace(newValue, "");

                    if (opt.equals("*")) {
                        double oldNumber = formatter.parse(oldValue).doubleValue();
                        int round = (int) Math.round(oldNumber);
                        StringBuilder result = new StringBuilder();
                        for (int i=0;i<round;i++) {
                            result.append(newValue);
                            result.append(System.lineSeparator());
                        }
                        return result.toString().trim();
                    }

                    return newValue;

                } else if (isNumeric(newValue, session.context.locale())) {

                    /**
                     * Support + - *
                     */

                    if (opt.equals("+"))
                        return oldValue + newValue;

                    if (opt.equals("-"))
                        return oldValue.replace(newValue, "");

                    if (opt.equals("*")) {
                        double newNumber = formatter.parse(newValue).doubleValue();
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
            } catch (ParseException e) {
              e.printStackTrace();
            }

        }

        return newValue;
    }
}
