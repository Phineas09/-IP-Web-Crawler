package ro.mta.teamsubsonic.webcrawler.view;

import ro.mta.teamsubsonic.webcrawler.model.Configurations;
import ro.mta.teamsubsonic.webcrawler.model.exceptions.CrawlerException;
import ro.mta.teamsubsonic.webcrawler.model.exceptions.InputException;
import ro.mta.teamsubsonic.webcrawler.model.exceptions._CrawlerException;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class implementing the Logger part.
 * Implements constructor and write function for the program output
 *
 * @author Panțucu Flavius
 * @author Ghenea Claudiu
 */
public class Logger {

    /** Singleton instance of the class */
    private static Logger instance = null;  // singleton object
    /** The outFile to log if this is set */
    public FileWriter     outputFile;       // the writer who will do the job done writing output

    /**
     * Logger class constructor
     */
    private Logger() { this.outputFile = null; }

    public static Logger getInstance() {
        // verify if the singleton object is already created
        if (instance == null)
                instance = new Logger();
        return instance;
    }

    /**
     * Sets the new outFile for the logger to print to.
     * @param filePath new file to set the output to.
     * @throws CrawlerException if the file path is invalid.
     */
    public void setOutputFile(String filePath) throws CrawlerException {
        try {
            if (filePath == null) {
                throw new InputException("Invalid filepath entered!");
            } else {
                //Close the old file if existent
                if(this.outputFile != null)
                    this.outputFile.close();
                File out = new File(filePath);
                out.createNewFile();
                outputFile = new FileWriter(out);
            }
        }
        catch(_CrawlerException | IOException err) {
            throw new InputException("Invalid filepath entered!");
        }
    }

    /**
     * Write output on the the specified file
     * @param message string that we have to display
     * @param filePath path to the file we need to write into
     */
    public void write(String message, String filePath, int logLevel){
        try {
            int globalLogLeve = Configurations.getInstance().getLogLevel();

            if (filePath == null) {
                throw new _CrawlerException("FileException","Invalid filepath entered");
            }
            else {

                if(logLevel > globalLogLeve) {
                    return;
                }
                switch (logLevel) {
                    case 1 -> message = "Error: " + message;
                    case 2 -> message = "Warning: " + message;
                    case 3 -> message = "Info: " + message;
                }
                //Format message
                String timeStamp = new SimpleDateFormat("[ dd.MM.yyyy - HH.mm.ss ] ").format(new Date());
                message = timeStamp + message;

                File out = new File(filePath);
                // creates the file
                out.createNewFile();
                // creates a FileWriter Object and write output
                FileWriter localOutFile = new FileWriter(out);
                localOutFile.write(message + '\n');
                localOutFile.close();
            }
        }
        catch(_CrawlerException | IOException err) {
            err.getMessage();
        }
    }

    /**
     * This function will log at given standard a certain log message
     * If printing in file, the file must be initialized first !
     * @param message Message to be logged
     * @param stdin Where to log the message 0 -> stdin, 1 -> file, 2 -> prints to both
     * @param logLevel 1 -> Errors, 2 -> Warns, 3 -> Anything else.
     */
    public void write(String message, int stdin, int logLevel) {
        try {
            int globalLogLeve = Configurations.getInstance().getLogLevel();

            if(logLevel > globalLogLeve) {
                return;
            }
            switch (logLevel) {
                case 1 -> message = "Error: " + message;
                case 2 -> message = "Warning: " + message;
                case 3 -> message = "Info: " + message;
            }
            this.write(message, stdin);
        }
        catch (CrawlerException crawlerException) {
            crawlerException.getMessage();
        }
    }

    /**
     * Logs a message to stdin must preceded by "Error | Warning | Info" this function will not
     * add this automatically, for this check the other write.
     *
     * @param message Message to be logged.
     * @param stdin Where to log, 0 for console ( stdout ), 1 for file that was set with
     *             setOutputFile and 2 for logging to both.
     */
    public void write(String message, int stdin){
        try {
            String timeStamp = new SimpleDateFormat("[ dd.MM.yyyy - HH.mm.ss ] ").format(new Date());
            message = timeStamp + message;
            switch (stdin) {
                case 0 -> System.out.println(message);
                case 1 -> outputFile.write(message + "\n");
                case 2 -> {
                    System.out.println(message);
                    outputFile.write(message + "\n");
                }
                default -> throw new InputException("Unknown stdin provided!");
            }
        }
        catch(Exception err) {
            err.getMessage();
        }
    }
}
