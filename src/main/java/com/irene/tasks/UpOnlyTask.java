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
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

public class UpOnlyTask extends Task {

    private static final Logger logger = LogManager.getLogger(UpOnlyTask.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
       //logger.info("executing UpOnlyTask:\n");
       String report =  task();
       if(!report.equals("")){
            java.util.List<Subscription> vibeSubs = bot.getSubscriptionDao().getSpecificSubscriptionType("vibe");
            for(Subscription sub : vibeSubs){
                    bot.execute(new SendMessage(sub.getChatID(), report)
                    .parseMode(ParseMode.HTML));
                    logger.debug("executing UpOnlyTask:\n" + report);
            }
       }

    }

    public static BigDecimal lastMaxBTCUSDTValue = new BigDecimal(34000); 

    public static String task(){
        String result = "";
  
        BigDecimal currentBTCUSDTValue = new BigDecimal(BinanceScraper.getCoinPrice("BTCUSDT"));
        BigDecimal difference = currentBTCUSDTValue.subtract(lastMaxBTCUSDTValue);
        boolean isPositive = true;
        if(difference.compareTo(BigDecimal.ZERO) < 0){
            isPositive = false;
            difference = difference.multiply(BigDecimal.valueOf(-1));
        }
        BigDecimal threshhold = lastMaxBTCUSDTValue.multiply(BigDecimal.valueOf(0.05));
        if(difference.compareTo(threshhold) > 0){
            lastMaxBTCUSDTValue = currentBTCUSDTValue;
            try{
                IreneBot.getInstance().keyValueDao.set(new KeyValue("btcusdtmax", lastMaxBTCUSDTValue.toString()));
            }catch(Exception e){
                logger.info(e.getMessage());
            }
            if(isPositive){
                result = "<b>UpOnly continues</b>\n<b>Current BTC-USDT value: </b>$" + currentBTCUSDTValue;
            }
            else{
                result = "<b>*Sad crypto bro noises*</b>\n<b>Current BTC-USDT value: </b>$" + currentBTCUSDTValue;
            }
        }
        logger.debug("UpOnly! --- currentValue: " + currentBTCUSDTValue + " lastMaxValue: " + lastMaxBTCUSDTValue);
        logger.debug("UpOnly! --- difference: " + difference + " threshhold: " + threshhold);
        //logger.info("UpOnly --- result: " + result);
        return result;
    }

    public static String taskTest(BigDecimal currentBTCUSDTValue){
        if(currentBTCUSDTValue == null){
           currentBTCUSDTValue = new BigDecimal(BinanceScraper.getCoinPrice("BTCUSDT"));
        }
        return  "<b>UpOnly continues</b>\n<b>Current BTC-USDT value: </b>$" + currentBTCUSDTValue;
    }

}
