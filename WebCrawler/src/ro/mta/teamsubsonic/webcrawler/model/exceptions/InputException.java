package ro.mta.teamsubsonic.webcrawler.model.exceptions;

/**
 * Exception in case the input is not correct
 *
 * @author VladTeapa
 */
public class InputException extends _CrawlerException {
    /**
     * Constructor for InputException
     *
     * @param message custom error message
     */
    public InputException(String message) {
        super("InputException", message);
    }
}
