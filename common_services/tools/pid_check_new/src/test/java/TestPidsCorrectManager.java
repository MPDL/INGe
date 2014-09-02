import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashSet;

import org.junit.BeforeClass;
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
    public void testCreateOrCorrectList() throws Exception
    {
        missingPidsCorrectManager.createOrCorrectSet(new HashSet<String>());
        
        assertTrue(new File("./allPids.txt").exists());
    }
}
