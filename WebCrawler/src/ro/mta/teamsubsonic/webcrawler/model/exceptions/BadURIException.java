package ro.mta.teamsubsonic.webcrawler.model.exceptions;

/**
 * Exception in case uri is not correct
 *
 * @author VladTeapa
 */
public class BadURIException extends _CrawlerException {

    /**
     * Constructor for BadURIException
     *
     * @param message custom error message
     */
    public BadURIException(String message) {
        super("BadURIException", message);
    }
}
