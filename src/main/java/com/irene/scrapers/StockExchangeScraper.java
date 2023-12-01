package com.irene.scrapers;

import java.io.IOException;
import java.text.DecimalFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;



public class StockExchangeScraper {
    
    private static final Logger logger = LogManager.getLogger(StockExchangeScraper.class);

    public static String getEur(){
        String url = "https://www.bloomberght.com/borsa/endeks/bist-100";
        try {
            Document doc = Jsoup.connect(url).get();
            String html = doc.selectXpath("/html/body/header/div/div[1]/div[1]/div/div/div/ul/li[5]/a/span/small[2]").toString();
            String result = html.split(">")[1].split("<")[0];
            logger.debug(result);
            double exchangeRate = Double.parseDouble(result.replace(",", "."));
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            String formattedExchangeRate = decimalFormat.format(exchangeRate);
            return formattedExchangeRate;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;    
        }    
        
    }

    public static String getInfo(){
        String url = "https://www.bloomberght.com/borsa/endeks/bist-100";
        
        StringBuilder result = new StringBuilder();
        try{
            result.append("<b>Boomer Coins -> </b>\n");
            Document doc = Jsoup.connect(url).get();         
            String html = doc.selectXpath("/html/body/header/div/div[1]/div[1]/div/div/div/ul/li[2]/a/span/small[2]").toString();
            String text = html.split("XU100 Index\">")[1].split("<")[0];
            result.append("XU100 Index: " + text + "\n");

            html = doc.selectXpath("/html/body/header/div/div[1]/div[1]/div/div/div/ul/li[7]/a/span").toString();
            text = html.split("value LastPrice\" data-secid=\"XAU Curncy\">")[1].split("<")[0];
            result.append("XAU/USD: $" + text + "\n");

            html = doc.selectXpath("/html/body/header/div/div[1]/div[1]/div/div/div/ul/li[8]/a").toString();
            text = html.split("value LastPrice\" data-secid=\"CO1 Comdty\">")[1].split("<")[0];
            result.append("Brent $" + text + "\n");
            result.append("WTI: $" + CNBCScraper.getQuote("@CL.1").split("<>")[0] + "\n");
            result.append("------------------------------\n");
            
        }
        catch(Exception e){
            logger.error(e.getMessage());
            return "null";
        }

        return result.toString().replace(",", "*").replace(".", ",").replace("*", ".");
    }

}
