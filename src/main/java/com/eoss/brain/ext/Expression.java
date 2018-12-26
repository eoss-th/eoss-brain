package com.eoss.brain.ext;

import com.eoss.brain.Session;

public class Expression {

    protected final Session session;
    protected final String [] arguments;

    public Expression(Session session, String [] arguments) {
        this.session = session;
        this.arguments = arguments;
    }

    public String execute() {
        StringBuilder sb = new StringBuilder();
        if (arguments!=null) {
            for (String arg:arguments) {
                sb.append(arg);
            }
        }
        return sb.toString().trim();
    }

    public static Expression build(Session session, String expression) {

        String text = expression;
        expression = expression.replace("(", "").replace(")", "");

        if (expression.startsWith("get://")) {

            /**
             * get://<<headers>>://<<url>>
             * get://<<url>>
             */

            String [] args = expression.split("://");

            return new GetHTTPExpression(session, args);
        }

        if (expression.startsWith("post://")) {

            /**
             * post://<<headers>>://<<body>>://<<url>>
             * post://<<body>>://<<url>>
             */

            String [] args = expression.split("://");

            return new PostHTTPExpression(session, args);
        }

        if (expression.startsWith("json-path://")) {

            /**
             * JSONPath
             * json-path://object://$.store.book.author
             */
            String [] args = expression.split("://");

            return new JSONExpression(session, args);
        }

        if (expression.startsWith("jsoup://")) {

            /**
             * jsoup://DOM://selector
             */

            String [] args = expression.split("://");

            return new JSoupExpression(session, args);
        }

        if (expression.startsWith("?")) {

            /**
             * ?name=value
             */
            return new VarExpression(session, new String[]{expression.substring(1)});
        }

        /**
         * return original text
         */
        return new Expression(session, new String[]{text});
    }

}