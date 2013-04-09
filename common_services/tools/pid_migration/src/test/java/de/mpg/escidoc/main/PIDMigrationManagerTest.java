package de.mpg.escidoc.main;

import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.handler.AssertionHandler;
import de.mpg.escidoc.handler.PreHandler;

public class PIDMigrationManagerTest
{
    private static Logger logger = Logger.getLogger(PIDMigrationManagerTest.class);   
    
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
    
    @Test
    public void transformFiles() throws Exception
    {
        File f = new File("src/test/resources/item/escidoc_1479027");
        new PIDMigrationManager(f);        
        assertTrue(checkAfterMigration(f));
        
        new PIDMigrationManager(new File("src/test/resources/item/itemPublicStatusPending"));        
        assertTrue(checkAfterMigration(new File("src/test/resources/item/itemPublicStatusPending")));
        
        new PIDMigrationManager(new File("src/test/resources/component/escidoc_418001"));        
        assertTrue(checkAfterMigration(new File("src/test/resources/component/escidoc_418001")));
    }
    
    @Test 
    public void transformDirectory() throws Exception
    {
        File f = new File("src/test/resources/item");
        new PIDMigrationManager(f);        
        assertTrue(checkAfterMigration(f));
        
        new PIDMigrationManager(new File("src/test/resources/component"));        
        assertTrue(checkAfterMigration(new File("src/test/resources/component")));
    }
    
    @Test
    @Ignore
    public void transformQa() throws Exception
    {
        FileUtils.deleteDirectory(new File("C:/Test/qa-coreservice/2013"));
        FileUtils.copyDirectory(new File("C:/Test/qa-coreservice/2013_sav"), 
                new File("C:/Test/qa-coreservice/2013"));
        new PIDMigrationManager(new File("C:/Test/qa-coreservice/2013"));        
        assertTrue(checkAfterMigration(new File("C:/Test/qa-coreservice/2013")));
    }

    private boolean checkAfterMigration(File file) throws Exception
    {
        logger.info("checkAfterMigration file <" + file.getName() + ">");
        if (file != null && file.isFile())
        {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            PreHandler preHandler = new PreHandler();
            AssertionHandler assertionHandler = new AssertionHandler(preHandler);
            
            parser.parse(file, preHandler);
            parser.parse(file, assertionHandler);
            
            return true;
        }
        
        File[] files = file.listFiles();
        
        for (File f : files)
        {
            if (f.getName().endsWith(".svn"))
                continue;
            if (f.isDirectory())
            {
                checkAfterMigration(f);
            }
            else
            {
                logger.info("checkAfterMigration file <" + f.getName() + ">");
                SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
                PreHandler preHandler = new PreHandler();
                AssertionHandler assertionHandler = new AssertionHandler(preHandler);
                
                parser.parse(f, preHandler);
                parser.parse(f, assertionHandler);
            }
        }
        
        return true;
    }
}
