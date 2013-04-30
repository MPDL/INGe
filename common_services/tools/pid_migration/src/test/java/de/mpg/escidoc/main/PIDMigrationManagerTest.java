package de.mpg.escidoc.main;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.mpg.escidoc.handler.AssertionHandler;
import de.mpg.escidoc.handler.PreHandler;
import de.mpg.escidoc.handler.PreHandler.PublicStatus;
import de.mpg.escidoc.handler.PreHandler.Type;

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
        
        new PIDMigrationManager(new File("src/test/resources/item/itemWithdrawn"));        
        assertTrue(checkAfterMigration(new File("src/test/resources/item/itemWithdrawn")));
        
        new PIDMigrationManager(new File("src/test/resources/item/itemInrevision"));        
        assertTrue(checkAfterMigration(new File("src/test/resources/item/itemInrevision")));
        
        new PIDMigrationManager(new File("src/test/resources/content-model/escidoc_persistent4"));        
        assertTrue(checkAfterMigration(new File("src/test/resources/content-model/escidoc_persistent4")));
        
        new PIDMigrationManager(new File("src/test/resources/context/escidoc_persistent3"));        
        assertTrue(checkAfterMigration(new File("src/test/resources/context/escidoc_persistent3")));
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
    @Ignore("needs to set pidchache property to an invalid value")
    public void testOnError() throws Exception
    {
        File f = new File("src/test/resources/item/escidoc_418001");
        PIDMigrationManager mgr = new PIDMigrationManager(f);  
     //   mgr.setPIDCacheUrl("http://dev-pubman.mpdl.mpg.de:8080/pidcache/handle");
        assertTrue(checkAfterMigration(f));
        
    }
    
    @Test
    @Ignore
    public void transformQa2013() throws Exception
    {
        FileUtils.deleteDirectory(new File("C:/Test/qa-coreservice/2013"));
        FileUtils.copyDirectory(new File("C:/Test/qa-coreservice/2013_sav"), 
                new File("C:/Test/qa-coreservice/2013"));
        new PIDMigrationManager(new File("C:/Test/qa-coreservice/2013"));        
        assertTrue(checkAfterMigration(new File("C:/Test/qa-coreservice/2013")));
    }
    
    @Test
    @Ignore
    public void transformQa2009() throws Exception
    {
        FileUtils.deleteDirectory(new File("C:/Test/qa-coreservice/2009"));
        FileUtils.copyDirectory(new File("C:/Test/qa-coreservice/2009_sav"), 
                new File("C:/Test/qa-coreservice/2009"));
        new PIDMigrationManager(new File("C:/Test/qa-coreservice/2009"));        
        assertTrue(checkAfterMigration(new File("C:/Test/qa-coreservice/2009")));
    }

    private boolean checkAfterMigration(File file) throws Exception
    {
        logger.info("checkAfterMigration file <" + file.getName() + ">");
        if (file != null && file.isFile())
        {
            return validateFile(file);
        }
        
        File[] files = file.listFiles();
        
        Collections.sort(Arrays.asList(files));
        
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
                
                return validateFile(f);
            }
        }
        
        return true;
    }

    private boolean validateFile(File file) throws ParserConfigurationException, SAXException, Exception, IOException
    {
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        PreHandler preHandler = new PreHandler();
        AssertionHandler assertionHandler = new AssertionHandler(preHandler);
        
        parser.parse(file, preHandler);
        
         // only migrate items and components
        if (preHandler.getObjectType() == null || !(preHandler.getObjectType().equals(Type.ITEM) || preHandler.getObjectType().equals(Type.COMPONENT)))
        {
            logger.info("No validation for file <" + file.getName() + "> because of type <" + preHandler.getObjectType() + ">");
            return true;
        }
        // do nothing for items in public-status != released
        if (preHandler.getObjectType().equals(Type.ITEM) && !(preHandler.getPublicStatus().equals(PublicStatus.RELEASED)))
        {
            logger.info("No validation for file <" + file.getName() + "> because of public-status <" + preHandler.getPublicStatus() + ">");
            return true;
        }
        parser.parse(file, assertionHandler);
        
        return true;
    }
}
