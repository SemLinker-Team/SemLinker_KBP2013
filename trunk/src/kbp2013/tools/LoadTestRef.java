package kbp2013.tools;

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

public class LoadTestRef {

	
	HashMap<String, String>  queriesKbCorrespondances = new HashMap<String, String>();
	HashMap<String, String>  queriesKbENLabelCorrespondances = new HashMap<String, String>();
	
	/**
	 * 
	 * @param PathOfReferenceFile
	 */
	public LoadTestRef(String PathOfReferenceFile){
		
		
		try {
			
			System.out.println("[Loading Test Reference]");
			
			// open the reference file
			BufferedReader reader = new BufferedReader(new FileReader(PathOfReferenceFile));
		
			String text = null;
			// count how many entities in KB
			
			while ((text = reader.readLine()) != null) 
			{ 
					
					// split to collect informations
					String[] extractQueryReferenceFromLine = text.split("\t");
					// load queries ref in Hashmap
					// key is the query num
					// value is the kb according to query
					queriesKbCorrespondances.put(extractQueryReferenceFromLine[0], extractQueryReferenceFromLine[1]);
					// set the label
					queriesKbENLabelCorrespondances.put(extractQueryReferenceFromLine[0], extractQueryReferenceFromLine[2]);
			}
			
			reader.close();
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	/**
	 * 
	 * @param QueryName
	 * @return
	 */
	public String returnQueryKBRef(String QueryName){
		
		return queriesKbCorrespondances.get(QueryName);
		
	}
	
	/**
	 * 
	 * @param QueryName
	 * @return
	 */
	public String returnQueryKBENLabelRef(String QueryName){
		
		return queriesKbENLabelCorrespondances.get(QueryName);
		
	}
	
}
