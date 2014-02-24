package kbp2013.wikipedia;

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
import java.io.FileReader;
import java.util.HashMap;

import configure.NistKBPConfiguration;

/**
 * 
 * This class finds a KB key from KBP according to a surface form. It is used with non
 * ambiguous forms when annotation process has not found answers for it. 
 * 
 * @author ericcharton
 *
 */
public class AnnotateWithKB {

	
	/**
	 * 
	 * Hashes of non ambiguous surface forms
	 * 
	 * String1 is the SF from KB and Wikimeta
	 * String2 is the KB key
	 * 
	 */
	private HashMap<String, String[]>  NonAmbiguousSurfaceforms = new HashMap<String, String[]>();
	
	
	
	/**
	 * 
	 * Constructor : instantiate the class and then load it, keeping only the non 
	 * ambiguous surface forms of more than one word. 
	 * 
	 */
	public AnnotateWithKB(){
		
		// get the path
		String pathToBase = (new NistKBPConfiguration()).PATH_TO_CORRESPONDENCE_TABLE;
		
		HashMap<String, Integer> AmbigousSurfaceforms = new HashMap<String, Integer>();
		
		// counters
		int ambigoussurfaceform = 0;
		
		// fill the Hash
		// base to read
		int idxtable = 0;
		String text = "";
		try {
			
				
				BufferedReader reader = new BufferedReader(new FileReader(pathToBase));
			
				System.out.println("[AnnotateWithKb] Loading KB Informations ...");
			
				while ((text = reader.readLine()) != null) 
				{
					
					// pre treatment :
					// KB contains some special chars in titles - remove them
					// 29;KBKEY:E0000030;NEREF:UKN;NEBASE:0;Larsen &amp; Toubro;NOOP
					text = text.replaceAll("&amp;", "&");
					text = text.replaceAll("&quot;", ""); // no quotes
					
					
					// 59267;KBKEY:E0059918;NEREF:PER;NEBASE:1;Roy Ascott;NEENT:PERS.HUM;
					// 59265;KBKEY:E0059916;NEREF:PER;NEBASE:0;Red Smith (third baseman);NEENT:PERS.HUM;OKEY:Red Smith (MLB third baseman)
					
					// load the Wikimeta / reference Key
					String[] tableLine = text.split(";");
					String wikimetaSFKey = tableLine[4]; // ex Red Smith (third baseman)
					// clean the parentheses
					String cleanwikimetaSFkey =  removeParenthesys(wikimetaSFKey).toLowerCase();
					
					// Load the KB Key
					String[] KBPKey = tableLine[1].split(":");
					String kbpKey = KBPKey[1]; // ex E0059916
					
					// load the NE Label from KB
					String[] NEsection = tableLine[2].split(":");
					String NELabelFromKb = NEsection[1];
					
					// String to store the information
					String[] tostore = new String[4];
					tostore[0] = kbpKey;
					tostore[1] = wikimetaSFKey;
					tostore[2]= NELabelFromKb;
					
					// load the hashes for the actual wikimeta  Key
					if ( ! NonAmbiguousSurfaceforms.containsKey(cleanwikimetaSFkey)){
						// load with surface form 
						NonAmbiguousSurfaceforms.put(cleanwikimetaSFkey, tostore);
					
					}else{
						AmbigousSurfaceforms.put(cleanwikimetaSFkey, 1);
						ambigoussurfaceform++;
					}
					
					
					
					// load the Original Key (OKEY)
					String Originalkey = null;
					String cleanokey = null;
					
					if (text.contains("OKEY"))
					{
						String[] Okey = tableLine[6].split(":");
						if ( Okey[0].contains("OKEY"))
						{
							
							Originalkey = Okey[1];
							// clean the parenthesis
							cleanokey = removeParenthesys(Originalkey).toLowerCase();
							// load the hashes for the actual wikimeta  Key
							if ( ! NonAmbiguousSurfaceforms.containsKey(cleanokey )) {
								NonAmbiguousSurfaceforms.put(cleanokey, tostore);
								// System.out.println("----" + cleanokey);
							}else{
								AmbigousSurfaceforms.put(cleanokey, 1);
								ambigoussurfaceform++;
							}
						}			
					}
			
					
					
					idxtable++;
				
				}
				
				reader.close();
				
				
				
	
		} catch ( Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("error while processing line " + idxtable + " " + text);
		}
		
		System.out.println(" End of load: SF:" + NonAmbiguousSurfaceforms.size() + " Ambigous:"  + AmbigousSurfaceforms.size() + " Ambigouscounted:" + ambigoussurfaceform);
		
		//----------------------------
		// clean bases to save memory
		//----------------------------
		for (String key : AmbigousSurfaceforms.keySet()) {
			NonAmbiguousSurfaceforms.remove(key);
		}
		AmbigousSurfaceforms.clear();
		
	}
	
	
	

	
	//------------------------------------------
	// Annotate and retrieve
	//------------------------------------------

	/**
	 * 
	 * Return a KB Key according to the mention sent if exists or
	 * null otherwise. 
	 * 
	 */
	public String getKeyforAMention(String mentionNormalized){
		
		String returnedkey = null;
		
		if ( NonAmbiguousSurfaceforms.containsKey(mentionNormalized) ) {
			
			String[] collectHashData = new String[3];
			collectHashData = NonAmbiguousSurfaceforms.get(mentionNormalized);
			
			returnedkey = collectHashData[0];
		}
		
		return returnedkey ;
	}
	
	
	//------------------------------------------
	// Utilities
	//------------------------------------------
	
	
	private String removeParenthesys(String originalkey){
		
		String keytoreturn = originalkey;
		
		// eventually remove parentheses
		// System.out.println("--->" + originalkey);
		if ( originalkey.contains("(")){ 
			int offsetp = originalkey.indexOf("(");
			try {
				keytoreturn = originalkey.substring(0, offsetp - 1);

			}catch (Exception e){} // try catch for some rare and specific cases like "(You Make Me Feel Like) A Natural Woman"
			
		}
		
		return(keytoreturn);
		
	}
}
