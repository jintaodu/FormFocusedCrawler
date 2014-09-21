package web_crawler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import extract_info.StopWords;

import extract_info.WordCount;

public class Page_Classifier {

	private final static int N = 243;// i.e. train size
	private static String[] domain_name = { "Airfares", "Automobiles", "Books",
			"CarRentals", "Hotels", "Jobs", "Movies", "MusicRecords" };

	private static int[] domain_size = { 28, 40, 39, 14, 25, 23, 39, 35 };
	// 每个领域的训练样本数

	private HashMap<String, Integer> NT = new HashMap<String, Integer>();

	private HashMap<String, Double> Page_Feature = new HashMap<String, Double>();

	public Page_Classifier(int domain) throws IOException,
			NumberFormatException {// 预处理各个领域的页面向量

		try {
			FileReader fw = new FileReader("dataset_uiuc/NT.txt");
			BufferedReader br = new BufferedReader(fw);

			String s = null;
			while ((s = br.readLine()) != null) {
				String[] ss = s.split(" ");
				int i = Integer.parseInt(ss[1]);
				NT.put(ss[0], i);
			}// 得到NT向量
			fw.close();
			br.close();
		} catch (IOException e) {
			System.out.println(e);
		}
		// System.out.println("NT.size = " + NT.size());

		String filename = "central_vector/Page_Feature/" + domain_name[domain]
				+ "_tfidf.txt";

		FileReader fr1 = new FileReader(filename);
		BufferedReader br1 = new BufferedReader(fr1);
		String s_line = null;
		while ((s_line = br1.readLine()) != null) {

			String[] s_int = s_line.split(" ");
			double ii = Double.parseDouble(s_int[1]);
			Page_Feature.put(s_int[0], ii);
		}

		fr1.close();
		br1.close();
	}

	// 得到页面与主题的相似度
	public double Get_Page_Score(String p) throws Exception {

		HashMap<String, Double> Instance = new HashMap<String, Double>();
		// 待检测网页的特征向量

		StopWords sw = new StopWords();
		String html = p;// 本页面的网页内容
		// String html = p;
		HashMap<String, Integer> wc = WordCount.count(html, sw);

		double sum = 0;
		for (String key : wc.keySet()) {

			if (NT.containsKey(key)) {
				double num = (double) wc.get(key);
				double tmp = num
						* Logarithm.log((double) N / NT.get(key), 10.0);
				sum += Math.pow(tmp, 2);

			}
		}

		double fen_mu = Math.sqrt(sum);

		// System.out.println("分母=" + fen_mu);
		sum = 0;

		for (String key : wc.keySet()) {
			if (NT.containsKey(key)) {
				double num = (double) wc.get(key);
				double tmp = num
						* Logarithm.log((double) N / NT.get(key), 10.0);
				Instance.put(key, tmp / fen_mu);
			}
		}
		// Instance用来存放当前待检测页面的特征向量
		double sim = 0;// 相似度变量
		double norm = 0.0;// 归一化因子
		double norm1 = 0.0;
		double norm2 = 0.0;
		for (String key : Page_Feature.keySet()) {
			norm1 += Math.pow(Page_Feature.get(key), 2);
			if (Instance.containsKey(key)) {
				sim += Page_Feature.get(key) * Instance.get(key);
				norm2 += Math.pow(Instance.get(key), 2);
			}
		}
		norm = Math.sqrt(norm1 * norm2);
		if (norm == 0.0)
			sim = 0;
		else
			sim /= norm;
		return sim;
	}

}

class Logarithm {

	public static double log(double value, double base) {

		// if(Math.log(value) / Math.log(base)==0)
		// System.out.println(value+"  "+base);
		return Math.log(value) / Math.log(base);
	}
}
