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

package de.mpg.escidoc.services.cone.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.cone.util.Pair;

/**
 * Servlet to answer calls from PubMan for options generation.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class OptionsConeServlet extends ConeServlet
{

    private static final Logger logger = Logger.getLogger(OptionsConeServlet.class);
    private static final String ERROR_TRANSFORMING_RESULT = "Error transforming result";
    private static final String REGEX_PREDICATE_REPLACE = ":/\\-\\.";
    private static final String DEFAULT_ENCODING = "UTF-8";
    
    /**
     * Send explain output to client.
     * 
     * @param response
     * 
     * @throws FileNotFoundException
     * @throws TransformerFactoryConfigurationError
     * @throws IOException
     */
    protected void explain(HttpServletResponse response) throws FileNotFoundException,
            TransformerFactoryConfigurationError, IOException
    {
        response.setContentType("text/xml");
        
        InputStream source = ResourceUtil.getResourceAsStream("explain/models.xml");
        InputStream template = ResourceUtil.getResourceAsStream("explain/options_explain.xsl");
        
        try
        {
            Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(template));
            transformer.setOutputProperty(OutputKeys.ENCODING, DEFAULT_ENCODING);
            transformer.transform(new StreamSource(source), new StreamResult(response.getWriter()));
        }
        catch (Exception e)
        {
            logger.error(ERROR_TRANSFORMING_RESULT, e);
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Formats an Map&lt;String, String> into a JQuery readable list.
     * 
     * @param result The RDF.
     * @return A String formatted  in a JQuery readable format.
     */
    protected String formatQuery(List<Pair> pairs) throws IOException
    {
        
        StringWriter result = new StringWriter();
        
        if (pairs != null)
        {
            for (Pair pair : pairs)
            {
                String key = pair.getKey();
                String value = pair.getValue();
                result.append(key.substring(key.lastIndexOf(":") + 1));
                result.append("|");
                result.append(value);
                result.append("\n");
            }
        }
        
        return result.toString();
    }

    /**
     * Formats an Map&lt;String, String> into a JQuery readable list.
     * 
     * @param result The RDF.
     * @return A String formatted  in a JQuery readable format.
     */
    protected String formatDetails(Map<String, List<String>> triples) throws IOException
    {
        
        StringWriter result = new StringWriter();
        
        result.append("{\n");
        for (Iterator<String> iterator = triples.keySet().iterator(); iterator.hasNext();)
        {
            String predicate = (String) iterator.next();
            List<String> objects = triples.get(predicate);
            
            result.append("\"");
            result.append(predicate.replaceAll("[" + REGEX_PREDICATE_REPLACE + "]+", "_").replace("'", "\\'"));
            result.append("\" : \"");
            if (objects.size() == 1)
            {
                result.append(objects.get(0).replace("'", "\\'"));
            }
            else
            {
                result.append("{\n");
                for (Iterator<String> iterator2 = objects.iterator(); iterator2.hasNext();)
                {
                    String object = (String) iterator2.next();
                    result.append("\"");
                    result.append(object.replace("'", "\\'"));
                    result.append("\"");
                    if (iterator2.hasNext())
                    {
                        result.append(",");
                    }
                    result.append("\n");
                }
                result.append("}");
            }
            result.append("\"");
            if (iterator.hasNext())
            {
                result.append(",");
            }
            result.append("\n");
        }
        result.append("}");
        return result.toString();
    }
    
}
