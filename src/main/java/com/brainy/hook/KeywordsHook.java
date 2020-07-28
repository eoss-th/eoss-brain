package com.brainy.hook;

import com.brainy.MessageObject;
import com.brainy.net.Hook;

import java.util.List;

public class KeywordsHook extends Hook {

    public KeywordsHook(String text, Match match) {
        super(text, match);
    }

    public KeywordsHook(String text, Match match, double weight) {
        super(text, match, weight);
    }

    @Override
    public boolean matched(MessageObject messageObject) {

        if (!messageObject.isSplitted()) {
            messageObject.split();
        }

        List<String> wordList = (List<String>) messageObject.attributes.get("wordList");

        String input = messageObject.toString();

        if (input.equalsIgnoreCase(text)) return true;

        //For Keywords Match!
        if (text.contains(",")) {
            String[] tokens = text.toLowerCase().split(",");
            for (String token : tokens) {
                if (input.equalsIgnoreCase(token)) {
                    return true;
                }
                if (wordList.contains(token)) {
                    return true;
                }
            }
        }

        return wordList.contains(text.toLowerCase());
    }
}