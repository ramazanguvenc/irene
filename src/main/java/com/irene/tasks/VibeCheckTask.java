package com.irene.tasks;

import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.entity.KeyValue;
import com.entity.Subscription;
import com.irene.IreneBot;
import com.irene.scrapers.BinanceScraper;
import com.irene.scrapers.CurrencyExchangeScraper;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

public class VibeCheckTask extends Task{

    private static final Logger logger = LogManager.getLogger(VibeCheckTask.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
       String report =  checkVibe();
       if(!report.equals("")){
            java.util.List<Subscription> vibeSubs = bot.getSubscriptionDao().getSpecificSubscriptionType("vibe");
            for(Subscription sub : vibeSubs){
                    bot.execute(new SendMessage(sub.getChatID(), report)
                    .parseMode(ParseMode.HTML));
                    logger.info("executing VibeCheckTask:\n" + report);
            }
       }

    }

    public static BigDecimal lastMaxUSDTTRYValue = new BigDecimal(28.27); 

    public static String checkVibe(){
        String result = "";
        BigDecimal currentUSDTTRYValue = new BigDecimal(BinanceScraper.getCoinPrice("USDTTRY"));
        BigDecimal difference = currentUSDTTRYValue.subtract(lastMaxUSDTTRYValue);
        BigDecimal threshhold = new BigDecimal(0.1);
        if(difference.compareTo(threshhold) > 0){
            lastMaxUSDTTRYValue = currentUSDTTRYValue;
            try{
                IreneBot.getInstance().keyValueDao.set(new KeyValue("maxusdtry", lastMaxUSDTTRYValue.toString()));
            }
            catch(Exception e){
                logger.info(e.getMessage());
            }
            result = "<b>UpOnly continues</b>\n<b>Current USDT-TRY value: </b>$" + currentUSDTTRYValue;
            result = result + "\n<b>Devlet kuru: $</b>" + CurrencyExchangeScraper.getCurrency("USD", "TRY").toString();
        }
        logger.info("VibeCheck! --- currentValue: " + currentUSDTTRYValue + " lastMaxValue: " + lastMaxUSDTTRYValue);
        
        return result;
    }
}
