package com.irene.scrapers;

import org.junit.Assert;
import org.junit.Test;

public class StockExchangeScraperTest {
    @Test
    public void testGetEur() {
        Assert.assertNotNull(StockExchangeScraper.getEur());
    }

    @Test
    public void testGetInfo() {
        Assert.assertNotEquals("null", StockExchangeScraper.getInfo());
    }
}
