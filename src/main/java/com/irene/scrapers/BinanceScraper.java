package com.irene.scrapers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.binance.connector.client.impl.SpotClientImpl;
import com.binance.connector.client.impl.spot.Market;

public class BinanceScraper {

    private static final Logger logger = LogManager.getLogger(BinanceScraper.class);
    
    public static String getCoinPrice(String symbol){
        try{
            SpotClientImpl client = new SpotClientImpl();
            Market market = client.createMarket();
            LinkedHashMap<String,Object> parameters = new LinkedHashMap<>();
            parameters.put("symbol", symbol);
            //parameters.put("type", "MINI");
            String result = market.ticker(parameters);
            JSONObject obj = new JSONObject(result);
            BigDecimal lastPriceBinance = new BigDecimal(obj.get("lastPrice").toString());
            lastPriceBinance = lastPriceBinance.setScale(2, RoundingMode.DOWN);
            parameters.clear();
            String rslt = lastPriceBinance.toString();
            return rslt;
        }catch(Exception e){
            logger.error("Binance getCoinPrice -> " + e.getMessage());
        }
        return null;
    }

}
