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

import nlp.upperlevel.NormalizeNE;
import nlp.upperlevel.SimpleCoreferenceDetector;

import semkit.extractor.WikiMetaExtractor;
import semkit.extractor.WikiMetaXMLDecoder;
import configure.SemkitConfiguration;

/**
 * 
 * Build an HTML file using the Wikimeta API.
 * 
 * @author ericcharton
 *
 */
public class BuildHtmlSample {

	
	// Colors from 
	// http://www.html-color-names.com/
	private static String pers = "style=\"color: #4B0082\""; // indigo
	private static String org = "style=\"color: #8B0000\""; // dark red
	private static String loc = "style=\"color: #2E8B57\""; // sea green
	private static String prod = "style=\"color: #0000FF\""; // blue
	private static String time = "style=\"color: #8B008B\""; // dark magenta
	private static String amount = "style=\"color: #5F9EA0\""; // cadet blue
	private static String fonc = "style=\"color: #FFA500\""; // orange
	
	
	/**
	 * 
	 * A class that demonstrates how to annotate a document, refine it, and then transform it in HTML code for
	 * a website. This is very similar to the new version of Wikimeta main form that you can use at <a href="http://www.wikimeta.com">wikimeta.com</a>.<br>
	 * <br>
	 * Command line:<br>
	 * <br>
	 * java -cp semlinker.jar semkit.demos.BuildHtmlSample<br>
	 * <br>
	 * [optional parameters]<br>
	 * -text texfile<br>
	 * -config configfile<br>
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		/* String to annotate by default (if no file is specified) */
		String toannotate = "The effects of Hurricane Ivan in the Lesser Antilles and South America in September 2004 included 44 deaths and over $1 billion in damage, primarily in Grenada (damage pictured) where it was considered the worst hurricane in nearly 50 years. Hurricane Ivan developed from a tropical wave on September 2 and rapidly intensified to become a major hurricane, passing through the southern Lesser Antilles on September 7 with winds of 125 mph (205 km/h).";
		// API Key variable
		String apikey = "";
		// config file name
		String configFile = null;
		// outputfilename
		String outFile ="output.html";
		
		// verbose
		System.out.println("Entering into BuildHtmlSample v1.0 ...");
		
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
						
							if ( args[x].contains("-config") ){
								configFile = args[x+1];
								System.out.println("--Define config file ");
							}
							
							if ( args[x].contains("-outfile") ){
								outFile = args[x+1];
								System.out.println("--Define output file ");
							}
							
							if ( args[x].contains("-text") ){
								
								toannotate = "";
								String filename = args[x+1];
								System.out.println("--Using a text file " + filename);
								
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
							
							
					} catch(Exception e){
						// Error
						System.out.println("An error occured, please check your command line instruction");
						System.exit(0); 
					}
							
		}
		
		/*
		 * Retrieve configuration
		 * 
		 */
		SemkitConfiguration vars;
		if (configFile != null){
			vars = new SemkitConfiguration(configFile);
		}else{
			vars = new SemkitConfiguration();
		}
		
		
		// Instantiate a NE Normalizer  
		NormalizeNE NEnorm = new NormalizeNE();
				
		/* String default language */
		String ln= "EN";
		
		/* String getting the result */
		String result = "";
		
		// configuration
		int command = 0;
			
		
		
		//-------------------------
		// Instantiate objects
		// and make annotation
		//-------------------------
		// Wikimeta Extractor
		result = WikiMetaExtractor.getResult(  SemkitConfiguration.APIAccount , WikiMetaExtractor.Format.XML, toannotate , ln);
		// Wikimeta decoder
		WikiMetaXMLDecoder annotations = new WikiMetaXMLDecoder(result, 15);
		// apply coreference detection
		annotations = SimpleCoreferenceDetector.applyCoreferenceCorrection(annotations);
		// apply NE normalizer
        annotations = NEnorm.rerankNE(annotations);
        // re-apply coreference corrections after NE Normalizer
        annotations = SimpleCoreferenceDetector.applyCoreferenceCorrection(annotations);
		
        // now output an HTML
        
        System.out.println("<html><head><title>Demo Page</title>" +
        		"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">" +
                "</head><body bgcolor=\"white\">");
        System.out.print("");
        
        for (int x=0; x< annotations.size(); x++){
        	
        	if (annotations.getSurfaceFormatpos(x) != null){
	        	//------------------------------------
	        	// Available Wikipedia annotations
	        	//------------------------------------
	        	if ( ! annotations.getMetadatakey(x).contains("NIL")){
	        		
	        		// fill the key
	        		String urikey = annotations.getMetadatakey(x).replaceAll(" ", "_");
	        		
	        		// get the NE
	        		String style = "";
	        		if (annotations.getNELabel(x).contains("PERS")) style = pers; 
	        		if (annotations.getNELabel(x).contains("ORG")) style = org; 
	        		if (annotations.getNELabel(x).contains("LOC")) style = loc; 
	        		if (annotations.getNELabel(x).contains("PROD")) style = prod; 
	        		if (annotations.getNELabel(x).contains("FONC")) style = fonc; 
	        		
	        		if (ln.contains("EN")){
	        			System.out.print("<a " + style + " href=http://en.wikipedia.org/wiki/" + urikey + ">" + annotations.getSurfaceFormatpos(x) + "</a> ");
	        		}else{
	        			System.out.print("<a " + style + " href=http://fr.wikipedia.org/wiki/" + urikey + ">" + annotations.getSurfaceFormatpos(x) + "</a> ");
	        		}
	        			
	        		// get the length of the mention
	        		String[] sflength = annotations.getSurfaceFormatpos(x).split(" ");
	        		x = x + sflength.length - 1;
	        		
	        	}else if (annotations.getNELabel(x).contains("TIME") || annotations.getNELabel(x).contains("AMOUNT")){
	        		
	        		String tosearch = annotations.getSurfaceFormatpos(x);
	        		tosearch = tosearch.replaceAll(" ", "%20");
	        		
	        		String style = "";
	        		if (annotations.getNELabel(x).contains("TIME")) style = time; 
	        		if (annotations.getNELabel(x).contains("AMOUNT")) style = amount;
	        		System.out.print("<a " + style + " href=http://www.google.com/#hl=en&q=" + tosearch  + ">" + annotations.getSurfaceFormatpos(x) + "</a> ");
	        	
	        		// get the length of the mention
	        		String[] sflength = annotations.getSurfaceFormatpos(x).split(" ");
	        		x = x + sflength.length - 1;
	        		
	            }
        	}else{
        		System.out.print("");
        		System.out.print(annotations.getwordatpos(x) + " ");
        	}
        }
		
		System.out.println("</body></html>");
	
	}

}
