/**
 * 
 */
package de.mpg.escidoc.tools.reindex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringWriter;

import org.apache.log4j.Logger;
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
	private static Logger logger = Logger.getLogger(IndexDocument.class);
	
	Document document;
	boolean inField = false;
	boolean storeField = false;
	boolean index = false;
	String fieldName = null;
	StringWriter content = null;
	String fulltextPath = null;
	String fulltextDir;
	
	public IndexDocument(Document document, String fulltextDir)
	{
		this.document = document;
		this.fulltextDir = fulltextDir;
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
			index = "UN_TOKENIZED".equals(attributes.getValue("index"));
			content = new StringWriter();
			fulltextPath = attributes.getValue("dsId");
			if (fulltextPath != null)
			{
				String realPath = fulltextDir + "/" + fulltextPath.replaceAll(".+/([^/]+)/content", "$1").replace(":", "_") + "+content+content.0.txt";
				logger.info("Reading fulltext from " + realPath);
				int readNum;
				try
				{
					FileReader reader = new FileReader(new File(realPath));
					char[] ftch = new char[2048];
					while ((readNum = reader.read(ftch)) != -1)
					{
						content.write(ftch, 0, readNum);
					}
					reader.close();
				}
				catch (FileNotFoundException e)
				{
					logger.error("File not found: " + realPath);
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
				}
			}
			inField = true;
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
			Field field = new Field(fieldName, content.toString(), 
					(storeField ? Field.Store.YES : Field.Store.NO), 
					(index ? Index.NOT_ANALYZED : Index.ANALYZED));
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
