package com.brainy.service.http.ext.facebook;

import com.brainy.MessageObject;
import com.brainy.Session;
import com.brainy.command.talk.Choice;
import com.brainy.command.talk.Question;
import com.brainy.service.RequestObject;
import com.brainy.service.ResponseObject;
import com.brainy.service.http.HttpEndpoint;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.send.*;

import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FacebookHttpEndpoint extends HttpEndpoint {

    private final String pageAccessToken;
    private final String appSecret;

    private final FacebookSignatureValidator facebookSignatureValidator;

    /**
     * Facebook senderId map to sessionId
     */
    private String senderId;

    /**
     * Facebook text map to messageObject
     */
    private String text;

    public FacebookHttpEndpoint(String contextName, String pageAccessToken, String appSecret, HttpServletRequest request, HttpServletResponse response) {
        super(contextName, request, response);
        this.pageAccessToken = pageAccessToken;
        this.appSecret = appSecret;
        this.facebookSignatureValidator = new FacebookSignatureValidator(appSecret.getBytes());
    }

    @Override
    public void onGotNewSession(String sessionId, Session session) {
        session.setVariable("#channel", "facebook.page");
        session.setVariable("#targetId", senderId);
    }

    @Override
    public RequestObject createRequestObject(HttpServletRequest request) {

        try {

            String headerSignature = request.getHeader("X-Hub-Signature");

            String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

            if (facebookSignatureValidator.validateSignature(requestBody.getBytes(), headerSignature)) {

                JSONObject requestObj = new JSONObject(requestBody);

                if (requestObj.getString("object").equals("page")) {

                    JSONObject messageObj = requestObj.getJSONArray("entry").getJSONObject(0).getJSONArray("messaging").getJSONObject(0);

                    senderId = messageObj.getJSONObject("sender").getString("id");

                    try {

                        /**
                         * First Try as simple text
                         */
                        text = messageObj.getJSONObject("message").getString("text");

                    } catch (Exception e) {

                        try {

                            /**
                             * Retry as attachment
                             */
                            JSONObject attachmentObj = messageObj.getJSONObject("message").getJSONArray("attachments").getJSONObject(0);

                            String type = attachmentObj.getString("type");

                            JSONObject payload = attachmentObj.getJSONObject("payload");

                            if (type.equals("location")) {

                                Double colat = payload.getJSONObject("coordinates").getDouble("lat");
                                Double colong = payload.getJSONObject("coordinates").getDouble("long");

                                text = colat + "," + colong;

                            } else {

                                text = payload.getString("url");

                            }

                        } catch (Exception attachment) {

                            try {

                                /**
                                 * Retry as Get Started!
                                 */
                                text = messageObj.getJSONObject("get_started").getString("payload");

                            } catch (Exception getStarted) {

                                try {

                                    /**
                                     *  Retry as referral
                                     */
                                    text = messageObj.getJSONObject("referral").getString("ref");
                                    text = URLDecoder.decode(text, "UTF-8");

                                } catch (Exception refereral) {

                                    try {

                                        /**
                                         *  Retry as Get Started referral
                                         */
                                        text = messageObj.getJSONObject("postback").getJSONObject("referral").getString("ref");
                                        text = URLDecoder.decode(text, "UTF-8");

                                    } catch (Exception getStartedReferral) {

                                        try {

                                            /**
                                             * Retry as Postback!
                                             */
                                            text = messageObj.getJSONObject("postback").getString("payload");

                                        } catch (Exception postback) {

                                            throw new RuntimeException("Undefined MSG:" + messageObj.toString());

                                        }
                                    }
                                }
                            }
                        }
                    }
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
                return senderId;
            }

            @Override
            public String contextName() {
                return contextName;
            }

        };
    }

    @Override
    public String response(ResponseObject responseObject) throws Exception {

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setCharacterEncoding("UTF-8");

        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        FacebookClient pageClient = new DefaultFacebookClient(pageAccessToken, Version.LATEST);

        String appsecret_proof = pageClient.obtainAppSecretProof(pageAccessToken, appSecret);

        List<Message> messages = createMessages(responseObject);

        SendResponse resp = null;
        StringBuilder logCollector = new StringBuilder();

        for (Message message:messages) {

            try {

                resp = pageClient.publish("me/messages", SendResponse.class,
                        Parameter.with("recipient", recipient),
                        Parameter.with("message", message),
                        Parameter.with("appsecret_proof", appsecret_proof));

            } catch (Exception e) {

                try {

                    if (message.getAttachment()!=null && (message.getAttachment().getType().equals("image") || message.getAttachment().getType().equals("audio"))) {

                        /**
                         * UrlPayload[isReusable=null url=%]
                         */
                        Object payload = ((MediaAttachment)message.getAttachment()).getPayload();
                        String url = payload.toString().replace("UrlPayload[isReusable=null url=", "");
                        url = url.substring(0, url.length()-1);

                        resp = pageClient.publish("me/messages", SendResponse.class,
                                Parameter.with("recipient", recipient),
                                Parameter.with("message", new Message(url)),
                                Parameter.with("appsecret_proof", appsecret_proof));

                    }

                } catch (Exception attachment) {
                    logCollector.append(attachment.getMessage());
                }

            } finally {
                /**
                 * Log Collection
                 */
                logCollector.append(message + System.lineSeparator());
            }

        }

        return logCollector.toString().trim();
    }

    private List<Message> createMessages(ResponseObject responseObject) {

        List<Message> messages = new ArrayList<>();

        List<Object> messageList = responseObject.messageList;
        ResponseObject.Text text;
        ResponseObject.Image image;
        ResponseObject.Audio audio;

        MediaAttachment attachment;
        List<Question> questionList;

        for (Object msg:messageList) {

            if (msg instanceof ResponseObject.Text) {

                text = (ResponseObject.Text) msg;
                Message message = new Message(text.toString());
                messages.add(message);

            } else if (msg instanceof ResponseObject.Image) {

                image = (ResponseObject.Image) msg;
                attachment = new MediaAttachment(MediaAttachment.Type.IMAGE, image.url);
                messages.add(new Message(attachment));

            } else if (msg instanceof ResponseObject.Audio) {

                audio = (ResponseObject.Audio) msg;
                attachment = new MediaAttachment(MediaAttachment.Type.AUDIO, audio.url);
                messages.add(new Message(attachment));

            } else {

                questionList = (List<Question>) msg;

                Message message;
                try {

                    /**
                     * Quick Reply Conditions
                     */
                    if (questionList.size()==1 &&
                            (questionList.get(0).choices.size()==1||
                                    questionList.get(0).choices.size()>3)) {

                        message = createQuickReply(questionList.get(0));

                    } else {

                        /**
                         * Slide Menus
                         */
                        message = createGenericTemplate(questionList);

                    }

                    messages.add(message);

                } catch (Exception e) {

                    for (Question question:questionList) {
                        messages.add(new Message(question.label));
                    }

                }

            }
        }

        return messages;
    }

    private Message createQuickReply(Question question) {

        Message message = new Message(question.label);

        QuickReply quickReply;
        for (Choice choice:question.choices) {

            quickReply = new QuickReply(choice.label, choice.parent + " " + choice.label);

            if (choice.imageURL!=null) {
                quickReply.setImageUrl(choice.imageURL);
            }

            message.addQuickReply(quickReply);
        }

        return message;
    }

    private Message createGenericTemplate(List<Question> questionList) {

        GenericTemplatePayload payload = new GenericTemplatePayload();
        Bubble bubble;
        AbstractButton button;
        for (Question question:questionList) {

            if (question.choices.size()>3) continue;

            bubble = new Bubble(question.label);

            if (question.hasImage()) {
                bubble.setImageUrl(question.imageURL);
            }

            for (Choice choice:question.choices) {
                if (choice.isLinkLabel()) {
                    if (choice.linkURL.startsWith("tel:")) {

                        button = new CallButton(choice.label, choice.linkURL.replace("tel:", ""));

                    } else {

                        button = new WebButton(choice.label, choice.linkURL);
                    }
                } else {

                    button = new PostbackButton(choice.label, choice.parent + " " + choice.label);
                }

                bubble.addButton(button);
            }

            if (bubble.getButtons().isEmpty()) continue;

            payload.addBubble(bubble);
        }

        return new Message(new TemplateAttachment(payload));
    }

}
