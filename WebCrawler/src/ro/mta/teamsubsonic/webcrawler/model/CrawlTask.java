package ro.mta.teamsubsonic.webcrawler.model;

import ro.mta.teamsubsonic.webcrawler.model.exceptions.*;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Class that extends abstract class Task
 * This class is responsible for: * recursive downloads web pages from the received url
 *                                * testing url references
 *                                * starting tasks that will download the found references
 *                                * replacing the references with the local path.
 * @author Gunyx
 * @author Phineas09
 */
public class CrawlTask extends Task {

    /** It is the taskId of the currently executing task in the threadPool and also represents the depth of the crawl */
    private final int taskId;
    /** It is the url that will be downloaded by this task */
    private final String url;
    /** It is the target path in the file system where the current url will be stored */
    private String targetPath;
    /** Is root path in the file system where, we will download the page */
    private final String targetRoot;

    /**
     * Constructor of class
     * @param taskId -> which identifies the current task id.It is also used to identify the depth of recursion
     * @param url -> contains the url from which the information is downloaded
     * @param targetRoot -> contains the path where the information is downloaded.
     */
    public CrawlTask(int taskId, String url, String targetRoot) {
        this.taskId = taskId;
        this.url = url;
        this.targetRoot = targetRoot;
        this.targetPath = null;
    }

    /**
     * Constructor of class
     * @param taskId -> which identifies the current task id.It is also used to identify the depth of recursion
     * @param url -> contains the url from which the information is downloaded
     * @param targetRoot -> contains the path root where the information will be downloaded ( inherited from the previous task )
     * @param targetPath -> contains the path where the information is downloaded.
     */
    public CrawlTask(int taskId, String url, String targetPath, String targetRoot) {
        this.taskId = taskId;
        this.url = url;
        this.targetPath = targetPath;
        this.targetRoot = targetRoot;
    }

    /**
     * Returns a resolved string with the destination of the next reference.
     * @param refType type of the reference ( anchor, image, script or link).
     * @param url the url that needs to be resolved.
     * @param baseUrl the base url for given website
     * @return a string containing the path in the system for current url.
     * @throws CrawlerException exception if the resolve will fail.
     */
    private String resolveHtml(String refType, String url, String baseUrl) throws CrawlerException {

        String resolvedString = this.targetRoot;

        String customPath = url.substring(baseUrl.length() + 1, url.length());
        customPath = customPath.replaceAll("/", "_");

        switch (refType) {
            case "script" -> resolvedString = resolvedString + "/scripts/";
            case "img" -> resolvedString = resolvedString + "/images/";
            case "a" -> resolvedString = resolvedString + "/pages/";
            case "link" -> resolvedString = resolvedString + "/css/";
            default -> throw new InternalException("This should never throw @CrawlTask.resolveHTML!");
        }
        return resolvedString + customPath;
    }

    /**
     * Parses a given web page for refs ( a | img | script | link ).
     *
     * @param webPage a web page in String format.
     * @return A HashMap of links and array of original link to be replaced and the target path to be downloaded in.
     * @throws CrawlerException exception if parsing will fail.
     */
    private HashMap<String, String[]> parsePageHtml(String webPage) throws CrawlerException {

        try {
            //This function is too big @! maybe improve here

            String baseUrl = "https://" + (new URI(this.url)).getHost();
            //Or make another variable to hold this information

            //Used for detecting all tags that may contain references
            Pattern referencesPattern = Pattern.compile("(<(script|img|a|link)\\s+(?:[^>]*?\\s+)?" +
                    "(src|href)(\\s?|\\s+)=(\\s?|\\s+)([\"'])(.*?)\\6)",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

            //Used for detecting paths from internal url
                Pattern internalPattern = Pattern.compile("^\\.?(?:\\.{2})?(?:\\/\\.{2})*(\\/?[\\w\\d-\\.\\*]+)+",
                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

            HashMap<String, String[]> returnHash = new HashMap<>();
            Matcher pageMatcher = referencesPattern.matcher(webPage);
            int indexOf = 0;

            while (pageMatcher.find()) {

                String newUrl = pageMatcher.group(7);

                if(newUrl.equals(baseUrl + "/") || newUrl.equals(baseUrl))
                    continue;

                if (newUrl.regionMatches(true, 0, baseUrl, 0, baseUrl.length())) {

                    //If the reference is a website part of same domain
                    //We can have arguments inside this uris, remove them
                    if((indexOf = newUrl.indexOf("?")) != -1) {
                        newUrl = newUrl.substring(0, indexOf);
                    }

                    //We will not try to download the .php files
                    if(!newUrl.endsWith(".php")) {
                        returnHash.putIfAbsent(newUrl, new String[]{newUrl,
                                resolveHtml(pageMatcher.group(2), newUrl, baseUrl)});
                    }
                }
                else {
                    Matcher pathMatcher = internalPattern.matcher(newUrl);
                    if(pathMatcher.find() && newUrl.equals(pathMatcher.group())) {
                        //If it is a reference inside the root dir
                        String tempUrl = baseUrl + newUrl;
                        //We will not try to download the .php files
                        if(!tempUrl.endsWith(".php")) {
                            returnHash.putIfAbsent(tempUrl, new String[]{newUrl,
                                    resolveHtml(pageMatcher.group(2), tempUrl, baseUrl)});
                        }
                    }
                }
            }
            return returnHash;
        }
        catch (Exception exception) {
            throw new InternalException("Web page does not exist!");
        }
    }

    /**
     * Downloads the web page of the current Task
     *
     * @return a string witch contains the entire web page
     * @throws CrawlerException if an error occurs during the download process
     */
    private String downloadPage() throws CrawlerException {
        try {
            URL url = new URL(this.url);
            BufferedReader siteReader =
                    new BufferedReader(new InputStreamReader(url.openStream()));
            // Read each line into a stringBuffer for further processing

            File filePath = new File(targetPath.substring(0 , targetPath.lastIndexOf("/")));

            if (!filePath.exists())
                filePath.mkdirs();

            BufferedWriter writer = new BufferedWriter(new FileWriter(targetPath));
            String line;
            StringBuilder siteBuffer = new StringBuilder();
            while ((line = siteReader.readLine()) != null) {
                siteBuffer.append(line + "\n");
                writer.write(line + "\n");
            }

            siteReader.close();
            writer.close();
            return siteBuffer.toString();
        }
        catch (Exception exception) {
            throw new InternalException(exception.getMessage());
        }
    }

    /**
     * Boolean method that tests->if the maximum level of recursion has been reached
     *                          ->if path and url is null
     * @return true/false
     */
    private boolean testTask() {
        try {

            /** If the next task will surpass depth we will not run the next task
             * And remove the references to be changed by replaceHtmlRefs
             * We will also test if the page has already been downloaded via resolveHtml function
             * and some tests on the file system with the help of this.targetPath and this.targetRoot
             */

            Configurations confInstance = Configurations.getInstance();

            //TODO: See if it has already been downloaded

            //TODO: Check for depth level

            //TODO: Remove from HashMap if necessary, HashMap will be given as param

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
                    String idStr = String.valueOf(taskId + 1);
                    argsList.add((idStr));
                    argsList.add(element.getKey());
                    argsList.add(element.getValue());

                    Task downloadTask = factoryTask.createTask(CrawlTask.class, argsList);//tipul+lista de argumente

                    threadPoolInstance.putTask(downloadTask);
                }
            }

        }catch (InternalException error){
            error.getMessage();
        }catch (InputException error){
            error.getMessage();
        } catch (CrawlerException crawlerException) {
            crawlerException.getMessage();
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
        try {
            //Only for the first task
            if(this.taskId == 0) {
                this.targetPath = this.targetRoot + "/index.html";
            }
            //startTasks(parsePageHtml());
            HashMap<String, String[]> hashMap =  parsePageHtml(downloadPage());

            for(String key : hashMap.keySet()) {
                System.out.println(key + " -> " + hashMap.get(key)[0] + " - " + hashMap.get(key)[1]);
            }

            //replaceHTMLRef();
            /**
             *
             * Download page -> return string + scrie in fisier pagina index.html practic
             * ParsePageHTML -> HasMap(<URL target><Target path>)
             * StartTasks -> Start all tasks to be downloaded  + verificare de depth si existenta ?
             * Replace : <URL target> cu <Target path> with modified HashMap<String, String>
             */
        }
        catch (CrawlerException crawlerException) {
            crawlerException.getMessage();
        }
    }
}
