/*
 * 
 * KBP2013 Package is a set of classes used to deploy
 * a system on NIST KBP 2012 and 2013 evaluation campaign.  
 * 
 */
package kbp2013.wikipedia;

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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import semkit.extractor.WikiMetaExtractor;
import kbp2013.managedocuments.SpellingCorrector;
import kbp2013.managedocuments.checkSuggestion;
import kbp2013.tools.ManageQueries;
import kbp2013.tools.ManageQueries.Query;
import configure.NistKBPConfiguration;

/**
 *
 * This class makes use of Lucene-search v2.1 for wiki (in use with Wikimedia wiki's) "do You Mean" 
 * functionality to correct a misspelled query. 
 * It can be used both with online Wikipedia (or any wiki) search engine or with a local one
 * configured with the extension Lucene-search for Wiki.<br>
 * <br> 
 * <a href="https://www.mediawiki.org/wiki/Extension:Lucene-search"> See documentation online</a>.
 * 
 * @author ericcharton
 *
 */
public class GetRewritingFromWikipedia {
	
	 private static int TIMETOWAITINMS = 500;
	
	 /**
	  * 
	  * Constructor
	  * 
	  */
	 public GetRewritingFromWikipedia(){
		 
	 }
	 
	 /**
	  * 
	  * Main method for direct calls with a list of words to correct or re-spell. Build the 
	  * misspelling correction algorithm. Not of use now but kept for comfort usage and investigations. 
	  * 
	  * @param args
	  */
	 public static void main(String[] args) {

	        // Instantiate classes of constants
	        NistKBPConfiguration KBvars = new NistKBPConfiguration();
	        int nbQueries = 0;
	       
	        //-----------------------------------
	        // get options of command lines
	        // override constants and variables if needed
	        //-----------------------------------
			for (int x=0; x < args.length; x++){
				
						try{
								// help
								if ( args[x].matches("-h")){
									System.out.println("-Help:");
									System.exit(0); // help always overrides others
								}
								
								// eval mode
								if ( args[x].matches("-pathofxml") ){
									   // usually "~/workspace/SemLinker/resources/kbp2013/entitylinkingeval/tac_2013_kbp_english_entity_linking_evaluation_queries.xml";
									   KBvars.PATH_TO_TRAIN = args[x + 1];
								}
								
								// config file
								if ( args[x].matches("-config") ){
									KBvars = new NistKBPConfiguration(args[x + 1]);
								}
								
						} catch(Exception e){
							// Error
							System.out.println("An error occured, please check your command line instruction");
							System.exit(0); 
						}
								
			}
	        
	        
	     
	        
	        try 
	        {
	        	
		        	//--------------------
		        	// open exp files
		        	//--------------------
		        	BufferedReader reader = null;        	
		        	reader = new BufferedReader(new FileReader(KBvars.PATH_TO_TRAIN));
		        	
		        	ManageQueries querymanager = new ManageQueries(); // Instantiate a query manager
		        	String text = "";
		        	Query query;
		        	
		            while ((text = reader.readLine()) != null) 
		            {
		            	 if (text.contains("<query")) {
		            		 nbQueries++;
			            	 query = querymanager.extractQueryData(reader, querymanager.extractQueryId(text), nbQueries);
			            
			            	 // send query to Wikipedia
			             	 String querytosend = query.name;
			             	 String result = getResult(querytosend);
			             	 
			             	
			             	 // process only when there is an answer
			            	 if (result != null){
			            		 	 
			            		 	 // selection algorithm
					             	 checkSuggestion.validateARewriting(querytosend, result); 
					            	 
					             	 // display
					             	 System.out.print(query.id + "\t:\t" + query.originalQuery + "\t");
					            	 System.out.println(result);
			            	 }else{
			            		
			            		 System.out.println(query.id + ":" + query.name + " no modification");
			            	 }
			            	 
			            	 
			            	 try{
			            		 Thread.sleep(TIMETOWAITINMS); // this is to do not access to often to Wikipedia search engine / avoiding blacklisting
			            	 }catch(InterruptedException e){}
			            	 
		            	 }
		            	 
		            }    
				
		            reader.close();
	            

	        } catch (FileNotFoundException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	 }
	
	 /**
	  * 
	  * This is a hack to simulate Lucene Search extension for Wiki 2.1 when there is no 
	  * active server available on Intranet. It uses directly the search engine of Wikipedia. 
	  * <a href="/w/index.php?title=Special:Search&amp;search=Phoenix&amp;fulltext=Search&amp;redirs=0&amp;profile=default" title="Special:Search"><em>Phoenix</em></a></div></td></tr></table><div class="mw-search-formheader"><div class="search-types"><ul><li class="current"><a href="/w/index.php?title=Special:Search&amp;search=Phonix&amp;fulltext=Search&amp;profile=default&amp;redirs=0" title="Search in (Article)">Content pages</a></li><li class="normal"><a href="/w/index.php?title=Special:Search&amp;search=Phonix&amp;fulltext=Search&amp;profile=images&amp;redirs=0" title="Search for files">Multimedia</a></li><li class="normal"><a href="/w/index.php?title=Special:Search&amp;search=Phonix&amp;fulltext=Search&amp;profile=help&amp;redirs=0" title="Search in Wikipedia, Help">Help and Project pages</a></li><li class="normal"><a href="/w/index.php?title=Special:Search&amp;search=Phonix&amp;fulltext=Search&amp;profile=all&amp;redirs=0" title="Search all of content (including talk pages)">Everything</a></li><li class="normal"><a href="/w/index.php?title=Special:Search&amp;search=Phonix&amp;fulltext=Search&amp;profile=advanced&amp;redirs=0" title="Search in custom namespaces">Advanced</a></li></ul></div><div class="results-info"><ul><li>Results <b>1–20</b> of <b>259</b> for <b>Phonix</b></li></ul></div><div style="clear:both"></div></div></form><div class='searchresults'><p class="mw-search-createlink">
	            	
	  * @param QueryToSearch
	  * @return
	  */
	 public static String getResult(String QueryToSearch) {    
	       
		 QueryToSearch = QueryToSearch.replaceAll(" ", "%20");
		 String ligne = null;
		 
		 // build url 
		 String searchurl = "https://en.wikipedia.org/w/index.php?title=Special%3ASearch&profile=default&search=" + QueryToSearch + "&fulltext=Search";
    
		 try {
	            URL url = new URL(searchurl);
	          
	            HttpURLConnection server = (HttpURLConnection)url.openConnection();
	            
	            server.setDoInput(true);
	            server.setDoOutput(true);
	            server.setRequestMethod("POST");
	            server.setRequestProperty("Accept", "http" );
	            server.setAllowUserInteraction(false);
	            
	       
	            server.connect();
	            
	            BufferedWriter bw = new BufferedWriter(
	                                new OutputStreamWriter(
	                                    server.getOutputStream()
	                                    )
	                                );
	           
	            
	            
	            String request = searchurl;
	            
	            bw.write(request, 0, request.length());
	            bw.flush();
	            bw.close();
	            
	            //send query
	            BufferedReader reader = new BufferedReader(new InputStreamReader(server.getInputStream()));
	            
	           
	            while ((ligne = reader.readLine()) != null) {
	            	
	            	 // <div class="searchdidyoumean">Did you mean: 
	            	if (ligne.contains("<div class=\"searchdidyoumean\">")){
	            		 
	            		 ligne = ligne.replaceAll("<div[^>]+>", "");
	            		 ligne = ligne.replaceAll("Did you mean: ", "");
	            		 ligne = ligne.replaceAll("<a href=[^>]+>", "");
	            		 ligne = ligne.replaceAll("<em>", "");
	            		 ligne = ligne.replaceAll("</em>", "");
	            		 ligne = ligne.replaceAll("</a>.+$", "");
	            		 System.out.println("Sequence to Search:" + ligne);
	            		 return ligne;
	            		 
	            	 }
	            }
	            
	            reader.close();
	            server.disconnect();
	        }
	        catch (Exception e)
	        {
	            Logger.getLogger(WikiMetaExtractor.class.getName()).log(Level.SEVERE, null, e);
	            return null; // Erreur, probably in http, send back null
	        }
	        
	      
	        return null; // end of process send null
	    }
	 
	 
	 
}
