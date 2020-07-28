package com.brainy.ext;

import com.brainy.MessageObject;
import com.brainy.Session;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JSoupExpression extends Expression {

    public JSoupExpression(Session session, String[] arguments) {
        super(session, arguments);
    }

    @Override
    public String execute(MessageObject messageObject) {

        String [] args = parameterized(messageObject, arguments);

        if (args.length==3) {
            return jsoup(args[1], args[2]);
        }

        return super.execute(messageObject);
    }

    protected final String jsoup(String dom, String path) {

        try {
            Object result = Jsoup.parse(dom).select(path);
            if (result instanceof Elements) {
                return ((Elements)result).text();
            }

            if (result instanceof Element) {
                return ((Element)result).text();
            }

            return result.toString();

        } catch (Exception e) {
            return e.getMessage();
        }

    }

}
