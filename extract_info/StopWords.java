package extract_info;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class StopWords {
	private Set<String> dict = new HashSet<String>();
	public StopWords() throws IOException {
			FileReader input = new FileReader("dataset_uiuc/stopwords.dict");
			BufferedReader bufRead = new BufferedReader(input);
			String line = null;
			line = bufRead.readLine();
			while (line != null) {
				 dict.add(line);
				 line = bufRead.readLine();
			}
			input.close();
			bufRead.close();
	}
	
	public String[] removeStopwords(String[] wordset) {
		ArrayList<String> res = new ArrayList<String>();
		for(String s : wordset) {
			if(!dict.contains(s.toLowerCase())) {
				String[] st = s.split("[^a-zA-Z]");
				for(String t:st) {
					if(t.matches("[a-zA-Z]+")&&t.length()>=3) {
						res.add(t.toLowerCase());
					}
				}
			}
		}
		return res.toArray(new String[0]);
	}
}