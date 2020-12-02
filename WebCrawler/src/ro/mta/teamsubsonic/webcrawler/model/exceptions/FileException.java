package ro.mta.teamsubsonic.webcrawler.model.exceptions;

/**
 * Exception in case a file cannot be opened
 *
 * @author VladTeapa
 */
public class FileException extends _CrawlerException {
    /**
     * Constructor for FileException
     *
     * @param message custom error message
     */
    public FileException(String message) {
        super("FileException", message);
    }
}
