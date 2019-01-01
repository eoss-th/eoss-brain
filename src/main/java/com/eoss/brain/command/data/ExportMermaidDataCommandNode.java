package com.eoss.brain.command.data;

import com.eoss.brain.MessageObject;
import com.eoss.brain.Session;
import com.eoss.brain.command.CommandNode;
import com.eoss.brain.net.Hook;
import com.eoss.brain.net.Node;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class ExportMermaidDataCommandNode extends CommandNode {

    public ExportMermaidDataCommandNode(Session session, String [] hooks) {
        super(session, hooks);
    }

    @Override
    public String execute(MessageObject messageObject) {

        JSONObject obj = new JSONObject();
        JSONArray entities = new JSONArray();
        JSONArray arrows = new JSONArray();

        JSONObject entity;
        int nodeId, forwardedCount;
        String input;
        boolean hasProcessor;
        for (Node node:session.context.nodeList) {

            nodeId = node.hashCode();
            input = node.response();
            entity = new JSONObject();
            entity.put("nodeId", nodeId);
            entity.put("hooks", Hook.toString(node.hookList()));
            entity.put("response", input);

            if (input.endsWith("!") || input.endsWith("?")) {

                input = input.substring(0, input.length()-1);

                Pattern pattern = Pattern.compile("\\`.*?\\`");
                Matcher matcher = pattern.matcher(input);

                String param;
                hasProcessor = false;
                while (matcher.find()) {
                    param = matcher.group();
                    input = input.replace(param, "");
                    hasProcessor = true;
                }

                pattern = Pattern.compile("#\\d+");
                matcher = pattern.matcher(input);

                while (matcher.find()) {
                    param = matcher.group();
                    input = input.replace(param, "");
                }

                input = input.replace("##", "");

                input = String.join(" ", session.context.split(input));

                input = input.trim();

                forwardedCount = 0;

                for (Node forwardedNode:session.context.nodeList) {
                    if (Hook.toString(forwardedNode.hookList()).startsWith(input)) {
                        arrows.put(nodeId + "-->" + forwardedNode.hashCode());
//                        arrows.put(Hook.toString(node.hookList()) + ":" +input + "-->" + Hook.toString(forwardedNode.hookList()));
                        forwardedCount ++;
                    }
                }

                if (forwardedCount>=2) {
                    entity.put("type", "decision");
                } else if (hasProcessor) {
                    entity.put("type", "processor");
                } else {
                    entity.put("type", "end");
                }

            } else {

                Pattern pattern = Pattern.compile("\\`.*?\\`");
                Matcher matcher = pattern.matcher(input);
                if (matcher.find()) {
                    entity.put("type", "processor");
                } else {
                    entity.put("type", "end");
                }

            }

            entities.put(entity);
        }

        obj.put("nodes", entities);
        obj.put("arrows", arrows);

        return obj.toString();
    }
}
