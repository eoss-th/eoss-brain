package com.brainy.service.http.ext;

import com.brainy.MessageObject;
import com.brainy.Session;
import com.brainy.service.RequestObject;
import com.brainy.service.http.HttpEndpoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Support Multi Contexts per single URI in format ../<accountId>/<botId>
 * You can override the contextName() to tell Hub that how to resolve context
 */
public class WAYOBotHttpEndpoint extends HttpEndpoint {

    public WAYOBotHttpEndpoint(HttpServletRequest request, HttpServletResponse response) {
        super(null, request, response);
    }

    @Override
    public void onGotNewSession(String sessionId, Session session) {
        session.setVariable("#channel", "web");
        session.setVariable("#targetId", sessionId);
    }

    @Override
    protected RequestObject createRequestObject(HttpServletRequest request) {

        String message = request.getParameter("message");
        if (message==null) {
            throw new IllegalArgumentException("Mission message parameter");
        }

        String sessionId = request.getParameter("sessionId");

        if (sessionId==null) {
            throw new IllegalArgumentException("Mission sessionId parameter");
        }

        String accountId;
        String botId;

        String [] uris = request.getRequestURI().split("/");
        if (uris.length>3) {
            accountId = uris[2];
            botId = uris[3];
        } else {
            throw new IllegalArgumentException("Invalid URI [../<accountId>/<botId>]");
        }

        return new RequestObject() {
            @Override
            public MessageObject messageObject() {
                /**
                 * Clean for what?
                 */
                return MessageObject.build(message).clean();
            }

            @Override
            public String sessionId() {
                /**
                 * For each bot in one accountID
                 */
                return botId + "." + sessionId;
            }

            @Override
            public String contextName() {
                return accountId + "/" + botId;
            }

        };
    }
}
