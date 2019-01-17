package com.eoss.brain.ext;

import com.eoss.brain.MessageObject;
import com.eoss.brain.Session;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class PostHTTPExpression extends HTTPExpression {
    public PostHTTPExpression(Session session, String[] arguments) {
        super(session, arguments);
    }

    @Override
    public String execute(MessageObject messageObject) {

        String [] args = parameterized(messageObject, arguments);
        if (args.length==4) {

            String url = "https://" + args[3];
            super.updateParameters(messageObject, post(url, createParamMapFromQueryString(args[1]), args[2]));
            return "";

        } else if (args.length==3) {

            String url = "https://" + args[2];
            super.updateParameters(messageObject, post(url, null, args[1]));
            return "";
        }

        return super.execute(messageObject);
    }

    protected final String post(String apiURL, Map<String, String> headerMap, String body) {

        StringBuffer response = new StringBuffer();
        try {
            URL url = new URL(apiURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            if (headerMap!=null) {
                for (Map.Entry<String, String> entry:headerMap.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(body.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();

            int respCode = conn.getResponseCode();  // New items get NOT_FOUND on PUT
            String line;

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

        } catch (Exception e) {
            response.append(e.getMessage());
        }
        return response.toString();
    }
}
