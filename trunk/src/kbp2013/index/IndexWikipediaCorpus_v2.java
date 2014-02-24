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

import java.io.BufferedInputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import org.apache.commons.lang.StringUtils;
import org.itadaki.bzip2.BZip2InputStream;

/**
 * 
 * Index all Wikipedia articles in a Lucene index using a XML dump.<br>
 * please collect the dumps at <a href="http://download.wikipedia.org">download.wikipedia.org</a>.
 * 
 * @author ludovicjeanlouis
 */
public class IndexWikipediaCorpus_v2 {

    //--------------------------------------
    // Indexation mode
    //--------------------------------------
	/**
	 * Set to :<br>
	 * 1 to initialize all the index<br>
	 * 0 to add files to an existing index (save time in case of crash)
	 * 
	 */
    private static int create = 1; 
    /**
     * Include a checking index process in case of crash.
     */
    private static int checkindex = 0;

    /**
     * 
     * Constructor
     * 
     */
    private IndexWikipediaCorpus_v2() {
    }
    
    static File INDEX_DIR;
    private static String wikiluceneIndex;    
    private static String wikidump;

    public static void initializeFromDefault() {

        NistKBPConfiguration lucenvars = new NistKBPConfiguration();

        wikidump = lucenvars.WIKI_CORPUS_FILE;
        wikiluceneIndex = lucenvars.INDEX_WIKIPEDIA;

    }

    /**
     * 
     * 
     * @param args
     * @throws IOException
     * @throws Exception
     */
    public static void main(String[] args) throws IOException, Exception {

        initializeFromDefault();        

        System.out.println("Indexing Wikipedia Dump to directory '" + wikiluceneIndex + "'...");

        INDEX_DIR = new File(wikiluceneIndex);
        if (INDEX_DIR.exists() && create == 1) {
            System.out.println("Cannot save index to '" + INDEX_DIR + "' directory, please delete it first");
            System.exit(1);
        }

        if (wikidump.endsWith(".bzip2") == false) {
            System.out.println("NOTICE: The Wikipedia dump must be in bzip2 format.");
            System.exit(0);
        }


        Directory dir = FSDirectory.open(new File(wikiluceneIndex));

        // Open lucene stuff
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_44);
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_44, analyzer);
        // configure Lucene Stuff
        iwc.setMaxThreadStates(100);

        // manage append mode
        if (create == 0) {
            // add new document to an existing index
            iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
            // if appending, check index
            if (checkindex == 1) {
                System.out.println("Checking index ...");
                CheckIndex ci = new CheckIndex(dir);
                ci.checkIndex();
                System.out.println("End of Checking index");
            }

        } else {
            iwc.setOpenMode(OpenMode.CREATE);
        }

        // build writer
        IndexWriter writer = new IndexWriter(dir, iwc);



        // --------------------------
        //
        // Open the Wikipedia Dump
        //
        //---------------------------
        //Processing the large xml file in bzip2 format
        InputStream fileInputStream = new BufferedInputStream(new FileInputStream(wikidump));
        BZip2InputStream inputStream = new BZip2InputStream(fileInputStream, false);

        InputStreamReader isr = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(isr);

        String line;
        //temporary stores the content of each file
        StringBuilder pageBuffer = new StringBuilder();
        //contains the title of the current page
        String docTitle = "";
        //contains the content of the current page
        String content = "";

        int docCount = 0; // number of documents that have been stored
        Date start = new Date(); //log the time when the indexing process starts

        while ((line = reader.readLine()) != null) {
            if (StringUtils.contains(line, "</page>") == true) {
                if (pageBuffer.length() > 0) {
                    //get the title of the page
                    int startIndex = pageBuffer.toString().indexOf("<title>") + 7;
                    int endIndex = pageBuffer.toString().indexOf("</title>");
                    docTitle = pageBuffer.toString().substring(startIndex, endIndex);
                    //get the content of the page
                    int startPageIndex = pageBuffer.toString().indexOf("<page>");
                    content = pageBuffer.toString().substring(startPageIndex) + "</page>";
                    //verify the namespace of the page, it should be 0
                    int namespaceValue = Integer.parseInt(content.substring(content.indexOf("<ns>") + 4, content.indexOf("</ns>")));
                    if (namespaceValue != 0) {
                        //reset buffer
                        pageBuffer = new StringBuilder();
                        continue;
                    }
                    //verify that it is not a redirect page
                    if (content.indexOf("<text xml:space=\"preserve\">#REDIRECT") != -1) {
                        //reset buffer
                        pageBuffer = new StringBuilder();
                        continue;
                    } else {
                        indexDocument(writer, content, docTitle.toLowerCase());
                        System.err.println("Processed " + docCount + " documents");
                    }
                    docCount++;
                    
                }
                //reset buffer
                pageBuffer = new StringBuilder();
            }
            pageBuffer.append(line);

        }
        fileInputStream.close();
        writer.close();

        Date end = new Date();

        // close properly the index writer
        // !! Caution !! in case of error, if this is not closed, the index is corrupted
        // and have to be regenerated
        reader.close();

        System.err.println(end.getTime() - start.getTime() + " total milliseconds");

    }

    /**
     * 
     * 
     * @param index_writer
     * @param doc_content
     * @param doc_title
     * @throws IOException
     * @throws Exception
     */
    private static void indexDocument(IndexWriter index_writer, String doc_content, String doc_title) throws IOException, Exception {
    	
        Document doc = new Document();
        doc.add(new StringField("text", doc_content, Field.Store.YES));
        doc.add(new StringField("title", doc_title, Field.Store.YES));

        index_writer.addDocument(doc);
    }
}