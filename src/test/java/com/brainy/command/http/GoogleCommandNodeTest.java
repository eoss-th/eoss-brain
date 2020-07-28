package com.brainy.command.http;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.*;

/**
 * Created by eossth on 10/4/2017 AD.
 */
public class GoogleCommandNodeTest {
    
    public static void main(String[]args) throws Exception {
        Map<String, String> googleMap = new TreeMap<>();

        googleMap.put("Albania", "https://www.google.al");
        googleMap.put("Algeria", "https://www.google.dz");
        googleMap.put("Argentina", "https://www.google.com.ar");
        googleMap.put("Armenia", "https://www.google.am");
        googleMap.put("Australia", "https://www.google.com.au");
        googleMap.put("Austria", "https://www.google.at");
        googleMap.put("Azerbaijan", "https://www.google.az");
        googleMap.put("Bahrain", "https://www.google.com.bh");
        googleMap.put("Bangladesh", "https://www.google.com.bd");
        googleMap.put("Belarus", "https://www.google.by");
        googleMap.put("Belgium", "https://www.google.be");
        googleMap.put("Bolivia", "https://www.google.com.bo");
        googleMap.put("Bosnia and Herzegovina", "https://www.google.ba");
        googleMap.put("Brazil", "https://www.google.com.br");
        googleMap.put("Bulgaria", "https://www.google.bg");
        googleMap.put("Cambodia", "https://www.google.com.kh");
        googleMap.put("Canada", "https://www.google.ca");
        googleMap.put("Chile", "https://www.google.cl");
        googleMap.put("China", "https://www.google.cn");
        googleMap.put("Colombia", "https://www.google.com.co");
        googleMap.put("Costa Rica", "https://www.google.co.cr");
        googleMap.put("Croatia", "https://www.google.hr");
        googleMap.put("Cuba", "https://www.google.com.cu");
        googleMap.put("Cyprus", "https://www.google.com.cy");
        googleMap.put("Czech Republic", "https://www.google.cz");
        googleMap.put("Denmark", "https://www.google.dk");
        googleMap.put("Dominican Republic", "https://www.google.com.do");
        googleMap.put("Ecuador", "https://www.google.com.ec");
        googleMap.put("Egypt", "https://www.google.com.eg");
        googleMap.put("El Salvador", "https://www.google.com.sv");
        googleMap.put("Estonia", "https://www.google.ee");
        googleMap.put("Ethiopia", "https://www.google.com.et");
        googleMap.put("Finland", "https://www.google.fi");
        googleMap.put("France", "https://www.google.fr");
        googleMap.put("Georgia", "https://www.google.ge");
        googleMap.put("Germany", "https://www.google.de");
        googleMap.put("Ghana", "https://www.google.com.gh");
        googleMap.put("Greece", "https://www.google.gr");
        googleMap.put("Guatemala", "https://www.google.com.gt");
        googleMap.put("Honduras", "https://www.google.hn");
        googleMap.put("Hong Kong", "https://www.google.com.hk");
        googleMap.put("Hungary", "https://www.google.co.hu");
        googleMap.put("Iceland", "https://www.google.is");
        googleMap.put("India", "https://www.google.co.in");
        googleMap.put("Indonesia", "https://www.google.co.id");
        googleMap.put("Iraq", "https://www.google.com.iq");
        googleMap.put("Ireland", "https://www.google.ie");
        googleMap.put("Israel", "https://www.google.co.il");
        googleMap.put("Italy", "https://www.google.it");
        googleMap.put("Japan", "https://www.google.co.jp");
        googleMap.put("Jordan", "https://www.google.jo");
        googleMap.put("Kenya", "https://www.google.co.ke");
        googleMap.put("Kuwait", "https://www.google.com.kw");
        googleMap.put("Laos", "https://www.google.la");
        googleMap.put("Latvia", "https://www.google.lv");
        googleMap.put("Lebanon", "https://www.google.com.lb");
        googleMap.put("Libya", "https://www.google.com.ly");
        googleMap.put("Lithuania", "https://www.google.lt");
        googleMap.put("Luxembourg", "https://www.google.lu");
        googleMap.put("Macedonia", "https://www.google.mk");
        googleMap.put("Malaysia", "https://www.google.com.my");
        googleMap.put("Malta", "https://www.google.com.mt");
        googleMap.put("Mexico", "https://www.google.com.mx");
        googleMap.put("Montenegro", "https://www.google.me");
        googleMap.put("Morocco", "https://www.google.co.ma");
        googleMap.put("Nepal", "https://www.google.com.np");
        googleMap.put("Netherlands", "https://www.google.nl");
        googleMap.put("New Zealand", "https://www.google.co.nz");
        googleMap.put("Nicaragua", "https://www.google.com.ni");
        googleMap.put("Nigeria", "https://www.google.com.ng");
        googleMap.put("Norway", "https://www.google.no");
        googleMap.put("Oman", "https://www.google.com.om");
        googleMap.put("Pakistan", "https://www.google.com.pk");
        googleMap.put("Panama", "https://www.google.com.pa");
        googleMap.put("Paraguay", "https://www.google.com.py");
        googleMap.put("Peru", "https://www.google.com.pe");
        googleMap.put("Philippines", "https://www.google.com.ph");
        googleMap.put("Poland", "https://www.google.pl");
        googleMap.put("Portugal", "https://www.google.pt");
        googleMap.put("Puerto Rico", "https://www.google.com.pr");
        googleMap.put("Qatar", "https://www.google.com.qa");
        googleMap.put("Romania", "https://www.google.ro");
        googleMap.put("Russia", "https://www.google.ru");
        googleMap.put("Saudi Arabia", "https://www.google.com.sa");
        googleMap.put("Serbia", "https://www.google.rs");
        googleMap.put("Singapore", "https://www.google.com.sg");
        googleMap.put("Slovakia", "https://www.google.sk");
        googleMap.put("Slovenia", "https://www.google.si");
        googleMap.put("South Africa", "https://www.google.co.za");
        googleMap.put("South Korea", "https://www.google.co.kr");
        googleMap.put("Spain", "https://www.google.es");
        googleMap.put("Sri Lanka", "https://www.google.lk");
        googleMap.put("Srilanka", "https://www.google.lk");
        googleMap.put("State of Palestine", "https://www.google.ps");
        googleMap.put("Sweden", "https://www.google.se");
        googleMap.put("Switzerland", "https://www.google.ch");
        googleMap.put("Taiwan", "https://www.google.com.tw");
        googleMap.put("Tanzania", "https://www.google.co.tz");
        googleMap.put("Thailand", "https://www.google.co.th");
        googleMap.put("Tunisia", "https://www.google.tn");
        googleMap.put("Turkey", "https://www.google.com.tr");
        googleMap.put("Ukraine", "https://www.google.com.ua");
        googleMap.put("United Arab Emirates", "https://www.google.ae");
        googleMap.put("United Kingdom", "https://www.google.co.uk");
        googleMap.put("United States", "https://www.google.com");
        googleMap.put("Uruguay", "https://www.google.com.uy");
        googleMap.put("Venezuela", "https://www.google.co.ve");
        googleMap.put("Vietnam", "https://www.google.com.vn");


        for (Map.Entry<String, String> entry:googleMap.entrySet()) {
            System.out.printf("googleMap.put(\"%s\", \"%s\");", entry.getKey(), entry.getValue() );
            System.out.println();
        }

        Document doc = Jsoup.connect("https://translate.google.com/#en/ja/yes").userAgent("Mozilla/5.0").get();

        System.out.println(doc);
    }

}