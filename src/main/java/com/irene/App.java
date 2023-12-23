package com.irene;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import com.dao.SubscriptionDao;
import com.entity.Subscription;
import config.Config;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Hello world!
 *
 */
public class App 
{

    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final Logger logger = LogManager.getLogger(App.class);
    public static void main( String[] args )
    {
        Configurator.initialize(null, "log4j2.xml");

        logger.info("App started");    
       
        IreneBot bot = IreneBot.getInstance();
        

        //testDB();

        //printDBire();
       
        //runPythonScript();

    
    }

 

    private static void testDB() {
        SubscriptionDao subscriptionDao = new SubscriptionDao();
        Subscription subscription = new Subscription(667, "testType");
        subscriptionDao.subscribe(subscription);

        List<Subscription> subscriptions = subscriptionDao.getSubscriptions();
        subscriptions.forEach(s -> System.out.println(s.getId() + ", " + s.getChatID() + ", " + s.getType()));
    
    }

    
}
