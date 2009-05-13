
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
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.transformation.transformations.otherFormats.edoc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

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
import de.mpg.escidoc.services.common.util.creators.Author;
import de.mpg.escidoc.services.common.util.creators.AuthorDecoder;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.Transformation.TransformationModule;
import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

/**
 * @author kurt (initial creation)
 * @author $Author: tendres $ (last modification)
 * @version $Revision: 2419 $ $LastChangedDate: 2009-04-01 09:41:19 +0200 (Mi, 01 Apr 2009) $ 
 *
 */
@TransformationModule
public class EDocImport extends DefaultHandler implements Transformation
{

    private StringWriter newXml = new StringWriter();
    private boolean inCreatorstring = false;
    private StringWriter creatorString = null;
    
    private static final Format ESCIDOC_ITEM_LIST_FORMAT
        = new Format("eSciDoc-publication-item-list", "application/xml", "*");
    private static final Format ESCIDOC_ITEM_FORMAT = new Format("eSciDoc-publication-item", "application/xml", "*");
    private static final Format EDOC_FORMAT = new Format("eDoc", "application/xml", "*");
    
    private static final String XSLT_PATH = "transformations/otherFormats/xslt/edoc-to-escidoc.xslt";
    
    /**
     * {@inheritDoc}
     */
    public Format[] getSourceFormats()
    {
        return new Format[]{EDOC_FORMAT};
    }

    /**
     * {@inheritDoc}
     */
    public Format[] getSourceFormats(Format trg)
    {
        if (trg != null && (trg.matches(ESCIDOC_ITEM_FORMAT) || trg.matches(ESCIDOC_ITEM_LIST_FORMAT)))
        {
            return new Format[]{EDOC_FORMAT};
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
        if (src != null && src.matches(EDOC_FORMAT))
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
    public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service)
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
            InputStream stylesheet = ResourceUtil.getResourceAsStream(XSLT_PATH);
            Transformer transformer = factory.newTransformer(new StreamSource(stylesheet));
            OutputStream fwout = new FileOutputStream(new File("edoc_export_out.xml"), false);
            
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
            
            transformer.setParameter("content-model", PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication"));
            
            transformer.setOutputProperty(OutputKeys.ENCODING, trgFormat.getEncoding());
            transformer.transform(new StreamSource(new StringReader(newXml.toString())), new StreamResult(result));
            System.out.println("done!");
            
            System.out.println("Finished!");
            
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

    
}
