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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONException;

/**
 * 
 * This Class is the one used to retrieve a DBPedia document and 
 * properties contained in it. See the sample class LODRetrievalSample.java for samples of 
 * uses.
 * 
 * 
 * 
 * @author ericcharton
 *
 */
public class DBPediaExtractor {

	private static String textsentback = "";
	private static String URItoget = "http://www.dbpedia.org/data/";

	/**
	 * Store the triples
	 */
	public static String DocumentReference;
	public static HashMap<String, String> Triples = new HashMap<String, String>(); // form prop -> uri
	public static HashMap<String, String> TriplesByUri = new HashMap<String, String>(); // form uri -> prop

	/**
	 * Default Constructor
	 */
	public DBPediaExtractor() { // default constructor - use default simplenlg.lexicon

	}

	/**
	 * 
	 *  Constructor with a specific depot name
	 * 
	 * @param DepotURI
	 */

	public DBPediaExtractor(String DepotURI) { // default constructor - use default simplenlg.lexicon
		URItoget = DepotURI;
	}


	/**
	 * 
	 * Get the resource according to a name descriptor
	 * 
	 * @param resource
	 * @param callFormat
	 * @return
	 * @throws JSONException 
	 */
	public static int getResourceByName(String NameOfRes, String callFormat) throws JSONException {

		// build the resource
		String resource = URItoget + NameOfRes;

		getResource(resource , callFormat);

		return 1;    

	}


	/**
	 * 
	 * Get the resource according to format
	 * 
	 * @param resource
	 * @param callFormat
	 * @return
	 * @throws JSONException 
	 */
	public static int getResource(String resource, String callFormat) throws JSONException {    

		String result = ""; 

		// adapt the resource URI name -> resource -> data
		resource = resource.replaceAll("/resource/", "/data/");


		// default
		String fullresource = resource + ".ntriples"; 
		String defaultFormat = "ntriples";

		// others - verify the formation of url
		if (callFormat.contains("JSON")) { 
			fullresource = resource + "." +  callFormat;
			defaultFormat = "JSON";
		}
		if (callFormat.contains("ntriples")) { 
			fullresource = resource + "." +  callFormat;
			defaultFormat = "ntriples";
		}



		// Extract
		try {
			URL url = new URL(fullresource);
			HttpURLConnection server = (HttpURLConnection)url.openConnection();
			server.setDoInput(true);
			server.setDoOutput(true);
			server.setRequestMethod("POST");
			// server.setRequestProperty("Accept", callFormat );
			server.connect();

			BufferedWriter bw = new BufferedWriter(
					new OutputStreamWriter(
							server.getOutputStream()));

			String request = "" ;
			bw.write(request, 0, request.length());
			bw.flush();
			bw.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(server.getInputStream()));

			String ligne;
			while ((ligne = reader.readLine()) != null) {
				result += ligne+"\n";
			}

			reader.close();
			server.disconnect();
		}
		catch (Exception e)
		{
			System.err.println("Error " + e + " on " +  fullresource);
		}



		// Store the string
		textsentback = result;

		// Decode the string according to inside format
		if (defaultFormat.contains("ntriples") ){

			DBPediaNtriplesDecoder localdecoder = new DBPediaNtriplesDecoder( );

			// load the triple hashmap from text
			Triples = localdecoder.NtripleDecoder( textsentback );

			// load the triple as uri -> prop
			TriplesByUri = localdecoder.NtripleDecoderbyprop(textsentback);

			// load the document name
			DocumentReference = localdecoder.theName;
		}

		if (defaultFormat.contains("JSON") ){
			DBPediaJSONDecoder localdecoder = new DBPediaJSONDecoder( textsentback);
		}

		// return
		return 1;
	}
}
