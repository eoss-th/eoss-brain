package com.eoss.service.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.eoss.brain.MessageObject;
import com.eoss.brain.Session;
import com.eoss.brain.command.talk.Question;

public class CallbackEndpoint {

    public interface CallbackEndpointListener {
        void onSucceed(String chatlog);
    }

    public final void process(HttpRequestAdapter requestAdapter, CallbackEndpointListener listener) throws IOException {

        String forwarderMessage = null;

        Session session = requestAdapter.getSession();

        /**
         * For get property command
         */
        if (requestAdapter.isGetPropertyCommand()) {

            String propertyValue = session.context.properties.get(requestAdapter.message);
            requestAdapter.response(propertyValue);
            return;
        }

        /**
         * For Greeting Event
         */
        if (requestAdapter.message.equals("greeting")) {
            if (session.hasProblem()) {
                session.clearProblem();
                session.solved(true);
            }
            String greetingValue = session.context.properties.get("greeting");
            if (greetingValue!=null) {
                if (greetingValue.endsWith("!")) {

                    int startSubIndex;
                    int lastIndexOfComma = greetingValue.lastIndexOf(",");
                    if (lastIndexOfComma==-1) {
                        startSubIndex = 0;//Start Substring at Zero;
                    } else {
                        startSubIndex = lastIndexOfComma + 1;
                        forwarderMessage = greetingValue.substring(0, lastIndexOfComma).trim();
                    }

                    greetingValue = greetingValue.substring(startSubIndex, greetingValue.length()-1).trim();
                    requestAdapter.messageObject = MessageObject.build(requestAdapter.messageObject, greetingValue);

                } else {
                    requestAdapter.response(greetingValue);
                    return;
                }
            } else {
                requestAdapter.response("greeting");
                return;
            }
        }

        /*
        if (requestAdapter.message.startsWith("image ")) {

            String imageName = requestAdapter.message.replace("image ", "");
            String imageURL = "https://wayobot.com/im/" + requestAdapter.contextName + "/" + imageName;

            if (session.hasProblem()) {
                List<String> parameters = new ArrayList<>();
                parameters.add(imageURL);
                requestAdapter.messageObject.attributes.put("parameters", parameters);

                requestAdapter.messageObject = MessageObject.build(requestAdapter.messageObject, "");

            } else {
                requestAdapter.response(imageURL);
                return;
            }

        }
        */

        /**
         * For Silent ping from Browser
         */
		/*
		if (requestAdapter.message.equals("...")) {
			if (session.hasProblem()) {
				session.clearProblem();
				session.solved(true);
			}
			String propertyValue = session.context.properties.get("silent");
			if (propertyValue!=null) {
				if (propertyValue.endsWith("!")) {
					requestAdapter.messageObject = MessageObject.build(requestAdapter.messageObject, propertyValue.substring(0, propertyValue.length()-1));
				} else {
					requestAdapter.response(propertyValue);
					return;
				}
			} else {
				requestAdapter.response("...");
				return;
			}
		}
		*/

        String responseText = session.parse(requestAdapter.messageObject).trim();

        /**
         * Retry if unknown occurs!
         */
        String unknown = (String) requestAdapter.request.getAttribute("unknown");
        if (!session.learning && unknown!=null) {

            if (unknown.endsWith("!")) {

                int startSubIndex;
                int lastIndexOfComma = unknown.lastIndexOf(",");
                if (lastIndexOfComma==-1) {
                    startSubIndex = 0;//Start Substring at Zero;
                } else {
                    startSubIndex = lastIndexOfComma + 1;
                    forwarderMessage = unknown.substring(0, lastIndexOfComma).trim();
                }

                unknown = unknown.substring(startSubIndex, unknown.length()-1).trim();

                List<String> parameters = new ArrayList<>();
                parameters.add(requestAdapter.message);
                requestAdapter.messageObject.attributes.put("parameters", parameters);

                requestAdapter.messageObject = MessageObject.build(requestAdapter.messageObject, unknown);

                responseText = session.parse(requestAdapter.messageObject).trim();

            } else {
                requestAdapter.response(unknown);
                return;
            }

        }

        HttpRequestAdapter.Reply reply;
        try {
            /**
             * Try with forwarding to Another WAYOBot API
             */
            reply = HttpRequestAdapter.Reply.build(responseText);
        } catch (JSONException e) {
            reply = new HttpRequestAdapter.Reply(responseText, (List<Question>) requestAdapter.request.getAttribute("questionList"));
        }

        if (forwarderMessage!=null&&!forwarderMessage.trim().isEmpty()) {
            //responseText = forwarderMessage.trim() + "\n\n\n" + responseText;
            reply.messageList.add(0, new HttpRequestAdapter.Reply.Text(forwarderMessage.trim()));
        }

        if (reply.messageList.isEmpty()) return;

        try {

            String chatlog = requestAdapter.response(reply);

            if (listener!=null) {
                listener.onSucceed(chatlog);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}