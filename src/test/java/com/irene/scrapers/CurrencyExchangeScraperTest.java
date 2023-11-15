package com.irene.scrapers;

import org.junit.Assert;
import org.junit.Test;

public class CurrencyExchangeScraperTest {
    @Test
    public void testGetCurrency() {
        Assert.assertNotNull(CurrencyExchangeScraper.getCurrency("USD", "TRY"));
    }

    
}
