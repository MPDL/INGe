package de.mpg.escidoc.main;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import javax.naming.NamingException;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class PIDMigrationManagerTest
{
    private static Logger logger = Logger.getLogger(PIDMigrationManagerTest.class);   
    
    private PIDMigrationManager mgr = new PIDMigrationManager();
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        FileUtils.copyFile(new File("src/test/resources/item/escidoc_1479027.sav"), new File("src/test/resources/item/escidoc_1479027"));
        FileUtils.copyFile(new File("src/test/resources/component/escidoc_418001.sav"), new File("src/test/resources/component/escidoc_418001"));
    }
    
    @Before
    public void setup()
    {
        org.apache.log4j.BasicConfigurator.configure();
        
        try
        {
            mgr.init();
        }
        catch (NamingException e)
        {
            fail(e.getMessage());
        }
    }
    
    @Test
    @Ignore
    public void getPid()
    {
        String pid = null;
        try
        {
            pid = mgr.getPid();
        }
        catch (HttpException e)
        {
            fail(e.getMessage());
        }
        catch (IOException e)
        {
            fail(e.getMessage());
        }
    
        assertTrue(pid != null);

    }
    
    @Test
    public void transform() throws Exception
    {
        File f = new File("src/test/resources/item/escidoc_1479027");
        mgr.transform(f);        
        assertTrue(checkAfterMigration(f));
        
        /*mgr.transform(new File("src/test/resources/component/escidoc_418001"));        
        assertTrue(checkAfterMigration(new File("src/test/resources/component/escidoc_418001")));*/
    }

    private boolean checkAfterMigration(File file) throws IOException
    {
        logger.debug("checkAfterMigration file " + file.getAbsolutePath());
        String fileAsString = FileUtils.readFileToString(file);
        logger.debug("string " + fileAsString);
        int idx = fileAsString.lastIndexOf("RELS-EXT");
        String s = fileAsString.substring(idx);
        logger.debug("string " + fileAsString);
        return(!s.contains("hdl:someHandle/test"));
    }
}
