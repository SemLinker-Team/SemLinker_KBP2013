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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * 
 * This class gives methods to retrieve records from the Redis base while storing NLGbAse metadata
 * 
 * @author ericcharton
 *
 */
public class NLGbAseRedisAccessMetadata {


	private JedisPool pool ;
	private Jedis jedisbase = null; // default object
	private int jedisStatus = 0;

	/**
	 * 
	 * Default constructor
	 * 
	 */
	public NLGbAseRedisAccessMetadata()  { 

		// open the base
		jedisStatus = jedisopen();

	}

	
	/**
	 * 
	 * Return the current Status of the base
	 * 
	 * @return integer
	 */
	public int getJedisStatus(){
		
		return jedisStatus;
	}


	/**
	 * 
	 * Open the Redis Base assumed to be loaded, if not, return a -1 value
	 * 
	 * @return
	 * @throws JedisConnectionException
	 */
	private int jedisopen() throws JedisConnectionException{

		int statustoreturn = 0;


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
			// String value = jedisbase.get("NLGbAseVersion");
			if (jedisbase.get("NLGbAseVersion") != null){
				statustoreturn = 1;
			}

		} 
		catch (JedisConnectionException e) { 

			System.out.println ("Redis Error:" + e.toString()); 
			statustoreturn = -1;

		}


		return(statustoreturn);

	}

	/**
	 * 
	 * Get NE according to a key
	 * 
	 */
	public String getNE(String valueKey){


		// continue if it is a key
		if ( jedisbase.hget(valueKey,"NE") != null){

			// get the elements
			return(jedisbase.hget(valueKey,"NE"));

		}else{

			return null;
		}


	}


	/**
	 * 
	 * 
	 * 
	 * @param uniquekey
	 * @return
	 */
	public String getDBPedia(String uniquekey){


		// continue if it is a key
		if ( jedisbase.hget(uniquekey,"dbpedia") != null){

			// get the elements
			return(jedisbase.hget(uniquekey,"dbpedia"));

		}else{

			return null;
		}


	}


	/**
	 * 
	 * Get all the surface forms for a given unique dbpedia/wikipedia/nlgbase name key
	 * 
	 * 
	 * @param valueKey
	 * @return
	 */
	public ArrayList<String>  getSF(String valueKey){


		ArrayList<String> wordList = new ArrayList<String>(); 


		// get status using hget
		// Returns the value associated with field in the hash stored at key.
		// Bulk reply: the value associated with field, or nil when field is not present in the hash or key does not exist.
		// http://www.jarvana.com/jarvana/view/redis/clients/jedis/1.3.1/jedis-1.3.1-javadoc.jar!/redis/clients/jedis/Jedis.html#hget(java.lang.String, java.lang.String)
		//status = jedisbase.hget(valueKey,"referto");

		try{

			// continue if it is a key
			//if ( status.compareTo("n") == 0){
			if (jedisbase.hget(valueKey, "offset") != null){

				// get list of words
				Map<String, String>	allkeys	=	new HashMap<String, String>(); // this is the hashmap to store all the keys
				allkeys = jedisbase.hgetAll(valueKey); // retrieve the keys in a hashmap

				// explore all keys and extract the sf ones
				for (String mapKey : allkeys.keySet()) {

					if (allkeys.get(mapKey).contentEquals("sf")){
						wordList.add(mapKey);
					}

				}


				// get the elements
				return(wordList);

			}else{

				return null;
			}

		}catch(Exception e){
			return null;
		}


	}

	/**
	 * 
	 * Test if a surface form is a unique key
	 * (useful for table correspondences, ie in KBP)
	 * 
	 */
	public int keyIsunIque(String surfaceform){

		int sfValue = 1;

		// get list of words
		Map<String, String>	allkeys	=	new HashMap<String, String>(); // this is the hashmap to store all the keys
		allkeys = jedisbase.hgetAll(surfaceform); // retrieve the keys in a hashmap

		if (allkeys.size() == 0) { sfValue = 0; }

		return sfValue;
	}


	/**
	 * 
	 * Get all the unique dbpedia/wikipedia/nlgbase name key for a given surfaceform
	 * 
	 * 
	 * @param surfaceform
	 * @return
	 */
	public ArrayList<String>  getUniqueKey(String surfaceform){


		ArrayList<String> KeyList = new ArrayList<String>(); 

		try{
			// get list of words
			Map<String, String>	allkeys	=	new HashMap<String, String>(); // this is the hashmap to store all the keys
			allkeys = jedisbase.hgetAll(surfaceform); // retrieve the keys

			// explore all keys and extract the sf ones
			for (String mapKey : allkeys.keySet()) {

				if (allkeys.get(mapKey).contentEquals("redirect")){
					KeyList.add(mapKey);
				}

			}

			// get the elements
			return(KeyList);


		}catch(Exception e){
			return null;
		}


	}

}
