package com.eoss.brain.ext;

import com.eoss.brain.Session;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class GetHTTPExpression extends HTTPExpression {

    public GetHTTPExpression(Session session, String[] arguments) {
        super(session, arguments);
    }

    @Override
    public String execute(String input) {

        String [] args = parameterized(input, arguments);
        if (args.length==3) {

            String url = "https://" + args[2];
            return get(url, createParamMapFromQueryString(args[1]));

        } else if (args.length==2) {

            String url = "https://" + args[1];
            return get(url, null);
        }

        return super.execute(input);
    }

    protected final String get(String apiURL, Map<String, String> headerMap) {
        StringBuffer response = new StringBuffer();
        try {
            URL url = new URL(apiURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.addRequestProperty("User-Agent", "Mozilla/4.0");
            if (headerMap!=null) {
                for (Map.Entry<String, String> entry:headerMap.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;

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
