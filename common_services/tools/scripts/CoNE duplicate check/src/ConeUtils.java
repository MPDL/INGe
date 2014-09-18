import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;

import net.sf.saxon.dom.DocumentBuilderFactoryImpl;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.sun.imageio.plugins.common.InputStreamAdapter;


/**
 * 
 * Class with some static methods doing CoNE-Requests
 * 
 * @author walter
 *
 */
public class ConeUtils {

	private static final String coneUrl = "http://pubman.mpdl.mpg.de/cone/";
	
	/**
     * Queries the CoNE service and transforms the result into a DOM node.
     * 
     * @param model The type of object (e.g. "persons")
     * @param name The query string.
     * @return A DOM node containing the results.
     */
    public static List<String> printQueryConeCompleteName(String model, String name)
    {
    	if (Process.VERBOSE) { 
    		System.out.println("-------------------\nStarted Querying CoNE\n-------------------");
    	}
    	List<String> possibleDublicates = new ArrayList<String>();
        
        try
        {
            DocumentBuilder documentBuilder;
            documentBuilder = DocumentBuilderFactoryImpl.newInstance().newDocumentBuilder();
            String queryUrl = coneUrl + model + "/query?format=jquery&dc:title=" + URLEncoder.encode("\"" + name + "\"", "UTF-8") + "&n=0";
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(queryUrl);
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            if (Process.VERBOSE) {
            	System.out.println("\n--------------------\nCoNE query: " + queryUrl + " returned ");
                System.out.println("--------------------\nHeader\n--------------------");
                System.out.println(httpResponse.toString());
                System.out.println("--------------------\nContent\n--------------------");
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
            String line = null;
            while ((line = in.readLine()) != null)
            {
            	possibleDublicates.add(line);
            	if (Process.VERBOSE) {
            		System.out.println(line);
            	}
            }
            if (httpResponse.getStatusLine().getStatusCode() == 200)
            {
                queryUrl = coneUrl + model + "/query?format=jquery&dcterms:alternative=" + URLEncoder.encode("\"" + name + "\"", "UTF-8") + "&n=0";
                httpGet = new HttpGet(queryUrl);
                httpResponse = httpClient.execute(httpGet);
                if (Process.VERBOSE) {
	                System.out.println("\n--------------------\nCoNE query: " + queryUrl + " returned ");
	                System.out.println("--------------------\nHeader\n--------------------");
	                System.out.println(httpResponse.toString());
	                System.out.println("--------------------\nContent\n--------------------");
                }
                in = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
                while ((line = in.readLine()) != null)
                {
                	possibleDublicates.add(line);
                	if (Process.VERBOSE) {
                		System.out.println(line);
                	}
                }
            }
            else
            {
                System.out.println("Error querying CoNE for " + name + " (Status: "
                        + httpResponse.getStatusLine().getStatusCode() + ")\n" + httpResponse.toString());
            }
        }
        catch (Exception e)
        {
            System.out.println("Error querying CoNE service");
            e.printStackTrace();
        }
        return possibleDublicates;
    }
}
