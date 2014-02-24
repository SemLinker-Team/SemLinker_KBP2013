package kbp2013.tools;

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


import configure.NistKBPConfiguration;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Lexicon {

    public Lexicon() {
        loadLexicon(commonEnglishWords, "wordsEn-2.txt");
        loadLexicon(properNouns, "countries.txt");
        loadLexicon(properNouns, "fname_base.txt");
        loadLexicon(properNouns, "cities15000.txt");
    }

    /**
     * 
     * @param document
     */
    public  Lexicon(String document) {
        
        loadLexicon(commonEnglishWords, "wordsEn-2.txt");

        String[] lines = document.split("\n");

        for (int i = 0; i < lines.length; i++) {
            String[] tokens = lines[i].split(" ");
            for (int j = 0; j < tokens.length; j++) {
                if (tokens[j].matches("^[A-Z].*") && !(j > 0 && tokens[j-1].matches("[!?\\.]"))) {
                    addItem(properNouns, tokens[j]);
                }
            }
        }
    }

    /**
     * 
     * @param word
     * @return
     */
    public boolean isCommonWord(String word) {
        return commonEnglishWords.containsKey(word.toLowerCase());
    }

    /**
     * 
     * @param word
     * @return
     */
    public boolean isProperNoun(String word) {
        return properNouns.containsKey(word.toLowerCase());
    }

    /**
     * 
     * @param map
     * @param item
     */
    private static void addItem(HashMap map, String item) {
        if (!item.startsWith("#") && !item.equals("")) {
            map.put(item.toLowerCase(), item);
        }
    }

    /**
     * 
     * @param word
     * @return
     */
    public String getProperNoun(String word) {
        return (properNouns.get(word.toLowerCase()));
    }

    /**
     * 
     * @param lexicon
     * @param fileName
     */
    private void loadLexicon(HashMap lexicon, String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(KBvars.PATH_TO_LEXICONS + fileName));

            String text = null;
            while ((text = reader.readLine()) != null) {
                addItem(lexicon, text);
            }
            
            reader.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Lexicon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Lexicon.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    private HashMap<String, String> commonEnglishWords = new HashMap();
    private HashMap<String, String> properNouns = new HashMap();
    NistKBPConfiguration KBvars = new NistKBPConfiguration();
}
