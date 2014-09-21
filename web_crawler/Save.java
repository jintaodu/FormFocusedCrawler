package web_crawler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Save implements Serializable{

	/**
	 * 
	 */
	private static String[] domain_name = { "Airfares", "Automobiles", "Books",
			"CarRentals", "Hotels", "Jobs", "Movies", "MusicRecords" };
	private HashSet<String> form_set = new HashSet<String>();

	public void reset() {
		form_set.clear();
	}

	public HashSet<String> get_form_set() {
		return this.form_set;
	}
	public void set_form_set(HashSet<String> value) {
		this.form_set = value;
	}
	public int get_distinct_form_num() {
		return form_set.size();
	}

	public void save_log(int domain,int all_searchable_forms,int all_visited_page) {
		// 保存每个领域的日志
		String filename = "save/" + domain_name[domain] + "/"
				+ domain_name[domain] + ".txt";
		/*
		 * Date date = new Date(); SimpleDateFormat date_format = new
		 * SimpleDateFormat( "yyyy-MM-dd  kk:mm:ss ");// 其中yyyy-MM-dd是你要表示的格式 //
		 * 可以任意组合
		 * ，不限个数和次序；具体表示为：MM-month,dd-day,yyyy-year;kk-hour,mm-minute,ss-second;
		 * String data_str = date_format.format(date);
		 */

		try {

			FileWriter fw = new FileWriter(filename, true);// 以追加形式写文件
			BufferedWriter bw = new BufferedWriter(fw);
			// bw.write("================================================="+"\r\n");
			bw.write(all_searchable_forms + " " + all_visited_page + "\r\n");
			bw.flush();
			fw.close();
			bw.close();
		} catch (Exception error) {

		}
	}

	public boolean save_form(Form form, int domain) throws IOException {
		String filename = "save/" + domain_name[domain] + "/"
				+ domain_name[domain] + ".html";

		Date date = new Date();
		SimpleDateFormat date_format = new SimpleDateFormat(
				"yyyy-MM-dd  kk:mm:ss ");// 其中yyyy-MM-dd是你要表示的格式
		// 可以任意组合，不限个数和次序；具体表示为：MM-month,dd-day,yyyy-year;kk-hour,mm-minute,ss-second;
		String data_str = date_format.format(date);

		Document doc = Jsoup.parse(form.get_html());
		Elements es = doc.getElementsByTag("form");
		// 通过action判断接口是否重复
		if (!form_set.contains(es.attr("action"))) {
			FileWriter fw = new FileWriter(filename, true);// 以追加形式写文件
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("<p><b>The URL Of this Query Interface：</b></p><p>"
					+ form.get_url() + "</p>");
			bw.write("<p>Date is " + data_str + "</p>");
			bw
					.write("<p><b>The Start Of this Query Interface Appearance!</b></p>");
			bw.write(form.get_html());

			bw
					.write("<p><b>The End Of this Query Interface Appearance!</b></p>");
			bw.flush();
			bw.close();
			fw.close();
			form_set.add(es.attr("action"));
			return true;
		} else {
			return false;
		}
	}
}
