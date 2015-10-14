/**
 * 
 */
package de.mpg.escidoc.tools.reindex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.escidoc.sb.gsearch.xslt.SortFieldHelper;

/**
 * @author franke
 *
 */
public class IndexDocument extends DefaultHandler
{
	private static final String SORT_LATEST_RELEASE_PREFIX = "sort";

	private static final String SORT_LATEST_VERSION_PREFIX = "/sort";

	private static Logger logger = Logger.getLogger(IndexDocument.class);
	
	Document document;
	boolean inField = false;
	Field.Store storeField = Field.Store.YES;
	Field.Index index = Field.Index.ANALYZED;
	Field.TermVector termVector = Field.TermVector.NO;
	
	String fieldName = null;
	StringWriter content = null;
	String fulltextPath = null;
	String fulltextDir;
	
	private List<String> storedSortFieldNames = new ArrayList<String>();
	
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
			
			if ("/title".equals(fieldName))
			{
				int i = 1;
			}
			
			if ("YES".equals(attributes.getValue("termVector")))
			{
				termVector = Field.TermVector.YES;
			}
			else if ("NO".equals(attributes.getValue("termVector")))
			{
				termVector = Field.TermVector.NO;
			}
			
			if ("YES".equals(attributes.getValue("store")))
			{
				storeField = Field.Store.YES;
			}
			else if ("NO".equals(attributes.getValue("store")))
			{
				storeField = Field.Store.NO;
			}

			if("UN_TOKENIZED".equals(attributes.getValue("index")))
			{
				index = Field.Index.NOT_ANALYZED;
			} 
			else if("NO".equals(attributes.getValue("index")))
			{
				index = Field.Index.NO;
			}
			else
			{
				index = Field.Index.ANALYZED;
			}
			
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
			if ("/title".equals(fieldName))
			{
			logger.debug("Writing to content <" + new String(ch).substring(start, start + length) + "> start <" + start + "> length <" + length + ">");
			}
					
			content.write(ch, start, length);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException
	{
		if (content != null && content.toString().trim().length() == 0)
		{
			return;
		}
			
		if ("IndexField".equals(qName))
		{
			Field field = new Field(fieldName, content.toString().trim(), storeField, index, termVector);
			
			if (fieldName.startsWith(SORT_LATEST_VERSION_PREFIX) || fieldName.startsWith(SORT_LATEST_RELEASE_PREFIX))
			{
				if (storedSortFieldNames.contains(fieldName))
				{
					logger.debug("Already stored <" + fieldName +">");
					return;
				}
				else
				{
					storedSortFieldNames.add(fieldName);
				}
			}
			
			logger.debug("fieldName <" + fieldName + "> " 
					+ "content <" + content.toString().trim() + "> "
					+ "storeField <" + storeField.toString() + "> " 
					+ "index <" + index.toString() + "> "
					+ "termVector <" + termVector.toString() + "> ");
					
	        document.add(field);
	        
	        content = null;     
	        storeField = Field.Store.YES;
	        index = Field.Index.ANALYZED;
	        termVector = Field.TermVector.NO;
	        
	        inField = false;
		}
	}

	@Override
	public void endDocument() throws SAXException
	{
		storedSortFieldNames.clear();
	}
}
