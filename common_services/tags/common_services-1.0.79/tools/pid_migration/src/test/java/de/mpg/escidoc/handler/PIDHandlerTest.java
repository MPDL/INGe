package de.mpg.escidoc.handler;

import static org.junit.Assert.assertFalse;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PIDHandlerTest
{    
    private static Logger logger = Logger.getLogger(PIDHandlerTest.class);   
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        //org.apache.log4j.BasicConfigurator.configure();
        
        // remove test files from previous tests
        try
        {
            FileUtils.deleteDirectory(
                    new File("src/test/resources/item"));
            FileUtils.deleteDirectory(
                    new File("src/test/resources/component"));
            FileUtils.deleteDirectory(
                    new File("src/test/resources/context"));
            FileUtils.deleteDirectory(
                    new File("src/test/resources/content-model"));
            
            FileUtils.copyDirectory(new File("src/test/resources/item_sav"), 
                    new File("src/test/resources/item"));
            FileUtils.copyDirectory(new File("src/test/resources/component_sav"), 
                    new File("src/test/resources/component"));
            FileUtils.copyDirectory(new File("src/test/resources/context_sav"), 
                    new File("src/test/resources/context"));
            FileUtils.copyDirectory(new File("src/test/resources/content-model_sav"), 
                    new File("src/test/resources/content-model"));
        }
        catch (Exception e)
        {
            logger.warn("Error when preparing test resources " + e);
            e.printStackTrace();
        }
    }
    

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void testIsUpdateDone() throws Exception
    {
        File f = new File("src/test/resources/item/itemReleasedOnceFullMigration");
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        PreHandler preHandler = new PreHandler();
        PIDHandler handler = new PIDHandler(preHandler);
        
        parser.parse(f, preHandler);
        parser.parse(f, handler);
        
        assertFalse(handler.isUpdateDone());
    }
}
