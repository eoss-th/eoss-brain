package com.eoss.brain.net;

import com.eoss.brain.MessageObject;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by eossth on 15/2/2018 AD.
 */
public class Hook implements Serializable {

    public enum Match {
        All(1.0f),
        Head(1.5f),
        Body(1.2f),
        Tail(0.95f),
        GreaterThan(1),
        GreaterEqualThan(1),
        LowerThan(1),
        LowerEqualThan(1),
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

    private List<Float> numberList(String text) {

        List<Float> numberList = new ArrayList<>();
        Pattern p = Pattern.compile("-?\\d+");
        Matcher m = p.matcher(text);
        while (m.find()) {
            try {
                numberList.add(Float.parseFloat(m.group()));
            } catch (Exception e) {

            }
        }
        return numberList;
    }

    boolean matched(MessageObject messageObject) {

        String input = messageObject.toString();
        Object modeObject = messageObject.attributes.get("mode");

        if (match == Match.All)
            return input.equalsIgnoreCase(text);
        if (match == Match.Head)
            return input.toLowerCase().startsWith(text.toLowerCase());
        if (match == Match.Tail)
            return input.toLowerCase().endsWith(text.toLowerCase());
        if (match == Match.Body)
            return input.toLowerCase().contains(text.toLowerCase());
        if (match == Match.GreaterThan) {
            Float targetNumber;
            try {
                targetNumber = Float.parseFloat(text);
            } catch (Exception e) {
                System.out.println(text);
                targetNumber = 0f;
            }

            List<Float> inputNumberList = numberList(input);
            boolean result = false;
            for (Float number:inputNumberList) {
                if (number>targetNumber)
                    result = true;
            }
            return result;
        }
        if (match == Match.GreaterEqualThan) {
            Float targetNumber;
            try {
                targetNumber = Float.parseFloat(text);
            } catch (Exception e) {
                targetNumber = 0f;
            }

            List<Float> inputNumberList = numberList(input);
            boolean result = false;
            for (Float number:inputNumberList) {
                if (number>=targetNumber)
                    result = true;
            }
            return result;
        }
        if (match == Match.LowerThan) {
            Float targetNumber;
            try {
                targetNumber = Float.parseFloat(text);
            } catch (Exception e) {
                targetNumber = 0f;
            }

            List<Float> inputNumberList = numberList(input);
            boolean result = false;
            for (Float number:inputNumberList) {
                if (number<targetNumber)
                    result = true;
            }
            return result;
        }
        if (match == Match.LowerEqualThan) {
            Float targetNumber;
            try {
                targetNumber = Float.parseFloat(text);
            } catch (Exception e) {
                targetNumber = 0f;
            }

            List<Float> inputNumberList = numberList(input);
            boolean result = false;
            for (Float number:inputNumberList) {
                if (number<=targetNumber)
                    result = true;
            }
            return result;
        }

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

    public static List<Hook> build(String [] hooks) {

        List<Hook> hookList = new ArrayList<>();

        if (hooks!=null) {
            String hook;
            for (int i=0; i<hooks.length; i++) {
                hook = hooks[i].trim();
                if (!hook.isEmpty()) {

                    if (hook.startsWith(">="))
                        hookList.add(new Hook(hook.replace(">=",""), Match.GreaterEqualThan));
                    else if (hook.startsWith(">"))
                        hookList.add(new Hook(hook.replace(">", ""), Match.GreaterThan));
                    else if (hook.startsWith("<="))
                        hookList.add(new Hook(hook.replace("<=", ""), Match.LowerEqualThan));
                    else if (hook.startsWith("<"))
                        hookList.add(new Hook(hook.replace("<", ""), Match.LowerThan));
                    else if (i==0)
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
                    hookList.add(new Hook(hook, match));
                }
            }
        }

        return hookList;
    }

    public static Hook build(JSONObject jsonObject) {
        String text = jsonObject.getString("text");
        Match match = Match.valueOf(jsonObject.getString("match"));
        double weight = jsonObject.getDouble("weight");
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