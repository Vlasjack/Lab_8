import java.util.*;

public class CrawlerTask implements Runnable {
    
	// Объект-пара
    private URLDepthPair element;
    
	// Объект со всеми списками
    private URLPool myPool;
    
	public CrawlerTask(URLPool pool) {
        this.myPool = pool;
    }
    
    /** Запуск заданий CrawlerTasks */
    public void run() {

		// Поток получает следующих элемент из списка непросмотренных
		// адресов или входит в режим ожидания
        element = myPool.get();
        
        // Глубина текущего элемента
        int myDepth = element.getDepth();
        
        // Получение всех ссылок после парсинга
        LinkedList<URLDepthPair> linksList = new LinkedList<URLDepthPair>();
        linksList = Crawler.parsePage(element);
        
		for (URLDepthPair pair: linksList) {
			myPool.put(pair);
		}
		
    }
}