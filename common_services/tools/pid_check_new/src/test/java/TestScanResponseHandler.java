

import static org.junit.Assert.*;

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;
import org.xml.sax.SAXException;

import de.mpg.escidoc.handler.ScanResponseHandler;
import de.mpg.escidoc.handler.SrwSearchResponseHandler;

public class TestScanResponseHandler
{
	ScanResponseHandler handler = new ScanResponseHandler();

	@Test
	public void test() throws Exception
	{
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        
		parser.parse(new File("src/test/resources/handlesFromScan.xml"), handler);
		
		assertTrue(handler.getPids().size() == 10);
		assertTrue(handler.getPids().contains("hdl:11858/00-001z-0000-000c-c37b-d"));
		assertTrue(handler.getPids().contains("hdl:11858/00-001z-0000-000c-c37d-9"));
		assertTrue(handler.getPids().contains("hdl:11858/00-001z-0000-000c-c37f-5"));
		assertTrue(handler.getPids().contains("hdl:11858/00-001z-0000-000c-c382-c"));
		assertTrue(handler.getPids().contains("hdl:11858/00-001z-0000-000c-c384-8"));
		
		assertTrue(handler.getPidsUsedSeveralTimes().size() == 1);
		assertTrue(handler.getPidsUsedSeveralTimes().contains("hdl:11858/00-001z-0000-000c-c37f-5"));
		
	}

}
