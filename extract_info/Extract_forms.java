package extract_info;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import machine_learning.Form_identify;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.*;

import web_crawler.Form;
import web_crawler.Page;

public class Extract_forms implements Serializable{

	private static String[] domain_name = { "Airfares", "Automobiles", "Books",
			"CarRentals", "Hotels", "Jobs", "Movies", "MusicRecords" };
	private Extr_QI_Attr eqa = new Extr_QI_Attr();
	private Form_identify fi = new Form_identify();

	public Set<Form> extr_forms(Page p, int domain) throws Exception {

		System.out.println("  Extract Forms ....");

		String html = p.get_html();
		String url = p.get_url();
		Set<Form> forms = new HashSet<Form>();

		Document doc1 = Jsoup.parse(html);
		Elements es = doc1.getElementsByTag("form");

		for (Element e : es) {
			
				Form form = new Form();
				form.set_url(url);
				form.set_html(e.toString());
				form.set_attribute(eqa.Ext_Qi_Attr(e.toString()));
				form.set_belong(fi.form_identify(form, domain));
				forms.add(form);

		}
		for (Form form : forms) {
			if (form.get_belong() == true)
				return forms;// 当本页面有可查询表单是才将本页面所有表单返回
		}
		return null;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
