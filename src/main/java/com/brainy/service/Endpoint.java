package com.brainy.service;

import com.brainy.MessageObject;
import com.brainy.NodeEvent;
import com.brainy.command.AdminCommandNode;
import com.brainy.context.FileContext;
import com.brainy.net.Context;
import com.brainy.net.ContextListener;
import com.brainy.Session;
import com.brainy.util.SessionManager;

public abstract class Endpoint<T> implements SessionManager.SessionManagerListener, ContextListener {

    protected final String contextName;

    public Endpoint(String contextName) {

        SessionManager.instance().register(this);

        this.contextName = contextName;
    }

    @Override
    public final void onNewSession(String sessionId, Session session) {

        /**
         * Register call back for nodeEvent (Authentication|Saved)
         */
        session.context.callback(this);

        onGotNewSession(sessionId, session);

    }

    @Override
    public Context onNewContext(String contextName) {
        return new FileContext(contextName);
    }

    @Override
    public final void callback(NodeEvent nodeEvent) {

        if (nodeEvent.event==NodeEvent.Event.Authentication) {

            MessageObject messageObject = nodeEvent.messageObject;

            if (!messageObject.attributes.get("key").equals("admin")) {
                throw new AdminCommandNode.AuthenticationException("Authentication to AdminCommand");
            }

        }

        if (nodeEvent.event==NodeEvent.Event.ContextSaved) {

            String contextName = nodeEvent.messageObject.toString();
            if (contextName.endsWith(".backup")) return;

            /*
				chatlogDao.clear(contextName, false);
				Entity showcaseEntity = showCaseDao.getShowCase(botId);
				showcaseEntity.setProperty("timeStamp", new Date());
				showCaseDao.update(showcaseEntity);
			*/
        }
    }

    public final String process(T input) {

        RequestObject requestObject = createRequestObject(input);

        Session session = SessionManager.instance().get(requestObject.sessionId(), requestObject.contextName());

        String responseText = session.parse(requestObject.messageObject()).trim();

        try {

            return response(new ResponseObject(responseText));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void onGotNewSession(String sessionId, Session session);

    protected abstract RequestObject createRequestObject(T input);

    public abstract String response(ResponseObject responseObject) throws Exception;

}