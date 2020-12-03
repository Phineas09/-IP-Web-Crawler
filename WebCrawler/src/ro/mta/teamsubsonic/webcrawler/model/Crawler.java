package ro.mta.teamsubsonic.webcrawler.model;

/**
 * Interface class through which the functionalities of the application are provided
 *  Contains the run() method that launches one of the services offered by the application
 *
 * @author Gunyx
 */

public interface Crawler {
    /**
     *Run method -> launches in execution the functionalities of the application(crawl/sitemap/search)
     *           -> this method will be overridden by the inheriting classes
     */
    public void run();
}
