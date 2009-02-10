package de.mpg.escidoc.services.edoc;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
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

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.common.util.creators.Author;
import de.mpg.escidoc.services.common.util.creators.AuthorDecoder;

/**
 * 
 */

/**
 * @author kurt
 *
 */
public class EDocImport extends DefaultHandler
{

	StringWriter newXml = new StringWriter();
	boolean inCreatorstring = false;
	StringWriter creatorString = null;
	
	/**
	 * @param args
	 */
	
	
	public static void main(String[] args) throws Exception
	{
		
		String pathXml = args[0];
		String pathXslt = "edoc-to-escidoc.xslt";
		
		EDocImport eDocImport = new EDocImport(pathXml, pathXslt);
		
	}
	
	public EDocImport(String pathXml, String pathXslt) throws Exception
	{
        System.out.print("Started SAX parser transformation...");
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        parser.parse(new InputSource(new FileReader(ResourceUtil.getResourceAsFile(pathXml))), this);
        System.out.println("done!");

        File test = new File("test.xml");
        FileWriter writer = new FileWriter(test);
        writer.write(newXml.toString());
        writer.close();
        
	    System.out.print("Started xslt transformation...");
		XSLTTransform transform = new XSLTTransform();
        OutputStream fwout = new FileOutputStream(new File("edoc_export_out.xml"), false);
		transform.transform(newXml.toString(), ResourceUtil.getResourceAsFile(pathXslt), fwout);
		System.out.println("done!");
		
		System.out.println("Finished!");
	}

	private String getResult() {
		
		return newXml.toString();
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
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
			throws SAXException {
		if ("issuecontributorfn".equals(name) ||
		        "proceedingscontributorfn".equals(name) ||
		        "seriescontributorfn".equals(name) ||
		        "bookcontributorfn".equals(name) ||
		        "bookcreatorfn".equals(name))
		{
			try
			{
				AuthorDecoder authorDecoder = new AuthorDecoder(creatorString.toString());
				List<Author> authors = authorDecoder.getBestAuthorList();
				if (authors.size() > 0)
				{
				    newXml.append("<creators>\n");
    				for(int i=0; i < authors.size(); i++){
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
    					newXml.append(escape(authors.get(i).getInitial()));
    					newXml.append("</creatorini>\n");
    					newXml.append("<creatornfamily>");
    					newXml.append(escape(authors.get(i).getSurname()));
    					newXml.append("</creatornfamily>\n");
    					newXml.append("<creatorngiven>");
    					newXml.append(escape(authors.get(i).getGivenName()));
    					newXml.append("</creatorngiven>\n");
    					newXml.append("</creator>\n");
    				}
    				 newXml.append("</creators>\n");
				}

			}
			catch (Exception e) {
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
			Attributes attributes) throws SAXException {
	    if ("issuecontributorfn".equals(name) ||
                "proceedingscontributorfn".equals(name) ||
                "seriescontributorfn".equals(name) ||
                "bookcontributorfn".equals(name) ||
                "bookcreatorfn".equals(name))
		{
			inCreatorstring = true;
			creatorString = new StringWriter();
			
		}
		else
		{
			
			newXml.append("<");
			newXml.append(name);
			for (int i = 0; i < attributes.getLength(); i++) {
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
		if(input != null){
			input = input.replace("&", "&amp;");
			input = input.replace("<", "&lt;");
			input = input.replace("\"", "&quot;");
		}
		else
		{
		    return "";
		}
		return input.trim();
	}

	
}
