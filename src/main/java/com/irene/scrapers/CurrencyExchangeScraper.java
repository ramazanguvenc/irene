package com.irene.scrapers;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;


public class CurrencyExchangeScraper {

    private static int counter = 0;

    

    public static BigDecimal getCurrency(String from, String to){
        String url = "https://www.google.com/search?q=" +from + "+" + to + "&hl=en";
        counter++;
        try {
            Document doc = Jsoup.connect(url).get();
            String html = doc.toString();
            String text = "";
            try{
                text = html.split("equals")[1].split("data-value=\"")[1].split("\"")[0];
            }catch(Exception e){
                if(from.equalsIgnoreCase("EUR"))
                    text = html.split("EUR 1 = USD ")[1].split(" ")[0];
            }          
            if(text == null) {
                if(counter > 10){
                    counter = 0;
                    return null;
                }
                else{
                    return getCurrency(from, to);
                }
            }
            counter = 0;
            
            BigDecimal result = new BigDecimal(text).setScale(2, RoundingMode.HALF_UP);
            return result;


        } catch (IOException | ArrayIndexOutOfBoundsException e) {
            counter = 0;
            return null;
        }

    }






}