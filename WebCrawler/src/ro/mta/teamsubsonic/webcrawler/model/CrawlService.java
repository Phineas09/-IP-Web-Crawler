package ro.mta.teamsubsonic.webcrawler.model;


import ro.mta.teamsubsonic.webcrawler.model.exceptions.CrawlTask;
import ro.mta.teamsubsonic.webcrawler.model.exceptions.Factory;
import ro.mta.teamsubsonic.webcrawler.model.exceptions.InternalException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that implements interface Crawler.
 * Responsible for generating Task Objects and pushing them into the thread pool
 *
 * @author Florea Vlad
 */
public class CrawlService implements Crawler {
private List<String> urls;
    @Override
    public void run(){
        try {
            CrawlerThreadPool threadPoolInstance = CrawlerThreadPool.getInstance();

            for(String url : urls){
                Factory factory= new Factory();
                List<String> args= new ArrayList<>();
                args.add(url);

                Task urlTask = factory.createTask(CrawlTask.class,args);

                threadPoolInstance.putTask(urlTask);
            }

        }
        catch(InternalException e){
            //TO DO: Log error

        }
    }
    public CrawlService(List<String> urls){
        this.urls = new ArrayList<>(urls);
    }
}
