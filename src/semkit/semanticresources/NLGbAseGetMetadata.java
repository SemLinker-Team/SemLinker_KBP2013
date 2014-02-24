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

import java.io.*;
import java.util.regex.*;

/**
 * 
 * @author ericcharton
 *
 */
public class NLGbAseGetMetadata {


	/**
	 * 
	 * Structure of the publicly available object extracted from metadata
	 * 
	 * 
	 */

	/** The size of the returned array containing n metadata elements */
	private double offset;
	/** The class of the NamedEntity */
	public String NamedEntityClass;
	/** The unique key of the metadata */
	public String EntryKey; 
	/** The string array containing all the surface forms */
	public String[] SurfaceForms =  new String[200];
	/** the amount of surface forms in the array */
	public int FormsAbs = 0;

	/** The url to the Wikipedia entry */
	public String Wikipedia = null;
	/** The uri to the dbpedia xml representation */
	public String Dbpedia = null;
	/** The uri to the Cia Factbook representation */
	public String Factbook = null;


	/* local variables */
	private BufferedReader readerWithBuffer = null;

	private static Pattern pattern;
	private static Matcher matcher;

	/* local constant */
	private static String wikipediaUrl = "en.wikipedia.org/wiki/";
	private static String DBpediaUrl = "http://dbpedia.org/data/";


	/**
	 * 
	 * @param chemin
	 * 
	 * 
	 */
	public NLGbAseGetMetadata (String PathTotheCSVFileOfMetadata)  {

		try {
			openfile(PathTotheCSVFileOfMetadata);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param chemin
	 * @throws IOException
	 */
	private void openfile(String chemin) throws IOException {


		try{

			readerWithBuffer = new BufferedReader(new FileReader(chemin));
		}
		catch(FileNotFoundException exc)
		{
			System.out.println("Erreur d'ouverture");
		}


	}

	/**
	 * 
	 * Read a line a get a metadata
	 * 
	 * @return
	 * 
	 * 
	 */
	private String getentry(BufferedReader mylecteurAvecBuffer) {

		String readedline = null;

		try {
			readedline = mylecteurAvecBuffer.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return(readedline);
	}


	/**
	 * 
	 * Read the next line
	 * 
	 * @return String a read line
	 */
	public String returnNext(){

		String readed = getentry(readerWithBuffer);

		return readed;
	}



	/**
	 * 
	 * Get a read line and structure it in the variables of this class
	 * 
	 * @param ReadedLine
	 */
	public void organizeText(String ReadedLine){


		String[] temp;

		/* delimiter */
		String delimiter = "\t";
		/* given string will be split by the argument delimiter provided. */
		temp = ReadedLine.split(delimiter);

		/* structure of the string array*/
		// offset
		// named entity label
		// key name in wikipedia
		//
		// n surface form in the original language
		// ln:surface form - followed by n surface form until another ln:
		// dbpedia.url
		//
		int i = 0;

		Double mydouble;
		this.offset = Double.parseDouble(temp[i++]);

		this.NamedEntityClass = temp[i++];
		this.EntryKey = temp[i++];

		this.Wikipedia = wikipediaUrl + EntryKey;

		/* Collect surface forms */
		int idxSurfaceform = 0;	
		int adx = 0;

		while( i < temp.length )	{

			// collect form
			String proStr = temp[i++];

			// test
			// dbpedia.
			// dbpedia.Afghanistan     factbook.Afghanistan    linkedgeodata.org/triplify/node/26847706#id
			pattern = Pattern.compile("^dbpedia");
			matcher = pattern.matcher(proStr);
			if (matcher.find()) {
				break;
			}

			// process form
			// http://docs.oracle.com/javase/1.4.2/docs/api/java/util/regex/Pattern.html
			pattern = Pattern.compile("^[a-z][a-z]:");
			matcher = pattern.matcher(proStr);

			if (matcher.find()){
				// remove beginning 
				// add form
				proStr = proStr.substring(3);
			}

			// store if non empty
			if ( ! proStr.equals("") ) SurfaceForms[adx] = proStr;
			adx++;

		}

		FormsAbs = adx-2; // how many 

		/* Manage the LinkedData content */
		Dbpedia = DBpediaUrl + temp[i-1].substring(8) + ".xml";

	}


}
