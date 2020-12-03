package ro.mta.teamsubsonic.webcrawler.model;

import ro.mta.teamsubsonic.webcrawler.model.exceptions.CrawlerException;
import ro.mta.teamsubsonic.webcrawler.model.exceptions.FileException;
import ro.mta.teamsubsonic.webcrawler.model.exceptions.InputException;
import ro.mta.teamsubsonic.webcrawler.model.exceptions.InternalException;

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
     * Members of class description
     */

    private static Configurations instance = null;

    private double delay;

    private int threadsNumber;
    private int depthLevel;
    private int logLevel;

    private String logFile;
    private String targetDirectory;
    private String configFileLocation;

    private Configurations() {
    }

    /**
     * Function that returns the instance to Configurations (must not call this one to initialize)
     *
     * @return return reference to object
     * @throws Exception
     */
    public static Configurations getInstance() throws Exception {
        if (instance == null) {
            throw new InternalException("Configurations not initialized! Call the other getInstance first!");
        }
        return instance;
    }

    /**
     * Function that initializez the configuration object. Must be called first (in case of reconfigurations call this function again)
     *
     * @param configFileLocation it can be null for a default configuration
     * @return return the reference to object
     * @throws Exception
     */

    public static Configurations getInstance(String configFileLocation) throws CrawlerException {
        try {
            if (instance == null)
                instance = new Configurations();
            reconfigure(configFileLocation);
            return instance;
        } catch (InputException ex) {
            throw new InputException("Bad config file");
        } catch (Exception ex) {
            throw new FileException("Bad file name");
        }
    }

    /**
     * Function that configures the instance and in case there isn't a config file, it gives a default configuration
     *
     * @param configFileLocation it can be null for a default configuration
     * @throws Exception
     */
    private static void reconfigure(String configFileLocation) throws Exception {

        instance.threadsNumber = 4;
        instance.delay = 20;
        instance.depthLevel = 4;
        instance.logFile = "log.txt";
        instance.logLevel = 2;
        instance.targetDirectory = "out";
        instance.configFileLocation = null;
        if (configFileLocation == null) {
            return;
        }

        File file = new File(configFileLocation);
        Scanner scanner = new Scanner(file);
        String fileData = null;
        HashMap<String, String> hashMap = new HashMap<>();


        while (scanner.hasNextLine()) {
            fileData = scanner.nextLine();
            String[] fields = fileData.split("=");
            if (fields.length != 2)
                throw new InputException("");
            hashMap.put(fields[0], fields[1]);
        }

        if (hashMap.containsKey("threadsNumber"))
            instance.threadsNumber = Integer.parseInt(hashMap.get("threadsNumber"));
        if (hashMap.containsKey("delay"))
            instance.delay = Double.parseDouble(hashMap.get("delay"));
        if (hashMap.containsKey("depthLevel"))
            instance.depthLevel = Integer.parseInt(hashMap.get("depthLevel"));
        if (hashMap.containsKey("logFile"))
            instance.logFile = hashMap.get("logFile");
        if (hashMap.containsKey("targetDirectory"))
            instance.targetDirectory = hashMap.get("targetDirectory");
        if (hashMap.containsKey("logLevel"))
            instance.logLevel = Integer.parseInt(hashMap.get("logLevel"));
        instance.configFileLocation = configFileLocation;

    }

    public int getThreadsNumber() {
        return threadsNumber;
    }

    public void setThreadsNumber(int threadsNumber) {
        this.threadsNumber = threadsNumber;
    }

    public double getDelay() {
        return delay;
    }

    public void setDelay(double delay) {
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
