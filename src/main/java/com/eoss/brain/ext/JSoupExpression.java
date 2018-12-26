package com.eoss.brain.ext;

import com.eoss.brain.Session;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JSoupExpression extends Expression {

    public JSoupExpression(Session session, String[] arguments) {
        super(session, arguments);
    }

    @Override
    public String execute() {

        if (arguments.length==3) {

            String dom = arguments[1].replace("`", "");
            return jsoup(dom, arguments[2]);
        }

        return super.execute();
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
