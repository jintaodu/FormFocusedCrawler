package web_crawler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import web_crawler.Crawler;

public class Test_Crawler {
	/*
	 * 15 different languages: Catalan (cat), Danish (dk), English (en),
	 * Estonian (ee), Finnish (fi), French (fr), German (de), Italian (it),
	 * Japanese (jp), Korean (kr), Norwegian (no), Sorbian (sorb), Swedish (se),
	 * and Turkish (tr).
	 */
	public static void main(String[] args) {

		ExecutorService pool = Executors.newFixedThreadPool(8);

		Crawler Jobs = new Crawler(5, "en");
		Crawler Automobiles = new Crawler(1, "en");

		pool.execute(Jobs);
		//pool.execute(Automobiles);

		pool.shutdown();
	}
}
