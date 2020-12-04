package ro.mta.teamsubsonic.webcrawler.model.exceptions;
import ro.mta.teamsubsonic.webcrawler.model.Crawler;
import ro.mta.teamsubsonic.webcrawler.model.Task;
import java.util.List;

/**
 * Class responsible for generating Tasks and Crawler objects.
 *
 * @author Florea Vlad
 */
public class Factory {

    /**
     * Create task method.
     * @return reference to created object
     *
     */
    public Task createTask(Class<?> type, List<String> args){
        //Create the appropiate type of object
        return null;
    }

    /**
     * Create crawler method.
     * @return reference to created object
     *
     */
    public Crawler createCrawler(Class<?>  type, List<String> args){
        //Create the appropiate type of object
        return null;
    }

}
