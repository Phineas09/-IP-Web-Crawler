package ro.mta.teamsubsonic.webcrawler.model.exceptions;

/**
 * Exception in case there is a timeout
 * @see ro.mta.teamsubsonic.webcrawler.model.exceptions._CrawlerException
 * @author VladTeapa
 */
public class TimeExceededException extends _CrawlerException {
    /**
     * Constructor for _CrawlerException
     *
     * @param message custom error message
     */
    public TimeExceededException(String message) {
        super("TimeExceedeException", message);
    }
}
