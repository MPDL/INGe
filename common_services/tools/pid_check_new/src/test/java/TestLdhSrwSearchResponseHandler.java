import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

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
        
        List<String> locators = handler.getLocators();
        
        assertTrue(locators.size() == 1);
        assertTrue(locators.get(0).equals("escidoc:227005 | http://arXiv.org/abs/physics/0408111"));
        
        handler = new LdhSrwSearchResponseHandler();
        
        parser.parse(new File("src/test/resources/srw2.xml"), handler);
        locators = handler.getLocators();
        
        assertTrue(locators.size() == 1);
        assertTrue(locators.get(0).equals("escidoc:485321 | http://google.de"));
        
        handler = new LdhSrwSearchResponseHandler();
        
        parser.parse(new File("src/test/resources/srw3.xml"), handler);
        locators = handler.getLocators();
        
        assertTrue(locators.size() == 1);
        assertTrue(locators.get(0).equals("escidoc:486028 | http://alpine-auskunft.de"));
        
        handler = new LdhSrwSearchResponseHandler();
        
        parser.parse(new File("src/test/resources/srw4.xml"), handler);
        locators = handler.getLocators();
        
        assertTrue(locators.size() == 3);
        assertTrue(locators.get(0).equals("escidoc:400080 | http://www.sil.org/acpub/repository/15234.pdf(partIII)"));
        assertTrue(locators.get(1).equals("escidoc:400080 | http://www.sil.org/acpub/repository/15233.pdf(partII)"));
        assertTrue(locators.get(2).equals("escidoc:400080 | http://www.sil.org/acpub/repository/15232.pdf(partI)"));
        
    }
}
