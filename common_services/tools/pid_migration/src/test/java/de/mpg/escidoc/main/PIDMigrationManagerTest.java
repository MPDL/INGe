package de.mpg.escidoc.main;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.util.MigrationStatistic;
import de.mpg.escidoc.util.Validator;

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
        assertTrue(new Validator().checkAfterMigration(f));
        
        new PIDMigrationManager(new File("src/test/resources/item/itemPublicStatusPending"));        
        assertTrue(new Validator().checkAfterMigration(new File("src/test/resources/item/itemPublicStatusPending")));
        
        new PIDMigrationManager(new File("src/test/resources/component/escidoc_418001"));        
        assertTrue(new Validator().checkAfterMigration(new File("src/test/resources/component/escidoc_418001")));
        
        new PIDMigrationManager(new File("src/test/resources/item/itemWithdrawn"));        
        assertTrue(new Validator().checkAfterMigration(new File("src/test/resources/item/itemWithdrawn")));
        
        new PIDMigrationManager(new File("src/test/resources/item/itemInrevision"));        
        assertTrue(new Validator().checkAfterMigration(new File("src/test/resources/item/itemInrevision")));
        
        new PIDMigrationManager(new File("src/test/resources/content-model/escidoc_persistent4"));        
        assertTrue(new Validator().checkAfterMigration(new File("src/test/resources/content-model/escidoc_persistent4")));
        
        new PIDMigrationManager(new File("src/test/resources/context/escidoc_persistent3"));        
        assertTrue(new Validator().checkAfterMigration(new File("src/test/resources/context/escidoc_persistent3")));
    }
    
    @Test
    public void transformOlderFiles() throws Exception
    {
        PIDMigrationManager m = new PIDMigrationManager(new File("src/test/resources/item/escidoc_61195"));   
        MigrationStatistic statistic = m.getMigrationStatistic();
        // quite strange, but foxml version:pids already starts with 3!!
        assertTrue(statistic.getTotalNumberOfPidsRequested() >= 13);
        assertTrue(new Validator().checkAfterMigration(new File("src/test/resources/item/escidoc_61195")));
        
        statistic.clear();
        
        m = new PIDMigrationManager(new File("src/test/resources/item/escidoc_28493"));   
        statistic = m.getMigrationStatistic();
        // quite strange, but foxml version:pids already starts with 3!!
        assertTrue(statistic.getTotalNumberOfPidsRequested() >= 13);
        assertTrue(new Validator().checkAfterMigration(new File("src/test/resources/item/escidoc_28493")));
    }
    
    @Test 
    public void transformDirectory() throws Exception
    {
        File f = new File("src/test/resources/item");
        new PIDMigrationManager(f);        
        assertTrue(new Validator().checkAfterMigration(f));
        
        new PIDMigrationManager(new File("src/test/resources/component"));        
        assertTrue(new Validator().checkAfterMigration(new File("src/test/resources/component")));
    }
    
    @Test 
    @Ignore("needs to set pidchache property to an invalid value")
    public void testOnError() throws Exception
    {
        File f = new File("src/test/resources/item/escidoc_418001");
        PIDMigrationManager mgr = new PIDMigrationManager(f);  
     //   mgr.setPIDCacheUrl("http://dev-pubman.mpdl.mpg.de:8080/pidcache/handle");
        assertTrue(new Validator().checkAfterMigration(f));
        
    }
}
