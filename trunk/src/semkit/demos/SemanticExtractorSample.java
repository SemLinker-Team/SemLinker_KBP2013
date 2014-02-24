package semkit.demos;

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

This software is maintained an released at:

https://code.google.com/p/semlinker/

Please contact respective authors from this page for support
or any inquiries. 

 */

import java.io.BufferedReader;
import java.io.FileReader;

import org.json.JSONException;

import semkit.extractor.DBPediaExtractor;
import semkit.extractor.WikiMetaExtractor;
import semkit.extractor.WikiMetaJSONDecoder;

import configure.SemkitConfiguration;


/**
 * 
 * This class is an example of a complete annotation and LOD extraction process.
 * A sentence is sent to the annotation engine, and then returned annotated.
 * LOD links (DBPedia) are then used to collect extra information like birthplace 
 * for all people found in the sentence. 
 * 
 * Test
 * 
 * @author ericcharton
 *
 */
public class SemanticExtractorSample {

	/**
	 * 
	 * Main method, you can give a String in parameter and this sample returns 
	 * complementary information on the submitted sentence.<br>
	 * <br>
	 * <br>
	 * Command line:<br>
	 * java -cp semlinker.jar semkit.demos.SemanticExtractorSample <br>
	 *  <br>
	 * -h help<br>
	 * -apikey key / define your own API key<br>
	 * -text filename / annotate a text file given in command line<br>
	 * <br>
	 * <br>
	 * @param args
	 * @throws JSONException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws JSONException {

		System.out.println("Entering into SemanticExtractorSample v1.0 ...");

		SemkitConfiguration vars = new SemkitConfiguration();
		WikiMetaExtractor SampleCaller = new WikiMetaExtractor(SemkitConfiguration.WMRestURi);
		String apikey = SemkitConfiguration.APIAccount; 

		// default string
		String toAnnotate = "Manuel Valls, Pierre Moscovici, Arnaud Montebourg, Vincent Peillon, mais aussi Ségolène Royal, Lionel Jospin, Claude Bartolone, Bruno Le Roux, Bertrand Delanoë, Michel Rocard... Une bonne partie du gouvernement, de nombreux parlementaires et des figures historiques du Parti socialiste ont assisté ce mercredi aux obsèques d'Olivier Ferrand, député PS des Bouches-du-Rhône décédé subitement samedi dernier.";


		//----------------------------
		// manage command line
		//----------------------------
		for (int x=0; x < args.length; x++){

			try{
				// help
				if ( args[x].matches("-h")){

					System.out.println("-Help:");
					System.exit(0); // help always overrides others
				}

				if ( args[x].contains("-apikey") ){

					apikey = args[x+1];
					System.out.println("--Define API key ");
				}

				if ( args[x].contains("-text") ){

					toAnnotate = "";
					String filename = args[x+1];
					System.out.println("--give a text file " + filename);

					BufferedReader br = new BufferedReader(new FileReader(filename));
					try {
						StringBuilder sb = new StringBuilder();
						String line = br.readLine();

						while (line != null) {
							sb.append(line);
							sb.append("\n");
							line = br.readLine(); 
						}
						toAnnotate = sb.toString();
					} finally {
						br.close();
					}

				}


			} catch(Exception e){
				// Error
				System.out.println("An error occured, please check your command line instruction");
				System.exit(0); 
			}

		}



		/*
		 *  To make a simple call 
		 * 
		 * Format is :
		 * 	   api account
		 *     format : WikiMetaExtractor.Format.JSON or WikiMetaExtractor.Format.XML
		 *     text to send :
		 *     !!!IMPORTANT!!! The Text must be in UTF-8. If the text is mixed, please use a converter (Notepad++ does it) to obtain best results
		 *                     Have also a look at http://www.rishida.net/tools/conversion/ 
		 *     Language used : FR or EN
		 */

		System.out.println("* Will annotate :");
		System.out.println(toAnnotate);

		String result = WikiMetaExtractor.getResult( apikey, WikiMetaExtractor.Format.JSON, toAnnotate , "FR");
		System.out.println( toAnnotate);
		System.out.println( result);


		/* 
		 * 
		 * Then retrieve the LINKEDDATA content
		 * 
		 * 
		 */	
		WikiMetaJSONDecoder JsonApiresult = new WikiMetaJSONDecoder(result);

		// return a word at a given position
		for ( int x = 0; x < JsonApiresult.size(); x++){


			// Is there a LinkedData  at this position 
			if  ( JsonApiresult.getLinkedData(x) != null ) {

				// do we have a dbpedia entry
				if ( JsonApiresult.getLinkedData(x).contains("dbpedia") ) {


					// is it a person
					if (JsonApiresult.getNELabel(x).contains("PERS")){

						System.out.println();
						System.out.print("EN: " + JsonApiresult.getNE(x) );
						System.out.print(" (LOD :" + JsonApiresult.getLinkedData(x) + "  )");
						System.out.println();

						// can we retrieve the political party this person belongs to
						// give the name of the Mayor Name of Lille
						String ResourceToGet =  JsonApiresult.getLinkedData(x); // this is the name of the person

						// Get a DBPedia document
						DBPediaExtractor person = new DBPediaExtractor(SemkitConfiguration.URItoGet);

						// get the resource content
						if (DBPediaExtractor.getResource(ResourceToGet, "") == 0){
							System.out.println("-No document returned");
						}else{
							System.out.println("-Document returned ok");

							// retrieve the http://dbpedia.org/property/website property
							String partyname = DBPediaExtractor.TriplesByUri.get("http://" + SemkitConfiguration.URInameOfRes + "/ontology/party");
							System.out.println("      ->Political affiliation of " + JsonApiresult.getNE(x) + " is " + partyname );
							String birthdate = DBPediaExtractor.TriplesByUri.get("http://" + SemkitConfiguration.URInameOfRes + "/ontology/birthDate");
							System.out.println("      ->Birthdate of " + JsonApiresult.getNE(x) + " is " + birthdate);
							String birthplace = DBPediaExtractor.TriplesByUri.get("http://" + SemkitConfiguration.URInameOfRes + "/property/placeOfBirth");
							System.out.println("      ->Birthplace of " + JsonApiresult.getNE(x) + " is " + birthplace);

						}
					}
				}
			}
		}
	}
}
