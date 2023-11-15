package com.irene.scrapers;

import org.junit.Assert;
import org.junit.Test;

public class CNBCScraperTest {
    @Test
    public void testGetQuote() {
        Assert.assertNotNull(CNBCScraper.getQuote("US10Y"));
        Assert.assertNotNull(CNBCScraper.getQuote("US2Y"));
    }
}
