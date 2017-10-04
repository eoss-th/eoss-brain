package com.eoss.brain.net;

import com.eoss.brain.Session;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * Created by eoss-th on 8/15/17.
 */
public class FileContext extends Context {

    private File dir;

    public FileContext(File dir, String name) {
        super(name);
        this.dir = dir;
    }

    public FileContext(String name) {
        this(null, name);
    }

    public File getDir() {
        if (dir.isDirectory())
            return dir;
        return null;
    }

    private File getFile() {
        File file;
        if (dir!=null&&dir.isDirectory()) {
            file = new File(dir, name + SUFFIX);
        } else {
            file = new File(name + SUFFIX);
        }
        return file;
    }

    @Override
    public void doLoad(String name) throws Exception {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(getFile()), StandardCharsets.UTF_8));
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
    public void doSave(String name, Set<Node> dataSet) {
        OutputStreamWriter out = null;
        try {
            StringBuilder data = new StringBuilder();
            for (Node node:dataSet) {
                data.append(toString(node));
            }
            out = new OutputStreamWriter(
                    new FileOutputStream(getFile(), false), StandardCharsets.UTF_8);
            out.write(data.toString().trim());
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out!=null) try {out.close();} catch (Exception e) {}
        }
    }

}
