import static org.junit.Assert.*;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;

import de.mpg.escidoc.handler.SrwSearchResponseHandler;


public class TestSrwSearchResponseHandler
{
    SrwSearchResponseHandler handler = new SrwSearchResponseHandler();
    
    @Test
    public void test() throws Exception
    {
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        
        // search for version pid
        SrwSearchResponseHandler handler = new SrwSearchResponseHandler();
        
        
        handler.setPidToSearchFor("hdl:11858/00-001Z-0000-0023-673A-F");
        
        parser.parse(new File("src/test/resources/escidoc_672822"), handler);
        
        assertTrue(handler.isVersionPid());
        assertTrue(!handler.isObjectPid());
        
        assertTrue(handler.getItemUrl().equals("/item/escidoc:672822"));
        assertTrue(handler.getVersionUrl().equals("/item/escidoc:672822:1"));
        assertTrue(handler.getEscidocId().equals("escidoc:672822"));
        assertTrue(handler.getLastModificationDate().equals("2014-04-02T12:32:55.440Z"));
        
        // search for object pid
        handler = new SrwSearchResponseHandler();
        handler.setPidToSearchFor("hdl:11858/00-001Z-0000-0023-6739-2");
        
        parser.parse(new File("src/test/resources/escidoc_672822"), handler);
        
        assertTrue(!handler.isVersionPid());
        assertTrue(handler.isObjectPid());
        
        assertTrue(handler.getItemUrl().equals("/item/escidoc:672822"));
        assertTrue(handler.getVersionUrl() == null);
        
        // search in item with several versions and components        
        handler = new SrwSearchResponseHandler();
        // object pid
        handler.setPidToSearchFor("hdl:11858/00-001Z-0000-0022-C896-2");        
        parser.parse(new File("src/test/resources/escidoc_530180"), handler);
        
        assertTrue(!handler.isVersionPid());
        assertTrue(handler.isObjectPid());
        
        assertTrue(handler.getItemUrl().equals("/item/escidoc:530180"));
        assertTrue(handler.getEscidocId().equals("escidoc:530180"));
        assertTrue(handler.getLastModificationDate().equals("2014-01-22T09:02:39.026Z"));
        assertTrue(handler.getVersionUrl() == null);
        
        // version pid
        handler = new SrwSearchResponseHandler();
        handler.setPidToSearchFor("hdl:11858/00-001Z-0000-0023-2D99-2");     
        parser.parse(new File("src/test/resources/escidoc_530180"), handler);
        
        assertTrue(handler.isVersionPid());
        assertTrue(!handler.isObjectPid());
        
        assertTrue(handler.getItemUrl().equals("/item/escidoc:530180"));
        assertTrue(handler.getVersionUrl().equals("/item/escidoc:530180:10"));
        
        // component pid1
        handler = new SrwSearchResponseHandler();
        handler.setPidToSearchFor("hdl:11858/00-001Z-0000-0022-EE20-8");        
        parser.parse(new File("src/test/resources/escidoc_530180"), handler);
        
        assertTrue(!handler.isVersionPid());
        assertTrue(!handler.isObjectPid());
        assertTrue(handler.isComponentPid());
        
        assertTrue(handler.getItemUrl().equals("/item/escidoc:530180"));
        assertTrue(handler.getVersionUrl() == null);
        assertTrue(handler.getComponentUrl().equals("/item/escidoc:530180/component/escidoc:553224/Rest_api_doc_SB_Search.pdf"));
        // component pid2
        handler = new SrwSearchResponseHandler();
        handler.setPidToSearchFor("hdl:11858/00-001Z-0000-0022-C899-B");        
        parser.parse(new File("src/test/resources/escidoc_530180"), handler);
        
        assertTrue(!handler.isVersionPid());
        assertTrue(!handler.isObjectPid());
        assertTrue(handler.isComponentPid());
        
        assertTrue(handler.getItemUrl().equals("/item/escidoc:530180"));
        assertTrue(handler.getVersionUrl() == null);
        assertTrue(handler.getComponentUrl().equals("/item/escidoc:530180/component/escidoc:530179/model_01.mat"));
        
    }
}
