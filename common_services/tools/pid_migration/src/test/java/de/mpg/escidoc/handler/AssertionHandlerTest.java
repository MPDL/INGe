package de.mpg.escidoc.handler;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

public class AssertionHandlerTest
{
    private static Logger logger = Logger.getLogger(AssertionHandlerTest.class); 
    
    private static SAXParser parser;
    private PreHandler preHandler;
    private AssertionHandler assertionHandler;
    
    @BeforeClass
    static public void setUpBeforeClass() throws Exception
    {
        //org.apache.log4j.BasicConfigurator.configure();
        
        parser = SAXParserFactory.newInstance().newSAXParser();
    }
    
    @Before
    public void setup() throws Exception
    {
        preHandler = new PreHandler();
        assertionHandler = new AssertionHandler(preHandler);
    }
    
    @Test
    public void testFilesBeforeMigration() throws Exception
    {
        File f = new File("src/test/resources/item_sav/escidoc_1479027");
        
        try
        {
            parser.parse(f, preHandler);
            parser.parse(f, assertionHandler);
        }
        catch (SAXException e)
        {
            assertTrue(e.getMessage().contains(AssertionHandler.DUMMY_HANDLE_FOUND_FOR_VERSION_OR_RELEASE_PID)
                    || e.getMessage().contains(AssertionHandler.DUMMY_HANDLE_FOUND_FOR_OBJECT_PID));
        }
        
        f = new File("src/test/resources/component_sav/escidoc_418001");
        
        preHandler = new PreHandler();
        assertionHandler = new AssertionHandler(preHandler);
        
        try
        {
            parser.parse(f, preHandler);
            parser.parse(f, assertionHandler);
        }
        catch (SAXException e)
        {
            assertTrue(e.getMessage().contains(AssertionHandler.DUMMY_HANDLE_FOUND_FOR_VERSION_OR_RELEASE_PID)
                    || e.getMessage().contains(AssertionHandler.DUMMY_HANDLE_FOUND_FOR_OBJECT_PID));
        }
        
        f = new File("src/test/resources/item_sav/itemReleasedOnce");
        
        preHandler = new PreHandler();
        assertionHandler = new AssertionHandler(preHandler);
        
        try
        {
            parser.parse(f, preHandler);
            parser.parse(f, assertionHandler);
        }
        catch (SAXException e)
        {
            assertTrue(e.getMessage().contains(AssertionHandler.DUMMY_HANDLE_FOUND_FOR_VERSION_OR_RELEASE_PID)
                    || e.getMessage().contains(AssertionHandler.DUMMY_HANDLE_FOUND_FOR_OBJECT_PID));
        }
    }

    @Test
    public void testFilesUncompleteMigration() throws IOException
    {
        File f = new File("src/test/resources/item_sav/itemReleasedOnceIncompleteMigration");
        
        try
        {
            parser.parse(f, preHandler);
            parser.parse(f, assertionHandler);
        }
        catch (SAXException e)
        {
            assertTrue(e.getMessage().contains(AssertionHandler.DUMMY_HANDLE_FOUND_FOR_OBJECT_PID));
        }        
       
    }
    
    
    @Test
    public void testFilesFullMigration() throws IOException
    {
        File f = new File("src/test/resources/item_sav/itemReleasedOnceFullMigration");
        
        try
        {
            parser.parse(f, preHandler);
            parser.parse(f, assertionHandler);
        }
        catch (SAXException e)
        {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testPattern() throws IOException
    {
        String s = "hdl:11858/00-001Z-0000-000E-50F3-0";
        Pattern handlePattern = AssertionHandler.handlePattern;
                
        Matcher m = handlePattern.matcher(s);
        assertTrue(m.matches());
        
        s = "hdl:11858/00-001Z-0000-000E-50F3-X";
        m = handlePattern.matcher(s);
        assertFalse(m.matches());
        
        s = "hdl:12345/00-001Z-0000-000E-1111-1";
        m = handlePattern.matcher(s);
        assertTrue(m.matches());
        
        s = "hdl:someHandle/test/escidoc:1479027";
        m = handlePattern.matcher(s);
        assertFalse(m.matches());
  
    }
}
