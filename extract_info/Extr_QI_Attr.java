package extract_info;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.*;

public class Extr_QI_Attr implements Serializable {
	// 抽取接口属性的类

	private int[] attribute = new int[15];

	// private BrowserAgent ba = new BrowserAgent();

	public void initial() {
		for (int i = 0; i < attribute.length; ++i)
			attribute[i] = 0;
	}

	// 抽取接口属性的方法,返回每个接口的html代码和属性数组
	public ArrayList<Integer> Ext_Qi_Attr(String form_html) throws IOException {

		Document doc = Jsoup.parse(form_html);
		Elements es2 = doc.getElementsByTag("input");

		for (Element ee : es2) {

			if (ee.attr("type").toString().toLowerCase().equals("email")
					|| Contain_Email(ee.attr("name").toString())
					|| Contain_Email(ee.attr("id").toString())) {
				attribute[13]++;
			} else if (ee.attr("type").toString().toLowerCase().equals(
					"password"))
				attribute[0]++;
			else if (ee.attr("type").toString().toLowerCase().equals("radio"))
				attribute[1]++;
			else if (ee.attr("type").toString().toLowerCase()
					.equals("checkbox")) {
				attribute[2]++;
			} else if (ee.attr("type").toString().toLowerCase().equals("text"))
				attribute[3]++;
			else if (ee.attr("type").toString().toLowerCase().equals("submit")) {
				attribute[4]++;
				if (Contain_SE_Word(ee.attr("value").toString().toLowerCase()))
					attribute[5]++;
			} else if (ee.attr("type").toString().toLowerCase()
					.equals("hidden"))
				attribute[6]++;
			else if (ee.attr("type").toString().toLowerCase().equals("reset"))
				attribute[7]++;
			else if (ee.attr("type").toString().toLowerCase().equals("file")) {
				attribute[8]++;

			} else if (ee.attr("type").toString().toLowerCase().equals("image")) {
				if (Contain_go_search(ee.attr("src").toString())) {
					attribute[9]++;
				} else
					attribute[14]++;
			} else if (ee.attr("type").toString().toLowerCase()
					.equals("button"))
				attribute[12]++;
			else if (ee.attr("type").toString() == null)
				attribute[3]++;
		}
		Elements es6 = doc.getElementsByTag("img");
		for (Element ee : es6) {
			if (Contain_go_search(ee.attr("src").toString())) {
				attribute[9]++;
			} else
				attribute[14]++;
		}

		Elements es3 = doc.getElementsByTag("select");
			attribute[10]+=es3.size();

		Elements es4 = doc.getElementsByTag("textarea");
			attribute[11]+= es4.size();
			
		Elements es5 = doc.getElementsByTag("button");
			attribute[12]+=es5.size();
			
		ArrayList<Integer> Attr = new ArrayList<Integer>();
		for (int i : attribute)
			Attr.add(i);
		initial();
		return Attr;
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
	public static void main(String[] args) throws IOException
	{
		FileReader fr = new FileReader("D://456.txt");
		BufferedReader br = new BufferedReader(fr);
		Extr_QI_Attr qa = new Extr_QI_Attr();
		String line = null;StringBuffer sb = new StringBuffer();
		while((line = br.readLine())!=null)
		{
			sb.append(line);
		}
		Document doc = Jsoup.parse(sb.toString());
		Elements es = doc.getElementsByTag("form");
		for(Element e : es)
		{
			
			System.out.println(qa.Ext_Qi_Attr(e.toString()));
		}
	}

}
