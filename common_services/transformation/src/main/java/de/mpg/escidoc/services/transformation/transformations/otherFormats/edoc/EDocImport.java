
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

package de.mpg.escidoc.services.transformation.transformations.otherFormats.edoc;

import java.io.ByteArrayInputStream;
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

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.transformation.Configurable;
import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.Transformation.TransformationModule;
import de.mpg.escidoc.services.transformation.Util;
import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.transformations.LocalUriResolver;
import de.mpg.escidoc.services.transformation.util.creators.Author;
import de.mpg.escidoc.services.transformation.util.creators.AuthorDecoder;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

/**
 * @author kurt (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$ 
 *
 */
@TransformationModule
public class EDocImport extends DefaultHandler implements Transformation, Configurable
{

    private StringWriter newXml = new StringWriter();
    private boolean inCreatorstring = false;
    private StringWriter creatorString = null;
    
    private static final Format ESCIDOC_ITEM_LIST_FORMAT
        = new Format("eSciDoc-publication-item-list", "application/xml", "*");
    private static final Format ESCIDOC_ITEM_FORMAT = new Format("eSciDoc-publication-item", "application/xml", "*");
    private static final Format EDOC_FORMAT = new Format("eDoc", "application/xml", "*");
    private static final Format EDOC_FORMAT_AEI = new Format("eDoc-AEI", "application/xml", "*");

    private Map<String, List<String>> properties = null;
    private Map<String, String> configuration = null;

    /**
     * {@inheritDoc}
     */
    public Format[] getSourceFormats()
    {
        return new Format[]{EDOC_FORMAT, EDOC_FORMAT_AEI};
    }

    /**
     * {@inheritDoc}
     */
    public Format[] getSourceFormats(Format trg)
    {
        if (trg != null && (trg.matches(ESCIDOC_ITEM_FORMAT) || trg.matches(ESCIDOC_ITEM_LIST_FORMAT)))
        {
            return new Format[]{EDOC_FORMAT, EDOC_FORMAT_AEI};
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
        if (src != null && (src.matches(EDOC_FORMAT) || src.matches(EDOC_FORMAT_AEI)))
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
            System.out.print("Started SAX parser transformation...");
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(new InputSource(new ByteArrayInputStream(src)), this);
            System.out.println("done!");
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error preparsing edoc xml", e);
        }

        try
        {
            System.out.print("Started xslt transformation...");
            TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl();
            
            String xslPath = PropertyReader.getProperty("escidoc.transformation.edoc.stylesheet.filename");
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
            
            if (trgFormat.matches(ESCIDOC_ITEM_LIST_FORMAT))
            {
                transformer.setParameter("is-item-list", Boolean.TRUE);
            }
            else if (trgFormat.matches(ESCIDOC_ITEM_FORMAT))
            {
                transformer.setParameter("is-item-list", Boolean.FALSE);
            }
            else
            {
                throw new TransformationNotSupportedException("The requested target format (" + trgFormat.toString() + ") is not supported");
            }
            
            if (configuration != null)
            {
                for (String key : configuration.keySet())
                {
                    System.out.println("ADD PARAM " + key + " WITH VALUE " + configuration.get(key));
                	transformer.setParameter(key, configuration.get(key));
                }
            }
            
            transformer.setParameter("content-model", PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication"));
            transformer.setParameter("external-ou", PropertyReader.getProperty("escidoc.pubman.external.organisation.id"));
            transformer.setParameter("root-ou", PropertyReader.getProperty("escidoc.pubman.root.organisation.id"));
            transformer.setParameter("source-name", srcFormat.getName());
            
            transformer.setOutputProperty(OutputKeys.ENCODING, trgFormat.getEncoding());
            transformer.transform(new StreamSource(new StringReader(newXml.toString())), new StreamResult(result));
            
            return result.toString().getBytes(trgFormat.getEncoding());
            
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error parsing edoc xml", e);
        }
    }

    private String getResult()
    {
        
        return newXml.toString();
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException
    {
        String string = new String(ch, start, length);
        if (inCreatorstring)
        {
            creatorString.append(string);
        }
        else
        {
            newXml.append(escape(string));
        }
    }

    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException
    {
        if ("issuecontributorfn".equals(name)
                || "proceedingscontributorfn".equals(name)
                || "seriescontributorfn".equals(name)
                || "bookcontributorfn".equals(name)
                || "bookcreatorfn".equals(name))
        {
            try
            {
                //Added, so the original XML-Elements still exist after conversion
                newXml.append("<" + name + ">\n" + creatorString.toString() + "\n</" + name + ">\n");
                
                AuthorDecoder authorDecoder = new AuthorDecoder(creatorString.toString());
                List<Author> authors = authorDecoder.getBestAuthorList();
                if (authors.size() > 0)
                {
                    newXml.append("<creators>\n");
                    for (int i = 0; i < authors.size(); i++)
                    {
                        newXml.append("<creator type=\"" + name + "\" role=\"");
                        if ("bookcreatorfn".equals(name))
                        {
                            newXml.append("author");
                        }
                        else
                        {
                            newXml.append("editor");
                        }
                        newXml.append("\" creatorType=\"individual\">\n");
                        newXml.append("<creatorini>");    
                        newXml.append(escape(authors.get(i).getInitial()).trim());
                        newXml.append("</creatorini>\n");
                        newXml.append("<creatornfamily>");
                        newXml.append(escape(authors.get(i).getSurname()).trim());
                        newXml.append("</creatornfamily>\n");
                        newXml.append("<creatorngiven>");
                        newXml.append(escape(authors.get(i).getGivenName()).trim());
                        newXml.append("</creatorngiven>\n");
                        newXml.append("</creator>\n");
                    }
                    newXml.append("</creators>\n");
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
                throw new SAXException(e);
            }
            creatorString = null;
            inCreatorstring = false;
        }
        else
        {
            newXml.append("</");
            newXml.append(name);
            newXml.append(">");
        }
    }

    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException
    {
        if ("issuecontributorfn".equals(name)
                || "proceedingscontributorfn".equals(name)
                || "seriescontributorfn".equals(name)
                || "bookcontributorfn".equals(name)
                || "bookcreatorfn".equals(name))
        {
            inCreatorstring = true;
            creatorString = new StringWriter();
            
        }
        else
        {
            
            newXml.append("<");
            newXml.append(name);
            for (int i = 0; i < attributes.getLength(); i++)
            {
                newXml.append(" ");
                newXml.append(attributes.getQName(i));
                newXml.append("=\"");
                newXml.append(escape(attributes.getValue(i)));
                newXml.append("\"");
            }
            newXml.append(">");

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
        props.load(ResourceUtil.getResourceAsStream(PropertyReader.getProperty("escidoc.transformation.edoc.configuration.filename")));
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
