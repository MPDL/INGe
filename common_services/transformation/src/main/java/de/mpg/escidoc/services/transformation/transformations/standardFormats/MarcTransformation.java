
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

package de.mpg.escidoc.services.transformation.transformations.standardFormats;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.transformation.Configurable;
import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.Transformation.TransformationModule;
import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.transformations.LocalUriResolver;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

/**
 * @author mfranke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 4133 $ $LastChangedDate: 2011-09-22 11:19:17 +0200 (Do, 22 Sep 2011) $
 * 
 * Transformation implementation for MARC21 and MARCXML transformations.
 *
 */
@TransformationModule
public class MarcTransformation implements Transformation, Configurable
{
    private static final Logger logger = Logger.getLogger(MarcTransformation.class);
    
    private static final Format ESCIDOC_ITEM_LIST_FORMAT
        = new Format("eSciDoc-publication-item-list", "application/xml", "*");
    private static final Format ESCIDOC_ITEM_FORMAT = new Format("eSciDoc-publication-item", "application/xml", "*");
    private static final Format MARC21_FORMAT = new Format("marc21", "application/marc", "*");
    private static final Format MARCXML_FORMAT_UTF8 = new Format("marcxml", "application/marc+xml", "UTF-8");
    private static final Format MARCXML_FORMAT_MARC8 = new Format("marcxml", "application/marc+xml", "MARC-8");

    private Map<String, List<String>> properties = null;
    private Map<String, String> configuration = null;

    /**
     * {@inheritDoc}
     */
    public Format[] getSourceFormats()
    {
        return new Format[]{ESCIDOC_ITEM_LIST_FORMAT, ESCIDOC_ITEM_FORMAT, MARC21_FORMAT, MARCXML_FORMAT_UTF8, MARCXML_FORMAT_MARC8};
        //return new Format[]{};
    }

    /**
     * {@inheritDoc}
     */
    public Format[] getSourceFormats(Format trg)
    {
        if (trg != null && (trg.matches(ESCIDOC_ITEM_FORMAT) || trg.matches(ESCIDOC_ITEM_LIST_FORMAT)))
        {
            return new Format[]{MARC21_FORMAT, MARCXML_FORMAT_UTF8, MARCXML_FORMAT_MARC8};
        }
        else if (trg != null && (trg.matches(MARC21_FORMAT) || trg.matches(MARCXML_FORMAT_UTF8) || trg.matches(MARCXML_FORMAT_MARC8)))
        {
            return new Format[]{ESCIDOC_ITEM_FORMAT, ESCIDOC_ITEM_LIST_FORMAT};
        }
        else
        {
            return new Format[]{};
        }
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    public String getSourceFormatsAsXml()
    {
        throw new RuntimeException("Not implemented");
    }

    /**
     * {@inheritDoc}
     */
    public Format[] getTargetFormats(Format src) throws RuntimeException
    {
        if (src != null && (src.matches(MARC21_FORMAT) || src.matches(MARCXML_FORMAT_UTF8) || src.matches(MARCXML_FORMAT_MARC8)))
        {
            return new Format[]{ESCIDOC_ITEM_FORMAT, ESCIDOC_ITEM_LIST_FORMAT};
        }
        else if (src != null && (src.matches(ESCIDOC_ITEM_FORMAT) || src.matches(ESCIDOC_ITEM_LIST_FORMAT)))
        {
            return new Format[]{MARC21_FORMAT, MARCXML_FORMAT_UTF8, MARCXML_FORMAT_MARC8};
        }
        else
        {
            return new Format[]{};
        }
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    public String getTargetFormatsAsXml(String srcFormatName, String srcType, String srcEncoding)
    {
        throw new RuntimeException("Not implemented");
    }

    /**
     * {@inheritDoc}
     */
    public byte[] transform(byte[] src, String srcFormatName, String srcType, String srcEncoding, String trgFormatName,
            String trgType, String trgEncoding, String service) throws TransformationNotSupportedException,
            RuntimeException
    {
        Format srcFormat = new Format(srcFormatName, srcType, srcEncoding);
        Format trgFormat = new Format(trgFormatName, trgType, trgEncoding);
        return transform(src, srcFormat, trgFormat, service);
    }

    /**
     * {@inheritDoc}
     */
    public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service, Map<String, String> configuration)
        throws TransformationNotSupportedException
    {
        StringWriter result = new StringWriter();
        
        try
        {
            logger.info("Start of XSL transformation...");
            TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl();
            
            String xslPath = PropertyReader.getProperty("escidoc.transformation.marc.stylesheet.filename");
            if (xslPath != null)
            {
                xslPath = xslPath.replace('\\', '/');
            }
            String xslDir;
            if (xslPath.contains("/"))
            {
                xslDir = xslPath.substring(0, xslPath.lastIndexOf("/"));
            }
            else
            {
                xslDir = ".";
            }
                
            factory.setURIResolver(new LocalUriResolver(xslDir));
            InputStream stylesheet = ResourceUtil.getResourceAsStream(xslPath);
            Transformer transformer = factory.newTransformer(new StreamSource(stylesheet));
            
            transformer.setParameter("source-format", srcFormat.getName());
            transformer.setParameter("target-format", trgFormat.getName());
            
            if (configuration != null)
            {
                for (String key : configuration.keySet())
                {
                    logger.debug("ADD PARAM " + key + " WITH VALUE " + configuration.get(key));
                	transformer.setParameter(key, configuration.get(key));
                }
            }
            
            transformer.setParameter("content-model", PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication"));
            transformer.setParameter("external-ou", PropertyReader.getProperty("escidoc.pubman.external.organisation.id"));
            transformer.setParameter("root-ou", PropertyReader.getProperty("escidoc.pubman.root.organisation.id"));
            
            transformer.setOutputProperty(OutputKeys.ENCODING, trgFormat.getEncoding());
            transformer.transform(new StreamSource(new StringReader(new String(src, srcFormat.getEncoding()))), new StreamResult(result));
            
            logger.info("...end of XSL transformation");
            
            return result.toString().getBytes(trgFormat.getEncoding());
            
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error parsing marc xml", e);
        }
    }
    
    public String escape(String input)
    {
        if (input != null)
        {
            input = input.replace("&", "&amp;");
            input = input.replace("<", "&lt;");
            input = input.replace("\"", "&quot;");
        }
        else
        {
            return "";
        }
        return input;
    }

    public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service) throws TransformationNotSupportedException, RuntimeException
    {
        return transform(src, srcFormat, trgFormat, service, null);
    }

    public Map<String, String> getConfiguration(Format srcFormat, Format trgFormat) throws Exception
    {
        if (configuration == null)
        {
            init();
        }

        return configuration;
    }

    private void init() throws IOException, FileNotFoundException, URISyntaxException
    {
        configuration = new LinkedHashMap<String, String>();
        properties = new HashMap<String, List<String>>();
        Properties props = new Properties();
        props.load(ResourceUtil.getResourceAsStream(PropertyReader.getProperty("escidoc.transformation.marc.configuration.filename")));
        for (Object key : props.keySet())
        {
            if (!"configuration".equals(key.toString()))
            {
                String[] values = props.getProperty(key.toString()).split(",");
                properties.put(key.toString(), Arrays.asList(values));
            }
            else
            {
                String[] confValues = props.getProperty("configuration").split(",");
                for (String field : confValues)
                {
                    String[] fieldArr = field.split("=", 2);
                    configuration.put(fieldArr[0], fieldArr[1] == null ? "" : fieldArr[1]);
                }
            }
        }
    }

    public List<String> getConfigurationValues(Format srcFormat, Format trgFormat, String key) throws Exception
    {
        if (properties == null)
        {
            init();
        }

        return properties.get(key);
    }

    
}
