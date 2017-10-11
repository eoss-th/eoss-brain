package com.eoss.brain.command.data;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.net.FileContext;
import com.eoss.brain.net.Node;
import com.eoss.brain.command.CommandNode;
import com.eoss.brain.net.Context;
import com.eoss.brain.net.GAEStorageContext;
import com.eoss.util.FileStream;
import com.eoss.util.GAEWebStream;
import com.eoss.util.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class ImportRawDataFromWebCommandNode extends CommandNode {

    private File dir;

    public ImportRawDataFromWebCommandNode(Session session, String [] hooks) {
        this(session, hooks, null);
    }

    public ImportRawDataFromWebCommandNode(Session session, String [] hooks, File dir) {
        super(session, hooks, Mode.MatchHead);
        this.dir = dir;
    }

    @Override
    public String execute(MessageObject messageObject) {

        String params = clean(messageObject.toString());

        try {
            Document doc = Jsoup.connect(params).userAgent("Mozilla/5.0").get();
            String content = doc.body().text();
            String [] sentences = content.split("\\s+");

            List<String> sentenceList = new ArrayList<>();

            String text, lastText;
            for (String sentence:sentences) {
                text = sentence.trim();
//            if (session.context.splitToList(text).size() > 5) {
                if (text.length() > 20) {
                    sentenceList.add(text);
                } else if (!sentenceList.isEmpty()) {
                    lastText = sentenceList.remove(sentenceList.size()-1);
                    sentenceList.add(lastText + " " + text);
                }
            }

            String fileName;
            Stream streamIndex;
            Context context;
            if (dir!=null && dir.isDirectory()) {
                fileName = params.replace("https://", "").replace("http://", "").replace("/", "-");
                streamIndex = new FileStream(dir, session.context.name + ".index");
                context = new FileContext(dir, fileName);
            } else {
                fileName = params.replace("https://", "").replace("http://", "");
                streamIndex = new GAEWebStream(fileName + ".index");
                context = new GAEStorageContext(fileName);
            }

            Node newNode = null;

            for (String sentence:sentenceList) {

                if (newNode!=null) {
                    newNode.addResponse(sentence);
                    context.add(newNode);
                }

                newNode = new Node(session.context.splitToList(sentence).toArray(new String[0]), null);
            }

            context.save();

            String [] indexData = streamIndex.read().split(System.lineSeparator());

            List<String> indexList = new ArrayList<>();

            for (String index:indexData) {
                if (index.trim().isEmpty()) continue;;
                indexList.add(index.trim());
            }

            indexList.add(fileName);

            if (dir!=null && dir.isDirectory()) {
                List<File> removeFileList = new ArrayList<>();
                while (indexList.size()>5) {
                    removeFileList.add(new File(dir, indexList.remove(0)));
                }

                for (File file:removeFileList) {
                    if (file.exists()) {
                        if (!file.delete()) break;
                    }
                }

            } else {
                while (indexList.size()>5) {
                    indexList.remove(0);
                }
            }

            StringBuilder indexString = new StringBuilder();

            for (String index:indexList) {
                indexString.append(index);
                indexString.append(System.lineSeparator());
            }

            streamIndex.write(indexString.toString().trim());

            session.context.load();

            return successMsg();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return failMsg();
    }

}
