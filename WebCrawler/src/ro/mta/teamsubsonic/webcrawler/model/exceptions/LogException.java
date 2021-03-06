package ro.mta.teamsubsonic.webcrawler.model.exceptions;

/**
 * Exception in case log has an error
 * @see ro.mta.teamsubsonic.webcrawler.model.exceptions._CrawlerException
 * @author VladTeapa
 */
public class LogException extends _CrawlerException {
    /**
     * Constructor for _CrawlerException
     *
     * @param message custom error message
     */
    public LogException(String message) {
        super("LogException", message);
    }
}
