package com.brainy.service.console;

import com.brainy.MessageObject;
import com.brainy.Session;
import com.brainy.service.Endpoint;
import com.brainy.service.RequestObject;
import com.brainy.service.ResponseObject;

public class ConsoleEndpoint extends Endpoint<String> {

    public ConsoleEndpoint(String contextName) {
        super(contextName);
    }

    @Override
    protected void onGotNewSession(String sessionId, Session session) {
        session.setVariable("#channel", "console");
        session.setVariable("#targetId", sessionId);
    }

    @Override
    protected RequestObject createRequestObject(String message) {

        return new RequestObject() {
            @Override
            public MessageObject messageObject() {
                return MessageObject.build(message);
            }

            @Override
            public String sessionId() {
                return "123";
            }

            @Override
            public String contextName() {
                return contextName;
            }

        };

    }

    @Override
    public String response(ResponseObject responseObject) throws Exception {

        return "Bot:>>" + responseObject.toString();
    }

}
