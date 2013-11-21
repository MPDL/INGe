import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.escidoc.www.services.om.ContextHandler;
import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ProxyHelper;
import de.mpg.escidoc.services.framework.ServiceLocator;


public class ESciDocSoapStressTest extends Thread
{
    private static ItemHandler itemHandler;
    private static ContextHandler contextHandler;
    private HttpClient httpClient;
    private String handle;
    private String location;
    private String itemComponent;
    private String itemContent;
    private String item;
    
    private static Logger logger = Logger.getLogger(ESciDocSoapStressTest.class);
    
    private static int numThreads = 50;
    
    private static String[][] itemsWithComponent = {
        {"escidoc:1808292","escidoc:1810312"},
        {"escidoc:1851340", "escidoc:1851344"},
        {"escidoc:720912:3", "escidoc:1569445"},
        };

    public ESciDocSoapStressTest(String handle) throws Exception
    {
        this.handle = handle;
        
        httpClient = new HttpClient();
        httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        
        location = PropertyReader.getProperty("escidoc.framework_access.framework.url");
    }
    
    public void run()
    {
        HashMap<String, String[]> filterMap = new HashMap<String, String[]>();
        filterMap.put("operation", new String[] { "searchRetrive" });
        filterMap.put("version", new String[] { "1.1" });
        
        

        try
        {
            itemHandler = ServiceLocator.getItemHandler(handle);
            contextHandler = ServiceLocator.getContextHandler(handle);

            do
            {
                String contexts = contextHandler.retrieveContexts(filterMap);
                
                try
                {
                    for (String[] id : itemsWithComponent )
                    {                        
                        item = itemHandler.retrieve(id[0]);
                        itemComponent = itemHandler.retrieveComponents(id[0]);
                        //itemComponent = itemHandler.retrieveComponent(id[0], id[1]);
                        String url = location + "/ir/item/" + id[0]  + "/components/component/" + id[1] + "/content";  
                        logger.info(this.getName() + " " + url);
                        GetMethod method = new GetMethod(url);
                        ProxyHelper.executeMethod(httpClient, method);
                        
                    } 
                }
                catch (Exception e)
                {                    
                    continue;
                }
                
            } while(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
    public static void main (String[] args) throws Exception
    {
      Login login = new Login();
      String userHdl = login.loginSystemAdministrator();
      
      for(int i = 0; i < numThreads; i++)
      {
          ESciDocSoapStressTest thread = new ESciDocSoapStressTest(userHdl);
          thread.setName("Thread " + i);
          thread.start();
      }
      
      Thread.currentThread().wait(10000);
      
    }
}    
