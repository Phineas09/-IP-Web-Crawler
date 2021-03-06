package ro.mta.teamsubsonic.webcrawler.model.exceptions;

/**
 * Exception in case there is an error while downloading
 * @see ro.mta.teamsubsonic.webcrawler.model.exceptions._CrawlerException
 * @author VladTeapa
 */
public class DownloadException extends _CrawlerException {
    /**
     * Constructor for DownloadException
     *
     * @param message custom error message
     */
    public DownloadException(String message) {
        super("DownloadException", message);
    }
}
