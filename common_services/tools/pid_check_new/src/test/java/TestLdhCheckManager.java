import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashSet;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.main.LdhCheckManager;


public class TestLdhCheckManager
{
    static LdhCheckManager ldhCheckManager = null;
    
    @BeforeClass
    public static void init() throws Exception
    {
        ldhCheckManager = new LdhCheckManager();
    }
    @Test
    @Ignore
    public void testCreateOrCorrectList() throws Exception
    {
        ldhCheckManager.createOrCorrectSet(new HashSet<String>());
        
        assertTrue(new File("./allLocators.txt").exists());
    }
}
