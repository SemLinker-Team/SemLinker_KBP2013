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

/**
 * 
 * This class try to locate quantifiers in queries
 * This include surface / length / references / amounts
 * 
 * @author ericcharton
 *
 */
public class AmountDecoder {
	
	/**
	 * 
	 * Constructor
	 * 
	 */
	public AmountDecoder(){
		
		
	}
	
	/*
	 * v 9 x 5 
	 * 1-3 8 to 1-37 64 35 to 40 mm Packs of 10
	 * R2 DE 1 4 X 1 08MTS
	 * R5 DE 5 16 X 0 77 MTS CON HEMBRAS DE 9 16 JIC
	 * 100 tonne 14 02 11
	 * 100 US dollars
	 * 24awg 4p 305M 
	 * 11PCE
	 * 0 050in-3 8in
	 * 12 1 4''
	 * 12 days rental of 3 1 7 16 tungsten bars
	 * 12 volt relay
	 * 12' x 15' US Door Model 651
	 * 12-Apr 1033518 11SEB0066 ZaZa Nabors 726 Inspection $268 00
	 * 12-point
	 * 1200 x 20 -> (size)
	 * 50MM X 50MT 
	 * 12vdc
	 * 7 inch 
	 * 1x25 pcs
	 * 
	 */
	
	
	/** This is the currency decoder
	 * 
	 * @param text
	 * @return
	 */

	public ArrayList<String> decodeamount(String text){
		
		 ArrayList<String> returnsequences = new ArrayList<String>();
		 
		 returnsequences = decode(text,"[$][0-9 ]+");
		 returnsequences = decodeAndAppend(text,"[0-9]+ US dolla[rs]+",returnsequences);
		 returnsequences = decodeAndAppend(text,"[0-9 ,\\.]+\\$",returnsequences);//27000 $
		 returnsequences = decodref(returnsequences);
		 return returnsequences;
		 
	 }
	
	 /**
	  * 
	  * This is the size decoder
	  * 
	  * @param text
	  * @return
	  */
	 public ArrayList<String> decodesize(String text){
		
		 text = text + " "; // this is to match the regular expression if it is at the end of sentence
		 ArrayList<String> returnsequences = new ArrayList<String>();
		 
		 returnsequences = decode(text,"[0-9-]+ [Xx]+ [0-9- ]+ ");//1200 x 20
		
		 // note below we use as text the returned original query minus the found sequence with returnsequences.get(0)
		 // to avoid new find inside
		 // a sequence already  found
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[0-9,]+'[x ]+[0-9,]+ '",returnsequences); //12' x 15'
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[0-9, -]+poin[ts]+ ",returnsequences);  //12-point
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[0-9, -]+ft ",returnsequences); //15ft
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[0-9, -]+mm ",returnsequences); //50 mm
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[0-9, -]+inch ",returnsequences); //7 inch 
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[0-9, -]+[Ii][Nn] ",returnsequences); //4 in 
		 // 50IN
		 // 30FT
		 returnsequences = decodref(returnsequences);
		 return returnsequences;
		// volts and watts
		 
	 }
	
	 /** 
	  * This is the weight decoder
	  * 
	  * @param text
	  * @return
	  */
	 public ArrayList<String> decodquant(String text){
		
		 text = text + " "; // this is to match the regular expression if it is at the end of sentence
		 ArrayList<String> returnsequences = new ArrayList<String>();
		 
		 returnsequences = decode(text,"[0-9, ]+[Ll] ");//10 L
		
		 // note below we use as text the returned original query minus the found sequence with returnsequences.get(0)
		 // to avoid new find inside
		 // a sequence already  found
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[0-9, -]+[Oo][Zz] ",returnsequences);  //14oz
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[0-9, -]+[Ll][Bb] ",returnsequences); //7 lb  // 150LB
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[0-9, -]+[kK][Gg] ",returnsequences); // 100 KG
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[0-9, -]+[g] ",returnsequences); // 400g 
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[0-9, -]+[Mm][Ll] ",returnsequences); //160 ML
		 returnsequences = decodref(returnsequences);
		 return returnsequences;
		 
	 }
	 
	 /**
	  * This is the energy decoder applied to volts and watts
	  * 
	  * @param text
	  * @return
	  */
	 public ArrayList<String> decodNRJt(String text){
		
		 text = text + " "; // this is to match the regular expression if it is at the end of sentence
		 ArrayList<String> returnsequences = new ArrayList<String>();
		 
		 returnsequences = decode(text,"[0-9, ]+Kvolts ");//17Kvolts
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[0-9, ]+[Vv]olt[s ]",returnsequences); //5 volts
		 returnsequences = decodeAndAppend(returnsequences.get(0),"[0-9,\\. ]+v ",returnsequences); //5.4 v
		 returnsequences = decode(text,"[0-9, ]+[Ww]att[s ]"); //40 watt 
		 returnsequences = decodref(returnsequences);
		 return returnsequences;
		 
	 }
	 
	 /** 
	  * This is the reference decoder
	  * 
	  * @param text
	  * @return
	  */

	 // No 3310
	 // 1LTR
	 public ArrayList<String> decodref(String text){
		
		text = text + " "; // this is to match the regular expression if it is at the end of sentence
		ArrayList<String> returnsequences = new ArrayList<String>();
		 
		returnsequences = decode(text,"[Nn][Oo] [0-9-]+ "); // no 2390 
		returnsequences = decodeAndAppend(returnsequences.get(0),"[Nn][Oo] [A-Z0-9-]+ ",returnsequences); //No BX154SS
		returnsequences = decodeAndAppend(returnsequences.get(0),"ref [ 0-9]+ ",returnsequences); //ref 22240</
																								 
		
		// note below we use as text the returned original query minus the found sequence to avoid new find inside
		// a sequence already  found
		returnsequences = decodeAndAppend(returnsequences.get(0),"[A-Z]+[0-9]+[A-Z]+ ",returnsequences);  // FC01LBG
		returnsequences = decodeAndAppend(returnsequences.get(0),"[0-9]{1,10}[A-Z]{1,10}[0-9]{1,10} ",returnsequences); // 6216A003
		returnsequences = decodeAndAppend(returnsequences.get(0),"[0-9]{1,10}[A-Z]{1,10} ",returnsequences);  // 6228SLT
		returnsequences = decodeAndAppend(returnsequences.get(0),"[0-9]+[-][0-9]+ ",returnsequences); //1323-0506
		returnsequences = decodeAndAppend(returnsequences.get(0),"[A-Z]+[-][0-9]+ ",returnsequences);  // MMT-7335finder
		
		returnsequences = decodeAndAppend(returnsequences.get(0),"[A-Z]+[0-9]+[-][0-9]+ ",returnsequences);  // SGH663-6 
		returnsequences = decodeAndAppend(returnsequences.get(0),"[0-9]+[-][A-Z]+ ",returnsequences);  // 4222-SP
		//13CR-L80
		//2SUT-20122

		returnsequences = decodref(returnsequences);
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
	 
	 /**
	  * 
	  * @param text
	  * @param matchreg
	  * @return
	  */
	 private ArrayList<String> decode(String text, String matchreg)
	 {

		ArrayList<String> returnsequences = new ArrayList<String>();
		returnsequences.add(text); 
		String newtext = text;
		
		// see http://www.programmersheaven.com/2/RegexJAVA
		// Compile pattern.
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
			
	
		// save in offset 1 the cleaned text
		returnsequences.set(0, newtext);
	
		return returnsequences;
		 
	 }
	 
	 /**
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
			
			// System.out.println("-->" + m.group() + ")");
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
	

}
