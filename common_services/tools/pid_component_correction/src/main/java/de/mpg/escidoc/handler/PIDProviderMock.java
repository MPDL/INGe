package de.mpg.escidoc.handler;

import org.apache.log4j.Logger;

import de.mpg.escidoc.main.PIDProviderIf;
import de.mpg.escidoc.util.Util;

public class PIDProviderMock implements PIDProviderIf
{
    private static Logger logger = Logger.getLogger(PIDProviderMock.class);  
    private static int totalNumberofPidsRequested = 0;
    
    static int count;
    
    @Override
    public void init()
    {       
    }

    @Override
    public int getTotalNumberOfPidsRequested()
    {
        return totalNumberofPidsRequested;
    }

	@Override
	public String updateComponentPid(String escidocId, String versionNumber, String componentId,
			String pid, String fileName) throws PIDProviderException
	{
		logger.info("UpdateComponentPid escidocId <" + escidocId + "> versionNumber <" + versionNumber
				+ "> componentId <" + Util.getPureComponentId(componentId) + "> pid <" + pid + "< fileName < " + fileName + ">");
		
		totalNumberofPidsRequested++;
		
		return "";
													
	}
  

}
