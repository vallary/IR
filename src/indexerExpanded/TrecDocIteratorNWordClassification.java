/*
 * Trec Document Iterator for document expansion and classification
 */
package indexerExpanded;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.knallgrau.utils.textcat.TextCategorizer;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;


public class TrecDocIteratorNWordClassification implements Iterator<Document> {
	protected BufferedReader rdr;
	protected boolean at_eof = false;
	protected WordNetDatabase database = WordNetDatabase.getFileInstance();
	public TrecDocIteratorNWordClassification(File file) throws FileNotFoundException {
		rdr = new BufferedReader(new FileReader(file));
		System.out.println("Reading " + file.toString());		
	}

	@Override
	public boolean hasNext() {
		return !at_eof;
	}

	@Override
	public Document next() {
		Document doc = new Document();
		StringBuffer sb = new StringBuffer();
		try {
			String line;
			Pattern docno_tag = Pattern.compile("<DOCNO>(.+?)</DOCNO>");
			//Pattern text_tag = Pattern.compile("<TEXT>\\s*(\\S+)\\s*<");
			Pattern text_tag = Pattern.compile("<TEXT>(.+?)</TEXT>");
			Pattern title_tag = Pattern.compile("<TITLE>(.+?)</TITLE>");
			Pattern tweetStatus_tag = Pattern.compile("<TweetStatus>(.+?)</TweetStatus>");
			Pattern tweetTime_tag = Pattern.compile("<Tweettime>(.+?)</Tweettime>");
			boolean in_doc = false;
			while (true) {
				line = rdr.readLine();
				if (line == null) {
					at_eof = true;
					break;
				}
				if (!in_doc) {
					if (line.startsWith("<DOC>"))
						in_doc = true;
					else
						continue;
				}
				if (line.startsWith("</DOC>")) {
					in_doc = false;
					sb.append(line);
					break;
				}

				Matcher m = docno_tag.matcher(line);
				if (m.find()) {
					String docno = m.group(1);
					Long docNo = Long.parseLong(docno);
					//System.out.println(docNo.toString());
					if (matching(docNo.toString()))
					{
						doc.add(new NumericField("docno",Field.Store.YES,true).setLongValue(docNo));
					}
					else
					{
						doc = null;
						return doc;
					}
					//doc.add(new Field("docno", docno, Field.Store.YES,Field.Index.ANALYZED));
				}
				Matcher m1 = text_tag.matcher(line);
				if (m1.find())
				{
					String text = m1.group(1);
					String category = "";
					TextCategorizer guesser = new TextCategorizer();
					category = guesser.categorize(text);
					String finalString = "";
					String[] arrString = new String[100000];
					int counter = 0;
					//System.out.println(text);
					//System.out.println(category.equalsIgnoreCase("english"));
					if (category.equalsIgnoreCase("english"))
					{
						String[] tokenizedWords = text.split(" ");
						for (String str : tokenizedWords)
						{
							Synset[] synsets = database.getSynsets(str);
							if (synsets.length > 0)
							{
								for (Synset s : synsets)
								{
									for (String wordForm : s.getWordForms())
									{
										if (wordForm.contains(","))
										{
											String[] splitWordForm = wordForm.split(",");
											for (String sWordForm : splitWordForm)
											{
												if (sWordForm != null)
												{
													arrString[counter]= sWordForm;
													counter++;
												}
											}
										}
										else
										{
											if (wordForm != null)
											{
												arrString[counter] = wordForm;
												counter++;
											}
										}
									}
								}
							}
						}
						/*
						System.out.println(counter);
						for (String s: arrString)
						{
							if (s != null)
							{
							System.out.println(s);
							}
						}*/
						String[] duplicateExcluded = removeDuplicates(arrString);
						for (String s : duplicateExcluded)
						{
							if (s != null)
							{
								finalString = finalString + s + " ";
							}
						}
						//System.out.println(finalString);
						doc.add(new Field("text", finalString, Field.Store.YES,Field.Index.ANALYZED));
					}
					else
					{
						doc.add(new Field("text","no", Field.Store.NO,Field.Index.ANALYZED));	
					}
				}
				
				Matcher m2 = title_tag.matcher(line);
				if (m2.find())
				{
					String title = m2.group(1);
					doc.add(new Field("title", title, Field.Store.NO,Field.Index.NOT_ANALYZED));
				}
				Matcher m3 = tweetStatus_tag.matcher(line);
				if (m3.find())
				{
					String tweetStatus = m3.group(1);
					//System.out.println(tweetStatus);
					//if (tweetStatus.equals("200"))
					//{
					 doc.add(new Field("tweetStatus", tweetStatus, Field.Store.YES,Field.Index.NOT_ANALYZED));
					//}
				}
				Matcher m4 = tweetTime_tag.matcher(line);
				if (m4.find())
				{
					String tweetTimeWithTag = m4.group(0);
					int indexEndTag = tweetTimeWithTag.lastIndexOf('<');
					String tweetTime = tweetTimeWithTag.substring(11,indexEndTag);
					//System.out.println(tweetTime);
					doc.add(new Field("tweetTime", tweetTime, Field.Store.YES,Field.Index.NOT_ANALYZED));
				}

				sb.append(line);
			}
			if (sb.length() > 0)
			{
				//System.out.println(sb);
				//doc.add(new Field("text", sb.toString(), Field.Store.NO, Field.Index.ANALYZED));
			}
		} catch (IOException e) {
			doc = null;
		}
		return doc;
	}

	@Override
	public void remove() {
		// Do nothing, but don't complain
	}
	public String[] removeDuplicates (String array[])
	{
		array = new HashSet<String>(Arrays.asList(array)).toArray(new String[0]);
		return array;
	}
	public boolean matching(String fileNo)
	{
		for (String s : IndexerForMeta.arrayOfFileNumbers)
		{
			if (s != null)
			{
				if (s.equals(fileNo))
				{
					//System.out.println(fileNo);
					return true;
				}
			}
			else
			{
				continue;
			}
		}
		return false;
	}
}
