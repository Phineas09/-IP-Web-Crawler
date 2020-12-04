package ro.mta.teamsubsonic.webcrawler.model.exceptions;
import ro.mta.teamsubsonic.webcrawler.model.Crawler;
import ro.mta.teamsubsonic.webcrawler.model.Task;
import java.util.List;

public class Factory {

    public Task createTask(Class<?> type, List<String> args){
        //Create the appropiate type of object
        return null;
    }

    public Crawler createCrawler(Class<?>  type, List<String> args){
        //Create the appropiate type of object
        return null;
    }

}
