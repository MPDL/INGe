package de.mpg.escidoc.pid;




import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.util.HandleUpdateStatistic;


// Mock object - writes the pids to be updated with its new registerUrl to a file.
public class PidProviderMock extends AbstractPidProvider
{
    public PidProviderMock() throws Exception
    {
        this.init();
    }
    
    public int updatePid(String pid, String itemId, HandleUpdateStatistic statistic)
    {
        logger.debug("updatePid starting");
        
        if ("".equals(itemId))
        {
            statistic.incrementHandlesNotFound();
            successMap.put(pid, "NOT USED");
            return 0;
        }
        
        String newUrl = "";
        try
        {
            newUrl = getRegisterUrl(itemId);
        }
        catch (Exception e)
        {
            failureMap.put(pid, newUrl);
            statistic.incrementHandlesUpdateError();
            return -1;
        }

        successMap.put(pid, newUrl);
        statistic.incrementHandlesUpdated();

        return 0;
    }

    private String getRegisterUrl(String itemId) throws Exception
    {
        String registerUrl =  PropertyReader.getProperty("escidoc.pubman.instance.url") +
                PropertyReader.getProperty("escidoc.pubman.instance.context.path") + itemId;
                
        return registerUrl;
    }
}

