package com.brainy.service.http.ext.line;

import com.brainy.MessageObject;
import com.brainy.Session;
import com.brainy.command.talk.Choice;
import com.brainy.command.talk.Question;
import com.brainy.service.RequestObject;
import com.brainy.service.ResponseObject;
import com.brainy.service.http.HttpEndpoint;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class LINEHttpEndpoint extends HttpEndpoint {

    private final String REPLY_URL = "https://api.line.me/v2/bot/message/reply";

    private String channelAccessToken;
    private String secret;

    private LINESignatureValidator lineSignatureValidator;

    /**
     * Use for LINE Response API
     */
    private String replyToken;

    /**
     * LINE sourceId map to sessionId
     */
    private String sourceId;

    /**
     * LINE source type (user|room|group)
     */
    private String sourceType;

    /**
     * LINE text map to messageObject
     */
    private String text;

    public LINEHttpEndpoint(String contextName, String channelAccessToken, String secret, HttpServletRequest request, HttpServletResponse response) {
        super(contextName, request, response);
        this.channelAccessToken = channelAccessToken;
        this.secret = secret;
        this.lineSignatureValidator = new LINESignatureValidator(secret.getBytes());
    }

    @Override
    public void onGotNewSession(String sessionId, Session session) {
        session.setVariable("#channel", "line." + sourceType);
        session.setVariable("#targetId", sourceId);
    }

    @Override
    public RequestObject createRequestObject(HttpServletRequest request) {

        try {

            String headerSignature = request.getHeader("X-Line-Signature");

            String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

            if (lineSignatureValidator.validateSignature(requestBody.getBytes(), headerSignature)) {

                JSONObject requestObj = new JSONObject(requestBody);
                //String destination = requestObj.getString("destination");
                JSONArray eventArray = requestObj.getJSONArray("events");

                JSONObject eventObj, srcObj, messageObj;
                String type;

                for (int i=0;i<eventArray.length();i++) {

                    eventObj = eventArray.getJSONObject(i);
                    type = eventObj.getString("type");
                    srcObj = eventObj.getJSONObject("source");
                    sourceType = srcObj.getString("type");

                    if (sourceType.equals("user")) {
                        sourceId = srcObj.getString("userId");
                    } else if (sourceType.equals("room")) {
                        sourceId = srcObj.getString("roomId");
                    } else if (sourceType.equals("group")) {
                        sourceId = srcObj.getString("groupId");
                    } else {
                        sourceId = "";
                    }

                    if (type.equals("leave") || type.equals("unfollow")) {
                        break;
                    }

                    replyToken = eventObj.getString("replyToken");

                    if (type.equals("message")) {

                        messageObj = eventObj.getJSONObject("message");

                        if (messageObj.getString("type").equals("text")) {

                            text = messageObj.getString("text");

                        } else if (messageObj.getString("type").equals("image")) {

                            JSONObject contentProvider = messageObj.getJSONObject("contentProvider");
                            String contentType = contentProvider.getString("type");

                            if (contentType.equals("line")) {

                                String messageId = messageObj.getString("id");
                                text = "https://api.line.me/v2/bot/message/" + messageId + "/content";

                            }

                        } else if (messageObj.getString("type").equals("sticker")) {

                            String packageId = messageObj.getString("packageId");
                            String stickerId = messageObj.getString("stickerId");
                            text = "line:sticker " + "lineStickerId:" + packageId + ":" + stickerId;

                        } else if (messageObj.getString("type").equals("location")) {

                            Double latitude = messageObj.getDouble("latitude");
                            Double longitude = messageObj.getDouble("longitude");
                            text = latitude + "," + longitude;

                        }

                    } else if (type.equals("postback")) {

                        text = eventObj.getJSONObject("postback").getString("data");

                    } else if (type.equals("follow") || type.equals("join") || type.equals("memberJoined")) {

                        text = "greeting";

                    } else {

                        text = "";
                    }

                    break;
                }

            } else {

                throw new RuntimeException("Invalid Signature");

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new RequestObject() {

            @Override
            public MessageObject messageObject() {
                return MessageObject.build(text);
            }

            @Override
            public String sessionId() {
                return sourceId;
            }

            @Override
            public String contextName() {
                return contextName;
            }

        };

    }

    @Override
    public String response(ResponseObject responseObject) throws Exception {

        JSONArray messages = createMessages(responseObject);
        StringBuilder logCollector = new StringBuilder();

        try {

            JSONObject object = new JSONObject();

            URL url = new URL(REPLY_URL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer {" + channelAccessToken + "}");

            object.put("replyToken", replyToken);

            object.put("messages", messages);

            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(object.toString().getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();

            int respCode = conn.getResponseCode();  // New items get NOT_FOUND on PUT
            String line;

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

        } catch (Exception e) {
            logCollector.append(e.getMessage());
        } finally {
            /**
             * Log Collection
             */
            JSONObject message;
            for (int i=0;i<messages.length();i++) {
                message = messages.getJSONObject(i);
                try {
                    logCollector.append(message + System.lineSeparator());
                } catch (JSONException e) {
                    continue; // go go go!
                }
            }
        }
        return logCollector.toString();
    }

    private JSONArray createMessages(ResponseObject responseObject) {

        JSONArray messages = new JSONArray();

        List<Object> messageList = responseObject.messageList;
        ResponseObject.Text text;
        ResponseObject.Image image;
        ResponseObject.Audio audio;
        List<Question> questionList;

        for (Object msg:messageList) {
            if (msg instanceof ResponseObject.Text) {

                text = (ResponseObject.Text) msg;
                messages.put(createText(text.toString()));

            } else if (msg instanceof ResponseObject.Image) {

                image = (ResponseObject.Image) msg;
                messages.put(createImage(image.url));

            } else if (msg instanceof ResponseObject.Audio) {

                audio = (ResponseObject.Audio) msg;
                messages.put(createAudio(audio.url));

            } else {
                questionList = (List<Question>) msg;

                JSONObject message;
                try {

                    if (questionList.size()==1) {

                        Question question = questionList.get(0);

                        if (question.choices.size()==1||question.choices.size()>4) {

                            message = createQuickReply(question);

                        } else {

                            message = createButtonsTemplate(question);

                        }

                    } else {

                        message = createCarouselTemplate(questionList);

                    }

                    messages.put(message);

                } catch (Exception e) {
                    for (Question question:questionList) {
                        messages.put(createText(question.label));
                    }
                }

            }

            if (messages.length()>5) break; //Limit to 5 messages

        }
        return messages;
    }

    private JSONObject createText(String text) {
        JSONObject message = new JSONObject();
        message.put("type", "text");
        message.put("text", text);
        return message;
    }

    private JSONObject createImage(String imageURL) {
        JSONObject image = new JSONObject();
        image.put("type", "image");
        image.put("originalContentUrl", imageURL);
        image.put("previewImageUrl", imageURL);
        return image;
    }

    private JSONObject createAudio(String audioURL) {
        JSONObject audio = new JSONObject();
        audio.put("type", "audio");
        audio.put("originalContentUrl", audioURL);
        audio.put("duration", 60000);
        return audio;
    }

    private JSONObject createQuickReply(Question question) {

        JSONObject quickReply = new JSONObject();

        JSONArray items = new JSONArray();
        JSONObject itemObj, actionObj;
        for (Choice choice:question.choices) {
            itemObj = new JSONObject();
            actionObj = new JSONObject();

            String label = choice.label.trim();
            if(label.length()>20) {
                label = label.substring(0, 18);
                label += "..";
            }

            actionObj.put("type", "postback");
            actionObj.put("label", label);
            actionObj.put("data", choice.parent + " " + choice.label);
            actionObj.put("displayText", choice.label);

            itemObj.put("type", "action");
            if (choice.imageURL!=null) {
                itemObj.put("imageUrl", choice.imageURL);
            }
            itemObj.put("action", actionObj);

            items.put(itemObj);
        }

        if (items.length()>13) throw new RuntimeException("Too many quick reply");

        quickReply.put("items", items);

        JSONObject message = new JSONObject();
        message.put("type", "text");
        message.put("text", question.label);
        message.put("quickReply", quickReply);

        return message;
    }

    private JSONObject createButtonsTemplate(Question question) {

        JSONObject template = new JSONObject();
        template.put("type", "buttons");

        boolean hasImage = false;
        if (question.hasImage()) {
            template.put("thumbnailImageUrl", question.imageURL);
            template.put("imageSize", "contain");
            hasImage = true;
        }

        int textLimit = hasImage?60:160;
        String text = question.label;
        if (text.length()>textLimit) {
            text = text.substring(0, textLimit - 2);
            text += "..";
        }

        template.put("text", text);

        JSONArray actions = new JSONArray();
        JSONObject actionObj;
        for (Choice choice:question.choices) {
            actionObj = new JSONObject();

            String label = choice.label.trim();
            if(label.length()>20) {
                label = label.substring(0, 18);
                label += "..";
            }

            if (choice.isLinkLabel()) {

                actionObj.put("type", "uri");
                actionObj.put("label", label);
                actionObj.put("uri", choice.linkURL);

            } else {

                actionObj.put("type", "postback");
                actionObj.put("label", label);
                actionObj.put("data", choice.parent + " " + choice.label);
                actionObj.put("displayText", choice.label);

            }

            actions.put(actionObj);
        }

        if (actions.length()==0) throw new RuntimeException("Empty Action");

        template.put("actions", actions);

        JSONObject message = new JSONObject();
        message.put("type", "template");
        message.put("altText", "Menu");
        message.put("template", template);

        return message;
    }

    private JSONObject createCarouselTemplate(List<Question> questionList) {

        int minChoiceSize = Integer.MAX_VALUE;
        boolean hasImage = false;
        for (Question question:questionList) {
            if (question.choices.size()<minChoiceSize) {
                minChoiceSize = question.choices.size();
            }
            if (question.hasImage()) {
                hasImage = true;
            }
        }
        if (minChoiceSize>4) {
            minChoiceSize = 4;
        }

        JSONObject template = new JSONObject();
        JSONArray columns = new JSONArray();
        JSONObject column;
        JSONArray actions;
        JSONObject actionObj;
        for (Question question:questionList) {

            if (question.choices.size()>minChoiceSize) continue;

            column = new JSONObject();

            if (question.hasImage()) {
                column.put("thumbnailImageUrl", question.imageURL);
            }

            int textLimit = question.hasImage()?60:120;
            String text = question.label;
            if (text.length()>textLimit) {
                text = text.substring(0, textLimit - 2);
                text += "..";
            }

            column.put("text", text);

            actions = new JSONArray();
            for (Choice choice:question.choices) {
                actionObj = new JSONObject();

                String label = choice.label.trim();
                if(label.length()>20) {
                    label = label.substring(0, 18);
                    label += "..";
                }

                if (choice.isLinkLabel()) {

                    actionObj.put("type", "uri");
                    actionObj.put("label", label);
                    actionObj.put("uri", choice.linkURL);

                } else {

                    actionObj.put("type", "postback");
                    actionObj.put("label", label);
                    actionObj.put("data", choice.parent + " " + choice.label);
                    actionObj.put("displayText", choice.label);

                }

                actions.put(actionObj);
            }

            if (actions.length()==0) continue;

            column.put("actions", actions);
            columns.put(column);
        }

        if (columns.length()==0) throw new RuntimeException("Empty columns");
        if (columns.length()>10) throw new RuntimeException("Too many columns");

        template.put("type", "carousel");
        template.put("columns", columns);

        if (hasImage) {
            template.put("imageSize", "cover");
        }

        JSONObject message = new JSONObject();
        message.put("type", "template");
        message.put("altText", "Menu");
        message.put("template", template);

        return message;
    }
}