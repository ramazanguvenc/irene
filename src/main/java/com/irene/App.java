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


/**
 * Hello world!
 *
 */
public class App 
{

    private static final Logger logger = LogManager.getLogger(App.class);
    public static void main( String[] args )
    {
        Configurator.initialize(null, "log4j2.xml");

        logger.info("App started");    

        IreneBot bot = IreneBot.getInstance();
  
        //testDB();

        //printDB();
       
        //runPythonScript();

        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("Europe/Istanbul"));
        logger.debug("Date and time in Istanbul: " + df.format(date));            
    }

    private static void testDB() {
        SubscriptionDao subscriptionDao = new SubscriptionDao();
        Subscription subscription = new Subscription(667, "testType");
        subscriptionDao.subscribe(subscription);

        List<Subscription> subscriptions = subscriptionDao.getSubscriptions();
        subscriptions.forEach(s -> System.out.println(s.getId() + ", " + s.getChatID() + ", " + s.getType()));
    
    }

    private static void runPythonScript(){
        String path = Config.getInstance().get("twitter_download_python_path");
        logger.debug("path: ", path);
        String outputPath = Config.getInstance().get("twitter_download_output_path") + IreneBot.generateRandomString(6);
        String link = "https://twitter.com/catshouldnt/status/1689847527460511744";

        ProcessBuilder Process_Builder = new
                                         ProcessBuilder("python",path, link, outputPath)
                                         .inheritIO();

        Process Demo_Process;
        try {
            Demo_Process = Process_Builder.start();
            Demo_Process.waitFor();
            BufferedReader Buffered_Reader = new BufferedReader(
                                         new InputStreamReader(
                                         Demo_Process.getInputStream()
                                         ));
            String Output_line = "";

            while ((Output_line = Buffered_Reader.readLine()) != null) {
                logger.debug(Output_line);
            }
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage());
        }
        
        
        logger.debug("Finished downloading video!");
        
    }
}
