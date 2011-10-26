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
* Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.transformation.transformations.standardFormats;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.TransformerFactoryImpl;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.transformation.Configurable;
import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.transformations.LocalUriResolver;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

/**
 * Handles all transformations for standard metadata records.
 * @author Friederike Kleinfercher (initial creation)
 *
 */
public class StandardTransformation 
{
    private final Logger logger = Logger.getLogger(StandardTransformation.class);
    
    private final String METADATA_XSLT_LOCATION ="transformations/standardFormats/xslt";
    private static final Format ESCIDOC_ITEM_LIST_FORMAT = new Format("eSciDoc-publication-item-list", "application/xml", "*");
    private static final Format ESCIDOC_ITEM_FORMAT = new Format("eSciDoc-publication-item", "application/xml", "*");
    private static final Format ESCIDOC_COMPONENT_FORMAT = new Format("eSciDoc-publication-component", "application/xml", "*");
   
    private static Properties properties;
    
 
    
    /**
     * Public constructor.
     */
    public StandardTransformation()
    {}
    

    
    /**
     * Metadata transformation method.
     * @param formatFrom
     * @param formatTo
     * @param itemXML
     * @return transformed metadata as String
     */
    public String xsltTransform(String formatFrom, String formatTo, String itemXML, Map<String, String> configuration) throws RuntimeException
    {
        String xsltUri = formatFrom.toLowerCase() + "2" + formatTo.toLowerCase() + ".xsl";
        
        TransformerFactory factory = new TransformerFactoryImpl();
        factory.setURIResolver(new LocalUriResolver(this.METADATA_XSLT_LOCATION));
        StringWriter writer = new StringWriter();
        
        try
        {
            ClassLoader cl = this.getClass().getClassLoader();
            InputStream in = cl.getResourceAsStream(this.METADATA_XSLT_LOCATION + "/" + xsltUri);
            Transformer transformer = factory.newTransformer(new StreamSource(in));
            
            transformer.setParameter("content-model", PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication"));

            //For zfn transformation
            if (configuration != null)
            {
                transformer.setParameter("JournalConeID", configuration.get("JournalConeID"));
                transformer.setParameter("License", configuration.get("License"));
                transformer.setParameter("zfnId", configuration.get("id"));
            }
    
            if(formatTo.endsWith("list"))
            {
                transformer.setParameter("is-item-list", Boolean.TRUE);
            }
            else
            {
                transformer.setParameter("is-item-list", Boolean.FALSE);
            }
            StringReader xmlSource = new StringReader(itemXML);
            transformer.transform(new StreamSource(xmlSource), new StreamResult(writer));
        }
        catch (Exception e)
        {
            throw new RuntimeException("An error occurred during a standard metadata transformation.", e);
        }
        
        return writer.toString();
    }
    
    public boolean checkXsltTransformation(String formatFrom, String formatTo)
    {
        String xsltUri = formatFrom.toLowerCase().trim() + "2" + formatTo.toLowerCase().trim() + ".xsl";
        boolean check = false;
        
        try {
            
            File transformFile = ResourceUtil.getResourceAsFile(this.METADATA_XSLT_LOCATION +"/"+xsltUri);
            check = true;
            
        }
        catch (FileNotFoundException e){this.logger.warn("No transformation file from format: " + formatFrom + " to format: " + formatTo);}

        return check;
    }
    
    /**
     * Gets the value of a property for the given key from the escidoc property file.
     *
     * @param key The key of the property.
     * @return The value of the property.
     * @throws IOException
     * @throws URISyntaxException 
     */
    public String getProperty(String key) throws IOException, URISyntaxException
    {
        String propertiesFile = null;
        Properties solProperties = new Properties();

        InputStream in = getInputStream("transformation.properties");
        solProperties.load(in);
        
        InputStream instream = getInputStream("transformation.properties");
        properties = new Properties();
        properties.load(instream);
        properties.putAll(solProperties);

        return properties.getProperty(key);
    }
    
    /**
     * Retrieves the Inputstream of the given file path.
     * First the resource is searched in the file system, if this fails it is searched using the classpath.
     *
     * @param filepath The path of the file to open.
     * @return The inputstream of the given file path.
     * @throws IOException If the file could not be found neither in the file system nor in the classpath.
     */
    private static InputStream getInputStream(String filepath) throws IOException
    {
        InputStream instream = null;
        // First try to search in file system
        try
        {
            instream = new FileInputStream(filepath);
        }
        catch (Exception e)
        {
            // try to get resource from classpath
            URL url = StandardTransformation.class.getClassLoader().getResource(filepath);
            if (url != null)
            {
                instream = url.openStream();
            }
        }
        if (instream == null)
        {
            throw new FileNotFoundException(filepath);
        }
        return instream;
    }

 
}
