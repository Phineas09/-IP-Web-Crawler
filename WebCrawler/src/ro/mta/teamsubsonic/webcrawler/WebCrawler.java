package ro.mta.teamsubsonic.webcrawler;

import ro.mta.teamsubsonic.webcrawler.model.Configurations;
import ro.mta.teamsubsonic.webcrawler.model.CrawlTask;
import ro.mta.teamsubsonic.webcrawler.model.CrawlerThreadPool;
import ro.mta.teamsubsonic.webcrawler.model.exceptions.CrawlerException;

public class WebCrawler {

    public static void main(String[] args) {

        try {
            Configurations.getInstance(null);
            CrawlerThreadPool crawlerThreadPool = CrawlerThreadPool.getInstance();

            System.out.println(Configurations.getInstance().getLogLevel());

            crawlerThreadPool.putTask(new CrawlTask(0, "https://mta.ro/",
                    "tests/mta.ro"));

            crawlerThreadPool.putTask(new CrawlTask(0, "https://wiki.mta.ro/",
                    "tests/wiki.mta.ro"));

            while(crawlerThreadPool.threadSafeUpdatePoolCount(0) != 0) {
                Thread.sleep(2000);
            }
            crawlerThreadPool.shutdownAndAwaitTermination();
        }
        catch (Exception exception) {
            exception.getMessage();
        }
    }
}
