package com.irene.tasks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.entity.Subscription;
import com.irene.scrapers.BinanceScraper;
import com.irene.scrapers.CNBCScraper;
import com.irene.scrapers.CurrencyExchangeScraper;
import com.irene.scrapers.StockExchangeScraper;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;


public class DailyReportTask extends Task{
    
    private static final Logger logger = LogManager.getLogger(DailyReportTask.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
       String report =  getReport();
       java.util.List<Subscription> dailySubs = bot.getSubscriptionDao().getSpecificSubscriptionType("daily");
       for(Subscription sub : dailySubs){
            bot.execute(new SendMessage(sub.getChatID(), report)
            .parseMode(ParseMode.HTML));
            logger.info("executing DailyReportTask:\n" + report);
       }
    }

    public static String getReport() {
        logger.info("executing getReport");
        StringBuilder message = new StringBuilder();
        message.append("<b>Oh shit, here we go again ;_;</b>");
        message.append("\n\n<b>Parity -> </b>\n");

        message.append("USD/TRY - " +CurrencyExchangeScraper.getCurrency("USD", "TRY") + " TL\n");
        message.append("USDT/TRY - " +BinanceScraper.getCoinPrice("USDTTRY") + " TL\n");
        message.append("EUR/USD - " + StockExchangeScraper.getEur()+ " USD\n");
        message.append("USD/JPY - " + CurrencyExchangeScraper.getCurrency("USD", "YEN") + " JPY\n");
        message.append("USD/CNY - " + CurrencyExchangeScraper.getCurrency("USD", "CNY") + " CNY\n");
        message.append("USD/CAD - " + CurrencyExchangeScraper.getCurrency("USD", "CAD") + " CAD\n");
        message.append("------------------------------\n");

        message.append("<b>Have fun staying poor -> </b>\n");
        message.append("Hard-money - $" + BinanceScraper.getCoinPrice("BTCUSDT") + "\n");
        message.append("Future of France - $" + BinanceScraper.getCoinPrice("ETHUSDT") + "\n");
        message.append("------------------------------\n");

        message.append("<b>Treasury -> </b>\n");
        //String [] cnbc10Y = getColoredVersion(CNBCScraper.getQuote("US10Y").split("<>")) ;
        //String [] cnbc2Y = getColoredVersion(CNBCScraper.getQuote("US2Y").split("<>")) ;
        String [] cnbc10Y = CNBCScraper.getQuote("US10Y").split("<>");
        String [] cnbc2Y = CNBCScraper.getQuote("US2Y").split("<>");
        message.append("U.S 10 Year - " + cnbc10Y[0] + "  " +cnbc10Y[1]+ "\n");
        message.append("U.S 2 Year - " + cnbc2Y[0] + "  " +cnbc2Y[1]+ "\n");
        message.append("------------------------------\n");

        message.append(StockExchangeScraper.getInfo());

        //message.append("------------------------------\n");
        message.append("xoxo gossip girl");
        String finalMessage = message.toString();
        logger.info(finalMessage);
        return finalMessage;
        
        
    }

    private static String[] getColoredVersion(String[] cnbc) {
        if(!cnbc[1].contains("-")){
            cnbc[1] = "<p style = \"color:green;\">" + cnbc[1] + "</p>";
        }
        else{
            cnbc[1] = "<p style = \"color:red;\">" + cnbc[1] + "</p>";
        }
        return cnbc;
    }
}
