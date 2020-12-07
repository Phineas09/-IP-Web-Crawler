package ro.mta.teamsubsonic.webcrawler.model;
import ro.mta.teamsubsonic.webcrawler.model.exceptions.CrawlerException;

/**
 * Abstract class which extends the functionality of the Runnable class
 * This class is responsible for new tasks and adding them to the ThreadPool and and decrementing the counter in the thread pool
 *
 *  @author Gunyx
 */

public abstract class Task implements Runnable {

    /**
     *  This method is responsible for the execution of various tasks. It will be overridden by the inheriting classes
     */
    protected abstract void _run();
    /**
     *  This method ->overrides the run() method from Runnable class. It is organized in an exception block
     *               -> runs the task created by the _run() function
     *               ->decrements ThreadPool counter
     */
    @Override
    public void run() {
        try {
            _run();
            CrawlerThreadPool.getInstance().threadSafeUpdatePoolCount(-1);
        }
        catch (CrawlerException crawlerException) {
            crawlerException.getMessage();
        }
    }
}
