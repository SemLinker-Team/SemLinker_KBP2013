package semkit.semanticresources;

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

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;


/**
 * 
 *  This method is intended to load NLGbAse Metadata
 *  in the REDIS database it uses the jedis jar and the redis base.<br>
 * <br>
 *  Some links to help:<br>
 *  <a href="https://github.com/xetorthio/jedis/wiki">The Github of Redis</a><br>
 *  <a href="http://redis.io/">The home of Redis</a><br>
 *  <br>
 *  <b>You need an active Redis Server before using this class</b>.<br>
 *  <br>
 *  To launch the Redis base under Linux :<br>
 *     launch [yourpath]./redis-server<br>
 *  <br>
 *  To launch the Redis base under Windows in 64 bits:<br>
 *      launch [yourpath]\redis-2.4.5-win32-win64\64bit\redis-server.exe<br>
 *  <br>
 *  We have tested Redis under Windows. It works with minor errors. Please do not forget
 *  that Redis Windows is a fork (a good one) but a little bit unstable.
 *  <br>
 *  Please see the Tool.java Class to load metadata in Redis automatically and easily.<br>
 *  <br>
 *  @see InitTool
 *  @author ericcharton
 */
public class NLGbAseRedisLoadMetadata {



	private String redisurl = "localhost"; // default address
	private String redisname = "nlgbase"; // default name
	private Jedis jedisbase = null; // default object
	public String NLGbAseVersion =  "6.1";
	private JedisPool pool ;
	private boolean loadInLowerCase = true; // by default load the base with all in lower case
	private boolean loadInUpperCase = false;

	/**
	 * 
	 * Path of the metadatas
	 * 
	 */
	private String metadatapath = " metadata/EN.data.csv"; // default path (usually modified by constructor)
	
	
	

	/**
	 * Constructor with the NLGBase base passed
	 * 
	 * @param nlgbasepath
	 */
	public NLGbAseRedisLoadMetadata(String nlgbasepath) { 

		// instantiate the Redis base object
		String flagver = jedisopen( redisurl ); 
		// open the file
		jedisload(nlgbasepath);		


	}

	/**
	 * constructor with URL passed to the metadata csv file
	 */
	public NLGbAseRedisLoadMetadata(String nlgbasepath, String myredisurl) { 


		// instantiate the Redis base object
		String flagver = jedisopen( myredisurl); 
		// open the file
		jedisload(nlgbasepath);


	}

	/**
	 * constructor with default Redis URL and only opening off the Redis base
	 * the base loading has to be done separately using method NLGbAseVerifyLoad
	 */
	public NLGbAseRedisLoadMetadata(String nlgbasepath, boolean loadcase) { 

		// loadCase flag set
		loadInLowerCase = loadcase;
		// instantiate the Redis base object
		jedisopen( redisurl ); 

	}

	

	/**
	 * constructor with URL passed to the metadata csv file
	 */
	public NLGbAseRedisLoadMetadata(String nlgbasepath, String myredisurl,boolean loadcase) { 

		// loadCase flag set
		loadInLowerCase = loadcase;
		// instantiate the Redis base object
		jedisopen( myredisurl); 
		// open the file
		jedisload(nlgbasepath);

	}




	/**
	 * 
	 * Verify if the base is loaded and then load it or return 1
	 * 
	 * @return 
	 * 	1 if already loaded, 0 if not loaded or loaded with an old version
	 * 
	 */
	public int NLGbAseVerifyLoad(){

		String value = jedisbase.get("NLGbAseVersion");

		// if this the right version is loaded
		if (value != null){
			if ( value.compareTo(NLGbAseVersion) == 0 ){

				return 1; // already loaded

			}else{

				// an old version is loaded
				return 0; 
			}
		}else{

			// an old version or no version is loaded
			return -1; 
		}
	}

	/**
	 * 
	 * Simply Load NLGbAse version
	 * 
	 */
	public void NLGbAseLoad(){

		// open the file
		jedisload(metadatapath);
	}

	/**
	 * 
	 * Simply Load NLGbAse version with URL in parameters
	 * 
	 */
	public void NLGbAseLoad(String NLGbAseMetadataPath){

		// open the file
		jedisload( NLGbAseMetadataPath);
	}




	/**
	 * 
	 * Method to open a new instance of Redis from constructors
	 * 
	 * @param nlgbasepath
	 * @param myredisurl
	 * @param redisname
	 * @return
	 * @throws JedisConnectionException
	 */
	private String jedisopen(String myredisurl) throws JedisConnectionException{

		String statustoreturn = null;


		try {

			// Use Jedis pool to avoid errors while saving the base
			// https://github.com/xetorthio/jedis/wiki/FAQ
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxActive(200);
			config.setMaxIdle(200);
			pool = new JedisPool(config,"localhost",6379,8000);

			// open the base
			jedisbase = pool.getResource();

			// verify the version key
			String value = jedisbase.get("NLGbAseVersion");
			if (value != null){
				if ( value.compareTo(NLGbAseVersion) == 0 ){
					statustoreturn = value;
				}
			}



		} 
		catch (JedisConnectionException e) { 

			System.out.println ("Redis Error:" + e.toString()); 
			statustoreturn = "error";

		}


		return(statustoreturn);


	}


	/**
	 * 
	 * This method is called by the constructors to load the NLGbAse metadata
	 * in the Redis base
	 * 
	 * 
	 * @param nlgbasepath is the path of the metadata base
	 * @return
	 * @throws JedisConnectionException
	 */
	public int jedisload( String nlgbasepath ) throws JedisConnectionException
	{

		// add a key to define the base version
		jedisbase.set("NLGbAseVersion", NLGbAseVersion);

		try{

			FileInputStream fstream = new FileInputStream(nlgbasepath);

			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			NLGbAseDecoder Thisdecoder = new NLGbAseDecoder(); 
			Map<String, String>	m	=	new HashMap<String, String>(); // this is the hashmap of the key property
			Map<String, String>	n	=	new HashMap<String, String>(); // this is the hashmap of the surface forms with redirection to unique key

			long count = 0;

			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   
			{

				// Decode the line from csv
				Thisdecoder.Decoder(strLine); 

				try{

					//*****************************************
					// write the unique key entry
					// and all parameters
					//*****************************************

					// build the hashmap of the original key
					// Map<String, String>	m	=	new HashMap<String, String>();
					m.put("dbpedia", Thisdecoder.DbpediaUri);
					m.put("NE", Thisdecoder.NE);	
					m.put("offset", Thisdecoder.WpoffsetStr);	
					m.put("referto", "n"); // kind of document -> this is a general wikipedia entry that refers to itself

					jedisbase.hmset(Thisdecoder.UniqueKey, m ); // include the data of the hash 

					// build the hash with all surface forms associated to the original key
					// load the surface forms in the arraylist
					// introduce the surface form to complement original key
					for( int t=0; t < Thisdecoder.surfaceforms.size(); t++)
					{
						String thisSf = Thisdecoder.surfaceforms.get(t);
						if (! thisSf.contentEquals("")){ 
							//HSETNX key field value
							//Set the value of a hash field, only if the field does not exist
							if ( loadInUpperCase ) {
								jedisbase.hsetnx(Thisdecoder.UniqueKey, thisSf, "sf" ); //add all the surface forms

							}
							if ( loadInLowerCase ) {
								jedisbase.hsetnx(Thisdecoder.UniqueKey, thisSf.toLowerCase(), "sf" ); //add all the surface forms in lower case
							}
						}
					}

					// clear the hashs used
					m.clear();



					//*****************************************
					// write the surface form entries
					// referring to unique keys
					//*****************************************
					// introduce the surface form to complement original key
					for( int t=0; t < Thisdecoder.surfaceforms.size(); t++)
					{

						String sforms = Thisdecoder.surfaceforms.get(t);

						// We have to preprocess sforms for :
						// Lowercase
						sforms = sforms.toLowerCase();
						// - remove parentheses -> add the form with p content & without p content & always remove (synonyms)
						sforms = sforms.replaceAll("(homonym)",""); // always remove (synonyms)
						// remove extra spaces at the end or at the beginning
						sforms = sforms.replaceAll("^[ ]+","");
						sforms = sforms.replaceAll("[ ]+$","");

						// some p remains
						if ( sforms.contains("(") ){

							String sformsWithoutParenthesys = sforms.replaceAll("[\\(\\)\\,]","");
							// add this form

							if ( loadInUpperCase ) {
								jedisbase.hsetnx(sformsWithoutParenthesys, Thisdecoder.UniqueKey, "redirect" ); //add all the surface forms
								jedisbase.hsetnx(sformsWithoutParenthesys, "referto" , "r" ); //add all the surface forms
							}
							if ( loadInLowerCase ) {
								jedisbase.hsetnx(sformsWithoutParenthesys.toLowerCase(), Thisdecoder.UniqueKey, "redirect" ); //add all the surface forms in lower case
								jedisbase.hsetnx(sformsWithoutParenthesys.toLowerCase(), "referto" , "r" ); //add all the surface forms
							}
							// remove the parenthesis content for next step
							sforms = sforms.replaceAll("\\(.*\\)","");
							// remove extra spaces at the end or at the beginning
							sforms = sforms.replaceAll("^[ ]+","");
							sforms = sforms.replaceAll("[ ]+$","");
						}

						// Add the final form
						if (! sforms.contentEquals("")){ 

							if ( loadInUpperCase ) {
								jedisbase.hsetnx(sforms, "referto" , "r" ); //add all the surface forms
								jedisbase.hsetnx(sforms, Thisdecoder.UniqueKey, "redirect" ); //add all the surface forms 
							}
							if ( loadInLowerCase ) { 
								jedisbase.hsetnx(sforms.toLowerCase(), "referto" , "r" ); //add all the surface forms
								jedisbase.hsetnx(sforms.toLowerCase(), Thisdecoder.UniqueKey, "redirect" ); //add all the surface forms in lower case

							}
						}


					}						

					// Reinit the decoder
					Thisdecoder.eraseDecoder();
					

				}
				catch (Exception f)
				{//Catch exception if any

					// display error
					System.err.println("Local Error during loading: " + f.getMessage());

					// wait a little bit 
					long t1;
					long t0 = System.currentTimeMillis();
					do{
						t1=System.currentTimeMillis();
					}
					while (t1-t0<2000); // let 2 seconds to save

					// reinitialize
					pool.returnResource(jedisbase);
				}


			}
			br.close();
		}
		catch (Exception e)
		{
			//Catch exception if any
			System.err.println("General Error: " + e.getMessage());

		}

		return(0);		
	}


	/**
	 * 
	 * This method is called by the constructors to load the NLGbAse keywords
	 * in the Redis base
	 * 
	 * 
	 * @param nlgbasepath is the path of the metadata base
	 * @return
	 * @throws JedisConnectionException
	 */
	public int keywordload( String nlgbaseidxpath ) throws JedisConnectionException
	{

		try{

			FileInputStream fstream = new FileInputStream(nlgbaseidxpath);

			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			NLGbAseDecoder Thisdecoder = new NLGbAseDecoder(); 
			Map<String, String>	m	=	new HashMap<String, String>(); // this is the hashmap of the key property
			Map<String, String>	n	=	new HashMap<String, String>(); // this is the hashmap of the surface forms with redirection to unique key

			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   
			{

				// Decode the line from csv
				Thisdecoder.Decoder(strLine); 


			}

			br.close();

		}catch(Exception e){

		}

		return(0);	
	}

}
