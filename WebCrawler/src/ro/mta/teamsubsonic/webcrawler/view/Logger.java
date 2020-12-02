package ro.mta.teamsubsonic.webcrawler.view;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
/**
 * Class implementing the Logger part.
 * Implements constructor and write function for the program output
 *
 * @author Panțucu Flavius
 */
public class Logger
{
    /**
     * Member decription
     */
    private static Logger instance = null;  //singleton object
    public String         outputFile;       //where the output will be written
    /**
     * Logger class constructor
     * @param _outputFile The file we work on
     */
    private Logger() { this.outputFile = null; }
    private Logger(String _outputFile) { this.outputFile = _outputFile; }

    public static Logger getInstance(String _output)
    {
        //verify if the singleton object is already created
        if (instance == null)
            //verify where we have to write program's output
            if(_output == null)
                instance = new Logger();
            else
                instance = new Logger(_output);
        return instance;
    }

    /**
     * Write output on the the file chosen at the beginning, stdout if null.
     * @param outputText String that we have to display
     */
    public void write(String outputText){
        if(this.outputFile == null){
            System.out.println(outputText);
        }
        else{
            Writer writer = null;
            try {
                writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(this.outputFile), "utf-8"));
                writer.write(outputText);
            } catch (IOException ex) {
            } finally {
                try {writer.close();} catch (Exception ex) {}
            }
        }
    }
}
