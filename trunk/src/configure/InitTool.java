package configure;
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

import org.json.JSONException;

import semkit.semanticresources.NLGbAseRedisInit;
import semkit.semanticresources.NLGbAseRedisLoadMetadata;


/**
 * 
 * The Tool allows to activate various functions of the SDK from
 * a command line. See main for parameters.</br>
 * 
 * @author ericcharton
 *
 */
public class InitTool {

	
	
	
	/**
	 * 
	 * Main for various command lines related to application initialization.</br> 
	 * </br>
	 * </br>
	 * java -cp semlinker.jar configure.InitTool -redisinit</br>
	 * - Initialize the Redis Base (remove all the content)</br>
	 * </br>
	 * java -cp semlinker.jar configure.InitTool -metadataload filename_and_path</br>
	 * - Load NLGbAse metadata in the Redis base from the metadata file (see <a href="http://www.nlgbase.org">www.nlgbase.org</a>)
	 * 
	 * @param args
	 * @throws JSONException
	 */
	public static void main(String[] args) throws JSONException {
		
		
		// path of metadata base
		String MetadataPath = null;
		int command = 0;
				
		// manage command lines
		for (int x=0; x < args.length; x++){
					
			try{
					// help
					if ( args[x].matches("-h")){
						
						System.out.println("-Help:");
						System.out.println(" 	--metadataload (name of metadata file) ");
						System.out.println(" 	--redisinit ");
						System.exit(0); // help always overrides others
					}
					
					if ( args[x].contains("-metadataload") ){
						
						
						MetadataPath = args[x+1];
						System.out.println("--Loading metadata from " + MetadataPath);
						command = 1;
					}
				
					
					if ( args[x].contains("-keyload") ){
						
						
						MetadataPath = args[x+1];
						System.out.println("--Loading keys from idx " + MetadataPath);
						command = 3;
					}
					
					if ( args[x].contains("-redisinit") ){
						
						System.out.println("--Initialize Redis base");
						command = 2;
					}
					
					
					
			} catch(Exception e){
				// Error
				System.out.println("An error occured, please check your command line instruction");
				System.exit(0); 
			}
					
		}
		
		
		// Loading metadata if not exist
		if (command == 1){
			
			// Manage metadata and initialization of Redis base
			NLGbAseRedisLoadMetadata redisobject = new NLGbAseRedisLoadMetadata(MetadataPath);
			
			// test if the base is loaded
			if ( redisobject.NLGbAseVerifyLoad() != 1){
				
				System.out.println("Loading NLGbAse " + MetadataPath + " This is a long process (more than one hour).");
				redisobject.jedisload(MetadataPath);
				System.exit(0);
				
			}else{
				
				System.out.println("A NLGbAse version is already loaded, please Init Redis first");
				
			}
			
		}
		
		// Loading keywords
		if (command == 3){
									
			// Manage metadata and initialization of Redis base
			NLGbAseRedisLoadMetadata redisobject = new NLGbAseRedisLoadMetadata(MetadataPath);
						
			// test if the base is loaded
			if ( redisobject.NLGbAseVerifyLoad() != 1){
							
						System.out.println("Loading NLGbAse " + MetadataPath + " This is a long process (more than one hour).");
						redisobject.keywordload(MetadataPath);
						System.exit(0);
							
				}else{
							
							System.out.println("A NLGbAse keyword version is already loaded, please Init Redis first");
							
				}
									
		}
		
		
		// Flush database
		if (command == 2){
					
					NLGbAseRedisInit initRedis = new NLGbAseRedisInit();
					
		}
		
	}
	
}
