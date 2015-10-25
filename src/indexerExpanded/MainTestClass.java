package indexerExpanded;

//import indexer.TrecDocIterator;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.lucene.document.Document;

public class MainTestClass {
	
	public static void main(String[] args)
	{
		System.setProperty("wordnet.database.dir", "C:/Program Files (x86)/WordNet/3.0/dict");
		//File directory = new File("C:/DATA/IR/IRProject/corpus1/");
		File directory = new File("corpus1");
		File files[]=directory.listFiles();
		for(File f : files){
			Document doc;
		       try {
		    	   TrecDocIteratorNWordClassification docs = new TrecDocIteratorNWordClassification(f);
		       //indexer.TrecDocIterator docIterator = new TrecDocIterator(path);
		       while (docs.hasNext()) {
		    	   doc = docs.next();
		    	   if (doc != null && doc.get("text") != null)
		    	   	{
		    		   
		    	   	}
		    	   }
		}
		       catch (FileNotFoundException e) {
					e.printStackTrace();
		}
		}
	}

}
