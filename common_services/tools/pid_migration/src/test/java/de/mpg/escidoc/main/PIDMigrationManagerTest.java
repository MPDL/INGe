package de.mpg.escidoc.main;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
import de.mpg.escidoc.handler.PreHandler.Status;
import de.mpg.escidoc.handler.PreHandler.Type;
import de.mpg.escidoc.util.MigrationStatistic;

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
    public void transformLargeFile() throws Exception
    {
        PIDMigrationManager m = new PIDMigrationManager(new File("src/test/resources/item/escidoc_61195"));   
        MigrationStatistic statistic = m.getMigrationStatistic();
        // quite strange, but foxml version:pids already starts with 3!!
        assertTrue(statistic.getTotalNumberOfPidsRequested() >= 13);
        assertTrue(checkAfterMigration(new File("src/test/resources/item/escidoc_61195")));
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
        PIDMigrationManager mgr = new PIDMigrationManager(new File("C:/Test/qa-coreservice/2013")); 
        
        MigrationStatistic statistic = mgr.getMigrationStatistic();
        logger.info("FilesMigratedTotal              " + statistic.getFilesMigratedTotal());
        logger.info("FilesMigratedNotReleased        " + statistic.getFilesMigratedNotReleased());
        logger.info("FilesMigratedNotItemOrComponent " + statistic.getFilesMigratedNotItemOrComponent());
        logger.info("FilesMigratedNotUpdated         " + statistic.getFilesMigratedNotUpdated());
        logger.info("FilesErrorOccured               " + statistic.getFilesErrorOccured());
        logger.info("FilesMigrationDone              " + statistic.getFilesMigrationDone());
        logger.info("***********************");
        logger.info("TotalNumberOfPidsRequested      " + statistic.getTotalNumberOfPidsRequested());
        assertTrue(statistic.getFilesMigratedTotal() == (statistic.getFilesMigratedNotItemOrComponent() + statistic.getFilesMigratedNotReleased()
                                         + statistic.getFilesMigratedNotUpdated() + statistic.getFilesMigrationDone()));
        assertTrue(statistic.getTotalNumberOfPidsRequested() > 100);
        
        assertTrue(checkAfterMigration(new File("C:/Test/qa-coreservice/2013"), statistic.getErrorList()));
    }
    
    @Test
    public void transformQa2009() throws Exception
    {
        FileUtils.deleteDirectory(new File("C:/Test/qa-coreservice/2009"));
        FileUtils.copyDirectory(new File("C:/Test/qa-coreservice/2009_sav"), 
                new File("C:/Test/qa-coreservice/2009"));
        PIDMigrationManager mgr = new PIDMigrationManager(new File("C:/Test/qa-coreservice/2009"));        
        MigrationStatistic statistic = mgr.getMigrationStatistic();
        logger.info("FilesMigratedTotal              " + statistic.getFilesMigratedTotal());
        logger.info("FilesMigratedNotReleased        " + statistic.getFilesMigratedNotReleased());
        logger.info("FilesMigratedNotItemOrComponent " + statistic.getFilesMigratedNotItemOrComponent());
        logger.info("FilesMigratedNotUpdated         " + statistic.getFilesMigratedNotUpdated());
        logger.info("FilesErrorOccured               " + statistic.getFilesErrorOccured());
        logger.info("FilesMigrationDone              " + statistic.getFilesMigrationDone());
        logger.info("***********************");
        logger.info("TotalNumberOfPidsRequested      " + statistic.getTotalNumberOfPidsRequested());
        assertTrue(statistic.getFilesMigratedTotal() == (statistic.getFilesMigratedNotItemOrComponent() + statistic.getFilesMigratedNotReleased()
                                         + statistic.getFilesMigratedNotUpdated() + statistic.getFilesMigrationDone()));
        assertTrue(statistic.getTotalNumberOfPidsRequested() > 20000);
        
        assertTrue(checkAfterMigration(new File("C:/Test/qa-coreservice/2009"), statistic.getErrorList()));
    }
    

    private boolean checkAfterMigration(File file) throws Exception
    {
        return checkAfterMigration(file, new ArrayList<String>());
    }

    private boolean checkAfterMigration(File file, List<String> ignoreList) throws Exception
    {
        logger.info("checkAfterMigration file <" + file.getName() + ">");
        if (file != null && file.isFile())
        {
            if (ignoreList.contains(file.getName()))
            {
                   return true;
            }

            return validateFile(file);
        }
        
        File[] files = file.listFiles();
        
        Collections.sort(Arrays.asList(files));
        
        for (File f : files)
        {
            if (f.getName().endsWith(".svn"))
                continue;
            
            if (ignoreList.contains(f.getName()))
                continue;
            
            if (f.isDirectory())
            {
                checkAfterMigration(f, ignoreList);
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
        if (preHandler.getObjectType().equals(Type.ITEM) && !(preHandler.getPublicStatus().equals(Status.RELEASED)))
        {
            logger.info("No validation for file <" + file.getName() + "> because of public-status <" + preHandler.getPublicStatus() + ">");
            return true;
        }
        parser.parse(file, assertionHandler);
        
        return true;
    }
}
