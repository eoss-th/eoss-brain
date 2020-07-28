package com.brainy.service;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResponseObjectTest {

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

    @Test
    public void testResponse() {
        System.out.println(createTextTest("https://m.pantip.com/tag/TVI_(หุ้น)"));
        System.out.println(createTextTest("line://oaMessage/@xux5590j/?ดูกราฟ%20TVI"));
    }

}