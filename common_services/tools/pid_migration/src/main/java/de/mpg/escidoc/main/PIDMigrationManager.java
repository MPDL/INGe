package de.mpg.escidoc.main;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.mpg.escidoc.handler.PIDHandler;
import de.mpg.escidoc.handler.PreHandler;
import de.mpg.escidoc.handler.PreHandler.PublicStatus;
import de.mpg.escidoc.handler.PreHandler.Type;


public class PIDMigrationManager
{
    private static Logger logger = Logger.getLogger(PIDMigrationManager.class);   
    
    public PIDMigrationManager(File rootDir) throws Exception
    {
        if (rootDir != null && rootDir.isFile())
        {
            transform(rootDir);
            return;
        }
        
        File[] files = rootDir.listFiles();
        
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
    
    public void transform(File file) throws Exception
    {   
        logger.info("****************** Start transforming " + file.getName());
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        PreHandler preHandler = new PreHandler();
        PIDHandler handler = new PIDHandler(preHandler);
        handler.setPIDMigrationManager(this);
        
        parser.parse(file, preHandler);
        
        // only migrate items and components
        if (!(preHandler.getObjectType().equals(Type.ITEM) || preHandler.getObjectType().equals(Type.COMPONENT)))
        {
            logger.info("Nothing to do for file <" + file.getName() + "> because of type <" + preHandler.getObjectType() + ">");
            return;
        }
        // do nothing for items in public-status != released
        if (preHandler.getObjectType().equals(Type.ITEM) && !(preHandler.getPublicStatus().equals(PublicStatus.RELEASED)))
        {
            logger.info("Nothing to do for file <" + file.getName() + "> because of public-status <" + preHandler.getPublicStatus() + ">");
            return;
        }
        parser.parse(file, handler);
        
        if (!handler.isUpdateDone())
        {
            logger.info("No update done for file <" + file.getName() + ">");
            return;
        }
        
        String result = handler.getResult();
        
        File tempFile = File.createTempFile("xxx", "yyy", file.getParentFile());
        
        FileUtils.writeStringToFile(tempFile, result, "UTF-8");
        File bakFile = new File(file.getAbsolutePath() + ".bak");
        FileUtils.copyFile(file, bakFile, true);        
        FileUtils.copyFile(tempFile, file);
        
        boolean b = bakFile.delete();
        if (!b)
        {
            logger.warn("****************** Error when deleting " + bakFile.getName());
        }
        b = tempFile.delete();
        if (!b)
        {
            logger.warn("****************** Error when deleting " + tempFile.getName());
        }
        logger.info("****************** End transforming " + file.getName());
    }
    
    public static void main(String[] args) throws Exception
    {       
        if (args[0] == null)
            throw new Exception("Start file or directory missing");
        File rootDir = new File(args[0]);
        if (!rootDir.exists())
            throw new Exception("file does not exists: " + rootDir.getAbsolutePath());
        
        PIDMigrationManager pidMigr = new PIDMigrationManager(rootDir);
        
    }
    
}
