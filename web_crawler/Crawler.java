package web_crawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import extract_info.Page_download;
import extract_info.Extract_Links;
import extract_info.Extract_forms;

public class Crawler extends Thread {
	private static String[] domain_name = { "Airfares", "Automobiles", "Books",
			"CarRentals", "Hotels", "Jobs", "Movies", "MusicRecords" };

	private Page_download Pd = new Page_download();
	private Extract_Links EL = new Extract_Links();

	private HashSet<Link> Link_Queue1 = new HashSet<Link>();// 站内链接队列-1
	private HashSet<Link> Link_Queue2 = new HashSet<Link>();// 站内链接队列-2
	private HashSet<Link> Link_Queue3 = new HashSet<Link>();// 站内链接队列-3
	private HashSet<Page> Page_Queue = new HashSet<Page>();// 页面队列
	private Set<String> visited_insite_link = new HashSet<String>();// 记录站内访问过的链接
	private Queue<Link> site_Queue = new LinkedList<Link>();// 站点级队列
	private HashSet<String> visited_site = new HashSet<String>();// 记录访问过的网站
	private int visited_page_num;// number of visited pages in site
	private int all_visited_page;// number of visited pages in all sites
	private int all_searchable_forms;// number of all distinct searchable forms
	// in all sites
	private Domain_Form_Classifier dsfc = new Domain_Form_Classifier();
	private Extract_forms exform = new Extract_forms();
	private Save save = new Save();
	private int domain;// 设定当前爬虫的领域
	private boolean alive;// 查看当前线程是否存活
	private String langid;// 设置爬虫的自然语言类别
	private Link Current_link;

	public Crawler(int domain, String langid) {

		// 初始化爬虫各种状态
		this.all_visited_page = 0;
		this.all_searchable_forms = 0;
		this.alive = true;
		this.domain = domain;
		this.langid = langid;
		try {
			String filename = "root/" + domain_name[domain] + ".txt";

			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			boolean first_crawling_or_not = Boolean.parseBoolean(line.trim());
			if (first_crawling_or_not == true) {
				while ((line = br.readLine()) != null) {
					// System.out.println(line);
					this.set_root(line);// 设置爬虫种子URL
				}
				RandomAccessFile rf = new RandomAccessFile(filename, "rw");
				rf.seek(0);
				rf.writeBytes("false");
				rf.close();

			} else {
				this.read_crawl_state();
			}
			fr.close();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public int get_all_visited_page() {
		return all_visited_page;
	}

	public int get_all_searchabl_forms() {
		return all_searchable_forms;
	}

	public synchronized void reset() {
		visited_page_num = 0;
		Page_Queue.clear();
		Link_Queue1.clear();
		Link_Queue2.clear();
		Link_Queue3.clear();
		visited_insite_link.clear();
	}

	public synchronized Link get_next_link() {
		// 从多级队列中抽取超链接
		Link next_link = null;
		if (Link_Queue1.size() != 0) {
			next_link = this.get_multi_link(Link_Queue1);
			while (next_link != null
					&& this.visited_insite_link.contains(next_link.get_url())) {
				next_link = this.get_multi_link(Link_Queue1);
				Link_Queue1.remove(next_link);
			}
			Link_Queue1.remove(next_link);
		} else if (Link_Queue2.size() != 0) {
			next_link = this.get_multi_link(Link_Queue2);
			while (next_link != null
					&& this.visited_insite_link.contains(next_link.get_url())) {
				next_link = this.get_multi_link(Link_Queue2);
				Link_Queue2.remove(next_link);
			}
			Link_Queue2.remove(next_link);
		} else if (Link_Queue3.size() != 0) {
			next_link = this.get_multi_link(Link_Queue3);
			while (next_link != null
					&& this.visited_insite_link.contains(next_link.get_url())) {
				next_link = this.get_multi_link(Link_Queue3);
				Link_Queue3.remove(next_link);
			}
			Link_Queue3.remove(next_link);
		}

		return next_link;

	}

	private Link get_multi_link(HashSet<Link> Link_Queue) {
		double max_score = 0;
		Link next_link = null;
		for (Link link : Link_Queue)
			if (link.get_score() >= max_score) {
				next_link = link;
				max_score = link.get_score();
			}

		return next_link;
	}

	private boolean link_queue_empty() {
		if (Link_Queue1.size() == 0 && Link_Queue2.size() == 0
				&& Link_Queue3.size() == 0)
			return true;
		else
			return false;
	}

	public synchronized Page get_next_page() {
		double max_score = 0;
		Page next_page = null;
		for (Page page : Page_Queue)
			if (page.get_score() >= max_score) {
				next_page = page;
				max_score = page.get_score();
			}
		Page_Queue.remove(next_page);
		return next_page;
	}

	public synchronized boolean set_root(String start_url) {
		// 设置爬虫种子URL
		if (!visited_site.contains(start_url) && start_url.length() != 0) {
			this.reset();
			Link link = new Link(0);
			link.set_belong(true);
			link.set_link(null);
			link.set_score(0);
			link.set_url(start_url);
			Link_Queue3.add(link);// 将首页URL插入到第三级链接队列中
			visited_site.add(start_url);
			return true;
		} else
			return false;
	}

	public synchronized void set_next_site() throws NumberFormatException,
			IOException {

		save.reset();

		Link next_site = site_Queue.poll();
		String new_site_url = new String();

		if (next_site != null) {
			new_site_url = next_site.get_url();
			while (!this.set_root(new_site_url)) {
				next_site = site_Queue.poll();
				if (next_site != null) {
					new_site_url = next_site.get_url();
				}
			}
			System.out
					.println("************Notice:@-> NEW SITE!**************");
			System.out.println("New Site homepage URL:" + new_site_url);
		} else {
			this.alive = false;
			System.out.println("Oops!The site queue is empty!");
		}
	}

	public synchronized boolean stop_criteria(int stop1, int stop2) {
		// 判断是否满足停止条件
		if (this.link_queue_empty()) {
			System.out.println("Arrive at Stop Criteria-1!");
			return true;//满足第一个停止条件
		} else if (stop1 >= 5) {
			System.out.println("Arrive at Stop Criteria-2!");
			return true;//满足第二个停止条件
		} else if (stop2 > 100) {
			System.out.println("Arrive at Stop Criteria-3!");
			return true;//满足第三个停止条件
		} else
			return false;
	}

	public synchronized void run() {
		while (this.alive) {
			this.crawling();
			System.gc();
		}
		System.out.println("Oops! Now The Crawler Shutdown ! \n"
				+ " You Can Relaunch the Crawler~");
	}

	public synchronized void crawling() {
		// 爬虫周期函数
		try {
			// 查看是否满足停止条件
			int stop1 = save.get_distinct_form_num();
			int stop2 = this.visited_page_num;
			if (!stop_criteria(stop1, stop2)) {
				Link link = this.get_next_link();
				while ((link == null || link.get_url() == null)
						&& !this.link_queue_empty()) {
					link = this.get_next_link();
				}
				Current_link = link;
				if (link != null) {
					visited_insite_link.add(link.get_url());
					System.out.println("\nNow @ -> ：" + link.get_url()
							+ "\n At distance:" + link.get_distance()
							+ "\n At level:" + link.get_depth()
							+ "\n  This Link Score is :" + link.get_score());

					Page p = Pd.download(link, this.domain, this.langid);

					if (p != null && p.get_html() != null) {
						Page_Queue.add(p);

						System.out.println("  Page_Queue size = "
								+ Page_Queue.size());
						Page page = this.get_next_page();
						System.out.println("  This Page Score is :"
								+ page.get_score());

						Set<Form> forms = exform.extr_forms(page, this.domain);

						if (forms != null && forms.size() != 0) {
							for (Form form : forms)
								if (form.get_belong() == true
										&& save.save_form(form, this.domain)) {
									all_searchable_forms++;
								}
						}

						Set<Link> extr_link = EL.extr_links(page, this.domain,
								this.langid);
						if (extr_link != null) {
							for (Link link1 : extr_link)
								if (link1.get_belong() == true) {
									if (link1.get_distance() == 1)
										Link_Queue1.add(link1);
									else if (link1.get_distance() == 2)
										Link_Queue2.add(link1);
									else if (link1.get_distance() == 3)
										Link_Queue3.add(link1);
								} else {
									link1.set_belong(true);
									site_Queue.offer(link1);
								}
						}
						all_visited_page++;// all visited pages in all sites
						visited_page_num++;// number of visited pages in a site

					}
					this.save_crawl_state();

				}
				System.out.println("!!! Report of this Crawling Circle: !!!\n"
						+ "\tnumber of visited pages in site:"
						+ this.visited_page_num
						+ "\n\tnumber of distinct searchable forms in site:"
						+ save.get_distinct_form_num());
				System.out.println("\tpage_Queue size= " + Page_Queue.size()
						+ "\n\tLink_Queue1 size= " + Link_Queue1.size()
						+ "\n\tLink_Queue2 size= " + Link_Queue2.size()
						+ "\n\tLink_Queue3 size= " + Link_Queue3.size()
						+ "\n\tsite_Queue size= " + site_Queue.size());
				save.save_log(domain, this.all_searchable_forms,
						this.all_visited_page);

			} else {
				this.set_next_site();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void read_crawl_state() {
		try {
			String filepath = "interrupted_point/" + domain_name[domain] + "/";

			FileReader fr = new FileReader(filepath + "crawler_state1.txt");
			BufferedReader br = new BufferedReader(fr);
			this.visited_page_num = Integer.parseInt(br.readLine());
			this.all_visited_page = Integer.parseInt(br.readLine());
			this.all_searchable_forms = Integer.parseInt(br.readLine());

			fr.close();
			br.close();
			// =============================================================
			FileInputStream in = new FileInputStream(filepath
					+ "crawler_state2.tmp");
			ObjectInputStream ois = new ObjectInputStream(in);
			this.Link_Queue1 = (HashSet<Link>) ois.readObject();
			this.Link_Queue2 = (HashSet<Link>) ois.readObject();
			this.Link_Queue3 = (HashSet<Link>) ois.readObject();
			this.Page_Queue = (HashSet<Page>) ois.readObject();
			this.site_Queue = (Queue<Link>) ois.readObject();
			this.visited_site = (HashSet<String>) ois.readObject();
			this.visited_insite_link = ((Set<String>) ois.readObject());
			save.set_form_set((HashSet<String>) ois.readObject());

			in.close();
			ois.close();

		} catch (Exception error) {
			error.printStackTrace();
		}

	}

	private void save_crawl_state() {
		try {

			if (Current_link.get_distance() == 1)
				this.Link_Queue1.add(Current_link);
			else if (Current_link.get_distance() == 2)
				this.Link_Queue2.add(Current_link);
			else if (Current_link.get_distance() == 3)
				this.Link_Queue3.add(Current_link);

			String filepath = "interrupted_point/" + domain_name[domain] + "/";
			FileWriter fw = new FileWriter(filepath + "crawler_state1.txt");
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(visited_page_num + "\r\n");
			bw.write(all_visited_page + "\r\n");
			bw.write(all_searchable_forms + "\r\n");
			bw.flush();
			fw.close();
			bw.close();
			// =============================================================
			FileOutputStream ostream = new FileOutputStream(filepath
					+ "crawler_state2.tmp");
			ObjectOutputStream oos = new ObjectOutputStream(ostream);

			oos.writeObject(Link_Queue1);
			oos.writeObject(Link_Queue2);
			oos.writeObject(Link_Queue3);
			oos.writeObject(Page_Queue);
			oos.writeObject(site_Queue);
			oos.writeObject(visited_site);
			oos.writeObject(visited_insite_link);
			oos.writeObject(save.get_form_set());
			oos.flush();
			ostream.close();
			oos.close();
			if (Current_link.get_distance() == 1)
				this.Link_Queue1.remove(Current_link);
			else if (Current_link.get_distance() == 2)
				this.Link_Queue2.remove(Current_link);
			else if (Current_link.get_distance() == 3)
				this.Link_Queue3.remove(Current_link);

		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Crawler crawler = new Crawler(1, "en");
		try {
			crawler.run();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
