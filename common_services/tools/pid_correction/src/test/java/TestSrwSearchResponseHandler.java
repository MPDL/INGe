import static org.junit.Assert.*;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;

import de.mpg.escidoc.handler.SrwSearchResponseHandler;


public class TestSrwSearchResponseHandler
{
    SrwSearchResponseHandler h = new SrwSearchResponseHandler();
    
    @Test
    public void test() throws Exception
    {
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        
        parser.parse(new File("src/test/resources/escidoc_672822"), h);
        
        assertTrue(h.getObjectPid().equals("hdl:11858/00-001Z-0000-0023-6739-2"));
        assertTrue(h.getVersionPid().equals("hdl:11858/00-001Z-0000-0023-673A-F"));
        
    }
}
