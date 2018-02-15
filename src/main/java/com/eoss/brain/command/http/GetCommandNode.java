package com.eoss.brain.command.http;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.command.CommandNode;
import com.eoss.brain.net.Hook;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class GetCommandNode extends CommandNode {

    public final String url;

    public GetCommandNode(Session session, String [] hooks, String url) {
        super(session, hooks, Hook.Match.Head);
        this.url = url;
    }

    @Override
    public boolean matched(MessageObject messageObject) {
        try {
            new URL(url+clean(messageObject.toString()));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public String execute(MessageObject messageObject) {

        String params = clean(messageObject.toString());

        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new URL(url + params).openStream(), StandardCharsets.UTF_8));
            String line;
            while (true) {
                line = br.readLine();
                if (line!=null)
                    sb.append(line);
                else
                    break;
                sb.append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br!=null) try { br.close(); } catch (Exception e) {}
        }

        return sb.toString();
    }

}
