package com.irene.scrapers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;

import org.json.JSONObject;

import com.binance.connector.client.impl.SpotClientImpl;
import com.binance.connector.client.impl.spot.Market;

public class BinanceScraper {
    
    public static String getCoinPrice(String symbol){
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
        return lastPriceBinance.toString();
    }

}
