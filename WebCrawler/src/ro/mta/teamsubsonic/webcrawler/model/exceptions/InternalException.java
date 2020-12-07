package ro.mta.teamsubsonic.webcrawler.model.exceptions;

/**
 * Exception in case there is an internal error
 *
 * @author VladTeapa
 */
public class InternalException extends _CrawlerException {
    /**
     * Constructor for InternalException
     *
     * @param message custom error message
     */
    public InternalException(String message) {
        super("InternalException", message);
    }
}
