package de.mpg.escidoc.pubman.installer;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Before;
import org.junit.Test;

import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ProxyHelper;

public class TestRuntimeExec
{
    @Before
    public void setUp() throws Exception
    {
        PropertyReader.setProperty(Configuration.KEY_INSTANCEURL, "http://localhost:8080");
    }
    
    @Test
    public void test() throws Exception
    {
        this.startEscidoc();
        int code = waitForFrameworkStarted();
        
        assertTrue(code == 200);
    }

    private void startEscidoc() throws Exception
    {
       
            String osName = System.getProperty("os.name" );
            String[] cmd = new String[3];
            if( osName.equals( "Windows 7" ) )
            {
                cmd[0] = "cmd" ;
                cmd[1] = "/C" ;
                cmd[2] = "run.bat";
            }
            else if( osName.equals( "Linux" ) )
            {
                cmd[0] = "bash.sh" ;
                cmd[1] = "/C" ;
                cmd[2] = "run.sh";
            }
            
            ProcessBuilder pb =
                  new ProcessBuilder("cmd", "/c", "run.bat");
                  
                  pb.directory(new File("c:/escidoc.pubman/jboss/bin"));
                  File log = new File("c:/escidoc.pubman/jboss/server/default/log/server.log");
                  pb.redirectErrorStream(true);
                  
            Process process = pb.start();
            
            StreamGobbler g = new StreamGobbler(process.getInputStream());
            g.start();            
    }
    
    private int waitForFrameworkStarted() throws HttpException, IOException, URISyntaxException
    {
        GetMethod method = new GetMethod(PropertyReader.getProperty(Configuration.KEY_INSTANCEURL));        
        HttpClient client = new HttpClient();
        
        client.getHttpConnectionManager().getParams().setConnectionTimeout(3*60*1000);
        ProxyHelper.executeMethod(client, method);
        
        return method.getStatusCode();
    }
    
    private void copy(InputStream in, OutputStream out) throws IOException {
        while (true) {
            int c = in.read();
            if (c == -1) {
                break;
            }
            out.write((char) c);
        }
    }

    class StreamGobbler extends Thread
    {
        InputStream is;
        String type;
        
        StreamGobbler(InputStream is)
        {
            this.is = is;
        }
        
        public void run()
        {
            try
            {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null)
                    System.out.println(type + ">" + line);
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
        }
    }
}
