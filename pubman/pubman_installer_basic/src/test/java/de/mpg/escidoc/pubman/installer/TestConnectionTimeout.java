package de.mpg.escidoc.pubman.installer;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;

import de.mpg.escidoc.services.framework.ProxyHelper;

public class TestConnectionTimeout
{
    @Test
    public void test()
    {
            GetMethod method = new GetMethod("http://dev-pubman.mpdl.mpg.de:8080");
            HttpClient client = new HttpClient();
            client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
           
                try
                {
                    ProxyHelper.executeMethod(client, method);
                }
                catch (HttpException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return;
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return;
                }

            
            if (method.getStatusCode() == 200)
            {
                return;
            }
            return;
    }
}
