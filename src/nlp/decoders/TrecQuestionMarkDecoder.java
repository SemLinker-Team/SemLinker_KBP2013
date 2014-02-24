package nlp.decoders;

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

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrecQuestionMarkDecoder {

	
	/**
	 * 
	 * Constructor
	 * 
	 */
	public TrecQuestionMarkDecoder(){
		
	}
	
	
	/**
	 * 
	 * Classe used to decode Questions in the TREC Q&A campaign.
	 * See <a href="http://trec.nist.gov/data/qamain.html">TREC Q&A main page for data.</a> 
	 * 
	 * @param text
	 * @return
	 */
	public ArrayList<String> decodequestion(String text){
			
		 text = text + " "; // this is to match the regular expression if it is at the end of sentence
		 ArrayList<String> returnsequences = new ArrayList<String>();
		 
		 returnsequences = decode(text,"[Ww]hen was "); // When was
		 
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[Ww]hat are ",returnsequences); // What are
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[Ww]hat is ",returnsequences); // What is
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[Ww]hat does ",returnsequences); // What is
		 
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[Ww]here was ",returnsequences); // Where was
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[Ww]here is ",returnsequences); // Where was
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[Ww]here do ",returnsequences); // Where was
		 
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[Hh]ow often ",returnsequences); // Where was
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[Hh]ow did ",returnsequences); // Where was
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[Hh]ow many ",returnsequences); // How Many
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[Hh]ow long ",returnsequences); // How Many
		 
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[Ii]n what ",returnsequences); // In what
		 
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[Ww]hen did ",returnsequences); // Where was
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[Ww]hen was ",returnsequences); // When was
		 
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[Ww]hich was ",returnsequences); // What is
		 
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[Ww]ho is ",returnsequences); // What is
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[Ww]ho have ",returnsequences); // What is
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[Ww]ho was ",returnsequences); // What is
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[Ww]ho are ",returnsequences); // What is
		 
		 // alone
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[Ww]hich ",returnsequences); // What is
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[Ww]hat ",returnsequences); // What is
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[Ww]ho ",returnsequences); // What is
		 
		 returnsequences = decodref(returnsequences);
		 
		 return returnsequences;
		 
	 }
	 
	 
	/**
	 * 
	 * 
	 * @param text
	 * @param matchreg
	 * @param returnsequences
	 * @return
	 */
	 private ArrayList<String> decodeAndAppend(String text, String matchreg, ArrayList<String> returnsequences)
	 {

		String newtext = text;
		
		// see http://www.programmersheaven.com/2/RegexJAVA
		// Compile the pattern.
		Pattern p = Pattern.compile(matchreg);
		// Match it.
		Matcher m = p.matcher(text); 
		// Get all matches and clean sentence
		while (m.find() == true){
			
			String extracted = m.group();
			extracted.replace("[ ]+$", "");
			returnsequences.add(m.group());  // add the decoded sequence
	
			
			// clean the query to not decode again
			newtext = text.replaceAll(m.group() , "");
		}
			
	
		// save in offset 1 the cleaned text
		returnsequences.set(0, newtext);
	
		return returnsequences;
		 
	 }
	 
	 
	 private ArrayList<String> decode(String text, String matchreg)
	 {

		ArrayList<String> returnsequences = new ArrayList<String>();
		returnsequences.add(text); 
		String newtext = text;
		
		// see http://www.programmersheaven.com/2/RegexJAVA
		// Compile the pattern.
		Pattern p = Pattern.compile(matchreg);
		// Match it.
		Matcher m = p.matcher(text); 
		// Get all matches and clean sentence
		while (m.find() == true){
			
			// System.out.println("-->" + m.group() + ")");
			String extracted = m.group();
			extracted.replace("[ ]+$", "");
			returnsequences.add(m.group());  // add the decoded sequence
	
			
			// clean the query to not decode again
			newtext = text.replaceAll(m.group() , "");
		}
		
		 return returnsequences;
	 }
	 
	 /**
	  * Remove doubles
	  * 
	  * @param returnsequences
	  * @return
	  */
	 private ArrayList<String> decodref(ArrayList<String> returnsequences){
			
		 	// search double entry
			for (int t=1; t< returnsequences.size(); t++){
				String testDouble = returnsequences.get(t);
				for (int u=t+1; u < returnsequences.size(); u++){
					
					// remove if double
					if (returnsequences.get(u).contentEquals(testDouble)){
						
						// remove the sequence (clearing to avoid shifting of collection)
						returnsequences.set(u, "");
						
						// remove doubles in the original text sequence 
						//String newtext = returnsequences.get(0).replaceAll(returnsequences.get(u) , "");
						//returnsequences.add(u, newtext);
					}else{
						// clean text
						String a = returnsequences.get(u);
						a.replace("[ ]+$", ""); // remove space at end
						a.replace("^[ ]+", "");  // replace space at beginning
						returnsequences.set(u, a);
					}
					
					
					
				}
			}
			
			return returnsequences;
	 }
	 
}
