package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Config {
    private static final Logger logger = LogManager.getLogger(Config.class);
    
    private static Config config = null;
    private Properties prop;

    private Config(){
        init();
    }

    private void init() {
        try(InputStream input = new FileInputStream("./config.properties")){
            prop = new Properties();
            prop.load(input);
        }
        catch(Exception e){
            logger.info("Error: " + e);
            String currentDirectory = System.getProperty("user.dir");

            // Create a File object for the current directory
            File directory = new File(currentDirectory);
    
            // Check if it's a directory
            if (directory.isDirectory()) {
                // List all files and directories in the current directory
                File[] filesAndDirs = directory.listFiles();
    
                if (filesAndDirs != null) {
                    logger.info("Files and directories in the current directory:");
                    for (File fileOrDir : filesAndDirs) {
                        if (fileOrDir.isDirectory()) {
                            logger.info("[Directory] " + fileOrDir.getName());
                        } else {
                            logger.info("[File] " + fileOrDir.getName());
                        }
                    }
                } else {
                    logger.info("No files or directories found in the current directory.");
                }
            } else {
                logger.info("The current path is not a directory.");
            }
        }
    }

    public static Config getInstance(){
        if(config == null){
            config = new Config();
        }
        return config;
    }

    public String get(String key){
        return prop.getProperty(key);
    }
    
}
