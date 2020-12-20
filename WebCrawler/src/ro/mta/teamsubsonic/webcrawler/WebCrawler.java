package ro.mta.teamsubsonic.webcrawler;

import ro.mta.teamsubsonic.webcrawler.controller.CrawlerController;
import ro.mta.teamsubsonic.webcrawler.model.Configurations;
import ro.mta.teamsubsonic.webcrawler.model.CrawlTask;
import ro.mta.teamsubsonic.webcrawler.model.Crawler;
import ro.mta.teamsubsonic.webcrawler.model.CrawlerThreadPool;
import ro.mta.teamsubsonic.webcrawler.model.exceptions.CrawlerException;
import ro.mta.teamsubsonic.webcrawler.model.exceptions.InternalException;
import ro.mta.teamsubsonic.webcrawler.model.exceptions._CrawlerException;

public class WebCrawler {

    public static void main(String[] args) {

        try {
            CrawlerController webCrawler = new CrawlerController(args);
            webCrawler.execute();

        }
        catch (Exception exception) {
            exception.getMessage();
        }
    }
}
