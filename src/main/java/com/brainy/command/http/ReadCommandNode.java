package com.brainy.command.http;

import com.brainy.Session;
import com.brainy.MessageObject;
import com.brainy.command.CommandNode;

import com.brainy.net.Hook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class ReadCommandNode extends CommandNode {

    public final String url;

    public ReadCommandNode(Session session, String [] hooks, String url) {
        super(session, hooks, Hook.Match.Head);
        this.url = url;
    }

    /*
    @Override
    public boolean matched(MessageObject messageObject) {
        try {
            new URL(url+clean(messageObject.toString()));
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    */

    @Override
    public String execute(MessageObject messageObject) {

        String params = clean(messageObject.toString());

        try {

            Document doc = Jsoup.connect(url+params).userAgent("Mozilla/5.0").get();
            return doc.body().text();

        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }

}
