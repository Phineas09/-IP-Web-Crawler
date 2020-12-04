package ro.mta.teamsubsonic.webcrawler.model;


import java.util.HashMap;
import java.util.List;

/**
 * Class that extends abstract class Task
 * This class is responsible for:->recursive downloads web pages from the received url
 *                               ->testing url references
 *                               ->starting tasks that will download the found references
 *                               ->replacing the references with the local path.
 * @author Gunyx
 * @author Phineas09
 */


public class CrawTask extends Task {
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
    public CrawTask(int taskId, String url, String targetPath) {
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
    private void replaceHTMLRef(){

    }

    /**
     *  This method ->overrides the _run() method from Task abstract class.
     *  It is responsible for starting the application functionality.
     */
    @Override
    protected void _run() {


    }
}
