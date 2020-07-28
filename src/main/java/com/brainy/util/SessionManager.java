package com.brainy.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import com.brainy.Session;
import com.brainy.command.wakeup.MenuWakeupCommandNode;
import com.brainy.net.Context;

public class SessionManager {

    public interface SessionManagerListener {
        void onNewSession(String sessionId, Session session);
        Context onNewContext(String contextName);
    }

    private static final int minutes = 10;

    private Map<String, Context> contextMap = new HashMap<>();

    private Map<String, Session> sessionMap = new HashMap<>();
    private Map<String, Long> sessionTimestampMap = new TreeMap<>();

    private Map<String, String> sessionToTokenMap = new HashMap<>();
    private Map<String, Session> tokenToSessionMap = new HashMap<>();

    private Map<String, List<String>> contextToSessionIdMap = new HashMap<>();

    private static SessionManager sessionManager;

    private SessionManagerListener sessionManagerListener;

    private Secure secure = new Secure();

    private SessionManager() {}

    public static SessionManager instance() {
        if (sessionManager==null) {
            sessionManager = new SessionManager();
        }
        return sessionManager;
    }

    public void register(SessionManagerListener sessionManagerListener) {
        this.sessionManagerListener = sessionManagerListener;
    }

    private synchronized Context getContext(String contextName) {

        Context context = contextMap.get(contextName);
        if (context==null) {
            context = this.sessionManagerListener.onNewContext(contextName);
            contextMap.put(contextName, context);
        }

        return context;
    }

    public synchronized Session get(String sessionId, String contextName) {

        if (sessionId==null) throw new RuntimeException("Null session Id:" + contextName);

        Session session = sessionMap.get(sessionId);

        if (session==null) {

            session = new Session(getContext(contextName));

            new MenuWakeupCommandNode(session).execute(null);

            String language = session.context.properties.get("language");

            session.context.locale(new Locale(language));

            sessionMap.put(sessionId, session);

            List<String> sessionIdList = contextToSessionIdMap.get(contextName);
            if (sessionIdList==null) {
                sessionIdList = new ArrayList<>();
            }
            sessionIdList.add(sessionId);
            contextToSessionIdMap.put(contextName, sessionIdList);

            String tokenId = secure.encryptPassword(Long.toString(System.currentTimeMillis()));
            session.setVariable("#tokenId", tokenId);
            sessionToTokenMap.put(sessionId, tokenId);
            tokenToSessionMap.put(tokenId, session);

            if (sessionManagerListener!=null) {
                sessionManagerListener.onNewSession(sessionId, session);
            }
        }

        sessionTimestampMap.put(sessionId, System.currentTimeMillis());

        return session;
    }

    public synchronized Session get(String tokenId) {
        return tokenToSessionMap.get(tokenId);
    }

    public synchronized void clearContext(String contextName) {
        contextMap.remove(contextName);
        List<String> sessionRemoveList = contextToSessionIdMap.get(contextName);
        if (sessionRemoveList!=null) {
            String tokenId;
            for (String sessionId:sessionRemoveList) {
                sessionMap.remove(sessionId);
                sessionTimestampMap.remove(sessionId);

                tokenId = sessionToTokenMap.get(sessionId);
                tokenToSessionMap.remove(tokenId);
                sessionToTokenMap.remove(sessionId);
            }
        }
    }

    public void clean() {

        synchronized (sessionMap) {
            long now = System.currentTimeMillis();

            List<String> sessionRemoveList = new ArrayList<>();
            //Clear Session
            Session session;
            List<String> sessionIdList;
            for (Map.Entry<String, Long> entry:sessionTimestampMap.entrySet()) {
                if (now-entry.getValue() > 1000 * 60 * minutes) {

                    session = sessionMap.get(entry.getKey());
                    sessionRemoveList.add(entry.getKey());

                    sessionIdList = contextToSessionIdMap.get(session.context.name);
                    sessionIdList.remove(entry.getKey());
                }
            }

            String tokenId;
            for (String sessionId:sessionRemoveList) {

                sessionMap.remove(sessionId);
                sessionTimestampMap.remove(sessionId);

                tokenId = sessionToTokenMap.get(sessionId);
                tokenToSessionMap.remove(tokenId);
                sessionToTokenMap.remove(sessionId);
            }
        }

        synchronized (contextMap) {
            List<String> contextRemoveList = new ArrayList<>();

            //Clear Context
            for (Map.Entry<String, List<String>> entry:contextToSessionIdMap.entrySet()) {
                if (entry.getValue().isEmpty()) {
                    contextRemoveList.add(entry.getKey());
                }
            }

            for (String contextName:contextRemoveList) {
                contextMap.remove(contextName);
                contextToSessionIdMap.remove(contextName);
            }
        }
    }

}

