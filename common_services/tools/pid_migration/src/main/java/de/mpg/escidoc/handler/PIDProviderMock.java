package de.mpg.escidoc.handler;

import java.io.IOException;

import org.apache.commons.httpclient.HttpException;

import de.mpg.escidoc.handler.PreHandler.Type;
import de.mpg.escidoc.main.PIDProviderIf;

public class PIDProviderMock implements PIDProviderIf
{
    static int count;

    public String getPid(String id, Type type) throws HttpException, IOException
    {
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

    @Override
    public void init()
    {
        
    }
}
