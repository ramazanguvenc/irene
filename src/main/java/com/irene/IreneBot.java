package com.irene;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendVideo;

import config.Config;

import com.dao.SubscriptionDao;
import com.entity.Subscription;
import com.irene.scrapers.CurrencyExchangeScraper;
import com.irene.tasks.DailyReportTask;
import com.irene.tasks.Tasks;

import com.irene.tasks.UpOnlyTask;
import com.irene.tasks.VibeCheckTask;



public class IreneBot extends TelegramBot{


    boolean dailySubscriptionFirstTime = true;
    boolean initTaskFirstTime = true;
    private static IreneBot bot = null;
    private static final Logger logger = LogManager.getLogger(IreneBot.class);
    private SubscriptionDao subscriptionDao; 

    public SubscriptionDao getSubscriptionDao() {
        return subscriptionDao;
    }



    private IreneBot() {
        super(Config.getInstance().get("token"));
        subscriptionDao = new SubscriptionDao(); 
        init(); 
        //initTasks();
        
    }
    


    public static IreneBot getInstance(){
        if(bot == null){
            bot = new IreneBot();
        }
        return bot;
    }

    private void init() {
        this.setUpdatesListener(updates -> {
            updates.forEach(update -> {
                if(initTaskFirstTime){
                    initTaskFirstTime = false;
                    Tasks.initTasks();
                }
                if(update == null || update.message() == null || update.message().text() == null){

                }
                else{
                    String [] args = update.message().text().split(" ");
                    switch (args[0]) {
                        case "/cur":
                            this.execute(new SendMessage(update.message().chat().id(), "Currency" + CurrencyExchangeScraper.getCurrency("USD", "EUR").toString()));
                            break;
                        case "/sub":
                            handleSubscriber(update.message().text(), update.message().chat().id());
                            break;
                        case "/start":
                            this.execute(new SendMessage(update.message().chat().id(), "Hello " + update.message().from().firstName()));
                            logger.info("Initiating Tasks!");
                            if(dailySubscriptionFirstTime){
                                dailySubscriptionFirstTime = false;
                                Tasks.createDailyReportTask();
                                Tasks.createCheckVibeTask();
                            }
                            break;
                        case "/test":
                            this.execute(new SendMessage(update.message().chat().id(), DailyReportTask.getReport())
                                .parseMode(ParseMode.HTML)
                            );
    
                            break;

                        case "/testVibe":
                            this.execute(new SendMessage(update.message().chat().id(), VibeCheckTask.checkVibe())
                            .parseMode(ParseMode.HTML)
                            );
                            break;
                        case "/testuponly":
                            this.execute(new SendMessage(update.message().chat().id(), UpOnlyTask.taskTest(null))
                            .parseMode(ParseMode.HTML)
                            );
                            break;
                        case "/help":
                            this.execute(new SendMessage(update.message().chat().id(), "Help"));
                            break;
                        case "/video":
                            
                            try{
                                if(args.length == 1){
                                    this.execute(new SendMessage(update.message().chat().id(), "Please provide link! (/video [url])"));
                            }
                            else{
                                String link = args[1];
                                File video = getVideoPath(link);
                                
                                if(video == null || !video.exists()){
                                    this.execute(new SendMessage(update.message().chat().id(), "Video couldn't downloaded!"));
                                    break;
                                }
                                logger.info("Sending Video!");
                                this.execute(new SendVideo(update.message().chat().id(), video));
                                if(!video.delete()){
                                    logger.info("File couldn't deleted!");
                                }
                                }
                            }catch(Exception e){
                                //log later.
                            }
                            break;
                        default:
                            break;
                    }
                }
                
            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private File getVideoPath(String link) {
        logger.debug("Getting Video Path!");
        String path = downloadVideo(link);
        if(path == null){
            return null;
        }
        return new File(path);
    }

    private String downloadVideo(String link){
        String path = Config.getInstance().get("twitter_download_python_path");
        String outputPath = Config.getInstance().get("twitter_download_output_path") + generateRandomString(6) + ".mp4";
        logger.debug("Output Path: " + outputPath);
        //String param = "";
        ProcessBuilder Process_Builder = new
                                         ProcessBuilder("python3",path, link, outputPath)
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
                logger.info(Output_line);
            }
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            logger.error("Error while downloading video!");
            return null;
        }
        logger.debug("Finished downloading video!");
        return outputPath;
    }

    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            randomString.append(randomChar);
        }

        return randomString.toString();
    }


    private void handleSubscriber(String text, Long id) {
        String[] args = text.split(" ");
        if(args.length > 1){
            switch (args[1]) {
                case "daily":         
                    subscriptionDao.subscribe(new Subscription(id, "daily"));
                    logger.info(id + " added to daily subscribers");
                    break;

                case "vibe":
                    subscriptionDao.subscribe(new Subscription(id, "vibe"));
                    logger.info(id + " added to vibe subscribers");
                    break;
                    
                default:
                    break;
            }
        }
    }



}
