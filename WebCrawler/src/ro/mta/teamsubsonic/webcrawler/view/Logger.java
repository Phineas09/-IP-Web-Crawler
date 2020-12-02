package ro.mta.teamsubsonic.webcrawler.view;
import ro.mta.teamsubsonic.webcrawler.model.exceptions._CrawlerException;

import java.io.*;

/**
 * Class implementing the Logger part.
 * Implements constructor and write function for the program output
 *
 * @author Panțucu Flavius
 */
public class Logger
{
    /**
     * Member description
     */
    private static Logger instance = null;  // singleton object
    public FileWriter     outputFile;       // the writer who will do the job done writing output
    /**
     * Logger class constructor
     */
    private Logger() { this.outputFile = null; }

    public static Logger getInstance()
    {
        // verify if the singleton object is already created
        if (instance == null)
                instance = new Logger();
        return instance;
    }
    /**
     * Write output on the the file chosen at the beginning, stdout if null.
     * @param message string that we have to display
     * @param filePath path to the file we need to write into
     */
    public void write(String message, String filePath){
        try {
            if (filePath == null) {
                throw new _CrawlerException("FileException","Invalid filepath entered");
            } else {
                File out = new File(filePath);

                // creates the file
                out.createNewFile();

                // creates a FileWriter Object and write output
                outputFile = new FileWriter(out);
                outputFile.write(message);
                outputFile.close();
            }
        }
        catch(_CrawlerException | IOException err) {
            err.getMessage();
        }
    }
    public void write(String message, int stdin){
        try {
           if(stdin == 0){
               System.out.println(message);
           }
           else{
               throw new _CrawlerException("InputException","Invalid value for stdin entered");
           }
        }
        catch(_CrawlerException err) {
            err.getMessage();
        }
    }
}
