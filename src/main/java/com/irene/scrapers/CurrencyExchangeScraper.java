package com.irene.scrapers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.dao.VideoLogDao;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;


public class CurrencyExchangeScraper {

    private static int counter = 0;
    private static final Logger logger = LogManager.getLogger(CurrencyExchangeScraper.class);

    

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
                logger.error(e.getMessage());
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