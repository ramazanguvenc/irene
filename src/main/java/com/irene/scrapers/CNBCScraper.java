package com.irene.scrapers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class CNBCScraper {

    public static String getQuote(String ticker){
        String url = "https://www.cnbc.com/quotes/" + ticker;
        try {
            Document doc = Jsoup.connect(url).get();
            String htmlPrice = doc.selectXpath("//*[@id=\"quote-page-strip\"]/div[3]/div/div[2]/span[1]").toString();
            String htmlChange = doc.selectXpath("//*[@id=\"quote-page-strip\"]/div[3]/div/div[2]/span[2]").toString();
            String textPrice = htmlPrice.split(">")[1].split("<")[0];
            String textChange = htmlChange.split("span>")[1].split("<")[0];
            return textPrice + "<>" +textChange;
        } catch (IOException | ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
    
}
