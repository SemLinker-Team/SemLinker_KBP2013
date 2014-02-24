package evaluation;

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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import kbp2013.tools.LoadTestRef;
import kbp2013.tools.ManageQueries;
import kbp2013.tools.ManageQueries.Query;
import configure.NistKBPConfiguration;

/**
 * 
 * Perform various analysis on results according to a reference.
 * 
 * @author ericcharton
 *
 */
public class AnalyseELKBP {

	
	/**
	 * 
	 * Perform various analysis on results according to a reference.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
	

		
		// Instantiate class of constants
	    NistKBPConfiguration KBvars = new NistKBPConfiguration();
		// Instantiate class of query manager
	    ManageQueries managequeries = new ManageQueries();
	   

	
	    try {
        	
        	
        	//--------------------
        	// open experience files
        	//--------------------
        	// output result reader
        	BufferedReader results = new BufferedReader(new FileReader(KBvars.PATH_TO_TRAIN_CLUSTERED_OUTPUT)); // the query output
        	 // load reference (if exists) - not in eval mode
            LoadTestRef testreference = new LoadTestRef(KBvars.PATH_TO_TRAIN_REF);
            
        	System.out.println("Reading " + KBvars.PATH_TO_TRAIN_OUTPUT);
        	
        	//--------------------
        	// begin browsing
        	//--------------------

            String text = null;
            int nbQueries = 0;
            Query query;
            
            int good = 0;
            int bad = 0;
            int goodnil = 0;
            int allkb = 0;
            int allnil = 0;
            
            while ((text = results.readLine()) != null) {

            	
            			String[] resultsOfel = text.split("\t");
            			if (resultsOfel.length > 1){
	            			String goodresult = testreference.returnQueryKBRef(resultsOfel[0]);
	            			System.out.println(text + " " + goodresult);
	            			
	            			if ( resultsOfel[1].contains(goodresult) ){
	            				good++;
	            			}
	            			
	            			if ( resultsOfel[1].contains("NIL") && goodresult.contains("NIL")){
	            				goodnil++;
	            			}
	            			
	            			if ( ! goodresult.contains("NIL")){
	            				allkb++;
	            			}else{
	            				allnil++;
	            			}
	            			
	            			
            			}
            }
           
            System.out.println("Correct KB  :" + good + " (" + allkb + " all)");
            System.out.println("Related NIL :" + goodnil+ " (" + allnil + " all)");
            
            results.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	    
	}
	
}
