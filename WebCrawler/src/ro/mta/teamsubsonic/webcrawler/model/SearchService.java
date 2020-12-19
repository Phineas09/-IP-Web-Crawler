package ro.mta.teamsubsonic.webcrawler.model;
import ro.mta.teamsubsonic.webcrawler.model.exceptions._CrawlerException;
import ro.mta.teamsubsonic.webcrawler.view.Logger;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class implementing the search part of WebCrawler application.
 * Implements the search function with the afferent rules which the application
 * will use
 *
 * @author Panțucu Flavius-Marian
 */
public class SearchService implements Crawler {
    /**
     * Member description
     */
    private List<String> fileExtensions;
    private String       keyword;
    private long         maxSize;
    private String       pattern;
    private String       path;
    /**
     * Class used to keep the files' details for later usage
     */
    public class CrawlFile{
        String fileExtension;
        String fileName;
        String filePath;
        long fileSize;
        /**
         * CrawlFile class constructor
         * @param _fileExtension Parameter where we keep the extension of the file
         * @param _fileName Parameter where we keep the name of the file
         * @param _filePath Parameter where we keep the path of the file
         * @param _fileSize Parameter where we keep the size of the file
         */
        CrawlFile(String _fileExtension, String _fileName, String _filePath, long _fileSize){
            this.fileExtension = _fileExtension;
            this.fileName = _fileName;
            this.filePath = _filePath;
            this.fileSize = _fileSize;
        }
    }
    /**
     * Function where we get all the files, recursively, using the path given
     * @param path Parameter where we going to search for files
     * @param searchFiles Parameter where we will save the files that we found
     */
    public void getFiles(String path, ArrayList<CrawlFile> searchFiles) {
        try {
            if(path == null)
                throw new _CrawlerException("InputException","Invalid input given");

            File root = new File(path);
            File[] listOfFiles = root.listFiles();

            for (File file : listOfFiles) {
                if (file.isDirectory()) {
                    //if the file is a directory, search for his files inside
                    Logger instance = Logger.getInstance();
                    instance.write(file.getName()+" is a directory, searching for files inside\n", 0,3);
                    getFiles(file.getAbsolutePath(), searchFiles);
                }
                else {
                    //get the file extension
                    int index = file.toString().lastIndexOf('.');
                    if (index > 0) {
                        String extension = file.toString().substring(index + 1);
                        CrawlFile toBeAdded = new CrawlFile(extension, file.getName(), file.getAbsolutePath(), file.length());
                        searchFiles.add(toBeAdded);
                        Logger instance = Logger.getInstance();
                        instance.write("    "+file.getName()+" is a file \n", 0,3);
                    }
                }
            }
        }
        catch (_CrawlerException error){
            error.getMessage();
        }
    }
    /**
     * Function that does the search job done. Verifies all files and make a list with
     * ones which pass the tests
     */
    @Override
    public void run(){
        ArrayList<CrawlFile> searchFiles = new ArrayList<>();
        String toBePrinted = new String("\n\n\nOutput:\n");

        getFiles(path,searchFiles);

        //for each file we will use our params to determine if the file will pass the tests
        for(CrawlFile file : searchFiles){
            if(maxSize != 0){
                if(maxSize < file.fileSize)
                    continue;
            }
            if(keyword != null) {
                if (!file.fileName.contains(keyword))
                    continue;
            }
            if(pattern != null){
                Pattern regex = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
                Matcher matcher = regex.matcher(file.fileName);
                boolean matchFound = matcher.find();
                if(!matchFound)
                    continue;
            }
            if(fileExtensions != null){
                boolean flagExtension = false;
                for(String fileExtension : fileExtensions){
                    if(fileExtension.equals(file.fileExtension)) {
                        flagExtension = true;
                        break;
                    }
                }
                if(flagExtension != true)
                    continue;
            }
            toBePrinted += file.filePath;
            toBePrinted += "\n";
        }
        Logger instance = Logger.getInstance();
        instance.write(toBePrinted,0,3);
    }
    /**
     * SearchService class constructor
     * @param _fileExtensions Parameter where we check the extension of the file
     * @param _keyword Parameter where we search by a keyword
     * @param _maxsize Parameter where we check the size of the file
     * @param _pattern Parameter where we use REGEX to see if the file corresponds
     * @param _path The path where we are going to search for files
     */
    public SearchService(List<String> _fileExtensions, String _keyword, long _maxsize, String _pattern, String _path){
        this.fileExtensions = new ArrayList<>();
        if(_fileExtensions != null)
            this.fileExtensions.addAll(_fileExtensions);
        else
            this.fileExtensions = null;
        this.keyword = _keyword;
        this.maxSize = _maxsize;
        this.pattern = _pattern;
        this.path = _path;

    }
}
