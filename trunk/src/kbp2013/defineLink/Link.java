package kbp2013.defineLink;

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

import java.util.HashMap;
import java.util.Iterator;

import semkit.extractor.WikiMetaXMLDecoder;

import kbp2013.managelinkoutput.CorrespondanceTableKBPWikimeta;
import kbp2013.tools.Logging;

/**
 * 
 * This class is used to define a KB Node according to an annotated document. 
 * It make the final decision on link choice. 
 * 
 * @author ericcharton
 *
 */
public class Link {

	// Instantiate a correspondence table between Wikimeta Links and KB
	CorrespondanceTableKBPWikimeta KBCorrespondanceTable;
		
	// this is to store KBref, mention related
	String[][] results = new String[5000][4];
	
	// this is to send back surface form
	private String allSf = "";
	// this is to send back Wikimeta link when exists (even if NIL for KB)
	private String allWMUri = "";
	// this is to send back the last best NE 
	private String allNE = "";
	// this is the last mention utilized to discover the Link
	private String finalMentionExpanded = "";
	// heuristic utilized
	private int heurisRef = 0;
	// key ranked string container
	private String keyRankeds = "";

    private String keyvalue = null;
        
        
	/**
	 * 
	 * Constructor and class execution
	 * 
	 * 
	 * @param annotations
	 * @param normalizedQueryName
	 * @param queryName
	 * @param linkAtPos
	 * @param KBCorrespondanceTablePassed
	 * @param log
	 * @param maxnumberofannotation
	 */
	public Link(WikiMetaXMLDecoder annotations, 
            	String normalizedQueryName, 
                String queryName, 
                int linkAtPos, 
                CorrespondanceTableKBPWikimeta KBCorrespondanceTablePassed,
                Logging log, int maxnumberofannotation)
	{	
            
		// instantiate a correspondence table between KB and Wikimeta
		KBCorrespondanceTable = KBCorrespondanceTablePassed;
        // activate a link decision process
        decideLink(annotations, normalizedQueryName, queryName, linkAtPos, log, maxnumberofannotation);		
	
	}
	
	
	/**
	 * 
	 * Baseline method to define a Link and a KB label.
	 * 
	 * 
	 * @param annotations     : The annotation object according to the text sent
	 * @param QnameNormalized : The string prepared for matching (no caps)
	 * @param Qname			  : The original mention target from query
	 * @param originalText  : The text with no transformations from the corpus
	 * @param linkAtPos     : The offset of the mention in the WikimetaXMLDecoder
	 * @return
	 */
	private void decideLink(WikiMetaXMLDecoder annotations , String QnameNormalized, String Qname,  int linkAtPos, Logging log, int maxnumberofannotation){
		
		int getSizeofDecoder = annotations.size();
		
		// initialize variables
		// String MentionExpanded = ""; // then mention utilized to match after expansion
		int charlenght = 0; // an estimated position in chars in the document
		int StringArrayIdx = 0;
		allSf = "";
		
		
			
		// ---------------------------------------------------------
		// Document based query expansion 
		//   - If the mention found after document annotation is larger than 
		//     the one contained in QnameNormalized, the expanded mention 
		//     is kept
		// ---------------------------------------------------------
		
		// Instantiate a Query expansion class
		ExpandQuery qexpander = new ExpandQuery();
		String MentionExpanded  = qexpander.getExpansion( annotations , QnameNormalized, Qname, linkAtPos, true);
		
		log.writeLog("    [Define Link]");
		
		//-----------------------------------
		// collect match according to Qname
		// or expanded query
		//-----------------------------------
		boolean setametalink = false;
		for(int h=0; h <getSizeofDecoder; h++){
		
				charlenght = charlenght + annotations.getwordatpos(h).length() +1;
				
				String StrToCompare = annotations.getSurfaceFormNormalizedatpos(h);
				
				// sort found references
				if (  StrToCompare != null){
					
					// ----------------------------
					// Reject some malformed mentions
					// or some mismatches
					// ----------------------------
					
					// Pre-processing to reject some malformed mention, source of noise. 
					boolean malformedMention = true;
					String verifymentionAnomaly=annotations.getSurfaceFormNormalizedatpos(h).toLowerCase();
					if (verifymentionAnomaly.contains(">")){malformedMention = false;}
					if (verifymentionAnomaly.contains("<")){malformedMention = false;}
					if (verifymentionAnomaly.contains("http")){malformedMention = false;}
					if (verifymentionAnomaly.contains("TEX")){malformedMention = false;}
					if (verifymentionAnomaly.contains(":")){malformedMention = false;} // Pos:11->US - NJ : Newton -  925:Managing query EL_ENG_00927 [Newton][newton] eng-NG-31-133410-9530245 1338 1343
								
					// ----------------------------
					// Collect a candidate list 
					// according to the surface form
					// ----------------------------
					if (   ( StrToCompare.contains( MentionExpanded ) == true ) && malformedMention == true ){
						
								String[] kbkey = new String[maxnumberofannotation];
								for (int z= 0; z < maxnumberofannotation; z++){
									if (annotations.getMetadataKeyRanked(z, h) != null) {
										kbkey[z] = KBCorrespondanceTable.returnKbpKey(annotations.getMetadataKeyRanked(z, h));
									}else{
										kbkey[z] = null;
									}
								}
								
								// store in the array
								results[StringArrayIdx][0]=kbkey[0]; // kb key
								results[StringArrayIdx][1]=annotations.getSurfaceFormNormalizedatpos(h).toLowerCase(); // Surface form of the mention -> normalized
								results[StringArrayIdx][2]=annotations.getMetadata(h); // Save the Wikimeta key than can disambiguate event without KB key
								results[StringArrayIdx][3]=annotations.getNELabel(h); // Save the Wikimeta EN
								StringArrayIdx++;
								
								// !! Annotation Error correction !!
								// if meta data have previously been found and there is a NIL NIL like :
								//  --897:4183:NIL:NIL:PERS(mention:purchase lucy walsh) NIL [null null])(key:NIL / null / null)
								// cancel the annotation
								if (setametalink && annotations.getMetadatakey(h).contains("NIL") ){
									StringArrayIdx--; // cancel the storage by canceling the new index increment
								}else{
									
									//-------------------------
									// display the results
									//-------------------------
									System.out.print("    --" + h + ":" + charlenght + ":" + annotations.getMetadata(h) + ":" + annotations.getMetadatakey(h) + ":" + annotations.getNELabel(h)  + 
												"   (mention:"+ annotations.getSurfaceFormNormalizedatpos(h)+")   " + 
												"   { "
									);
									// display keys, NIL or NULL
									for (int o = 0; o< maxnumberofannotation; o++){
										System.out.print(kbkey[o] + "/");
									}
									// display original key from wikimeta or null
									System.out.print(" } (key:" );
									for (int o = 0; o< maxnumberofannotation; o++){
										System.out.print(annotations.getMetadataKeyRanked(o, h) + "/");
									}
									System.out.println(" )" );
									
									// store the ranked list (KB Key, NIL or null)
									keyRankeds = kbkey[0];
									for (int o = 1; o< maxnumberofannotation; o++){
										keyRankeds = keyRankeds + "\t" + kbkey[o] ;
									}
									
								}
								
								// update
								if ( !annotations.getMetadatakey(h).contains("NIL") ) setametalink = true;
							}
					
					
					
				}
				
		}
		
		//-----------------------------------
		//
		// Heuristics
		// 
		//-----------------------------------

		// nothing is given
		// ---> NIL by default
	
		int allequals = 1;
		
		// no solution returned
		if (StringArrayIdx == 0){

			// set the returned variables
			keyvalue = "NIL";	// default URI
			allWMUri = "NORDF"; 	// Default NIL metadata from Wikimeta
			allNE = "UNK";// default EN
			allSf = MentionExpanded ; // default SF
		
			allequals = -1;
		}
		
		//----------------------------------
		// filtering ambiguity
		//----------------------------------
		
		//----------------------------------------
		// *perfect match 
		// ---> all entities related to the 
		//      query with same KB number
		for (int u = 0; u <StringArrayIdx; u++){
			
			// set the returned variables
			keyvalue = results[u][0];
			allSf = results[u][1]; 
			allWMUri = results[u][2]; 
			allNE = results[u][3];
			
			
			for (int v = 0; v <StringArrayIdx; v++){
				
				// if only one key differs, it's enough to refuse this // optionresults[u][1].length()
				if (! results[u][0].contains(results[v][0])){
					
					allequals = 0;
					
					// Default NIL metadata from Wikimeta
					allWMUri = "NORDF";
					keyvalue = "NIL";
					allNE = "UNK";
					allSf = MentionExpanded ; // default SF
				}
			}	
		}
		
		// --------------------------------
		// Flag to choose Heuristics
		// --------------------------------
		int bestmatch = 1;
		
		
		//----------------------------------
		// *bestkey
		// mention of more than one word best match
		// ---> many mention with n words are associated to entity name
		//     - http://wikimeta.com/wapi/display.pl?query=PAL Express&search=EN:PAL Express:201(searched:pal)(mention:PAL Express) E0475739
		int wordLimit = 2; // limit size 
		HashMap<String, Integer>  selected = new HashMap<String, Integer>(); // the hash of the keys
		HashMap<String, Integer>  selectedSize = new HashMap<String, Integer>(); // the size of the keys (in words)
		HashMap<String, String>   selectedKbKey = new HashMap<String, String>(); // the KB Keys
		
		// index all the mentions with more than one word
		for (int u = 0; u <StringArrayIdx; u++){
			
			// --- we split to evaluate the number of words in the mention 
			String tosplit = results[u][1].replaceAll(" [\\.,]", " "); // do not count punctuation in split
			tosplit = tosplit.replaceAll("[\\.,] ", " "); // do not count punctuation in split
			tosplit = tosplit.replaceAll("[ ]+", " "); // replace double spaces
			// split
			String[] splited = tosplit.split(" ");
			
			// ------ make the test
			// if the mention is more than 1 word long
			if (splited.length >1){
			
				int localkeyvalue = 0;
				// increment the key
				//   --->first case: is it an existing one -> collect its occurrences
				if (selected.containsKey(results[u][1])) localkeyvalue = selected.get(results[u][1]); 
				
				selected.put(results[u][1],localkeyvalue + 1);   // this is an existing key -> updating the count
				selectedSize.put(results[u][1], splited.length); // save the number of words
				selectedKbKey.put(results[u][1], results[u][0]); // store the kbkey -> this is to detect NIL 
				
			}
		}
		
		// find the most cited of the more-than-one word long
		String most = ""; // the key of the most cited
		int valuemost =0; // the amount of citations
		int numberOfWords = 0; // number of words in the key
		
		
		
		// iterate on all keys
		for( Iterator ii = selected .keySet().iterator(); ii.hasNext();) { 
			
			// collect information about each keys
			String key = (String)ii.next(); // get the current key
			int value = selected .get(key); // get the value of the current key
			int WordsInKey = selectedSize.get(key); // get the size of the key
			// System.out.println(key + "-" + value + "-" + WordsInKey);
			
			// set the best
			//     there is an error here -> sometimes more than one are bests
			//     we also remove long errors > If there is a NIL, it's very often a mention boundary bug and we don't collect
			if (value >= valuemost && WordsInKey >= numberOfWords && WordsInKey <= wordLimit) 
			{ 
					valuemost = value; 
					most = key;
					numberOfWords = WordsInKey;
			}
		}
		// search now in array
		// String kbkey = "";
		for (int u = 0; u <StringArrayIdx; u++){
			if (results[u][1].contentEquals(most)) { 
			
				// Store the metadata
				allWMUri = results[u][2];
				// store the key
				keyvalue = results[u][0];
				// store the NE 
				allNE = results[u][3];
				// store the surface form
				allSf = results[u][1]; 
			}
		}
	
		
		// GPE Heuristic
		/// -----> overwrite a bestkey
		if ( linkAtPos > 0 && annotations.getNELabel(linkAtPos).contains("LOC") )
		{
				
			// System.out.println( "------------------------------>LOC Heuristic");
			
			// Hash for checking if the reference key is the majority
			HashMap<String, Integer>  statsOnKey = new HashMap<String, Integer>(); // hash of keys
			// collect the reference key at POS
			String referenceKey  = KBCorrespondanceTable.returnKbpKey(annotations.getMetadatakey(linkAtPos));
			
			// index all the keys and accumulate
			int maxkeyAmount = 0;
			String maxreferencedkey = "";
			
			for (int u = 0; u <StringArrayIdx; u++){
				
				if (! results[u][0].contains("NIL")){
					
						// store the key and its occurrences amount
						int storeKey = 0;
						if (statsOnKey.containsKey(results[u][0])) 
						{ 
							storeKey = statsOnKey.get(results[u][0]); 
						} 
						//   --->this is a new key
						statsOnKey.put(results[u][0], storeKey + 1);   // this is an existing key -> updating the count
						
						// check if it is the most used key
						if ( statsOnKey.get(results[u][0]) >= maxkeyAmount ){
							maxkeyAmount = statsOnKey.get(results[u][0]);
							maxreferencedkey = results[u][0];
						}
				}
			}
			
			
			// if the reference key is the same as at target
			if (maxreferencedkey.contentEquals(referenceKey)){
				bestmatch = 3;			
				// keyvalueAtPos = referenceKey;
				// Store the URI of Wikimeta
				allWMUri = annotations.getMetadata(linkAtPos);		// return the key
				// Store the KB Key value
				keyvalue = referenceKey;
				//Store the mention NE
				allNE = annotations.getNELabel(linkAtPos);
				// store the mention SF
				allSf = annotations.getSurfaceFormNormalizedatpos(linkAtPos);
			}
		}
		
		// -------------------------------
		// *BestKeyOfoneWord:
		// Most marked of one word long
		// When Bestkey returns 0 as valuemost 
		// evaluate all the propositions (see fairfield EL_ENG_01291 )
		if ( valuemost == 0 && bestmatch == 1 && allequals == 0 ){
	
			//String bestKbKey = "NIL";
			int mostcitedbestKbKey = 0;
			int offsetOfmostCited = 0;
			
			HashMap<String, Integer>  bestStats = new HashMap<String, Integer>();
			
			// sort results
			for (int u = 0; u <StringArrayIdx; u++){
				
				String currentkeyvalue = results[u][0];
				int quantOfKeyCite = 1;
				
				// store key values with count
				if ( bestStats.containsKey(results[u][0])) {
					quantOfKeyCite  = bestStats.get(results[u][0])+1;
					bestStats.put(currentkeyvalue, quantOfKeyCite );
				}else{
					bestStats.put(currentkeyvalue, quantOfKeyCite );
				}
				
				if ( quantOfKeyCite  > mostcitedbestKbKey){
						mostcitedbestKbKey =  quantOfKeyCite  ;
						// update values
						// bestKbKey = currentkeyvalue;
						valuemost = mostcitedbestKbKey;
						offsetOfmostCited = u;
				}
			}
			
			// Choose the key with the more results
			// Store the metadataseparated 
			allWMUri = results[offsetOfmostCited ][2];		
			// store the key
			keyvalue = results[offsetOfmostCited ][0];
			// store the NE 
			allNE = results[offsetOfmostCited ][3];
			// store the SF
			allSf = results[offsetOfmostCited][1]; 
			
			bestmatch = 2;
		}
		
		
		//---------------------------
		// results
		// - ordered by priority
		//---------------------------
		System.out.println("    {Heuristic:}");
		if (allequals == -1){	
			System.out.println("      *DefaultValues:" +  keyvalue + " (NLGbAseKey:" + allWMUri + ")");
			heurisRef = 1;
		}
		if (allequals == 1){	
			System.out.println("      *PerfectMatch:" +  keyvalue + " (NLGbAseKey:" + allWMUri + ")");
			heurisRef = 2;
		}
		if (bestmatch == 1 && allequals == 0){	
			System.out.println("      *BestKey: " + keyvalue+ " (NLGbAseKey:" + allWMUri + ")");
			heurisRef = 3;
		}
		if (bestmatch == 2 && allequals == 0){	
			System.out.println("      *BestKeyOfoneWord: " + keyvalue+ " (NLGbAseKey:" + allWMUri + ")");
			heurisRef = 4;
		}
		if (bestmatch == 3 && allequals == 0){	
			System.out.println("      *BestKeyAtPos for GPE: "  + keyvalue+ " (NLGbAseKey:" + allWMUri + ")"); 
			heurisRef = 5;
		}
		
		// store the last expanded mention
		finalMentionExpanded =  MentionExpanded ;
		
		
		System.out.println("");
		
	}
	
	
	
	//--------------------------------------
	//
	// Those methods return the last 
	// reference related to the previously
	// processed entry
	//
	//-------------------------------------
	
	/**
	 * return code of heuristic used
	 * 
	 * @return
	 */
	public int heuristicUsed(){
		
		return heurisRef;
	}
	 
	/**
	 * return last surface form
	 * 
	 * @return
	 */
	public String bestSf(){
		
		return allSf;
	}
	
	/**
	 * 
	 * @return
	 */
	public String bestFinalMention(){
		
		return finalMentionExpanded;
		
	}
	
	
	/**
	 * return last URI according to Wikimeta and NLGbAse
	 */
	public String bestUri(){
		
		return allWMUri;
	}
	
	/**
	 * Return last URI according to Wikimeta and NLGbAse
	 * 
	 * @return
	 */
	public String bestEN(){
		
		return allNE;
	}
	
	/**
	 * 
	 * Return last KB Key value
	 * 
	 * @return
	 */
	public String kbKeyValue(){
		
		return keyvalue;
		
	}
	
	/**
	 * 
	 * Return a string of ranked answers separated by tabulations -> KBKey, NIL or null
	 * 
	 * @return
	 */
	public String listofKeysRanked(){
		
		return keyRankeds;
		
	}

	
}
