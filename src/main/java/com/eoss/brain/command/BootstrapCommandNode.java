package com.eoss.brain.command;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Created by eossth on 7/31/2017 AD.
 */
@Deprecated
public class BootstrapCommandNode extends CommandNode {

    public BootstrapCommandNode(Session session) {

        super(session);
    }

    @Override
    public String execute(MessageObject messageObject) {

        File file = new File("session.bootstrap");
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream("session.bootstrap"), StandardCharsets.UTF_8));
            String line;
            while (true) {
                line = br.readLine();
                if (line == null) break;
                line = line.trim();
                if (line.isEmpty()) continue;
                session.parse(MessageObject.build(messageObject, line));
            }
            return successMsg();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br!=null) try { br.close(); } catch (Exception e) {}
        }
        return file.getAbsolutePath();
    }
}
