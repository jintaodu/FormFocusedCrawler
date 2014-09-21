package extract_info;
import java.io.IOException;
import java.util.HashMap;


public class WordCount {
	private WordCount() {
	}
	public static HashMap<String,Integer> count(String sentence, StopWords sw) throws IOException {
		String[] wordset = sentence.split("\\s+");
		//¿Õ°×·û(¿Õ¸ñ¡¢tab¡¢»»ÐÐ¡¢»»Ò³ºÍ»Ø³µ·û)

		if(wordset != null) {
			wordset = sw.removeStopwords(wordset);
			if(wordset.length != 0)
			{
				wordset = Stemmer.filter(wordset);
				wordset = sw.removeStopwords(wordset);
			}
		}		
		HashMap<String,Integer> res = new HashMap<String,Integer>();
		for(String w : wordset) {
			if(w!=null && w.length() >=3){
			if(!res.containsKey(w)) {
				res.put(w, 1);
			}else {
				res.put(w, res.get(w)+1);
			}			
		}}
		return res;
	}
	
	public static void main(String[] args) throws IOException
	{

	}
}
