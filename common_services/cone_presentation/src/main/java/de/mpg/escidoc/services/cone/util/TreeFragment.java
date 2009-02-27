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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A representation of a tree-like structure built of s-p-o triples.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class TreeFragment extends HashMap<String, List<LocalizedTripleObject>> implements LocalizedTripleObject
{
    private static final String REGEX_PREDICATE_REPLACE = ":/\\-\\.";
    
    private String subject;
    private String language;
    
    /**
     * Default constructor.
     */
    public TreeFragment()
    {
        
    }
    
    /**
     * Constructor with given subject.
     * 
     * @param subject The subject.
     */
    public TreeFragment(String subject)
    {
        this.subject = subject;
    }
    
    /**
     * Constructor with given subject and language.
     * 
     * @param subject The subject.
     * @param language The language.
     */
    public TreeFragment(String subject, String language)
    {
        this.subject = subject;
        this.language = language;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    /**
     * Display this object as RDF/XML.
     * 
     * @return The object as RDF
     */
    public String toRdf()
    {
        if (size() == 0)
        {
            return RdfHelper.xmlEscape(subject);
        }
        else
        {
            StringWriter result = new StringWriter();
            Map<String, String> namespaces = new HashMap<String, String>();
            int counter = 0;

            result.append("<rdf:Description");
            if (!subject.startsWith("genid:"))
            {
                result.append(" rdf:about=\"");
                result.append(subject);
                result.append("\"");
            }
            
            if (language != null && !"".equals(language))
            {
                result.append(" xml:lang=\"");
                result.append(language);
                result.append("\"");
            }
            
            for (String predicate : keySet())
            {
                int lastSlash = predicate.lastIndexOf("/");
                if (lastSlash >= 0)
                {
                    String namespace = predicate.substring(0, lastSlash);
                    if (!namespace.endsWith("#"))
                    {
                        namespace += "/";
                    }
                    
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
            
            for (String predicate : keySet())
            {
                int lastSlash = predicate.lastIndexOf("/");
                String namespace = null;
                String tagName = null;
                String prefix = null;
                if (lastSlash >= 0)
                {
                    namespace = predicate.substring(0, lastSlash);
                    if (!namespace.endsWith("#"))
                    {
                        namespace += "/";
                    }
                    prefix = namespaces.get(namespace);
                    tagName = predicate.substring(lastSlash + 1);
                }
                else
                {
                    int lastColon = predicate.lastIndexOf(":");
                    tagName = predicate.substring(lastColon + 1);
                }
                
                List<LocalizedTripleObject> values = get(predicate);
                
                for (LocalizedTripleObject value : values)
                {
                    result.append("<");
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
                    result.append(value.toRdf());
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
            result.append("</rdf:Description>\n");
            return result.toString();
        }
    }

    /**
     * Display this object as JSON object.
     * 
     * @return The object as JSON
     */
    public String toJson()
    {
        if (size() == 0)
        {
            return "\"" + subject.replace("\"", "\\\"") + "\"";
        }
        else
        {
            StringWriter writer = new StringWriter();
            writer.append("{\n");
            for (Iterator<String> iterator = keySet().iterator(); iterator.hasNext();)
            {
                String key = iterator.next();

                writer.append("\"");
                writer.append(key.replaceAll("[" + REGEX_PREDICATE_REPLACE + "]+", "_").replace("\"", "\\\""));
                writer.append("\" : ");
                if (get(key).size() == 1)
                {
                    writer.append(get(key).get(0).toJson());
                }
                else
                {
                    writer.append("[\n");
                    for (Iterator<LocalizedTripleObject> iterator2 = get(key).iterator(); iterator2.hasNext();)
                    {
                        LocalizedTripleObject object = (LocalizedTripleObject) iterator2.next();
                        writer.append(object.toJson());
                        if (iterator2.hasNext())
                        {
                            writer.append(",");
                        }
                        writer.append("\n");
                    }
                    writer.append("]");
                }
                if (iterator.hasNext())
                {
                    writer.append(",\n");
                }
            }
            writer.append("\n}\n");
            return writer.toString();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return subject;
    }
}
