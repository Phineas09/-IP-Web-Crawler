package ro.mta.teamsubsonic.webcrawler.model.exceptions;

/**
 * Exception in case log has an error
 *
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
