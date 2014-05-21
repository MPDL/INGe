import static org.junit.Assert.*;

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;
import org.xml.sax.SAXException;

import de.mpg.escidoc.handler.LdhSrwSearchResponseHandler;
import de.mpg.escidoc.handler.SrwSearchResponseHandler;


public class TestLdhSrwSearchResponseHandler
{
    LdhSrwSearchResponseHandler handler = new LdhSrwSearchResponseHandler();
    
    @Test
    public void test() throws Exception
    {
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();

        parser.parse(new File("src/test/resources/srw1.xml"), handler);
        
        String locator = handler.getLocator();
        
        assertTrue(locator.equals("http://arXiv.org/abs/physics/0408111"));
        
        handler = new LdhSrwSearchResponseHandler();
        
        parser.parse(new File("src/test/resources/srw2.xml"), handler);
        locator = handler.getLocator();
        
        assertTrue(locator.equals("http://google.de"));
        
        handler = new LdhSrwSearchResponseHandler();
        
        parser.parse(new File("src/test/resources/srw3.xml"), handler);
        locator = handler.getLocator();
        
        assertTrue(locator.equals("http://alpine-auskunft.de"));
        
    }
}
