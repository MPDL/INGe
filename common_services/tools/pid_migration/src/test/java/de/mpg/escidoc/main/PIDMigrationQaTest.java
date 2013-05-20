package de.mpg.escidoc.main;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.util.MigrationStatistic;
import de.mpg.escidoc.util.Validator;

public class PIDMigrationQaTest
{
    private static Logger logger = Logger.getLogger(PIDMigrationQaTest.class);
    
    @Test
    @Ignore
    public void transformQa2008() throws Exception
    {
        FileUtils.deleteDirectory(new File("C:/Test/qa-coreservice/2008"));
        FileUtils.copyDirectory(new File("C:/Test/qa-coreservice/2008_sav"), 
                new File("C:/Test/qa-coreservice/2008"));
        PIDMigrationManager mgr = new PIDMigrationManager(new File("C:/Test/qa-coreservice/2008"));        
        MigrationStatistic statistic = mgr.getMigrationStatistic();
        logger.info("FilesMigratedTotal              " + statistic.getFilesTotal());
        logger.info("FilesMigratedNotReleased        " + statistic.getFilesMigratedNotReleased());
        logger.info("FilesMigratedNotItemOrComponent " + statistic.getFilesMigratedNotItemOrComponent());
        logger.info("FilesMigratedNotUpdated         " + statistic.getFilesMigratedNotUpdated());
        logger.info("FilesErrorOccured               " + statistic.getFilesErrorOccured());
        logger.info("FilesMigrationDone              " + statistic.getFilesMigrationDone());
        logger.info("***********************");
        logger.info("TotalNumberOfPidsRequested      " + statistic.getTotalNumberOfPidsRequested());
        logger.info("***********************");
        System.out.println("ErrorList                " + statistic.getErrorList());
        assertTrue(statistic.getFilesTotal() == (statistic.getFilesMigratedNotItemOrComponent() + statistic.getFilesMigratedNotReleased()
                                         + statistic.getFilesMigratedNotUpdated() + statistic.getFilesMigrationDone()));
        assertTrue(statistic.getTotalNumberOfPidsRequested() > 5000);
        
        assertTrue(new Validator().checkAfterMigration(new File("C:/Test/qa-coreservice/2008"), statistic.getErrorList()));
    }
    
    @Test
    @Ignore
    public void transformQa2009() throws Exception
    {
        FileUtils.deleteDirectory(new File("C:/Test/qa-coreservice/2009"));
        FileUtils.copyDirectory(new File("C:/Test/qa-coreservice/2009_sav"), 
                new File("C:/Test/qa-coreservice/2009"));
        PIDMigrationManager mgr = new PIDMigrationManager(new File("C:/Test/qa-coreservice/2009"));        
        MigrationStatistic statistic = mgr.getMigrationStatistic();
        logger.info("FilesMigratedTotal              " + statistic.getFilesTotal());
        logger.info("FilesMigratedNotReleased        " + statistic.getFilesMigratedNotReleased());
        logger.info("FilesMigratedNotItemOrComponent " + statistic.getFilesMigratedNotItemOrComponent());
        logger.info("FilesMigratedNotUpdated         " + statistic.getFilesMigratedNotUpdated());
        logger.info("FilesErrorOccured               " + statistic.getFilesErrorOccured());
        logger.info("FilesMigrationDone              " + statistic.getFilesMigrationDone());
        logger.info("***********************");
        logger.info("TotalNumberOfPidsRequested      " + statistic.getTotalNumberOfPidsRequested());
        assertTrue(statistic.getFilesTotal() == (statistic.getFilesMigratedNotItemOrComponent() + statistic.getFilesMigratedNotReleased()
                                         + statistic.getFilesMigratedNotUpdated() + statistic.getFilesMigrationDone()));
        assertTrue(statistic.getTotalNumberOfPidsRequested() > 20000);
        
        assertTrue(new Validator().checkAfterMigration(new File("C:/Test/qa-coreservice/2009"), statistic.getErrorList()));
    }
    
    @Test
    @Ignore
    public void transformQa2010() throws Exception
    {
        FileUtils.deleteDirectory(new File("C:/Test/qa-coreservice/2010"));
        FileUtils.copyDirectory(new File("C:/Test/qa-coreservice/2010_sav"), 
                new File("C:/Test/qa-coreservice/2010"));
        PIDMigrationManager mgr = new PIDMigrationManager(new File("C:/Test/qa-coreservice/2010"));        
        MigrationStatistic statistic = mgr.getMigrationStatistic();
        logger.info("FilesMigratedTotal              " + statistic.getFilesTotal());
        logger.info("FilesMigratedNotReleased        " + statistic.getFilesMigratedNotReleased());
        logger.info("FilesMigratedNotItemOrComponent " + statistic.getFilesMigratedNotItemOrComponent());
        logger.info("FilesMigratedNotUpdated         " + statistic.getFilesMigratedNotUpdated());
        logger.info("FilesErrorOccured               " + statistic.getFilesErrorOccured());
        logger.info("FilesMigrationDone              " + statistic.getFilesMigrationDone());
        logger.info("***********************");
        logger.info("TotalNumberOfPidsRequested      " + statistic.getTotalNumberOfPidsRequested());
        assertTrue(statistic.getFilesTotal() == (statistic.getFilesMigratedNotItemOrComponent() + statistic.getFilesMigratedNotReleased()
                                         + statistic.getFilesMigratedNotUpdated() + statistic.getFilesMigrationDone()));
        assertTrue(statistic.getTotalNumberOfPidsRequested() > 20000);
        
        assertTrue(new Validator().checkAfterMigration(new File("C:/Test/qa-coreservice/2010"), statistic.getErrorList()));
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
        logger.info("FilesMigratedTotal              " + statistic.getFilesTotal());
        logger.info("FilesMigratedNotReleased        " + statistic.getFilesMigratedNotReleased());
        logger.info("FilesMigratedNotItemOrComponent " + statistic.getFilesMigratedNotItemOrComponent());
        logger.info("FilesMigratedNotUpdated         " + statistic.getFilesMigratedNotUpdated());
        logger.info("FilesErrorOccured               " + statistic.getFilesErrorOccured());
        logger.info("FilesMigrationDone              " + statistic.getFilesMigrationDone());
        logger.info("***********************");
        logger.info("TotalNumberOfPidsRequested      " + statistic.getTotalNumberOfPidsRequested());
        assertTrue(statistic.getFilesTotal() == (statistic.getFilesMigratedNotItemOrComponent() + statistic.getFilesMigratedNotReleased()
                                         + statistic.getFilesMigratedNotUpdated() + statistic.getFilesMigrationDone()));
        assertTrue(statistic.getTotalNumberOfPidsRequested() > 700);
        
        assertTrue(new Validator().checkAfterMigration(new File("C:/Test/qa-coreservice/2013"), statistic.getErrorList()));
    }
    
    
}
