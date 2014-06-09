import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashSet;

import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.escidoc.main.AllPidsCheckManager;


public class TestAllPidsCheckManager
{
	static AllPidsCheckManager allPidsCheckManager = null;
    
    @BeforeClass
    public static void init()
    {
    	allPidsCheckManager = new AllPidsCheckManager();
    }
    
    @Test
    public void testCreateOrCorrectList() throws Exception
    {
    	allPidsCheckManager.createOrCorrectSet(new HashSet<String>());
        
        assertTrue(new File("./allPids.txt").exists());
    }
}
