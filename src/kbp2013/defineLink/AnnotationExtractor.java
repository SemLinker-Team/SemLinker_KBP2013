package kbp2013.defineLink;

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

import semkit.extractor.WikiMetaExtractor;
import semkit.extractor.WikiMetaXMLDecoder;
import configure.SemkitConfiguration;

public class AnnotationExtractor {

	

    String apikey ;
	WikiMetaExtractor SampleCaller; 
	SemkitConfiguration vars ;
	boolean verbose = true;
	
	/**
	 * 
	 * Constructor
	 * 
	 */
	public AnnotationExtractor(){
		
		vars = new SemkitConfiguration();
		SampleCaller = new WikiMetaExtractor(SemkitConfiguration.WMRestURi);
		apikey = SemkitConfiguration.APIAccount; 
	
	}
	
	/**
	 * 
	 * Constructor
	 * 
	 * @param uriToApi
	 */
	public AnnotationExtractor(String uriToApi){
		
		vars = new SemkitConfiguration();
		SampleCaller = new WikiMetaExtractor(uriToApi);
		apikey = SemkitConfiguration.APIAccount; 
		
	}
	
	
	
	/**
	 * 
	 * 
	 * @param document
	 * @return
	 */
	public String getWikimetaAnnotations(String document){
		
		String result = "";
		int lengthofsplit = 30000;
		// divide document in an array
		if (document.length() > 60000){
			
			  
				// make sized sequential annotations
		        int fin = lengthofsplit; int deb = 0;
		        while(fin < document.length()){
		        	
		        	if (verbose) System.out.println("->Annotation extractor " + deb + " " + fin + " / " + document.length());
		        	result = result + WikiMetaExtractor.getResult( apikey, WikiMetaExtractor.Format.XML, document.substring(deb, fin)  , "EN");
		        	
		        	deb = fin + 1;
		        	fin = fin + lengthofsplit;
		        	
		        	if (fin > document.length() ) 
		        	{ 
		        		if (deb < document.length() ){
		        			if (verbose) System.out.println("->*Annotation extractor " + deb + " " + fin + " / " + document.length());
		        			result = result + WikiMetaExtractor.getResult( apikey, WikiMetaExtractor.Format.XML, document.substring(deb,  document.length() )  , "EN");
		        		}
		        	}
		        }
		        
		        //-----------------------
		        // rebuild the XML
		        // and aggregate the CDATA
		        //-----------------------
		       
		        String textarray[] = result.split("\n"); 
		        String newresult = "<![CDATA[\n";
		        int y = 0;
		        
		    	// collect CDATA tags
		    	for (int x = 0; x < textarray.length; x++){
		    		if (textarray[x].contains("<![CDATA[")){	
		    			y = x + 1; // go just after CDATA
		    			// accumulate the CDATA
		    			String provisoireresult = ""; 
		    			while(! textarray[y].contains("]]>")){
		    				
		    					provisoireresult = provisoireresult.concat(textarray[y]);
		    					provisoireresult = provisoireresult.concat("\n");
		    					
		    					y++; 
		    					if (y > textarray.length) break;
		    			}
		    			x = y + 1 ;
		    			newresult = newresult.concat(provisoireresult);
		    		
		    		}
		    	}
		    	newresult = newresult + "]]>\n";
		    	result = newresult; 
		    	
		        
		}else{
			
			result = WikiMetaExtractor.getResult( apikey, WikiMetaExtractor.Format.XML, document , "EN");
		}
		
		return result;
		
	}
	
	
	
	/**
	 * 
	 * 
	 * @param apioutput String retrieved in XML from Wikimeta
	 * @param amountofannotations Number of annotations to retrieve from Wikipedia
	 * @return
	 */
	public WikiMetaXMLDecoder getAnnotations(String XMLfromApiOutput, int amountofannotations){
		
		WikiMetaXMLDecoder StructuredContent = new WikiMetaXMLDecoder(XMLfromApiOutput, amountofannotations);
		
		return StructuredContent ;
	}
	
	/*
	public String getWikimetaAnnotationsNormalized(String document){
		
		document = document.replaceAll("@", "");
		document = document.replaceAll("\\[", "");
		document = document.replaceAll("\\]", "");
		
		String result = WikiMetaExtractor.getResult( apikey, WikiMetaExtractor.Format.XML, document , "EN");
		
		return result;
		
	}
	*/
	
}
