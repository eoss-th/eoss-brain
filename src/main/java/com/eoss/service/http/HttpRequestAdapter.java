package com.eoss.service.http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.eoss.util.SessionManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.eoss.brain.MessageObject;
import com.eoss.brain.NodeEvent;
import com.eoss.brain.Session;
import com.eoss.brain.command.talk.Choice;
import com.eoss.brain.command.talk.Question;
import com.eoss.brain.context.FileContext;
import com.eoss.brain.net.Context;
import com.eoss.brain.net.ContextListener;
import com.eoss.brain.net.Hook;
import com.eoss.brain.net.SessionListener;

public class HttpRequestAdapter implements SessionManager.SessionManagerListener {

    public HttpServletRequest request;
    public HttpServletResponse response;

    public Session session;

    public String accountId;
    public String botId;
    public String contextName;

    public String serverName;
    public String sessionId;
    public String message;
    private String cmd;
    public boolean isAdminCmd;

    public MessageObject messageObject;

    public static class Reply {

        public static class Text {
            private final String text;
            public Text(String text) {
                this.text = text;
            }
            @Override
            public String toString() {
                return text;
            }
        }
        public static class Image {
            public final String url;
            public Image(String url) {
                this.url = url;
            }
            @Override
            public String toString() {
                return url;
            }
        }
        public static class Audio {
            public final String url;
            public Audio(String url) {
                this.url = url;
            }
            @Override
            public String toString() {
                return url;
            }
        }

        private final String responseText;

        private final List<Question> questionList;

        public final List<Object> messageList = new ArrayList<>();

        public Reply(String responseText, List<Question> questionList) {

            boolean hasQuestion = questionList!=null && !questionList.isEmpty();
            String [] lines = responseText.split("\n\n\n");
            List<String> messages = Arrays.asList(lines).stream().filter(new Predicate<String>() {
                @Override
                public boolean test(String t) {
                    return !t.trim().isEmpty();
                }
            }).collect(Collectors.toList());

            //Remove Last messsage if there is any question
            if (hasQuestion) {
                if (!messages.isEmpty()) {
                    messages.remove(messages.size()-1);
                }
            }

            String [] replyTokens;
            for (String message:messages) {
                replyTokens = message.split(" ", 2);
                String firstToken = replyTokens[0].toLowerCase();
                if (firstToken.startsWith("https")) {

                    if (firstToken.endsWith("png") || firstToken.endsWith("jpg") || firstToken.endsWith("jpeg")|| firstToken.endsWith("gif") || firstToken.endsWith("PNG") || firstToken.endsWith("JPG") || firstToken.endsWith("JPEG") || firstToken.endsWith("GIF")) {
                        messageList.add(new Image(replyTokens[0]));
                    } else if (firstToken.endsWith("m4a")) {
                        messageList.add(new Audio(replyTokens[0]));
                    } else {
                        messageList.add(new Text(replyTokens[0]));
                    }

                    if (replyTokens.length==2) {
                        messageList.add(new Text(replyTokens[1]));
                    }

                } else {
                    messageList.add(new Text(message));
                }
            }

            if (hasQuestion) {
                messageList.add(questionList);
            }

            this.responseText = responseText;
            this.questionList = questionList;
        }

        @Override
        public String toString() {
            return this.responseText;
        }

        public static Reply build(String replyJSON) {
            JSONObject replyObj = new JSONObject(replyJSON);
            String responseText = replyObj.getString("text");
            List<Question> questionList = new ArrayList<>();

            try {
                Question question;
                Choice choice;

                JSONArray questionArray = replyObj.getJSONArray("questions");
                JSONArray choiceArray;

                JSONObject questionObj, choiceObj;
                List<Choice> choiceList;

                String parent, label, imageURL, linkURL;
                for (int i=0;i<questionArray.length();i++) {
                    questionObj = questionArray.getJSONObject(i);

                    choiceList = new ArrayList<>();
                    choiceArray = questionObj.getJSONArray("choices");

                    for (int j=0;j<choiceArray.length();j++) {
                        choiceObj = choiceArray.getJSONObject(j);

                        try {
                            parent = choiceObj.getString("parent");
                        } catch (JSONException e) {
                            parent = null;
                        }

                        try {
                            label = choiceObj.getString("label");
                        } catch (JSONException e) {
                            label = null;
                        }

                        try {
                            imageURL = choiceObj.getString("imageURL");
                        } catch (JSONException e) {
                            imageURL = null;
                        }

                        try {
                            linkURL = choiceObj.getString("linkURL");
                        } catch (JSONException e) {
                            linkURL = null;
                        }

                        choice = new Choice(parent, label, imageURL, linkURL);
                        choiceList.add(choice);
                    }

                    try {
                        parent = questionObj.getString("parent");
                    } catch (JSONException e) {
                        parent = null;
                    }

                    try {
                        label = questionObj.getString("label");
                    } catch (JSONException e) {
                        label = null;
                    }

                    try {
                        imageURL = questionObj.getString("imageURL");
                    } catch (JSONException e) {
                        imageURL = null;
                    }

                    question = new Question(parent, label, imageURL, choiceList);
                    questionList.add(question);
                }

            } catch (JSONException e) {
            }

            return new Reply(responseText, questionList);
        }

        public String toJSONString() {

            JSONObject obj = new JSONObject();
            obj.put("text", this.responseText);

            boolean hasQuestion = questionList!=null && !questionList.isEmpty();
            if (hasQuestion) {

                JSONArray questionArray = new JSONArray();
                JSONObject questionObj;
                JSONArray choiceArray;
                JSONObject choiceObj;
                for (Question question:questionList) {
                    questionObj = new JSONObject();
                    questionObj.put("parent", question.parent);
                    questionObj.put("label", question.label);
                    questionObj.put("imageURL", question.imageURL);

                    choiceArray = new JSONArray();
                    for (Choice choice:question.choices) {
                        choiceObj = new JSONObject();
                        choiceObj.put("parent", choice.parent);
                        choiceObj.put("label", choice.label);
                        choiceObj.put("imageURL", choice.imageURL);
                        choiceObj.put("linkURL", choice.linkURL);
                        choiceArray.put(choiceObj);
                    }
                    questionObj.put("choices", choiceArray);

                    questionArray.put(questionObj);
                }
                obj.put("questions", questionArray);
            }

            return obj.toString();
        }

    }

    public HttpRequestAdapter(HttpServletRequest request, HttpServletResponse response) {

        this.request = request;
        this.response = response;

        serverName = request.getServerName();
        message = request.getParameter("message");

        String [] uris = request.getRequestURI().split("/");
        if (uris.length>3) {
            accountId = uris[2];
            botId = uris[3];
            contextName = accountId + "/" + botId;
        }

        sessionId = botId + request.getParameter("sessionId");

        cmd = request.getParameter("cmd");

        HttpSession httpSession = request.getSession();

        String role = (String) httpSession.getAttribute("role");
        String token = (String) httpSession.getAttribute("token");

        if (cmd!=null && accountId!=null && role != null && role.equals("administrator")) {

            String sessionAccountId = (String) httpSession.getAttribute("accountId");
            if (sessionAccountId==null || !accountId.equals(sessionAccountId)) throw new RuntimeException("To access the admin you must have the session:" + accountId);

            SessionManager sessionManager = SessionManager.instance();

            String type = request.getParameter("header");

            if (type!=null) {

                if (cmd.equals("import")) {

                    if (type.equals("type1")) {

                        isAdminCmd = true;
                        message = "ใส่ข้อมูลถามตอบ\n" + message;

                    } else if(type.equals("type2")) {

                        isAdminCmd = true;
                        message = "ใส่ข้อมูลดิบ\n" + message;

                    }

                    httpSession.setAttribute("importMessage", message);
                    sessionManager.clearContext(contextName);

                } else if (cmd.equals("clear")) {

                    isAdminCmd = true;
                    message = "ล้างข้อมูล";

                    httpSession.setAttribute("importMessage", "");
                    sessionManager.clearContext(contextName);

                } else if (cmd.equals("get")) {

                    if (type.equals("type1")) {

                        isAdminCmd = true;
                        message = "ดูข้อมูลถามตอบ";

                    } else if (type.equals("type2")) {

                        isAdminCmd = true;
                        message = "ดูข้อมูลดิบ";

                    } else if (type.equals("mermaid")) {

                        isAdminCmd = true;
                        message = "ดูข้อมูลกราฟ";

                    }
                }

            } else if (cmd.equals("load")) {

                isAdminCmd = true;
                message = "โหลดข้อมูล";

            }
        }

        if (message==null) {
            message = "";
        }

        //Clean Message
        message = message.replace(", ", " ").replace("!", "").replace("?", "");

        messageObject = MessageObject.build(message);

        if (token!=null) {
            messageObject.attributes.put("userId", token);
        }

    }

    protected final Session getSession() {

        SessionManager sessionManager = SessionManager.instance();

        sessionManager.register(this);

        Session session = sessionManager.get(sessionId, contextName);

        final String unknown = session.context.properties.get("unknown");

        session.callback(new SessionListener() {

            @Override
            public void callback(NodeEvent nodeEvent) {
                if (nodeEvent.event==NodeEvent.Event.HesitateConfidence) {
                    request.setAttribute("hesitate", Hook.toString(nodeEvent.node.hookList()));
                } else if (nodeEvent.event==NodeEvent.Event.LowConfidence) {
                    request.setAttribute("unknown", unknown);
                } else if (nodeEvent.event==NodeEvent.Event.Question) {
                    request.setAttribute("questionList", nodeEvent.messageObject.attributes.get("Question"));
                }
            }

        });

        session.context.callback(new ContextListener() {

            @Override
            public void callback(NodeEvent nodeEvent) {
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

        });

        String sessionAccountId = (String) request.getSession().getAttribute("accountId");

        if ( sessionAccountId!=null && accountId.equals(sessionAccountId) ) {
            String tokenId = (String) request.getSession().getAttribute("token");
            session.context.admin(Arrays.asList(tokenId));
            //session.learning = true;
        } else {
            session.learning = false;
        }

        this.session = session;

        return session;
    }

    public boolean isGetPropertyCommand() {
        return cmd!=null && cmd.equals("property");
    }

    public final void response(String reply) {
        try {
            response(new Reply(reply, null));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

	/*
	public void responseText(Reply reply) {

		response = srp.setRespHead(response,"*");
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		try {

			response.getWriter().print(reply);
			log(reply.toString());

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	*/

    public String response(Reply reply) throws Exception {

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String replyJSON = reply.toJSONString();
        response.getWriter().print(replyJSON);

        return replyJSON;
    }

    public static void main(String [] args) {
        System.out.println(createTextTest("https://m.pantip.com/tag/TVI_(หุ้น)"));
        System.out.println(createTextTest("line://oaMessage/@xux5590j/?ดูกราฟ%20TVI"));
    }

    private static String createTextTest(String text) {
        String regex = "(https?|ftp|file|line)://[\u0E00-\u0E7F-a-zA-Z0-9+&@#/%?=~_\\(\\)|!:,.;]*[\u0E00-\u0E7F-a-zA-Z0-9+&@#/%=~_\\(\\)|]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        String url;
        String result = text;

        String youtubeThumbnail;
        while (matcher.find()) {
            url = matcher.group();

            youtubeThumbnail = null;
            if (url.startsWith("https://youtu.be/")) {
                youtubeThumbnail = url.replace("https://youtu.be/", "");
            }
            if (url.startsWith("https://www.youtube.com/watch?v=")) {
                youtubeThumbnail = url.replace("https://www.youtube.com/watch?v=", "");
            }
            if (youtubeThumbnail!=null) {
                youtubeThumbnail = "http://i3.ytimg.com/vi/" + youtubeThumbnail + "/maxresdefault.jpg";
                result = result.replace(url, "<a href=\"" + url + "\" target=\"_blank\"><div class=\"eoss_image_head\" style=\"background-image: url('" + youtubeThumbnail + "'); background-size: contain\"></div></a>");
                continue;
            }

            result = result.replace(url, "<a href=\"" + url + "\" target=\"_blank\">" + url + "</a>");
        }

        result = result.replace("\n", "<br/>");
        return result;
    }

    @Override
    public void onNewSession(String sessionId, Session session) {
        session.setVariable("#channel", "web");
        session.setVariable("#targetId", sessionId);
        //usageStatsDAO.addSessionCount(requestAdapter.botId);
    }

    @Override
    public Context createContext(String contextName) {
        return new FileContext(contextName);
    }


}
