package ro.mta.teamsubsonic.webcrawler.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Class that contains the configuration for Crawl
 * Pattern used: Singleton
 *
 * @author VladTeapa
 */

public class Configurations {

    /**
     * Members of class description
     */

    private static Configurations instance = null;
    private int threadsNumber;
    private double delay;
    private int logLevel;
    private String logFile;
    private String targetDirectory;
    private int depthLevel;
    private String configFileLocation;

    private Configurations() {
    }

    /**
     * Function that returns the instance to Configurations (must not call this one to initialize)
     * @return return reference to object
     * @exception Exception
     */
    public static Configurations getInstance() throws Exception{
        if (instance == null) {
            //throw
        }
        return instance;
    }

    /**
     * Function that initializez the configuration object. Must be called first (in case of reconfigurations call this function again)
     * @param configFileLocation configuration file
     * @return return the reference to object
     * @throws Exception
     */

    public static Configurations getInstance(String configFileLocation) throws Exception {
        try {
            if (instance == null)
                instance = new Configurations();
            reconfigure(configFileLocation);
        } catch (FileNotFoundException ex) {
            //throw Exceptie
        } catch (JSONException ex){
            //throw Exceptie
        }
    }

    /**
     * Functie care preia din fisier datele si le atribuie membrilor care trebuie
     * @param configFileLocation
     * @throws Exception
     */
    private static void reconfigure(String configFileLocation) throws Exception {

        JSONObject jsonObject;
        File file = new File(configFileLocation);
        Scanner scanner = new Scanner(file);
        StringBuilder fileData = new StringBuilder();

        while (scanner.hasNextLine()) {
            fileData.append(scanner.nextLine());
        }

        jsonObject = new JSONObject(fileData);

        instance.threadsNumber = jsonObject.getInt("threadsNumber");
        instance.delay = jsonObject.getDouble("delay");
        instance.depthLevel = jsonObject.getInt("depthLevel");
        instance.logFile = jsonObject.getString("logFile");
        instance.targetDirectory = jsonObject.getString("targetDirectory");
        instance.logLevel = jsonObject.getInt("logLevel");
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
