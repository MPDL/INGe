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
import de.mpg.escidoc.main.ComponentPidTransformer;
import de.mpg.escidoc.util.LocationHelper;
import de.mpg.escidoc.util.Util;


public class ItemHandlerTest
{
    private ItemHandler itemHandler = null;
    private static SAXParser parser = null;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        org.apache.log4j.BasicConfigurator.configure();
        parser = SAXParserFactory.newInstance().newSAXParser();
        
        ComponentPidTransformer pidMigr = new ComponentPidTransformer();
		pidMigr.createLocationFile(new File("src/test/resources"));	
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
       
        Map<String, Set<String>> m = itemHandler.getElementMapFor(Util.getRelsExtAsInteger("RELS-EXT.11"));
        
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
       
        Map<Integer, Map<String, Set<String>>> globalElementMap = itemHandler.getGlobalElementMap();
        
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
        
        Map<Integer, Map<String, Set<String>>> globalElementMap = itemHandler.getGlobalElementMap();
        
        assertTrue(globalElementMap != null);
        assertTrue(globalElementMap.size() == 1);
       
        Map<String, Set<String>> m = itemHandler.getElementMapFor(Util.getRelsExtAsInteger("RELS-EXT.10"));
        
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
        
        Map<Integer, Map<String, Set<String>>> globalElementMap = itemHandler.getGlobalElementMap();
        
        assertTrue(globalElementMap != null);
        assertTrue(globalElementMap.size() == 1);
       
        Map<String, Set<String>> m = itemHandler.getElementMapFor(Util.getRelsExtAsInteger("RELS-EXT.10"));
        assertTrue(m.get("version:number").iterator().next().equals("1"));
        assertTrue(m.get("prop:public-status").iterator().next().equals("released"));
        assertTrue(m.get("version:status").iterator().next().equals("released"));
        
        assertTrue(itemHandler.getSrelComponent("RELS-EXT.10").contains("info:fedora/escidoc:2111687")
        		&& itemHandler.getSrelComponent("RELS-EXT.10").contains("info:fedora/escidoc:2111688"));
       
 		
 	}
 	
    // escidoc:656742 released item with 2 component; escidoc:656741 locator and escidoc:656740 component with html
 	@Test
 	public void testReleasedItemTwoComponents_656742() throws Exception
 	{
 		File file = new File("src/test/resources/item/escidoc_656742");
        
        parser.parse(file, itemHandler);
        
        assertTrue(itemHandler.getObjectType().equals(Type.ITEM));
        assertTrue(itemHandler.getEscidocId().equals("escidoc:656742"));
        
        Map<Integer, Map<String, Set<String>>> globalElementMap = itemHandler.getGlobalElementMap();
        assertTrue(globalElementMap.keySet().contains(22)
        		&& globalElementMap.keySet().contains(30)
        		&& globalElementMap.keySet().contains(38)
        		&& globalElementMap.keySet().contains(46)
        		&& globalElementMap.keySet().contains(54)
        		&& globalElementMap.keySet().contains(62)
        		&& globalElementMap.keySet().contains(70)
        		&& globalElementMap.keySet().contains(76)
        		&& globalElementMap.keySet().contains(84)
        		&& globalElementMap.keySet().contains(92)
        		&& globalElementMap.keySet().contains(100)
        		&& globalElementMap.keySet().contains(108)
        		&& globalElementMap.keySet().contains(116)       		
        		);
        
        
        assertTrue(globalElementMap != null);
        assertTrue("Is <" + globalElementMap.size()  + "> expected 13", globalElementMap.size() == 13);
       
        Map<String, Set<String>> m = itemHandler.getElementMapFor(Util.getRelsExtAsInteger("RELS-EXT.30"));
        assertTrue(m != null);
        assertTrue(m.get("version:number").iterator().next().equals("7"));
        assertTrue(m.get("prop:public-status").iterator().next().equals("released"));
        assertTrue(m.get("version:status").iterator().next().equals("released"));
        
        assertTrue(itemHandler.getSrelComponent("RELS-EXT.30").contains("info:fedora/escidoc:656740")
        		&& itemHandler.getSrelComponent("RELS-EXT.30").contains("info:fedora/escidoc:656741"));
        
        m = itemHandler.getElementMapFor(Util.getRelsExtAsInteger("RELS-EXT.38"));
        assertTrue(m != null);
        assertTrue(m.get("version:number").iterator().next().equals("8"));
        assertTrue(m.get("prop:public-status").iterator().next().equals("released"));
        assertTrue(m.get("version:status").iterator().next().equals("released"));
        
        assertTrue(itemHandler.getSrelComponent("RELS-EXT.38").contains("info:fedora/escidoc:656740")
        		&& itemHandler.getSrelComponent("RELS-EXT.38").contains("info:fedora/escidoc:656741"));
        
        m = itemHandler.getElementMapFor(Util.getRelsExtAsInteger("RELS-EXT.70"));
        assertTrue(m != null);
        assertTrue(m.get("version:number").iterator().next().equals("12"));
        assertTrue(m.get("prop:public-status").iterator().next().equals("released"));
        assertTrue(m.get("version:status").iterator().next().equals("released"));
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
         
         Map<Integer, Map<String, Set<String>>> globalElementMap = itemHandler.getGlobalElementMap();
         
         assertTrue(globalElementMap != null);
         assertTrue(globalElementMap.size() == 2);
        
         Map<String, Set<String>> m = itemHandler.getElementMapFor(Util.getRelsExtAsInteger("RELS-EXT.10"));
         assertTrue(m.get("version:number").iterator().next().equals("1"));
         assertTrue(m.get("prop:public-status").iterator().next().equals("released"));
         assertTrue(m.get("version:status").iterator().next().equals("released"));         
         assertTrue(itemHandler.getSrelComponent("RELS-EXT.10").contains("info:fedora/escidoc:2169636")); 
         
         m = itemHandler.getElementMapFor(Util.getRelsExtAsInteger("RELS-EXT.22"));
         assertTrue(m.get("version:number").iterator().next().equals("3"));
         assertTrue(m.get("prop:public-status").iterator().next().equals("released"));
         assertTrue(m.get("version:status").iterator().next().equals("released"));         
         assertTrue(itemHandler.getSrelComponent("RELS-EXT.22").contains("info:fedora/escidoc:2170641")); 

  	}
  	
  	@Test
  	public void testWithdrawnItem() throws Exception
  	{
  		File file = new File("src/test/resources/item/escidoc_788002");
        
        parser.parse(file, itemHandler);
        
        assertTrue(itemHandler.getObjectType().equals(Type.ITEM));
        assertTrue(itemHandler.getEscidocId().equals("escidoc:788002"));
        
        Map<String, Set<String>> m = itemHandler.getElementMapFor(Util.getRelsExtAsInteger("RELS-EXT.54"));
        
        Map<Integer, Map<String, Set<String>>> globalElementMap = itemHandler.getGlobalElementMap();
        
        assertTrue(globalElementMap != null);
        assertTrue("Excpected 0, found <" + globalElementMap.size() + ">", globalElementMap.size() == 0);
  	}
}
