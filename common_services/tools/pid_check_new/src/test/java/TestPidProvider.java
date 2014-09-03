import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.pid.PidProvider;
import de.mpg.escidoc.util.HandleUpdateStatistic;


public class TestPidProvider
{
    static PidProvider pidProvider;
    static HandleUpdateStatistic updateStatistic = new HandleUpdateStatistic();
    static HandleUpdateStatistic verifyStatistic = new HandleUpdateStatistic();
    
    // handle occurs on dev-pubman
    @Test
    @Ignore
    public void testUpdatePid() throws Exception
    {
        pidProvider = new PidProvider();
        updateStatistic.clear();
        
        int code = pidProvider.updatePid("11858/00-001Z-0000-0023-673A-F", "/item/escidoc:672822:1", updateStatistic);
        
        assertTrue(code == HttpStatus.SC_OK);
        
        pidProvider.storeResults(updateStatistic);
        
        assertTrue((new File(pidProvider.getLatestSuccessFile())).exists());
        assertTrue((new File(pidProvider.getLatestFailureFile())).exists());
        
        assertTrue(FileUtils.readFileToString((new File(pidProvider.getLatestSuccessFile()))).contains("11858/00-001Z-0000-0023-673A-F"));
        assertTrue(FileUtils.readFileToString((new File(pidProvider.getLatestSuccessFile()))).contains("/item/escidoc:672822:1"));
        assertTrue(!FileUtils.readFileToString((new File(pidProvider.getLatestFailureFile()))).contains("11858/00-001Z-0000-0023-673A-F"));    
        assertTrue(!FileUtils.readFileToString((new File(pidProvider.getLatestFailureFile()))).contains("/item/escidoc:672822:1"));
    }
    
    @Test
    @Ignore
    public void testUpdatePidLive() throws Exception
    {
        pidProvider = new PidProvider();
        
        int code = pidProvider.updatePid("11858/00-001M-0000-0013-B522-3", "/item/escidoc:1787368", updateStatistic);
        
        assertTrue(code == HttpStatus.SC_OK);
       
        pidProvider.storeResults(updateStatistic);
        
        assertTrue((new File(pidProvider.getLatestSuccessFile())).exists());
        assertTrue(!(new File(pidProvider.getLatestFailureFile())).exists());
        
        assertTrue(FileUtils.readFileToString((new File(pidProvider.getLatestSuccessFile()))).contains("11858/00-001M-0000-0013-B522-3"));
        assertTrue(FileUtils.readFileToString((new File(pidProvider.getLatestSuccessFile()))).contains("/item/escidoc:1787368"));
    }
    
    @Test
    @Ignore
    public void testVerifyPidLive() throws Exception
    {
        pidProvider = new PidProvider();
        
        int code = pidProvider.checkToResolvePid("11858/00-001M-0000-0013-B1A4-0", verifyStatistic);        
        assertTrue(code == HttpStatus.SC_OK);
        
        pidProvider.storeResults(updateStatistic);
        
        assertTrue((new File(pidProvider.getLatestSuccessFile())).exists());
        assertTrue(!(new File(pidProvider.getLatestFailureFile())).exists());
        
        assertTrue(FileUtils.readFileToString((new File(pidProvider.getLatestSuccessFile()))).contains("11858/00-001M-0000-0013-B1A4-0"));       
    }
    
    @Test
    public void testVerifyPidLiveDoesNotExists() throws Exception
    {
        pidProvider = new PidProvider();
        
        int code = pidProvider.checkToResolvePid("11858/00-001M-0000-0013-XXXX-0", verifyStatistic);
        assertTrue(code != HttpStatus.SC_OK);
        
        pidProvider.storeResults(updateStatistic);
        
        assertTrue((new File(pidProvider.getLatestFailureFile())).exists());
        
        assertTrue(FileUtils.readFileToString((new File(pidProvider.getLatestFailureFile()))).contains("11858/00-001M-0000-0013-XXXX-0"));       
    }
}
