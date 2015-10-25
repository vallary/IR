package indexerExpanded;

//import indexer.TrecDocIterator;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

public class IndexerForMeta {
	public static String[] arrayOfFileNumbers = new String[100000];
	public static void initializeArray()
	{
		try {
			//FileInputStream fileInputStream = new FileInputStream("C:/DATA/IR/IRProject/AdHoc/MetaIndex/fileNos.txt");
			FileInputStream fileInputStream = new FileInputStream("AdHoc/MetaIndex/fileNos.txt");
			DataInputStream data = new DataInputStream(fileInputStream);
			BufferedReader reader = new BufferedReader(new InputStreamReader(data));
			String strLine;
			
			int counter = 0;
			while ((strLine = reader.readLine()) != null)
			{
				arrayOfFileNumbers[counter] = strLine.trim();
				counter++;
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String args[])
	{
		initializeArray();
		FSDirectory dir;
		IndexWriter writer = null;
		try {
			//dir = FSDirectory.open(new File("C:/DATA/IR/IRProject/AdHoc/MetaIndex/FinalIndex/index"));
			dir = FSDirectory.open(new File("AdHoc/MetaIndex/FinalIndex/index"));
			System.setProperty("wordnet.database.dir", "C:/Program Files (x86)/WordNet/3.0/dict");
			//File directory = new File("C:/DATA/IR/IRProject/corpus/");
			File directory = new File("corpus1");
			Calendar calendar = Calendar.getInstance();

			// get a java.util.Date from the calendar instance.
			// this date will represent the current instant, or "now".
			java.util.Date now = calendar.getTime();

			// a java current time (now) instance
			java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
			System.out.println(currentTimestamp);
			File files[]=directory.listFiles();
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
				    	   TrecDocIteratorNWordClassification docs = new TrecDocIteratorNWordClassification(f);
				       //indexer.TrecDocIterator docIterator = new TrecDocIterator(path);
				       while (docs.hasNext()) {
				    	   doc = docs.next();
				    	   if (doc != null && doc.get("text") != null)
				    	   	{
				    		 if (!(doc.get("text").equals("no")))
				    	   		{
				    	   			writer.addDocument(doc);
				    	   		}
				    	   		
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
