package de.mpg.escidoc.handler;

import java.io.IOException;

import org.apache.commons.httpclient.HttpException;

import de.mpg.escidoc.main.PIDProviderIf;

public class PIDProviderMock implements PIDProviderIf
{
    static int count;

    /** As two pids are needed pro item (one for object pid, one for version pid, return alternatly the following two pids */
    public String getPid() throws HttpException, IOException
    {
        count++;
        if (count % 2 == 0)
            return "hdl:12345/00-001Z-0000-000E-1111-1";
        else
            return "hdl:12345/00-001Z-0000-000E-2222-2";
    }

    @Override
    public void init()
    {
        
    }
}
