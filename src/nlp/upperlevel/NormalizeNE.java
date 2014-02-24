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


/**
 * 
 * Normalize NE in a document.
 * Usually applied after the disambiguation and coreference process
 * correct all NE in the doc (this is to have better marks of GPE).
 * 
 * @author ericcharton
 *
 */
public class NormalizeNE {

	
	boolean verbose= true; // display meaningful and very interesting information
	
	
	/**
	 * 
	 * Constructor
	 * 
	 */
	public NormalizeNE(){
		
	}
	
	/**
	 * 
	 * Apply NE correction on a document
	 * 
	 * @param annotations a WikiMetaXMLDecoder object
	 * @return the same WikiMetaXMLDecoder object with corrected NE
	 */
	public  WikiMetaXMLDecoder rerankNE(WikiMetaXMLDecoder annotations ){
		
		System.out.println("    [NE normalization]");
			
		
		HashMap<String, Integer> keymetadatatoexclude = new HashMap<String, Integer>(); // the content retrieved according to the page key
		
		for(int h=0; h < annotations.size() ; h++){
			
			if ( annotations.getSurfaceFormatpos(h)!= null){
				
				if ( annotations.getMetadatakey(h) != null && ! annotations.getMetadatakey(h).contains("NIL")){
					
					HashMap<String, Integer>  NEcandidates = new HashMap<String, Integer>(); // the content retrieved according to the page key
					
					String referencemetadata = annotations.getMetadatakey(h);
					
					String refNE = "";
					int scorerefNE = 0;
					
					// if this metadata has not been processed
					if ( !  keymetadatatoexclude.containsKey(referencemetadata) ){
					
							// look for all NE corresponding to this metadata, and rank them
							for(int i=h + 1; i < annotations.size() ; i++){
								
								if  ( annotations.getMetadatakey(i) != null ){
									
									if ( annotations.getMetadatakey(i).contentEquals(referencemetadata) ){
									
										String NEtostore = annotations.getNELabel(i);
										
										if (NEtostore.contains("PERS") || NEtostore.contains("ORG") || NEtostore.contains("LOC") ){
											
											if (NEcandidates.containsKey(NEtostore)){
												int currentscore = NEcandidates.get(NEtostore);
												NEcandidates.put(NEtostore, currentscore + 1);
											}else{
												NEcandidates.put(NEtostore, 1);
											}
											
											// update the best NE
											if ( NEcandidates.get(NEtostore) > scorerefNE) {
												scorerefNE = NEcandidates.get(NEtostore);
												refNE = NEtostore;
											}
											
										}
									}
									keymetadatatoexclude.put(referencemetadata, 1);
								}
								
							}
							// replace final NE
							if (! refNE.contentEquals("")){
								if ( verbose ) System.out.println("        *for key " + referencemetadata + " Normalize with " + refNE);
								for(int z=0; z < annotations.size() ; z++){
									if (annotations.getMetadatakey(z) != null){
										if ( annotations.getMetadatakey(z).contains(referencemetadata)){
											// to find the span and affect the new label to all
											String originalMention = annotations.getNELabel(z);
											int spanMarker = z;
											// 7 is the maximum number of possible mentions
											for (int w = 0; w < 7; w++){
													annotations.setENlabel(z + w, refNE);
													int pos = z + w + 1;
													if (pos >= annotations.size()  ) break;
													if ( ! annotations.getNELabel(pos).equals(originalMention) ) break;
											}
										}
									}
								}
							}
							// exclude metadata from the process
							keymetadatatoexclude.put(referencemetadata, 1);
						}
				}
				
			}
			
		}
		
		System.out.println("");
		return annotations;	
	}
	
	
	
}

