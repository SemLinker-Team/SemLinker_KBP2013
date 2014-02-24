/*
 * 
 * KBP2013 Package is a set of classes used to deploy
 * a system on NIST KBP 2012 and 2013 evaluation campaign.  
 * 
 */
package kbp2013.managedocuments;

/*

SemLinker V 0.9
Copyright (C) 2013  Eric Charton & Marie-Jean Meurs &
                    Ludovic Jean-Louis & Michel Gagnon

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, 
Boston, MA  02110-1301, USA.

Contacts :

This software is maintained and released at:

https://code.google.com/p/semlinker/

Please contact respective authors from this page for support
or any inquiries. 

*/

/**
 * Class that applies various normalizations to query word sequence
 * 
 * @author ericcharton
 * 
 */
public class QueryProcessing {

	
	
	/**
	 * 
	 * Normalize for various special chars. 
	 * 
	 * @param Qname A query string
	 * @return the normalized query
	 */
	public static String normalize(String Qname){

		// clean Name
		Qname = Qname.replaceAll("^[ ]+", "");
		
		// normalize name for mention detection
		String QnameNormalized = Qname.toLowerCase(); // to lower case
		
		// remove brackets
		QnameNormalized = QnameNormalized.replaceAll("[,]", " "); 
		
		// QnameNormalized = QnameNormalized.replaceAll("[\\.]", " "); // query EL_ENG_00524 [Arthur M. Schlesinger Jr][arthur m schlesinger jr] APW_ENG_20071217.0131.LDC2009T13 3274 3297
		// query EL_ENG_00597 [Lewis A. Kaplan][lewis a kaplan] APW_ENG_20070702.0984.LDC2009T13 504 518 
		//		QnameNormalized = QnameNormalized.replaceAll("Inc|Corp", " "); 
		//		QnameNormalized = QnameNormalized.replaceAll(" inc| corp", " ");
		QnameNormalized = QnameNormalized.replaceAll("[ ]+", " "); 
		QnameNormalized = QnameNormalized.replaceAll("^ ", ""); 
		QnameNormalized = QnameNormalized.replaceAll(" $", ""); 
		
		return QnameNormalized;
		
	}
	
	
	/**
	 * 
	 * Correct some common misspellings in queries, like special chars and 
	 * remove some conflicting chars like * or _ .<br>
	 * <br>
	 * Can use a Gazeeter or one of the 2 versions of Lucene Wiki.
	 * 
	 * 
	 * @param Querytext	the text sequence from query to protect
	 * @param useSpellchecker define if will use a gazeteer or one of the 2 lucene wiki acess
	 * @param useLuceneWikiSpellchecker if true, use a locally installed version of Lucene Wiki Spell check, instead
	 *        use the online search engine of Wikipedia. 
	 * @return the corrected query
	 * 
	 */
	public static String correctQuery(String Querytext, boolean useSpellchecker, boolean useLuceneWikiSpellchecker){
		
		// instantiate a spelling corrector
		SpellingCorrector mySpellingcorrector = new SpellingCorrector();
		
		//----------------------------------
		// According to flag, use gazetteer
		// or Lucene Wiki Spell Checker
		//----------------------------------		
		if ( useSpellchecker ){
			
			if (useLuceneWikiSpellchecker){
				
				Querytext = mySpellingcorrector.SpellWithLuceneWiki(Querytext); // use LucenWikiSpellCheck
			}else{
				Querytext = mySpellingcorrector.SpellWithOnlineWiki(Querytext); // use LucenWikiSpellCheck
			}
			
		}else{
		
			Querytext = mySpellingcorrector.ReplaceMisspelledWordInMention(Querytext);	// use a gazetteer
		}
		
		
		
		// clean * _ -
		Querytext = Querytext.replaceAll("\\*", "");
		Querytext = Querytext.replaceAll("_", "");
	
		// 0 -> 0 1 -> I 5 -> S
		Querytext = Querytext.replaceAll("0", "O");
		Querytext = Querytext.replaceAll("1", "I");
		Querytext = Querytext.replaceAll("5", "S");
		
		// $ -> S
		Querytext = Querytext.replaceAll("\\$", "S");
		
		// athens -> Athens
		String firstletter = Querytext.substring(0, 1).toUpperCase();
		String partialQuery = Querytext.substring(1, Querytext.length());
		Querytext = firstletter + partialQuery;
		
		// replace double spaces if exists
		Querytext= Querytext.replaceAll("[ ]+", " ");
		
		System.out.println("[correctQuery]Final Query " + Querytext);
		
		return Querytext;
		
	}
	
	
	
}
