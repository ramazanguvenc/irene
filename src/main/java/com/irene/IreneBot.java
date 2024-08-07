package com.irene;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendVideo;

import config.Config;

import com.dao.KeyValueDao;
import com.dao.SubscriptionDao;
import com.dao.VideoLogDao;
import com.entity.KeyValue;
import com.entity.Subscription;
import com.entity.VideoLog;
import com.irene.scrapers.BinanceScraper;
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
    public KeyValueDao keyValueDao;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);


    public SubscriptionDao getSubscriptionDao() {
        return subscriptionDao;
    }



    private IreneBot() {
        super(Config.getInstance().get("token"));
        subscriptionDao = new SubscriptionDao();
        videoLogDao = new VideoLogDao();
        keyValueDao = new KeyValueDao(); 
        KeyValue btcusdtmax = keyValueDao.get("btcusdtmax");
        KeyValue usdtrymax = keyValueDao.get("maxusdtry");
        logger.info("max --> " + btcusdtmax + ", max2 ---> " + usdtrymax);
        if (btcusdtmax != null && !btcusdtmax.equals(""))
            UpOnlyTask.lastMaxBTCUSDTValue = new BigDecimal(btcusdtmax.getValue());
        if (usdtrymax != null && !usdtrymax.equals("")){
            VibeCheckTask.lastMaxUSDTTRYValue = new BigDecimal(usdtrymax.getValue());
        } 
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
                            case "/remindme":
                                if(args.length <= 1){
                                   _sendMessage(chatId, "example-> /remindme 5 seconds/days/week [your message]");
                                    break; 
                                }
                                else{
                                    scheduler.schedule(() -> _sendMessage(chatId, "You're being reminded for = " + args[args.length - 1]), Integer.valueOf(args[1].replaceAll("[\\D]", "")), resolveTimeUnit(update.message().text()));
                                    _sendMessage(chatId, "Your reminder is set!");
                                }
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
                            //TODO: Make dynamic later on
                            case "/crypto":
                                handleCryptoCommand(chatId);
                                break;
                            case "/chat":
                                String output = getGeminiOutput(update.message().text());
                                _sendMessage(chatId, output);
                                break;
                            case "/add":
                                handleAdd(args, chatId);
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
                                        _sendMessage(chatId, "Video couldn't downloaded!");
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
                            case "/testSet":
                                keyValueDao.set(new KeyValue("test", "123"));
                                this.execute(new SendMessage(update.message().chat().id(), "set! this -> " + keyValueDao.get("test")));
                                break;
                            case "/help":
                                this.execute(new SendMessage(update.message().chat().id(), "Help"));
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

    private void handleAdd(String [] args, Long chatId) {
        if(args.length <= 1){
            _sendMessage(chatId, "example-> /add eth");
        }
        else{
            //check if coin really exist
            String isExistSTR = BinanceScraper.getCoinPrice(args[1].toUpperCase() + "USDT");
            if(isExistSTR == null){
                _sendMessage(chatId, "This coin does not exist!");
                return;
            }
            //else
            KeyValue keyValue = keyValueDao.get(chatId.toString() + "crypto");
            if(keyValue == null)
                keyValueDao.set(new KeyValue(chatId + "crypto", args[1]));
            else{
                keyValue.setValue(keyValue.getValue() + "," + args[1]);
                keyValueDao.set(keyValue);
            }
                _sendMessage(chatId, "Coin: " + args[1] + " added!");
        }
    }

    private TimeUnit resolveTimeUnit(String durationString) {
            if (durationString.toLowerCase().contains("second")) {
                return TimeUnit.SECONDS;
            } else if (durationString.toLowerCase().contains("minute")) {
                return TimeUnit.MINUTES;
            } else if (durationString.toLowerCase().contains("hour")) {
                return TimeUnit.HOURS;
            } else if (durationString.toLowerCase().contains("day")) {
                return TimeUnit.DAYS;
            } else{
                return TimeUnit.SECONDS;
            }
        }

    private void handleCryptoCommand(Long chatId) {
        KeyValue keyValue = keyValueDao.get(chatId.toString() + "crypto");
        if(keyValue == null){
            _sendMessage(chatId, "Please add coins first!");
            return;
        }
        String [] coins = keyValue.getValue().split(",");
        StringBuilder msg = new StringBuilder();
        for(String coin : coins){
            msg.append("Price of " + coin.toUpperCase() + " = $" + BinanceScraper.getCoinPrice(coin.toUpperCase() + "USDT") + "\n");
        }
        _sendMessage(chatId, msg.toString());
    }



    private void _sendMessage(Long chatId, String msg) {
        this.execute(new SendMessage(chatId, msg));
    }



    private File getVideoPath(String link) {
        String path = null;
        
        path = downloadVideo(link);
       
        if(path == null){
            return null;
        }
        return new File(path);
    }

    private String getGeminiOutput(String input){
        String pythonScriptPath = Config.getInstance().get("gemini_path");;
        String argument = input;
        String[] command = {"python", pythonScriptPath, argument};

        
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            String pythonOutput = output.toString();
            logger.info("Python output: " + pythonOutput);
            return pythonOutput;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage());
        }
        return "error!";

        
    }

    public static String downloadVideo(String link){
        String path = Config.getInstance().get("twitter_download_go_path");
        String outputPath = Config.getInstance().get("twitter_download_output_path") + generateRandomString(6) + ".mp4";
        logger.info("destination: " + outputPath);
        
        try {
            ProcessBuilder processBuilder = null;
            if(link.contains("youtube"))
                //bv*[ext=mp4]+ba[ext=m4a]/b[ext=mp4] / bv*+ba/b -> for best video quality. Telegram doesn't support >50M
                processBuilder = new ProcessBuilder("yt-dlp","-f", "b", "-S", "filesize~50M", link, "--no-part", "-o", outputPath);
            else  
                processBuilder = new ProcessBuilder("go", "run", "main.go", "-url", link, "-destination", outputPath);      
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
                    return null;
                }
                return null;
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
