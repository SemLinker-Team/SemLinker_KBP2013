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

import semkit.extractor.WikiMetaXMLDecoder;
import kbp2013.managedocuments.DocumentNormalizer;
import kbp2013.tools.Logging;


/**
 * 
 * Retrieve the surface form annotated by Wikimeta
 * at the given position of the query
 * 
 * @author ericcharton
 *
 */
public class RetrieveExactMentionAnnotation {

	
	/**
	 * 
	 * Retrieve the annotation for the marked mention according to its x and y position
	 * 
	 * 
	 * @param annotations An annotation object from Wikimeta or any annotation engine
	 * @param Qname The original query
	 * @param deb Beginning of the mention
	 * @param fin End of the mention
	 * @param originalText Source text of the original document before pre-processing
	 * @return
	 */
	public static int getAnnotationAtPosition(WikiMetaXMLDecoder annotations, 
                                                    String Qname, 
                                                    int deb, int fin, 
                                                    String originalText,
                                                    Logging log){
		
		originalText = DocumentNormalizer.PrepareDocForMentionLocation(originalText);
		
		log.writeLog("    [Retrieve Original Annotation]");
		
		// define variables
		int span = 40; // what to get around the entity
		int adjustL = 300; // increase the search 
		
		// adjust the beginning position 
		int a = deb - adjustL; // beginning span
		int lenght = fin - deb + 1; // size of target mention
		if ( a < 0 ) { a= 0; }
		
		String thecontent = originalText;
		
		try{
			
		
			// test around the position to find the mention
			while ( ! thecontent.substring(a, a + lenght ).contains( Qname ) ){
			
				a++;
				// security if the exact mention location was not found
				if ( a >= deb ) {
					log.writeLog("    --->Search at " + deb + " " + fin + " " + Qname + " a:" + a);
					
					break;	
				}
			}
		}catch (Exception e){
			log.writeLog("--->Exact mention not found (Out of Range) [error]");
		}
			
		// explore all the annotation array
		int posOfAnnotation = 0;
		int charSize = 0;
		
		// collect context around identified NE
		int cVarB= a - span ;
		int cVarE =  a + lenght + span;
		if (cVarB < 0) cVarB = 0;
		if (cVarE > thecontent.length()) cVarE = thecontent.length() -1;
		String theContext = "";
		
		int viterator= 0;
		
		try{
			
			// simple chain of original context
			theContext = thecontent.substring( cVarB, cVarE);
			// context for testing trigrams
			String theContextPrepared = theContext.replaceAll("\\.", " \\. ");
			theContextPrepared = theContextPrepared.replaceAll("\n", " ");
			theContextPrepared = theContextPrepared.replaceAll("\\,", " \\, ");
			theContextPrepared = theContextPrepared.replaceAll(";", "  ; ");
			theContextPrepared = theContextPrepared.replaceAll(":", "  : ");
			theContextPrepared = theContextPrepared.replaceAll("'", "  ' ");
			theContextPrepared = theContextPrepared.replaceAll("\\\"", "  \\\" ");
			theContextPrepared = theContextPrepared.replaceAll("\\)", " \\) ");
			theContextPrepared = theContextPrepared.replaceAll("\\(", " \\( ");
			theContextPrepared = theContextPrepared .replaceAll("[ ]+", " ");
			
			// if mention longer than one word, divide it to find the first one
			// ex  [Tammy Faye Messner]
			String[] ArrayAccordingToMention = Qname.split(" ");
			
			//------------------------------------
			// Search offset of mention with 3-grams
			// strict
			//------------------------------------
			for (viterator= 0; viterator < annotations.size(); viterator++)
			{
				// locate first word of original mention (ex -> Detroit)
				if ( ArrayAccordingToMention[0].contentEquals(annotations.getwordatpos(viterator)) )
				{				
					
						if (viterator < annotations.size() - 3){
						
							// build the 3 gram sequence to search after pos
							String totest =  annotations.getwordatpos(viterator+1) + " " + annotations.getwordatpos(viterator+2)+ " " + annotations.getwordatpos(viterator+3) ;
							// Search the ngram after
							if (theContextPrepared.contains(totest))
							{
								log.writeLog("    --->Marked(R):" + annotations.getwordatpos(viterator) + " WikimetaOffset:" + viterator + " CharOffset:" + charSize + "  Ngram:" + annotations.getwordatpos(viterator) + "-->" + annotations.getwordatpos(viterator+1) + "/" + annotations.getwordatpos(viterator+2)+ "/" + annotations.getwordatpos(viterator+3));
								posOfAnnotation = viterator;		
								break;
							}
						}
						
						if ( viterator > 1){
							// build the 3 gram sequence to search before pos
							String totest = annotations.getwordatpos(viterator - 2) + " " + annotations.getwordatpos(viterator - 1)+ " " + annotations.getwordatpos(viterator) ;
							// search the ngram before
							if (theContextPrepared.contains( totest))
							{
								log.writeLog("    --->Marked(L):" + annotations.getwordatpos(viterator) + " WikimetaOffset:" + viterator + " CharOffset:" + charSize + "  Ngram:" + annotations.getwordatpos(viterator) + "-->" + annotations.getwordatpos(viterator+1) + "/" + annotations.getwordatpos(viterator+2)+ "/" + annotations.getwordatpos(viterator+3));
								posOfAnnotation = viterator;		
								break;
							}
						}
						
					
				}
				
				charSize = charSize + annotations.getwordatpos(viterator).length() + 1;
			}
	
			
			
			
			//------------------------------------
			// if not found with 3-grams
			// soften the constraint
			//------------------------------------
			if (posOfAnnotation == 0){
				
				charSize = 0;
				
				for (viterator= 0; viterator < annotations.size(); viterator++)
				{
					// locate first word of original mention (ex -> Detroit)
					if ( ArrayAccordingToMention[0].contentEquals(annotations.getwordatpos(viterator)) )
					{				
						// utilize trigram with the 2 words following the mention
						// to confirm its position. Ex Ngram:Detroit-->on/Petr
						if (viterator < annotations.size() - 3)
						{
							// other detectors are less reliable
							if (theContext.contains(annotations.getwordatpos(viterator+1)) &&  theContext.contains(annotations.getwordatpos(viterator+2))  &&  theContext.contains(annotations.getwordatpos(viterator+3)) )
							{
								
								log.writeLog("    --->Marked(R):" + annotations.getwordatpos(viterator) + " WikimetaOffset:" + viterator + " CharOffset:" + charSize + "  Ngram:" + annotations.getwordatpos(viterator) + "-->" + annotations.getwordatpos(viterator+1) + "/" + annotations.getwordatpos(viterator+2)+ "/" + annotations.getwordatpos(viterator+3));
								posOfAnnotation = viterator;		
								break;
							}	
						}
					}
					charSize = charSize + annotations.getwordatpos(viterator).length() + 1;
					
				}
				
			}
			
		}catch (Exception e){
			log.writeLog("    --->Marked problem [error]" + e + " " + viterator);
		}
		
		
		
		
		
		//-------------------------------------------
		// look for pos annotation from wikimeta
		// sometimes before (for example -> Kennedy = mention ----> Caroline Kennedy found in text
		//-------------------------------------------
		if (annotations.getMetadatakey(posOfAnnotation) == null) {
			
			// Real position -1 (2-grams mention)
			if       (annotations.getMetadatakey(posOfAnnotation -1) != null && annotations.getNELabel(posOfAnnotation -1).contentEquals(annotations.getNELabel(posOfAnnotation )) )
			{
					posOfAnnotation--;
					
			// Real position -2 (3-grams mention) -> very few cases
			}
			else if (annotations.getMetadatakey(posOfAnnotation -2) != null && annotations.getNELabel(posOfAnnotation -2).contentEquals(annotations.getNELabel(posOfAnnotation )) )
			{
					posOfAnnotation-=2;
					
			}
		}
			
		//-------------------------------------------
		// Verbose
		//-------------------------------------------
		String contextcrRemoved = theContext.replaceAll("\n", " ");
		log.writeLog("    --->Context:" + contextcrRemoved);
		log.writeLog("    --->MentionSF:" + annotations.getSurfaceFormatpos(posOfAnnotation));
		log.writeLog("    --->Annotation:" + annotations.getMetadata(posOfAnnotation) + " Pos:" + posOfAnnotation);
		log.writeLog("");
		
		// put 0 if not enough information
		if (annotations.getSurfaceFormatpos(posOfAnnotation) == null ){
			posOfAnnotation = 0;
		}
		
		return posOfAnnotation;
		
	}
	
}
