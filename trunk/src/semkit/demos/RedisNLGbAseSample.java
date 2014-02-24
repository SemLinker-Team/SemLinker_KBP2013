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

import java.util.ArrayList;

import org.json.JSONException;

import configure.SemkitConfiguration;
import semkit.semanticresources.NLGbAseRedisAccessMetadata;
import semkit.semanticresources.NLGbAseRedisLoadMetadata;


/**
 * 
 * This sample helps to use and understand the Redis Base and NLGbAse metadata access.<br>
 * <br>
 * Command line samples:<br>
 * <br>
 * - How to read a metadata for a given entity (entity using wikipedia key):<br>
 * <br>
 * java -cp semlinker.jar semkit.demos.RedisNLGbAseSample -TestEnt Canada<br>
 * <br>
 * - How to find available metadata for a given surface form:<br>
 * <br>
 * java -cp semlinker semkit.demos.RedisNLGbAseSample -TestKey challenger<br>
 * <br>
 * <b>CAUTION: do not forget to have Redis Base and server active !!!</b>
 * 
 * @author ericcharton
 *
 */
public class RedisNLGbAseSample {


	/**
	 * 
	 * This main entry point verifies if the Redis base manager is active and
	 * loaded with current NLGbAse metadata. <br>
	 * <br>
	 * If it is not loaded, the Redis server is called and each metadata is
	 * charged in memory. Please note that you have to define the metadata path in the
	 * SemanticConstants.java class or to give it in command line. <br>
	 * <br>
	 * Those metadata can be loaded freely from your 
	 * wikimeta account (<a href="http://www.wikimeta.com">see www.wikimeta.com</a>).<br>
	 * <br>
	 * Finally the program gives samples of Redis call for retrieving metadata.
	 * 
	 * 
	 * 
	 * @param args
	 * @throws JSONException
	 */
	public static void main(String[] args) throws JSONException {

		String StringToMatch = null;
		int option = 0; 

		// manage command line
		for (int x=0; x < args.length; x++){

			// help
			if ( args[x].matches("-h")){

				System.out.println("-Help: --metadata (metadata file) ");
				System.exit(0); // help always overrides others
			}

			if ( args[x].contains("-TestEnt") ){

				StringToMatch = args[x+1];
				option =1;
				System.out.println("--Searching metadata for Entity " + StringToMatch);

			}

			if ( args[x].contains("-TestKey") ){

				StringToMatch = args[x+1];
				option =2;	
				System.out.println("--Searching keys for given form " + StringToMatch);

			}
			
	
		}

	
		
		/*
		 * 
		 * Get back an entry and all its surface forms
		 * 
		 * 
		 */
		NLGbAseRedisAccessMetadata metadata = new NLGbAseRedisAccessMetadata();
		
		// verify status of the base
		if (metadata.getJedisStatus() == -1){
			
			System.out.println("Problem with the Redis Base pelase check your configuration");
			System.out.println(" - Do you have a Redis Base installed and loaded ?");
			System.out.println(" - Is the jedis server starded with ./redis-server ?");
			System.out.println(" - Do you have acces to the Redis base socket (firewall, etc)?");
			System.exit(0);
			
		}
		
		/*
		 * 
		 * Test sequence from console as a NE entry
		 * 
		 * 
		 */
		if (StringToMatch != null && option == 1){

			String ne = metadata.getNE(StringToMatch);
			if (ne != null ){

				System.out.println("Named Entity:" + ne);
				ArrayList<String> wordList = metadata.getSF(StringToMatch);
				// display all the surface forms
				if (wordList != null ){
					System.out.println("Surface forms:");
					for ( String surfaceForm : wordList){
						System.out.println(" sf:" + surfaceForm);
					}
				}else{
					System.out.println("Don't exists");
				}


			}else{
				System.out.println("Don't exists");
			}

			System.exit(0);
		}

		/*
		 * 
		 * Test sequence from console as a surface form entry
		 * 
		 * 
		 */
		if (StringToMatch != null && option == 2){

			ArrayList<String> keyList = metadata.getUniqueKey(StringToMatch);
			// display all the surface forms
			if (keyList != null ){
				for ( String SourceKey : keyList){
					System.out.println(" Keys:" + SourceKey);
				}
			}else{
				System.out.println("Don't exists");
			}

			System.exit(0);
		}


		
		/*
		 * 
		 * Ready to use samples only in option 0
		 * 
		 * 
		 */
		
		if (option == 0){
		
			System.out.println("\nRetrieving NLGbAse NE from Alabama aka:");
			String ne = metadata.getNE("Alabama");
			if (ne != null ){
				System.out.println(" " + ne);
			}else{
				System.out.println("Don't exists");
			}
	
			// Get all the surface forms and display them
			System.out.println("\nRetrieving NLGbAse surface forms redis for Paris key:");
			ArrayList<String> wordList = metadata.getSF("Paris");
			// display all the surface forms
			if (wordList != null ){
				for ( String surfaceForm : wordList){
					System.out.println(" sf:" + surfaceForm);
				}
			}else{
				System.out.println("Don't exists");
			}
	
			/*
			 * 
			 * 
			 * Get all the keys referred by a surface form
			 * 
			 * 
			 */
			System.out.println("\nRetrieving NLGbAse keys from redis for paname key:");
			ArrayList<String> keyList = metadata.getUniqueKey("paname");
			// display all the surface forms
			if (keyList != null ){
				for ( String SourceKey : keyList){
					System.out.println(" Keys:" + SourceKey);
				}
			}else{
				System.out.println("Do not exist");
			}
		}
	}
}
