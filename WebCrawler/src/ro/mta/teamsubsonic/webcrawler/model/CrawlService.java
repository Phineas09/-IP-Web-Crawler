package ro.mta.teamsubsonic.webcrawler.model;


import ro.mta.teamsubsonic.webcrawler.model.exceptions.InternalException;
import ro.mta.teamsubsonic.webcrawler.model.exceptions._CrawlerException;
import ro.mta.teamsubsonic.webcrawler.view.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that implements interface Crawler.
 * Responsible for generating Task Objects and pushing them into the thread pool
 *
 * @author Florea Vlad
 */
public class CrawlService implements Crawler {
    private static final int STARTINDEX=0;

    private List<String> urls;
    /**
     * Run method.
     *  Gets the threadPool instance
     *  Creates a list with the url.
     *  Creates the appropriate task (i.e. CrawlTask) through a factory object.
     *  Puts it in the threadPoolInstance.
     *
     *  ONE TASK for each URL.
     */
    @Override
    public void run(){
        try {
            CrawlerThreadPool threadPoolInstance = CrawlerThreadPool.getInstance();

            for(String url : urls){
                Factory factory= new Factory();
                List<String> args= new ArrayList<>();

                /**
                 * Always start at index 0
                 */
                args.add(String.valueOf(STARTINDEX));
                /**
                 * Target url
                 */
                args.add(url);
                /**
                 * Target directory
                 */
                args.add(Configurations.getInstance().getTargetDirectory());

                Task urlTask = factory.createTask(CrawlTask.class,args);

                threadPoolInstance.putTask(urlTask);
            }

        }
        catch(_CrawlerException e){
            e.getMessage();

        }
    }
    public CrawlService(List<String> urls){
        this.urls = new ArrayList<>(urls);
    }
}
