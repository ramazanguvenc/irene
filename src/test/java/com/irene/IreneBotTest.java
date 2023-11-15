package com.irene;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

public class IreneBotTest {
    @Test
    public void testDownloadVideo() {
        String link = "https://twitter.com/tim_cook/status/1717270084018995529";
        String rs = IreneBot.downloadVideo(link);
        Assert.assertNotNull(rs);
        Assert.assertTrue(new File(rs).delete());
    }
}
