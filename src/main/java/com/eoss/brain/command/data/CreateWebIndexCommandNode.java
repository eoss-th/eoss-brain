package com.eoss.brain.command.data;

import com.eoss.brain.Session;
import com.eoss.brain.MessageObject;
import com.eoss.brain.net.Hook;
import com.eoss.brain.net.Node;
import com.eoss.brain.command.CommandNode;
import com.eoss.brain.context.GAEStorageContext;
import com.eoss.brain.net.Context;
import com.eoss.util.GAEWebStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by eossth on 7/31/2017 AD.
 */
public class CreateWebIndexCommandNode extends CommandNode {

    public CreateWebIndexCommandNode(Session session, String [] hooks) {
        super(session, hooks, Hook.Match.Head);
    }

    @Override
    public String execute(MessageObject messageObject) {

        String params = clean(messageObject.toString());
        String fileName = params.replace("https://", "").replace("http://", "");

        try {
            Document doc = Jsoup.connect(params).get();
            Elements elements = doc.body().select("a");

            Node newNode;
            Elements imgs;
            Element tagA, tagImg;
            String imgSource, linkResponse;
            Map<String, String> linkImageMap = new HashMap<>();
            for (int i=0;i<elements.size();i++) {
                tagA = elements.get(i);
                linkResponse = tagA.attr("href");
                if (linkResponse==null) continue;

                imgs = tagA.select("img");
                if (imgs!=null&&imgs.size()>0) {
                    tagImg = imgs.first();
                    imgSource = tagImg.attr("src");
                    if (imgSource!=null)
                        linkImageMap.put(linkResponse.trim(), imgSource.trim());
                }
            }

            Context context = new GAEStorageContext(fileName);
            elements = doc.body().select("a");
            Element parentOfA;
            String innerText;
            for (int i=0;i<elements.size();i++) {

                tagA = elements.get(i);

                linkResponse = tagA.attr("href");
                if (linkResponse==null) continue;
                linkResponse = linkResponse.trim();

                parentOfA = tagA.parent();
                innerText = parentOfA!=null?parentOfA.text():tagA.text();

                if (innerText==null) continue;
                innerText = innerText.trim();

                imgSource = linkImageMap.get(linkResponse);

                innerText = innerText.trim();

                if (imgSource!=null) {
                    linkResponse = fixLink(params, imgSource) + " " + innerText + System.lineSeparator() + fixLink(params, linkResponse) + ":" + "View";
                } else {
                    linkResponse = innerText + System.lineSeparator() + fixLink(params, linkResponse);
                }

                if (!innerText.isEmpty()) {
                    newNode = new Node(Hook.build(session.context.split(innerText)));
                    newNode.setResponse(linkResponse);
                    context.add(newNode);
                }
            }

            context.save();

            GAEWebStream gaeWebStream = new GAEWebStream(session.context.name + ".index");
            String indexData = gaeWebStream.read();
            if (!indexData.contains(fileName)) {
                gaeWebStream.write(indexData + fileName + System.lineSeparator());
            }

            session.context.load();

            return successMsg();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return failMsg();
    }

    private String fixLink (String domain, String link) {
        link = link.trim();
        if (!link.startsWith("http")) {

            if ( link.startsWith("/")) {
                try {
                    URI uri = new URI(domain);
                    link = uri.getScheme() + "://" + uri.getHost() + link;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    URI uri = new URI(domain);
                    if (uri.getPath().endsWith("/"))
                        link = uri.getScheme() + "://" + uri.getHost() + uri.getPath().substring(0, uri.getPath().lastIndexOf("/")) + "/" +  link;
                    else
                        link = uri.getScheme() + "://" + uri.getHost() + uri.getPath() + "/" +  link;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return link;
    }
}
