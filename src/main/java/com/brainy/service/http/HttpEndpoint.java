package com.brainy.service.http;

import com.brainy.MessageObject;
import com.brainy.Session;
import com.brainy.service.Endpoint;
import com.brainy.service.RequestObject;
import com.brainy.service.ResponseObject;
import org.json.JSONException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpEndpoint extends Endpoint<HttpServletRequest> {

    protected final HttpServletRequest request;
    protected final HttpServletResponse response;

    public HttpEndpoint(String contextName, HttpServletRequest request, HttpServletResponse response) {

        super(contextName);

        this.request = request;
        this.response = response;

    }

    @Override
    protected void onGotNewSession(String sessionId, Session session) {

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

        return new RequestObject() {
            @Override
            public MessageObject messageObject() {
                return MessageObject.build(message);
            }

            @Override
            public String sessionId() {
                return sessionId;
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

        String result;
        try {
            result = responseObject.toJSONString();
            response.setContentType("application/json");
            response.getWriter().print(result);
        } catch (JSONException e) {
            result = responseObject.toString();
            response.setContentType("text/plain");
            response.getWriter().print(result);
        }

        return result;
    }

}
