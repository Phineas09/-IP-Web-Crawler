package ro.mta.teamsubsonic.webcrawler.model;
import ro.mta.teamsubsonic.webcrawler.model.exceptions.InternalException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Class implementing the application thread pool
 * Has a member of type ExecutorService and is a Singleton as
 * we only want one instance of this thread pool (global in application)
 *
 * @author Phineas09
 */
public class CrawlerThreadPool {

    /**
     * We have the threadPool which is of type ExecutorService, holds and manages the entire thread pool
     * We have instance, mandatory for Singleton pattern
     * And currentExecutingTasks as an integer to keep track of the tasks both running and in queue.
     */
    private final ExecutorService threadPool;
    private static CrawlerThreadPool instance = null;
    private int currentExecutingTasks;

    /**
     * Private constructor for Singleton pattern
     * requires Configurations class
     *
     * @throws InternalException if thread Pool fails to initialize or Configurations class was not initialized
     */
    private CrawlerThreadPool() throws InternalException {
        try {
            threadPool = Executors.newFixedThreadPool(Configurations.getInstance().getThreadsNumber());
            currentExecutingTasks = 0;
        }
        catch (Exception exception) {
            throw new InternalException("Configurations class was not initialized!");
        }
    }

    /**
     * Increments or Decrements currentExecutingTasks number and returns the value
     *
     * @param updateCount 0 just for current int, 1 for increment, -1 for decrement
     * @return number of threads currently in queue and in execution
     */
    public synchronized int threadSafeUpdatePoolCount(int updateCount) {
        currentExecutingTasks += updateCount;
        return currentExecutingTasks;
    }

    /**
     * Returns if it exists instance of CrawlerThreadPool else it
     * creates it.
     *
     * @return CrawlerThreadPool instance
     * @throws InternalException if instantiating the instance fails
     */
    public static CrawlerThreadPool getInstance() throws  InternalException {
        try {
            if (instance == null)
                instance = new CrawlerThreadPool();
            return instance;
        }
        catch (Exception exception) {
            throw new InternalException("Error while creating the thread pool!");
        }
    }

    /**
     * Each Task must implement the _run method
     *
     * @param queueTask new Task to be put in current thread pool queue
     * @throws InternalException if the thread pool has been disabled
     */
    public void putTask(Task queueTask) throws InternalException {
        if(!threadPool.isShutdown()) {
            //Increment counter
            threadSafeUpdatePoolCount(1);
            //Add Task in the queue
            threadPool.execute(queueTask);
        }
        else
            throw new InternalException("Thread pool is disabled!");
    }

    /**
     * Must be called before exiting the program, or it will execute forever
     * Disables putTask function, it will return InternalException
     * Waits for all threads to terminate execution, if the call is made from
     * a worker thread it will close it as well.
     *
     * @return 0 if called from Main Thread and 1 if called from worker thread.
     * @throws InternalException if it takes more than 5 seconds for all threads to finnish.
     */
    public int shutdownAndAwaitTermination() throws  InternalException {
        threadPool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                threadPool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!threadPool.awaitTermination(5, TimeUnit.SECONDS))
                    throw new InternalException("Thread pool did not terminate!");
            }
            return 0;
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            threadPool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
            return 1;
        }
    }

}
