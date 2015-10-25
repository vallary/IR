package trecQueries;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class FilteringTask {
public static void main(String[] args)
{
	try {
		//FileInputStream tempTopFile = new FileInputStream("C:/DATA/IR/IRProject/Filtering/FilteringTemp2IndexesTop500.top");
		FileInputStream tempTopFile = new FileInputStream("Filtering/FilteringTemp2IndexesTop500.top");
		//FileInputStream tempTopFile1 = new FileInputStream("C:/DATA/IR/IRProject/Filtering/FilteringTemp2IndexesTop500.top");
		FileInputStream tempTopFile1 = new FileInputStream("Filtering/FilteringTemp2IndexesTop500.top");
		DataInputStream topStream = new DataInputStream(tempTopFile);
		DataInputStream topStream1 = new DataInputStream(tempTopFile1);
		BufferedReader t1 = new BufferedReader(new InputStreamReader(topStream));
		BufferedReader t2 = new BufferedReader(new InputStreamReader(topStream1));
		//FileWriter f = new FileWriter("C:/DATA/IR/IRProject/Filtering/Filtering2IndexesTop500.top");
		FileWriter f = new FileWriter("Filtering/Filtering2IndexesTop500.top");
		BufferedWriter out= new BufferedWriter(f);
		String strLine = "";
		float totalScore = 0.0f;
		int counter = 0;
		while ((strLine = t1.readLine()) != null)
		{
			String[] columns = new String[4];
			columns = strLine.split("\t");
			totalScore = totalScore + Float.parseFloat(columns[2]);
			counter = counter + 1;
		}
		//System.out.println(totalScore);
		float avgScore = (float)(totalScore/counter);
		t1.close();
		while ((strLine = t2.readLine()) != null)
		{
			//System.out.println("Test");
			String[] columns = new String[4];
			columns = strLine.split("\t");
			String condition = "";
			if(Float.parseFloat(columns[2]) >= avgScore)
			{
				condition = "yes";
			}
			else
				condition = "no";
			out.write(columns[0]+"\t"+columns[1]+"\t"+columns[2]+"\t"+condition+"\t"+"FilteringRun2IndexesTop100"+"\n");
		}
		t2.close();
		out.close();
		
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
}
