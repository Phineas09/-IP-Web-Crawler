package ro.mta.teamsubsonic.webcrawler.model.exceptions;

/**
 * Abstract class for all Exception in this application
 *
 * @author Phineas09
 */
public abstract class CrawlerException extends Exception {

    /**
     * Returns a custom message for the current error "exceptionType : message"
     * @return String which represents the error type and error message
     */
    public abstract String getMessage();

}
