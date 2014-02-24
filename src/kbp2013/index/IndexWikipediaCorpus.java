package kbp2013.index;

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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CheckIndex;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;

import configure.NistKBPConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


/** 
 * 
 * Index all text files under a directory. 
 * 
 * @deprecated This class is replaced by V2. 
 * @author ericcharton
 * @see kbp2013.index.IndexWikipediaCorpus_v2
 * 
 * */
public class IndexWikipediaCorpus {
	
	
	//--------------------------------------
	// Indexation mode
	//--------------------------------------
	private static int create = 1; // 1 -> initialize index, do not re-write  / 0 -> append to existing
	private static int checkindex = 0 ;
	
  
	private IndexWikipediaCorpus() {}
	private static String home;
	private static String index = "index";
	static File INDEX_DIR = new File(home+index);

	private static String wikiluceneIndex;
	private static String homelist;
	private static String wikidump;
	
	public static void initializeFromDefault() {
	
			NistKBPConfiguration lucenvars = new NistKBPConfiguration();
		
			home = lucenvars.KBP_CORPUS_PATH;
			wikidump = lucenvars.WIKI_CORPUS_FILE;
			wikiluceneIndex = lucenvars.INDEX_WIKIPEDIA;
	
	}
	
	public static void main(String[] args) throws IOException{
		
		initializeFromDefault();
		
		int managed = 0; // counter to count idents
		int counted = 0; // when to display
		int tocount = 1000;
		int saved = 0;
		
		System.out.println("Indexing Wikipedia Dump to directory '" + wikiluceneIndex + "'...");
		
		INDEX_DIR = new File(wikiluceneIndex);
		if (INDEX_DIR.exists() && create == 1) {
		  System.out.println("Cannot save index to '" +INDEX_DIR+ "' directory, please delete it first");
		  System.exit(1);
		}
		
		
		Directory dir = FSDirectory.open(new File(wikiluceneIndex));

		// Open lucene stuff 
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_44);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_44, analyzer);
		// configure Lucene Stuff
		iwc.setMaxThreadStates(100);
		
		// manage append mode
		if (create == 0){
			// add new document to an existing index
			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			// if appending, check index
			if (checkindex == 1){
				System.out.println("Checking index ...");
				CheckIndex ci = new CheckIndex(dir );
				ci.checkIndex();
				System.out.println("End of Checking index");
			}
			
		}else{
			iwc.setOpenMode(OpenMode.CREATE);		
		}
		
		// build writer
		IndexWriter writer = new IndexWriter(dir, iwc);
	
		
	
		// --------------------------
		//
		// Open the Wikipedia Dump
		//
		//---------------------------
		BufferedReader reader = new BufferedReader(new FileReader(wikidump));
		
		// read the domains
		String text = "";
		ArrayList domain = new ArrayList(); // the content retrieved according to the page key
			
		while ( ! text.contains("</siteinfo>") ) 
        { 
			text = reader.readLine();
			if (text.contains("<namespace key=") && ! text.contains("<namespace key=\"0")){
				
				String thisnamespace = text.replaceAll("<namespace key=[^>]+>", "");
				thisnamespace = thisnamespace.replaceAll("</namespace>", "");
				thisnamespace = thisnamespace.replaceAll("^[ ]+", "");
				thisnamespace = thisnamespace + ":";
				if ( ! thisnamespace.contentEquals("") ) {
					domain.add(thisnamespace);
					System.out.println("Registered domain:" + thisnamespace + ";");	
				}
			}
        }
		
		System.out.println("--------------------------------");
		
		// read the pages
		while ((text = reader.readLine()) != null) 
        { 
			
			
				String textdoc = ""; // inside the file, the reader for the document
				String pagename = "";
				boolean tosave = true;
				
				// beginning of a page
				// accumulate
				if (text.contains("<page>") ){
					
					textdoc = text;
					
					while ( ! text.contains("</page>") )
					{
						text = reader.readLine();
						textdoc= textdoc + text;
						
						if (text.contains("<title>")){
							
							pagename = text.replaceAll("<title>", "");
							pagename = pagename.replaceAll("</title>", "");
							pagename = pagename.replaceAll("[ ]{2,10}", "");
							//System.out.println("Page:" + pagename);
							
						}
						
						// safety
						
					}
					
					// after page reading index document
					// verify if document 
					//         A) is not a redirect
					//         B) is not from a domain
					for (int a = 0; a < domain.size(); a++){
						String domaintosearch = domain.get(a).toString();
						if (pagename.toLowerCase().contains(domaintosearch.toLowerCase())) 
						{
							System.out.println("Specific page:" + pagename);
							tosave = false;
						}
					}
					/*
					if (textdoc.contains("[A-Za-z ]+:")){
						System.out.println("Specific page domain:" + pagename);
						tosave = false;
					}*/
					if (textdoc.contains("#REDIRECT")){
						// System.out.println("Redirect:" + pagename);
						tosave = false;
					}
					
					if (tosave ) 
						{
						saved++;
						indexDocs(writer, pagename , textdoc);
						}
							
					// display info
					managed++;
					counted++;
							
					if (managed > tocount){
							managed = 0;
							System.out.println(counted+":" + saved + ":" + pagename + ":------>" + textdoc.length() );
							// System.out.println(textdoc);
							writer.commit();
					}
				}
				
				
		} // end while
			
		
		// close properly the index writer 
		// !! Caution !! in case of error, if this is not closed, the index is corrupted
		// and has to be regenerated
		writer.close();
		reader.close();
		
	}
	
	static void indexDocs(IndexWriter writer, String srcID, String text){
		
		
		Document doc = new Document();
		
		srcID = srcID.replace("-", "_"); // normalize
		srcID = srcID.toLowerCase();
		
		// Use StringField for id fields and Textfield for tokenized file
		// caution : do not make mistake with the reference obj (use Lucene not Java)
		doc.add(new StringField("title", srcID, Field.Store.YES));
		doc.add(new StringField("text", text, Field.Store.YES));
		
		
		try {
			
			writer.addDocument(doc);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("This is IOException in Writer");
			e.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("This is an Exception in Writer");
			e.printStackTrace();
		}
		
		
	}

}