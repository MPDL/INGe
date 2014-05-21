import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.escidoc.main.LdhCheckManager;


public class TestLdhCheckManager
{
    static LdhCheckManager ldhCheckManager = null;
    
    @BeforeClass
    public static void init()
    {
        ldhCheckManager = new LdhCheckManager();
    }
    @Test
    public void testGenerateOrCorrectList() throws Exception
    {
        ldhCheckManager.generateOrCorrectList(new ArrayList<String>());
        
        assertTrue(new File("./allLocators.txt").exists());
    }
}
