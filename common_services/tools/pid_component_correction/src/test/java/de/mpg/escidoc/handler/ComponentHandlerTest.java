package de.mpg.escidoc.handler;

import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ComponentHandlerTest
{
	private ComponentHandler componentHandler = null;
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
    	componentHandler = new ComponentHandler();
    }

    @Test
    // internal managed content
	public void testComponent_2110507() throws Exception
	{
    	File file = new File("src/test/resources/component/escidoc_2110507");
        
        parser.parse(file, componentHandler);
        
        assertTrue(componentHandler.getComponentMap() != null);
        assertTrue(componentHandler.getFilename().equals("RJMP22_9.pdf"));
        assertTrue(componentHandler.getPid().equals("hdl:11858/00-001M-0000-0025-0ADE-6"));
        assertTrue(componentHandler.getContentLocation().equals("INTERNAL_ID"));
	}
    
    @Test
    // internal managed content
	public void testComponent_2111687() throws Exception
	{
    	File file = new File("src/test/resources/component/escidoc_2111687");
        
        parser.parse(file, componentHandler);
        
        assertTrue(componentHandler.getComponentMap() != null);
        assertTrue(componentHandler.getFilename().equals("Hafer_et_al_2015.pdf"));
        assertTrue(componentHandler.getPid().equals("hdl:11858/00-001M-0000-0025-7377-9"));
        assertTrue(componentHandler.getContentLocation().equals("INTERNAL_ID"));
	}
    
    @Test
    // external url - without component pid
	public void testComponent_2111688() throws Exception
	{
    	File file = new File("src/test/resources/component/escidoc_2111688");
        
        parser.parse(file, componentHandler);
        
        assertTrue(componentHandler.getComponentMap() != null);
        assertTrue(componentHandler.getFilename().equals("http://onlinelibrary.wiley.com/doi/10.1111/evo.12612/abstract"));
        assertTrue(componentHandler.getPid() == null);
        assertTrue(componentHandler.getContentLocation().equals("URL"));
	}

}
