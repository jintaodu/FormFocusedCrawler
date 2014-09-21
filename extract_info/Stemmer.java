package extract_info;
import java.util.List;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.morph.WordnetStemmer;

public class Stemmer
{
	public static String[] filter(String[] words) throws MalformedURLException {
	   ArrayList<String> res = new ArrayList<String>();
	   String wnhome = System.getenv("WNHOME");
	   String path = wnhome + File.separator + "dict"; 
	   URL url = new URL("file", null, path); 
	   IDictionary dict = new Dictionary(url);
	   dict.open();
	   WordnetStemmer ws = new WordnetStemmer(dict);
	   for(String w : words) {
		   List<String> ans = ws.findStems(w);
		   if(ans.size()==0) res.add(w);
		   else if(ans.size()>0&&ans.get(0).matches("[a-zA-Z]+")) {
			   res.add(ans.get(0));
		   }   
	   }
	   return res.toArray(new String[0]);
   }
}

