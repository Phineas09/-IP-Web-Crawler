package ro.mta.teamsubsonic.webcrawler.model;

import ro.mta.teamsubsonic.webcrawler.model.exceptions.FileException;
import ro.mta.teamsubsonic.webcrawler.model.exceptions.InputException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Class that extends abstract class Task
 * This class is responsible for:->recursive downloads web pages from the received url
 *                               ->testing url references
 *                               ->starting tasks that will download the found references
 *                               ->replacing the references with the local path.
 * @author Gunyx
 * @author Phineas09
 */

public class CrawlTask extends Task {
    /**
     * Members of class description
     */
    private final int taskId;
    private final String url;
    private final String targetPath;


    /**
     * Constructor of class
     * @param taskId ->  which identifies the current task id.It is also used to identify the depth of recursion
     * @param url ->contains the url from which the information is downloaded
     * @param targetPath ->contains the path where the information is downloaded.
     */
    public CrawlTask(int taskId, String url, String targetPath) {
        this.taskId = taskId;
        this.url = url;
        this.targetPath = targetPath;
    }

    private HashMap<String,String> parsePageHtml() {

        return new HashMap<>();//referintele paginilor+locatia lor de download;
    }
    private boolean testTask(){

        return false;
    }
    private void downloadPage(){

    }
    private void startTasks(){

    }

    /**
     * Method that replaces all references with the names of locally downloaded files
     *
     */
    private void replaceHTMLRef() {
        try {
            String inPath = targetPath + "\\" + url;//aici cum numim fisierele-> NU putem pune url ca nume
            String outPath = targetPath + "\\1" + url;

            File oldFile = new File(inPath);
            PrintWriter newFile = new PrintWriter(outPath);

            Scanner reader = new Scanner(oldFile);

            int nrRef = 0;
            while (reader.hasNextLine()) {
                int status = 0;
                String data = reader.nextLine();
                String patternString = "\\b((https?|ftp):\\/\\/)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[A-Za-z]{2,6}\\b(\\/[-a-zA-Z0-9@:%_\\+.~#?&//=]*)*(?:\\/|\\b)";
                Pattern pattern = Pattern.compile(patternString);
                Matcher matcher = pattern.matcher(data);

                String newStateLine = null;
                String lastStateLine = data;
                while (matcher.find()) {
                    status = 1;
                    nrRef++;

                    String stringValue = String.valueOf(nrRef);
                    int lenString = lastStateLine.length();

                    newStateLine = lastStateLine.substring(0, matcher.start()) + stringValue + lastStateLine.substring(matcher.end(), lenString);
                    matcher = pattern.matcher(newStateLine);
                    lastStateLine = newStateLine;
                }
                if (status != 0) {
                    newFile.println(newStateLine);
                } else {
                    newFile.println(data);
                }
            }
            newFile.close();
            reader.close();
            oldFile.delete();

            /**
             * The new file is renamed after the input file
             */
            Path source = Paths.get(outPath);
            Files.move(source, source.resolveSibling(inPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  This method ->overrides the _run() method from Task abstract class.
     *  It is responsible for starting the application functionality.
     */
    @Override
    protected void _run() {


    }
}
