/*
 * 
 * KBP2013 Package is a set of classes used to deploy
 * a system on NIST KBP 2012 and 2013 evaluation campaign.  
 * 
 */
package kbp2013.managedocuments;

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

/**
 * 
 * This class implements various methods for query corrections 
 * 
 * @author eric
 *
 */
public class checkSuggestion {
	
	
	/**
	 * 
	 * 
	 * This class is the implementation of the query reformulation described in LREC 2014 paper
	 * it receive the original mention, the suggested one and return the choosen one. 
	 * 
	 * @param originalMention
	 * @param suggestedMention
	 * @return
	 */
	public static String validateARewriting(String originalMention, String suggestedMention){
		
		// constantes de réglage
		int NOTCOMMONLETTERS = 2;
		
		try{
						// preprocessing
						String originalMentionCleaned = originalMention.toLowerCase();
						String suggestedMentionCleaned = suggestedMention.toLowerCase();
						
						// remove 's
						// Zimbabwe's Daily News	Zimbabwe Daily News -> accept
						originalMentionCleaned = originalMentionCleaned.replaceAll("'s", "");
						suggestedMentionCleaned = suggestedMentionCleaned.replaceAll("'s", "");
						
						// absolument identiques
						if ( originalMentionCleaned.contentEquals(suggestedMentionCleaned) ){
							System.out.println("[checkSuggestion]Identical after cleaning " + suggestedMention);
							return(suggestedMention);
						}
						
						// distance
						// Howeird	Howard -> reject
						// Cesare Geronzi	Cesare Geraci
						char[] first  = originalMentionCleaned.toLowerCase().toCharArray();
						char[] second = suggestedMentionCleaned.toLowerCase().toCharArray();
						int counter = 0;
						for (int u = 0; u< first.length; u++){
							
							for (int v=0; v< second.length; v++){
								
								if (first[u] == second[v] && first[u] != 0 && second[v] != 0){
									
										first[u] = 0;
										second[v]= 0;
										counter++;
								}
							}			
						}
						if ( counter >= originalMentionCleaned.length() - NOTCOMMONLETTERS) {
							System.out.println("[checkSuggestion]Accepted similarity " + counter + "/" + suggestedMentionCleaned.length() + " " +suggestedMention);
							return(suggestedMention);
						}
						
						
						// Firestone Vineyards	Firestone Vineyard -> accept
						// American Motorcyclists Association	American Motorcyclist Association ->accept

		}catch (Exception e){
			//System.out.println("[checkSuggestion]Error: " + originalMention + " / " + suggestedMention + e);
			System.out.println("[checkSuggestion]Null proposition");
		}
					
		System.out.println("[checkSuggestion]Rejected " + suggestedMention);
		return null;
	}

}
