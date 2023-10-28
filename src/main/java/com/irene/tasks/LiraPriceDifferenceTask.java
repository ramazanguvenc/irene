package com.irene.tasks;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.irene.scrapers.CurrencyExchangeScraper;
import com.pengrad.telegrambot.request.SendMessage;

public class LiraPriceDifferenceTask extends Task{

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        StringBuilder message = new StringBuilder();
        message.append("USD-EUR: " +CurrencyExchangeScraper.getCurrency("USD", "EUR"));
        message.append("\nUSD-YEN: " +CurrencyExchangeScraper.getCurrency("USD", "YEN"));
        message.append("\nUSD-TRY: " + CurrencyExchangeScraper.getCurrency("USD", "TRY"));
        
        
    }
    
}
