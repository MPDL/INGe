/**
 * 
 */
package de.mpg.escidoc.tools.reindex;

import java.io.StringWriter;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author franke
 *
 */
public class IndexDocument extends DefaultHandler
{

	Document document;
	boolean inField = false;
	boolean storeField = false;
	String fieldName = null;
	StringWriter content = null;
	
	public IndexDocument(Document document)
	{
		this.document = document;
	}
	
	@Override
	public void startDocument() throws SAXException
	{

	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException
	{
		if ("IndexDocument".equals(qName))
		{
			String pid = attributes.getValue("PID");
			Field pidField = new Field("PID", pid, Field.Store.NO, Index.ANALYZED);
	        document.add(pidField);
		}
		else if ("IndexField".equals(qName))
		{
			fieldName = attributes.getValue("IFname");
			storeField = "YES".equals(attributes.getValue("store"));
			content = new StringWriter();
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException
	{
		if (inField)
		{
			content.write(ch, start, length);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException
	{
		 if ("IndexField".equals(qName))
		{
			Field field = new Field(fieldName, content.toString(), (storeField ? Field.Store.YES : Field.Store.NO), Index.ANALYZED);
	        document.add(field);
	        content = null;
	        inField = false;
	        storeField = false;
		}
	}

	@Override
	public void endDocument() throws SAXException
	{

	}

	
}
