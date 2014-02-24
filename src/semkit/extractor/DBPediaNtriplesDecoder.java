package semkit.extractor;

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

/**
 * 
 * 
 * @author ericcharton
 *
 */
public class DBPediaNtriplesDecoder {

	public String theName = "";

	public DBPediaNtriplesDecoder() {
		// TODO Auto-generated constructor stub

	}


	/**
	 * 
	 * Decode the triple from plain text and store it under the form prop -> uri load
	 * 
	 * @param textsentback
	 * @return
	 */
	HashMap<String, String>  NtripleDecoder(String textsentback){

		HashMap<String, String> localtriples = new HashMap<String, String>(); // hashmap for storage and to return the triples


		String[] temp; // to split lines

		/* delimiter */
		String delimiter = "\n";
		/* given string will be split by the argument delimiter provided. */
		temp = textsentback.split(delimiter);


		for (int i =0 ; i < temp.length ; i++){

			// System.out.println( i + "-" + temp[i] + "-");

			String[] templine; // to split each lines
			String inlinedelimiter = "\t";
			templine = temp[i].split(inlinedelimiter);

			try{


				// get the name
				theName = templine[0].substring(1,templine[0].length()-1);

				// get the URI of pos 1
				//<http://dbpedia.org/resource/Lyon>_<http://dbpedia.org/ontology/wikiPageExternalLink>_<http://www.jmpatin.fr/myPictures/?q=category/42-lyon>
				String URILOD = templine[1].substring(1,templine[1].length()-1); // remove the <>

				// decode pos 2
				String Prop = templine[2].substring(1,templine[2].length()-3); // remove the . at end


				// load the triples by property
				// localtriplesbyprop.put( URILOD, Prop);
				//System.out.println("                NTP:" + URILOD + " " + Prop);
				// property, definition
				localtriples.put( Prop, URILOD);

			}
			catch (Exception e)
			{
				System.err.println("Error " + e);
			}
		}



		return(localtriples);
	}



	/**
	 * 
	 * Decode the triple from plain text and store it under the form urilod -> prop
	 * 
	 * @param textsentback
	 * @return
	 */
	HashMap<String, String>  NtripleDecoderbyprop(String textsentback){


		HashMap<String, String> localtriplesbyprop = new HashMap<String, String>(); // hashmap for storage and return the triples

		String[] temp; // to split lines

		/* delimiter */
		String delimiter = "\n";
		/* given string will be split by the argument delimiter provided. */
		temp = textsentback.split(delimiter);


		for (int i =0 ; i < temp.length ; i++){

			// System.out.println( i + "-" + temp[i] + "-");

			String[] templine; // to split each lines
			String inlinedelimiter = "\t";
			templine = temp[i].split(inlinedelimiter);

			try{


				// get the name
				theName = templine[0].substring(1,templine[0].length()-1);

				// get the URI of pos 1
				//<http://dbpedia.org/resource/Lyon>_<http://dbpedia.org/ontology/wikiPageExternalLink>_<http://www.jmpatin.fr/myPictures/?q=category/42-lyon>
				String URILOD = templine[1].substring(1,templine[1].length()-1); // remove the <>

				// decode pos 2
				String Prop = templine[2].substring(1,templine[2].length()-3); // remove the . at end


				// load the triples by property
				localtriplesbyprop.put( URILOD, Prop);

			}
			catch (Exception e)
			{
				System.err.println("Error " + e);
			}
		}

		return( localtriplesbyprop );
	}
}
