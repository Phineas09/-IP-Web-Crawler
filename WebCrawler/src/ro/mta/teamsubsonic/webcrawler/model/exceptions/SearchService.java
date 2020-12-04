package ro.mta.teamsubsonic.webcrawler.model.exceptions;

import ro.mta.teamsubsonic.webcrawler.model.Crawler;

import java.util.List;


public class SearchService implements Crawler {
    private List<String> extensions;
    private String keyword;
    private Integer maxSize;
    private String pattern;

    public SearchService(List<String> extensions, String keyword, Integer maxSize, String pattern){
        this.extensions=extensions;
        this.keyword=keyword;
        this.maxSize=maxSize;
        this.pattern=pattern;
    }
    @Override
    public void run(){

    }
}
