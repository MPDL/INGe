package de.mpg.escidoc.services.extraction;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class TestWordtextExtraction
{
	private static Parser parser = new AutoDetectParser();
	
	@BeforeClass
	public static void prepare()
	{
		
	}

	@Test
	public void testParseDocx() throws IOException, SAXException, TikaException
	{	
		InputStream stream = TikaInputStream.get(new File("src/test/resources/CoNE-Quiz.docx"));
		
		ContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		
		parser.parse(stream, handler, metadata, new ParseContext());
		
		String content = handler.toString();
		
		assertTrue(content != null);
		assertTrue("Is " + metadata.get(Metadata.CONTENT_TYPE),  
				metadata.get(Metadata.CONTENT_TYPE).startsWith("application/vnd.openxmlformats-officedocument"));
		
		stream.close();
	}
	
	@Test
	public void testParseDoc() throws IOException, SAXException, TikaException
	{	
		InputStream stream = TikaInputStream.get(new File("src/test/resources/Word97.doc"));
		
		ContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		
		parser.parse(stream, handler, metadata, new ParseContext());
		
		String content = handler.toString();
		
		assertTrue(content != null);
		assertTrue("Is " + metadata.get(Metadata.CONTENT_TYPE),  
				metadata.get(Metadata.CONTENT_TYPE).startsWith("application/msword"));
		
		stream.close();
	}

}
