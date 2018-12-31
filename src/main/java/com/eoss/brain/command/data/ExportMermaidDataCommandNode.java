package com.eoss.brain.command.data;

import com.eoss.brain.MessageObject;
import com.eoss.brain.Session;
import com.eoss.brain.command.CommandNode;
import com.eoss.brain.net.Hook;
import com.eoss.brain.net.Node;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Set;

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
        for (Node node:session.context.nodeList) {

            nodeId = node.hashCode();
            entity = new JSONObject();
            entity.put("nodeId", nodeId);
            entity.put("hooks", Hook.toString(node.hookList()));
            entity.put("response", node.response());

            if (node.response().endsWith("!") || node.response().endsWith("?")) {
                forwardedNodes = session.context.feed(MessageObject.build(node.response().substring(0, node.response().length()-1)));
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
