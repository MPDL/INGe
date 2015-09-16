package de.mpg.escidoc.handler;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.escidoc.handler.PreHandler.Type;


public class PreHandlerTest
{
    private PreHandler preHandler = null;
    private static SAXParser parser = null;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        org.apache.log4j.BasicConfigurator.configure();
        parser = SAXParserFactory.newInstance().newSAXParser();
    }
    
    @Before
    public void setUp()
    {
    	preHandler = new PreHandler();
    }
    
    @Test
	// escidoc_2110501 without component
	public void testItemWithoutComponent() throws Exception
	{
    	File file = new File("src/test/resources/item/escidoc_2110501");
        
        parser.parse(file, preHandler);
        
        assertTrue(preHandler.getObjectType().equals(Type.ITEM));
        assertTrue(preHandler.getPublicStatus().equals(""));
        assertTrue(preHandler.getVersionStatus().equals(""));
        
        assertTrue(preHandler.getEscidocId().equals("escidoc:2110501"));
       
        Map<String, Set<String>> m = preHandler.getAttributeMapFor("RELS-EXT.11");
        
        assertTrue(m == null);
	}
     
    @Test
    public void testItemOneComponent() throws Exception
    {
        
        File file = new File("src/test/resources/item/escidoc_2110508");
        
        parser.parse(file, preHandler);
        
        assertTrue(preHandler.getObjectType().equals(Type.ITEM));
        assertTrue(preHandler.getPublicStatus().equals("released"));
        assertTrue(preHandler.getVersionStatus().equals("released"));
        
        assertTrue(preHandler.getReleaseNumber().equals("1"));
        assertTrue(preHandler.getVersionNumber().equals("1"));
        assertTrue(preHandler.getEscidocId().equals("escidoc:2110508"));
       
        Map<String, Set<String>> m = preHandler.getAttributeMapFor("RELS-EXT.11");
        
        assertTrue(m.get("version:number").iterator().next().equals("1"));
        assertTrue(m.get("prop:public-status").iterator().next().equals("released"));
        assertTrue(m.get("version:status").iterator().next().equals("released"));
        assertTrue(m.get("srel:component").contains("info:fedora/escidoc:2110507"));
    }
    

    
    // escidoc:2111689 released item with 2 component; escidoc:2111688 locator and escidoc:2111687 component with pdf
 	@Test
 	public void testReleasedItemTwoComponents() throws Exception
 	{
 		File file = new File("src/test/resources/item/escidoc_2111689");
        
        parser.parse(file, preHandler);
        
        assertTrue(preHandler.getObjectType().equals(Type.ITEM));
        assertTrue(preHandler.getPublicStatus().equals("released"));
        assertTrue(preHandler.getVersionStatus().equals("released"));
        
        assertTrue(preHandler.getReleaseNumber().equals("1"));
        assertTrue(preHandler.getVersionNumber().equals("1"));
        assertTrue(preHandler.getEscidocId().equals("escidoc:2111689"));
        
        assertTrue(preHandler.getSrelComponent().contains("info:fedora/escidoc:2111687")
        		&& preHandler.getSrelComponent().contains("info:fedora/escidoc:2111688"));
       
 		
 	}
    
 
}
