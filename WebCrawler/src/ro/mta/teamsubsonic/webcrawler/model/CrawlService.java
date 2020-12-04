package ro.mta.teamsubsonic.webcrawler.model;


import ro.mta.teamsubsonic.webcrawler.model.exceptions.CrawlTask;
import ro.mta.teamsubsonic.webcrawler.model.exceptions.Factory;
import ro.mta.teamsubsonic.webcrawler.model.exceptions.InternalException;

import java.util.ArrayList;
import java.util.List;

//TO DO -> REMOVE
/*CrawlerService : Crawler (Model) - aceasta este o clasa ce
        implementează interfața Crawler ce va fi responsabilă de inițializarea
        clasei CrawlerThreadPool (Singleton) și de descărcarea site-urilor
        web solicitate de utilizator.
        o Membrii:
        ▪ urls – este o listă de URL-uri furnizate de către utilizator
        ce trebuie descărcate.
        o Metode:
        ▪ run – metodă ce va inițializa thread pool-ul, va începe
        descărcarea paginilor, specificând locația pentru
        descărcare și va aștepta ca toate task-urile din coada poolului să se finalizeze.
*/

public class CrawlService implements Crawler {
private List<String> urls;
    @Override
    public void run(){
        try {
            CrawlerThreadPool threadPoolInstance = CrawlerThreadPool.getInstance();

            for(String url : urls){
                Factory factory= new Factory();
                List<String> args= new ArrayList<>();
                args.add(url);

                Task urlTask = factory.createTask(CrawlTask.class,args);

                threadPoolInstance.putTask(urlTask);
            }

        }
        catch(InternalException e){
            //TO DO: Log error

        }
    }
    public CrawlService(List<String> urls){
        this.urls = new ArrayList<>(urls);
    }
}
