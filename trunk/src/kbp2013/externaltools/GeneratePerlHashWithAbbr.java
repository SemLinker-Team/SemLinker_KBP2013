package kbp2013.externaltools;

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

public class GeneratePerlHashWithAbbr {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// instantiate classes of constants
		NistKBPConfiguration KBvars = new NistKBPConfiguration();
		// map abbreviation <--> full name 
		HashMap<String, Integer> AbbreviationMap = new HashMap<String, Integer>();

		
		//----------------------
		// load abbreviation map
		//-----------------------
		try {

				BufferedReader reader = new BufferedReader(new FileReader(KBvars.ABBREVIATION_MAP_FILE));

				String abbrvline = "";

				while (( abbrvline = reader.readLine()) != null) {
					
					if (! abbrvline.startsWith("#")) {
						String[] abbrvfull = abbrvline.split("\t"); 
						String fullname = abbrvfull[0];
						String abbreviation = abbrvfull[1];

						// load hashmap
						AbbreviationMap.put(fullname,1); // only one time for each final key

					}

				}
				reader.close();

			} 
			catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// generate the perl hashmap
			System.out.print("my %bnamehash = (");
			for (String key : AbbreviationMap.keySet()) {
			    System.out.print(" \"" + key + "\" => 1 ,");
			}
			System.out.print(");");
			
		}
		

}
