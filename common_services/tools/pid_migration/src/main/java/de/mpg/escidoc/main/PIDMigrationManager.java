package de.mpg.escidoc.main;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.mpg.escidoc.handler.PIDHandler;
import de.mpg.escidoc.handler.PIDProviderException;
import de.mpg.escidoc.handler.PreHandler;
import de.mpg.escidoc.handler.PreHandler.Status;
import de.mpg.escidoc.handler.PreHandler.Type;
import de.mpg.escidoc.util.MigrationStatistic;
import de.mpg.escidoc.util.Util;


public class PIDMigrationManager
{
    private static Logger logger = Logger.getLogger(PIDMigrationManager.class);   
    
    private static MigrationStatistic statistic = new MigrationStatistic();
    
    private String actualFileName = "";
    
    
    public PIDMigrationManager(File rootDir) throws Exception
    {
        if (rootDir != null && rootDir.isFile())
        {
            transform(rootDir);
            return;
        }
        
        File[] files = rootDir.listFiles();
        
        Collections.sort(Arrays.asList(files));
        
        for (File file : files)
        {
            if (file.getName().endsWith(".svn"))
                continue;
            if (file.isDirectory() && !file.getName().startsWith(".svn"))
            {
                new PIDMigrationManager(file);
            }
            else
            {
                transform(file);
            }
        }
    }
    
    public static MigrationStatistic getMigrationStatistic()
    {
        return statistic;
    }

    public void transform(File file) throws Exception
    {   
        logger.info("****************** Start transforming " + file.getName());
        
        statistic.incrementFilesMigratedTotal();
        
        actualFileName = file.getName();
        
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        PreHandler preHandler = new PreHandler();
        PIDHandler handler = new PIDHandler(preHandler);
        handler.setPIDMigrationManager(this);
        
        parser.parse(file, preHandler);
        
        // only migrate items and components
        if ((preHandler.getObjectType()== null) || !(preHandler.getObjectType().equals(Type.ITEM) || preHandler.getObjectType().equals(Type.COMPONENT)))
        {
            logger.info("Nothing to do for file <" + file.getName() + "> because of type <" + preHandler.getObjectType() + ">");
            logger.info("****************** End   transforming " + file.getName());
            statistic.incrementFilesMigratedNotItemOrComponent();
            return;
        }
        // do nothing for items in public-status != released
        if (preHandler.getObjectType().equals(Type.ITEM) && !(preHandler.getPublicStatus().equals(Status.RELEASED)))
        {
            logger.info("Nothing to do for file <" + file.getName() + "> because of public-status <" + preHandler.getPublicStatus() + ">");
            logger.info("****************** End   transforming " + file.getName());
            statistic.incrementFilesMigratedNotReleased();
            return;
        }
        parser.parse(file, handler);
        
        if (!handler.isUpdateDone())
        {
            logger.info("No update done for file <" + file.getName() + ">");
            logger.info("****************** End   transforming " + file.getName());
            statistic.incrementFilesMigratedNotUpdated();
            return;
        }
        
        statistic.incrementFilesMigrationDone();
        statistic.setPidsRequested(handler.getTotalNumberOfPidsRequested());
        
        String result = handler.getResult();
        
        File tempFile = File.createTempFile("xxx", "yyy", file.getParentFile());
        
        FileUtils.writeStringToFile(tempFile, result, "UTF-8");
        File bakFile = new File(file.getAbsolutePath() + ".bak");
        FileUtils.copyFile(file, bakFile, true);        
        FileUtils.copyFile(tempFile, file);
        
        boolean b = bakFile.delete();
        if (!b)
        {
            logger.warn("Error when deleting " + bakFile.getName());
        }
        b = tempFile.delete();
        if (!b)
        {
            logger.warn("Error when deleting " + tempFile.getName());
        }
        logger.info("****************** End   transforming " + file.getName());
    }
    
    public void onError(PIDProviderException e)
    {
        logger.warn("Error getting PID " + e.getMessage(), e);
        statistic.incrementFilesErrorOccured();
        statistic.addToErrorList(actualFileName);
        
        // continue processing for deprecated components - otherwise stop
        if (e.getMessage().contains("No item was found"))
            return;
        
        System.out.println("FilesMigratedTotal              " + statistic.getFilesMigratedTotal());
        System.out.println("FilesMigratedNotReleased        " + statistic.getFilesMigratedNotReleased());
        System.out.println("FilesMigratedNotItemOrComponent " + statistic.getFilesMigratedNotItemOrComponent());
        System.out.println("FilesMigratedNotUpdated         " + statistic.getFilesMigratedNotUpdated());
        System.out.println("FilesErrorOccured               " + statistic.getFilesErrorOccured());
        System.out.println("TotalNumberOfPidsRequested      " + statistic.getTotalNumberOfPidsRequested());
        
        System.exit(1);
    }
    
    public static void main(String[] args) throws Exception
    {       
        if (args[0] == null)
            throw new Exception("Start file or directory missing");
        File rootDir = new File(args[0]);
        if (!rootDir.exists())
            throw new Exception("file does not exists: " + rootDir.getAbsolutePath());
        
        int totalNumberOfFiles = Util.countFilesInDirectory(rootDir);
        System.out.println("Total number of files to migrate <" + totalNumberOfFiles + "> for directory <"  + rootDir.getName() + ">");
        
        PIDMigrationManager pidMigr = new PIDMigrationManager(rootDir);      
        
        MigrationStatistic statistic = pidMigr.getMigrationStatistic();
        
        System.out.println("FilesMigratedTotal              " + statistic.getFilesMigratedTotal());
        System.out.println("FilesMigratedNotReleased        " + statistic.getFilesMigratedNotReleased());
        System.out.println("FilesMigratedNotItemOrComponent " + statistic.getFilesMigratedNotItemOrComponent());
        System.out.println("FilesMigratedNotUpdated         " + statistic.getFilesMigratedNotUpdated());
        System.out.println("FilesErrorOccured               " + statistic.getFilesErrorOccured());
        System.out.println("TotalNumberOfPidsRequested      " + statistic.getTotalNumberOfPidsRequested());
        System.out.println("ErrorList                       " + statistic.getErrorList());
        
        
    }

}
