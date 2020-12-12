package ro.mta.teamsubsonic.webcrawler.model;

import ro.mta.teamsubsonic.webcrawler.model.exceptions.CrawlerException;
import ro.mta.teamsubsonic.webcrawler.model.exceptions.FileException;
import ro.mta.teamsubsonic.webcrawler.model.exceptions.InputException;
import ro.mta.teamsubsonic.webcrawler.view.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Class that contains the configuration for Crawl
 * Pattern used: Singleton
 * In case you don't have a configuration file you can always use a default one
 *
 * @author VladTeapa
 */

public class Configurations {

    /**
     * @param instance, is the singleton object
     * @param delay, is the delay in ms between requests
     * @param threadsNumber, number of threads used
     * @param depthLevel, number of recursion levels in requests
     * @param logLevel, how much to log
     * @param logFile, the file to log in
     * @param targetDirectory, the directory where to download the files
     * @param configFileLocation, location of the configuration file
     * @param robots, true if using robots.txt or false if not using
     **/

    private static Configurations instance = null;

    private long delay;

    private int threadsNumber;
    private int depthLevel;
    private int logLevel;

    private String logFile;
    private String targetDirectory;
    private String configFileLocation;

    private boolean robots;

    private static Logger logger;

    private Configurations() {
        logger = Logger.getInstance();
    }

    /**
     * Function that returns the instance to Configurations with default configurations
     *
     * @return return reference to object
     * @throws CrawlerException
     */
    public static Configurations getInstance() {
        if (instance == null) {
            instance = new Configurations();
            reconfigure(null);
        }
        return instance;
    }

    /**
     * Function that initializez the configuration object with custom configuration
     *
     * @param configFileLocation it can be null for a default configuration
     * @return return the reference to object
     * @throws CrawlerException
     */

    public static Configurations getInstance(String configFileLocation) {
        if (instance == null)
            instance = new Configurations();
        reconfigure(configFileLocation);
        return instance;
    }

    /**
     * Function that configures the instance and in case there isn't a config file, it gives a default configuration
     *
     * @param configFileLocation it can be null for a default configuration
     * @throws Exception which will be translated into another type of Exception
     */
    private static void reconfigure(String configFileLocation) {
        try {
            instance.threadsNumber = 4;
            instance.delay = 20;
            instance.depthLevel = 4;
            instance.logFile = "log.txt";
            instance.logLevel = 3;
            instance.targetDirectory = "out";
            instance.configFileLocation = null;
            instance.robots = true;
            if (configFileLocation == null) {
                configFileLocation = "config.cfg";
            }

            File file = new File(configFileLocation);
            Scanner scanner = new Scanner(file);
            String fileData = null;
            HashMap<String, String> hashMap = new HashMap<>();


            while (scanner.hasNextLine()) {
                fileData = scanner.nextLine();
                String[] fields = fileData.split("=");
                if (fields.length != 2)
                    throw new InputException("File is not properly formatted. Using default configuration!");
                hashMap.put(fields[0], fields[1]);
            }

            if (hashMap.containsKey("threadsNumber"))
                instance.threadsNumber = Integer.parseInt(hashMap.get("threadsNumber"));
            if (hashMap.containsKey("delay"))
                instance.delay = Long.parseLong(hashMap.get("delay"));
            if (hashMap.containsKey("depthLevel"))
                instance.depthLevel = Integer.parseInt(hashMap.get("depthLevel"));
            if (hashMap.containsKey("logFile"))
                instance.logFile = hashMap.get("logFile");
            if (hashMap.containsKey("targetDirectory"))
                instance.targetDirectory = hashMap.get("targetDirectory");
            if (hashMap.containsKey("logLevel"))
                instance.logLevel = Integer.parseInt(hashMap.get("logLevel"));
            if (hashMap.containsKey("robots"))
                instance.robots = Boolean.parseBoolean(hashMap.get("robots"));
            instance.configFileLocation = configFileLocation;
            logger.setOutputFile(instance.logFile);
        } catch (InputException ex) {
            ex.getMessage();
        } catch (NumberFormatException ex) {
            InputException exception = new InputException("Bad config file! Using rest of params with default value!");
            exception.getMessage();
        } catch (Exception ex) {
            FileException exception = new FileException("File not found. Using default configuration!");
            exception.getMessage();
        } finally {
            logger.write("Configurations done!", 2, 3);
        }
    }

    public int getThreadsNumber() {
        return threadsNumber;
    }

    public void setThreadsNumber(int threadsNumber) {
        this.threadsNumber = threadsNumber;
    }

    public long getDelay() {
        return delay;
    }

    public boolean trackRobots() {
        return robots;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public int getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    public String getLogFile() {
        return logFile;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }

    public String getTargetDirectory() {
        return targetDirectory;
    }

    public void setTargetDirectory(String targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    public int getDepthLevel() {
        return depthLevel;
    }

    public void setDepthLevel(int depthLevel) {
        this.depthLevel = depthLevel;
    }

    public String getConfigFileLocation() {
        return configFileLocation;
    }
}
