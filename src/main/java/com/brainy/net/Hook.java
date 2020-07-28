package com.brainy.net;

import com.brainy.hook.KeywordsHook;
import com.brainy.hook.NumberHook;
import com.brainy.MessageObject;
import com.brainy.ext.VarExpression;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by eossth on 15/2/2018 AD.
 */
public class Hook implements Serializable {

    public enum Match {
        All(1.0f),
        Head(1.0f),
        Body(1.0f),
        Tail(1.0f),
        Words(1.0f),
        GreaterThan(1.0f),
        GreaterEqualThan(1.0f),
        LowerThan(1.0f),
        LowerEqualThan(1.0f),
        Mode(1.0f);

        public final float initWeight;
        Match(float initWeight) {
            this.initWeight = initWeight;
        }
    }

    public final String text;

    public final Match match;

    public double weight;

    public Hook(String text, Match match) {
        this(text, match, match.initWeight);
    }

    public Hook(String text, Match match, double weight) {
        this.text = text;
        this.match = match;
        this.weight = weight;
    }

    void feedback(float feedback) {
        weight += feedback*weight;
        if (weight<0)
            weight = 0;
    }

    public boolean matched(MessageObject messageObject) {
        String input = messageObject.toString().toLowerCase();

        if (match == Match.All)
            return input.equalsIgnoreCase(text);
        if (match == Match.Head)
            return input.startsWith(text.toLowerCase());
        if (match == Match.Tail)
            return input.endsWith(text.toLowerCase());
        if (match == Match.Body) {
            return input.contains(text.toLowerCase());
        }

        Locale locale = Locale.getDefault();

        //Number Compare
        if (VarExpression.isNumeric(input, locale) && VarExpression.isNumeric(text, locale)) {

            try {

                NumberFormat formatter = NumberFormat.getInstance(locale);

                double inputNumber = formatter.parse(input).doubleValue();
                double hookNumber = formatter.parse(text).doubleValue();

                if (match == Match.GreaterEqualThan)
                    return inputNumber >= hookNumber;
                if (match == Match.GreaterThan)
                    return inputNumber > hookNumber;
                if (match == Match.LowerEqualThan)
                    return inputNumber <= hookNumber;
                if (match == Match.LowerThan)
                    return inputNumber < hookNumber;

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        Object modeObject = messageObject.attributes.get("mode");
        return modeObject!=null && modeObject.toString().equalsIgnoreCase(text);
    }

    @Override
    public String toString() {
        if (match == Match.All)
            return text;
        if (match == Match.Head)
            return text+"*";
        if (match == Match.Tail)
            return "*"+text;
        if (match == Match.Body)
            return "*"+text+"*";
        if (match == Match.GreaterThan)
            return ">"+text;
        if (match == Match.GreaterEqualThan)
            return ">="+text;
        if (match == Match.LowerThan)
            return "<"+text;
        if (match == Match.LowerEqualThan)
            return "<="+text;
        if (match == Match.Words)
            return "\"" + text + "\"";

        //Match
        return "["+text+"]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(match, text);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Hook) {
            Hook another = (Hook)obj;
            return match == another.match && Objects.equals(text, another.text);
        }
        return false;
    }

    @Deprecated
    /**
     * Use Node.build instead
     */
    public static List<Hook> build(String [] hooks) {
        List<Hook> hookList = new ArrayList<>();
        if (hooks!=null) {
            String hook;
            for (int i=0; i<hooks.length; i++) {
                hook = hooks[i].trim();
                if (!hook.isEmpty()) {
                    if (i==0)
                        hookList.add(new Hook(hook, Match.Head));
                    else if (i==hooks.length-1)
                        hookList.add(new Hook(hook, Match.Tail));
                    else
                        hookList.add(new Hook(hook, Match.Body));
                }
            }
        }
        return hookList;
    }

    public static List<Hook> build(String [] hooks, Match match) {

        List<Hook> hookList = new ArrayList<>();

        if (hooks!=null) {
            String hook;
            for (int i=0; i<hooks.length; i++) {
                hook = hooks[i].trim();
                if (!hook.isEmpty()) {
                    hookList.add(build(hook, match, match.initWeight));
                }
            }
        }

        return hookList;
    }

    public static Hook build(JSONObject jsonObject) {
        String text = jsonObject.getString("text");
        Match match = Match.valueOf(jsonObject.getString("match"));
        double weight = jsonObject.getDouble("weight");
        return build(text, match, weight);
    }

    private static Hook build(String text, Match match, double weight) {
        if (match==Match.Words) {
            return new KeywordsHook(text, match, weight);
        }

        if (match==Match.GreaterThan||match==Match.GreaterEqualThan||match==Match.LowerThan||match==Match.LowerEqualThan) {
            return new NumberHook(text, match, weight);
        }

        return new Hook(text, match, weight);
    }

    public static JSONObject json(Hook hook) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("text", hook.text);
        jsonObject.put("match", hook.match);
        jsonObject.put("weight", hook.weight);
        return jsonObject;
    }

    public static String toString(List<Hook> hookList) {
        StringBuilder sb = new StringBuilder();
        for (Hook hook:hookList) {
            if (Match.Mode==hook.match) continue;
            sb.append(hook.text);
            sb.append(" ");
        }
        return sb.toString().trim();
    }

}