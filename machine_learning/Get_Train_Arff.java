package machine_learning;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.*;

public class Get_Train_Arff implements Serializable{

	private int[] attribute = new int[15];

	private void initial() {
		for (int i = 0; i < attribute.length; ++i)
			attribute[i] = 0;
	}

	public void get_train_data(String filename, String decision)
			throws IOException {

		FileReader rd = new FileReader(filename);
		BufferedReader reader = new BufferedReader(rd);

		FileWriter wr = new FileWriter("train_data/Train_data.arff", true);
		BufferedWriter writer = new BufferedWriter(wr);

		StringBuffer xml = new StringBuffer();
		String s = null;
		while ((s = reader.readLine()) != null) {
			xml.append(s);
		}
		Document doc1 = Jsoup.parse(xml.toString());
		Elements es = doc1.getElementsByTag("form");
		//System.out.println(decision + ":" + es.size());// 输出正反项数据的个数

		for (Element e : es) {

			Document doc2 = Jsoup.parse(e.toString());
			Elements es2 = doc2.getElementsByTag("input");

			for (Element ee : es2) {

				if (ee.attr("type").toString().toLowerCase().equals("email")
						|| Contain_Email(ee.attr("name").toString())
						|| Contain_Email(ee.attr("id").toString())) {
					attribute[13]++;
				} else if (ee.attr("type").toString().toLowerCase().equals(
						"password"))
					attribute[0]++;
				else if (ee.attr("type").toString().toLowerCase().equals(
						"radio"))
					attribute[1]++;
				else if (ee.attr("type").toString().toLowerCase().equals(
						"checkbox")) {
					attribute[2]++;
				} else if (ee.attr("type").toString().toLowerCase().equals(
						"text"))
					attribute[3]++;
				else if (ee.attr("type").toString().toLowerCase().equals(
						"submit")) {
					attribute[4]++;
					if (Contain_SE_Word(ee.attr("value").toString()
							.toLowerCase()))
						attribute[5]++;
				} else if (ee.attr("type").toString().toLowerCase().equals(
						"hidden"))
					attribute[6]++;
				else if (ee.attr("type").toString().toLowerCase().equals(
						"reset"))
					attribute[7]++;
				else if (ee.attr("type").toString().toLowerCase()
						.equals("file")) {
					attribute[8]++;

				} else if (ee.attr("type").toString().toLowerCase().equals(
						"image")) {
					if (Contain_go_search(ee.attr("src").toString())) {
						attribute[9]++;
					} else
						attribute[14]++;
				} else if (ee.attr("type").toString().toLowerCase().equals(
						"button"))
					attribute[12]++;
				else if (ee.attr("type").toString() == null)
					attribute[3]++;
			}
			Elements es6 = doc2.getElementsByTag("img");
			for (Element ee : es6) {
				if (Contain_go_search(ee.attr("src").toString())) {
					attribute[9]++;
				} else
					attribute[14]++;
			}

			Elements es3 = doc2.getElementsByTag("select");
			attribute[10] += es3.size();

			Elements es4 = doc2.getElementsByTag("textarea");
			attribute[11] += es4.size();

			Elements es5 = doc2.getElementsByTag("button");
			attribute[12] += es5.size();

			for (int i = 0; i < attribute.length; ++i) {
				writer.write(attribute[i] + ",");
				writer.flush();
			}
			writer.write(decision + "\r\n");
			writer.flush();
			initial();
		}

	}

	public void Train_data() throws IOException {
		FileWriter wr = new FileWriter("train_data/Train_data.arff");
		BufferedWriter writer = new BufferedWriter(wr);

		writer.write("@relation Train_Dataset" + "\r\n\n");
		writer.write("@attribute password numeric" + "\r\n");
		writer.write("@attribute radio numeric" + "\r\n");
		writer.write("@attribute checkbox numeric" + "\r\n");
		writer.write("@attribute text numeric" + "\r\n");
		writer.write("@attribute submit numeric" + "\r\n");
		writer.write("@attribute SE_Words numeric" + "\r\n");
		writer.write("@attribute hidden numeric" + "\r\n");
		writer.write("@attribute reset numeric" + "\r\n");
		writer.write("@attribute file numeric" + "\r\n");
		writer.write("@attribute img_go_search numeric" + "\r\n");
		writer.write("@attribute select numeric" + "\r\n");
		writer.write("@attribute textarea numeric" + "\r\n");
		writer.write("@attribute button numeric" + "\r\n");
		writer.write("@attribute email numeric" + "\r\n");
		writer.write("@attribute image numeric" + "\r\n");
		writer.write("@attribute searchable? {yes,no}" + "\r\n\n");
		writer.write("@data" + "\r\n\n");

		writer.flush();

	}

	private boolean Contain_SE_Word(String value) {

		Set<String> Se_words = new HashSet<String>();
		Se_words.add("google");
		Se_words.add("baidu");
		Se_words.add("yahoo");
		Se_words.add("altavista");
		Se_words.add("bing");
		Se_words.add("ask");
		Se_words.add("有道");
		Se_words.add("搜搜");
		Se_words.add("百度");
		Se_words.add("搜狗");
		for (String s : Se_words)
			if (value.contains(s))
				return true;
		return false;
	}

	private boolean Contain_Email(String value) {
		if (value.toLowerCase().contains("email"))
			return true;
		else
			return false;
	}

	private boolean Contain_go_search(String value) {
		String s = value.toLowerCase();
		HashSet<String> words = new HashSet<String>();
		String[] w = s.split("\\W");
		for (String ss : w) {
			words.add(ss);
		}
		if (words.contains("go") || words.contains("search"))
			return true;
		else
			return false;
	}

	public void create_train_arff() {
		try {
			String filename1 = "train_data/positive.txt";
			String filename2 = "train_data/negative.txt";
			this.Train_data();
			this.get_train_data(filename1, "yes");
			this.get_train_data(filename2, "no");
		} catch (Exception error) {
             error.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {

		Get_Train_Arff test = new Get_Train_Arff();
		test.create_train_arff();

	}

}
