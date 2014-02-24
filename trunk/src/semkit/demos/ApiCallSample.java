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
import org.json.*;

import semkit.extractor.WikiMetaExtractor;
import semkit.extractor.WikiMetaJSONDecoder;

import configure.SemkitConfiguration;

/**
 * 
 * Sample Java API Caller for Wikimeta. Just run to view all the possibilities.<br>
 * <br>
 * <br>
 * Initially provided by www.wikimeta.org www.wikimeta.com as free code, included
 * in semkit by courtesy.<br>
 * Updated September 2013<br>
 * <br>
 * 
 */
public class ApiCallSample {

	/**
	 * 
	 * Main method. This method from the sample tool integrates all the different possible
	 * usages of the Semantic Development Kit. <br>
	 * <br>
	 * Command line:<br>
	 * java -cp semlinker.jar semkit.demos.ApiCallSample <br>
	 * <br>
	 * -h help<br>
	 * -apikey key / define your own API key<br>
	 * -text filename / annotate a text file given in command line<br>
	 * -distrib / display the word distribution<br>
	 * -decodejson / display the JSON decoding<br>
	 * -language {EN|FR} / force a language of annotation<br>
	 * <br>
	 * <br>
	 * @param args - No argument needed, default options are provided
	 * @throws JSONException 
	 * 
	 */
	public static void main(String[] args) throws JSONException {
		
		System.out.println("Entering into ApiCallSample v1.0 ...");
		
		/*
		 * Sample of Caller
		 * 
		 * give address in SemanticConstants.java or fill directly the parameters
		 * 
		 */
		SemkitConfiguration vars = new SemkitConfiguration();
		WikiMetaExtractor SampleCaller = new WikiMetaExtractor(SemkitConfiguration.WMRestURi);
		
		/* obtain your free API key at http://my.wikimeta.com/amember/login */ 
		String apikey = SemkitConfiguration.APIAccount; // define your API key here or in SemanticConstants file
		
		/* String to annotate by default (if no file is specified) */
		String toannotate = "The effects of Hurricane Ivan in the Lesser Antilles and South America in September 2004 included 44 deaths and over $1 billion in damage, primarily in Grenada (damage pictured) where it was considered the worst hurricane in nearly 50 years. Hurricane Ivan developed from a tropical wave on September 2 and rapidly intensified to become a major hurricane, passing through the southern Lesser Antilles on September 7 with winds of 125 mph (205 km/h).";
		
		/* String language */
		String ln= "EN";
		
		/* String getting the result */
		String result = "";
		
		// configuration
		int command = 0;
		
		
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
								
								toannotate = "";
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
								        toannotate = sb.toString();
								    } finally {
								        br.close();
								    }
								
							}
							
							if ( args[x].contains("-distrib") ){
								command = 1;
								System.out.println("Get word distribution");
							}
							
							if ( args[x].contains("-decodejson") ){
								command = 2;
								System.out.println("GetJsonCode");
							}
							
							if ( args[x].contains("-language") ){
								ln = args[x+1].toUpperCase();
								System.out.println("Force language to " + ln);
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
		 * 	   API account
		 *     format : WikiMetaExtractor.Format.JSON or WikiMetaExtractor.Format.XML
		 *     text to send
		 *     Language used : FR or EN
		 */
		if ( command == 0) {
				result = WikiMetaExtractor.getResult( apikey, WikiMetaExtractor.Format.JSON, toannotate , ln);
				System.out.println(result);
		}
		
		
		/*
		 * 
		 * To make a call and ask a distribution of words
		 * 
		 * 
		 */
		if ( command == 1) {
			result = WikiMetaExtractor.getResult( apikey, WikiMetaExtractor.Format.JSON, toannotate,  toannotate, 1);
			System.out.println(result);
		}

		
		/**
		* 
		* Transfer the JSON text to a Java Object.
		* 
		* 
		* @param source A string that begins with
		* <code>[</code>&nbsp;<small>(left bracket)</small>
		* and ends with <code>]</code>&nbsp;<small>(right bracket)</small>.
		* @throws JSONException If there is a syntax error.
		* 
		* @url
		* http://www.json.org/java/
		* http://www.docjar.com/jar_detail/json-org.jar.html
		*/
		if (command == 2){
				System.out.println("----------- Decode Json ------------");
				
				result = WikiMetaExtractor.getResult( apikey, WikiMetaExtractor.Format.JSON, toannotate , ln);
				
				WikiMetaJSONDecoder JsonApiresult = new WikiMetaJSONDecoder(result);
				
				// method to return a word at a given position
				for ( int x = 0; x < JsonApiresult.size(); x++){
				
					// Display the word - pos sequence - 
					System.out.print("Mot: " + JsonApiresult.getwordatpos(x) + " POS:" + JsonApiresult.getPOSatpos(x));
					
					// Is there a NE at this position 
					if ( JsonApiresult.getNE(x) != null ){
						
						// print NE String
						System.out.print("  EN: " + JsonApiresult.getNE(x) );
						// print NE Tag
						System.out.print("  ( Label :" + JsonApiresult.getNELabel(x) + "  )");
					}
					
					// Is there a metadata  at this position 
					if ( JsonApiresult.getMetadata(x) != null ) System.out.print("  (Label :" + JsonApiresult.getMetadata(x) + "  )");
					// Is there a LinkedData  at this position 
					if ( JsonApiresult.getLinkedData(x) != null ) System.out.print("  (LOD :" + JsonApiresult.getLinkedData(x) + "  )");
								
					System.out.println();
					
					
				}
		}
		
		
	}

}
