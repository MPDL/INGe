package de.mpg.escidoc.handler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.mpg.escidoc.main.ComponentPidTransformer;
import de.mpg.escidoc.main.PIDProviderIf;
import de.mpg.escidoc.util.Util;

public class PIDProviderMock implements PIDProviderIf
{
    private static Logger logger = Logger.getLogger(PIDProviderMock.class);  
    private static int count;
    
    private Properties properties = new Properties();
    
    @Override
    public void init() throws IOException
    {    
    	logger.debug("init starting");
        
        InputStream s = getClass().getClassLoader().getResourceAsStream(ComponentPidTransformer.PROPERTY_FILE_NAME);
		
		if (s != null)
		{
			properties.load(s);
			logger.info(properties.toString());
		}
		else 
		{
			throw new FileNotFoundException("Property file not found " + ComponentPidTransformer.PROPERTY_FILE_NAME);
		}
    }

	@Override
	public String updateComponentPid(String itemId, String versionNumber, String componentId,
			String pid, String fileName) throws PIDProviderException
	{
		logger.info("UpdateComponentPid itemId <" + itemId
				+ "> versionNumber <" + versionNumber + "> componentId <"
				+ Util.getPureComponentId(componentId) + "> pid <" + pid
				+ "< fileName < " + fileName + ">");

		String registerUrl = properties.getProperty("pubman.instance.url")
				
				+ properties.getProperty("pubman.instance.context.path")
						
				+ properties.getProperty("pubman.component.pattern")					
						.replaceAll("\\$1", itemId)
						.replaceAll("\\$2", versionNumber)
						.replaceAll("\\$3", Util.getPureComponentId(componentId))		
						.replaceAll("\\$4", fileName);
		count++;

		logger.info("URL given to PID resolver: <" + registerUrl + ">");

		return registerUrl;
								
	}

	@Override
	public int getTotalNumberOfPidsRequested()
	{
		return count;
	}
  

}
