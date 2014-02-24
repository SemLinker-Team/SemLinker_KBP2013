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

/**
 * 
 * Expand query using the document annotations object
 * 
 * 
 * @author ericcharton michelgagnon
 *
 */
public class ExpandQuery {

	/**
	 * 
	 * Transform a long sequence into an abbreviation to compare
	 * 
	 * @param name
	 * @return
	 */
	private static String abbreviate(String name){
		
            String abbreviatedName = name;
         
            try{
            	abbreviatedName = abbreviatedName.replaceAll(" of ", " "); // Remove some noise
                abbreviatedName = abbreviatedName.replaceAll(" and ", " "); // Remove some noise
                abbreviatedName = abbreviatedName.replaceAll("[a-z ]+", ""); // Just keep cap letters (remove spaces and lowercases)   
            }catch(Exception e){
            	
            }
            
            return abbreviatedName;
    }
    
	/**
	 * 
	 * Get an expansion of the original query by investigating the annotation found at its position or by expanding an abbreviation
	 * 
	 * @param annotations
	 * @param QnameNormalized
	 * @param Qname
	 * @param linkAtPos
	 * @param activatePLF
	 * @return
	 */
	public static String getExpansion(WikiMetaXMLDecoder annotations , String QnameNormalized, String Qname, int linkAtPos, boolean activatePLF){
		
            System.out.println("    [Query Expansion]");

            // ---------------------------------------------------------
            // Document based query expansion 
            //   - If the mention found after document annotation is larger than 
            //     the one contained in QnameNormalized, the expanded mention 
            //     is kept
            // ---------------------------------------------------------

            // if the mention was found at position
            if (linkAtPos > 0) {
            	
            	//--------------------------------------
            	// If the mention found by Wikimeta at 
            	// declared position is longer than the 
            	// query item, we keep the Wikimeta mention.
            	//--------------------------------------
                if (annotations.getSurfaceFormNormalizedatpos(linkAtPos).length() > QnameNormalized.length()) {
                    System.out.println("    Query Expanded to:" + annotations.getSurfaceFormNormalizedatpos(linkAtPos) + " !!!\n");
                    return annotations.getSurfaceFormNormalizedatpos(linkAtPos);
                }
                
                
                //--------------------------------------
                // Try to locate an expansion according
                // to the key and the length of mentions
                // related
                //--------------------------------------
                String metakeyAtPos = annotations.getMetadatakey(linkAtPos); // metadata at position
                String expandCandidate = QnameNormalized; // original mention put in expanded candidate
                	
                 //    --- search an antecessor with same family name and first name
                 for (int h = 0; h < linkAtPos; h++) {
                        // if 
                    	//   B) contains the original mention
                    	//   C) has the same metakey
                    	//   D) is longer than the original mention
                    	if (annotations.getSurfaceFormNormalizedatpos(h) != null && annotations.getMetadatakey(h) != null){
  
	                        if (
	                      
	                        		 annotations.getSurfaceFormNormalizedatpos(h).contains(QnameNormalized) && 
	                        		 annotations.getMetadatakey(h).contentEquals(metakeyAtPos) &&
	                        		 annotations.getSurfaceFormNormalizedatpos(h).length() > expandCandidate.length()
	                        	) 
	                        {
	                        			// store the longest mention
	                        			expandCandidate = annotations.getSurfaceFormNormalizedatpos(h);
	                        			
	                        }
                    	}
                   } // end for
                    
                   // if the expanded candidate is longer than the original, save it
                   if (expandCandidate.length() > QnameNormalized.length()){
                    	System.out.println("    NE Query Expanded to:" + expandCandidate + " !!!\n");
                    	return(expandCandidate);
                   }
                
            }
                
            
            //--------------------------------------
            // This is for special decoding
            // ---> sometimes it differs but it's just before the (ABR)
            //           The Czech Telecommunication Office (CTU) has announced that Czech Amateurs will
            //           services of the Angolan Armed Forces (FAA) from Tuesday as the FAA were celebrati
            // --> restrictions don't match (Apple)
            //--------------------------------------
            if (Qname.matches("[A-Z]+") && activatePLF == true) {
                for (int h = 0; h < annotations.size(); h++) {
                	// if mention exists, and is equal to the abbreviation, and is preceded by a parenthesis 
                    if (annotations.getSurfaceFormNormalizedatpos(h) != null && annotations.getSurfaceFormNormalizedatpos(h).toLowerCase().equals(QnameNormalized)) {

                        // System.out.println(linkAtPos + " M:" + annotations.getSurfaceFormNormalizedatpos(h) );

                    	// if abbreviation preceded, and followed by a parenthesis
                        //	 --> Angolan Armed Forces (FAA)
                        //   counter example 
                        //   -->Pirates  (BNA/Sony BMG Nashville),EL_ENG_01744
                    	// no space between sequence in parenthesis and what is preceding it 
                        if (annotations.getwordatpos(h - 1).equals("(") && annotations.getwordatpos(h + 1).equals(")") && !annotations.getNELabel(h - 2).contentEquals("UNK")) {

                            // look for the end of the mention
                            int posRef = h - 2;
                            while (annotations.getNELabel(posRef).contentEquals(annotations.getNELabel(h - 2))) {
                                posRef--;
                                if (posRef == 0) {
                                    break;
                                }
                            }
                            String PotentialExpansion = annotations.getSurfaceFormatpos(posRef + 1);
                            String abbreviatedMention = abbreviate(PotentialExpansion);

                            // if contains only capitals 
                            if (abbreviatedMention != null){
	                            if (abbreviatedMention.matches("[A-Z]+")) {
	                                System.out.println("    Query Expanded by Potential long form:" + PotentialExpansion + " !!!\n");
	                                return PotentialExpansion.toLowerCase();
	                            }
                            }
                        }
                    }
                }
            }
        
            //--------------------------------------
            // final choice
            // See if the query is an abbreviation of the Wikimeta mention. In this case, it will take the Wikimeta mention.
            //--------------------------------------
            int sizeOfDecoder = annotations.size();
            for (int h = 0; h < sizeOfDecoder; h++) {
            	// if a mention exists, and is not equal to the abbreviation
                if (annotations.getSurfaceFormNormalizedatpos(h) != null && !annotations.getSurfaceFormNormalizedatpos(h).equals(QnameNormalized)) {
                    String abbreviatedMention = abbreviate(annotations.getSurfaceFormatpos(h));
                    if (abbreviatedMention.startsWith(QnameNormalized.toUpperCase())) {
                        System.out.println("    Query Expanded by abreviation to: " + annotations.getSurfaceFormNormalizedatpos(h) + " !!!\n");     
                        return annotations.getSurfaceFormNormalizedatpos(h);
                    }
                }
            }
            
            
        System.out.print("    Query not Expanded:" + QnameNormalized + "\n");
        System.out.println("");
        return QnameNormalized; // by default the expanded query is equal to the original mention
    }	
}
