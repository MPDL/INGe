/*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/


package de.mpg.escidoc.services.structuredexportmanager.functions;

import java.util.HashMap;
import java.util.Map;

/**
 * Function extensions for the BibTex export functionality.
 * To be used from the XSLT.   
 * Converts PubMan item-list to one of the structured formats.   
 *
 * @author Vlad Makarenko (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision:$ $LastChangedDate:$
 *
 */ 

public class BibTex {

	/* 
	 * UNICODE -> BibTex mapping
	 * not really comprehensive
	 *  
	 * */
    public static final Map<String, String>  ENTITIES =     
    	new HashMap<String, String>()   
    	{  
	    	{
	    	      put("Å","{\\AA}");
	    	      put("À","{\\`A}");
	    	      put("Â","{\\^A}");
	    	      put("Á","{\\'A}");
	    	      put("Ã","{\\~A}");
	    	      put("Ä","{\\\"A}");
	    	      put("Æ","{\\AE}");
	    	      put("Ø","{\\O}");
	    	      put("à","{\\`a}");
	    	      put("á","{\\'a}");
	    	      put("â","{\\^a}");
	    	      put("ã","{\\~a}");
	    	      put("ä","{\\\"a}");
	    	      put("å","{\\aa}");
	    	      put("æ","{\\ae}");
	    	      put("Ç","{\\c C}");
	    	      put("Č","{\\u C}");
	    	      put("Č","{\\v C}");
	    	      put("ç","{\\c c}");
	    	      put("ć","{\\'c}");
	    	      put("č","{\\v c}");
	    	      put("È","{\\`E}");
	    	      put("Ê","{\\^E}");
	    	      put("É","{\\'E}");
	    	      put("Ë","{\\\"E}");
	    	      put("è","{\\`e}");
	    	      put("é","{\\'e}");
	    	      put("ê","{\\^e}");
	    	      put("ë","{\\\"e}");
	    	      put("Î","{\\^I}");
	    	      put("Í","{\\'I}");
	    	      put("Ï","{\\\"I}");
	    	      put("ì","{\\`{\\i}}");
	    	      put("í","{\\'{\\i}}");
	    	      put("î","{\\^{\\i}}");
	    	      put("ï","{\\\"{\\i}}");
	    	      put("Ñ","{\\~N}");
	    	      put("ñ","{\\~n}");
	    	      put("Ó","{\\'O}");
	    	      put("Ô","{\\^O}");
	    	      put("Ø","{\\O}");
	    	      put("Ö","{\\\"O}");
	    	      put("Œ","\\OE}");
	    	      put("ò","{\\`o}");
	    	      put("ó","{\\'o}");
	    	      put("ô","{\\^o}");
	    	      put("õ","{\\~o}");
	    	      put("ö","{\\\"o}");
	    	      put("œ","{\\oe}");
	    	      put("ø","\\o}");
	    	      put("ş","{\\c s}");
	    	      put("š","{\\v s}");
	    	      put("Ţ","{\\c T}");
	    	      put("ţ","{\\c t}");
	    	      put("Ú","{\\'U}");
	    	      put("Û","{\\^U}");
	    	      put("Ü","{\\\"U}");
	    	      put("ù","{\\`u}");
	    	      put("ú","{\\'u}");
	    	      put("û","{\\^u}");
	    	      put("ü","{\\\"u}");
	    	      put("Ÿ","{\\\"Y}");
	    	      put("ÿ","{\\\"y}");
	    	      put("Ž","{\\v Z}");
	    	      put("ž","{\\v z}");
	    	      put("ß","{\\ss}");
	    	      put("£","\\pounds");
	    	      put("±","$\\pm$");
	    	      put("–","--");
	    	      put("—","---");
	    	      put("•","*");
	    	      put("…","{\\ldots}");
	    	      put("§","{\\S}");
	    	      put("©","{\\copyright}");
	    	      put("®","{\\textregistered}");
	    	      put("™","{\\texttrademark}");
	    	      put("°","$^{\\circ}$");
	    	      put("%","\\%");    	    		
/*				put("{\"}","\"");									
				put("\\acute{e}","é");
				put("\\ast","*");
				put("\\star","*");
				put("{\\{}","{");
				put("\\{","{");
				put("{\\}}","}");
				put("\\}","}");
				put("^{\\underline{\\rm a}}","ª");
				put("^{\\circ}","°");
				put("{\\pm}","±");
				put("^{2}","²");
				put("^{3}","³");
				put("^{1}","¹");
				put("\\frac{1}{2}","½");
				put("{\\times}","×");
				put("\\times","×");
				put("{\\div}","÷");
				put("\\div","÷");
				put("\\dot G","Ġ");
				put("\\Gamma","Γ");
				put("\\Delta","Δ");
				put("\\Lambda","Λ");
				put("\\Sigma","Σ");
				put("\\Omega","Ω");
				put("\\delta","δ");
				put("\\alpha","α");
				put("\\beta","β");
				put("\\gamma","γ");
				put("\\delta","δ");
				put("\\epsilon","ε");
				put("\\zeta","ζ");
				put("\\eta","η");
				put("\\theta","θ");
				put("\\kappa","κ");
				put("\\lambda","λ");
				put("\\mu","μ");
				put("\\nu","ν");
				put("\\xi","ξ");
				put("\\pi","π");
				put("\\rho","ρ");
				put("\\sigma","σ");
				put("\\tau","τ");
				put("\\phi","φ");
				put("\\chi","χ");
				put("\\omega","ω");
				put("\\ell","ℓ");
				put("\\rightarrow","→");
				put("\\to","→");
				put("\\leftrightarrow","↔");
				put("\\nabla","∇");
				put("\\sim","∼");
				put("\\le","≤");
				put("\\ge","≥");
				put("\\lesssim","≲");
				put("\\gtrsim","≳");
				put("\\odot","⊙");
				put("\\infty","∞");
				put("\\circ","∘");
				put("\\cdot","⋅");
				put("\\dot{P}","Ṗ");
				put("{\\vec B}","⃗");
				put("\\symbol{94}","^");
				put("\\symbol{126}","~");
				put("\\~{}","~");
				put("\\$\\\\sim\\$","~");
				put("{!`}","¡");
				put("{\\copyright}","©");
				put("{?`}","¿");
				put("{\\`{A}}","À");
				put("{\\`A}","À");
				put("\\`{A}","À");
				put("\\`A","À");
				put("{\\'{A}}","Á");
				put("{\\'A}","Á");
				put("\\'{A}","Á");
				put("\\'A","Á");
				put("{\\^{A}}","Â");
				put("{\\^A}","Â");
				put("\\^{A}","Â");
				put("\\^A","Â");
				put("{\\~{A}}","Ã");
				put("{\\~A}","Ã");
				put("\\~{A}","Ã");
				put("\\~A","Ã");
				put("{\\\"{A}}","Ä");
				put("{\\\"A}","Ä");
				put("\\\"{A}","Ä");
				put("\\\"A","Ä");
				put("{\\AA}","Å");
				put("{\\AE}","Æ");
				put("{\\c{C}}","Ç");
				put("\\c{C}","Ç");
				put("{\\`{E}}","È");
				put("{\\`E}","È");
				put("\\`{E}","È");
				put("\\`E","È");
				put("{\\'{E}}","É");
				put("{\\'E}","É");
				put("\\'{E}","É");
				put("\\'E","É");
				put("{\\^{E}}","Ê");
				put("{\\^E}","Ê");
				put("\\^{E}","Ê");
				put("\\^E","Ê");
				put("{\\\"{E}}","Ë");
				put("{\\\"E}","Ë");
				put("\\\"{E}","Ë");
				put("\\\"E","Ë");
				put("{\\`{I}}","Ì");
				put("{\\`I}","Ì");
				put("\\`{I}","Ì");
				put("\\`I","Ì");
				put("{\\'{I}}","Í");
				put("{\\'I}","Í");
				put("\\'{I}","Í");
				put("\\'I","Í");
				put("{\\^{I}}","Î");
				put("{\\^I}","Î");
				put("\\^{I}","Î");
				put("\\^I","Î");
				put("{\\\"{I}}","Ï");
				put("{\\\"I}","Ï");
				put("\\\"{I}","Ï");
				put("\\\"I","Ï");
				put("{\\~{N}}","Ñ");
				put("\\~{N}","Ñ");
				put("{\\~N}","Ñ");
				put("\\~N","Ñ");
				put("{\\`{O}}","Ò");
				put("{\\`O}","Ò");
				put("\\`{O}","Ò");
				put("\\`O","Ò");
				put("{\\'{O}}","Ó");
				put("{\\'O}","Ó");
				put("\\'{O}","Ó");
				put("\\'O","Ó");
				put("{\\^{O}}","Ô");
				put("{\\^O}","Ô");
				put("\\^{O}","Ô");
				put("\\^O","Ô");
				put("{\\~{O}}","Õ");
				put("{\\~O}","Õ");
				put("\\~{O}","Õ");
				put("\\~O","Õ");
				put("{\\\"{O}}","Ö");
				put("{\\\"O}","Ö");
				put("\\\"{O}","Ö");
				put("\\\"O","Ö");
				put("{\\O}","Ø");
				put("{\\`{U}}","Ù");
				put("{\\`U}","Ù");
				put("\\`{U}","Ù");
				put("\\`U","Ù");
				put("{\\'{U}}","Ú");
				put("{\\'U}","Ú");
				put("\\'{U}","Ú");
				put("\\'U","Ú");
				put("{\\^{U}}","Û");
				put("{\\^U}","Û");
				put("\\^{U}","Û");
				put("\\^U","Û");
				put("{\\\"{U}}","Ü");
				put("{\\\"U}","Ü");
				put("\\\"{U}","Ü");
				put("\\\"U","Ü");
				put("{\\'{Y}}","Ý");
				put("{\\'Y}","Ý");
				put("\\'{Y}","Ý");
				put("\\'Y","Ý");
				put("{\\ss}","ß");
				put("{\\`{a}}","à");
				put("{\\`a}","à");
				put("\\`{a}","à");
				put("\\`a","à");
				put("{\\'{a}}","á");
				put("{\\'a}","á");
				put("\\'{a}","á");
				put("\\'a","á");
				put("{\\^{a}}","â");
				put("{\\^a}","â");
				put("\\^{a}","â");
				put("\\^a","â");
				put("{\\~{a}}","ã");
				put("{\\~a}","ã");
				put("\\~{a}","ã");
				put("\\~a","ã");
				put("{\\\"{a}}","ä");
				put("{\\\"a}","ä");
				put("\\\"{a}","ä");
				put("\\\"a","ä");
				put("{\\aa}","å");
				put("{\\ae}","æ");
				put("{\\c{c}}","ç");
				put("\\c{c}","ç");
				put("\\c c","ç");
				put("{\\`{e}}","è");
				put("{\\`e}","è");
				put("{\\` e}","è");
				put("\\`{e}","è");
				put("\\`e","è");
				put("{\\'{e}}","é");
				put("{\\'e}","é");
				put("{\\' e}","é");
				put("\\'{e}","é");
				put("\\'e","é");
				put("{\\^{e}}","ê");
				put("{\\^e}","ê");
				put("\\^{e}","ê");
				put("\\^e","ê");
				put("{\\\"{e}}","ë");
				put("{\\\"e}","ë");
				put("\\\"{e}","ë");
				put("\\\"e","ë");
				put("{\\`{\\i}}","ì");
				put("{\\`\\i}","ì");
				put("\\`{\\i}","ì");
				put("\\`\\i","ì");
				put("{\\'{\\i}}","í");
				put("{\\'\\i}","í");
				put("\\'{\\i}","í");
				put("\\'\\i","í");
				put("{\\'{i}}","í");
				put("{\\'i}","í");
				put("\\'{i}","í");
				put("\\'i","í");
				put("{\\^{\\i}}","î");
				put("{\\^\\i}","î");
				put("\\^{\\i}","î");
				put("\\^\\i","î");
				put("{\\\"{\\i}}","ï");
				put("{\\\"\\i}","ï");
				put("\\\"{\\i}","ï");
				put("\\\"\\i","ï");
				put("{\\~{n}}","ñ");
				put("\\~{n}","ñ");
				put("{\\~n}","ñ");
				put("\\~n","ñ");
				put("{\\`{o}}","ò");
				put("{\\`o}","ò");
				put("\\`{o}","ò");
				put("\\`o","ò");
				put("{\\'{o}}","ó");
				put("{\\'o}","ó");
				put("\\'{o}","ó");
				put("\\'o","ó");
				put("{\\^{o}}","ô");
				put("{\\^o}","ô");
				put("\\^{o}","ô");
				put("\\^o","ô");
				put("{\\~{o}}","õ");
				put("{\\~o}","õ");
				put("\\~{o}","õ");
				put("\\~o","õ");
				put("{\\\"{o}}","ö");
				put("{\\\"o}","ö");
				put("{\\\" o}","ö");
				put("\\\"{o}","ö");
				put("\\\"o","ö");
				put("{\\o}","ø");
				put("{\\o }","ø");
				put("{\\`{u}}","ù");
				put("{\\`u}","ù");
				put("\\`{u}","ù");
				put("\\`u","ù");
				put("{\\'{u}}","ú");
				put("{\\'u}","ú");
				put("\\'{u}","ú");
				put("\\'u","ú");
				put("{\\^{u}}","û");
				put("{\\^u}","û");
				put("\\^{u}","û");
				put("\\^u","û");
				put("{\\\"{u}}","ü");
				put("{\\\"u}","ü");
				put("{\\\" u}","ü");
				put("\\\"{u}","ü");
				put("\\\"u","ü");
				put("{\\'{y}}","ý");
				put("{\\'y}","ý");
				put("\\'{y}","ý");
				put("\\'y","ý");
				put("{\\th}","þ");
				put("{\\\"{y}}","ÿ");
				put("{\\\"y}","ÿ");
				put("\\\"{y}","ÿ");
				put("\\\"y","ÿ");
				put("{\\'{C}}","Ć");
				put("\\'{C}","Ć");
				put("{\\' C}","Ć");
				put("{\\'C}","Ć");
				put("\\'C","Ć");
				put("{\\'{c}}","ć");
				put("\\'{c}","ć");
				put("{\\' c}","ć");
				put("{\\'c}","ć");
				put("\\'c","ć");
				put("\\`c","ć");
				put("{\\v{C}}","Č");
				put("\\v{C}","Č");
				put("{\\v C}","Č");
				put("{\\v{c}}","č");
				put("\\v{c}","č");
				put("{\\v c}","č");
				put("{\\u{g}}","ğ");
				put("\\u{g}","ğ");
				put("{\\u g}","ğ");
				put("{\\u{\\i}}","ĭ");
				put("{\\u\\i}","ĭ");
				put("\\u{\\i}","ĭ");
				put("\\u\\i","ĭ");
				put("{\\i}","ı");
				put("{\\L}","Ł");
				put("{\\l}","ł");
				put("\\l{}","ł");put("{\\'{N}}","Ń");
				put("\\'{N}","Ń");
				put("{\\' N}","Ń");
				put("{\\'N}","Ń");
				put("\\'N","Ń");
				put("{\\'{n}}","ń");
				put("\\'{n}","ń");
				put("{\\' n}","ń");
				put("{\\'n}","ń");
				put("\\'n","ń");
				put("{\\OE}","Œ");
				put("{\\oe}","œ");
				put("{\\v{r}}","ř");
				put("\\v{r}","ř");
				put("{\\v r}","ř");
				put("{\\'{S}}","Ś");
				put("\\'{S}","Ś");
				put("{\\' S}","Ś");
				put("{\\'S}","Ś");
				put("\\'S","Ś");
				put("{\\'{s}}","ś");
				put("\\'{s}","ś");
				put("{\\' s}","ś");
				put("{\\'s}","ś");
				put("\\'s","ś");
				put("\\'s","ś");
				put("{\\c{S}}","Ş");
				put("\\c{S}","Ş");
				put("{\\c{s}}","ş");
				put("\\c{s}","ş");
				put("{\\v{S}}","Š");
				put("\\v{S}","Š");
				put("{\\v S}","Š");
				put("{\\u{s}}","š");
				put("\\u{s}","š");
				put("{\\v{s}}","š");
				put("\\v{s}","š");
				put("{\\'{t}}","ť");
				put("\\'{t}","ť");
				put("{\\'t}","ť");
				put("\\'t","ť");
				put("{\\={u}}","ū");
				put("{\\=u}","ū");
				put("\\={u}","ū");
				put("\\=u","ū");
				put("{\\r{u}}","ů");
				put("\\r{u}","ů");
				put("{\\'{z}}","ź");
				put("\\'{z}","ź");
				put("{\\'z}","ź");
				put("\\'z","ź");
				put("\\'z","ź");
				put("{\\.{Z}}","Ż");
				put("\\.{Z}","Ż");
				put("{\\.Z}","Ż");
				put("\\.Z","Ż");
				put("{\\.{z}}","ż");
				put("\\.{z}","ż");
				put("{\\.z}","ż");
				put("\\.z","ż");
				put("{\\v{Z}}","Ž");
				put("\\v{Z}","Ž");
				put("{\\v Z}","Ž");
				put("{\\v{z}}","ž");
				put("\\v{z}","ž");
				put("{\\v z}","ž");
				put("{\\c{e}}","ȩ");
				put("\\c{e}","ȩ");
				put("{\\v{A}}","Ǎ");
				put("\\v{A}","Ǎ");
				put("{\\v A}","Ǎ");
				put("{\\v{a}}","ǎ");
				put("\\v{a}","ǎ");
				put("{\\v a}","ǎ");
				*/
			}
    	};	
    	
	/**
	 * Escapes UNICODE string with the BibTex entities
	 * @param s 
	 * @return escaped String
	 */
	public static String texString(String str)
	{
		for( Map.Entry<String, String> entry: ENTITIES.entrySet() )
			str = str.replace(entry.getKey(), entry.getValue());
		return str;
	}
	
}


