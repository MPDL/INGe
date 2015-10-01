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

import de.mpg.escidoc.handler.ItemHandler.Type;


public class ItemHandlerTest
{
    private ItemHandler itemHandler = null;
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
    	itemHandler = new ItemHandler();
    }
    
    @Test
	// escidoc_2110501 without component
	public void testItemWithoutComponent1() throws Exception
	{
    	File file = new File("src/test/resources/item/escidoc_2110501");
        
        parser.parse(file, itemHandler);
        
        assertTrue(itemHandler.getObjectType().equals(Type.ITEM));  
        assertTrue(itemHandler.getEscidocId().equals("escidoc:2110501"));
       
        Map<String, Set<String>> m = itemHandler.getElementMapFor("RELS-EXT.11");
        
        assertTrue(m == null);
	}
    
    @Test
    // escidoc:2110518 no component
    public void testItemWithoutComponent2() throws Exception
    {
        
        File file = new File("src/test/resources/item/escidoc_2110518");
        
        parser.parse(file, itemHandler);
        
        assertTrue(itemHandler.getObjectType().equals(Type.ITEM));
        assertTrue(itemHandler.getEscidocId().equals("escidoc:2110518"));
       
        Map<String, Map<String, Set<String>>> globalElementMap = itemHandler.getGlobalElementMap();
        
        assertTrue(globalElementMap != null);
        assertTrue(globalElementMap.size() == 0);
    }
     
    @Test
    // escidoc:2110508 one component escidoc:2110507 internal-managed
    public void testItemOneComponent() throws Exception
    {
        
        File file = new File("src/test/resources/item/escidoc_2110508");
        
        parser.parse(file, itemHandler);
        
        assertTrue(itemHandler.getObjectType().equals(Type.ITEM));
        assertTrue(itemHandler.getEscidocId().equals("escidoc:2110508"));
        
        Map<String, Map<String, Set<String>>> globalElementMap = itemHandler.getGlobalElementMap();
        
        assertTrue(globalElementMap != null);
        assertTrue(globalElementMap.size() == 1);
       
        Map<String, Set<String>> m = itemHandler.getElementMapFor("RELS-EXT.10");
        
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
        
        parser.parse(file, itemHandler);
        
        assertTrue(itemHandler.getObjectType().equals(Type.ITEM));
        assertTrue(itemHandler.getEscidocId().equals("escidoc:2111689"));
        
        Map<String, Map<String, Set<String>>> globalElementMap = itemHandler.getGlobalElementMap();
        
        assertTrue(globalElementMap != null);
        assertTrue(globalElementMap.size() == 1);
       
        Map<String, Set<String>> m = itemHandler.getElementMapFor("RELS-EXT.10");
        assertTrue(m.get("version:number").iterator().next().equals("1"));
        assertTrue(m.get("prop:public-status").iterator().next().equals("released"));
        assertTrue(m.get("version:status").iterator().next().equals("released"));
        
        assertTrue(itemHandler.getSrelComponent("RELS-EXT.10").contains("info:fedora/escidoc:2111687")
        		&& itemHandler.getSrelComponent("RELS-EXT.10").contains("info:fedora/escidoc:2111688"));
       
 		
 	}
 	
 	// escidoc:2169637 released item with totally 3 components; 
 	// escidoc:2169636 component of version 1 
 	// escidoc:2170639 component of version 2 (not released)
 	// escidoc:2170641 component of version 3
  	@Test
  	public void testReleasedItemWithModifiedComponents() throws Exception
  	{
  		File file = new File("src/test/resources/item/escidoc_2169637");
         
         parser.parse(file, itemHandler);
         
         assertTrue(itemHandler.getObjectType().equals(Type.ITEM));
         assertTrue(itemHandler.getEscidocId().equals("escidoc:2169637"));
         
         Map<String, Map<String, Set<String>>> globalElementMap = itemHandler.getGlobalElementMap();
         
         assertTrue(globalElementMap != null);
         assertTrue(globalElementMap.size() == 2);
        
         Map<String, Set<String>> m = itemHandler.getElementMapFor("RELS-EXT.10");
         assertTrue(m.get("version:number").iterator().next().equals("1"));
         assertTrue(m.get("prop:public-status").iterator().next().equals("released"));
         assertTrue(m.get("version:status").iterator().next().equals("released"));         
         assertTrue(itemHandler.getSrelComponent("RELS-EXT.10").contains("info:fedora/escidoc:2169636")); 
         
         m = itemHandler.getElementMapFor("RELS-EXT.22");
         assertTrue(m.get("version:number").iterator().next().equals("3"));
         assertTrue(m.get("prop:public-status").iterator().next().equals("released"));
         assertTrue(m.get("version:status").iterator().next().equals("released"));         
         assertTrue(itemHandler.getSrelComponent("RELS-EXT.22").contains("info:fedora/escidoc:2170641")); 

  	}
    
 
}
