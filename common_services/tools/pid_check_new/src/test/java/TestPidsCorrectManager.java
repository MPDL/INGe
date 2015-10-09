import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashSet;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.main.MissingPidsCorrectManager;


public class TestPidsCorrectManager
{
static MissingPidsCorrectManager missingPidsCorrectManager = null;
    
    @BeforeClass
    public static void init() throws Exception
    {
        missingPidsCorrectManager = new MissingPidsCorrectManager();
    }
    
    @Test
    @Ignore
    public void testCreateOrCorrectList() throws Exception
    {
        missingPidsCorrectManager.createOrCorrectSet(new HashSet<String>());        
        assertTrue(new File("./allPids.txt").exists());
    }
    
    @Test
    public void testGetLastModificationDate()
    {
    	String itemXml = "xlink:href=\"/ir/item/escidoc:2056556\" last-modification-date=\"2014-10-29T08:23:04.867Z\" > ";
    	
    	assertTrue(MissingPidsCorrectManager.getLastModificationDate(itemXml).equals("2014-10-29T08:23:04.867Z"));
    }
}
