package de.mpg.escidoc.handler;

import org.apache.log4j.Logger;

import de.mpg.escidoc.handler.PreHandler.Type;
import de.mpg.escidoc.main.PIDProviderIf;
import de.mpg.escidoc.util.SQLQuerier;
import de.mpg.escidoc.util.Util;

public class PIDProviderMock implements PIDProviderIf
{
    private static Logger logger = Logger.getLogger(PIDProviderMock.class);  
    
    static int count;

    
    public String getPid(String escidocId, Type type, String fileName) throws PIDProviderException
    {
        logger.debug("getPid starting");

        String registerUrl = "";
       
        
        try
        {
            if (type.equals(Type.ITEM))
            {
                registerUrl = getRegisterUrlForItem(escidocId);
            }
            else if (type.equals(Type.COMPONENT))
            {
                registerUrl = getRegisterUrlForComponent(escidocId, fileName);
            }
        }
        catch (Exception e)
        {
            logger.warn("Error occured when registering Url for <" + escidocId + ">" 
                                    + " of type <" + type + ">"  + " and fileName <" + fileName + ">" );
            throw new PIDProviderException(e);
        }
        
        count++;
        if (count % 10 == 1)
            return "hdl:12345/00-001Z-0000-000E-1111-1";
        else if (count % 10 == 2)
            return "hdl:12345/00-001Z-0000-000E-2222-2";
        else if (count % 10 == 3)
            return "hdl:12345/00-001Z-0000-000E-3333-3";
        else if (count % 10 == 4)
            return "hdl:12345/00-001Z-0000-000E-4444-4";
        else if (count % 10 == 5)
            return "hdl:12345/00-001Z-0000-000E-5555-5";
        else if (count % 10 == 6)
            return "hdl:12345/00-001Z-0000-000E-6666-6";
        else if (count % 10 == 7)
            return "hdl:12345/00-001Z-0000-000E-7777-7";
        else if (count % 10 == 8)
            return "hdl:12345/00-001Z-0000-000E-8888-8";
        else if (count % 10 == 9)
            return "hdl:12345/00-001Z-0000-000E-9999-9";
        else if (count % 10 == 0)
            return "hdl:12345/00-001Z-0000-000E-0000-0";
        
        return "hdl:12345/00-001Z-0000-000E-6789-0";
            
    }
    
    private String getRegisterUrlForItem(String itemId)
    {
        String registerUrl =  Util.getProperty("escidoc.pubman.instance.url") +
                Util.getProperty("escidoc.pubman.instance.context.path") +
                Util.getProperty("escidoc.pubman.item.pattern").replaceAll("\\$1", itemId);
        return registerUrl;
    }
    
    private String getRegisterUrlForComponent(String componentId, String fileName) throws Exception
    {
        SQLQuerier querier = new  SQLQuerier();
        String itemId = querier.getItemIdForComponent(componentId);
        
        String registerUrl =  Util.getProperty("escidoc.pubman.instance.url") +
                Util.getProperty("escidoc.pubman.instance.context.path") +
                Util.getProperty("escidoc.pubman.component.pattern")
                        .replaceAll("\\$1", itemId)
                        .replaceAll("\\$2", componentId)
                        .replaceAll("\\$3", fileName);

        logger.debug("URL given to PID resolver: " + registerUrl);
        
        return registerUrl;
    }

    @Override
    public void init()
    {
        
    }
}
