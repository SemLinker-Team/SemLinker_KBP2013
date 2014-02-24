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

/**
 * 
 * This class hosts various methods for 
 * cleaning web docs: 
 * - remove | replace web special strings (ex: smileys)
 * - remove | replace HTML reserved chars (ex: &amp;)
 * 
 * @author mariejeanmeurs
 * @author method replaceSpecialCharacters from CUNY added by ericcharton
 * 
 */
public class NormalizedWebDoc {
	
	/**
	 * 
	 * Remove | replace HTML reserved characters
	 * 
	 * @param content
	 * @return
	 */
	public static String ReplaceHtmlReservedChars(String content){
		
		if (content.length() == 0) { return content; }
		
		// http://www.w3schools.com/tags/ref_entities.asp
		content = content.replaceAll("&quot;", "\""); // " quotation mark
		content = content.replaceAll("&apos;", "\'"); // ' apostrophe
		content = content.replaceAll("&amp;", " and "); // & ampersand
		content = content.replaceAll("&gt;", " "); // < greater-than
		content = content.replaceAll("&lt;", " "); // > less-than
		
		return(content);
	}

	/**
	 * 
	 * Remove | replace ISO 8859-1 Symbols
	 * Warning: some of these symbols are replaced with their full names
	 * tag: -> ! FULLNAME
	 * Default replacement is " "
	 * @param content
	 * @return
	 */
	public static String ReplaceISO_8859_1Symbols(String content){
		
		if (content.length() == 0) { return content; }
		
		// http://www.w3schools.com/tags/ref_entities.asp
		content = content.replaceAll("&nbsp;", " "); // non-breaking space
		content = content.replaceAll("&iexcl;", " "); // inverted exclamation mark
		content = content.replaceAll("&cent;", " cent "); // cent -> ! FULLNAME
		content = content.replaceAll("&pound;", " pound "); // pound -> ! FULLNAME
		content = content.replaceAll("&curren;", " currency "); // currency -> ! FULLNAME
		content = content.replaceAll("&yen;", " yen "); // yen -> ! FULLNAME
		content = content.replaceAll("&brvbar;", " "); // broken vertical bar
		content = content.replaceAll("&sect;", " "); // section
		content = content.replaceAll("&uml;", " - "); // spacing diaeresis
		content = content.replaceAll("&copy;", " copyright "); // copyright -> ! FULLNAME
		content = content.replaceAll("&ordf;", " "); // feminine ordinal indicator
		content = content.replaceAll("&laquo;", "\""); // angle quotation mark (left)
		content = content.replaceAll("&not;", " not "); // negation  -> ! FULLNAME
		content = content.replaceAll("&shy;", " "); // soft hyphen
		content = content.replaceAll("&reg;", " registered trademark "); // registered trademark  -> ! FULLNAME
		content = content.replaceAll("&macr;", " - "); // spacing macron
		content = content.replaceAll("&deg;", " degree "); // degree  -> ! FULLNAME
		content = content.replaceAll("&plusmn;", " "); // plus-or-minus 
		content = content.replaceAll("&sup2;", " "); // superscript 2
		content = content.replaceAll("&sup3;", " "); // superscript 3
		content = content.replaceAll("&acute;", " "); // spacing acute
		content = content.replaceAll("&micro;", " micro "); // micro  -> ! FULLNAME
		content = content.replaceAll("&para;", " "); // paragraph
		content = content.replaceAll("&middot;", " "); // middle dot
		content = content.replaceAll("&cedil;", " "); // spacing cedilla
		content = content.replaceAll("&sup1;", " "); // superscript 1
		content = content.replaceAll("&ordm;", " "); // masculine ordinal indicator
		content = content.replaceAll("&raquo;", "\""); // angle quotation mark (right)
		content = content.replaceAll("&frac14;", " "); // fraction 1/4
		content = content.replaceAll("&frac12;", " "); // fraction 1/2
		content = content.replaceAll("&frac34;", " "); // fraction 3/4
		content = content.replaceAll("&iquest;", " "); // inverted question mark
		content = content.replaceAll("&times;", " "); // multiplication
		content = content.replaceAll("&divide;", " "); // division
		
		return(content);
	}	
	/**
	 * 
	 * Remove / replace ISO 8859-1 Characters
	 * 
	 * @param content
	 * @return
	 */
	public static String ReplaceISO_8859_1Characters(String content){
		
		if (content.length() == 0) { return content; }
		
		// http://www.w3schools.com/tags/ref_entities.asp		
		content = content.replaceAll("&Agrave;", "A"); // capital a, grave accent 
		content = content.replaceAll("&Aacute;", "A"); // capital a, acute accent
		content = content.replaceAll("&Acirc;", "A"); // capital a, circumflex accent
		content = content.replaceAll("&Atilde;", "A"); // capital a, tilde
		content = content.replaceAll("&Auml;", "A"); // capital a, umlaut mark
		content = content.replaceAll("&Aring;", "A"); // capital a, ring 
		content = content.replaceAll("&AElig;", "AE"); // capital ae
		content = content.replaceAll("&Ccedil;", "C"); // capital c, cedilla
		content = content.replaceAll("&Egrave;", "E"); // capital e, grave accent
		content = content.replaceAll("&Eacute;", "E"); // capital e, acute accent
		content = content.replaceAll("&Ecirc;", "E"); // capital e, circumflex accent
		content = content.replaceAll("&Euml;", "E"); // capital e, umlaut mark
		content = content.replaceAll("&Igrave;", "I"); // capital i, grave accent
		content = content.replaceAll("&Iacute;", "I"); // capital i, acute accent
		content = content.replaceAll("&Icirc;", "I"); // capital i, circumflex accent
		content = content.replaceAll("&Iuml;", "I"); // capital i, umlaut mark
		content = content.replaceAll("&ETH;", " "); // capital eth, Icelandic
		content = content.replaceAll("&Ntilde;", "N"); // capital n, tilde
		content = content.replaceAll("&Ograve;", "O"); // capital o, grave accent
		content = content.replaceAll("&Oacute;", "O"); // capital o, acute accent
		content = content.replaceAll("&Ocirc;", "O"); // capital o, circumflex accent
		content = content.replaceAll("&Otilde;", "O"); // capital o, tilde
		content = content.replaceAll("&Ouml;", "O"); // capital o, umlaut mark
		content = content.replaceAll("&Oslash;", "O"); // capital o, slash
		content = content.replaceAll("&Ugrave;", "U"); // capital u, grave accent
		content = content.replaceAll("&Uacute;", "U"); // capital u, acute accent
		content = content.replaceAll("&Ucirc;", "U"); // capital u, circumflex accent
		content = content.replaceAll("&Uuml;", "U"); // capital u, umlaut mark
		content = content.replaceAll("&Yacute;", "Y"); // capital y, acute accent
		content = content.replaceAll("&THORN;", " "); // capital THORN, Icelandic
		content = content.replaceAll("&szlig;", "ss"); // small sharp s, German
		content = content.replaceAll("&agrave;", "a"); // small a, grave accent
		content = content.replaceAll("&aacute;", "a"); // small a, acute accent
		content = content.replaceAll("&acirc;", "a"); // small a, circumflex accent
		content = content.replaceAll("&atilde;", "a"); // small a, tilde
		content = content.replaceAll("&auml;", "a"); // small a, umlaut mark
		content = content.replaceAll("&aring;", "a"); // small a, ring
		content = content.replaceAll("&aelig;", "ae"); // small ae
		content = content.replaceAll("&ccedil;", "c"); // small c, cedilla
		content = content.replaceAll("&egrave;", "e"); // small e, grave accent
		content = content.replaceAll("&eacute;", "e"); // small e, acute accent
		content = content.replaceAll("&ecirc;", "e"); // small e, circumflex accent
		content = content.replaceAll("&euml;", "e"); // small e, umlaut mark
		content = content.replaceAll("&igrave;", "i"); // small i, grave accent
		content = content.replaceAll("&iacute;", "i"); // small i, acute accent
		content = content.replaceAll("&icirc;", "i"); // small i, circumflex accent 
		content = content.replaceAll("&iuml;", "i"); // small i, umlaut mark
		content = content.replaceAll("&eth;", " "); // small eth, Icelandic
		content = content.replaceAll("&ntilde;", "n"); // small n, tilde
		content = content.replaceAll("&ograve;", "o"); // small o, grave accent
		content = content.replaceAll("&oacute;", "o"); // small o, acute accent
		content = content.replaceAll("&ocirc;", "o"); // small o, circumflex accent
		content = content.replaceAll("&otilde;", "o"); // small o, tilde
		content = content.replaceAll("&ouml;", "o"); // small o, umlaut mark
		content = content.replaceAll("&oslash;", "o"); // small o, slash
		content = content.replaceAll("&ugrave;", "u"); // small u, grave accent
		content = content.replaceAll("&uacute;", "u"); // small u, acute accent
		content = content.replaceAll("&ucirc;", "u"); // small u, circumflex accent
		content = content.replaceAll("&uuml;", "u"); // small u, umlaut mark
		content = content.replaceAll("&yacute;", "y"); // small y, acute accent
		content = content.replaceAll("&thorn;", " "); // small thorn, Icelandic
		content = content.replaceAll("&yuml;", "y"); // small y, umlaut mark
		
		return(content);
	}
	
	/**
	 * 
	 * Remove | replace smil-ish characters
	 * 
	 * @param content
	 * @return
	 */
	public static String ReplaceHtmlSmileyChars(String content){
		
		try{
			
			if (content.length() == 0) { return content; }
			
			content = content.replaceAll("\\{\\}:   \\)", ""); // evilly happy
			content = content.replaceAll("\\{\\}:   \\(", ""); // evilly angry
			content = content.replaceAll("\\{\\}\' \\.    \\)", ""); // 
			content = content.replaceAll("\\{\\} \\.  - \\)", ""); // 
			content = content.replaceAll("&#9786;", ""); // happy white
			content = content.replaceAll("&#9787;", ""); // happy black
			content = content.replaceAll("&#1578;", ""); // happy 
			content = content.replaceAll("&#12485;", ""); // happy
			content = content.replaceAll("&#12484;", ""); // happy
			content = content.replaceAll("&#12483;", ""); // happy
			content = content.replaceAll("&#12471;", ""); // happy
			content = content.replaceAll("&#220;", ""); // happy
			content = content.replaceAll("&#993;", ""); // happy
			content = content.replaceAll("&#64354;", ""); // happy
		}catch (Exception e){
			System.out.println("Error in String ReplaceHtmlSmileyChars(String content):" + e);
		}
		
		return(content);
	}
	
	/**
	 * 
	 * Remove | replace HTML Math Symbols
	 * Warning: most of these symbols are replaced with their full names
	 * Default replacement is " "
	 * @param content
	 * @return
	 */
	public static String ReplaceHtmlMathSymbol(String content){
		
		if (content.length() == 0) { return content; }
		
		// http://www.w3schools.com/tags/ref_symbols.asp
		content = content.replaceAll("&forall;", " for all "); //
		content = content.replaceAll("&part;", " part "); //
		content = content.replaceAll("&exist;", " exists "); //
		content = content.replaceAll("&empty;", " empty "); // 
		content = content.replaceAll("&nabla;", " nabla "); // 
		content = content.replaceAll("&isin;", " is in"); // 
		content = content.replaceAll("&notin;", " not in "); // 
		content = content.replaceAll("&ni;", " ni "); //
		content = content.replaceAll("&prod;", " prod "); // 
		content = content.replaceAll("&sum;", " sum "); // 
		content = content.replaceAll("&minus;", " minus "); //
		content = content.replaceAll("&lowast;", " lowast "); // 
		content = content.replaceAll("&radic;", " square root "); // 
		content = content.replaceAll("&prop;", " proportional to "); //
		content = content.replaceAll("&infin;", " infinity "); // 
		content = content.replaceAll("&ang;", " angle "); // 
		content = content.replaceAll("&and;", " and "); //
		content = content.replaceAll("&or;", " or "); // 
		content = content.replaceAll("&cap;", " cap "); // 
		content = content.replaceAll("&cup;", " cup "); //
		content = content.replaceAll("&int;", " integral "); // 
		content = content.replaceAll("&there4;", " therefore "); //
		content = content.replaceAll("&sim;", " similar to "); // 
		content = content.replaceAll("&cong;", " congruent to "); //
		content = content.replaceAll("&asymp;", " almost equal "); // 
		content = content.replaceAll("&ne;", " not equal "); //
		content = content.replaceAll("&equiv;", " equivalent "); // 
		content = content.replaceAll("&le;", " less or equal "); //
		content = content.replaceAll("&ge;", " greater or equal "); // 
		content = content.replaceAll("&sub;", " subset of "); //
		content = content.replaceAll("&sup;", " superset of "); // 
		content = content.replaceAll("&nsub;", " not subset of "); //
		content = content.replaceAll("&sube;", " subset or equal "); // 
		content = content.replaceAll("&supe;", " superset or equal "); //
		content = content.replaceAll("&oplus;", " circled plus "); //
		content = content.replaceAll("&otimes;", " circled times "); // 
		content = content.replaceAll("&perp;", " perpendicular "); //
		content = content.replaceAll("&sdot;", " dot operator "); //
	
		return(content);
	}
 
	/**
	 * 
	 * Remove | replace HTML Greek Letters
	 * Warning: letters are replaced with their full names
	 * Default replacement is " "
	 * @param content
	 * @return
	 */
	public static String ReplaceHtmlGreekLetter(String content){
		
		if (content.length() == 0) { return content; }
		
		// http://www.w3schools.com/tags/ref_symbols.asp
		content = content.replaceAll("&Alpha;", "Alpha"); // 
		content = content.replaceAll("&Beta;", "Beta"); //
		content = content.replaceAll("&Gamma;", "Gamma"); //
		content = content.replaceAll("&Delta;", "Delta"); // 
		content = content.replaceAll("&Epsilon;", "Epsilon"); //
		content = content.replaceAll("&Zeta;", "Zeta"); //
		content = content.replaceAll("&Eta;", "Eta"); // 
		content = content.replaceAll("&Theta;", "Theta"); //
		content = content.replaceAll("&Iota;", "Iota"); // 
		content = content.replaceAll("&Kappa;", "Kappa"); //
		content = content.replaceAll("&Lambda;", "Lambda"); //
		content = content.replaceAll("&Mu;", "Mu"); // 
		content = content.replaceAll("&Nu;", "Nu"); //
		content = content.replaceAll("&Xi;", "Xi"); //
		content = content.replaceAll("&Omicron;", "Omicron"); // 
		content = content.replaceAll("&Pi;", "Pi"); //
		content = content.replaceAll("&Rho;", "Rho"); // 
		content = content.replaceAll("&Sigma;", "Sigma"); //
		content = content.replaceAll("&Tau;", "Tau"); //
		content = content.replaceAll("&Upsilon;", "Upsilon"); // 
		content = content.replaceAll("&Phi;", "Phi"); //
		content = content.replaceAll("&Chi;", "Chi"); //
		content = content.replaceAll("&Psi;", "Psi"); // 
		content = content.replaceAll("&Omega;", "Omega"); //
		content = content.replaceAll("&alpha;", "alpha"); // 
		content = content.replaceAll("&beta;", "beta"); //
		content = content.replaceAll("&gamma;", "gamma"); //
		content = content.replaceAll("&delta;", "delta"); // 
		content = content.replaceAll("&epsilon;", "epsilon"); //
		content = content.replaceAll("&zeta;", "zeta"); //
		content = content.replaceAll("&eta;", "eta"); // 
		content = content.replaceAll("&theta;", "theta"); //
		content = content.replaceAll("&iota;", "iota"); // 
		content = content.replaceAll("&kappa;", "kappa"); //
		content = content.replaceAll("&lambda;", "lambda"); //
		content = content.replaceAll("&mu;", "mu"); // 
		content = content.replaceAll("&nu;", "nu"); //
		content = content.replaceAll("&xi;", "xi"); //
		content = content.replaceAll("&omicron;", "omicron"); // 
		content = content.replaceAll("&pi;", "pi"); //
		content = content.replaceAll("&rho;", "rho"); //
		content = content.replaceAll("&sigmaf;", "sigmaf"); // 
		content = content.replaceAll("&sigma;", "sigma"); //
		content = content.replaceAll("&tau;", "tau"); //
		content = content.replaceAll("&upsilon;", "upsilon"); // 
		content = content.replaceAll("&phi;", "phi"); //
		content = content.replaceAll("&chi;", "chi"); //
		content = content.replaceAll("&psi;", "psi"); // 
		content = content.replaceAll("&omega;", "omega"); //
		content = content.replaceAll("&thetasym;", "theta"); //
		content = content.replaceAll("&upsih;", "upsilon"); // 
		content = content.replaceAll("&piv;", "pi"); //
	
		return(content);
	}	
	
	/**
	 * TODO 
	 * Remove | replace various HTML entities
	 * Default replacement is ""
	 * @param content
	 * @return
	 */
	public static String ReplaceHtmlOtherEntities(String content){
		
		if (content.length() == 0) { return content; }
		
		// http://www.w3schools.com/tags/ref_symbols.asp
		content = content.replaceAll("&OElig;", "OE"); // capital ligature OE
		content = content.replaceAll("&oelig;", "oe"); // small ligature oe
		content = content.replaceAll("&Scaron;", "S"); // capital S with caron
		content = content.replaceAll("&scaron;", "s"); // small S with caron
		content = content.replaceAll("&Yuml;", "Y"); // capital Y with diaeres
		content = content.replaceAll("&fnof;", "f"); // f with hook
		content = content.replaceAll("&circ;", ""); // modifier letter circumflex accent
		content = content.replaceAll("&tilde;", ""); // small tilde
		content = content.replaceAll("&ensp;", " "); // en space
		content = content.replaceAll("&emsp;", " "); // em space
		content = content.replaceAll("&thinsp;", " "); // thin space
		content = content.replaceAll("&zwnj;", ""); // zero width non-joiner
		content = content.replaceAll("&zwj;", ""); // zero width joiner
		content = content.replaceAll("&lrm;", ""); // left-to-right mark
		content = content.replaceAll("&rlm;", ""); // right-to-left mark
		content = content.replaceAll("&ndash;", "-"); // en dash
		content = content.replaceAll("&mdash;", "-"); // em dash
		content = content.replaceAll("&lsquo;", "\'"); // left single quotation mark
		content = content.replaceAll("&rsquo;", "\'"); // right single quotation mark
		content = content.replaceAll("&sbquo;", "\'"); // single low-9 quotation mark
		content = content.replaceAll("&ldquo;", "\""); // left double quotation mark
		content = content.replaceAll("&rdquo;", "\""); // right double quotation mark
		content = content.replaceAll("&bdquo;", "\""); // double low-9 quotation mark
		content = content.replaceAll("&dagger;", ""); // dagger
		content = content.replaceAll("&Dagger;", ""); // double dagger
		content = content.replaceAll("&bull;", " . "); // bullet
		content = content.replaceAll("&hellip;", "..."); // horizontal ellipsis
		content = content.replaceAll("&permil;", "per mille"); // 
		content = content.replaceAll("&prime;", "minutes"); // minutes
		content = content.replaceAll("&Prime;", "seconds"); // seconds
		content = content.replaceAll("&lsaquo;", "\'"); // single left angle quotation
		content = content.replaceAll("&rsaquo;", "\'"); // single right angle quotation
		content = content.replaceAll("&oline;", ""); // overline
		content = content.replaceAll("&euro;", "euro"); // euro
		content = content.replaceAll("&trade;", "trademark"); // trademark
		content = content.replaceAll("&larr;", ""); // left arrow
		content = content.replaceAll("&uarr;", ""); // up arrow
		content = content.replaceAll("&rarr;", ""); // right arrow
		content = content.replaceAll("&darr;", ""); // down arrow
		content = content.replaceAll("&harr;", ""); // left right arrow
		content = content.replaceAll("&crarr;", ""); // carriage return arrow
		content = content.replaceAll("&lceil;", ""); // left ceiling
		content = content.replaceAll("&rceil;", ""); // right ceiling
		content = content.replaceAll("&lfloor;", ""); // left floor
		content = content.replaceAll("&rfloor;", ""); // right floor
		content = content.replaceAll("&loz;", ""); // lozenge
		content = content.replaceAll("&spades;", ""); // spade
		content = content.replaceAll("&clubs;", ""); // club
		content = content.replaceAll("&hearts;", ""); // heart
		content = content.replaceAll("&diams;", ""); // diamond
	
		return(content);
	}		
	
	
	/**
	 * Handles special characters in HTML documents by replacing sequences of
	 * the form <code>&...;</code> by the corresponding characters.
	 * 
	 * This class comes from CUNY HTMLConverter.java
	 * 
	 * @param html html document
	 * @return transformed html document
	 */
	public static String replaceSpecialCharacters(String html) {
		html = html.replaceAll("&#09;", " ");
		html = html.replaceAll("&#10;", " ");
		html = html.replaceAll("&#32;", " ");
		html = html.replaceAll("&#33;", "!");
		html = html.replaceAll("(?i)(&#34;|&quot;)", "\"");
		html = html.replaceAll("&#35;", "#");
		html = html.replaceAll("&#36;", "$");
		html = html.replaceAll("&#37;", "%");
		html = html.replaceAll("(?i)(&#38;|&amp;)", "&");
		html = html.replaceAll("&#39;", "'");
		html = html.replaceAll("&#40;", "(");
		html = html.replaceAll("&#41;", ")");
		html = html.replaceAll("&#42;", "*");
		html = html.replaceAll("&#43;", "+");
		html = html.replaceAll("&#44;", ",");
		html = html.replaceAll("&#45;", "-");
		html = html.replaceAll("&#46;", ".");
		html = html.replaceAll("(?i)(&#47;|&frasl;)", "/");
		html = html.replaceAll("&#48;", "0");
		html = html.replaceAll("&#49;", "1");
		html = html.replaceAll("&#50;", "2");
		html = html.replaceAll("&#51;", "3");
		html = html.replaceAll("&#52;", "4");
		html = html.replaceAll("&#53;", "5");
		html = html.replaceAll("&#54;", "6");
		html = html.replaceAll("&#55;", "7");
		html = html.replaceAll("&#56;", "8");
		html = html.replaceAll("&#57;", "9");
		html = html.replaceAll("&#58;", ":");
		html = html.replaceAll("&#59;", ";");
		html = html.replaceAll("(?i)(&#60;|&lt;)", "<");
		html = html.replaceAll("&#61;", "=");
		html = html.replaceAll("(?i)(&#62;|&gt;)", ">");
		html = html.replaceAll("&#63;", "?");
		html = html.replaceAll("&#64;", "@");
		html = html.replaceAll("&#65;", "A");
		html = html.replaceAll("&#66;", "B");
		html = html.replaceAll("&#67;", "C");
		html = html.replaceAll("&#68;", "D");
		html = html.replaceAll("&#69;", "E");
		html = html.replaceAll("&#70;", "F");
		html = html.replaceAll("&#71;", "G");
		html = html.replaceAll("&#72;", "H");
		html = html.replaceAll("&#73;", "I");
		html = html.replaceAll("&#74;", "J");
		html = html.replaceAll("&#75;", "K");
		html = html.replaceAll("&#76;", "L");
		html = html.replaceAll("&#77;", "M");
		html = html.replaceAll("&#78;", "N");
		html = html.replaceAll("&#79;", "O");
		html = html.replaceAll("&#80;", "P");
		html = html.replaceAll("&#81;", "Q");
		html = html.replaceAll("&#82;", "R");
		html = html.replaceAll("&#83;", "S");
		html = html.replaceAll("&#84;", "T");
		html = html.replaceAll("&#85;", "U");
		html = html.replaceAll("&#86;", "V");
		html = html.replaceAll("&#87;", "W");
		html = html.replaceAll("&#88;", "X");
		html = html.replaceAll("&#89;", "Y");
		html = html.replaceAll("&#90;", "Z");
		html = html.replaceAll("&#91;", "[");
		//html = html.replaceAll("&#92;", "\\");
		html = html.replaceAll("&#93;", "]");
		html = html.replaceAll("&#94;", "^");
		html = html.replaceAll("&#95;", "_");
		html = html.replaceAll("&#96;", "`");
		html = html.replaceAll("&#97;", "a");
		html = html.replaceAll("&#98;", "b");
		html = html.replaceAll("&#99;", "c");
		html = html.replaceAll("&#100;", "d");
		html = html.replaceAll("&#101;", "e");
		html = html.replaceAll("&#102;", "f");
		html = html.replaceAll("&#103;", "g");
		html = html.replaceAll("&#104;", "h");
		html = html.replaceAll("&#105;", "i");
		html = html.replaceAll("&#106;", "j");
		html = html.replaceAll("&#107;", "k");
		html = html.replaceAll("&#108;", "l");
		html = html.replaceAll("&#109;", "m");
		html = html.replaceAll("&#110;", "n");
		html = html.replaceAll("&#111;", "o");
		html = html.replaceAll("&#112;", "p");
		html = html.replaceAll("&#113;", "q");
		html = html.replaceAll("&#114;", "r");
		html = html.replaceAll("&#115;", "s");
		html = html.replaceAll("&#116;", "t");
		html = html.replaceAll("&#117;", "u");
		html = html.replaceAll("&#118;", "v");
		html = html.replaceAll("&#119;", "w");
		html = html.replaceAll("&#120;", "x");
		html = html.replaceAll("&#121;", "y");
		html = html.replaceAll("&#122;", "z");
		html = html.replaceAll("&#123;", "{");
		html = html.replaceAll("&#124;", "|");
		html = html.replaceAll("&#125;", "}");
		html = html.replaceAll("&#126;", "~");
		html = html.replaceAll("(?i)(&#150;|&ndash;)", "");
		html = html.replaceAll("(?i)(&#151;|&mdash;)", "");
		html = html.replaceAll("(?i)(&#160;|&nbsp;)", " ");
		html = html.replaceAll("(?i)(&#161;|&iexcl;)", "¡");
		html = html.replaceAll("(?i)(&#162;|&cent;)", "¢");
		html = html.replaceAll("(?i)(&#163;|&pound;)", "£");
		html = html.replaceAll("(?i)(&#164;|&curren;)", "¤");
		html = html.replaceAll("(?i)(&#165;|&yen;)", "¥");
		html = html.replaceAll("(?i)(&#166;|&brvbar;|&brkbar;)", "¦");
		html = html.replaceAll("(?i)(&#167;|&sect;)", "§");
		html = html.replaceAll("(?i)(&#168;|&uml;|&die;)", "¨");
		html = html.replaceAll("(?i)(&#169;|&copy;)", "©");
		html = html.replaceAll("(?i)(&#170;|&ordf;)", "ª");
		html = html.replaceAll("(?i)(&#171;|&laquo;)", "«");
		html = html.replaceAll("(?i)(&#172;|&not;)", "¬");
		html = html.replaceAll("(?i)(&#173;|&shy;)", "");
		html = html.replaceAll("(?i)(&#174;|&reg;)", "®");
		html = html.replaceAll("(?i)(&#175;|&macr;|&hibar;)", "¯");
		// TODO complete this list
		// (http://webmonkey.wired.com/webmonkey/reference/special_characters/)
		
		html = html.replaceAll("&#?+\\w*+;", "");  // drop invalid codes
		
		return html;
	}
	
	/**
	 * 
	 * Used to normalize forum documents.
	 * 
	 * @deprecated for bad performances
	 * 
	 */
    public static String forumnormalizer(String textContent){        
        //beginning of the tags
        textContent = textContent.replaceAll("<post [^<]+>", "");
        textContent = textContent.replaceAll("</post>", "");
        
        textContent = textContent.replaceAll("<img [^<]+/>", "");

        textContent = textContent.replaceAll("<a [^<]+>", "");
        textContent = textContent.replaceAll("</a>", "");

        textContent = textContent.replaceAll("<quote [^<]+>","");
        textContent = textContent.replaceAll("</quote>","");
        
        return textContent;
    }
}