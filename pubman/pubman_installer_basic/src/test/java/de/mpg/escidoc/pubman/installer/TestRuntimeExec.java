package de.mpg.escidoc.pubman.installer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;

import org.junit.Test;

public class TestRuntimeExec
{
    @Test
    public void test()
    {
        try
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
                cmd[0] = "command.com" ;
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
            
            Thread.currentThread().sleep(10000);
             
        } catch (Throwable t)
          {
            t.printStackTrace();
          }
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
