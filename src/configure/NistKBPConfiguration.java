package configure;

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
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * Variables of the software prototype used for NIST KBP resolution.
 * All those can be overwritten by command
 * line or using a configuration file. <br>
 * <br>
 * The configuration file should be under the form :<br>
 * <br>
 * HOME_DIR=/home/YOURPATH/workspace/SemLinker/<br>
 * RESOURCE_DIR=/home/YOURPATH/workspace/SemLinker/resources/<br>
 * EXPERIMENT_DIR=/home/YOURPATH/workspace/SemLinker/resources/kbp2013/entitylinkingeval/<br>
 * TEST_DIR=/home/YOURPATH/workspace/SemLinker/resources/kbp2013/entitylinkingeval/test/<br>
 * index=/YOURPATH_TO_LUCENEINDEX_OFKBPCORPORA/index5<br>
 * indexwikipedia=/YOURPATH_TO_INDEXED_WIKIPEDIA/indexwiki_2013<br>
 * <br>
 *
 *
 * @author ericcharton, mariejeanmeurs
 *
 */
public class NistKBPConfiguration {

    // debug
    public boolean verbose;
	
    /**
     *
     * Constructor with default parameters.
     *
     */
    public NistKBPConfiguration() {
        initVars();
    }

    /**
     *
     * Constructor with custom parameter file.
     *
     * @param configfile
     */
    public NistKBPConfiguration(String configfile) {
        CONFIG_FILE = configfile;
        initVars();
    }

    /** Name of the config file: by default <b>config.cfg</b> */
    public static String CONFIG_FILE = "config.cfg";
    /** This is the hash used to collect configuration informations */
    public HashMap<String, String> CONFIG_MAP = new HashMap<String, String>();

    /** This is the path to the home directory of the application */
    public String HOME_DIR;
    /** This is the path to the resource directory : containing various files used in processes, like abbreviation resources */
    public String RESOURCE_DIR;
    /** This is the path of the folder where are copied the query files from KBP (.tab and .xml) */
    public String EXPERIMENT_DIR;
    /** This is the path of the folder where to write experimental results */
    public String TEST_DIR;
    
    public String KBP_CORPUS_FILES;
    public String KBP_CORPUS_PATH;
    public String WIKI_CORPUS_FILE;
    public String API_URI;

    /**
     * This is the full path to the LUCENE Index file of the NIST KBP Corpus.
     * @see kbp2013.index.IndexSourceCorpus
     */
    public String INDEX;
    /**
     * This is the full path to the LUCENE Index file of a Wikipedia XML dump.
     * @see kbp2013.index.IndexWikipediaCorpus
     *
    */
    public String INDEX_WIKIPEDIA;

    /**  This is a full path to a lexicon file */
    public String PATH_TO_LEXICONS;

    /**  full path to log files */
    public String PATH_TO_LOGS;
    
    /**   name of full file */
    public String NAME_OF_FULL_KB;
    
    /** path to correspondence table */
    public String PATH_TO_CORRESPONDENCE_TABLE;

    /** path to the experimental default file */
    public String PATH_TO_TRAIN; 
    public String PATH_TO_TRAIN_REF; 
    // 2013
    public String PATH_TO_TEST; 

    /** path to the output for the results */
    public String PATH_TO_TRAIN_OUTPUT;
    public String PATH_TO_TRAIN_MULTIPLE_OUTPUT;
    public String PATH_TO_TRAIN_CLUSTERED_OUTPUT;
    //2013
    public String PATH_TO_TEST_OUTPUT;
    public String PATH_TO_TEST_MULTIPLE_OUTPUT;
    public String PATH_TO_TEST_CLUSTERED_OUTPUT;

    
    //-------------------------------------
    // paths for configuration of
    // the experiment
    //-------------------------------------

    /** number of annotations used from the returned list of the annotator */
    public int MAX_ANNOTATIONS;
    
    /** path to abbreviation map */
    public String ABBREVIATION_MAP_FILE; 

    /** path to spelling map */
    public String SPELLING_MAP_FILE;
 
    /** Lucene, Wiki Spell Checker */
    public boolean USE_WIKI_SPELLCHECKER; 
    public boolean USE_LUCENE_WIKI_SPELLCHECKER; 

    /** test mode */
    public boolean TEST_MODE; 

    
    /**
     *
     * Method to initialize variables and configuration information.
     *
     */
    private void initVars() {
        String text = null;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE));
            while ((text = reader.readLine()) != null) {

                if (! text.startsWith("#")){

                    String label = text.split("=")[0];
                    String value = text.split("=")[1];
                    CONFIG_MAP.put(label, value);
                }
            }
            reader.close();
        } catch (IOException ex) {
             System.out.println("[error]No config file for NIST KBP management");
             System.exit(0);
        }


        //--------------------------------------------
        //
        // Get folders paths
        //
        //--------------------------------------------
        if (CONFIG_MAP.containsKey("HOME_DIR")) HOME_DIR = CONFIG_MAP.get("HOME_DIR");
        if (CONFIG_MAP.containsKey("RESOURCE_DIR")) RESOURCE_DIR = CONFIG_MAP.get("RESOURCE_DIR");
        if (CONFIG_MAP.containsKey("EXPERIMENT_DIR")) EXPERIMENT_DIR = CONFIG_MAP.get("EXPERIMENT_DIR");
        if (CONFIG_MAP.containsKey("TEST_DIR")) TEST_DIR = CONFIG_MAP.get("TEST_DIR");

        //--------------------------------------------
        //
        // Get resource paths
        //
        //--------------------------------------------
        // KBP
        if (CONFIG_MAP.containsKey("KBP_CORPUS_PATH")) KBP_CORPUS_PATH = CONFIG_MAP.get("KBP_CORPUS_PATH");
        if (CONFIG_MAP.containsKey("KBP_CORPUS_FILES")) KBP_CORPUS_FILES = CONFIG_MAP.get("KBP_CORPUS_FILES");
        if (CONFIG_MAP.containsKey("INDEX")) INDEX = CONFIG_MAP.get("INDEX");
        // Wikipedia
        if (CONFIG_MAP.containsKey("INDEX_WIKIPEDIA")) INDEX_WIKIPEDIA = CONFIG_MAP.get("INDEX_WIKIPEDIA");
        if (CONFIG_MAP.containsKey("WIKI_CORPUS_FILE")) WIKI_CORPUS_FILE = CONFIG_MAP.get("WIKI_CORPUS_FILE");
        // reference to the API URI
        if (CONFIG_MAP.containsKey("API_URI")) API_URI = CONFIG_MAP.get("API_URI");

        
        // Manage the number of annotations taken into account
        // standard Wikimeta list contains 3
        if (CONFIG_MAP.containsKey("MAX_ANNOTATIONS")) {
        	MAX_ANNOTATIONS = Integer.parseInt(CONFIG_MAP.get("MAX_ANNOTATIONS"));
        }
        else{ MAX_ANNOTATIONS = 3; }
        
        //--------------------------------------------
        //
        // Configuration of Spell Checker
        //
        //--------------------------------------------

        // Manage the flag of the spelling checker
        if (CONFIG_MAP.containsKey("USE_WIKI_SPELLCHECKER")) {
            if (CONFIG_MAP.get("USE_WIKI_SPELLCHECKER").contains("true") )
                USE_WIKI_SPELLCHECKER = true;
        }
        else{ USE_WIKI_SPELLCHECKER = false; }
        
        // Manage the flag of the Lucene spelling checker (locally installed version)
        if (CONFIG_MAP.containsKey("USE_LUCENE_WIKI_SPELLCHECKER")) {
            if (CONFIG_MAP.get("USE_LUCENE_WIKI_SPELLCHECKER").contains("true") )
                USE_LUCENE_WIKI_SPELLCHECKER = true;
        }
        else{ USE_LUCENE_WIKI_SPELLCHECKER = false;}

        // Manage abbreviation file
        if (CONFIG_MAP.containsKey("ABBREVIATION_MAP_FILE")) {
        	ABBREVIATION_MAP_FILE = RESOURCE_DIR + CONFIG_MAP.get("ABBREVIATION_MAP_FILE");
        }
        else{ ABBREVIATION_MAP_FILE = RESOURCE_DIR + "/abbreviations"; }

        // Manage the file of spelling mistakes for SpellingCorrector class
        if (CONFIG_MAP.containsKey("SPELLING_MAP_FILE")) {
            SPELLING_MAP_FILE = RESOURCE_DIR + CONFIG_MAP.get("SPELLING_MAP_FILE");
        }
        else{ SPELLING_MAP_FILE = RESOURCE_DIR + "/spelling_mistakes"; }

        // Manage the flag of test mode
        if (CONFIG_MAP.containsKey("TEST_MODE")) {
        	if (CONFIG_MAP.get("TEST_MODE").contains("true") ) 
        		TEST_MODE = true; 
        }
        else{ TEST_MODE = false; }
        //--------------------------------------------
        //
        // Use the folder paths to build complete
        // paths
        //
        //--------------------------------------------

        // Manage path to lexicon files
        if (CONFIG_MAP.containsKey("PATH_TO_LEXICONS")) {
        	PATH_TO_LEXICONS = RESOURCE_DIR + CONFIG_MAP.get("PATH_TO_LEXICONS");
        }
        else{ PATH_TO_LEXICONS = RESOURCE_DIR + "kbp2013/Lexicons/"; }
        
        // Manage path to log files
        if (CONFIG_MAP.containsKey("PATH_TO_LOGS")) {
        	PATH_TO_LOGS = RESOURCE_DIR + CONFIG_MAP.get("PATH_TO_LOGS");
        }
        else{ PATH_TO_LOGS = RESOURCE_DIR + "kbp2013/temp/"; }

        // Manage name of full KB file
        if (CONFIG_MAP.containsKey("NAME_OF_FULL_KB")) {
        	NAME_OF_FULL_KB = RESOURCE_DIR + CONFIG_MAP.get("NAME_OF_FULL_KB");
        }
        else{ NAME_OF_FULL_KB = RESOURCE_DIR + "kbase/fullkb.xml"; }
        
        // Manage path to correspondence table
        if (CONFIG_MAP.containsKey("PATH_TO_CORRESPONDENCE_TABLE")) {
        	PATH_TO_CORRESPONDENCE_TABLE = RESOURCE_DIR + CONFIG_MAP.get("PATH_TO_CORRESPONDENCE_TABLE");
        }
        else{ PATH_TO_CORRESPONDENCE_TABLE = RESOURCE_DIR + "kbp2013/wikimeta_table/WikimetaTable.txt"; }
        
        //--------------------------------------------
        //
        // path to default experimental files
        //
        //--------------------------------------------
        // training 2012
        if (CONFIG_MAP.containsKey("PATH_TO_TRAIN")) {
        	PATH_TO_TRAIN = EXPERIMENT_DIR + CONFIG_MAP.get("PATH_TO_TRAIN");
        }
        else{ PATH_TO_TRAIN =  EXPERIMENT_DIR + "tac_2012_kbp_english_evaluation_entity_linking_queries.xml"; }
        // 2012 reference
        if (CONFIG_MAP.containsKey("PATH_TO_TRAIN_REF")) {
        	PATH_TO_TRAIN_REF = EXPERIMENT_DIR + CONFIG_MAP.get("PATH_TO_TRAIN_REF");
        }
        else{ PATH_TO_TRAIN_REF = EXPERIMENT_DIR + "tac_2012_kbp_english_evaluation_entity_linking_query_types.tab"; }
        
        // test 2013
        if (CONFIG_MAP.containsKey("PATH_TO_TEST")) {
        	PATH_TO_TEST = EXPERIMENT_DIR + CONFIG_MAP.get("PATH_TO_TEST");
        }
        else{ PATH_TO_TEST = EXPERIMENT_DIR + "tac_2013_kbp_english_entity_linking_evaluation_queries.xml"; }

        //--------------------------------------------
        //
        // path to output for the results
        //
        //--------------------------------------------
        // training 2012
        // output of the intermediary file
        if (CONFIG_MAP.containsKey("PATH_TO_TRAIN_OUTPUT")) {
        	PATH_TO_TRAIN_OUTPUT = TEST_DIR + CONFIG_MAP.get("PATH_TO_TRAIN_OUTPUT");
        }
        else{ PATH_TO_TRAIN_OUTPUT = TEST_DIR + "tac_2012_kbp_english_evaluation_entity_linking_queries-result.tab"; }
        // output of the ranked list
        if (CONFIG_MAP.containsKey("PATH_TO_TRAIN_MULTIPLE_OUTPUT")) {
        	PATH_TO_TRAIN_MULTIPLE_OUTPUT = TEST_DIR + CONFIG_MAP.get("PATH_TO_TRAIN_MULTIPLE_OUTPUT");
        }
        else{ PATH_TO_TRAIN_MULTIPLE_OUTPUT = TEST_DIR + "tac_2012_kbp_english_entity_linking_evaluation_queries-result-ranked.tab"; }
        // final output with clustering
        if (CONFIG_MAP.containsKey("PATH_TO_TRAIN_CLUSTERED_OUTPUT")) {
        	PATH_TO_TRAIN_CLUSTERED_OUTPUT = TEST_DIR + CONFIG_MAP.get("PATH_TO_TRAIN_CLUSTERED_OUTPUT");
        }
        else{ PATH_TO_TRAIN_CLUSTERED_OUTPUT = TEST_DIR + "tac_2012_kbp_english_evaluation_entity_linking_queries-result-k.tab"; } 
        
        // test 2013
        // output of the intermediary file
        if (CONFIG_MAP.containsKey("PATH_TO_TEST_OUTPUT")) {
        	PATH_TO_TEST_OUTPUT = TEST_DIR + CONFIG_MAP.get("PATH_TO_TEST_OUTPUT");
        }
        else{ PATH_TO_TEST_OUTPUT = TEST_DIR + "tac_2013_kbp_english_entity_linking_evaluation_queries-result.tab"; } 
        // output of the ranked list
        if (CONFIG_MAP.containsKey("PATH_TO_TEST_MULTIPLE_OUTPUT")) {
        	PATH_TO_TEST_MULTIPLE_OUTPUT = TEST_DIR + CONFIG_MAP.get("PATH_TO_TEST_MULTIPLE_OUTPUT");
        }
        else{ PATH_TO_TEST_MULTIPLE_OUTPUT = TEST_DIR + "tac_2013_kbp_english_entity_linking_evaluation_queries-result-ranked.tab"; } 
        // final output with clustering
        if (CONFIG_MAP.containsKey("PATH_TO_TEST_CLUSTERED_OUTPUT")) {
        	PATH_TO_TEST_CLUSTERED_OUTPUT = TEST_DIR + CONFIG_MAP.get("PATH_TO_TEST_CLUSTERED_OUTPUT");
        }
        else{ PATH_TO_TEST_CLUSTERED_OUTPUT = TEST_DIR + "tac_2013_kbp_english_entity_linking_evaluation_queries-result-k.tab"; } 
       
    }
}
