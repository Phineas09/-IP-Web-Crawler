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

            crawlerThreadPool.putTask(new CrawlTask(0, "https://github.com/Phineas09/-IP-Web-Crawler",
                    "tests/mta.ro"));

            while(crawlerThreadPool.threadSafeUpdatePoolCount(0) != 0) {
                Thread.sleep(1000);
            }
            crawlerThreadPool.shutdownAndAwaitTermination();
        }
        catch (Exception exception) {
            exception.getMessage();
        }
    }
}