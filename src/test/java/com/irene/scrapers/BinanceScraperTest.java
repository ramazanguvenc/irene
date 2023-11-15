package com.irene.scrapers;

import org.junit.Assert;
import org.junit.Test;

public class BinanceScraperTest {
    @Test
    public void testGetCoinPrice() {
        Assert.assertNotNull(BinanceScraper.getCoinPrice("BTCUSDT"));
    }
}
