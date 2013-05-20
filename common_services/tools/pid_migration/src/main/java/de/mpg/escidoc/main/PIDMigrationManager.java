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
import de.mpg.escidoc.util.Validator;


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
    
    public MigrationStatistic getMigrationStatistic()
    {
        return statistic;
    }

    public void transform(File file) throws Exception
    {   
        logger.info("****************** Start transforming " + file.getName());
        
        statistic.incrementFilesTotal();
        
        if ((statistic.getFilesTotal() % 1000) == 0 )
        {
            logger.info("***********************************************************");
            logger.info("****************** Number of files already done " + statistic.getFilesTotal());
            logger.info("***********************************************************");
        }
        
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
        
        logger.info("FilesTotal                      " + statistic.getFilesTotal());
        logger.info("FilesMigratedNotReleased        " + statistic.getFilesMigratedNotReleased());
        logger.info("FilesMigratedNotItemOrComponent " + statistic.getFilesMigratedNotItemOrComponent());
        logger.info("FilesMigratedNotUpdated         " + statistic.getFilesMigratedNotUpdated());
        logger.info("FilesErrorOccured               " + statistic.getFilesErrorOccured());
        logger.info("FilesMigrationDone              " + statistic.getFilesMigrationDone());
        logger.info("TotalNumberOfPidsRequested      " + statistic.getTotalNumberOfPidsRequested());
        long s = statistic.getTimeUsed();
        logger.info("TimeUsed                        " + String.format("%d:%02d:%02d", s/3600, (s%3600)/60, (s%60)));
        
        System.exit(1);
    }
    
    static private void printUsage(String message)
    {
        System.out.print("***** " + message + " *****");
        System.out.print("Usage: ");
        System.out.println("java "  + " [-transform|-validate|-transformvalidate] rootDir");
        System.out.println("  -transform\t\tTransform the foxmls");
        System.out.println("  -validate\t\tValidate the (transformed) foxmls");
        System.out.println("  -transformvalidate\t\tTransform and validate the (transformed) foxmls in a single step.");
        System.out.println("  <rootDir>\tThe root directory to start pid migration from");

        System.exit(-1);
    }
    
    public static void main(String[] args) throws Exception
    {  
        String mode = args[0];
        
        if (mode == null || (!mode.contains("transform") && !mode.contains("validate")))
                printUsage("Invalid mode parameter.");
        
        String rootDirName = args[1];
        if (rootDirName == null)
            printUsage("Invalid root directory parameter.");
        
        File rootDir = new File(rootDirName);
        if (!rootDir.exists())
            printUsage("Directory or file does not exists: " + rootDir.getAbsolutePath() + "\n");
        
        int totalNumberOfFiles = Util.countFilesInDirectory(rootDir);
        logger.info("Total number of files to migrate <" + totalNumberOfFiles + "> for directory <"  + rootDir.getName() + ">");
        
        MigrationStatistic statistic = new MigrationStatistic(); 
        
        if (mode.contains("transform"))
        {
            PIDMigrationManager pidMigr = new PIDMigrationManager(rootDir);
            statistic = pidMigr.getMigrationStatistic();
            logger.info("FilesTotal                      " + statistic.getFilesTotal());
            logger.info("FilesMigratedNotReleased        " + statistic.getFilesMigratedNotReleased());
            logger.info("FilesMigratedNotItemOrComponent " + statistic.getFilesMigratedNotItemOrComponent());
            logger.info("FilesMigratedNotUpdated         " + statistic.getFilesMigratedNotUpdated());            
            logger.info("FilesErrorOccured               " + statistic.getFilesErrorOccured());
            logger.info("FilesMigrationDone              " + statistic.getFilesMigrationDone());
            logger.info("TotalNumberOfPidsRequested      " + statistic.getTotalNumberOfPidsRequested());           
            long s = statistic.getTimeUsed();
            logger.info("TimeUsed                        " + String.format("%d:%02d:%02d", s/3600, (s%3600)/60, (s%60)));
            logger.info("ErrorList                       " + statistic.getErrorList());
        }
        if(mode.contains("validate"))
        {
            Validator validator = new Validator();
            
            validator.checkAfterMigration(rootDir, statistic.getErrorList());
        }
        
        
        
        
    }

}
