package kbp2013.tools;

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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import semkit.semanticresources.NLGbAseRedisAccessMetadata;
import semkit.semanticresources.NLGbAseRedisLoadMetadata;
import configure.NistKBPConfiguration;
import configure.SemkitConfiguration;

/**
 * 
 * Build a table of correspondence between Wikimeta annotations system
 * and KB from NIST KB key system, using NLGbAse metadata. 
 * 
 * 
 * @author ericcharton
 *
 */
public class buildKBTable {

	/**
	 * 
	 * !!! CAUTION : Redis Base must be active and filled before using this class !!!
	 * 
	 * @see semkit.semanticresources
	 * @see semkit.semanticresources.NLGbAseRedisLoadMetadata
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		
	    //-----------------------------------
        // get options of command lines
        // override constants and variables if needed
        //-----------------------------------
		String configfile = null;
		for (int x=0; x < args.length; x++){
					try{
							// help
							if ( args[x].matches("-h")){
								
								System.out.println("-Help:");
								System.exit(0); // help always overrides others
							}
								// config file
							if ( args[x].matches("-config") ){
								configfile =  args[x + 1];
							}
							
					} catch(Exception e){
						// Error
						System.out.println("An error occured, please check your command line instruction");
						System.exit(0); 
					}
							
		}
		
		
		// find path and this kind of stuff
		NistKBPConfiguration nVars;
		SemkitConfiguration semkitVars;
		
		if (configfile != null){
			nVars = new NistKBPConfiguration(configfile);
			semkitVars = new SemkitConfiguration(configfile);
		}else{
			nVars = new NistKBPConfiguration();
			semkitVars = new SemkitConfiguration();
		}
		
		// build full path
		String kbSourceBase = nVars.NAME_OF_FULL_KB;
				
		// Manage metadata and initialization of Redis base
		// NLGbAseRedisLoadMetadata redisobject = new NLGbAseRedisLoadMetadata(semkitVars.NLGbAsePath);
		
		// Get back an entry and all its surface forms
		NLGbAseRedisAccessMetadata metadata = new NLGbAseRedisAccessMetadata();

		// test if the base is loaded
		if ( metadata.getJedisStatus() != 1){
			
			System.out.println("No NLGbAse version loaded : please load it first using tool command");
			System.exit(0);
			
		}
	
	
		
		// open the base and retrieve the content
		try {
			
			// base to read
			BufferedReader reader = new BufferedReader(new FileReader(kbSourceBase));
			// table to write
			BufferedWriter writer = new BufferedWriter(new FileWriter(nVars.PATH_TO_CORRESPONDENCE_TABLE));
			
			String text = null;
			
			// counters
			int counter = 0;// count how many entities in KB
			int noop = 0; // how many unaligned entities
			
			
			while ((text = reader.readLine()) != null) 
            {
            	
				// <entity wiki_title="Mike_Quigley_(footballer)" type="PER" id="E0000001" name="Mike Quigley (footballer)">
            	if ( text.contains("wiki_title=") ) {
            		
            		
            		String entid = text;
            		String [] entidAr = entid.split(" ");
            		String neref = "";
            		
            		for (int x =0; x < entidAr.length; x++){
            			
            			// extract the entity id
            			if (entidAr[x].contains("id=")){
            				entid = entidAr[x].replace("id=\"", "");
            				entid = entid.replace("\"", "");
            			}
            			// extract the NE
            			if (entidAr[x].contains("type=")){
            				entid = entidAr[x].replace("type=\"", "");
            				neref = entid.replace("\"", "");
            			}
            		}
            		
            		
            		// extract the wiki name
            		String entname = text;
            		entname = entname.replace("<entity wiki_title=\"", "");
            		entname = entname.replaceFirst("\" type=.+$" ,"");
            		entname = entname.replaceAll("_", " ");
            		
            		// if 0 not found, else name is unique
            		int a = metadata.keyIsunIque(entname);
            		
            		// collect remaining data
            		String ne = metadata.getNE(entname);
            		String db = metadata.getDBPedia(entname);
            		
            		//-----------------------------
            		// Display and save
            		//-----------------------------
            		System.out.print(counter + ";KBKEY:" + entid  + ";NEREF:" + neref + ";NEBASE:" + a + ";" );
            		writer.append(counter + ";KBKEY:" + entid  + ";NEREF:" + neref + ";NEBASE:" + a + ";" );
            		
            		if ( a >0 ){ 
            				System.out.print(entname + ";NEENT:" + ne +";"); 
            				writer.append(entname + ";NEENT:" + ne +";");
            		}else{
            			
            			// prepare a new key to find correspondence
            			String searchfrom = entname.toLowerCase();
            			searchfrom = searchfrom.replaceAll("\\(", "");
            			searchfrom = searchfrom.replaceAll("\\)", "");
            			
            			// if not found, search in surface forms for redirection          			
            			ArrayList<String> wordList = metadata.getUniqueKey(searchfrom);
            			// display all the surface forms
            			if (wordList.size() > 0 ){
            				
            				for ( String newwikiname : wordList){
            					String newne = metadata.getNE(newwikiname);
            					System.out.print(newwikiname + ";NEENT:" + newne +";OKEY:" + entname); 
            					writer.append(newwikiname + ";NEENT:" + newne +";OKEY:" + entname); 
            					break;
            				}
            			}else{
            				System.out.print(entname + ";NOOP");
            				writer.append(entname + ";NOOP");
            				noop++;
            			}
            			
            			
            			
            		}
            		
            		System.out.println();
            		writer.append("\n");
            		counter++;
            	}
            	
            	
            }
			System.out.println(counter + " Unknown:" + noop);
			reader.close();
			writer.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}

}
