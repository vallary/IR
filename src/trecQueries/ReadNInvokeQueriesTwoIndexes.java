package trecQueries;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
//import org.apache.lucene.search.FieldValueFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeFilter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.knallgrau.utils.textcat.TextCategorizer;

public class ReadNInvokeQueriesTwoIndexes {
	public static void main(String[] args){
		try {
			//FSDirectory dir1 = FSDirectory.open(new File("C:/DATA/IR/IRProject/AdHoc/part3/index"));
			FSDirectory dir1 = FSDirectory.open(new File("AdHoc/part3/index"));
			IndexReader reader1 = IndexReader.open(dir1);
			IndexSearcher searcher1 = new IndexSearcher(reader1);
			FSDirectory dir2 = FSDirectory.open(new File("AdHoc/MetaIndex/FinalIndex/index"));
			//FSDirectory dir2 = FSDirectory.open(new File("C:/DATA/IR/IRProject/AdHoc/MetaIndex/FinalIndex/index"));
			IndexReader reader2 = IndexReader.open(dir2);
			IndexSearcher searcher2 = new IndexSearcher(reader2);
			FileInputStream topics = new FileInputStream("AdHoc/topics.mblog12.51-110");
			//FileInputStream topics = new FileInputStream("C:/DATA/IR/IRProject/AdHoc/topics.mblog12.51-110");
			//FileInputStream topics = new FileInputStream("C:/DATA/IRProject/AdHoc/topics.txt");
			DataInputStream t = new DataInputStream(topics);
			BufferedReader t1 = new BufferedReader(new InputStreamReader(t));
			EnglishAnalyzer analyzer = new EnglishAnalyzer(Version.LUCENE_36);
			QueryParser qp = new QueryParser(Version.LUCENE_36,"text", analyzer);
			//FileWriter f = new FileWriter("C:/DATA/IR/IRProject/AdHoc/AdHoc2Indexes_New_top100.top");
			FileWriter f = new FileWriter("AdHoc/AdHoc2Indexes_New_top100.top");
			BufferedWriter out= new BufferedWriter(f);
			String querynum = "";
			String queryInvoked = "";
			String strLine;
			int totalDocs = 0;
			// create a java calendar instance
			 Calendar calendar1 = Calendar.getInstance();

			 // get a java.util.Date from the calendar instance.
			 // this date will represent the current instant, or "now".
			 java.util.Date now1 = calendar1.getTime();

			 // a java current time (now) instance
			 java.sql.Timestamp currentTimestamp1 = new java.sql.Timestamp(now1.getTime());
			 System.out.println(currentTimestamp1);
			 //writer.close();
			 
			while ((strLine = t1.readLine()) != null) {
			if(strLine.contains("<num>"))
			{
			int ind_q = strLine.indexOf("M");
			int ind_q1 = strLine.indexOf("</num>");
			querynum = strLine.substring(ind_q,ind_q1);
			System.out.println(querynum);
			}
			if(strLine.contains("<query>"))
			{
			int ind_query = strLine.indexOf(" ", 7);
			int end_index_query = strLine.indexOf("</query>");
			queryInvoked = strLine.substring(ind_query,end_index_query);
			//System.out.println(queryInvoked);
			}
			if (strLine.contains("<querytweettime>"))
			{
			int ind = strLine.indexOf(" ",16);
			int end_index = strLine.indexOf("</querytweettime>");
			String tweetTime = strLine.substring(ind,end_index);
			tweetTime = tweetTime.trim();
			Long docLimit = Long.parseLong(tweetTime);
			Long lowerLimit = Long.parseLong("1");
			Filter rangeFilter = NumericRangeFilter.newLongRange("docno", lowerLimit, docLimit,true,true);
			//Query rangeQuery = NumericRangeQuery.newLongRange("docno",lowerLimit,docLimit,true,true);
			Query query = qp.parse(queryInvoked);
			//ScoreDoc[] scores = searcher.hits(strLine.substring(ind));
			//TopDocs results = searcher.search(query,1000);
			TopDocs firstResultSet = searcher1.search(query,rangeFilter,4000);
			//System.out.println(firstResultSet.totalHits);
			TopDocs resultSetFromMeta = searcher2.search(query, rangeFilter,100);
			for(int i=0;i<Math.min(4000,firstResultSet.totalHits);i++) 
			{
				//System.out.println(i);
				float score = 0.7f * firstResultSet.scoreDocs[i].score;
				//Integer docNo = scores[i].doc;
				Document doc = reader1.document(firstResultSet.scoreDocs[i].doc);
				String queryNumberFirstIndex = doc.get("docno");
				//System.out.println(queryNumberFirstIndex);
				Long docNoForFiltering = Long.parseLong(queryNumberFirstIndex);
				Filter docFilter = NumericRangeFilter.newLongRange("docno", docNoForFiltering, docNoForFiltering,true,true);
				TopDocs secondResultSet = searcher2.search(query,docFilter,1);
				if (secondResultSet.totalHits != 0)
				{
					Document tempDoc = reader2.document(secondResultSet.scoreDocs[0].doc);
					if (tempDoc.get("docno").equals(queryNumberFirstIndex))
					{
						//System.out.println(tempDoc.get("docno"));
						//System.out.println(tempDoc.get("text"));
						score = score + (0.3f * secondResultSet.scoreDocs[0].score);
					}
				}
				//@SuppressWarnings("deprecation")
				String category = "";
				TextCategorizer guesser = new TextCategorizer();
				category = guesser.categorize(doc.get("text"));
				if (category.equalsIgnoreCase("english"))
				{
					if (doc.get("tweetStatus").equals("200"))
					{
						NumericField docno = (NumericField)doc.getFieldable("docno");
						Long LdocNo = (Long)docno.getNumericValue();
						//String docno = doc.getField("docno").stringValue();
						out.write(querynum+"\t"+LdocNo+"\t"+score+"\t"+"AdHocRunTwoIndex"+"\n");
					}
				}
				
			} 
			for(int i=0;i<Math.min(100,resultSetFromMeta.totalHits);i++) 
			{
				//System.out.println(i);
				float score = 0.3f * resultSetFromMeta.scoreDocs[i].score;
				//Integer docNo = scores[i].doc;
				Document doc = reader2.document(resultSetFromMeta.scoreDocs[i].doc);
				String queryNumberFirstIndex = doc.get("docno");
				//System.out.println(queryNumberFirstIndex);
				Long docNoForFiltering = Long.parseLong(queryNumberFirstIndex);
				Filter docFilter = NumericRangeFilter.newLongRange("docno", docNoForFiltering, docNoForFiltering,true,true);
				TopDocs resultSetMatch = searcher1.search(query,docFilter,1);
				if (resultSetMatch.totalHits == 0)
				{
					if (doc.get("tweetStatus").equals("200"))
					{
						NumericField docno = (NumericField)doc.getFieldable("docno");
						Long LdocNo = (Long)docno.getNumericValue();
						//String docno = doc.getField("docno").stringValue();
						out.write(querynum+"\t"+LdocNo+"\t"+score+"\t"+"AdHocRunTwoIndex"+"\n");
					}
				}
				//@SuppressWarnings("deprecation")
				
			} 
			}
			//t1.close();
			//out.close();
			}
			// create a java calendar instance
			 Calendar calendar = Calendar.getInstance();

			 // get a java.util.Date from the calendar instance.
			 // this date will represent the current instant, or "now".
			 java.util.Date now = calendar.getTime();

			 // a java current time (now) instance
			 java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
			 System.out.println(currentTimestamp);
			 //writer.close();
			 System.out.println(totalDocs);
			 out.close();
			 t1.close();
			 
		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}