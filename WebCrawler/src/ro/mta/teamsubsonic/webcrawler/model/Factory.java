package ro.mta.teamsubsonic.webcrawler.model;
import ro.mta.teamsubsonic.webcrawler.model.exceptions.*;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Class responsible for generating Task and Crawler objects.
 *
 * @author Florea Vlad
 */


public class Factory {
    /**
     * Token used for splitting the extensions string
     * removes multiple white spaces
     */
    private static final String extensionRegex = "\\s+";
    /**
     * Inline definition for arg count and error codes.
     */

    private static final int createCrawlTaskArgsCount =3;
    private static final int createSiteMapArgsCount =2;
    private static final int searchServiceArgsCount=5;
    private static final int BADPATH=-1;
    private static final int ISADIR=2;
    private static final int ISAFILE= 3;

    /**
     * Check if a path is ok and returns the appropriate code.
     */
    private static Integer checkPath(String path){

        File f = new File(path);
        if(!f.exists()){
            return BADPATH;
        }
        if((f.isDirectory())){
            return ISADIR;
        }
        return ISAFILE;


    }
    /**
     * Method that checks if an url is valid as a string.*
     */
    private static boolean checkUrl(String url){

        try{
            new URL(url).toURI();
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    /**
     * Create task method.
     * @return reference to created object
     * @throws CrawlerException as BadURIException,InputException and InternalException.
     *
     */
    public Task createTask (Class<?> type, List<String> args) throws CrawlerException {
            if(type == CrawlTask.class) {
                /**
                 * Verifying the number of args
                 */
                if (args.size() != createCrawlTaskArgsCount) {
                    throw new InternalException("Wrong number of arguments to create CrawlTask. Expected " + createCrawlTaskArgsCount + " received " + args.size());
                }
                int taskId;
                /**
                 * Verifying the Id
                 */
                try {
                    taskId= Integer.parseInt(args.get(0));
                }
                catch (NumberFormatException e)
                {
                    throw new InternalException("Bad task Id. Not a number.");
                }
                /**
                 * Verifying the URL
                 */
                if(!checkUrl(args.get(1))){
                    throw new BadURIException("Bad URI format:"+ args.get(1)+" .This is not a URI!");
                }


                CrawlTask retCrawlTask = new CrawlTask(taskId,args.get(1),args.get(2));
                return retCrawlTask;

            }
        throw new InternalException("Trying to create a wrong type of Task!");
    }

    /**
     * Create crawler method.
     * @return Crawler as CrawlService, SearchService,SiteMapService
     * @throws CrawlerException as InputException and InternalException.
     *
     */
    public Crawler createCrawler(Class<?>  type, List<String> args) throws CrawlerException{
        /**
         *Create the appropiate type of object
         *
         */
        if(type == CrawlService.class){
            for(String url : args){
                if(!checkUrl(url)){
                    throw new BadURIException("Error creating CrawlService. One of the urls is wrong:"+url);
                }
            }
            return new CrawlService(args);
        }
        if(type == SiteMapService.class){
            //path si outhfile -> ambele fisiere
            /**
             * Check to see if number of args matches
             */
            if(args.size() != createSiteMapArgsCount){
                 throw new InternalException("Wrong number of arguments to create SiteMapService. Expected " + createSiteMapArgsCount + " received " + args.size());
             }
            /**
             * Check to see if the path given is valid.
             */
                String Path = args.get(0);
                int retCode =checkPath(Path);
                if(retCode == ISAFILE){
                    if(retCode == ISADIR){
                        throw new InputException("The given path to site's root file is a directory:"+Path);
                    }
                    else{
                        throw new InputException("The given path to site's root file is not valid:"+Path);
                    }
                }

           return new SiteMapService(args.get(0),args.get(1));
        }
        if(type == SearchService.class){

            /**
             * Check size of arg
             */
            if(args.size() != searchServiceArgsCount){
                throw new InternalException("Wrong number of arguments to create SiteMapService. Expected " + searchServiceArgsCount + " received " + args.size());
            }
            /**
             * Parse the extensions
             */
            List<String> extensions=null;
            try {
                extensions=Arrays.asList(args.get(0).split(extensionRegex));
            }
            catch (Exception e){
                extensions=null;
            }
            String keyword =args.get(1);

            /**
             * cast to integer
             */
            Long maxSize=null;
            try {
                maxSize= Long.parseLong(args.get(2));
            }
            catch (NumberFormatException e)
            {
                maxSize=null;
            }
            String pattern = args.get(3);
            String path =args.get(4);
            return  new SearchService(extensions,keyword,maxSize,pattern,path);

        }

        throw new InternalException("Trying to create a wrong type of Crawler!");
    }

}
