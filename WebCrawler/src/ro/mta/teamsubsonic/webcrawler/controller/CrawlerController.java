package ro.mta.teamsubsonic.webcrawler.controller;

import ro.mta.teamsubsonic.webcrawler.model.*;
import ro.mta.teamsubsonic.webcrawler.model.exceptions.CrawlerException;
import ro.mta.teamsubsonic.webcrawler.model.exceptions.InputException;
import ro.mta.teamsubsonic.webcrawler.model.exceptions.InternalException;
import ro.mta.teamsubsonic.webcrawler.view.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * CrawlerController
 * Class responsible for parsing cli args and determining the type of service.
 *
 * @author Florea Vlad
 */

/**
 * ./program service args
 * service can be:
 * @param  search
 * @param sitemap
 * @param crawl
 *
 * CLI services areȘ
 *   crawl
 *   search
 *   sitemap
 * CLI args are:
 * -in
 * -config
 * -url
 * -keyword
 * -out
 * -pattern
 * -maxsize
 * -extensions
 */
public class CrawlerController {
    /**
     * In java first arg is args[0]
     */
    private static final Integer firstIndex=0;


    /**
     * Delim for args, -
     */
    private static final String delim="-";

    /**
     * User defined args
     */
    private static final String inArg="in";
    private static final String configArg="config";
    private static final String urlArg="url";
    private static final String keywArg="keyword";
    private static final String crawlArg="crawl";
    private static final String searchArg="search";
    private static final String siteMapArg="sitemap";
    private static final String outArg="out";
    private static final String patternArg="pattern";
    private static final String maxSizeArg="maxsize";
    private static final String extensionsArg="extensions";

    /**
     * Nested class used to hold args values
     */
    private class Args{
        public String service=null;
        public String inFile=null;
        public String configFile=null;
        public String url=null;
        public String outFile=null;
        public String keyword=null;
        public String pattern =null;
        public String maxSize =null;
        public List<String> extensions =null;
    }
    private Args currentArgs;

    /**
     * The crawler service in use.
     * Can be siteMap, Search or Crawl
     */
    private Crawler crawlerService;

    /**
     * Method used to parse args.
     * @param args --> CLI args
     * @return Crawler object
     *
     */
    private Crawler ParseArgs(String[] args){
        this.currentArgs = new Args();
        currentArgs.service = args[firstIndex];

        String inLineString="";
        /**
         * Joining every arg in a string
         */
        for(String str : args) {
            inLineString= String.join(" ",inLineString,str);
        }
        inLineString = inLineString.substring(1);
        /**
         * Splitting the arg string on '-'
         */
        String[] argList = inLineString.split(delim);

        /**
         * Parse every arg and add it to currentArgs
         */
        for(String str: argList){
            String[] content = str.split(" ");
            try {
                if (content[0].equals(inArg)) {
                    this.currentArgs.inFile = content[1];
                } else if (content[0].equals(configArg)) {
                    this.currentArgs.configFile = content[1];

                } else if (content[0].equals(urlArg)) {
                    this.currentArgs.url = content[1];

                } else if (content[0].equals(outArg)) {
                    this.currentArgs.outFile = content[1];
                } else if (content[0].equals(patternArg)) {
                    this.currentArgs.pattern = content[1];
                } else if (content[0].equals(maxSizeArg)) {
                    this.currentArgs.maxSize = content[1];
                } else if (content[0].equals(extensionsArg)) {
                    this.currentArgs.extensions = new ArrayList<>();
                    for (Integer i = 1; i < content.length; i++) {
                        this.currentArgs.extensions.add(content[i]);
                    }
                } else if (content[0].equals(keywArg)) {
                    this.currentArgs.keyword = content[1];
                }
            }
            catch (Exception e){
                String msg ="Error while parsing args, probably the format is wrong:"+e.getMessage();
                InputException err = new InputException(msg);
                err.getMessage();
                return null ;
            }

        }
        try {
            /**
             * Check the service type -> crawl,search, siteMap
             */

            Configurations.getInstance(this.currentArgs.configFile);
            Logger.getInstance();
            Factory factory= new Factory();
            if(this.currentArgs.service.equals(crawlArg)) {
                /**
                 * The service type is CrawlService
                 * Its parameters are
                 * @param List<String> urls
                 */
                List<String> crawlArgs=new ArrayList<>();
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(this.currentArgs.inFile));

                    String line = reader.readLine();
                    while (line != null) {
                        crawlArgs.add(line);
                        line = reader.readLine();
                    }
                    reader.close();
                }
                catch(IOException e){
                    throw new InputException("Problem reading urls from input file, -in might be null:"+this.currentArgs.inFile);
                }
                return this.crawlerService = factory.createCrawler(CrawlService.class, crawlArgs);
            }
            else if(this.currentArgs.service.equals(siteMapArg)){
                /**
                 * Service type is SiteMap
                 * Its parameters are:
                 * @param path -> String
                 * @param outhFile -> String
                 */
                List<String> siteMapArgs = new ArrayList<>();
                if(this.currentArgs.inFile ==null){
                    throw new InputException("Bad -in file is null, cannot create siteMapService");
                }
                if(this.currentArgs.outFile ==null){
                    throw new InputException("Bad -out file is null, cannot create siteMapService");
                }
                siteMapArgs.add(this.currentArgs.inFile);
                siteMapArgs.add(this.currentArgs.outFile);
                return  factory.createCrawler(SiteMapService.class,siteMapArgs);
            }
            else if(this.currentArgs.service.equals(searchArg)){

                /**
                 * Service type is Search
                 * Its parameters are:
                 * @param extensions -> String
                 * @param keyWord -> String
                 * @param maxSize -> String
                 * @param pattern -> String
                 * @param path -> String
                 */

                List<String> searchServiceArgs = new ArrayList<>();

                /**
                 * Converting List<String> extensions to a single string.
                 */

                String extensions_str ="";
                try {
                    for (String ext : this.currentArgs.extensions) {
                        extensions_str = String.join(" ", extensions_str, ext);
                    }
                }
                catch (Exception e){
                    extensions_str=null;
                }

                searchServiceArgs.add(extensions_str);



                searchServiceArgs.add(currentArgs.keyword);
                searchServiceArgs.add(currentArgs.maxSize);
                searchServiceArgs.add(currentArgs.pattern);

                if(this.currentArgs.inFile ==null){
                    throw new InputException("Bad -in file, is null, cannot create SearchService");
                }
                searchServiceArgs.add(currentArgs.inFile);

                return factory.createCrawler(SearchService.class,searchServiceArgs);
            }
            else{
                throw new InputException("Bad input:"+this.currentArgs.service+" is not a valid service!");
            }


        }
        catch (CrawlerException e){
            e.getMessage();
            return null;
        }


    }

    /**
     * Class's Constructor
     * @param args
     */
    public CrawlerController(String[] args){

        crawlerService = this.ParseArgs(args);
    }

    /**
     * Execute method, calls run() method of CrawlerService
     */
    public void execute(){
        try {
            if (this.crawlerService == null) {
                throw new InternalException("CrawlerService is null!");
            }
            this.crawlerService.run();
        }
        catch (CrawlerException e){
            e.getMessage();
        }
    };

    /**
     * Method used to display messages
     * @param val
     */
    public void display(String val){
        Logger.getInstance().write(val,0);

    };


}
