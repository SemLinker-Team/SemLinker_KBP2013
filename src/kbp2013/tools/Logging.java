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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Logging {
   
    BufferedWriter writer = null;

    public Logging(){
        
    }
    public Logging(String logFile) {
        if (logFile != null) {
            try {
                writer = new BufferedWriter(new FileWriter(logFile));
            } catch (IOException ex) {
                Logger.getLogger(Logging.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
    }
        

    /**
     * 
     * @param content
     */
    public void writeLog(String content) {
        System.out.println(content);

        if (writer != null) {
            try {
                writer.append(content + "\n");
                writer.flush();
            } catch (IOException ex) {
                Logger.getLogger(Logging.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }


}
