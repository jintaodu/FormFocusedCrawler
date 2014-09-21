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

import machine_learning.RunLanguageId;

public class Page_download1 {
	
	private static RunLanguageId ri = new RunLanguageId();

	public String download1(String link) throws NumberFormatException,
			IOException {

		String webAddress = link;

		HttpURLConnection httpConn = null;
		try {
			URL url = new URL(webAddress);

			httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("GET");
			httpConn
					.setRequestProperty(
							"User-Agent",
							"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.8.1.14) Gecko/20080404 Firefox/2.0.0.14");
			InputStream in = httpConn.getInputStream();

			BufferedReader bf = new BufferedReader(new InputStreamReader(in));
			String result = null;
			StringBuffer sb = new StringBuffer("");
			while ((result = bf.readLine()) != null) {
				sb.append(result);
			}

			return sb.toString();

		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			if (httpConn != null)
				httpConn.disconnect();
			// System.out.println("释放此http连接~~~");
		}

		// System.out.println("此页面为空！");
		return null;

	}

	public static void main(String[] args) throws Exception {
		Page_download1 pd = new Page_download1();
		String html = pd.download1("http://www.bestjobs.hk/bt-jobd-johnny3c-200144.htm");
		System.out.println(html);
		Document doc = Jsoup.parse(html);
		Elements es = doc.getElementsByTag("title");
		Pattern p=Pattern.compile("[.,\"\\?!:']");//增加对应的标点

        Matcher m=p.matcher(es.text().toString());

        String first=m.replaceAll(""); //把英文标点符号替换成空，即去掉英文标点符号

			System.out.println(es.text().toString());
			System.out.println(first);
			ri.get_languageid("Best Jobs ");

	}

}
