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
import java.util.logging.Level;
import java.util.logging.Logger;

import configure.SemkitConfiguration;


/**
 *
 * The Wikimeta Extractor is a sample API caller to the REST 
 * interface. You just call it with a text sequence, and
 * it manage all the calls trough the Internet and return
 * you a text.
 *
 * @author descl
 * 
 * Improvements by ericcharton / eric.charton@polymtl.ca
 * 
 */
public class WikiMetaExtractor {

	/**
	 * 
	 * Default values for Span and Threshold
	 * 
	 */
	static int defaultSpan = 100; // default 100 - > 300 = test 10 - performance decreased
	static int  defaultTreshold = 10;

	public enum Format {

		XML("xml"),
		JSON("json");

		private final String value;

		Format(String value) {this.value = value;}

		public String getValue() {return this.value;}
	}

	/*
	 * Define API URL by default
	 * 
	 */
	private static String apiurl = ("http://www.wikimeta.com/wapi/service");

	/**
	 * construct a default loader
	 */
	public WikiMetaExtractor() { // default constructor - use default simplenlg.lexicon
		SemkitConfiguration sc = new SemkitConfiguration();
		apiurl = SemkitConfiguration.WMRestURi;
	}

	/**
	 * constructor with an API URL passed 
	 */
	public WikiMetaExtractor(String ParametrizedUrl) { // default constructor - use default simplenlg.lexicon
		WikiMetaExtractor.apiurl = ParametrizedUrl;
	}

	/*
	 * Preformated method needs only API key, return format, string, language
	 * 
	 */
	public static String getResult(String apiKey, Format format, String content, String lng) {
		return WikiMetaExtractor.getResult(apiKey, format, content, defaultTreshold, defaultSpan , lng ,true, 0);
	}

	/*
	 * Method with inclusion of word distributions parameter (1 on or 0 off)
	 * 
	 */
	public static String getResult(String apiKey, Format format, String content, String lng, int stats) {
		return WikiMetaExtractor.getResult(apiKey, format, content, defaultTreshold, defaultSpan , lng ,true, stats);
	}

	/*
	 * Full method
	 * 
	 */
	public static String getResult(String apiKey, Format format, String content, int treshold, int span, String lng, boolean semtag, int stats) {    

		String result = "";
		String callFormat = format.value;

		try {
			URL url = new URL(apiurl);

			HttpURLConnection server = (HttpURLConnection)url.openConnection();

			server.setDoInput(true);
			server.setDoOutput(true);
			server.setRequestMethod("POST");
			server.setRequestProperty("Accept", callFormat );
			server.setAllowUserInteraction(false);


			server.connect();

			BufferedWriter bw = new BufferedWriter(
					new OutputStreamWriter(
							server.getOutputStream()));


			String semtagS = "0";
			if(semtag)semtagS = "1";

			String request = "treshold="+treshold+"&span="+span+"&lng="+lng+"&semtag="+semtagS+"&api="+apiKey+"&textmining="+stats+"&contenu="+content;

			bw.write(request, 0, request.length());
			bw.flush();
			bw.close();

			//send query
			/*PrintStream ps = new PrintStream(server.getOutputStream());
            ps.print(request);
            ps.close();
			 */
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
			Logger.getLogger(WikiMetaExtractor.class.getName()).log(Level.SEVERE, null, e);
		}
		return result;
	}


	/**
	 * 
	 * This method is used for UTF compliance checking
	 * 
	 * @param inString
	 * @return
	 */
	private static String removeNonUtf8CompliantCharacters( final String inString ) {

		if (null == inString ) return null;

		byte[] byteArr = inString.getBytes();

		for ( int i=0; i < byteArr.length; i++ ) {
			byte ch= byteArr[i];
			// remove any characters outside the valid UTF-8 range as well as all control characters
			// except tabs and new lines
			if ( !( (ch > 31 && ch < 253 ) || ch == '\t' || ch == '\n' || ch == '\r') ) {
				byteArr[i]=' ';
			}
		}
		return new String( byteArr );
	}

	public void modDefaultUrl(String newurl){

		apiurl = newurl;
	}

}
