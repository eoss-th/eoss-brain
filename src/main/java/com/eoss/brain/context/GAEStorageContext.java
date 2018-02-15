package com.eoss.brain.context;

import com.eoss.brain.net.Context;
import com.eoss.brain.net.Node;
import org.json.JSONArray;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by eoss-th on 8/15/17.
 */
public class GAEStorageContext extends Context {

    private final ExecutorService executorService;

    private static final String dataURL = "https://eoss-ai.appspot.com/s/";

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
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine())!=null) {
                sb.append(line);
            }
            nodeList.clear();
            nodeList.addAll(build(new JSONArray(sb.toString())));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (br!=null) try { br.close(); } catch (Exception e) {}
        }
    }

    @Override
    public void doSave(final String name, final List<Node> nodeList) {

        if (executorService==null)
            doFutureSave(name, nodeList);
        else
            executorService.execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            doFutureSave(name, nodeList);
                        }
                    }
            );
    }

    private void doFutureSave(String name, List<Node> nodeList) {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(dataURL + name + SUFFIX).openConnection();
            connection.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
            out.write(json(nodeList).toString());
            out.flush();
            out.close();
            InputStream in = connection.getInputStream();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
