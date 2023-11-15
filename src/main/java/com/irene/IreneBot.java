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
import com.dao.VideoLogDao;
import com.entity.Subscription;
import com.entity.VideoLog;
import com.irene.scrapers.BinanceScraper;
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
    private VideoLogDao videoLogDao;

    public SubscriptionDao getSubscriptionDao() {
        return subscriptionDao;
    }



    private IreneBot() {
        super(Config.getInstance().get("token"));
        subscriptionDao = new SubscriptionDao();
        videoLogDao = new VideoLogDao(); 
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
                    try{
                        String [] args = update.message().text().split(" ");
                        Long chatId = update.message().chat().id();
                        switch (args[0]) {
                            case "/p":
                                System.out.println("debug:: " + args[1]);
                                if(args.length <= 1){
                                    _sendMessage(chatId, "example-> /p ETHUSD");
                                    break;
                                }
                                else{
                                    if(args[1].length() < 4 && !(args[1].toUpperCase().contains("usd"))){
                                        args[1] = args[1] + "usdt"; //add usdt at the end if user decides to write /p btc etc.
                                    }
                                    _sendMessage(chatId, "Price of " + args[1].toUpperCase() + " = " + 
                                                    BinanceScraper.getCoinPrice(args[1].toUpperCase()));
                                    break;
                                }                               
                            case "/sub":
                                handleSubscriber(update.message().text(), chatId);
                                break;
                            case "/start":
                                _sendMessage(chatId, "Welcome back!, starting now");
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
                                logger.info("trying to download video for given url:");
                                try{
                                    if(args.length == 1){
                                        _sendMessage(chatId, "Please provide link! (/video [url])");
                                }
                                else{
                                    String link = args[1];
                                    videoLogDao.add(new VideoLog(update.message().chat().firstName(), update.message().chat().id(), link));
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
                                    logger.error("exception video - > " + e.getMessage());
                                }
                                break;
                            default:
                                break;
                        }
                    }catch(Exception e){
                        logger.error("exception general -> " + e.getMessage());
                    }
                }
                
            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void _sendMessage(Long chatId, String msg) {
        this.execute(new SendMessage(chatId, msg));
    }



    private File getVideoPath(String link) {
        String path = downloadVideo(link);
        if(path == null){
            return null;
        }
        return new File(path);
    }

    private String downloadVideo(String link){
        //String path = Config.getInstance().get("twitter_download_python_path");
        String path = Config.getInstance().get("twitter_download_go_path");
        String outputPath = Config.getInstance().get("twitter_download_output_path") + generateRandomString(6) + ".mp4";
        
    
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("go", "run", ".", "-url", link, "-destination", outputPath);      
            processBuilder.directory(new File(path));
            processBuilder.redirectErrorStream(true);
            
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            logger.debug("exitCode: " + exitCode);
            if(exitCode != 0){
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String errorLine;
                    logger.error("Error Details:");
                    while ((errorLine = errorReader.readLine()) != null) {
                        logger.error(errorLine);
                    }
                }catch(Exception e){
                    logger.error("exception->" + e.getMessage());
                }
            }

        } catch (IOException | InterruptedException e) {
            logger.error("Error while downloading video! -> " + e.getMessage());
            return null;
        }
        logger.info("Finished downloading video!");
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
