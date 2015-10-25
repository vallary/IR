/*
 * Test Class to check how the language identification tool works.
 */
package indexer;

import org.knallgrau.utils.textcat.TextCategorizer;

public class LanguageIdentification {

	public static void main(String args[])
	{
		String category = "";
        TextCategorizer guesser = new TextCategorizer();
        String language = "Lady you smell like playdoh #pfchangs";
        //if(args.length > 0) {
           category = guesser.categorize(language);
        //}
        System.out.println(category);
     }
	}

