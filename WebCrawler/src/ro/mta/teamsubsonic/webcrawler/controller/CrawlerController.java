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
 *
 * @author Florea Vlad
 */

/**
 * ./program service args
 * service can be:
 * @param  search
 * @param sitemap
 * @param crawl
 * CLI args are:
 * -in
 * -config
 * -url
 * -keywords
 * -crawl
 * -search
 * -sitemap
 * -out
 * -pattern
 * -maxsize
 * -extensons
 */
public class CrawlerController {
    //Index 0 is the program name
    private static final Integer firstIndex=1;
    //Delim for args, -
    private static final String delim="-";

    //Args names definition
    private static final String inArg="in";
    private static final String configArg="config";
    private static final String urlArg="url";
    private static final String keywArg="keywords";
    private static final String crawlArg="crawl";
    private static final String searchArg="search";
    private static final String siteMapArg="sitemap";
    private static final String outArg="out";
    private static final String patternArg="pattern";
    private static final String maxSizeArg="maxsize";
    private static final String extensionsArg="extensions";

    //Nested class to hold the current args
    private class Args{
        public String service=null;
        public String inFile=null;
        public String configFile=null;
        public String url=null;
        public String outFile=null;
        public List<String> keywords=null;
        public String pattern =null;
        public String maxSize =null;
        public List<String> extensions =null;
    }
    private Args currentArgs;

    //Crawler object in use
    private Crawler crawlerService;

    /**
     * ParseArgs method used by constructor
     * @param args --> CLI args
     * @return Crawler object
     *
     */
    private Crawler ParseArgs(String[] args){
        this.currentArgs = new Args();
        currentArgs.service = args[firstIndex];

        String inLineString="";
        //Create inline string in order to split it by -
        for(String str : args) {
            inLineString= String.join(" ",inLineString,str);
        }

        String[] argList = inLineString.split(delim);

        //Parse the args and inits the currentArgs object.
        for(String str: argList){
            String[] content = str.split(" ");
            if(content[0].equals(inArg)){
                this.currentArgs.inFile=content[1];
            }
            else if(content[0].equals(configArg)){
                this.currentArgs.configFile=content[1];

            }
            else if(content[0].equals(urlArg)){
                this.currentArgs.url=content[1];

            }
            else if(content[0].equals(outArg)){
                this.currentArgs.outFile = content[1];
            }
            else if(content[0].equals(patternArg)){
                this.currentArgs.pattern=content[1];
            }
            else if(content[0].equals(maxSizeArg)){
                this.currentArgs.maxSize = content[1];
            }
            else if(content[0].equals(extensionsArg)){
                this.currentArgs.extensions = new ArrayList<>();
                for(Integer i=1; i< content.length;i++){
                    this.currentArgs.extensions.add(content[i]);
                }
            }
            else if(content[0].equals(keywArg)){
                this.currentArgs.keywords=new ArrayList<>();
                for(Integer i=1; i< content.length;i++){
                    this.currentArgs.keywords.add(content[i]);
                }
            }

        }
        try {

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
                 * Service type is SiteMap
                 * Its parameters are:
                 * @param extensions -> String
                 * @param keyWords -> String
                 * @param maxSize -> String
                 * @param pattern -> String
                 */

                List<String> searchServiceArgs = new ArrayList<>();

                //Converting List<String> extensions to a single string.
                String extensions_str ="";
                for(String ext : this.currentArgs.extensions){
                    extensions_str= String.join(" ",extensions_str,ext);
                }
                searchServiceArgs.add(extensions_str);

                //Converting List<String> keywords to a single string.
                String keyStr ="";
                for(String key: this.currentArgs.keywords){
                    keyStr = String.join(" ",key,keyStr);
                }
                searchServiceArgs.add(keyStr);

                if(this.currentArgs.maxSize==null){
                    throw new InputException("Missing -maxsize arg for SearchService.");
                }
                searchServiceArgs.add(currentArgs.maxSize);
                searchServiceArgs.add(currentArgs.pattern);

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
    public CrawlerController(String[] args){

        crawlerService = this.ParseArgs(args);
    }
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
    public void display(String val){
        Logger.getInstance().write(val,0);

    };


}
