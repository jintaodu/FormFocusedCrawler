package extract_info;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import web_crawler.Page;
import web_crawler.Link;
import web_crawler.Link_Classifier;

public class Extract_Links implements Serializable {

	/* 提取一个页面上的URL，并将相对URL转换为绝对URL */
	public Set<Link> extr_links(Page value, int domain, String langid)
			throws URISyntaxException, NumberFormatException, IOException {

		Set<Link> links = new HashSet<Link>();

		Page p = value;
		if (p.get_depth() <= 3 ) {
			String page_url = p.get_url().toString();
			URI base1 = new URI(page_url);
			String page_host = base1.getHost();// 得到本页面所在网站的主页地址

			int depth = p.get_depth();// 得到本页面及本页面上链接的深度。

			String html = p.get_html();
			Document doc = Jsoup.parse(html);
			Elements es = doc.select("a[href]");

			System.out.println("  page_host = " + page_host);
			System.out.println("  Extracting links......");

			for (Element e : es) {

				try {
					URI abs = base1.resolve(e.attr("href").toString());
					String abs_url = abs.toString();

					if (abs_url.length() != 0) {

						Link link = new Link(depth + 1);// 链接的层次数加一
						link.set_url(abs_url.toString());
						link.set_link(e.toString());// 设置anchor文本

						Link_Classifier LC = new Link_Classifier(domain);

						URI base2 = new URI(abs_url);
						String link_host = base2.getHost();

						if (link_host.equals(page_host)) {
							link.set_belong(true);
							double[] link_score = LC.Get_Link_Score(e
									.toString());
							link.set_score(0.7 * link_score[0] + 0.3
									* p.get_score());
							link.set_distance((int) link_score[1]);
						} else {
							link.set_belong(false);
							link.set_score(0);
							link.set_url("http://" + link_host);
						}

						links.add(link);
					}
				} catch (Exception error) {
					// System.out.println(error.toString());
				}
			}
			// System.out.println("links size=" + links.size());
		}
		return links;
	}

	public static void main(String[] args) throws URISyntaxException,
			MalformedURLException, UnsupportedEncodingException {
		// TODO Auto-generated method stub
		URI base1 = new URI("http://www.autonation.com");
		System.out
				.println(base1
						.resolve("/index.cfm?action=inventorysearch&subaction=searchdisplaynew"));
	}

}
