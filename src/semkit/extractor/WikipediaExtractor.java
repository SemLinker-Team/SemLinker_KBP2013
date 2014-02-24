package semkit.extractor;

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
import java.util.HashMap;

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

import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

/**
 * 
 * This class is used to retrieve a document from a Lucene Base built from 
 * a Wikipedia XML dump or directly from online API calls to Wikipedia.
 * It allows to explore Wikipedia document and store in a hash the internal
 * links and the categories from the document. It is used for mutual disambiguation
 * algorithms built for NIST KBP 2013. 
 * 
 * @see kbp2013.index.IndexWikipediaCorpus_v2
 * 
 * @author ericcharton
 *
 */
public class WikipediaExtractor {

	IndexReader reader;
	static IndexSearcher searcher;
	Analyzer analyzer;
	static QueryParser parser;
	static Query query;

	private static boolean verbose = false;
	private static boolean docverbose = false;

	/**
	 * Declare a Wikibot for accessing the media wiki
	 */
	private static MediaWikiBot b = null;

	/**
	 * 
	 * Declare a path for a lucene index
	 * 
	 */
	private static String localLuceneIndex = null;

	// Variables to retrieve a page structure
	public static HashMap<String, String>  ArticleContent = new HashMap<String, String>(); // the content retrieved according to the page key
	public static HashMap<String, HashMap<String, Integer>> InternalLinks = new HashMap<String, HashMap<String, Integer>>(); // for a given page all the internal links
	public static HashMap<String, HashMap<String, Integer>>  InternalCategories =  new HashMap<String, HashMap<String, Integer>>(); // for a given page all the categories



	/**
	 * 
	 * Main for declaration and test purpose 
	 * 
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args)  {

		String ArticleName = "AZ_(rapper)"; // default name
		// get name from CL
		if (args.length > 0){
			ArticleName = args[0];
		}

		b = new MediaWikiBot("http://en.wikipedia.org/w/");
		getWikipediaDocument(ArticleName);

	}


	/**
	 * 
	 * Simple default constructor for online API calls
	 * 
	 */
	public WikipediaExtractor(){

		b = new MediaWikiBot("http://en.wikipedia.org/w/");

	}

	/** Constructor for Lucene wikipedia index
	 * 
	 * @param wikipediaLuceneIndexPath
	 */
	public WikipediaExtractor(String wikipediaLuceneIndexPath){

		String fieldToSearch = "title";
		localLuceneIndex = wikipediaLuceneIndexPath;

		try {
			reader = DirectoryReader.open(FSDirectory.open(new File(localLuceneIndex)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		searcher = new IndexSearcher(reader);
		//analyzer = new StandardAnalyzer(Version.LUCENE_44);
		analyzer = new KeywordAnalyzer();
		parser = new QueryParser(Version.LUCENE_44, fieldToSearch , analyzer);

	}

	/**
	 * 
	 * 
	 * @param wikipediakey
	 */
	public static void getWikipediaDocument(String wikipediakey ){


		if (verbose) System.out.print("Collecting :[" + wikipediakey + "]");
		if (wikipediakey.equals("")){ System.out.println(""); return;}

		// for measurement purpose
		int amountOfLinks = 0;
		int amountOfCats = 0;

		//-----------------------------------------
		// 
		// Get the Wikipedia page from
		//   A) Online API Call
		//   B) Local Index (faster)	
		//------------------------------------------
		String text = "";
		Article article = null;

		if (localLuceneIndex == null){

			article = b.getArticle(wikipediakey);
			text = article.getText();
			if (verbose) System.out.print(" [on line " + text.length() + "] ");

		}else{


			String queries = null;
			int repeat = 0;
			boolean raw = false;
			String queryString = null;
			int hitsPerPage = 10;
			String content = null;

			try {	

				// normalize key
				String keytosearch = wikipediakey.toLowerCase();
				keytosearch = "\"" + QueryParser.escape(keytosearch) + "\""; 
				// parse key
				query = parser.parse(keytosearch);
				if (verbose) System.out.print("Key searched:" + keytosearch);

				// Collect enough docs to show 5 pages    
				TopDocs results = searcher.search(query , 5 * hitsPerPage);
				ScoreDoc[] hits = results.scoreDocs;

				if (results.totalHits > 0){
					Document doc = searcher.doc(hits[0].doc);
					text = doc.get("text");	
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.print("[[Exception]]");
			}

			if (verbose) System.out.print(" [lucene:" + text.length() + "] ");
			if (docverbose) System.out.println(" [Text]#" + text);
		}


		//---------------------------------
		// 
		// From here 
		// managing article content and
		// extract semantic features
		//
		//---------------------------------


		// extracting categories
		// store the article
		ArticleContent.put(wikipediakey, text);

		// clean the article
		text = text.replaceAll("\\[\\[", "\n\\[\\[");
		text = text.replaceAll("\\]\\]", "\\]\\]\n");
		String[] content = text.split("\n");

		// create local keys
		HashMap<String, Integer> documentCats = new HashMap<String, Integer>();
		HashMap<String, Integer> documentLinks = new HashMap<String, Integer>();

		// extract categories and internal links
		for(int x= 0; x< content.length; x++){

			if ( content[x].startsWith("[[")){

				if ( content[x].startsWith("[[Category:")){

					// store the categories
					documentCats.put(content[x].toLowerCase(), 1);
					amountOfCats++; 
				}else{

					// clean the link
					String cleanedLink = content[x].toLowerCase();
					cleanedLink = cleanedLink.replaceAll("\\|[a-z0-9 -]+\\]\\]", "");
					cleanedLink = cleanedLink.replaceAll("\\]\\]", "");
					cleanedLink = cleanedLink.replaceAll("\\[\\[", "");

					// store the internal links
					documentLinks.put(cleanedLink, 1);
					amountOfLinks++;
				}
			}

		}

		// store the document description
		InternalLinks.put(wikipediakey, documentLinks);
		InternalCategories.put(wikipediakey, documentCats);


		// verbose
		if (verbose) {
			System.out.println(" Links:" + amountOfLinks + " Cats:" + amountOfCats);
		}
	}

	/**
	 * 
	 * Clean the object
	 * 
	 */
	public void destroyobject(){
		this.destroyobject();
	}

	/**
	 * 
	 * Clear the object
	 * Remove hashmap content
	 * 
	 */
	public void clearobject(){

		ArticleContent.clear();
		InternalLinks.clear();
		InternalCategories.clear();

	}
}
