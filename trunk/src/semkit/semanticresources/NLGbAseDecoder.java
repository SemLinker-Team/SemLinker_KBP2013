package semkit.semanticresources;

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


/**
 * 
 * This class decodes a line from 
 * NLGbAse.
 * 
 * @author ericcharton
 * 
 */
public class NLGbAseDecoder {


	public ArrayList<String> surfaceforms = new ArrayList<String>(); 
	public String NE = ""; 
	public String UniqueKey = ""; 
	public String DbpediaUri = "";
	public String CiaUri = "";
	public String GeonameUri = "";
	public String UsCensus = "";

	public double WPoffset = 0;
	public String WpoffsetStr = "";

	/**
	 * constructor 
	 */
	public NLGbAseDecoder() { 

	}


	/**
	 * 
	 * Method to decode a CSV line
	 * 
	 * @param NLGbAseline
	 */
	void Decoder(String NLGbAseline) { 

		String strNLGline[]=NLGbAseline.split("\t");

		WPoffset = new Double(strNLGline[0]);
		WpoffsetStr = strNLGline[0];

		NE = strNLGline[1];
		UniqueKey = strNLGline[2];

		// The original key is the first surface form
		surfaceforms.add(UniqueKey);


		// extract surface forms
		for (int x=3; x < strNLGline.length; x++){ 


			// search for the end of the csv line, 
			// where the LOD uri begins
			if (strNLGline[x].contains("dbpedia.") ){ 

				for (int y=x; y < strNLGline.length; y++){ 


					// decode Dbpedia
					if (strNLGline[y].contains("dbpedia") ){ 

						DbpediaUri = strNLGline[y];
					}

					// decode CIA

					// decode geonames

				}
				break;  // we stop the loop if we have found a LOD link (no Surface forms after it)
			}

			// verify and clean the surface form (ie begins with nationality definition)
			// match en: fr: etc
			strNLGline[x] = strNLGline[x].replaceAll("^[a-z][a-z]:",""); // replace the nationality by nothing

			// add a surface form to the surface form list if not exists
			surfaceforms.add(strNLGline[x]);

		}

	}

	/**
	 * 
	 * Reset the decoder variables
	 * 
	 */
	void eraseDecoder(){

		NE = ""; 
		UniqueKey = ""; 
		DbpediaUri = "";
		WPoffset = 0;
		WpoffsetStr = "";

		surfaceforms.removeAll(surfaceforms);
	}

}
