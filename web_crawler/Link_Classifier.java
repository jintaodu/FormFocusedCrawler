package web_crawler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import extract_info.StopWords;
import extract_info.WordCount;

public class Link_Classifier {

	private static String[] domain_name = { "Airfares", "Automobiles", "Books",
			"CarRentals", "Hotels", "Jobs", "Movies", "MusicRecords" };
	private int domain;

	private HashMap<String, Double> Link_Feature = new HashMap<String, Double>();

	// 本类完成的是对链接的打分
	public Link_Classifier(int domain) {
		this.domain = domain;
	}

	public void read_Link_Feature(int level, int domain)
			throws NumberFormatException, IOException {

		String filename = "central_vector/Link_Feature/" + domain_name[domain]
				+ "_link" + level + ".txt";

		FileReader fr1 = new FileReader(filename);
		BufferedReader br1 = new BufferedReader(fr1);
		String s_line = null;
		while ((s_line = br1.readLine()) != null) {

			String[] s_int = s_line.split(" ");
			double ii = Double.parseDouble(s_int[1]);
			Link_Feature.put(s_int[0], ii);
		}
		fr1.close();
		br1.close();
	}

	public double[] Get_Link_Score(String link) throws IOException {

		double link_score[] = new double[2];
        //第一维是打分分数；第二维是所属层次
		HashMap<String, Double> Instance = new HashMap<String, Double>();
		// 待检测链接的特征向量

		StopWords sw = new StopWords();
		String html = link;// 本链接的文本内容
		HashMap<String, Integer> wc = WordCount.count(html, sw);
		double sim = 0;
		double normolization = 0.0;// 归一化因子
		for (String key : wc.keySet()) {
			normolization += Math.pow(wc.get(key), 2);
		}
		normolization = Math.sqrt(normolization);
		for (String key : wc.keySet()) {
			Instance.put(key, wc.get(key) / normolization);
		}
		
		double norm = 0;
		double score = 0.0;
		for (int level = 1; level <= 3; ++level) {
            sim = 0;
			Link_Feature.clear();
			this.read_Link_Feature(level, domain);
			norm = 0;
			double norm1 = 0;
			double norm2 = 0;

			for (String key : Link_Feature.keySet()) {
				norm1 += Math.pow(Link_Feature.get(key), 2);
				if (Instance.containsKey(key)) {
					sim += Link_Feature.get(key) * Instance.get(key);
					norm2 += Math.pow(Instance.get(key), 2);
				}

			}
			norm = Math.sqrt(norm1 * norm2);
			if (norm == 0.0)
				sim = 0;
			else {
				sim /= norm;
			}
			if (sim >= score) {
				score = sim;
				link_score[0] = sim;
				link_score[1] = level;
			}
		//System.out.println("Level "+level+" Score is "+sim);

		}
      // System.out.println("Distance is "+link_score[1]);
		return link_score;// 返回链接的相似度
	}

}
