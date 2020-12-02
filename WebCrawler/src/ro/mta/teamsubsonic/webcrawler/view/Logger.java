package ro.mta.teamsubsonic.webcrawler.view;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;

class Logger
{

    private static Logger instance = null;
    public String         outputFile;

    private Logger() { this.outputFile = null; }
    private Logger(String _outputFile) { this.outputFile = _outputFile; }

    public static Logger getInstance(String _output)
    {
        if (instance == null)
            if(_output == null)
                instance = new Logger();
            else
                instance = new Logger(_output);
        return instance;
    }
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
