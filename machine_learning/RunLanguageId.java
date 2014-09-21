package machine_learning;

import com.aliasi.classify.BaseClassifier;
import com.aliasi.classify.Classification;

import com.aliasi.util.AbstractExternalizable;

import java.io.File;

public class RunLanguageId {

	public String get_languageid(String args)throws Exception
	{
		
		File modelFile = new File("train_data/langid-leipzig.classifier");
		//System.out.println("Reading classifier from " + modelFile + "\n");
		@SuppressWarnings("unchecked")
		// required for deserialization
		BaseClassifier<CharSequence> classifier = (BaseClassifier<CharSequence>) AbstractExternalizable
				.readObject(modelFile);
		
			//System.out.println("Input=" + sentences[i]);
			Classification classification = classifier.classify(args);
			System.out.println("  Language Id= "+classification.bestCategory().toString());
			// classification.bestCategory();
			return classification.bestCategory().toString();
	
	}

	public static void main(String[] args) throws Exception {
		File modelFile = new File("train_data/langid-leipzig.classifier");
		System.out.println("Reading classifier from " + modelFile + "\n");
		@SuppressWarnings("unchecked")
		// required for deserialization
		BaseClassifier<CharSequence> classifier = (BaseClassifier<CharSequence>) AbstractExternalizable
				.readObject(modelFile);
		String[] sentences = { "book", "libri", "¤Ë¤Û¤ó¤´",
				"La Pr¨¦sidence de la R¨¦publique", "Guten Tag","Google identify language - Google Search" };
		for (int i = 0; i < sentences.length; ++i) {
			System.out.println("Input=" + sentences[i]);
			Classification classification = classifier.classify(sentences[i]);
			System.out.println(classification.bestCategory().toString());
			// classification.bestCategory();
		}
	}

}