package de.mpg.escidoc.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import de.mpg.escidoc.handler.AssertionHandler;
import de.mpg.escidoc.handler.PreHandler;
import de.mpg.escidoc.handler.PreHandler.Status;
import de.mpg.escidoc.handler.PreHandler.Type;

public class Validator
{
    private static Logger logger = Logger.getLogger(Validator.class);
    
    public boolean checkAfterMigration(File file) throws Exception
    {
        return checkAfterMigration(file, new ArrayList<String>());
    }

    public boolean checkAfterMigration(File file, Collection<String> ignoreList)
    {
        logger.info("checkAfterMigration file <" + file.getName() + ">");
        if (file != null && file.isFile())
        {
            if (ignoreList.contains(file.getName()))
            {
                   return true;
            }

            try
            {
                return validateFile(file);
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                logger.warn("Problem occured in validate <" + file.getName() + ">");
                return false;
            }
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
                
                try
                {
                    return validateFile(f);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    logger.warn("Problem occured in validate <" + file.getName() + ">");
                    continue;
                }
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
