package machine_learning;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import extract_info.Extr_QI_Attr;
import weka.core.Instances;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import web_crawler.Form;

public class Form_identify implements Serializable{

	private static String[] domain_name = { "Airfares", "Automobiles", "Books",
			"CarRentals", "Hotels", "Jobs", "Movies", "MusicRecords" };
	private J48 tree = new J48();
	private Get_Train_Arff gta = new Get_Train_Arff();

	public Form_identify() {
		try {
			gta.create_train_arff();
			BufferedReader reader = new BufferedReader(new FileReader(
					"train_data/Train_data.arff"));
			Instances data = new Instances(reader);
			reader.close();
			// setting class attributes
			data.setClassIndex(data.numAttributes() - 1);// 设置决策项的下标
			String[] options = new String[1];// 训练决策树
			options[0] = "-U";
			J48 tree1 = new J48();
			tree1.setOptions(options);
			tree1.buildClassifier(data);
			/* Cross-validation */
			Evaluation eval = new Evaluation(data);
			eval.crossValidateModel(tree1, data, 10, new Random(1));

			this.tree = tree1;
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	public synchronized boolean form_identify(Form form, int domain)
			throws Exception {

		ArrayList<Integer> attribute = form.get_attribute();
		this.write_arff_file(attribute, domain);
		String unlabelfile = "train_data/" + domain_name[domain]
				+ "_unlabeled.arff";
		Instances unlabeled = new Instances(new BufferedReader(new FileReader(
				unlabelfile)));
		unlabeled.setClassIndex(unlabeled.numAttributes() - 1);

		// create copy
		Instances labeled = new Instances(unlabeled);

		double clsLabel = tree.classifyInstance(unlabeled.instance(0));
		labeled.instance(0).setClassValue(clsLabel);
		if (clsLabel == 0.0)// no:1.0 yes:0.0
		{
			// System.out.println("YES~~~");
			return true;
		} else {
			// System.out.println("NO~~~");
			return false;
		}
	}

	private void write_arff_file(ArrayList<Integer> attribute, int domain)
			throws IOException {
		String unlabelfile = "train_data/" + domain_name[domain]
				+ "_unlabeled.arff";

		FileWriter wr = new FileWriter(unlabelfile);
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

		for (int i = 0; i < attribute.size(); ++i)
			writer.write(attribute.get(i) + ",");
		writer.write("?");
		writer.flush();
		wr.close();
		writer.close();
	}

	public static void main(String[] args) throws Exception {

		FileReader fr = new FileReader("D://678.txt");
		BufferedReader br = new BufferedReader(fr);
		Form_identify qa = new Form_identify();
		Extr_QI_Attr qb = new Extr_QI_Attr();
		String line = null;
		StringBuffer sb = new StringBuffer();
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		Document doc = Jsoup.parse(sb.toString());
		Elements es = doc.getElementsByTag("form");
		for (Element e : es) {
			Form form = new Form();
			form.set_attribute(qb.Ext_Qi_Attr(e.toString()));
			form.set_html(e.toString());
			qa.form_identify(form, 2);
		}

	}

}
