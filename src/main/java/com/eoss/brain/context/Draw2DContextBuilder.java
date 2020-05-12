package com.eoss.brain.context;

import com.eoss.brain.net.Context;
import com.eoss.brain.net.Hook;
import com.eoss.brain.net.Node;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

public class Draw2DContextBuilder {

    public static class Entity {

        private final Node node;
        public Entity(String keyword, String response, boolean isQuestion) {

            this.node = new Node();

            if (!keyword.equals("GREETING") && !keyword.equals("UNKNOWN")) {
                this.node.addHook(keyword, Hook.Match.Words);
            }

            this.node.setResponse(response);

            this.node.attributes.put("id", UUID.randomUUID().toString());
            this.node.attributes.put("isQuestion", isQuestion);
        }

    }

    public final Context context;

    public final Entity GREETING;

    public final Entity UNKNOWN;

    public final Entity SILENT;

    private int x;
    private int y;

    public Draw2DContextBuilder(Context context, String title, int shift) {

        this.context = context;

        x = shift;
        y = shift;

        GREETING = new Entity("GREETING", "", false);
        GREETING.node.attributes.put("x", x);
        GREETING.node.attributes.put("y", y);
        this.context.attributes.put("start", new JSONObject(GREETING.node.attributes));
        this.context.properties.put("greeting", "");
        y += shift;

        UNKNOWN = new Entity("UNKNOWN", "", false);
        UNKNOWN.node.attributes.put("x", x);
        UNKNOWN.node.attributes.put("y", y);
        this.context.attributes.put("end", new JSONObject(UNKNOWN.node.attributes));
        this.context.properties.put("unknown", "");
        y += shift;

        SILENT = new Entity("SILENT", "", false);
        SILENT.node.attributes.put("x", x);
        SILENT.node.attributes.put("y", y);
        this.context.attributes.put("silent", new JSONObject(SILENT.node.attributes));
        this.context.properties.put("silent", "");
        y += shift;

        this.context.properties.put("title", title);
        this.context.properties.put("borderColor", "#64c583");
        this.context.properties.put("language", "th");

    }

    /**
     * db.newEntity(GREETING, "").newEntity("", "").newQuestion("", [{},{},{}])
     * @return
     */
    public Entity newEntity(Entity [] parentEntities, String keyword, String response, boolean isQuestion) {

        Entity newEntity = new Entity(keyword, response, isQuestion);

        newEntity.node.attributes.put("x", x);
        newEntity.node.attributes.put("y", y);

        if (parentEntities!=null) {

            for (Entity parentEntity:parentEntities) {

                JSONArray connectionArray = (JSONArray) context.attributes.get("connections");
                if (connectionArray == null) {
                    connectionArray = new JSONArray();
                    context.attributes.put("connections", connectionArray);
                }

                JSONObject connectionObj = new JSONObject();
                connectionObj.put("id", UUID.randomUUID().toString());
                connectionObj.put("source", parentEntity.node.attributes.get("id"));
                connectionObj.put("target", newEntity.node.attributes.get("id"));

                connectionArray.put(connectionObj);

                if (parentEntity==GREETING) {

                    this.context.properties.put("greeting", ", @" + parentEntity.node.attributes.get("id") + "!");

                } else if (parentEntity==UNKNOWN) {

                    this.context.properties.put("unknown", ", @" + parentEntity.node.attributes.get("id") + "!");

                } else if (!parentEntity.node.response().endsWith(", @" + parentEntity.node.attributes.get("id") + "!")) {

                    parentEntity.node.setResponse(parentEntity.node.response() + ", @" + parentEntity.node.attributes.get("id") + "!");

                }

                newEntity.node.addHook("@" + parentEntity.node.attributes.get("id"), Hook.Match.Words);
            }

        }

        this.context.add(newEntity.node);

        return newEntity;
    }

    public void nextColumn(int shift) {
        x += shift;
    }

    public void nextRow(int shift) {
        y += shift;
    }

}
