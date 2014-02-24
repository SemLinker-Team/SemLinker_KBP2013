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

/** 
 * Index all text files under a directory. 
 * 
 * @deprecated This class is replaced by V2. 
 * @author ericcharton
 * @see kbp2013.index.IndexSourceCorpus_v2
*/
public class IndexSourceCorpus {
	
	
	//--------------------------------------
	// Indexation mode
	//--------------------------------------
	private static int create = 1; // 1 -> initialize index, and do not re-write / 0 -> append to existing
	private static int checkindex = 0 ;
	
  
	private IndexSourceCorpus() {}
	private static String home;
	private static String index = "index";
	static File INDEX_DIR = new File(home+index);

	private static String luceneIndex;
	private static String homelist;
	
	
	public static void initializeFromDefault() {
	
			NistKBPConfiguration lucenvars = new NistKBPConfiguration();
		
			home = lucenvars.KBP_CORPUS_PATH;
			homelist = lucenvars.KBP_CORPUS_FILES;
			luceneIndex = lucenvars.INDEX;
	
	}
	
	public static void main(String[] args) throws IOException{
		
		initializeFromDefault();
		
		int managed = 0; // counter to count idents
		int counted = 0; // when to display
		int tocount = 10;
		
		System.out.println("Indexing to directory '" + luceneIndex + "'...");
		
		INDEX_DIR = new File(luceneIndex);
		if (INDEX_DIR.exists() && create == 1) {
		  System.out.println("Cannot save index to '" +INDEX_DIR+ "' directory, please delete it first");
		  System.exit(1);
		}
		
		
		Directory dir = FSDirectory.open(new File(luceneIndex));

		// Open lucene stuff 
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_43, analyzer);
		// iwc.setRAMBufferSizeMB(1024); // http://wiki.apache.org/lucene-java/ImproveIndexingSpeed
		iwc.setMaxThreadStates(100);
		
		// manage append mode
		if (create == 0){
			// add new document to an existing index
			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			// if appending, checkindex
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
	
		
	
		
		final File docDir = new File(home);
		System.out.println("Indexing directory '" + home + "'...");
		if (!docDir.exists() || !docDir.canRead()) {
			System.out.println("Document directory '" +docDir.getAbsolutePath()+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}
		
		// read all the files
		BufferedReader reader = new BufferedReader(new FileReader(homelist));
		
		// read line by line each file name
		String text = "";
		boolean verbose = true;
		
		while ((text = reader.readLine()) != null) 
        { 
			
			String filename = home + text;
			final File testFile = new File(filename);
		
			// verbose - remove from one line files
			if (verbose) { System.out.println("---V-->" + "Indexing content of " + filename);}
		
			if (testFile.isFile() && ! filename.contains("\\.gz")) {
							
				// open file and read
				FileReader fread = new FileReader(filename);
				BufferedReader readerDoc = new BufferedReader(fread);
				
				// initialize variable for loop
				String fileRef = ""; // the line containing the document id
				String fromfile = ""; // the first reader for all the file
				String textdoc = ""; // inside the file the reader for the document
				
				
				
				while ((fromfile = readerDoc.readLine()) != null) 
		        { 
					if (fromfile.toUpperCase().contains("<DOC ID=") || fromfile.toUpperCase().contains("<DOC>") ){
							
							String fromdoc = fromfile; // begin to index the DOCID (to keep good offset for collection of mention)
							textdoc = fromfile ; // initialize variable and keep the first line
							
							// accumulate all the content
							while(! fromdoc.toUpperCase().contains("</DOC>") ){
								
								// collect the doc id
								// store the current file ref
								// it can come :
								//     - from the last fromfile (first iteration)
								//     - from a current iteration of fromdoc (any iteration)
								
								if ( fromdoc.toUpperCase().contains("<DOC ID=") || fromdoc.toUpperCase().contains("<DOCID>") ) {
									fileRef = fromdoc;
								}
								
								// accumulate the complete document for later offset reading of mention
								fromdoc = readerDoc.readLine();
								textdoc = textdoc + "\n" + fromdoc;
								
								
							}
							
					
					
							// locate id
							// 2 forms
							// <DOCID> ALHURRA_NEWS13_ARB_20050412_130100-2.LDC2006E92 </DOCID>
							// <doc id="bolt-eng-DF-183-195681-7948494">
							// form 1
							String idStr = fileRef ;
							
							if (idStr.contains("<DOCID>")){
								idStr = idStr.replace("<DOCID>", "");
								idStr = idStr.replace("</DOCID>", "");
								idStr = idStr.replace(" ", ""); // retire l'espace
							}
							if (idStr.contains("<DOC id=")){
		
								idStr = idStr.replace("<DOC id=\"", "");
								idStr = idStr.replaceAll("\".+>$", "");
								//idStr = idStr.replaceAll("\">$", "");
							}		
							// lower case -> new corpus of LDC
							/*
							if (idStr.contains("<docid>")){
								idStr = idStr.replace("<docid>", "");
								idStr = idStr.replace("</docid>", "");
								idStr = idStr.replace(" ", ""); // retire l'espace
							}
							if (idStr.contains("<doc id=")){
		
								idStr = idStr.replace("<doc id=\"", "");
								idStr = idStr.replaceAll("\".+>$", "");
								// idStr = idStr.replaceAll("\">$", "");
							}		
							*/
					
							indexDocs(writer, idStr, textdoc);
							
							// display info
							managed++;
							counted++;
							
							// verbose remove for 1 doc files
							if (verbose) {  System.out.println("---V-->" + counted+":"+filename + ":" + idStr + ":" + textdoc.length()); }
							
							if (managed > tocount){
								managed = 0;
								System.out.println(counted+":"+filename + ":------>" + idStr);
								
								// clean the writer
								//writer.waitForMerges();
								//writer.forceMergeDeletes();
								writer.commit();
							}
					} // end of if
					
		        }// end of while
				readerDoc.close();
				fread.close();
				
			}else{
				
				System.out.println(counted + ":Non lisible ou non requis:" + filename);
				
			}
			
			
        }
		
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
		doc.add(new StringField("id", srcID, Field.Store.YES));
		doc.add(new StringField("text", text, Field.Store.YES));
		// doc.add(new StoredField("text", text));
		
		
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