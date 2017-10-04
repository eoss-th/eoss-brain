package com.eoss.brain.net;

import com.eoss.brain.Session;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by eoss-th on 8/15/17.
 */
public class GAEStorageContext extends Context {

    private final ExecutorService executorService;

    private static final String dataURL = "https://eoss-chatbot.appspot.com/s/";

    public GAEStorageContext(String name) {
        this(name, Executors.newFixedThreadPool(1));
    }

    public GAEStorageContext(String name, ExecutorService executorService) {
        super(name);
        this.executorService = executorService;
    }

    @Override
    public void doLoad(String name) throws Exception {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new URL(dataURL + name + SUFFIX).openStream(), StandardCharsets.UTF_8));
            String line;
            Node node = null;
            String[] hookWeight;
            dataSet.clear();
            while (true) {
                line = br.readLine();
                if (line == null) break;
                if (line.isEmpty()) continue;
                if (!line.startsWith(" ")) {
                    node = parse(line);
                    dataSet.add(node);
                } else {
                    hookWeight = line.trim().split("\t");
                    addResponse(node, hookWeight[0], hookWeight[1]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (br!=null) try { br.close(); } catch (Exception e) {}
        }
    }

    @Override
    public void doSave(final String name, final Set<Node> dataSet) {

        if (executorService==null)
            doFutureSave(name, dataSet);
        else
            executorService.execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            doFutureSave(name, dataSet);
                        }
                    }
            );
    }

    private void doFutureSave(String name, Set<Node> dataSet) {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(dataURL + name + SUFFIX).openConnection();
            connection.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);

            StringBuilder data = new StringBuilder();

            for (Node node:dataSet) {
                data.append(toString(node));
            }

            out.write(data.toString().trim());
            out.flush();
            out.close();

            InputStream in = connection.getInputStream();

            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
