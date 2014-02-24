package nlp.upperlevel;

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

import configure.NistKBPConfiguration;

import semkit.extractor.WikiMetaXMLDecoder;
import semkit.extractor.WikipediaExtractor;


/**
 * 
 * This class receives an WikimetaXMLDecoder Annotated Object, 
 * retrieves all the DBPedia or Wikipedia content for each
 * link, and uses them to disambiguate. 
 * It is applied to a WikiMetaXMLDecoder object.
 * (see SemLinker NIST KBP2013 paper for  information!)
 * 
 * 
 * @author ericcharton
 *
 */
public class MutualDisambiguation {

	boolean verbose = false;
	
	boolean fullCompare = false; // compare all annotations against all annotations

	WikipediaExtractor wpextract;
	
	private int limitOfcollection = 1599; // do not collect more documents
	
	private int countModifications = 0; // count the modifications of semantic annotations applied
	private int countMatchKeyModifications = 0; // count the modifications by matchkey only
	
	private String lucenPathofWikipediaIndex = null;
	
	//--------------------------------------
	// Weighting of Mutual relations
	//--------------------------------------
	private int mutualLinksWeight = 2;
	private int mutualCatsWeight = 1;
	private int linkToTheOthersWeight = 10;

	NistKBPConfiguration KBvars ;	
	private int numberOfCandidates = 15; // 3 is default value
	
	/**
	 * 
	 * Constructor
	 * 
	 */
	public MutualDisambiguation(){
				
			this.wpextract = new WikipediaExtractor();

	}
	
	/**
	 * 
	 * 
	 * @param numberofcandidates
	 */
	public MutualDisambiguation(int numberofcandidates){
		
		this.wpextract = new WikipediaExtractor();
		numberOfCandidates = numberofcandidates;
	}
	
	/**
	 * 
	 * Constructor
	 * 
	 * @param LucenePath
	 */
	public MutualDisambiguation(String LucenePath){
		lucenPathofWikipediaIndex = LucenePath;
		this.wpextract = new WikipediaExtractor(lucenPathofWikipediaIndex );
	}
	
	/**
	 * Constructor
	 * 
	 * @param LucenePath
	 * @param numberofcandidates
	 */
	public MutualDisambiguation(String LucenePath, int numberofcandidates){
		lucenPathofWikipediaIndex = LucenePath;
		this.wpextract = new WikipediaExtractor(lucenPathofWikipediaIndex );
		numberOfCandidates = numberofcandidates;
	}

	
	
	/**
	 * 
	 * 
	 * 
	 * @param annotations
	 * @return
	 */
	public WikiMetaXMLDecoder disambig(WikiMetaXMLDecoder annotations, String documentCleaned ){
		
		HashMap<String, String>  candidates = new HashMap<String, String>(); 
		HashMap<String, Integer>  metalinks = new HashMap<String, Integer>(); 
		
		// the document is used for matching, clean it
		documentCleaned = documentCleaned.replaceAll(",", " ");
		documentCleaned = documentCleaned.replaceAll("[ ]+", " ");
		documentCleaned = documentCleaned.toLowerCase();
		
		// build a hashmap of all the metada to retrieve 2294
		// Wikipedia informations
		int getSizeofDecoder = annotations.size();
//		System.out.println("    [Document Mutual Disambiguisation ( Lucene=" + lucenPathofWikipediaIndex + ") (rank used=" + numberOfCandidates + ")]" );
		System.out.println("    [Document Mutual Disambiguisation ( Lucene=" + lucenPathofWikipediaIndex + ") (rank used=" + numberOfCandidates + ")]" );
		int listindex = 0;
		countModifications = 0;
		
		for(int h=0; h <getSizeofDecoder; h++){
			
		
			
			//---------------------------------
			//
			//---------------------------------
			if (annotations.getSurfaceFormatpos(h) != null){
				
				if (verbose) System.out.println(" --->" + annotations.getSurfaceFormatpos(h)+ "   [" + annotations.getMetadatakey(h) + "] [" + annotations.getMetadataKeyRanked(1,h) + "] [" + annotations.getMetadataKeyRanked(2,h) +"] and more ...");
			
				//-------------------------------
				// collect all metadata
				//-------------------------------
				// rank 1
				if (annotations.getMetadatakey(h)!= null)  {
					
					metalinks.put(annotations.getMetadatakey(h), 1);
					if ( ! annotations.getMetadatakey(h).contentEquals("NIL") ) candidates.put(annotations.getMetadatakey(h),annotations.getSurfaceFormatpos(h));
					listindex++;
				}
				
				// rank 2 to n
				for (int rank = 1; rank < numberOfCandidates ; rank ++ ){
					
					if (annotations.getMetadataKeyRanked(rank,h)!= null) {
						metalinks.put(annotations.getMetadataKeyRanked(rank,h), 1);
						if (fullCompare) 
							{
							if ( ! annotations.getMetadataKeyRanked(rank,h).contentEquals("NIL") ) candidates.put(annotations.getMetadataKeyRanked(rank,h), annotations.getSurfaceFormatpos(h));
							}
						listindex++;
					}
					
				}
				
			}
			
		}
		
		
		if ( listindex < limitOfcollection ){
		
			// ----------------------------------
			// Collect document information
			// ----------------------------------
	
			// activate Wikipedia extractor and collect the content
			
			System.out.println("    Collecting " + listindex + " Wikipedia Documents and links ...");
			// collect all the documents according to the hash
			for (String mapKey : metalinks.keySet()) {
				wpextract.getWikipediaDocument(mapKey);
				if (verbose)  System.out.println("    			Collecting " + mapKey);
			}
			
			// ----------------------------------
			// Disambiguation 
			// on Wikimeta annotations
			// ----------------------------------
			for(int h=0; h <getSizeofDecoder; h++){
				
				if (annotations.getSurfaceFormatpos(h) != null){
					
					if (verbose) System.out.println("-------------------------------------------------------");
					if (verbose) System.out.println("Line:" + h );
					
					// store rank 0
					String[] key = new String[numberOfCandidates];
					// key[0] = annotations.getMetadatakey(h);
					
					// rank 2 to n
					for (int rank = 0; rank < numberOfCandidates ; rank ++ ){
						//System.out.println(rank + " " + annotations.getMetadataKeyRanked(rank,h)); 
						key[rank] = annotations.getMetadataKeyRanked(rank,h);
					}
					
					//-------------------------------------
					//
					// rerank according to mutual relation
					//
					//-------------------------------------
					String mention = annotations.getSurfaceFormatpos(h);
					String keyref = annotations.getMetadatakey(h);
					String[] keyReturned = this.disambigLine(key, keyref, mention, candidates, h);

					if (verbose) System.out.println("               -Apply reranking...");
					// set annotation object according to reranking for the concerned line
					for (int rank = 0; rank < numberOfCandidates ; rank ++ ){
						annotations.setMetadatarankedWithKey(h, keyReturned[rank], rank);
					}
					
					//---------------------------------
					//
					// rerank according to matching key
					//
					//----------------------------------
					boolean searchmatch = true;
					String[] matchkey = new String[numberOfCandidates];
					//---------------------------------
					// avoid same keys (key:Kevin Carter / Kevin Carter (American football) / Kevin Carter (song))
					// if only 2 keys are the same 
					//---------------------------------
					for (int o=0; o < numberOfCandidates ; o++){
						
						if (key[o] != null ){
						
							int offsetp = 0;
							matchkey[o] = key[o]; // default value 
							
							// remove parentheses
							if ( key[o].contains("(")){ 
								offsetp = key[o].indexOf("(");
								try {
									matchkey[o] = key[o].substring(0, offsetp - 1);
									if (verbose)  System.out.println("               Potential Rematch : " + key[o] + "--->" + matchkey[o]);
								}catch (Exception e){} // try catch for some rare and specific cases like "(You Make Me Feel Like) A Natural Woman"
								
							}
							
						}else{
							// to avoid null offset errors, put a default value
							matchkey[o] =  " ";
						}
						// do not use ABR for rematch
						// -Rematch : OSI (band)--->OSI
						if (matchkey[o].matches("[A-Z0-9]+")){
							if (verbose)  System.out.println("                 -Dont match (ABR): " + matchkey[o]);
							matchkey[o] =  " ";
						}
					}
					// if only 2 are equal, do not apply this test
					if ( matchkey[0].equals(matchkey[1]) || matchkey[0].equals(matchkey[2]) || matchkey[2].equals(matchkey[1])) 
					{ 
						searchmatch = false;
						if (verbose) System.out.println("                 -Searchmatch false, 2 or more equals");
					}
					//--------------------------
					// manage the search
					//---------------------------
					if (searchmatch){
						int firstrank = 0; int reranked = 0;
						for (int p = 0; p < numberOfCandidates; p ++)
						{
							if (key[p] != null){
								String metakeytomatch = key[p].toLowerCase();
								metakeytomatch = metakeytomatch.replaceAll(",", " ");
								metakeytomatch = metakeytomatch.replaceAll("[ ]+", " ");
								String[] sizetomatch = metakeytomatch.split(" ");
								// match direct regex only if there are more than one word (do not match single word -> too ambiguous)
								if ( documentCleaned.contains(metakeytomatch) && sizetomatch.length > 1 ){
									firstrank = p;
									reranked++;
								}
								// match each element of a regex
							}
						}
						if (reranked == 1 && firstrank !=0){
							String oldkey = annotations.getMetadatakey(h);
							annotations.setMetadataWithKey(h, key[firstrank]); // replace key 0
							annotations.setMetadatarankedWithKey(h, oldkey, firstrank); // transfer old key 0 to moved key position
							countMatchKeyModifications++; // update count
							if (verbose) System.out.println("               Reranking by metakey after rematch..." + oldkey + " to " + key[firstrank] );
						}
					}
					
				} 
			}
			
	    }
		
		System.out.println("    Permutations done : " + countModifications + " (match by key: " + countMatchKeyModifications + " )");
		System.out.println("");
	
		// clean wikipedia extractor
		wpextract.clearobject();
		
		// clear local objects
		candidates.clear();
		metalinks.clear();
		
		return annotations;
	}
	
	/**
	 * 
	 * 
	 * 
	 * @param key
	 * @param keyref
	 * @param mention
	 * @param candidates
	 * @param line
	 * @return
	 */
	private String[] disambigLine(String[] key, String keyref, String mention, HashMap<String, String> candidates, int line){
	
		String[] newkeys = key;
		int bestScore = 0;
		String bestkey = key[0];
		int numofbest = 0;
			
		if (verbose) System.out.println("Keyref:" + keyref + " - AllKeys: " + key[0] + " / " + key[1] + " / " + key[2] + " Candidates: " + candidates.size());
		
		for (int s = 0; s  < key.length ; s++){
			
			if (  key[s] != null )
			{
			
				if (verbose) {
					System.out.println("--------------------------------------------------------------------------");
					System.out.println(" * Testing:" + key[s]);
				}
				
				int accumulateScore = 0;
				//----------------------------------------------------
				// calculate the score for links to the other
				//----------------------------------------------------
				accumulateScore = searchLinksToTheOthers(key[s], candidates);
				if (verbose) System.out.println("              Links to the others:" + accumulateScore);
				
				//----------------------------------------------------
				// calculate the score for common links and cats
				//----------------------------------------------------
				for (String mapKey : candidates.keySet()) {
					
					if (keyref != null){
						if ( ! mapKey.contentEquals(keyref) ){
										
							// Calculate the score for common links and common cats
							int scoreLinks = searchCommonsLinks(key[s], mapKey);
							int scoreCats = searchCommonsCats(key[s], mapKey);
							accumulateScore = accumulateScore + scoreLinks + scoreCats;
							if (verbose) System.out.println("              Against:" + mapKey + " " + scoreLinks + " " + scoreCats);
						}
					}
					
				}
				if (verbose){
					System.out.println(" * Score:" + accumulateScore);
					System.out.println("--------------------------------------------------------------------------");
					
				}
				// memory of the best score
				if (accumulateScore > bestScore){
					 bestScore = accumulateScore;
					 bestkey = key[s];
					 numofbest = s;
				}
			}
			
		}
		
		// permutation if the best has changed
		if ( numofbest != 0){
			String oldbest = newkeys[0]; // save best old one that will be permuted 
			newkeys[0] = bestkey; // replace by new one  
			newkeys[numofbest] = oldbest; // permut old best key
			
			// verbose
			if (verbose) System.out.print("               -Reranking done:");
			for (int o = 0; o < newkeys.length; o++){
				if (verbose) System.out.print(newkeys[o] + "/");
			}
			if (verbose) System.out.println("");
			
			// count reranking operations
			countModifications++;
			
		}
		
		return newkeys;
	}
	
	
	/**
	 * 
	 * Part 1 of semantic disambiguation check if a specific reference has
	 * links to other entities in the list of the candidates
	 * 
	 * @param metaRef
	 * @param candidates
	 * @return
	 */
	private int searchLinksToTheOthers(String metaRef, HashMap<String, String> candidates){
		
		// score accum
		int scoreaccum = 0;
		
		// get all the links of the ref
		HashMap<String, Integer> metareflinks = wpextract.InternalLinks.get(metaRef);
		
		// calculate the score for common links and cats
		// for all the candidates names ---> a metadatakey indexed in candidates
		for (String mapKey : candidates.keySet()) {
			
				// if it is not the same key (do not count against himself)
				if ( ! mapKey.contentEquals(metaRef) && mapKey != null ){
					
					if (verbose)  System.out.println(metaRef + "---->" + mapKey );
					
					//for (String keyofref : metareflinks.keySet()) {
					//	if (verbose) System.out.println("                           key in " + metaRef + "   " + keyofref);
					//}
					
					// if links from the reference are in the candidate list
					try{
						if (metareflinks.containsKey(mapKey.toLowerCase())){
							scoreaccum = scoreaccum +  linkToTheOthersWeight;
							if (verbose) System.out.println("                            External Link from:" + metaRef + " point to candidate :" + mapKey );
						}
					}catch (Exception e){
						
					}
					
				}
		} 
		return scoreaccum;
	}
	
	
	/**
	 * 
	 * Compare the selected metadata against one other in the document
	 * 
	 * @param metaRef
	 * @param metaCompare
	 * @return
	 * 
	 */
	private int searchCommonsLinks(String metaRef, String metaCompare){
		
		// ----------------- manage the link comparison
		// get all the links of the reference
		HashMap<String, Integer> metareflinks = wpextract.InternalLinks.get(metaRef);
		// get all the links of the compared
		HashMap<String, Integer> comparedlinks = wpextract.InternalLinks.get(metaCompare);
		int countCommonsLinks = 0;
		
		// count in the hash the common categories and link
		if (verbose) System.out.println("------------------------------------------");
		
		try{
			for (String mapKey : metareflinks .keySet()) {
				if (comparedlinks.containsKey(mapKey)){
					countCommonsLinks =  countCommonsLinks + mutualLinksWeight;
					if (verbose) System.out.println("                            L:" + mapKey);
				}
			}
		}catch (Exception e){
			
			if (verbose) System.out.println("Exception " + e);
			
		}
		
		return countCommonsLinks;
	}
	
	/**
	 * 
	 * 
	 * @param metaRef
	 * @param metaCompare
	 * @return
	 */
	private int searchCommonsCats(String metaRef, String metaCompare){
		
		// ----------------- manage the link comparison
		// get all the links of the reference
		HashMap<String, Integer> metareflinks = wpextract.InternalCategories.get(metaRef);
		// get all the links of the compared
		HashMap<String, Integer> comparedlinks = wpextract.InternalCategories.get(metaCompare);
		int countCommonsLinks = 0;
		
		try{
			// count in the hash the common categories and link
			for (String mapKey : metareflinks .keySet()) {
				if (comparedlinks.containsKey(mapKey)){
					countCommonsLinks = countCommonsLinks + mutualCatsWeight;
					if (verbose) System.out.println("                            C:" + mapKey);
				}
			}
		}catch (Exception e){
			
			if (verbose) System.out.println("Exception " + e);
			
		}
		
		return countCommonsLinks;
	}
	

	
}
