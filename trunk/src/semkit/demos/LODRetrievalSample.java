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



import org.json.JSONException;
import semkit.extractor.DBPediaExtractor;
import configure.SemkitConfiguration;


/**
 * 
 * This is an example to demonstrate how to retrieve data from the Linked Open Data according to an existing entity.<br>
 * <br>
 * 
 * www.wikimeta.org www.wikimeta.com<br>
 * Update April 2012
 * 
 * 
 */
public class LODRetrievalSample {

	/**
	 * 
	 * Main Sample : get a LOD resource related to a city, and obtain related information<br>
	 * <br>
	 * Command line:<br>
	 * java -cp semlinker.jar semkit.demos.LODRetrievalSample<br>
	 * <br>
	 * Options:<br>
	 * -cityname name_of_a_city<br>
	 * <br>
	 * Example:<br>
	 * java -cp semkit.jar semkit.demos.LODRetrievalSample -cityname Lille<br>
	 *  
	 * @param args
	 */
	public static void main(String[] args) throws JSONException {
		// TODO Auto-generated method stub

		System.out.println("This is a sample of LOD annotation retrieval\n");

		/*
		 * 
		 * Here we get some properties from a full resource URI
		 * 
		 */
		SemkitConfiguration vars = new SemkitConfiguration();

		// the city to search
		String cityname = "Paris";

		//----------------------------
		// manage command line
		//----------------------------
		for (int x=0; x < args.length; x++){

			try{

				if ( args[x].contains("-cityname") ){

					cityname = args[x+1];
				}

			} catch(Exception e){
				// Error
				System.out.println("An error occured, please check your command line instruction");
				System.exit(0); 
			}

		}




		// give the full name of a resource
		String ResourceToGet = SemkitConfiguration.URItoGet + cityname; // you can also use http://www.dbpedia.org/data/Paris

		// Get a DBPedia document
		DBPediaExtractor town = new DBPediaExtractor(SemkitConfiguration.URItoGet);

		// get the resource content
		if (DBPediaExtractor.getResource(ResourceToGet, "") == 0){
			System.out.println("Error");
			System.exit(0);
		}else{
			System.out.println("Document returned for resource " +  ResourceToGet + ":");
		}


		// retrieve the http://dbpedia.org/property/website property
		String websitename = DBPediaExtractor.TriplesByUri.get("http://" + SemkitConfiguration.URInameOfRes + "/property/website");
		System.out.println("Website name of " + DBPediaExtractor.DocumentReference + " is " + websitename);
		String RegionRelated = DBPediaExtractor.TriplesByUri.get("http://" + SemkitConfiguration.URInameOfRes + "/ontology/region");
		System.out.println("Region of " + DBPediaExtractor.DocumentReference + " is " + RegionRelated);
		System.out.println("");

		/*
		 * 
		 * Here we get some properties from a name
		 * This name is related to unique descriptor key of Wikipedia documents
		 * 
		 */

		// give the name of a resource
		String NameToGet = cityname ;

		// Get a DBPedia document using a defined repos
		DBPediaExtractor town2 = new DBPediaExtractor(SemkitConfiguration.URItoGet);

		// get the resource from WP descriptor
		if (DBPediaExtractor.getResourceByName( NameToGet, "ntriples")==0){
			System.out.println("Error");
			System.exit(0);
		}else{
			System.out.println("Document returned for resource according to name " +  NameToGet + ":");
		}


		// retrieve the http://dbpedia.org/property/website property
		websitename = DBPediaExtractor.TriplesByUri.get("http://" + SemkitConfiguration.URInameOfRes + "/property/website");
		System.out.println("Website name of " + DBPediaExtractor.DocumentReference + " is " + websitename);

		RegionRelated = DBPediaExtractor.TriplesByUri.get("http://" + SemkitConfiguration.URInameOfRes + "/ontology/region");
		System.out.println("Region of " + DBPediaExtractor.DocumentReference + " is " + RegionRelated);

		String MayorRes = DBPediaExtractor.TriplesByUri.get("http://" + SemkitConfiguration.URInameOfRes + "/ontology/mayor");
		System.out.println("Mayor of " + DBPediaExtractor.DocumentReference + " is " + MayorRes);
		System.out.println("");

		/*
		 * 
		 *  retrieve some properties of the Mayor according to previous city
		 *  
		 */

		// give the name of the Mayor Name of Lille
		// ResourceToGet = "http://dbpedia.org/data/Martine_Aubry";
		ResourceToGet = MayorRes;

		// Get a DBPedia document
		DBPediaExtractor person = new DBPediaExtractor(SemkitConfiguration.URItoGet);
		// get the resource content

		if (DBPediaExtractor.getResource(ResourceToGet, "") == 0){
			System.out.println("Error");
			System.exit(0);
		}else{
			System.out.println("Now retrieving the political party of City mayor :");
		}


		// retrieve the http://dbpedia.org/property/website property
		String partyname = DBPediaExtractor.TriplesByUri.get("http://" + SemkitConfiguration.URInameOfRes + "/ontology/party");
		System.out.println("Political affiliation of " + MayorRes + " is " + partyname);
	}
}
