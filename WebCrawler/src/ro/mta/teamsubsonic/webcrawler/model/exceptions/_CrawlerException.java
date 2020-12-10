package ro.mta.teamsubsonic.webcrawler.model.exceptions;
import ro.mta.teamsubsonic.webcrawler.view.Logger;

/**
 * Concrete class for abstract CrawlerException
 * for all exceptions to extend
 *
 * @author Phineas09
 */
public class _CrawlerException extends CrawlerException {

    /**
     * Message string, contains the corresponding error message
     * ExceptionType contains the type of the thrown error
     */
    private String message;
    private String exceptionType;

    /**
     * Constructor for _CrawlerException
     *
     * @param exceptionType exceptionType of the derived error
     * @param message custom error message
     */
    public _CrawlerException(String exceptionType, String message) {
        this.message = message;
        this.exceptionType = exceptionType;
    }

    /**
     * Returns a custom message for the current error "exceptionType : message"
     * @return String which represents the error type and error message
     */
    @Override
    public String getMessage() {
        String messageString = "Exception -> " + exceptionType + " : " + message;
        Logger.getInstance().write(messageString, 0, 1); //Always show as errors
        return messageString;
    }
}
