package extract_info;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import web_crawler.Page;
import web_crawler.Link;
import web_crawler.Page_Classifier;
import machine_learning.RunLanguageId;

public class Page_download {

	private RunLanguageId ri = new RunLanguageId();

	public Page download(Link link, int domain, String lang_id)
			throws NumberFormatException, IOException {

		String webAddress = link.get_url();

		HttpURLConnection httpConn = null;
		try {
			URL url = new URL(webAddress);

			httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("GET");
			httpConn
					.setRequestProperty(
							"User-Agent",
							"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.8.1.14) Gecko/20080404 Firefox/2.0.0.14");
			httpConn.setConnectTimeout(10000);// 设置连接主机超时（单位：毫秒）
			httpConn.setReadTimeout(10000);// 置从主机读取数据超时（单位：毫秒）
			InputStream in = httpConn.getInputStream();

			BufferedReader bf = new BufferedReader(new InputStreamReader(in));
			String result = null;
			StringBuffer sb = new StringBuffer("");
			while ((result = bf.readLine()) != null) {
				sb.append(result);
			}

			Document doc = Jsoup.parse(sb.toString());
			Elements es = doc.getElementsByTag("title");
			String title_txt = this.remove_punctuation(es.text().toString());
			String langid = ri.get_languageid(title_txt);

			if (lang_id.equals(langid)) {

				Page page = new Page(link.get_depth());
				page.set_langid(langid);
				page.set_html(sb.toString());
				page.set_url(link.get_url());

				Page_Classifier PC = new Page_Classifier(domain);
				double score = PC.Get_Page_Score(page.get_html());
				page.set_score(0.7 * score + 0.3 * link.get_score());
				// 页面分类中用到的二步法：
				if (score >= 0.15) {
					System.out.println("  The Web Page download Success!");
					return page;
				} else {
					double max = 0.0;
					int belong_domain = 0;
					for (int i = 0; i <= 7; ++i) {
						Page_Classifier pclassify = new Page_Classifier(i);
						double s = pclassify.Get_Page_Score(page.get_html());
						if (s >= max) {
							max = s;
							belong_domain = i;
						}
					}
					if (belong_domain == domain) {
						System.out.println("  The Web Page download Success!");
						return page;
					} else
						return null;
				}
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			if (httpConn != null)
				httpConn.disconnect();
		}

		System.out.println("  The Web Page download fail!");
		return null;

	}

	private String remove_punctuation(String title) {
		// 去除title中的标点符号
		Pattern p = Pattern.compile("[.,\"\\?!:']");// 增加对应的标点
		Matcher m = p.matcher(title);

		return m.replaceAll(""); // 把英文标点符号替换成空，即去掉英文标点符号
	}

	public static void main(String[] args) throws MalformedURLException {

	}

}
