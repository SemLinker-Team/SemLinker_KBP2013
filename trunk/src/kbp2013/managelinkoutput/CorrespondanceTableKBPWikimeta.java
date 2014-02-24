package kbp2013.managelinkoutput;

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

import configure.NistKBPConfiguration;

/**
 * 
 * This class establishes a correspondence between a Wikimeta generated key and a KB node.
 * 
 * You can find information about Wikimeta annotation format <a href="http://www.wikimeta.com/forum/viewtopic.php?f=9&t=5&sid=02aef266e197712f554b2f68348d6ad3">here</a>.
 * 
 * @see kbp2013.tools.buildKBTable
 * 
 * @author ericcharton
 *
 */
public class CorrespondanceTableKBPWikimeta {

	/** hashmap of WikimetaKey correspondences -> kbkey */
	private HashMap<String, String>  correspondence = new HashMap<String, String>(); // wikimetakey, key
	/** hashmap of kb key correspondences -> name entity */
	private HashMap<String, String>  keybyNE= new HashMap<String, String>(); // key, NE
	
	private String pathToBase = "";
	
	
	/**
	 * 
	 * Constructor : load the correspondence table.
	 * 
	 * 
	 */
	public CorrespondanceTableKBPWikimeta(){
				
		// get the path
		pathToBase = (new NistKBPConfiguration()).PATH_TO_CORRESPONDENCE_TABLE;
		
		// fill the Hash
		// base to read
		int idxtable = 0;
		try {
			
				String text = "";
				BufferedReader reader = new BufferedReader(new FileReader(pathToBase));
			
				System.out.println("[CorrespondenceTableKBPWikimeta]Loading correspondence tables ...");
			
				while ((text = reader.readLine()) != null) 
				{
					// 59267;KBKEY:E0059918;NEREF:PER;NEBASE:1;Roy Ascott;NEENT:PERS.HUM;
					// 59265;KBKEY:E0059916;NEREF:PER;NEBASE:0;Red Smith (third baseman);NEENT:PERS.HUM;OKEY:Red Smith (MLB third baseman)
					String[] tableLine = text.split(";");
					String wikimetaKey = tableLine[4];
					
					String[] KBPKey = tableLine[1].split(":");
					String kbpKey = KBPKey[1];
					
					String[] KBPNE  = tableLine[2].split(":"); // read the NE from KB (sometime some errors)
					String kbpNE = KBPNE[1];
					try{
						String[] SdKBPNE  = tableLine[5].split(":"); // Read the NE from NLGBase
						String SdkbpNE = SdKBPNE[1];
						// correct NE
						if (kbpNE.contains("UKN") && ! SdkbpNE.contains("UNK")){
							SdkbpNE = SdkbpNE.toLowerCase();
							if (SdkbpNE.contains("pers")){ kbpNE = "PER";}
							if (SdkbpNE.contains("loc")){ kbpNE = "GPE";}
							if (SdkbpNE.contains("org")){ kbpNE = "ORG";}
						}
					}catch (Exception e){
						
					}
					correspondence.put(wikimetaKey, kbpKey); // store correspondence between WikimetaKey and KBPKey
					keybyNE.put(kbpKey, kbpNE); // Store NE for a given Key
					
					idxtable++;
				
				}
				
				System.out.println(" Correspondence tables loaded ..." + idxtable + " entries");
				reader.close();
				
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("error while processing line " + idxtable);
		}
		
		
		
	}
	
	/**
	 * 
	 * Take a Wikimeta key and return the corresponding KB Key.
	 * 
	 * @param wikimetakey a Wikimeta key (or a Wikipedia Key)
	 * @return a KB Key
	 */
	public String returnKbpKey(String wikimetaKey){
		
		String toreturn = "NIL"; // default value
		
		if ( correspondence.containsKey(wikimetaKey)){
			
			toreturn = correspondence.get(wikimetaKey); //  key found
			
		}
		
		return toreturn;
	}
	
	/**
	 * 
	 * Take a KB Key and return the KB NE corresponding. 
	 * 
	 * @param Kbpkey a KB Key
	 * @return the corresponding named entity
	 */
	public String returnNEforKbpKey(String Kbpkey){
		
		String NELabeltoreturn = "NIL"; // default value
		
		if ( keybyNE.containsKey(Kbpkey)){
			
			NELabeltoreturn = keybyNE.get(Kbpkey); // key found
			
		}
		
		return NELabeltoreturn;
	}
	
}
