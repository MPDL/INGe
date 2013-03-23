package de.mpg.escidoc.main;

import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.escidoc.handler.AssertionHandler;
import de.mpg.escidoc.handler.PreHandler;

public class PIDMigrationManagerTest
{
    private static Logger logger = Logger.getLogger(PIDMigrationManagerTest.class);   
    
    private PIDMigrationManager mgr = new PIDMigrationManager();
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        org.apache.log4j.BasicConfigurator.configure();
        
        FileUtils.copyFile(new File("src/test/resources/item/escidoc_1479027.sav"), 
                new File("src/test/resources/item/escidoc_1479027"));
        FileUtils.copyFile(new File("src/test/resources/component/escidoc_418001.sav"), 
                new File("src/test/resources/component/escidoc_418001"));
    }
    
    @Test
    public void transformFiles() throws Exception
    {
        File f = new File("src/test/resources/item/escidoc_1479027");
        mgr.transform(f);        
        assertTrue(checkAfterMigration(f));
        
        mgr.transform(new File("src/test/resources/component/escidoc_418001"));        
        assertTrue(checkAfterMigration(new File("src/test/resources/component/escidoc_418001")));
    }
    
    @Test
    public void transformDirectory() throws Exception
    {
        File f = new File("src/test/resources/item");
        mgr.transform(f);        
        assertTrue(checkAfterMigration(f));
        
        mgr.transform(new File("src/test/resources/component/escidoc_418001"));        
        assertTrue(checkAfterMigration(new File("src/test/resources/component/escidoc_418001")));
    }

    private boolean checkAfterMigration(File file) throws Exception
    {
        logger.debug("checkAfterMigration file " + file.getAbsolutePath());
        
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        PreHandler preHandler = new PreHandler();
        AssertionHandler assertionHandler = new AssertionHandler(preHandler);
        
        parser.parse(file, preHandler);
        parser.parse(file, assertionHandler);
        
        return true;
    }
}
