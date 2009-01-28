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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.cone.util;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static String formatList(List<Pair> pairs)
    {
        
        StringWriter result = new StringWriter();
        
        result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        result.append("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" "
                + "xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n");
        if (pairs != null)
        {
            for (Pair pair : pairs)
            {
                String key = pair.getKey();
                String value = pair.getValue();
                
                result.append("\t<rdf:Description rdf:about=\"" + key.replace("\"", "\\\"") + "\">\n");
                result.append("\t\t<dc:title>" + xmlEscape(value) + "</dc:title>\n");
                result.append("\t</rdf:Description>\n");
            }
        }
        
        result.append("</rdf:RDF>\n");
        
        return result.toString();
    }
    
    private static String xmlEscape(String value)
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
    public static String formatMap(String id, Map<String, List<LocalizedString>> triples)
    {
        
        StringWriter result = new StringWriter();
        
        result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        result.append("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"");
        
        Map<String, String> namespaces = new HashMap<String, String>();
        int counter = 0;
        
        if (triples != null)
        {
            for (String predicate : triples.keySet())
            {
                int lastSlash = predicate.lastIndexOf("/");
                if (lastSlash >= 0)
                {
                    String namespace = predicate.substring(0, lastSlash + 1);
                    
                    if (!namespaces.containsKey(namespace))
                    {
                        counter++;
                        String prefix = "ns" + counter;
                        namespaces.put(namespace, prefix);
                        
                        result.append(" xmlns:" + prefix + "=\"" + namespace + "\"");
                    }
                }
            }
            
            result.append(">\n");
            result.append("\t<rdf:Description rdf:about=\"");
            result.append(id);
            result.append("\">\n");
            
            for (String predicate : triples.keySet())
            {
                int lastSlash = predicate.lastIndexOf("/");
                String namespace = null;
                String tagName = null;
                String prefix = null;
                if (lastSlash >= 0)
                {
                    namespace = predicate.substring(0, lastSlash + 1);
                    prefix = namespaces.get(namespace);
                    tagName = predicate.substring(lastSlash + 1);
                }
                else
                {
                    int lastColon = predicate.lastIndexOf(":");
                    tagName = predicate.substring(lastColon + 1);
                }
                
                List<LocalizedString> values = triples.get(predicate);
                
                for (LocalizedString value : values)
                {
                    result.append("\t\t<");
                    if (namespace != null)
                    {
                        result.append(prefix);
                        result.append(":");
                    }
                    result.append(tagName);
                    
                    if (value.getLanguage() != null && !"".equals(value.getLanguage()))
                    {
                        result.append(" xml:lang=\"");
                        result.append(value.getLanguage());
                        result.append("\"");
                    }
                    
                    result.append(">");
                    result.append(xmlEscape(value.getValue()));
                    result.append("</");
                    if (namespace != null)
                    {
                        result.append(prefix);
                        result.append(":");
                    }
                    result.append(tagName);
                    result.append(">\n");
                }
            }
        }
        result.append("\t</rdf:Description>\n");
        result.append("</rdf:RDF>\n");
        
        return result.toString();
    }
}
