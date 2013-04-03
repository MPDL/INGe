package de.mpg.escidoc.handler;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.mpg.escidoc.main.PIDMigrationManagerTest;

public class AssertionHandlerTest
{
    private static Logger logger = Logger.getLogger(AssertionHandlerTest.class); 
    
    private static SAXParser parser;
    private static PreHandler preHandler;
    private static AssertionHandler assertionHandler;
    
    @BeforeClass
    static public void setUpBeforeClass() throws Exception
    {
        org.apache.log4j.BasicConfigurator.configure();
        
        parser = SAXParserFactory.newInstance().newSAXParser();
        preHandler = new PreHandler();
        assertionHandler = new AssertionHandler(preHandler);
    }
    
    @Test
    public void testFilesBeforeMigration() throws IOException
    {
        File f = new File("src/test/resources/item_sav/escidoc_1479027");
        
        try
        {
            parser.parse(f, preHandler);
            parser.parse(f, assertionHandler);
        }
        catch (SAXException e)
        {
            assertTrue(e.getMessage().contains(AssertionHandler.DUMMY_HANDLE_FOUND_FOR_VERSION_OR_RELEASE_PID));
        }
        
        f = new File("src/test/resources/component_sav/escidoc_418001");
        
        try
        {
            parser.parse(f, preHandler);
            parser.parse(f, assertionHandler);
        }
        catch (SAXException e)
        {
            assertTrue(e.getMessage().contains(AssertionHandler.DUMMY_HANDLE_FOUND_FOR_VERSION_OR_RELEASE_PID));
        }
        
        f = new File("src/test/resources/item_sav/itemReleasedOnce");
        
        try
        {
            parser.parse(f, preHandler);
            parser.parse(f, assertionHandler);
        }
        catch (SAXException e)
        {
            assertTrue(e.getMessage().contains(AssertionHandler.DUMMY_HANDLE_FOUND_FOR_VERSION_OR_RELEASE_PID));
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
}
