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

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import configure.NistKBPConfiguration;

/**
 * 
 * This class retrieves a document from the KBP collection using Lucene index. 
 * 
 * @author ericcharton
 * @see kbp2013.index.IndexSourceCorpus
 *
 */
public class IndexedDocumentCollection {
	
	
	IndexReader reader;
	IndexSearcher searcher;
	Analyzer analyzer;
	QueryParser parser;
	Query query;
	
	/**
	 * 
	 * Field that define the name of the query
	 */
	private String field = "id";
	/**
	 * Path to the folder where the Lucene index is
	 */
	private String index = (new NistKBPConfiguration()).INDEX;
	
	/**
	 * 
	 * Constructor
	 * 
	 */
	public IndexedDocumentCollection(){
		
		try {
			reader = DirectoryReader.open(FSDirectory.open(new File(index)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		searcher = new IndexSearcher(reader);
		// analyzer = new StandardAnalyzer(Version.LUCENE_44); // old LUCENE_40
		analyzer = new KeywordAnalyzer(); // old LUCENE_40
		parser = new QueryParser(Version.LUCENE_44, field, analyzer);
	}
	
	
	/**
	 * Get a KB doc from the Lucene index
	 * 
	 * 
	 * @param nameOfDoc
	 * @return
	 */
	public String getDocFromIR(String nameOfDoc){
		
		// normalize and clean nameOfDoc
		nameOfDoc = nameOfDoc.replaceAll("-", "_");
		nameOfDoc = nameOfDoc.toLowerCase(); // Normalize according to the indexed format
		 
		int hitsPerPage = 10;
		String content = null;
				
		try {
			
			query = parser.parse(nameOfDoc);
			
			// Collect enough docs to show 5 pages    
			TopDocs results = searcher.search(query, 5 * hitsPerPage);
			ScoreDoc[] hits = results.scoreDocs;
			
		
			for (int x = 0; x < hits.length; x++){				
				Document doc = searcher.doc(hits[x].doc);
				content= doc.get("text");               
			}			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return content;
	}
}
