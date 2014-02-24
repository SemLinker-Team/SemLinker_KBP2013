package configure;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


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

/**
 * 
 * Define your constants in this class. 
 * A default configuration file must be in the same folder as
 * the jar file if you want to use it. 
 * 
 * 
 * @author ericcharton
 *
 */
public class SemkitConfiguration {
	
	/** Give here the URI of the Wikimeta REST service */
	public static String WMRestURi = "http://www.wikimeta.com/wapi/service";
	/** Give here the API key of your Wikimeta API Account */
	public static String APIAccount = "752538502038"; // this is a demo key, can expire at any moment
	/** Give here the path to access to the NLGbAse metadata (used only to load REDIS base) */
	public static String NLGbAsePath = "metadata/EN.data.csv";
	/** Give here the URI of RDF repository */
	public static String URItoGet = "http://www.dbpedia.org/data/";
	public static String URInameOfRes= "dbpedia.org";
 
	/** Name of the config file: by default <b>config_sample.cfg</b> */
    public static String CONFIG_FILE = "config.cfg";
    /** This is the hash used to collect configuration information */
    public HashMap<String, String> CONFIG_MAP_API = new HashMap<String, String>();
    
	/**
	 * 
	 * Allows to use constants under the form thisclass.API.RestURI
	 * 
	 * @author ericcharton
	 *
	 */
	public enum API {
	    	
	    	RestURI(WMRestURi),
            APIKey(APIAccount);
	    	
	        private final String value;
	        API(String value) {this.value = value;}
	        public String getValue() {return this.value;}
	}
	
	/**
	 * 
	 * Constructor by default
	 * 
	 */
	public SemkitConfiguration() {
		
		initVars();
		
	}
	
	/**
	 * 
	 * Constructor with config file passed as full path. 
	 * 
	 * @param configfile
	 */
	public SemkitConfiguration(String configfile){
		CONFIG_FILE = configfile;
		initVars();
		
	}
	
	
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

                    String label = text.split("=")[0].toLowerCase();
                    String value = text.split("=")[1];
                    CONFIG_MAP_API.put(label, value);
             		
             	}
            }
            reader.close();
        } catch (IOException ex) {
            System.out.println("[error]No config file for API management");
            System.exit(0);
        }
        
        // fill the variables
        if (CONFIG_MAP_API.containsKey("nlgbasepath")){
        	NLGbAsePath = CONFIG_MAP_API.get("nlgbasepath");
        }
        if (CONFIG_MAP_API.containsKey("wmresturi")){
        	WMRestURi= CONFIG_MAP_API.get("wmresturi");
        }
        if (CONFIG_MAP_API.containsKey("apiaccount")){
        	APIAccount = CONFIG_MAP_API.get("apiaccount");
        }
	}
	
}
