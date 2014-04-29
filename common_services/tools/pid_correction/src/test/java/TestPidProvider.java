import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.pid.PidProvider;


public class TestPidProvider
{
    PidProvider pidProvider;
    
    @Test
    @Ignore
    public void testUpdatePid() throws Exception
    {
        pidProvider = new PidProvider();
        
        int code = pidProvider.updatePid("11858/00-001Z-0000-0023-673A-F", "/item/escidoc:672822:1");
        
        assertTrue(code == HttpStatus.SC_OK);
        
        pidProvider.storeResults();
        
        assertTrue((new File("./success")).exists());
        assertTrue((new File("./failure")).exists());
        
        assertTrue(FileUtils.readFileToString((new File("./success"))).contains("11858/00-001Z-0000-0023-673A-F"));
        assertTrue(FileUtils.readFileToString((new File("./success"))).contains("/item/escidoc:672822:1"));
        assertTrue(!FileUtils.readFileToString((new File("./failure"))).contains("11858/00-001Z-0000-0023-673A-F"));    
        assertTrue(!FileUtils.readFileToString((new File("./failure"))).contains("/item/escidoc:672822:1"));
    }
    
    @Test
    public void testVerifyPid() throws Exception
    {
        pidProvider = new PidProvider();
        
        int code = pidProvider.checkToResolvePid("11858/00-001Z-0000-0023-47DF-E");
        
        assertTrue(code == HttpStatus.SC_OK);
        
        pidProvider.storeResults();
        
        assertTrue((new File("./success")).exists());
        assertTrue((new File("./failure")).exists());
        
        assertTrue(FileUtils.readFileToString((new File("./success"))).contains("11858/00-001Z-0000-0023-47DF-E"));       
        assertTrue(!FileUtils.readFileToString((new File("./failure"))).contains("11858/00-001Z-0000-0023-47DF-E"));    
    }
    
    @AfterClass
    public static void tearDown()
    {
        FileUtils.deleteQuietly((new File("./success")));
        FileUtils.deleteQuietly((new File("./failure")));
    }
}
