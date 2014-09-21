package web_crawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import extract_info.StopWords;
import extract_info.WordCount;

public class Domain_Form_Classifier {
	/*
	 * 对每一个查询接口进行领域分类
	 */
	private static String[] domain_name = { "Airfares", "Automobiles", "Books",
			"CarRentals", "Hotels", "Jobs", "Movies", "MusicRecords" };
	private HashMap<String, Double> Form_Feature = new HashMap<String, Double>();
	//private int[] Domain_tmp = { 0, 1, 2, 5 };
	private int[] Domain_tmp = { 0, 1, 2, 3,4,5,6,7 };
	public boolean DSFC(String form_html, int domain) {

		int judge_domain = domain;
		try {
			HashMap<String, Double> Instance = this.get_form_vs(form_html);
			double norm = 0;
			double score = 0.0;
			double sim = 0.0;

			for (int i = 0; i < Domain_tmp.length; ++i) {

				this.read_Form_Feature(i);// 读取每个领域查询接口的特征向量

				sim = 0;

				for (String key : Form_Feature.keySet()) {
					if (Instance.containsKey(key)) {
						sim += Form_Feature.get(key) * Instance.get(key);
					}
				}
				if (sim > score) {
					score = sim;
					judge_domain = Domain_tmp[i];
				}
				//System.out.println(domain_name[Domain_tmp[i]] + " " + sim);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println("Judge_domain = " + judge_domain);
		if (judge_domain == domain)
			return true;
		else
			{System.out.println("judge domain="+domain_name[judge_domain]);
			return false;}

	}

	private void read_Form_Feature(int domain) {

		try {
			Form_Feature.clear();
			String fp = "central_vector/Domain_Form_Feature/";
			FileReader fr = new FileReader(fp + domain_name[Domain_tmp[domain]]
					+ "_tfidf.txt");
			BufferedReader br = new BufferedReader(fr);
			String s_line = null;
			while ((s_line = br.readLine()) != null) {

				String[] s_int = s_line.split(" ");
				double ii = Double.parseDouble(s_int[1]);
				Form_Feature.put(s_int[0], ii);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public HashMap<String, Double> get_form_vs(String form_html)
			throws IOException {
		// 得到新来查询接口的特征向量
		HashMap<String, Integer> Form_origin = new HashMap<String, Integer>();
		HashMap<String, Double> Instance = new HashMap<String, Double>();
		try {
			StopWords sw = new StopWords();
			Form_origin = WordCount.count(form_html, sw);

			double normolization = 0.0;
			for (String key : Form_origin.keySet()) {
				normolization += Math.pow(Form_origin.get(key), 2);
			}
			normolization = Math.sqrt(normolization);
			for (String key : Form_origin.keySet()) {
				Instance.put(key, Form_origin.get(key) / normolization);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Instance;

	}

	public static void main(String[] args) throws IOException {
		Domain_Form_Classifier dsfc = new Domain_Form_Classifier();
		int[] Domain_tmp = { 0, 1, 2, 3,4,5,6,7 };
		int num = 0;
		int judge_right = 0;
		for (int i = 0; i < Domain_tmp.length; ++i) {
			String fp = "D:\\Form_Domian_dataset\\"
					+ domain_name[Domain_tmp[i]] + ".html";
			int num_domain = 0;
			int judge_right_domain = 0;
			System.out.println(domain_name[Domain_tmp[i]]+"  result is :");
			FileReader fr = new FileReader(fp);
			BufferedReader br = new BufferedReader(fr);
			String line;
			StringBuffer sb = new StringBuffer("");
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			Document doc = Jsoup.parse(sb.toString());
			Elements es = doc.getElementsByTag("form");
			for (Element e : es) {
				num ++;num_domain++;
				boolean tmp = dsfc.DSFC(e.toString(), Domain_tmp[i]);
				System.out.println(tmp);
				if(tmp == true) {judge_right++;judge_right_domain++;}
			}
		System.out.println("this domain size is "+num_domain);
		System.out.println("this domain precision is:"+(double)judge_right_domain/num_domain);
		}
		System.out.println("test dataset size:"+num);
		System.out.println("precision is:"+(double)judge_right/num);



	}
}
