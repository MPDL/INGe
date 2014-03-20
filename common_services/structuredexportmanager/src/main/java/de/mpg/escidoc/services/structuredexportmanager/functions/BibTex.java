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
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
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
 * @version $Revision$ $LastChangedDate$
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
                put("\"","{\"}");                                
                put("é","\\acute{e}");
                put("*","\\ast");
                put("*","\\star");
                put("{","{\\{}");
                put("{","\\{");
                put("}","{\\}}");
                put("}","\\}");
                put("ª","^{\\underline{\\rm a}}");
                put("°","^{\\circ}");
                put("±","{\\pm}");
                put("²","^{2}");
                put("³","^{3}");
                put("¹","^{1}");
                put("½","\\frac{1}{2}");
                put("×","{\\times}");
                put("×","\\times");
                put("÷","{\\div}");
                put("÷","\\div");
                put("Ġ","\\dot G");
                put("Γ","\\Gamma");
                put("Δ","\\Delta");
                put("Λ","\\Lambda");
                put("Σ","\\Sigma");
                put("Ω","\\Omega");
                put("δ","\\delta");
                put("α","\\alpha");
                put("β","\\beta");
                put("γ","\\gamma");
                put("δ","\\delta");
                put("ε","\\epsilon");
                put("ζ","\\zeta");
                put("η","\\eta");
                put("θ","\\theta");
                put("κ","\\kappa");
                put("λ","\\lambda");
                put("μ","\\mu");
                put("µ","\\mirco");
                put("ν","\\nu");
                put("ξ","\\xi");
                put("π","\\pi");
                put("ρ","\\rho");
                put("σ","\\sigma");
                put("τ","\\tau");
                put("φ","\\phi");
                put("χ","\\chi");
                put("ω","\\omega");
                put("ℓ","\\ell");
                put("→","\\rightarrow");
                put("→","\\to");
                put("↔","\\leftrightarrow");
                put("∇","\\nabla");
                put("∼","\\sim");
                put("≤","\\le");
                put("≥","\\ge");
                put("≲","\\lesssim");
                put("≳","\\gtrsim");
                put("⊙","\\odot");
                put("∞","\\infty");
                put("∘","\\circ");
                put("⋅","\\cdot");
                put("Ṗ","\\dot{P}");
                put("⃗","{\\vec B}");
                put("^","\\symbol{94}");
                put("~","\\symbol{126}");
                put("~","\\~{}");
                put("~","\\$\\\\sim\\$");
                put("¡","{!`}");
                put("©","{\\copyright}");
                put("¿","{?`}");
                put("À","{\\`{A}}");
                put("À","{\\`A}");
                put("À","\\`{A}");
                put("À","\\`A");
                put("Á","{\\'{A}}");
                put("Á","{\\'A}");
                put("Á","\\'{A}");
                put("Á","\\'A");
                put("Â","{\\^{A}}");
                put("Â","{\\^A}");
                put("Â","\\^{A}");
                put("Â","\\^A");
                put("Ã","{\\~{A}}");
                put("Ã","{\\~A}");
                put("Ã","\\~{A}");
                put("Ã","\\~A");
                put("Ä","{\\\"{A}}");
                put("Ä","{\\\"A}");
                put("Ä","\\\"{A}");
                put("Ä","\\\"A");
                put("Å","{\\AA}");
                put("Æ","{\\AE}");
                put("Ç","{\\c{C}}");
                put("Ç","\\c{C}");
                put("È","{\\`{E}}");
                put("È","{\\`E}");
                put("È","\\`{E}");
                put("È","\\`E");
                put("É","{\\'{E}}");
                put("É","{\\'E}");
                put("É","\\'{E}");
                put("É","\\'E");
                put("Ê","{\\^{E}}");
                put("Ê","{\\^E}");
                put("Ê","\\^{E}");
                put("Ê","\\^E");
                put("Ë","{\\\"{E}}");
                put("Ë","{\\\"E}");
                put("Ë","\\\"{E}");
                put("Ë","\\\"E");
                put("Ì","{\\`{I}}");
                put("Ì","{\\`I}");
                put("Ì","\\`{I}");
                put("Ì","\\`I");
                put("Í","{\\'{I}}");
                put("Í","{\\'I}");
                put("Í","\\'{I}");
                put("Í","\\'I");
                put("Î","{\\^{I}}");
                put("Î","{\\^I}");
                put("Î","\\^{I}");
                put("Î","\\^I");
                put("Ï","{\\\"{I}}");
                put("Ï","{\\\"I}");
                put("Ï","\\\"{I}");
                put("Ï","\\\"I");
                put("Ñ","{\\~{N}}");
                put("Ñ","\\~{N}");
                put("Ñ","{\\~N}");
                put("Ñ","\\~N");
                put("Ò","{\\`{O}}");
                put("Ò","{\\`O}");
                put("Ò","\\`{O}");
                put("Ò","\\`O");
                put("Ó","{\\'{O}}");
                put("Ó","{\\'O}");
                put("Ó","\\'{O}");
                put("Ó","\\'O");
                put("Ô","{\\^{O}}");
                put("Ô","{\\^O}");
                put("Ô","\\^{O}");
                put("Ô","\\^O");
                put("Õ","{\\~{O}}");
                put("Õ","{\\~O}");
                put("Õ","\\~{O}");
                put("Õ","\\~O");
                put("Ö","{\\\"{O}}");
                put("Ö","{\\\"O}");
                put("Ö","\\\"{O}");
                put("Ö","\\\"O");
                put("Ø","{\\O}");
                put("Ù","{\\`{U}}");
                put("Ù","{\\`U}");
                put("Ù","\\`{U}");
                put("Ù","\\`U");
                put("Ú","{\\'{U}}");
                put("Ú","{\\'U}");
                put("Ú","\\'{U}");
                put("Ú","\\'U");
                put("Û","{\\^{U}}");
                put("Û","{\\^U}");
                put("Û","\\^{U}");
                put("Û","\\^U");
                put("Ü","{\\\"{U}}");
                put("Ü","{\\\"U}");
                put("Ü","\\\"{U}");
                put("Ü","\\\"U");
                put("Ý","{\\'{Y}}");
                put("Ý","{\\'Y}");
                put("Ý","\\'{Y}");
                put("Ý","\\'Y");
                put("ß","{\\ss}");
                put("à","{\\`{a}}");
                put("à","{\\`a}");
                put("à","\\`{a}");
                put("à","\\`a");
                put("á","{\\'{a}}");
                put("á","{\\'a}");
                put("á","\\'{a}");
                put("á","\\'a");
                put("â","{\\^{a}}");
                put("â","{\\^a}");
                put("â","\\^{a}");
                put("â","\\^a");
                put("ã","{\\~{a}}");
                put("ã","{\\~a}");
                put("ã","\\~{a}");
                put("ã","\\~a");
                put("ä","{\\\"{a}}");
                put("ä","{\\\"a}");
                put("ä","\\\"{a}");
                put("ä","\\\"a");
                put("å","{\\aa}");
                put("æ","{\\ae}");
                put("ç","{\\c{c}}");
                put("ç","\\c{c}");
                put("ç","\\c c");
                put("è","{\\`{e}}");
                put("è","{\\`e}");
                put("è","{\\` e}");
                put("è","\\`{e}");
                put("è","\\`e");
                put("é","{\\'{e}}");
                put("é","{\\'e}");
                put("é","{\\' e}");
                put("é","\\'{e}");
                put("é","\\'e");
                put("ê","{\\^{e}}");
                put("ê","{\\^e}");
                put("ê","\\^{e}");
                put("ê","\\^e");
                put("ë","{\\\"{e}}");
                put("ë","{\\\"e}");
                put("ë","\\\"{e}");
                put("ë","\\\"e");
                put("ì","{\\`{\\i}}");
                put("ì","{\\`\\i}");
                put("ì","\\`{\\i}");
                put("ì","\\`\\i");
                put("í","{\\'{\\i}}");
                put("í","{\\'\\i}");
                put("í","\\'{\\i}");
                put("í","\\'\\i");
                put("í","{\\'{i}}");
                put("í","{\\'i}");
                put("í","\\'{i}");
                put("í","\\'i");
                put("î","{\\^{\\i}}");
                put("î","{\\^\\i}");
                put("î","\\^{\\i}");
                put("î","\\^\\i");
                put("ï","{\\\"{\\i}}");
                put("ï","{\\\"\\i}");
                put("ï","\\\"{\\i}");
                put("ï","\\\"\\i");
                put("ñ","{\\~{n}}");
                put("ñ","\\~{n}");
                put("ñ","{\\~n}");
                put("ñ","\\~n");
                put("ò","{\\`{o}}");
                put("ò","{\\`o}");
                put("ò","\\`{o}");
                put("ò","\\`o");
                put("ó","{\\'{o}}");
                put("ó","{\\'o}");
                put("ó","\\'{o}");
                put("ó","\\'o");
                put("ô","{\\^{o}}");
                put("ô","{\\^o}");
                put("ô","\\^{o}");
                put("ô","\\^o");
                put("õ","{\\~{o}}");
                put("õ","{\\~o}");
                put("õ","\\~{o}");
                put("õ","\\~o");
                put("ö","{\\\"{o}}");
                put("ö","{\\\"o}");
                put("ö","{\\\" o}");
                put("ö","\\\"{o}");
                put("ö","\\\"o");
                put("ø","{\\o}");
                put("ø","{\\o }");
                put("ù","{\\`{u}}");
                put("ù","{\\`u}");
                put("ù","\\`{u}");
                put("ù","\\`u");
                put("ú","{\\'{u}}");
                put("ú","{\\'u}");
                put("ú","\\'{u}");
                put("ú","\\'u");
                put("û","{\\^{u}}");
                put("û","{\\^u}");
                put("û","\\^{u}");
                put("û","\\^u");
                put("ü","{\\\"{u}}");
                put("ü","{\\\"u}");
                put("ü","{\\\" u}");
                put("ü","\\\"{u}");
                put("ü","\\\"u");
                put("ý","{\\'{y}}");
                put("ý","{\\'y}");
                put("ý","\\'{y}");
                put("ý","\\'y");
                put("þ","{\\th}");
                put("ÿ","{\\\"{y}}");
                put("ÿ","{\\\"y}");
                put("ÿ","\\\"{y}");
                put("ÿ","\\\"y");
                put("Ć","{\\'{C}}");
                put("Ć","\\'{C}");
                put("Ć","{\\' C}");
                put("Ć","{\\'C}");
                put("Ć","\\'C");
                put("ć","{\\'{c}}");
                put("ć","\\'{c}");
                put("ć","{\\' c}");
                put("ć","{\\'c}");
                put("ć","\\'c");
                put("ć","\\`c");
                put("Č","{\\v{C}}");
                put("Č","\\v{C}");
                put("Č","{\\v C}");
                put("č","{\\v{c}}");
                put("č","\\v{c}");
                put("č","{\\v c}");
                put("ğ","{\\u{g}}");
                put("ğ","\\u{g}");
                put("ğ","{\\u g}");
                put("ĭ","{\\u{\\i}}");
                put("ĭ","{\\u\\i}");
                put("ĭ","\\u{\\i}");
                put("ĭ","\\u\\i");
                put("ı","{\\i}");
                put("Ł","{\\L}");
                put("ł","{\\l}");
                put("ł","\\l{}");
                put("Ń","{\\'{N}}");
                put("Ń","\\'{N}");
                put("Ń","{\\' N}");
                put("Ń","{\\'N}");
                put("Ń","\\'N");
                put("ń","{\\'{n}}");
                put("ń","\\'{n}");
                put("ń","{\\' n}");
                put("ń","{\\'n}");
                put("ń","\\'n");
                put("Œ","{\\OE}");
                put("œ","{\\oe}");
                put("ř","{\\v{r}}");
                put("ř","\\v{r}");
                put("ř","{\\v r}");
                put("Ś","{\\'{S}}");
                put("Ś","\\'{S}");
                put("Ś","{\\' S}");
                put("Ś","{\\'S}");
                put("Ś","\\'S");
                put("ś","{\\'{s}}");
                put("ś","\\'{s}");
                put("ś","{\\' s}");
                put("ś","{\\'s}");
                put("ś","\\'s");
                put("ś","\\'s");
                put("Ş","{\\c{S}}");
                put("Ş","\\c{S}");
                put("ş","{\\c{s}}");
                put("ş","\\c{s}");
                put("Š","{\\v{S}}");
                put("Š","\\v{S}");
                put("Š","{\\v S}");
                put("š","{\\u{s}}");
                put("š","\\u{s}");
                put("š","{\\v{s}}");
                put("š","\\v{s}");
                put("ť","{\\'{t}}");
                put("ť","\\'{t}");
                put("ť","{\\'t}");
                put("ť","\\'t");
                put("ū","{\\={u}}");
                put("ū","{\\=u}");
                put("ū","\\={u}");
                put("ū","\\=u");
                put("ů","{\\r{u}}");
                put("ů","\\r{u}");
                put("ź","{\\'{z}}");
                put("ź","\\'{z}");
                put("ź","{\\'z}");
                put("ź","\\'z");
                put("ź","\\'z");
                put("Ż","{\\.{Z}}");
                put("Ż","\\.{Z}");
                put("Ż","{\\.Z}");
                put("Ż","\\.Z");
                put("ż","{\\.{z}}");
                put("ż","\\.{z}");
                put("ż","{\\.z}");
                put("ż","\\.z");
                put("Ž","{\\v{Z}}");
                put("Ž","\\v{Z}");
                put("Ž","{\\v Z}");
                put("ž","{\\v{z}}");
                put("ž","\\v{z}");
                put("ž","{\\v z}");
                put("ȩ","{\\c{e}}");
                put("ȩ","\\c{e}");
                put("Ǎ","{\\v{A}}");
                put("Ǎ","\\v{A}");
                put("Ǎ","{\\v A}");
                put("ǎ","{\\v{a}}");
                put("ǎ","\\v{a}");
                put("ǎ","{\\v a}");
            }
        };  
        
    /**
     * Escapes UNICODE string with the BibTex entities
     * @param s 
     * @return escaped String
     */
    public static String texString(String str)
    {
        if ( str==null || "".equals(str.trim()) ) return null;
        String key;
        String value;
        for( Map.Entry<String, String> entry: ENTITIES.entrySet() )
        {
            str = str.replace(entry.getKey(), entry.getValue());
        }
        return str;
    }
    
}


