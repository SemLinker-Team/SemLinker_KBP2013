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

public class GetCombination {

	
	/**
	 * constructor 
	 */
	public GetCombination() { 
	
	}
	
	/**
	 * 
	 * Clean the sequence before splitting and testing
	 * 
	 * 
	 */
	public String cleanSequence(String text){
		
		text = text.replaceAll("[.,?/\"\t():;*+]", " ");
    	text = text.replaceAll("^[ ]+", "");
    	text = text.replaceAll("[ ]+$", "");
    	text = text.replaceAll("[ ]+", " ");
    	
    	return(text);
	}
	
	/**
	 * 
	 * Get all the combinations from 0 to span
	 * 
	 * @param query
	 * @param span
	 * @return
	 */
	public ArrayList<String> getSequences(String query, int span){
		
		query = query.replaceAll("^[ ]+", " ");
		String inline[] = query.split(" ");
		ArrayList<String> returnsequences = new ArrayList<String>();
		
		for (int a =0; a < inline.length; a++){
			
			String assemble = "";
			for(int x = 0; x < span; x++){
				
				if (a+x >= inline.length){ 
						break;
				}else{
					
					assemble = assemble + " " + inline[a+x];
					assemble = assemble.replaceAll("^[ ]+", "");
					returnsequences.add(assemble);
				}
					
				// System.out.println("-" + assemble + "-");
			}
			
		}
		
	
		return returnsequences; 	
	}
	
	
	/**
	 * 
	 * Get all the combinations for specific span
	 * 
	 * @param query
	 * @param span
	 * @return
	 */
	public ArrayList<String> getSequencesUniq(String query, int span){
		
		query = query.replaceAll("^[ ]+", " ");
		String inline[] = query.split(" ");
		ArrayList<String> returnsequences = new ArrayList<String>();
		
		// only successive according to span are built
		for (int a =0; a < ( inline.length - span + 1); a++){
			
			String assemble = inline[a];
			
			for(int x = a+1; x < a  + span; x++){
					assemble = assemble + " " + inline[x];
			}
			
			// add the sequence for [a:a+span]
			returnsequences.add(assemble);
		}
		
	
		return returnsequences; 	
	}
}
