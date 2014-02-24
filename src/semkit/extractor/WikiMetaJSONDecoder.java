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

import org.json.*;

import java.util.HashMap; 

/**
 *
 * The JSON Decoder receives an API output string from the Wikimeta API
 * under the JSON format. It integrates various methods to directly access
 * the API output (named entities, POS, and so on)
 *
 *
 * @author ericcharton
 * 
 *
 * 
 */
public class WikiMetaJSONDecoder {


	/*
	 * 
	 *  Define local public and private variable
	 * 
	 */

	public JSONTokener JSONTokenized = null; // Local object to store the tokenized string
	private JSONObject jObject = null; 
	private JSONArray ArrayjDoc = null; // the full document in an array 

	// word pos
	private JSONObject ReadTheWords = null;
	private JSONArray WordEntries = null;

	// NE and semantic links
	private JSONObject ReadTheNE = null;
	private JSONArray NEEntries = null;

	// Creating new HashMap objects 
	// keys are Integer, values are String
	HashMap<Integer, String> NEStorage = new HashMap<Integer, String>();
	HashMap<Integer, String> NELabel = new HashMap<Integer, String>();
	HashMap<Integer, String> MetadataUri = new HashMap<Integer, String>();
	HashMap<Integer, String> LINKEDDATA = new HashMap<Integer, String>();

	public WikiMetaJSONDecoder (){

	}

	/**
	 * 
	 * 
	 * Construct some JSONArray from a source JSON text.
	 * 
	 * @param source A string that begins with
	 * <code>[</code>&nbsp;<small>(left bracket)</small>
	 * and ends with <code>]</code>&nbsp;<small>(right bracket)</small>.
	 * @throws JSONException If there is a syntax error.
	 * 
	 * Docs
	 * http://www.json.org/java/
	 * http://www.docjar.com/jar_detail/json-org.jar.html
	 * 
	 * 
	 */
	public WikiMetaJSONDecoder (String jsonfromapi){

		try {
			// prepare the JSON objects
			JSONTokenized = new JSONTokener(jsonfromapi); // tokenize string
			jObject = new JSONObject(JSONTokenized); // build JSONObject
			ArrayjDoc = jObject.getJSONArray("document"); // get doc


			// Get the words
			ReadTheWords = new JSONObject(ArrayjDoc.getString(1)); 
			WordEntries = ReadTheWords.getJSONArray("words");


			// Get NE characteristics
			JSONObject ReadTheNE = new JSONObject(ArrayjDoc.getString(2)); 
			JSONArray NEEntries = ReadTheNE.getJSONArray("Named Entities");
			// send the NE in a hashmap - key = num pos

			for (int i = 0; i < NEEntries.length(); i++) {

				// get an object
				JSONObject entry = NEEntries.getJSONObject(i);
				// load the hashmap for NE
				int position =  Integer.parseInt(entry.getString("position"));

				// Get the NE string
				String wordkey = entry.getString("EN");
				NEStorage.put(position, wordkey);

				// Get the NE Label
				String Label  = entry.getString("type");
				NELabel.put(position, Label);

				// Get the Metadata Link
				String Uri  = entry.getString("URI");
				if ( ! Uri.contentEquals("NORDF") ) MetadataUri.put(position, Uri);

				// Get the DBPedia Link if exists
				String LINKEDDATAString  = entry.getString("LINKEDDATA");
				if ( ! Uri.contentEquals("NORDF") ) LINKEDDATA.put(position, LINKEDDATAString);

			}
		} 
		catch (JSONException e) { 
			System.out.println (e.toString()); 
		}
	}



	/**
	 * 
	 *  Return the size of the array of words returned by the API
	 * 
	 *  @return int representing the number of words in the sequence
	 * 
	 */
	public int size(){

		return(WordEntries.length());
	}




	// -------------------------------
	//
	// methods to collect API result
	//
	// -------------------------------

	/**
	 * 
	 * Methods to collect text word from API : sends the line number and receives 
	 * the corresponding word
	 * 
	 * @param int corresponding to the word offset number
	 * @see size method
	 * @return string corresponding to the word at the offset
	 * 
	 * 
	 */
	public String getwordatpos (int linenumber){

		String StringTOReturn = null;

		try {

			// Identify word to return by its index
			JSONObject FoundEntry = WordEntries.getJSONObject(linenumber);
			StringTOReturn = FoundEntry.getString("word");

		}
		catch (JSONException e) { 
			System.out.println (e.toString()); 
		}

		return(StringTOReturn);
	}

	/**
	 * 
	 * Methods to collect POS tag from API : sends the line number and receives 
	 * the corresponding POS tag
	 * 
	 * @param int corresponding to the word and POS offset number
	 * @see size method
	 * @return string corresponding to the POS at the offset according to word
	 * 
	 * 
	 */
	public String getPOSatpos (int linenumber){

		String POSTOReturn = null;

		try {

			// Identify word to return by its index
			JSONObject FoundEntry = WordEntries.getJSONObject(linenumber);

			// fill pos to return 
			POSTOReturn = FoundEntry.getString("PartOfSPeech");


		}
		catch (JSONException e) { 
			System.out.println (e.toString()); 
		}

		return( POSTOReturn);
	}

	/**
	 * 
	 * Invoke this method to retrieve the lemma associated to the word at
	 * linenumber
	 * 
	 * 
	 * @param linenumber
	 * @return
	 */
	public String getLemmeatpos(int linenumber){

		String LemmeReturn = null;

		try {

			// Identify word to return by its index
			JSONObject FoundEntry = WordEntries.getJSONObject(linenumber);

			// fill lemma to return  
			LemmeReturn = FoundEntry.getString("Lemme");


		}
		catch (JSONException e) { 
			System.out.println (e.toString()); 
		}

		return( LemmeReturn);
	}

	/**
	 * 
	 * Invoke this method to retrieve the Stem associated to the word
	 * at the line number
	 * 
	 * 
	 * @param linenumber
	 * @return
	 */
	public String getStematpos(int linenumber){

		String StemReturn = null;

		try {

			// Identify word to return by its index
			JSONObject FoundEntry = WordEntries.getJSONObject(linenumber);

			// fill stem to return  
			StemReturn = FoundEntry.getString("Stem");


		}
		catch (JSONException e) { 
			System.out.println (e.toString()); 
		}

		return( StemReturn);
	}

	/**
	 * 
	 * Methods to get an NE at a given pos if exists
	 * 
	 * 
	 */
	public String getNE (int linenumber){

		if  (NEStorage.containsKey(linenumber)) { 

			return( NEStorage.get(linenumber));

		}
		else{
			return(null); // no NE at this POS
		}
	}

	/**
	 * 
	 * Methods to get an NE label at a pos if exists
	 * 
	 * 
	 */
	public String getNELabel (int linenumber){

		if  (NELabel.containsKey(linenumber)) { 

			return( NELabel.get(linenumber));

		}
		else{
			return(null); // no NE at this POS
		}
	}

	/**
	 * 
	 * Methods to get the NLGBASE URI descriptor
	 * 
	 * 
	 */
	public String getMetadata (int linenumber){

		if  (MetadataUri.containsKey(linenumber)) { 

			return( MetadataUri.get(linenumber));

		}
		else{
			return(null); // no NE at this POS
		}
	}

	/**
	 * 
	 * Methods to get the LinkedData URI descriptor
	 * 
	 * 
	 */
	public String getLinkedData (int linenumber){

		if  (LINKEDDATA.containsKey(linenumber)) { 

			return( LINKEDDATA.get(linenumber));

		}
		else{
			return(null); // no NE at this POS
		}
	}

	/**
	 * 
	 * Methods to return a list
	 * http://docs.oracle.com/javase/1.4.2/docs/guide/collections/index.html
	 * 
	 */
	public void  getalist(int linenumber){

		String StringTOReturn = null;
		String POSTOReturn = null;

		try {

			// word - pos
			JSONObject ReadTheWords = new JSONObject(ArrayjDoc.getString(1)); 
			JSONArray WordEntries = ReadTheWords.getJSONArray("words");

			// fill word to return
			JSONObject FoundEntry = WordEntries.getJSONObject(linenumber);
			StringTOReturn = FoundEntry.getString("word");
			// fill pos to return 
			POSTOReturn = FoundEntry.getString("PartOfSPeech");


		}
		catch (JSONException e) { 
			System.out.println (e.toString()); 
		}
	}
}
