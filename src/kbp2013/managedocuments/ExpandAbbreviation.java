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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import configure.NistKBPConfiguration;

/**
 * 
 * This class expands abbreviations according to 
 * "abbreviations" mapping file
 * ex: Boulder, CO --> Boulder, Colorado
 * 
 * @author mariejeanmeurs
 *
 */
public class ExpandAbbreviation {

	// instantiate classes of constants
	NistKBPConfiguration KBvars = new NistKBPConfiguration();
	// map abbreviation <--> full name 
	HashMap<String, String> AbbreviationMap = new HashMap<String, String>();

	/**
	 * 
	 * Constructor
	 * 
	 * load abbreviation <--> fullname file
	 * 
	 */
	public ExpandAbbreviation(){

		//-----------------------
		// load abbreviation map
		//-----------------------
		try {

			BufferedReader reader = new BufferedReader(new FileReader(KBvars.ABBREVIATION_MAP_FILE));

			String abbrvline = "";

			while (( abbrvline = reader.readLine()) != null) {
				if (! abbrvline.startsWith("#")) {
					String[] abbrvfull = abbrvline.split("\t"); 
					String fullname = abbrvfull[0];
					String abbreviation = abbrvfull[1];

					// load hashmap
					// key = abbreviation | value = fullname
					AbbreviationMap.put(abbreviation, fullname);
					//System.out.println(abbreviation + " : " + fullname);

				}

			}
			reader.close();

		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * 
	 * Replace abbreviation with fullname
	 * @param content
	 * @return
	 */
	public String ReplaceAbbreviation(String content) {

		String abbrv = "";
		String fullnm = "";

		try{
			// Boulder, CO --> Boulder, Colorado
			for(Entry<String, String> entry : AbbreviationMap.entrySet()) {
				abbrv = entry.getKey();
				fullnm = entry.getValue();

				String wabbrvw = " " + abbrv + " ";
				String wabbrvc = " " + abbrv + ",";
				String wabbrvd = " " + abbrv + ".";
				String wfullnmw = " " + fullnm + " ";
				String wfullnmc = " " + fullnm + ",";
//				String wfullnmd = " " + fullnm + ".";
				String wfullnm = " " + fullnm;

				// replace found abbreviations
				if( content.contains(wabbrvw) ) { 
					content = StringUtils.replace(content, wabbrvw,wfullnmw);
//					String wabbrvwregex = wabbrvw.replaceAll("\\.","\\.");
//					content = content.replaceAll(wabbrvwregex,wfullnmw);
					System.out.println("Replaced Abbreviation w: " + wabbrvw + " : " + wfullnmw);
				}

				if (content.contains(wabbrvc)) {
					content = StringUtils.replace(content, wabbrvc,wfullnmc);
//					String wabbrvcregex = wabbrvc.replaceAll("\\.","\\."); //" " + abbrv + "\\.";
//					content = content.replaceAll(wabbrvcregex,wfullnmc);
					System.out.println("Replaced Abbreviation c: " + wabbrvc + " : " + wfullnmc);
				}

				if (content.contains(wabbrvd)) {
					// no final dot in replaced form, as the original one 
					// was probably belonging to the abbreviation itself
					// Regexp: as replaceAll takes String regex as first 
					// parameter, while contains takes Charsequence,
					// we need to create a regex from wabbrvd
					content = StringUtils.replace(content, wabbrvd,wfullnm);
//					String wabbrvdregex = " " + abbrv + "\\.";
//					content = content.replaceAll(wabbrvdregex,wfullnm);
					System.out.println("Replaced Abbreviation d: " + wabbrvd + " : " + wfullnm);
				}
			}
		}
		catch (Exception e)
		{
			//Catch exception
			System.err.println("ExpandAbbreviation Error: " + e.getMessage());

		}

		return(content);
	}

	/**
	 * 
	 * Add candidate full name before abbreviation
	 * 
	 * Warning: !! performance decreased !! 
	 * 
	 * @param content
	 * @return
	 */
	public String AddFullNameToAbbreviation(String content) {

		String abbrv = "";
		String fullnm = "";

		try{
			// Boulder, CO --> Boulder, Colorado
			for(Entry<String, String> entry : AbbreviationMap.entrySet()) {
				abbrv = entry.getKey();
				fullnm = entry.getValue();

				String wabbrvw = " " + abbrv + " ";
				String wabbrvc = " " + abbrv + ",";
				String wabbrvd = " " + abbrv + ".";
//				String wfullnmw = " " + fullnm + " ";
				String wfullnmc = " " + fullnm + ",";
//				String wfullnmd = " " + fullnm + ".";
				String wfullnm = " " + fullnm;

				// replace found abbreviations
				if( content.contains(wabbrvw) ) { 
					content = StringUtils.replace(content, wabbrvw, wfullnm + wabbrvw);
//					content = content.replaceAll(wabbrvw , wfullnm + wabbrvw);
					System.out.println("Replaced Abbreviation: " + wabbrvw + " : " + wfullnm + wabbrvw);
				}

				if (content.contains(wabbrvc)) {
					content = StringUtils.replace(content, wabbrvw, wfullnmc + wabbrvc);
//					content = content.replaceAll(wabbrvc , wfullnmc + wabbrvc);
					System.out.println("Replaced Abbreviation: " + wabbrvc + " : " + wfullnmc + wabbrvc);
				}

				if (content.contains(wabbrvd)) {
					// no final dot in replaced form, as the original one 
					// was probably belonging to the abbreviation itself
					// Regexp: as replaceAll takes String regex as first 
					// parameter, while contains takes Charsequence,
					// we need to create a regex from wabbrvd
					content = StringUtils.replace(content, wabbrvw, wfullnm + wabbrvd);
//					String wabbrvdregex = " " + abbrv + "\\.";
//					content = content.replaceAll(wabbrvdregex , wfullnm + wabbrvd);
					System.out.println("Replaced Abbreviation: " + wabbrvd + " : " + wfullnm + wabbrvd);
				}
			}

		}
		catch (Exception e)
		{
			//Catch exception
			System.err.println("ExpandAbbreviation Error: " + e.getMessage());

		}

		return(content);
	}
}