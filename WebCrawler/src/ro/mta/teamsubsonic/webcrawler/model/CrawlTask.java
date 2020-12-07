package ro.mta.teamsubsonic.webcrawler.model;

import ro.mta.teamsubsonic.webcrawler.model.exceptions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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

    /**
     * Boolean method that tests->if the maximum level of recursion has been reached
     *                          ->if path and url is null
     * @return true/false
     */
    private boolean testTask() {
        try {
            Configurations confInstance = Configurations.getInstance();

            int depthLv=confInstance.getDepthLevel();
            if((url==null)||(targetPath==null))
            {
                throw new InputException("Null HashMap given to method startTasks");
            }
            if(taskId+1<=depthLv)
            {
                return true;
            }
            else{
                return false;
            }

        } catch (_CrawlerException error){
            error.getMessage();
        }
        return false;
    }

    private void downloadPage(){

    }

    /**
     *The method that starts the tasks of downloading the initial url references to the max depth set
     * @param nextRef
     */
    private void startTasks(HashMap<String,String> nextRef){
        try {
            if(testTask()) {
                CrawlerThreadPool threadPoolInstance = CrawlerThreadPool.getInstance();

                if(nextRef==null)
                {
                    throw new InputException("Null HashMap given to method startTasks");
                }
                for (HashMap.Entry<String, String> element : nextRef.entrySet()) {
                    Factory factoryTask = new Factory();

                    List<String> argsList = new ArrayList<>();
                    argsList.add(element.getKey());
                    argsList.add(element.getValue());
                    String idStr = String.valueOf(taskId + 1);
                    argsList.add((idStr));

                    Task downloadTask = factoryTask.createTask(CrawlTask.class, argsList);//tipul+lista de argumente

                    threadPoolInstance.putTask(downloadTask);
                }
            }

        }catch (InternalException error){
            error.getMessage();
        }catch (InputException error){
            error.getMessage();
        }
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
        } catch (Exception error){
            error.getMessage();
        }
    }

    /**
     *  This method ->overrides the _run() method from Task abstract class.
     *  It is responsible for starting the application functionality.
     */
    @Override
    protected void _run() {
        startTasks(parsePageHtml());
        downloadPage();
        replaceHTMLRef();

    }
}