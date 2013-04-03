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
        File rootDir = new File(args[0]);
        if (!rootDir.exists())
            throw new Exception("file does not exists: " + rootDir.getAbsolutePath());
        
        PIDMigrationManager pidMigr = new PIDMigrationManager(rootDir);
        
    }
    
}
