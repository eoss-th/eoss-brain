package com.eoss.brain.command.data;

import com.eoss.brain.MessageObject;
import com.eoss.brain.Session;
import com.eoss.brain.command.CommandNode;
import com.eoss.brain.net.Hook;
import com.eoss.brain.net.Node;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
        Set<Node> forwardedNodes;
        int nodeId;
        String input;
        MessageObject msgObject;
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
                while (matcher.find()) {
                    param = matcher.group();
                    input = input.replace(param, "");
                }

                pattern = Pattern.compile("#\\d+");
                matcher = pattern.matcher(input);

                while (matcher.find()) {
                    param = matcher.group();
                    input = input.replace(param, "");
                }

                input = input.replace("##", "");
                input = input.trim();

                msgObject = MessageObject.build(input);
                msgObject.attributes.put("wordCount", session.context.split(input).length);

                forwardedNodes = session.context.feed(msgObject);
                if (!forwardedNodes.isEmpty()) {
                    for (Node forwardNode:forwardedNodes) {
                        arrows.put(nodeId + "-->" + forwardNode.hashCode());
                    }
                }
            }

            entities.put(entity);
        }

        obj.put("nodes", entities);
        obj.put("arrows", arrows);

        return obj.toString();
    }
}
