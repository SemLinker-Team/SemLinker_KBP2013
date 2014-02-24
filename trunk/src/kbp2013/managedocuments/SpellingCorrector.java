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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import kbp2013.wikipedia.GetRewritingFromWikipedia;

import org.apache.commons.lang.StringUtils;

import configure.NistKBPConfiguration;

/**
 * 
 * This class corrects spelling mistakes using
 * "spelling_mistakes" mapping file
 * ex:  soctland --> Scotland
 * 
 * @author mariejeanmeurs
 *
 */
public class SpellingCorrector {

	// instantiate classes of constants
	NistKBPConfiguration KBvars = new NistKBPConfiguration();
	// map misspelled <--> rightly spelled 
	HashMap<String, String> spellingMap = new HashMap<String, String>();

	/**
	 * 
	 * Constructor
	 * 
	 * load misspelled word <--> corrected word
	 * 
	 */
	public SpellingCorrector(){

		//-----------------------
		// load spelling map
		//-----------------------
		try {

			BufferedReader reader = new BufferedReader(new FileReader(KBvars.SPELLING_MAP_FILE));

			String spelline = "";

			while (( spelline = reader.readLine()) != null) {
				if (! spelline.startsWith("#")) {
					String[] spellfull = spelline.split("\t"); 
					String misspelled = spellfull[0];
					String rightlyspelled = spellfull[1];

					spellingMap.put(misspelled, rightlyspelled);

				}

			}
			// System.out.println("[SpellingCorrector]Spelling map loaded " + spellingMap.size());
			reader.close();

		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * 
	 * Replace misspelled with rightly spelled in doc content
	 * @param content
	 * @return
	 */
	public String ReplaceMisspelledWordInDocContent(String content) {

		String missp = "";
		String rightsp = "";

		try{
			
			// 
			for(Entry<String, String> entry : spellingMap.entrySet()) {
				missp = entry.getKey();
				rightsp = entry.getValue();

				String wmisspw = " " + missp + " ";
				String wmisspc = " " + missp + ",";
				String wmisspd = " " + missp + ".";
				String wrightspw = " " + rightsp + " ";
				String wrightspc = " " + rightsp + ",";
				String wrightspd = " " + rightsp + ".";
//				String wrightsp = " " + rightsp;

				// replace 
				if( content.contains(wmisspw) ) { 
					content = StringUtils.replace(content, wmisspw,wrightspw);
					System.out.println("Replaced spelling mistake w: " + wmisspw + " : " + wrightspw);
				}

				if (content.contains(wmisspc)) {
					content = StringUtils.replace(content, wmisspc,wrightspc);
					System.out.println("Replaced spelling mistake c: " + wmisspc + " : " + wrightspc);
				}

				if (content.contains(wmisspd)) {
					content = StringUtils.replace(content, wmisspd,wrightspd);
					System.out.println("Replaced spelling mistake d: " + wmisspd + " : " + wrightspd);
				}
			}
		}
		
		catch (Exception e)
		{
			//Catch exception
			System.err.println("SpellingCorrector Error: " + e.getMessage());

		}

		return(content);
	}
	
	/**
	 * 
	 * Replace misspelled with rightly spelled in mention
	 * using gazetteer
	 * @param mention
	 * @return
	 */
	public String ReplaceMisspelledWordInMention(String mention) {

		String missp = "";
		String rightsp = "";

		try{
			
			// soctland --> Scotland
			for(Entry<String, String> entry : spellingMap.entrySet()) {
				
				missp = entry.getKey();
				rightsp = entry.getValue();
				
				// replace 
				if( mention.contains(missp) ) { 
					mention = StringUtils.replace(mention, missp,rightsp);
					System.out.println("[Gazetteer]Replaced spelling mistake: err : " + missp + " : right : " + rightsp + " : corr : " + mention);
					
				}
			}
		}
		catch (Exception e)
		{
			//Catch exception
			System.err.println("SpellingCorrector Error: " + e.getMessage());

		}

		return(mention);
	}
	
	/**
	 * Replace misspelled with rightly spelled in mention
	 * using Lucene wiki spell checker
	 * 
	 * @param mention
	 * @return
	 */
	public String SpellWithLuceneWiki(String mention){
		
		GetRewritingFromWikipedia wpSpell = new GetRewritingFromWikipedia();
		
		// currently the hack
		// use a locally installed version of LWSC
		String result = wpSpell.getResult(mention);
		
		//---------------------------------
		// Algo to process the answer
		// see LREC 2014 paper
		//---------------------------------
		result = checkSuggestion.validateARewriting(mention, result);
		
		if (result != null){
			System.out.println("[WPLucene]Replaced spelling mistake: err : " + mention +  " : corr : " + result);
			mention = result;
		}
		
		return(mention);
	}
	
	/**
	 * 
	 * This process the Query Rewriting with the Lucene Wiki Spell Checker of 
	 * Wikipedia Search engine
	 * 
	 * @param mention
	 * @return
	 */
	public String SpellWithOnlineWiki(String mention){
		
		GetRewritingFromWikipedia wpSpell = new GetRewritingFromWikipedia();
	
		// use the hack
		String result = wpSpell.getResult(mention);
		
		//---------------------------------
		// Algo to process the answer
		// see LREC 2014 paper
		//---------------------------------
		result = checkSuggestion.validateARewriting(mention, result);

		if (result != null){
			System.out.println("[WPLucene]Replaced spelling mistake: err : " + mention +  " : corr : " + result);
			mention = result;
		}
		
		return(mention);
	}
	

	
	
}