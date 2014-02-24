package nlp.decoders;

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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * 
 * @author ericcharton
 *
 */
public class Stoplist {

	
	/**
	 * 
	 * Hashmap to store the list
	 * 
	 */
	private HashMap<String, Integer> stoplist = new HashMap<String, Integer>(); // form prop -> uri
			
	
	/**
	 * 
	 * Constructor build the stoplist
	 * 
	 * 
	 */
	public Stoplist(String stopListPath){
		
		
		try 
        {
			
			BufferedReader reader = new BufferedReader(new FileReader(stopListPath));
            String text = null;
            	
            //--------------------------------------------
            // repeat until all lines are read
            // from the file
            //--------------------------------------------
            while ((text = reader.readLine()) != null) 
            {
            	
            	stoplist.put(text, 0);
            	// System.out.print(text + ":");
            	
            }
        
            reader.close();
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } 
		
	}
	
	/**
	 * Return sentence without stop words
	 * 
	 * @param sentence
	 * @return
	 */
	public String returnStoped(String sentence){
		
		String sendsentenceback = "";
		String[] stoptab = sentence.split(" ");
		
		for (int x =0; x< stoptab.length; x++){
			
			if (! stoplist.containsKey(stoptab[x])){
				
				sendsentenceback = sendsentenceback + stoptab[x] + " ";
				
			}else{
				
				// System.out.println(stoptab[x]  + " removed");
			}
			
		}
		
		
		return sendsentenceback;
		
	}
	
	
	/**
	 * 
	 * Remove stopped punctuation
	 * 
	 * @param sentence
	 * @return
	 */
	public String returnStopedPunct(String sentence){
		
		
		sentence = sentence.replaceAll("[;.?!:]", "");
		
		return  sentence;
		
	}
	
	/**
	 * 
	 * Return the status of a word according to stoplist
	 * 
	 * @param word
	 * @return true if exist in stoplist
	 */
	public boolean getStop(String word){
		
		boolean stopStatus = false;
		
		if (stoplist.containsKey(word)){
			stopStatus = true;
		}
		
		return(stopStatus);
		
	}
	
	
}
