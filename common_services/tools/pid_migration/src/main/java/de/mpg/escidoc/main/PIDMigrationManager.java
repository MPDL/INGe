package de.mpg.escidoc.main;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.mpg.escidoc.handler.PIDHandler;
import de.mpg.escidoc.handler.PreHandler;


public class PIDMigrationManager
{
    private static Logger logger = Logger.getLogger(PIDMigrationManager.class);   
    
    public void transform(File file) throws Exception
    {        
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        PreHandler preHandler = new PreHandler();
        PIDHandler handler = new PIDHandler(preHandler);
        handler.setPIDMigrationManager(this);
        
        File tempFile = File.createTempFile("xxx", "yyy", file.getParentFile());
        
        parser.parse(file, preHandler);
        parser.parse(file, handler);
        
        String result = handler.getResult();
        FileUtils.writeStringToFile(tempFile, result, "UTF-8");
        
        File bakFile = new File(file.getAbsolutePath() + ".bak");
        FileUtils.copyFile(file, bakFile, true);
        
        FileUtils.copyFile(tempFile, file);
        boolean b = bakFile.delete();    
        logger.debug("after delete bak file " + b);
    }
    
    public static void main(String[] args) throws Exception
    {       
        if (args[0] == null)
            throw new Exception("Start file or directory missing");
        File file = new File(args[0]);
        if (!file.exists())
            throw new Exception("file does not exists: " + file.getAbsolutePath());
        
        PIDMigrationManager pidMigr = new PIDMigrationManager();
        
        if (file.isDirectory())
        {
            for (File f : file.listFiles())
            {
                logger.info("****************** Starting migration of " + f.getCanonicalPath());
                pidMigr.transform(f);
                logger.info("****************** Ended    migration of " + f.getCanonicalPath());
            }
        }
        
    }
    
}
