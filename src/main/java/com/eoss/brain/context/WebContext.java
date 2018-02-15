package com.eoss.brain.context;

import com.eoss.brain.net.Context;
import com.eoss.brain.net.Node;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by eoss-th on 8/15/17.
 */
public class WebContext extends Context {

    public WebContext(String name) {
        super(name);
    }

    @Override
    public void doLoad(String name) throws Exception {
        Document doc = Jsoup.connect(name).get();
        String content = doc.body().text();
        String [] sentences = content.split("\\s+");

        List<String> sentenceList = new ArrayList<>();

        String text, lastText;
        for (String sentence:sentences) {
            text = sentence.trim();
//            if (session.context.split(text).size() > 5) {
            if (text.length() > 20) {
                sentenceList.add(text);
            } else if (!sentenceList.isEmpty()) {
                lastText = sentenceList.remove(sentenceList.size()-1);
                sentenceList.add(lastText + " " + text);
            }
        }

        for (String sentence:sentenceList) {
            System.out.println(sentence);
        }

    }

    @Override
    public void doSave(String name, List<Node> nodeList) {
    }

}
