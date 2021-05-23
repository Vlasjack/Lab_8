import java.lang.Exception;
import java.util.*;
import java.net.MalformedURLException;
import java.net.*;
import java.io.*;

public class CrawlerHelper {
	
	// Набор форматов, которые поддерживает браузер
	public static String[] formats = {".html", ".pdf", ".java", ".xml", "txt", ".css", ".doc", ".c"};	
	
	/*
	* Общий алгоритм для проверки аргументов
	*/
	public URLDepthPair getURLDepthPairFromArgs(String[] args) {
		if (args.length > 2) System.out.println("Warning more than 2 parameters from command line!\n");
		if (args.length < 2) {
			System.out.println("Warning less than 2 parameters from command line!\n");
			return null;
		}
		
		// Проверка второго параметра - глубины
		int depth;
		try {
			depth = Integer.parseInt(args[1]);
		} catch (Exception e) {
			System.out.println("Error depth parameter!");
			return null;
		}
		
		URLDepthPair urlDepth;
		
		// Вызов конструктора класса конструктором класса (вызовается, если не понравится какой-либо параметр)
		try {
			urlDepth = new URLDepthPair(args[0], depth);
		} 
		catch (MalformedURLException ex) {
			System.out.println(ex.getMessage() + "\n");
			return null;
		}  
		catch (IllegalArgumentException e) {
			System.out.println(e.getMessage() + "\n");
			return null;
		}
		
		return urlDepth;
	}
	
	
	/*
	* Получение пользовательского ввода
	*/
	public URLDepthPair getURLDepthPairFromInput() {
		
		// Временные переменные для хранения
		String url;
		int depth;
		
		// Массив со значениями для передачи на проверку
		String[] args;
		
		// Объект искомого класса
		URLDepthPair urlDepth = null;
		
		// Сканер ввода пользователя
		Scanner in = new Scanner(System.in);
		
		/*
		* Считывание, преобразование и проверка ввода пользователя
		*/ 	
		while (urlDepth == null) {
			
			// Считывание
			System.out.println("Enter URL and depth of parsing (in a line with a space between):");
			String input = in.nextLine();
		
			// Преобразование
			args = input.split(" ", 2);
			
			// Проверка
			urlDepth = this.getURLDepthPairFromArgs(args);
			if (urlDepth == null) System.out.println("Try again!\n");
		}
		return urlDepth;	
	}
	
	/*
	* Получение количества потоков
	*/
	public static int getNumOfThreads(String[] args) {
		if (args == null || args.length < 3) {
			// Получение ввода от пользователя
			
			// Сканер ввода пользователя
			Scanner in = new Scanner(System.in);
			
			boolean nice = false;
			int input = Crawler.NUM_OF_DEFAULT_THREADS;
			
			while (!nice) {
				try{
					System.out.println("\nEnter amount of threads which you want to do parsing:");
					input = in.nextInt();
					if (input > 0 && input < 100) nice = true;
				}
				catch (Exception e) {
					in.nextLine();
				}
			}
			System.out.println("");
			return input;
		} 
		else {
			// Считывание из аргументов командной строки
			int threads;
			try {
				threads = Integer.parseInt(args[2]);
				if (threads > 0) return threads;
			} catch (Exception e) {
				System.out.println("Error threads-parameter in arguments. Using default amount!");
			}
		}
		return Crawler.NUM_OF_DEFAULT_THREADS;
	}

	// Выделение ссылки из текста тэга
	public static String getURLFromHTMLTag(String line) {
		if (line.indexOf(Crawler.HOOK_REF) == -1) return null;
		
		int indexStart = line.indexOf(Crawler.HOOK_REF) + Crawler.HOOK_REF.length();
		int indexEnd = line.indexOf("\"", indexStart);
		// Если получится так, что кавычек больше нет
		if (indexEnd == -1) return null;
		return line.substring(indexStart, indexEnd);
	}
	
	// Очищает от мусора после адреса
	// После определяет, указана ли ссылка в формате конечного файла, если да
	// То из адреса вырезается имя файла с расширением
	// То есть остается лишь каталог, в котором находится этот файл
	public static String cutURLEndFormat(String url) {
		url = CrawlerHelper.cutTrashAfterFormat(url);
		for (String format : formats) {
			if (url.endsWith(format)) {
				int lastCatalog = url.lastIndexOf("/");
				return url.substring(0, lastCatalog + 1);
			}
		}
		return url;
	}
	
	// Склейка ссылки с возвратом с полной текущей ссылкой
	public static String urlFromBackRef(String url, String backRef) {
		int count = 2;
		int index = url.length();
		char[] urlSequnce = url.toCharArray();
		while (count > 0 && index > 0) {
			index -= 1;
			if (urlSequnce[index] == '/') count -= 1;
		}
		
		if (index == 0) return null;
		
		String cutURL = url.substring(0, index + 1);
		String cutBackRef = backRef.substring(3, backRef.length());
		
		return (cutURL + cutBackRef);
	}
	
	// Очистка лишней вохможной информации после указания формата в адресе
	public static String cutTrashAfterFormat(String url) {
		int index = url.lastIndexOf("#");
		if (index == -1) return url;
		return url.substring(0, index);
		
	}
}