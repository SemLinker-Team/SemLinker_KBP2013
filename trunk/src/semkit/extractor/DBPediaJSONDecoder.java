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

/**
 * 
 * @author ericcharton
 *
 */

public class DBPediaJSONDecoder {



	public JSONTokener DBPJSONTokenized = null; // Local object to store the tokenized string
	private JSONObject DBPjObject = null; 
	private JSONArray DBPArrayjDoc = null; // the full document in an array 

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
	public DBPediaJSONDecoder (String jsonfromstring) throws JSONException{

		// prepare the JSON objects
		DBPJSONTokenized = new JSONTokener(jsonfromstring); // tokenize the string
		DBPjObject = new JSONObject(DBPJSONTokenized); // build the JSONObject
		// DBPArrayjDoc = new JSONArray(jsonfromstring);

		for (int x=0; x < DBPjObject.length(); x++){

			// DBPArrayjDoc = DBPjObject.;
			//System.out.print(x + " " + DBPjObject.toString(x));
			//System.exit(0);
		}

	}

}
