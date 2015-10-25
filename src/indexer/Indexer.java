package indexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

import org.apache.lucene.analysis.Analyzer;
//import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
//import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

public class Indexer {
public static void main(String[] args)
{
	FSDirectory dir;
	IndexWriter writer = null;
	try {
		//dir = FSDirectory.open(new File("C:/DATA/IR/IRProject/AdHoc/part3/index"));
		dir = FSDirectory.open(new File("AdHoc/part3/index"));
	//File directory = new File("C:/DATA/IR/IRProject/corpus/");
	File directory = new File("corpus1");
	//File[] myarray;
	// create a java calendar instance
	Calendar calendar = Calendar.getInstance();

	// get a java.util.Date from the calendar instance.
	// this date will represent the current instant, or "now".
	java.util.Date now = calendar.getTime();

	// a java current time (now) instance
	java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
	System.out.println(currentTimestamp);
	//myarray=new File[10];
	File files[]=directory.listFiles();
	//System.out.println(myarray.length);
	Analyzer analyzer = new EnglishAnalyzer(Version.LUCENE_36);
	//Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
	IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36,analyzer);
	config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
	try {
		writer = new IndexWriter(dir,config);
		for(File f : files){
		       //File path=myarray[j];
		       Document doc;
		       try {
		    	   TrecDocIterator docs = new TrecDocIterator(f);
		       //indexer.TrecDocIterator docIterator = new TrecDocIterator(path);
		       while (docs.hasNext()) {
		    	   doc = docs.next();
		    	   if (doc != null && doc.get("text") != null)
		    	   	{
		    		   //if (!(doc.get("tweetStatus").equals("302")) && !(doc.get("text").equals("no")))
		    	   		//{
		    	   			writer.addDocument(doc);
		    	   		//}
		    	   	}
		    	   }
		       
		}
		       catch (FileNotFoundException e) {
					e.printStackTrace();
		}
		       
		  
			}
	} 
	
	catch (CorruptIndexException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (LockObtainFailedException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	}
	 catch (IOException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	}
	finally
	{
		 System.out.println("Closing the IndexWriter");
		// create a java calendar instance
		 Calendar calendar1 = Calendar.getInstance();

		 // get a java.util.Date from the calendar instance.
		 // this date will represent the current instant, or "now".
		 java.util.Date now1 = calendar1.getTime();

		 // a java current time (now) instance
		 java.sql.Timestamp currentTimestamp1 = new java.sql.Timestamp(now1.getTime());
		 System.out.println(currentTimestamp1);
		 try {
			writer.close();
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
}
}

