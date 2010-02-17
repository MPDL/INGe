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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.citationmanager.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.citationmanager.data.FontStyle;
import de.mpg.escidoc.services.citationmanager.data.FontStylesCollection;

/**
 * Function extensions for the citationmanager XSLTs 
 *
 * @author Vlad Makarenko (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */ 



public class XsltHelper 
{
	private static final Logger logger = Logger.getLogger(XsltHelper.class);

	//FontStyleCollection 
	public static FontStylesCollection fsc = null;
		
	public static final String I18N_TAG = "localized";
	
	/**
	 * Load Default FontStylesCollection only once 
	 * 
	 * @throws CitationStyleManagerException
	 */
	public static void loadFontStylesCollection() throws CitationStyleManagerException 
	{
		if ( fsc != null ) return;
		try 
		{
			fsc = FontStylesCollection.loadFromXml( 
					ResourceUtil.getPathToCitationStyles()
					+ "font-styles.xml"
			);
			
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			throw new CitationStyleManagerException( "Cannot loadFontStylesCollection: ", e);
		}
	}

	
	/**
	 * Converts snippet &lt;span&gt; tags to the appropriate JasperReport Styled Text.
	 * Note: 	If at least one &lt;span&gt; css class will not match FontStyle css, 
	 * 			the snippet will be returned without any changes.
	 * @param snippet 
	 * @return converted snippet
	 * @throws CitationStyleManagerException
	 */
	public static String convertSnippetToJasperStyledText(String snippet) throws CitationStyleManagerException
    {  
		
		snippet = removeI18N (snippet);
		
		loadFontStylesCollection();
		
        if ( ! Utils.checkVal(snippet) || fsc == null ) return snippet;
     
//        logger.info("passed str:" + str);
        
        FontStyle fs;
        
        StringBuffer sb = new StringBuffer();
        String regexp = "(<span\\s+class=\"(\\w+)\".*?>)";
        Matcher m = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(snippet);
        while (m.find ())
        {
        	fs = fsc.getFontStyleByCssClass(m.group(2));
        	//logger.info("fs:" + fs);
        	
        	//Rigorous: if at list once no css class has been found return str as it is 
        	if ( fs == null ) 
        	{
        		return snippet;
        	}
        	else
        	{
            	m.appendReplacement(sb, "<style" + fs.getStyleAttributes() + ">");
        	}
        }
        snippet = m.appendTail(sb).toString();
        
        snippet = Utils.replaceAllTotal(snippet, "</span>", "</style>");
        
//        logger.info("processed str:" + str);
        
        return snippet;
    }


	public static String removeI18N( String snippet ) 
	{
		return Utils.replaceAllTotal(
				snippet, 
				"<" + I18N_TAG + "\\s+class=\"\\w+\".*?>(.*?)</" + I18N_TAG + ">", 
				"$1"
		);
	}
	
}
