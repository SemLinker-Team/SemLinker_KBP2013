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

import semkit.extractor.WikiMetaXMLDecoder;

import configure.NistKBPConfiguration;

/**
 * 
 * This is a simple coreference detector and resolution tool utilized to
 * correct coreference clusters for PERS, ORG and GPE.
 * It is applied to a WikiMetaXMLDecoder object.
 * 
 * @author ericcharton
 *
 */
public class SimpleCoreferenceDetector {

	static boolean verbose = false; // verbose flag and default configuration
	
	private static int countModifications = 0; // count the modifications of coref applied
	
	/**
	 * 
	 * Simple constructor
	 * 
	 * 
	 */
	public SimpleCoreferenceDetector (){
		
		
	}
	
	/**
	 * 
	 * Verbose constructor
	 * 
	 * @param config
	 */
	public SimpleCoreferenceDetector (NistKBPConfiguration config){
		
		verbose = config.verbose;
		
	}
	
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * @param annotations
	 * @return
	 */
	public static  WikiMetaXMLDecoder applyCoreferenceCorrection(WikiMetaXMLDecoder annotations){
		
		System.out.println("    [Simple Coreference Correction]");
		
		countModifications = 0;
		
		//----------------------------
		// correct PERS mentions
		//----------------------------
		if (verbose == true) System.out.println("     [PERS Coreferences]");
		
		// -------------------------------------
		// Attach a Title mention
		//	  query EL_ENG_00049
		//    <HEADLINE> URGENT  Jones wins women's 100m breaststroke world ti
		//    Followed by Leisel Jones
		//	  Urgent: Annan, Carter cancel planned visit to Zimbabwe
		// 	  Followed by Jimmy carter and Kofi Annan
		
		HashMap<String, Integer>   excludeDetectedMention = new HashMap<String, Integer>(); // do not attach 2 times the same mention
		
		for (int i = 0; i < annotations.size(); i++){
			
			// look for first mention
			if (annotations.getNELabel(i).contains("PER") && annotations.getSurfaceFormatpos(i)!=null ){
				
				String[] sizeofMentionName = annotations.getSurfaceFormatpos(i).split(" ");
				String originalmention = annotations.getSurfaceFormatpos(i).toLowerCase();
				
				// if length of first mention is 1
				if (sizeofMentionName.length == 1 ){
					
					// look for longest attachment
					for (int j = i +1; j < annotations.size(); j++)
					{
						
								if ( annotations.getSurfaceFormatpos(j)!=null  && annotations.getNELabel(j).contains("PER") ){
									
											 String[] sizeofMentionNameToRefer = annotations.getSurfaceFormatpos(j).split(" ");
											 
											 if (sizeofMentionNameToRefer.length > 1){
												 
												 // verify if not excluded previously
												 if ( !  excludeDetectedMention.containsKey(originalmention)){
												 
													 // test if the short mention is included in a longer mention
													 if ( annotations.getSurfaceFormatpos(j).toLowerCase().contains(originalmention )){
														 
														 if (verbose == true) System.out.println("     -Attaching a title mention:" + originalmention + "/" + i + " " + annotations.getSurfaceFormatpos(j) + "/" + j);
														 
														 try{
														 if (! annotations.getMetadata(i).contentEquals(annotations.getMetadata(j))){
																 countModifications++;
														 }
														 }catch(Exception E){}
														 
														 annotations.setMetadataWithUri(i, annotations.getMetadata(j)); // attach the mention
														 excludeDetectedMention.put(originalmention, 1); // exclude the mention for next iteration
														 
														 break; // attachment done, process can stop
														 
													 }
												 }
											 }
								}
						
					}
					// break; 
				}
				
				
			}
		}
		
		
		
		// -------------------------------------
		// A mention that begins with FNAM: correct its successors with only family name
		// ex William Clarke ---> Clarke
		try{
			
				for (int i = 0; i < annotations.size(); i++){
					
					// System.out.println(i + " " + annotations.getSurfaceFormatpos(i));
					
					if (annotations.getNELabel(i).contains("PER") && annotations.getSurfaceFormatpos(i)!=null){
						
						// check the size (more than 1 word)
						String[] sizeofMentionName = annotations.getSurfaceFormatpos(i).split(" ");
						
						// first mention
						//			A)verify first name is present
						//			B)verify mention is longer than one word (no family name alone)
						// if (  annotations.getPOSatpos(i).contains("FNAM") && sizeofMentionName.length > 1){
						
						if (  
								( annotations.getPOSatpos(i).contains("FNAM") || 
										( annotations.getPOSatpos(i).contains("NAM") || annotations.getNELabel(i).contains("PERS.HUM") )
								) 
								&& sizeofMentionName.length > 1
							)
						{
						// particular case -> no FNAM first
						// 284--Austin NAM PERS.HUM --->Austin Sperry   [http://wikimeta.com/wapi/display.pl?query=Austin Sperry&search=EN] [null] null
						// 285--Sperry NAM PERS.HUM
								
							// save the first name
							String firstName = annotations.getwordatpos(i);
							// Collect the family name with an array -> assumed the family name is the last one
							String[] fullName = annotations.getSurfaceFormatpos(i).split(" ");
							String familyName = fullName[fullName.length-1].toLowerCase();
							// Save the MetadataKey or NORDF
							String metaKey = annotations.getMetadata(i);
							
							if (verbose == true) System.out.println("     -Found a complete mention:" + annotations.getSurfaceFormatpos(i) + " {" + familyName + " " + firstName + "} " + " " + metaKey + " " + i);
								
							// update the i 
							int i2 = i + fullName.length;
							if (i2 >= annotations.size()) i2 = annotations.size(); // out of array security
							
							int j = 0;
							try{
								// now search the single mentions that follow William Clark -> Clarck
								for (j = i2; j < annotations.size(); j++){
									
									// boolean flagnext = false;
									if ( annotations.getSurfaceFormatpos(j) != null){
										
										// Utilize this string to verify if other annotations contain a first name
										String[] fullNameCorefCandidate = annotations.getSurfaceFormatpos(j).split(" ");
										
										// if mention longer than one word, stop coref detection process
										if ( annotations.getSurfaceFormatpos(j).toLowerCase().contains(familyName) && fullNameCorefCandidate.length > 1){
												// System.out.println("           End of Coref:" + j);
												break;
										}
										
										// if annotation is potentially a coreference
										//		A) contains exactly the family name
										// 		B) is one word long
										if ( annotations.getSurfaceFormatpos(j).toLowerCase().contentEquals(familyName) && fullNameCorefCandidate.length == 1)
										{
											
											if (verbose == true) System.out.println("           Coref:" + annotations.getSurfaceFormatpos(j) + " " + j);
											if (! annotations.getMetadata(i).contentEquals(annotations.getMetadata(j))){
												 countModifications++;
											}
											
											annotations.setENlabel(j, "PER");
											annotations.setMetadataWithUri(j, metaKey);
											
											
										}
										// if annotation 
										// 			A) one word long (first name alone or family name alone)
										// 			B) contained in original sequence 
										else if ( annotations.getSurfaceFormatpos(i).toLowerCase().contains(annotations.getSurfaceFormatpos(j).toLowerCase()) && fullNameCorefCandidate.length == 1)
										{
											
											if (verbose == true) System.out.println("           Coref:" + annotations.getSurfaceFormatpos(j) + " " + j);
											if (! annotations.getMetadata(i).contentEquals(annotations.getMetadata(j))){
												 countModifications++;
											}
											
											annotations.setENlabel(j, "PER");
											annotations.setMetadataWithUri(j, metaKey);
						
										}
									}
									
								}
								
							}catch (Exception e){
								System.out.println(e + " j:" + j + " i2:" + i2 + " i:" + i);
							}
							i = i2; // update loop
							
						}
						
					}
			
				}
		}catch (Exception e){
			System.out.println(e);
		}
		
		
		
		
		//----------------------------
		// correct LOC mentions
		//----------------------------
		if (verbose == true) System.out.println("     [LOC Coreferences]");
		
		// A longer structure like Paris, Texas: correct its successors
	    // --206:1092:http://wikimeta.com/wapi/display.pl?query=Portland, Maine&search=EN:Portland, Maine:LOC.ADMI(searched:portland expanded:portland)(mention:Portland , Maine) E0594272 [null null]
	    // --238:1252:http://wikimeta.com/wapi/display.pl?query=Portland, Maine&search=EN:Portland, Maine:LOC.ADMI(searched:portland expanded:portland)(mention:Portland) E0594272 [E0471856 E0336161]
	    // --251:1306:http://wikimeta.com/wapi/display.pl?query=Portland, Maine&search=EN:Portland, Maine:LOC.ADMI(searched:portland expanded:portland)(mention:Portland) E0594272 [E0471856 E0336161]
		
		// Look for a first mention of structure Town , State
		for (int i = 0; i < annotations.size(); i++){
			
			
			if (annotations.getNELabel(i).contains("LOC") && annotations.getSurfaceFormatpos(i)!=null ){
				
				String[] ArraywithMentionName = annotations.getSurfaceFormatpos(i).split(" ");
				String ENAtPos = annotations.getNELabel(i);
				String metaKey = annotations.getMetadata(i);
				
				// if (ArraywithMentionName.length > 2 ){
				// modification for annotation errors (no metadata =>  not ref)
				if (ArraywithMentionName.length > 2 && ! annotations.getMetadata(i).contains("NIL")){
					
					// control structure
					if (ArraywithMentionName[0].matches("[A-Za-z]+") && ArraywithMentionName[1].contentEquals(",") && ArraywithMentionName[2].matches("[A-Za-z]+") ){
						
						String locName = ArraywithMentionName[0];
						if (verbose == true) System.out.println("     -Found a complete mention:" + annotations.getSurfaceFormatpos(i) + " " + metaKey + " " + i);
						
						// search the next mention with same name after
						// for (int j =  i + ArraywithMentionName.length ; j  < annotations.size(); j++){
						for (int j =  0 ; j  < annotations.size(); j++){
								
							if (annotations.getSurfaceFormatpos(j) != null){
								if (annotations.getSurfaceFormatpos(j).contentEquals(locName)){
									
									if (verbose == true) System.out.println("           Coref:" + annotations.getSurfaceFormatpos(j) + " " + j);
									if (! annotations.getMetadata(i).contentEquals(annotations.getMetadata(j))){
										 countModifications++;
									}
									
									annotations.setENlabel(j, ENAtPos);
									annotations.setMetadataWithUri(j, metaKey);

								}
							}
						}
						
					}
				}
			}
		}
		
		//----------------------------
		// correct ORG mentions
		//----------------------------
		// 139:Managing query EL_ENG_00139 [Daily News][daily news] APW_ENG_20070721.0837.LDC2009T13 910 919
		//  --74:376: (mention:aspen daily news)      {E0710920 [null null])}   (key:Aspen Daily News / null / null) 
		//  --151:751 (mention:daily news)            {E0626311 [E0504008 E0359481])}   (key:The Daily News (Palo Alto) / Daily News (New York) / The Galveston County Daily News) 
		// Algo :
		// Look for an org mention with more than one word
		// search consecutive mentions with ORG && included in first mention
		for (int i = 0; i < annotations.size(); i++){
			
			
			if (annotations.getNELabel(i).contains("ORG") && annotations.getSurfaceFormatpos(i)!=null ){
				
				String[] ArraywithMentionName = annotations.getSurfaceFormatpos(i).split(" ");
				int wordsofmention = ArraywithMentionName.length;
				String referenceMention = annotations.getSurfaceFormatpos(i);
				String metakey = annotations.getMetadatakey(i);
				
				if (wordsofmention > 1)
				{
					// search 
					//    A) next mentions with a name included in the reference && ORG Tag
					for (int j =  i +1 ; j  < annotations.size(); j++){
							
						if (annotations.getSurfaceFormatpos(j) != null){
							
							// verify if the coreference chain is broken by an other word
							
							// verify if the ORG mention is included in the reference mention
							if (referenceMention.contains(annotations.getSurfaceFormatpos(j)) && annotations.getNELabel(j).contains("ORG")){
								
								if (verbose == true) System.out.println("           Coref:" + annotations.getSurfaceFormatpos(j) + " " + j);
								
								// set the count
								try{
									if (! annotations.getMetadata(i).contentEquals(annotations.getMetadata(j))) countModifications++;
								}catch (Exception e){}
								
								// replace the annotation
								annotations.setMetadataWithUri(j, metakey);
	
							}
						}
					}
				}
				
			}
		}
		
		
		
		
		System.out.println("    Modifications applied:" + countModifications);
		System.out.println("");
		
		return annotations;
	}
	
}