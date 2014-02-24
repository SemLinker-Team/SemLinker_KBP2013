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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import kbp2013.LinkEntities;
import kbp2013.managedocuments.QueryProcessing;

/**
 * 
 * Class managing a query according to KBP EL format. 
 * 
 * @author ericcharton
 *
 */
public class ManageQueries {

	/**
	 * 
	 * Define the representation of a Query
	 * 
	 */
    public static class Query {

        public String id = "";
        public Integer number = 0;
        public String docid = "";
        public String name = "";
        public String normalizedName = "";
        public String originalQuery = "";
        public Integer beg = 0;
        public Integer end = 0;
    }

    /**
     *  To remove markup from a line
     * @param text
     * @param tag
     * @return
     */
    private static String filterMarkup(String text, String tag) {
        String result = text;
        String openHtmlTag = "<" + tag + ">";
        String closeHtmlTag = "</" + tag + ">";

        result = result.replace(openHtmlTag, "");
        result = result.replace(closeHtmlTag, "");
        
        return result;
    }

    /**
     * 
     * @param text
     * @return
     */
    private static String stripSpaces(String text) {
        String result = text;
        result = result.replaceAll("^[ ]+", "");
        result = result.replaceAll("[ ]+$", "");

        return result;
    }

    /**
     * 
     * @param text
     * @return
     */
    public static String extractQueryId(String text) {
        // get the Query Id
        String queryId = text;

        queryId = queryId.replace("<query id=\"", "");
        queryId = queryId.replace("\">", "");
        queryId = queryId.replaceAll("^[ ]+", "");

        return queryId;
    }
    
    
    /**
     * 
     * Extract Query Data from a XML KBP formated file. 
     * 
     * 
     * @param reader	File reader of the XML file at the current position of a Query entry
     * @param queryId   
     * @param queryNumber
     * @return	A Query object
     */
    public static Query extractQueryData(BufferedReader reader, String queryId, Integer queryNumber) {
    	
        Query query = new Query();
        query.id = queryId;
        query.number = queryNumber;

        String text = "";
        while (!text.contains("</query")) {
            try {
                text = reader.readLine();
            } catch (IOException ex) {
                Logger.getLogger(LinkEntities.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (text.contains("<name>")) {
            
                query.name = filterMarkup(stripSpaces(text), "name"); // collect query from file
                query.originalQuery = query.name; // this will be used one time to synchronize the document occurrences of query with new query corrected
                query.normalizedName = QueryProcessing.normalize(query.name); // normalize query mention for later search in link or in kbannotator
                
            }
            if (text.contains("<docid>")) {
                query.docid = filterMarkup(stripSpaces(text), "docid");
            }
            if (text.contains("<beg>")) {
                query.beg = Integer.parseInt(filterMarkup(stripSpaces(text), "beg"));
            }
            if (text.contains("<end>")) {
                query.end = Integer.parseInt(filterMarkup(stripSpaces(text), "end"));
            }
        }

        return query;
    }

    /**
     * 
     * Return a formated display in text for a given query.
     * 
     * @param query
     * @return
     */
    public static String createQueryInfo(Query query) {
        return "------------------------------------------------------------------------------\n"
                + query.number + ":Managing query "
                + query.id + " ["
                + query.originalQuery + "]["
                //+ query.name + "]["
                //+ query.normalizedName + "] "
                + query.docid + " "
                + query.beg + " "
                + query.end;
    }

}
