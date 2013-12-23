package de.mpg.escidoc.pubman.installer.panels;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.izforge.izpack.installer.InstallData;

import de.mpg.escidoc.services.citationmanager.utils.ResourceUtil;

public class JUnitInstallData extends InstallData
{
    private static Logger logger = Logger.getLogger(JUnitInstallData.class);
    
    Properties props;
    
    public JUnitInstallData() throws FileNotFoundException, IOException
    {
        this.props = new Properties();
        this.init();
    }
    
    private void init() throws InvalidPropertiesFormatException, FileNotFoundException, IOException
    {       
        props.loadFromXML(ResourceUtil.getResourceAsStream("variables.xml"));
        
        logger.info(props);
    }
    
    public String getVariable(String key)
    {
        return props.getProperty(key);
    }
    
    
}
