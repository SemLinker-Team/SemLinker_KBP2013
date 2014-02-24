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

import java.util.regex.Pattern;
import kbp2013.tools.Lexicon;
import kbp2013.tools.Logging;
import org.apache.commons.lang.StringUtils;

/**
 *
 * This class is intended to normalize documents from KBP Corpus to make them
 * compatible with Wikimeta API
 *
 * @author ericcharton, michelgagnon
 *
 */
public class DocumentNormalizer {

    private boolean verbosehead = true;
    private boolean verbosecap = false;

    /**
     * 
     * Constructor
     * 
     */
    public DocumentNormalizer() {
        lexicon = new Lexicon();
    }

    /**
     *
     * To avoid noise in the annotation process remove all the tags
     *
     * @param content
     * @return
     */
    public static String RemoveTags(String content) {

        // on docid files
        //<DOC id="APW_ENG_20081105.0541" type="story" >
        content = content.replaceAll("<DOC id=[^>]+>", ""); // remove Doc id tag

        // on ng files
        content = content.replaceAll("<POSTER>[^>]+</POSTER>", ""); // remove poster name
        content = content.replaceAll("<DOCID>[^>]+</DOCID>", ""); // remove DOCID definition

        content = content.replaceAll("<DOCTYPE SOURCE=\"usenet\">", " ");
        content = content.replaceAll("<DOCTYPE SOURCE= usenet >", " ");
        content = content.replaceAll("</DOCTYPE>", " ");

        content = content.replaceAll("USENET TEXT", " ");

        // add dots
        content = content.replaceAll("<P>", " ");
        content = content.replaceAll("</P>", " . ");

        // NOTE: why is opening markup replaced by .? (MG)
        //content = content.replaceAll("<HEADLINE>", " . ");
        //content = content.replaceAll("</HEADLINE>", " . ");
        content = content.replaceAll("<HEADLINE>[^<]+</HEADLINE>", " ");
        
        
        // NOTE: why is opening markup replaced by .? (MG)
        Lexicon lexicon;
        content = content.replaceAll("</DATELINE>", " . ");
        content = content.replaceAll("<DATELINE>", " . ");

        content = content.replaceAll("<DATETIME>", " ");
        content = content.replaceAll("</DATETIME>", " . ");

        content = content.replaceAll("<POSTDATE>", "  ");
        content = content.replaceAll("</POSTDATE>", " . ");

        content = content.replaceAll("<TEXT>", " ");
        content = content.replaceAll("</TEXT>", " ");

        content = content.replaceAll("<DOC>", " ");
        content = content.replaceAll("</DOC>", " ");

        content = content.replaceAll("<POST>", " ");
        content = content.replaceAll("</POST>", " ");

        content = content.replaceAll("<BODY>", " ");
        content = content.replaceAll("</BODY>", " ");

        // weird stuff
        content = content.replaceAll("--", " ");

        // cleaning, simplification
        content = content.replaceAll("[\n]+", "\n");


        //--------------------------------------------
        // Segmentations 
        //			UNC-Charlotte 
        //			University of North Carolina-Charlotte
        // content = content.replaceAll("-", " ");

        return (content);
    }

    /**
     *
     * Remove symbols that turn the Wikimeta API crazy
     *
     * @param content
     * @return
     */
    public static String ApplyNorm(String content) {

        if (content.length() == 0) {
            return content;
        } // safety


        // 10:Managing query EL_ENG_00010 [Burlington][burlington] eng-NG-31-126076-12043293 433 442
        // --->Context:ay, August 21 2008: Burlington* Quadrafonics at Bl
        content = content.replaceAll("\\*", " ");

        // problem in some usenets 
        // ----> 276:Managing query EL_ENG_00276 [Lucy][lucy] eng-NG-31-142756-10090832 456 459
        content = content.replaceAll("[`]+", " ");
        // 1127:Managing query EL_ENG_01130 [MOORE][moore] eng-WL-110-174612-12993709 156 160
        content = content.replaceAll("~", " ");
        // Managing underscores
        content = content.replaceAll("_", " ");

        // with the game;
        // name;
        content = content.replaceAll(";", " . "); // solution not very clean but will solve it 

        // >Context:shareholders. </P> <P> The 22-year-old, Watertown-based company manages more than 600 chi
        content = content.replaceAll("-", " - ");

        // replace double space
        content = content.replaceAll("[ ]+", " ");
        return (content);
    }

    /**
     *
     * This method applies very light normalization used for mention
     * location detection
     *
     * The replacements are made with blank spaces to keep the same number of
     * chars in the text (and allow localization by offset)
     *
     * @param originalText
     * @return
     */
    public static String PrepareDocForMentionLocation(String originalText) {

        // Some corrections for identification
        originalText = originalText.replaceAll("\\*", " ");

        // removing some quotes
        //822:Managing query EL_ENG_00824 [Ruth][ruth] eng-NG-31-151741-10662807 1060 1063
        //--->Context:ndicapped! :)  </POST> <POST> <POSTER> "Ruth Rittgers" &lt;rittg...@infionline.net&g
        originalText = originalText.replaceAll("\"", " ");


        return originalText;
    }

    /**
     *
     * Modify the text content when the target is not in cap (i.e in
     * web usenet forums)
     *
     * @param Content the text to modify
     * @param toReplace the string with no cap at the beginning that has to be
     * substituted (\ex cross -> Cross)
     * @return
     */
    public static String IntroduceCap(String Content, String toReplace) {

        // transpose the first word of replacement string to cap
        String capedToReplace = toReplace.substring(0, 1);
        String endOfSequence = toReplace.substring(1, toReplace.length());

        String toReplacemodified = capedToReplace.toUpperCase() + endOfSequence;

        Content = Content.replace(toReplace, toReplacemodified);

        return Content;
    }
    

    /*
     * NYT_ENG_20070325.0028 <HEADLINE> EAGLES TOO HOT TO HANDLEEAGLES ENSURE
     * SAINTS MARCH OUT BC4ST. LAWRENCE1 </HEADLINE>
     *
     * NYT_ENG_20080202.0176.LDC2009T13 EVANS KNOWS WAY AROUND BLOCK
     *
     * NYT_ENG_20070323.0009 KIRK'S MISS WASN'T AGGIES' LAST SHOT
     *
     * eng-NG-31-102868-11652560 THE HON'BLE  SUPREME COURT OF INDIA.  (CEC) I
     * NEED SUPPORT
     */
    /**
     * 
     */
    public String NormalizeCapSequences(String originalContent, String queryName, Logging log) {        
     
        // build an array to work on
        String[] lines = originalContent.split("\n");

        docLexicon = new Lexicon(removeHeadlines(originalContent));

        if (verbosehead || verbosecap) {
            log.writeLog("*Doc Normalization: ");
        }

        // Process headlines
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].toLowerCase().contains("<headline>")) {                

                String[] tokenizedHeadline = lines[i + 1].split(" ");

                if (verbosehead) {
                    log.writeLog(" -Original Headline: " + lines[i + 1]);
                }

                tokenizedHeadline = uncapitalizeCommonWords(tokenizedHeadline);
                tokenizedHeadline = capitalizeProperNouns(tokenizedHeadline);

                lines[i + 1] = StringUtils.join(tokenizedHeadline, " ");

                if (verbosehead) {
                    log.writeLog(" -Transformed Headline: " + lines[i + 1] + "\n");
                }

            }
        }

        // Process lines in all caps
        for (int i = 0; i < lines.length; i++) {

            // ex -> HOW TO TURN YOUR BABY INTO A MILLIONAIRE
            // step one: transform all complete lines in CAPS in lowercases
            if (lines[i].matches("[A-Z0-9 \\.,;]+")) {

                if (verbosecap) {
                    log.writeLog(" -Original Cap Line: " + lines[i]);
                }

                String[] tokenizedLine = lines[i].split(" ");
                tokenizedLine = uncapitalizeCommonWords(tokenizedLine);
                tokenizedLine = capitalizeProperNouns(tokenizedLine);

                lines[i] = StringUtils.join(tokenizedLine, " ");

                if (verbosecap) {
                    log.writeLog(" -Transformed Cap Line: " + lines[i] + "\n");
                }
            }
        }


        // restore document and send it back
        String returnedContent = "";
        for (int i = 0; i < lines.length; i++) {
            returnedContent = returnedContent + lines[i] + "\n";
        }        

        // get back original mention
        returnedContent = returnedContent.replaceAll(queryName.toLowerCase(), queryName);
        
        return returnedContent;

    }

    public static String normalizePunctuation(String originalContent) {

        String modifiedContent = originalContent;
        modifiedContent = modifiedContent.replaceAll("([\\(\\[])", "$1 ");
        modifiedContent = modifiedContent.replaceAll("([\\)\\]\\.,!;?])", " $1");
        return modifiedContent;
    }

    public static String espaceText(String originalContent){
        String escapedCnt = "";
        escapedCnt = Pattern.quote(originalContent).replaceAll("\\*","_");        
        return escapedCnt;
    }

    public static String removeHeadlines(String originalContent) {        
        String[] lines = originalContent.split("\n");     
        
        // Process headlines
        for (int i = 0; i < lines.length; i++) {            
            if (lines[i].toLowerCase().contains("<headline>")) {
                lines[i + 1] = " ";
            }
        }

        // Process lines in all caps
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].matches("[A-Z0-9 \\.,;]+")) {
                lines[i] = "";
            }
        }

        // restore document and send it back
        String returnedContent = "";
        for (int i = 0; i < lines.length; i++) {
            returnedContent = returnedContent + lines[i] + "\n";
        }

        return returnedContent;
    }
    


    // ------------------------- Private methods -----------------------------------------
    private String[] uncapitalizeCommonWords(String[] wordList) {

        String[] uncapitalizedList = wordList.clone();

        for (int i = 0; i < wordList.length; i++) {
            if (lexicon.isCommonWord(uncapitalizedList[i])) {
                uncapitalizedList[i] = uncapitalizedList[i].toLowerCase();
            }
        }

        return uncapitalizedList;
    }

    private String[] capitalizeProperNouns(String[] wordList) {

        String[] capitalizedList = wordList.clone();

        for (int i = 0; i < wordList.length; i++) {
            if (lexicon.isProperNoun(capitalizedList[i])) {
                capitalizedList[i] = lexicon.getProperNoun(capitalizedList[i].toLowerCase());
            } else if (docLexicon.isProperNoun(capitalizedList[i])) {
                capitalizedList[i] = docLexicon.getProperNoun(capitalizedList[i].toLowerCase());
            }
        }

        return capitalizedList;
    }

    private static int countCapitalizedWords(String[] line) {
        int count = 0;
        for (int i = 0; i < line.length; i++) {
            if (line[i].matches("^[A-Z].*")) {
                count++;
            }

        }

        return count;

    }



    // --------------------------- Privates attributes ---------------------------------------
    Lexicon lexicon;
    Lexicon docLexicon;
}
