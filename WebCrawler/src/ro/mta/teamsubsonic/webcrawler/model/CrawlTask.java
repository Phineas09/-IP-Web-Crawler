package ro.mta.teamsubsonic.webcrawler.model;

import ro.mta.teamsubsonic.webcrawler.model.exceptions.*;
import ro.mta.teamsubsonic.webcrawler.view.Logger;

import java.io.*;
import java.net.URI;
import java.net.URL;
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
 * @author Vlad Florea -> it ain't much but it's honest work.
 */


public class CrawlTask extends Task {
    private static final String userAgent=""; //Name of the webCrawler - default name is empty

    /**
     * Regexes used to identify entries in robots.txt
     */
    private static final String userAgentMatcher="^User-agent: .*$";
    private static final Integer userAgentIndex="User-agent: ".length();
    private static final String allowRuleRegex="^Allow: .*$";
    private static final String disallowRuleRegex="^Disallow: .*$";
    private static final String ruleRegex="^Disallow: .*$|^Allow: .*$";

    /**
     * Indexes used to address rules in robots.txt
     */
    private static final Integer allowRuleIndex="Allow: ".length();
    private static final Integer disallowRuleIndex="Disallow: ".length();

    /**
     * TypeDefs for rule types - allow or disallow
     */
    private static final Boolean ruleAllow = true;
    private static final Boolean ruleDisalow = false;


    /**
     * It is the taskId of the currently executing task in the threadPool and also represents the depth of the crawl
     */
    private final int taskId;
    /**
     * It is the url that will be downloaded by this task
     */
    private HashMap<String,Boolean> robotsRules;
    /**
     * HashMap that stores the Rules from robots.txt
     */
    private final String url;
    /**
     * It is the target path in the file system where the current url will be stored
     */
    private String targetPath;
    /**
     * Is root path in the file system where, we will download the page
     */
    private final String targetRoot;

    /**
     * Constructor of class
     *
     * @param taskId     -> which identifies the current task id.It is also used to identify the depth of recursion
     * @param url        -> contains the url from which the information is downloaded
     * @param targetRoot -> contains the path where the information is downloaded.
     */
    public CrawlTask(int taskId, String url, String targetRoot) {
        this.taskId = taskId;
        this.url = url;
        this.targetRoot = targetRoot;
        this.targetPath = null;
        this.robotsRules = new HashMap<>();
    }

    /**
     * Constructor of class
     *
     * @param taskId     -> which identifies the current task id.It is also used to identify the depth of recursion
     * @param url        -> contains the url from which the information is downloaded
     * @param targetRoot -> contains the path root where the information will be downloaded ( inherited from the previous task )
     * @param targetPath -> contains the path where the information is downloaded.
     */
    public CrawlTask(int taskId, String url, String targetPath, String targetRoot) {
        this.taskId = taskId;
        this.url = url;
        this.targetPath = targetPath;
        this.targetRoot = targetRoot;
        this.robotsRules = new HashMap<>();

    }

    /**
     * Method used to match Strings against regular expressions written in the style of filename*
     * @param string -> Identifies the string
     * @param against -> Identifies the expression
     * @return Boolean
     */

    private Boolean matchOnStar(String string, String against){
        String matchAgainst = new String();
        matchAgainst= against.replaceAll("\\*",".*");
        Pattern pattern = Pattern.compile(matchAgainst);//. represents single character
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();

    }
    /**
     * Method used to analyze a robots.txt file as defined by the standard.
     * This method should be used only the first time when accessing a domain.
     * This method initializes the HashMap robotsRules.
     * @param robotsURL -> The URL where the robots file is located, usually www.domain.com/robots.txt
     *
     */
    private void readRobots(String robotsURL){
        String robotsString;
        try {
            URL url = new URL(robotsURL);
            Logger.getInstance().write("Parsing robots page "+robotsURL, 0, 3);

            BufferedReader siteReader = new BufferedReader(new InputStreamReader(url.openStream()));
            // Read each line into a stringBuffer for further processing


            String line;
            StringBuilder siteBuffer = new StringBuilder();
            while ((line = siteReader.readLine()) != null) {
                siteBuffer.append(line + "<newLine>");
            }

            siteReader.close();
            robotsString=siteBuffer.toString();
        } catch (Exception exception) {

            Logger.getInstance().write("Can't access "+robotsURL,2,1);
            new InternalException(exception.getMessage()).getMessage();
            return;
        }

        ArrayList<String> lineList = new ArrayList<String>(Arrays.asList(robotsString.split(System.getProperty("line.separator"))));

        /**
         * Parse every line and check whether it matches the begging of the rules for the given name of the webCrawler
         * Default name is empty, should match on *
         */
        Integer rulesIndex = null;
        for(String rule : lineList){
            Pattern pattern = Pattern.compile(userAgentMatcher);
            Matcher matcher = pattern.matcher(rule);
            if(matcher.matches()){
                String userPattern = rule.substring(userAgentIndex);
                if(matchOnStar(userAgent,userPattern)){
                    /**
                     * Found the first entry that matches our webCrawler name
                     */
                    rulesIndex = lineList.indexOf(rule)+1;
                    break;
                }
            }
        }
        if(rulesIndex == null){
            /**
             * robots.txt might be empty or have no viable entries.
             */
            Logger.getInstance().write("No entries found in "+robotsURL, 0, 3);
            return;
        }
        Pattern pattern = Pattern.compile(disallowRuleRegex);
        /**
         * Getting rid of every other entries before our webCrawler rules.
         */
        lineList.subList(0,rulesIndex).clear();
        for(String rule : lineList){
            Pattern rulePattern = Pattern.compile(ruleRegex);
            Matcher ruleMatcher = rulePattern.matcher(rule);
            if(ruleMatcher.matches()){
                /**
                 *Check whether the rule is allow/disallow
                 */
                Pattern allowPattern = Pattern.compile(allowRuleRegex);
                Matcher allowMatcher = allowPattern.matcher(rule);
                if(allowMatcher.matches()){
                    /**
                     * Allow rule
                     */
                    this.robotsRules.put(rule.substring(allowRuleIndex).replaceAll("\\s",""),ruleAllow);


                }
                else{
                    /**
                     * Disallow rule
                     */
                    this.robotsRules.put(rule.substring(disallowRuleIndex).replaceAll("\\s",""),ruleDisalow);

                }
            }
            else{
                break;
            }
        }
        Logger.getInstance().write("Parsed successfully robots file: "+robotsURL, 0, 3);


    }

    /**
     * Returns a resolved string with the destination of the next reference.
     *
     * @param refType type of the reference ( anchor, image, script or link).
     * @param url     the url that needs to be resolved.
     * @param baseUrl the base url for given website
     * @return a string containing the path in the system for current url.
     * @throws CrawlerException exception if the resolve will fail.
     */
    private String resolveHtml(String refType, String url, String baseUrl) throws CrawlerException {

        String resolvedString = this.targetRoot;

        String customPath = url.substring(baseUrl.length() + 1, url.length());
        customPath = customPath.replaceAll("/", "_");

        String urlPath = url.substring(baseUrl.length());
        int indexOfExtension = urlPath.lastIndexOf('.');

        switch (refType) {
            case "script" -> resolvedString = resolvedString + "/scripts/" + customPath;
            case "img" -> resolvedString = resolvedString + "/images/" + customPath;
            case "a" -> {
                if (indexOfExtension != -1) { //We already have extension, so no append + '.html'
                    resolvedString = resolvedString + "/pages/" + customPath;
                } else {
                    resolvedString = resolvedString + "/pages/" + customPath + ".html";
                }
            }
            case "link" -> resolvedString = resolvedString + "/css/" + customPath;
            default -> throw new InternalException("This should never throw @CrawlTask.resolveHTML!");
        }
        return resolvedString;
    }

    /**
     * Parses a given web page for refs ( a | img | script | link ).
     *
     * @param webPage a web page in String format.
     * @param baseUrl Base url for web page.
     * @return A HashMap of links and array of original link to be replaced and the target path to be downloaded in.
     * @throws CrawlerException exception if parsing will fail.
     */
    private HashMap<String, String[]> parsePageHtml(String webPage, String baseUrl) throws CrawlerException {
        try {
            //This function is too big @! maybe improve here
            //Assert baseUrl is not null
            if (baseUrl == null)
                baseUrl = "https://" + (new URI(this.url)).getHost();

            //String baseUrl = "https://" + (new URI(this.url)).getHost();
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

                if (newUrl.equals(baseUrl + "/") || newUrl.equals(baseUrl))
                    continue;

                if (newUrl.regionMatches(true, 0, baseUrl, 0, baseUrl.length())) {

                    //If the reference is a website part of same domain
                    //We can have arguments inside this uris, remove them
                    if ((indexOf = newUrl.indexOf("?")) != -1) {
                        newUrl = newUrl.substring(0, indexOf);
                    }

                    //We will not try to download the .php files
                    if (!newUrl.endsWith(".php")) {
                        returnHash.putIfAbsent(newUrl, new String[]{newUrl,
                                resolveHtml(pageMatcher.group(2), newUrl, baseUrl)});
                    }
                } else {
                    Matcher pathMatcher = internalPattern.matcher(newUrl);
                    if (pathMatcher.find() && newUrl.equals(pathMatcher.group())) {
                        //If it is a reference inside the root dir
                        String tempUrl = baseUrl + newUrl;
                        //We will not try to download the .php files
                        if (!tempUrl.endsWith(".php")) {
                            returnHash.putIfAbsent(tempUrl, new String[]{newUrl,
                                    resolveHtml(pageMatcher.group(2), tempUrl, baseUrl)});
                        }
                    }
                }
            }
            return returnHash;
        } catch (Exception exception) {
            throw new InternalException("Web page does not exist!");
        }
    }

    /**
     * Create the string with refs of the current Task
     *
     * @return a string witch contains the entire web page
     * @throws CrawlerException if an error occurs during the download process
     */
    private String createStringPage() throws CrawlerException {
        try {
            URL url = new URL(this.url);
            //Logger.getInstance().write("Downloading web page " + this.url, 0, 3);

            Logger.getInstance().write("Downloading web page " + this.url + "depth level -> " + this.taskId, 0, 3);

            BufferedReader siteReader =
                    new BufferedReader(new InputStreamReader(url.openStream()));
            // Read each line into a stringBuffer for further processing


            String line;
            StringBuilder siteBuffer = new StringBuilder();
            while ((line = siteReader.readLine()) != null) {
                siteBuffer.append(line + "<newLine>");
            }

            siteReader.close();
            return siteBuffer.toString();
        } catch (Exception exception) {
            throw new InternalException(exception.getMessage());
        }
    }

    /**
     * Downloads a web page as binary, used for .zip, .png and all other files
     * that do not require processing.
     *
     * @throws CrawlerException if an exception occurs while downloading.
     */
    private void downloadBinaryPage() throws CrawlerException {
        try {
            URL url = new URL(this.url);
            BufferedInputStream in = new BufferedInputStream(url.openStream());

            File filePath = new File(targetPath.substring(0, targetPath.lastIndexOf("/")));

            if (!filePath.exists())
                filePath.mkdirs();

            FileOutputStream fileOutputStream = new FileOutputStream(this.targetPath);
            Logger.getInstance().write("Downloading web page " + this.url + "depth level -> " + this.taskId, 0, 3);
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new InternalException(e.getMessage());
        }
    }

    /**
     * This method is useful for the last level of recursion where only the references to the already downloaded pages need to be replaced.
     *
     * @param path the local path of the reference to be replaced
     * @return true/false if it can be replaced
     */
    private boolean testForReplace(String path) {
        try {
            Configurations confInstance = Configurations.getInstance();

            int depthLv = confInstance.getDepthLevel();

            if (taskId == depthLv) {//daca suntem pe ultimul nivel
                File testExist = new File(path);
                if (testExist.exists()) { //testez daca exista url-ul descarcat sa-l inlocuiesc
                    return true;
                } else {
                    return false;//nu inlocuiesc daca NU exista
                }

            } else {
                return true;//inlocuiesc toate ref pana la ultimul nivel
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return true;
    }

    /**
     * Boolean method that tests->if the maximum level of recursion has been reached
     * ->if path and url is null
     * ->if a ref page has already been downloaded
     *
     * @param nextPath the following url to download
     * @return true/false
     */
    private boolean testTask(String nextPath) {
        try {
            Configurations confInstance = Configurations.getInstance();
            int depthLv = confInstance.getDepthLevel();

            if ((this.url == null) || (this.targetPath == null)) {
                throw new InputException("Null HashMap given to method startTasks");
            }
            if (taskId + 1 <= depthLv) {
                File testExist = new File(nextPath);
                return !testExist.exists();
            } else {
                return false;
            }
        } catch (_CrawlerException error) {
            error.getMessage();
        }
        return false;
    }

    /**
     * The method that starts the tasks of downloading the initial url references to the max depth set
     *
     * @param nextRef
     */
    private void startTasks(HashMap<String, String[]> nextRef) {
        try {
            CrawlerThreadPool threadPoolInstance = CrawlerThreadPool.getInstance();
            if (nextRef == null) {
                throw new InputException("Null HashMap given to method startTasks");
            }
            for (String key : nextRef.keySet()) {

                if (testTask(nextRef.get(key)[1])) { //test depth+already exist

                    //  Factory factoryTask = new Factory();
                    List<String> argsList = new ArrayList<>();

                    String idStr = String.valueOf(taskId + 1);
                    argsList.add((idStr));
                    argsList.add(key);//url
                    argsList.add(nextRef.get(key)[1]);//path

                    threadPoolInstance.putTask(new CrawlTask(taskId + 1, key, nextRef.get(key)[1], targetRoot));
                }
            }

        } catch (_CrawlerException crawlerException) {
            crawlerException.getMessage();
        }
    }

    /**
     * Method that download page from current url and replaces all references with the names of locally downloaded files
     */
    private void replaceHTMLRef(HashMap<String, String[]> refMap, String siteString) {
        try {
            String[] siteLines = siteString.split("<newLine>");

            File filePath = new File(targetPath.substring(0, targetPath.lastIndexOf("/")));

            String absoluteFilePath = filePath.getCanonicalPath();

            if (this.taskId != 0)
                absoluteFilePath = filePath.getParentFile().getCanonicalPath();

            absoluteFilePath = absoluteFilePath.replace('\\', '/') + "/";

            if (!filePath.exists())
                filePath.mkdirs();

            BufferedWriter writer = new BufferedWriter(new FileWriter(targetPath));
            String line;
            for (int i = 0; i < Arrays.stream(siteLines).count(); i++) {
                line = siteLines[i];
                int status = 0;
                String newStateLine = line;
                for (String element : refMap.keySet()) {

                    line = newStateLine;
                    String patternString = refMap.get(element)[0];
                    Pattern pattern = Pattern.compile(patternString);
                    Matcher matcher = pattern.matcher(line);

                    String lastStateLine = line;

                    while (matcher.find()) {
                        int lenString = lastStateLine.length();

                        if ((lastStateLine.charAt(matcher.end()) == '"')
                                && (lastStateLine.charAt(matcher.start() - 1) == '"')) {

                            String replaceString = refMap.get(element)[1].substring(this.targetRoot.length() + 1);

                            if (testForReplace(replaceString)) {

                                newStateLine = lastStateLine.substring(0, matcher.start()) + absoluteFilePath
                                        + replaceString + lastStateLine.substring(matcher.end(), lenString);

                                matcher = pattern.matcher(newStateLine);
                                lastStateLine = newStateLine;
                                status = 1;
                            }
                        } else {
                            continue;
                        }
                    }
                }
                if ((status != 0)) {
                    writer.write(newStateLine);
                } else {
                    writer.write(line);
                }
            }
            writer.close();
        } catch (Exception error) {
            error.getMessage();
        }
    }

    /**
     * This method ->overrides the _run() method from Task abstract class.
     * It is responsible for starting the application functionality.
     */
    @Override
    protected void _run() {
        try {

            if (this.taskId == 0) {
                this.targetPath = this.targetRoot + "/index.html";
            }

            String baseUrl = "https://" + (new URI(this.url)).getHost();
            String urlPath = this.url.substring(baseUrl.length());
            int indexOfExtension = urlPath.lastIndexOf('.');
            if (indexOfExtension != -1) { //If we have an extension
                String extension = urlPath.substring(indexOfExtension);
                if (!extension.equalsIgnoreCase(".html")) { //or other extensions for web
                    //Download normal binary page
                    downloadBinaryPage();
                    return;
                }
            }
            //Download normal page, replace refs, etc

            String stringPage = createStringPage();
            HashMap<String, String[]> hashMap = parsePageHtml(stringPage, baseUrl);
            replaceHTMLRef(hashMap, stringPage);
            //Logger.getInstance().write("Waiting for " + Configurations.getInstance().getDelay() + " ms.", 0, 3);
            Thread.sleep((long) Configurations.getInstance().getDelay());
            startTasks(hashMap);

        } catch (Exception crawlerException) {
            crawlerException.getMessage();
        }
    }
}
