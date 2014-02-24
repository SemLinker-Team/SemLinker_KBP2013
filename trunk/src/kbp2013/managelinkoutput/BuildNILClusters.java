package kbp2013.managelinkoutput;

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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 * 
 * Class devoted to cluster outputs from Entity Linking process
 * according to NIST NIL KBP rules.
 * 
 * 
 * @author ericcharton
 *
 */
public class BuildNILClusters {

	
	/**
	 * 
	 * Those are file in and file out path by default (this is a relative path).
	 * 
	 */
	private static String infile = null;
	private static String outfile = null;
	
	/**
	 * This class can be called from command line to allow 
	 * re-processing of an output file classification
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		
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
							if ( args[x].matches("-infile") ){
								infile = args[x+1];
							}
							
							// config file
							if ( args[x].matches("-outfile") ){
								outfile = args[x+1];
							}
							
					} catch(Exception e){
						// Error
						System.out.println("An error occured, please check your command line instruction");
						System.exit(0); 
					}
							
		}
		
		
		System.out.println("Reading " + infile);
		System.out.println("Outputing " + outfile);
		makeClusters();
		
	}
	
	
	
	
	
	/**
	 * 
	 * Constructor
	 * 
	 * @param fileIn  Name of file with annotation references
	 * @param fileOut Name of file used for generating the KBP experiment file
	 */
	public BuildNILClusters (String fileIn, String fileOut){
		
		infile = fileIn;
		outfile = fileOut;
		
		
	}
	
	
	/**
	 * 
	 * Build the clusters, take input file with linked entities
	 * and output clusters on file
	 * 
	 */
	public static void makeClusters(){
		
		// verify if the infile and outfile are set
		if (infile == null || outfile == null){
			
			System.out.println("[BuildNilCluster]Abort clustering: please define the input and output file from command line or from the constructor.");
			System.exit(0);
			
		}
		
		
		// define variable
		int idx = 0;
		String[][] toCluster = new String[3000][7]; // query - KBLink - qname - expanded qname - mentionExpanded - URI or NORDF|NIL - NE
		int nilClusterIdx = 0;
		
		//-----------------------------------
		// read the queries and results
		// and store them in array
		//-----------------------------------
		try {
			
			BufferedReader reader = new BufferedReader(new FileReader(infile));
						
			String text = null;
			
			
			while ((text = reader.readLine()) != null) 
            { 
				
				// extract information
				// EL_ENG_00761	NIL	sky blues	sky blues	sky blues	NORDF
				// EL_ENG_00758	E0588970	art buchwald	art buchwald	art buchwald 	http://wikimeta.com/wapi/display.pl?query=Art Buchwald&search=EN
				toCluster[idx] = text.split("\t");
				
				//
				System.out.println("Loading : " + idx + " " + text);
				
				// uniform NIL - NORDF 
				if (toCluster[idx][5].contains("NORDF")) {toCluster[idx][5] = "NIL";}
				
				idx++;
				
            }
			
			reader.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//-----------------------------------------------
		// Try to complete KB links with some other
		// queries with NIL (error correction)
		//-----------------------------------------------
		
		System.out.println();
		System.out.println("---------------------------------------------");
		System.out.println("   Attaching same KB with NIL of same size ");
		System.out.println("---------------------------------------------");
		
		for (int x= 0 ; x< idx; x++){
			
			// to make the clustering we have to verify if the sequence is more than 1 word long
			String[] verifySizeOfMention = toCluster[x][4].split(" ");
			
			// if already in KB
			if ( toCluster[x][1].matches("E[0-9]+") && verifySizeOfMention.length > 1 ){
				
				System.out.println("---------------------------------------------");
				System.out.println("        Managing KB: " +  toCluster[x][1] +  " " + toCluster[x][4] + " (Query: " + toCluster[x][0] + ")" );
				
				for (int y= x + 1 ; y< idx; y++){
					
					if ( toCluster[x][4].contentEquals(toCluster[y][4]) && toCluster[x][1].matches("NIL")  ){
						
						toCluster[y][1] = toCluster[x][1];
						
						System.out.println("                Rattaching Query to a KB: " + toCluster[y][0] + " "  + toCluster[y][4]);
						
					}
					
				}
				
			}
		}
		
		
		//-----------------------------------------------
		// build the clusters for NIL using URI queries -> NIL203
		//-----------------------------------------------
		
		System.out.println();
		System.out.println("---------------------------------------------");
		System.out.println("   Clustering by NIL with Links ");
		System.out.println("---------------------------------------------");
		
	
		for (int x= 0 ; x< idx; x++){
			
			if ( toCluster[x][1].contentEquals("NIL") && toCluster[x][5].contains("http://") ){
				
				String NilNumber = "NIL" + Integer.toString(nilClusterIdx);
				toCluster[x][1] = NilNumber;// initialize
				nilClusterIdx++;
				
				System.out.println("---------------------------------------------");
				System.out.println("NilNumber:" + NilNumber + " Query:" + toCluster[x][0] + " uri:" +  toCluster[x][5] + "         key:" + toCluster[x][2]);
				
				
				
				// Explore what is remaining in the array
				// -- look for the same uri for a NIL
				for (int y= x + 1 ; y< idx; y++){
				
					// A) if a URI is found
					// B) if this URI is identical 
					// C) if KB ref is not found yet
					if ( toCluster[x][5].contentEquals(toCluster[y][5]) &&  toCluster[y][5].contains("http://") && toCluster[y][1].contentEquals("NIL") ){
						
						System.out.println("     Cluster by uri:" + toCluster[y][0] + " " + toCluster[y][5] + "         key:" + toCluster[x][2]);
						// replace NIL by cluster Number
						toCluster[y][1] = NilNumber;
					}				
				}
				
			}
		}
		
		// ------------------------------------------------
		// Continue to search in the cluster with non uris 
		// ------------------------------------------------
		
		System.out.println();
		System.out.println("---------------------------------------------");
		System.out.println("   Clustering NIL by surface forms ");
		System.out.println("---------------------------------------------");
		
	
		for (int x= 0 ; x< idx; x++){
		
			// to make the clustering we have to verify if the sequence is more than 1 word long
			String[] verifySizeOfMention = toCluster[x][3].split(" ");
			
			// search 
			//    A) non numbered queries
			//    B) queries with at least 2 words
			if (toCluster[x][1].contentEquals("NIL") && verifySizeOfMention.length > 1){
		
				String NilNumber = "NIL" + Integer.toString(nilClusterIdx);
				toCluster[x][1] = NilNumber;
				nilClusterIdx++;
		
				System.out.println("---------------------------------------------");
				System.out.println("NilNumber:" + NilNumber + " " + " Query:" + toCluster[x][0] +  "         key:" + toCluster[x][3]);
				
				
				// -- look for the same structure of word sequences
				for (int y= x + 1 ; y< idx; y++){
				
					// if identical surface form && not already identified
					if (toCluster[x][3].contentEquals(toCluster[y][3]) && toCluster[y][1].contentEquals("NIL") ){
						
						System.out.println("     Cluster by Surface form:" + toCluster[y][0] + " " + toCluster[y][5] + "         key:" + toCluster[x][3]);
						// replace NIL by cluster Number
						toCluster[y][1] = NilNumber;
						
						
						
					}				
				}
				
			}
		}
		
		System.out.println();
		System.out.println("---------------------------------------------");
		System.out.println("   Affecting NIL Numbers to the remaining    ");
		System.out.println("   - Singleton finding");
		System.out.println("---------------------------------------------");
		
		// ------------------------------------------------
		// search all remaining NIL and give a key to
		// singletons
		// ------------------------------------------------
		for (int z= 0 ; z< idx; z++){
			
			// search non numbered queries
			if (toCluster[z][1].contentEquals("NIL")){
		
				String NilNumber = "NIL" + Integer.toString(nilClusterIdx);
				toCluster[z][1] = NilNumber;
				nilClusterIdx++;
				
				System.out.println("     Clustering remaining Singleton:" + toCluster[z][0] + " " + toCluster[z][5] + "         key:" + toCluster[z][2]);
			}
			
		}

		//-----------------------------------------------
		// output the results
		//-----------------------------------------------
		try {
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
			
			for (int x= 0 ; x< idx; x++){
				
				//// query - KBLink - qname - expanded qname - URI or NORDF|NIL - NE / confidence score
				writer.append(toCluster[x][0] + "\t" + toCluster[x][1]+ "\t0.99" + "\n");
				writer.flush();
			}
			
			
			writer.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
