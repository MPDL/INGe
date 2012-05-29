/*
*
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

package de.mpg.escidoc.services.cone.util;

import java.io.StringWriter;
import java.util.List;

import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * Static Helper class for RDF formatting.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class RdfHelper
{
    
    private RdfHelper()
    {
        
    }
    
    /**
     * Formats an List&lt;Pair&gt; into an RDF list.
     * 
     * @param pairs A list of key-value pairs
     * 
     * @return The RDF
     */
    public static String formatList(List<? extends Describable> pairs)
    {
        
        StringWriter result = new StringWriter();
        
        result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        result.append("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" "
                + "xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n");
        if (pairs != null)
        {
            
            for (Describable pair : pairs)
            {
                if (pair instanceof Pair)
                {
                    String key = ((Pair) pair).getKey();
                    try
                    {
                        result.append("\t<rdf:Description rdf:about=\"" + PropertyReader.getProperty("escidoc.cone.service.url") + key.replace("\"", "\\\"") + "\">\n");
                        if (((Pair) pair).getValue() instanceof LocalizedString)
                        {
                            if (((LocalizedString)((Pair) pair).getValue()).getLanguage() != null)
                            {
                                result.append("\t\t<dc:title xml:lang=\"" + ((LocalizedString)((Pair) pair).getValue()).getLanguage() + "\">" + xmlEscape(((LocalizedString)((Pair) pair).getValue()).getValue()) + "</dc:title>\n");
                            }
                            else
                            {
                                result.append("\t\t<dc:title>" + xmlEscape(((LocalizedString)((Pair) pair).getValue()).getValue()) + "</dc:title>\n");
                            }
                        }
                        else
                        {
                            result.append("\t\t<dc:title>" + xmlEscape(((Pair) pair).getValue().toString()) + "</dc:title>\n");
                        }
                        result.append("\t</rdf:Description>\n");
                    }
                    catch (Exception exception)
                    {
                        throw new RuntimeException(exception);
                    }
                }
                else if (pair instanceof TreeFragment)
                {
                    result.append(((TreeFragment) pair).toRdf());
                }
            }
        }
        
        result.append("</rdf:RDF>\n");
        
        return result.toString();
    }
    
    public static String xmlEscape(String value)
    {
        value = value.replace("&", "&amp;");
        value = value.replace("<", "&lt;");
        value = value.replace(">", "&gt;");

        return value;
    }

    /**
     * Formats an a Map of triples into RDF.
     * 
     * @param id The cone-id of the object
     * @param triples A map of s-p-o triples
     * 
     * @return The RDF
     */
    public static String formatMap(String id, TreeFragment triples)
    {
        if (triples != null)
        {
	        StringWriter result = new StringWriter();
	        
	        result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
	        result.append("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n");
	        
	        result.append(triples.toRdf());
	        
	        result.append("</rdf:RDF>\n");
	        
	        return result.toString();
        }
        else
        {
        	return "";
        }
    }
}
