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

package de.mpg.escidoc.services.cone.formatter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.cone.ModelList.Model;
import de.mpg.escidoc.services.cone.util.Describable;
import de.mpg.escidoc.services.cone.util.RdfHelper;
import de.mpg.escidoc.services.cone.util.TreeFragment;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * Servlet to answer calls from the JQuery Javascript API.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class HtmlFormatter extends Formatter
{

    private static final Logger logger = Logger.getLogger(HtmlFormatter.class);
    private static final String ERROR_TRANSFORMING_RESULT = "Error transforming result";
    private static final String DEFAULT_ENCODING = "UTF-8";
    
    @Override
    public String getContentType()
    {
        return "text/html;charset=" + DEFAULT_ENCODING;
    }

    /**
     * Send explain output to the client.
     * 
     * @param response
     * 
     * @throws FileNotFoundException
     * @throws TransformerFactoryConfigurationError
     * @throws IOException
     */
    public void explain(HttpServletResponse response) throws FileNotFoundException,
            TransformerFactoryConfigurationError, IOException, URISyntaxException
    {
        response.setContentType("text/xml");
        
        InputStream source = ResourceUtil.getResourceAsStream(PropertyReader.getProperty("escidoc.cone.modelsxml.path"));
        InputStream template = ResourceUtil.getResourceAsStream("explain/html_explain.xsl");
        
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
     * Formats an List&lt;Pair&gt; into an HTML list.
     * 
     * @param pairs A list of key-value pairs
     * @return A String formatted as HTML
     */
    public String formatQuery(List<? extends Describable> pairs) throws IOException
    {
        
        String result = RdfHelper.formatList(pairs);
        StringWriter writer = new StringWriter();
        try
        {
            Transformer transformer = TransformerFactory
                    .newInstance()
                    .newTransformer(
                            new StreamSource(ResourceUtil.getResourceAsStream("xslt/html/resultlist-html.xsl")));
            transformer.setOutputProperty(OutputKeys.ENCODING, DEFAULT_ENCODING);
            transformer.transform(new StreamSource(new StringReader(result)), new StreamResult(writer));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return writer.toString();
    }

    /**
     * Formats an Map of triples into RDF.
     * 
     * @param triples The map of triples
     * 
     * @return A String formatted in HTML.
     * 
     * @throws IOException Any i/o exception
     */
    public String formatDetails(String id, Model model, TreeFragment triples, String lang)
        throws IOException
    {
        
        String result = RdfHelper.formatMap(id, triples);
        StringWriter writer = new StringWriter();
        try
        {
            File xsltFile = null;
            try
            {
                xsltFile = ResourceUtil.getResourceAsFile("xslt/html/" + model.getName() + "-html.xsl");
            }
            catch (FileNotFoundException fnfe)
            {
                logger.debug("No HTML template for '" + model.getName() + "' found, using generic template.");
                xsltFile = ResourceUtil.getResourceAsFile("xslt/html/generic-html.xsl");
            }
            
            TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl();
            
            Transformer transformer = factory
                    .newTransformer(
                            new StreamSource(xsltFile));
            transformer.setOutputProperty(OutputKeys.ENCODING, DEFAULT_ENCODING);
            String exportFormat="APA";
            if("ja".equals(lang))
            {
                exportFormat="APA(CJK)";
            }
            
            for(Object key : PropertyReader.getProperties().keySet()) 
            {
            	transformer.setParameter(key.toString(), PropertyReader.getProperty(key.toString()));
            }
            
            transformer.setParameter("citation-link", PropertyReader.getProperty("escidoc.pubman.instance.url") + "/search/SearchAndExport?cqlQuery=escidoc.any-identifier=\"" + PropertyReader.getProperty("escidoc.cone.service.url") + id + "\"&exportFormat=" + exportFormat + "&outputFormat=snippet&language=all&sortKeys=escidoc.any-dates&sortOrder=descending");
            transformer.setParameter("item-link", PropertyReader.getProperty("escidoc.pubman.instance.url") + PropertyReader.getProperty("escidoc.pubman.instance.context.path") + PropertyReader.getProperty("escidoc.pubman.item.pattern"));
            transformer.setParameter("lang", lang);
            transformer.transform(new StreamSource(new StringReader(result)), new StreamResult(writer));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return writer.toString();
    }
    
}
