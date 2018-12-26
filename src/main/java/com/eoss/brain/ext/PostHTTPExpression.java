package com.eoss.brain.ext;

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
    public String execute() {

        if (arguments.length==4) {

            String url = "https://" + arguments[3];
            return post(url, createParamMapFromQueryString(arguments[1]), arguments[2]);

        } else if (arguments.length==3) {

            String url = "https://" + arguments[2];
            return post(url, null, arguments[1]);
        }

        return super.execute();
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
        return protectHTTPObject(response.toString());
    }
}
