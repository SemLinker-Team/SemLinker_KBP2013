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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;

/**
 * 
 * This class is intended to Index with Lucene a KBP corpus, according to a file list. 
 *
 * @author ludovicjeanlouis
 */
public class IndexSourceCorpus_v2 {/*List to handle all the options*/


    private static File indexDir;
    private static String inputLstFile;
    Pattern patternTitle = Pattern.compile("<headline>(.*)</headline>");
    static Pattern docId_1 = Pattern.compile("<doc id=\"(.*)\"><");
    static Pattern docId_2 = Pattern.compile("<DOC id=\"(.*)\" type");
    static Pattern docId_3 = Pattern.compile("<DOCID>(.*)</DOCID>");
    Pattern pageStart = Pattern.compile("<page>");
    Pattern pageEnd = Pattern.compile("</page>");

    /**
     * 
     * 
     * 
     * @param  args
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ClassNotFoundException
     * @throws Exception
     */
    public static void main(String[] args) throws IOException, FileNotFoundException, ClassNotFoundException, Exception {        

        Date start = new Date();
        Directory targetIndexDir = FSDirectory.open(indexDir);
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_44);
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_44, analyzer);

        if (indexDir.exists() == false) {
            // Create a new index in the directory, removing any
            // previously indexed documents:
            iwc.setOpenMode(OpenMode.CREATE);

        } else {
            System.err.println("Adding files to existing index: '" + indexDir);
            // Add new documents to an existing index:
            iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
        }

        //set ram buffer size (optional)
        iwc.setRAMBufferSizeMB(256.0);

        IndexWriter writer = new IndexWriter(targetIndexDir, iwc);

        System.err.println("Indexing to directory '" + indexDir + "'...");
        int docCount = 1;


        BufferedReader reader = new BufferedReader(new FileReader(inputLstFile));
        String currentDocPath;
        while ((currentDocPath = reader.readLine()) != null) {
            currentDocPath = currentDocPath.trim();
            System.err.println("Processing file: " + currentDocPath);
            //Processing each gzip file
            InputStream fileInputStream = new BufferedInputStream(new FileInputStream(currentDocPath));
            InputStreamReader streamReader;
            GZIPInputStream zipReader = null;

            if (currentDocPath.endsWith(".gz")) {
                //case when the file to index is a gzip file
                zipReader = new GZIPInputStream(fileInputStream);
                streamReader = new InputStreamReader(zipReader);
            } else {
                streamReader = new InputStreamReader(fileInputStream);
            }

            BufferedReader br = new BufferedReader(streamReader);

            String docTitle = "";
            String fileContent = "";
            String line;
            String docId = "";
            //String rawCnt = "";

            StringBuilder pageBuffer = new StringBuilder();
            //raw content with the "\n"
            StringBuilder rawPageBuffer = new StringBuilder();


            while ((line = br.readLine()) != null) {
                if (StringUtils.contains(line.toLowerCase(), "</doc>") == true) {
                    pageBuffer.append(line).append(" ");
                    rawPageBuffer.append(line + "MY_CUSTOM_SPACE");
                    //rawCnt = rawCnt + "MY_CUSTOM_SPACE" + line;
                    if (pageBuffer.length() > 0) {
                        fileContent = pageBuffer.toString().replaceAll("  ", " ");
                        docId = extractDocId(fileContent);
                        //get the title of the page
                        docTitle = extractTitle(fileContent);
                        //get the content of the page
                        String content = extractContent(fileContent);
                        String rawContent = extractRawContent(rawPageBuffer.toString());

                        indexDocument(writer, docId, content, docTitle, rawContent);
                        System.err.println("Processed " + docCount + " documents");
                        docCount++;

                    }
                    //reset buffer
                    pageBuffer = new StringBuilder();
                    rawPageBuffer = new StringBuilder();
                    //rawCnt = "";
                }
                pageBuffer.append(line).append(" ");
                rawPageBuffer.append(line + "MY_CUSTOM_SPACE");

            }
            fileInputStream.close();
            if (currentDocPath.endsWith(".gz") && zipReader != null) {
                zipReader.close();
            }
            streamReader.close();
        }

        reader.close();
        writer.close();

        Date end = new Date();
        System.err.println(end.getTime() - start.getTime() + " total milliseconds");

    }

    /**
     * 
     * Method called to index a unique document.
     * 
     * @param index_writer
     * @param doc_id
     * @param doc_content
     * @param doc_title
     * @param originalContent
     * @throws IOException
     * @throws Exception
     */
    private static void indexDocument(IndexWriter index_writer, String doc_id, String doc_content, String doc_title, String originalContent) throws IOException, Exception {
        Document doc = new Document();

        doc_id = doc_id.replaceAll("-", "_").toLowerCase(); // normalize

        //add proper "\n" in the original document
        originalContent = originalContent.replaceAll("MY_CUSTOM_SPACE","\n");

        doc.add(new StringField("id", doc_id, Field.Store.YES));
        doc.add(new StringField("text", originalContent, Field.Store.YES));
        doc.add(new TextField("title", doc_title, Field.Store.YES));
        doc.add(new TextField("cnt", doc_content, Field.Store.YES));

        index_writer.addDocument(doc);
    }

    /**
     * 
     * @param xmlContent
     * @return
     */
    static String extractDocId(String xmlContent) {
        String doc_id = "";
        //apply every id extractor regex to the text content
        Matcher form1Matcher = docId_1.matcher(xmlContent);
        Matcher form2Matcher = docId_2.matcher(xmlContent);
        Matcher form3Matcher = docId_3.matcher(xmlContent);

        if (form1Matcher.find()) {
            doc_id = form1Matcher.group(1);
            //in some cases the regex matches too much text, we need to restrict the matched content
            int i = doc_id.indexOf("\"");
            if (i > 0) {
                doc_id = doc_id.substring(0, i);

            }
        }

        if (form2Matcher.find()) {
            doc_id = form2Matcher.group(1);
        }

        if (form3Matcher.find()) {
            doc_id = form3Matcher.group(1);
        }
        if (doc_id.trim().length() == 0) {
            int start = xmlContent.toLowerCase().indexOf("id=");
            int end = xmlContent.toLowerCase().indexOf("\">");
            doc_id = xmlContent.substring(start + 4, end);
        }
        return doc_id.trim();
    }

    /**
     * 
     * @param content
     * @return
     */
    static String extractTitle(String content) {
        String title = "";
        String headerTag = "<headline>";
        try {
            int startIndex = content.toLowerCase().indexOf(headerTag) + headerTag.length();
            int endIndex = content.toLowerCase().indexOf("</headline>");
            title = content.substring(startIndex, endIndex);
        } catch (Exception e) {
        }
        return title.trim();
    }

    /**
     * 
     * @param content
     * @return
     */
    static String extractContent(String content) {
        String cnt = "";
        String tagName = "<doc";
        int startIndex = content.toLowerCase().indexOf(tagName);
        cnt = content.substring(startIndex);
        return cnt.trim();
    }

    /**
     * 
     * @param content
     * @return
     */
    static String extractRawContent(String content) {
        String cnt = "";
        String tagName = "<doc";
        int startIndex = content.toLowerCase().indexOf(tagName);
        cnt = content.substring(startIndex);
        return cnt.trim();
    }
}