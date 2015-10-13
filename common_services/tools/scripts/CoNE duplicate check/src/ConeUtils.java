import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


/**
 * 
 * Class with some static methods doing CoNE-Requests
 * 
 * @author walter
 *
 */
public class ConeUtils {

	
	
	/**
     * Queries the CoNE service and transforms the result into a DOM node.
     * 
     * @param model The type of object (e.g. "persons")
     * @param name The person's name.
     * @return A DOM node containing the results.
     */
    public static List<String> queryConePerson(String name)
    {
    	if (ConfigUtil.VERBOSE) { 
    		System.out.println("-------------------\nStarted Querying CoNE\n-------------------");
    	}
    	List<String> possibleDublicates = new ArrayList<String>();
        
        try
        {
            String queryUrl = ConfigUtil.CONE_URL + ConfigUtil.PERSON_MODEL + "/query?format=jquery&dc:title=" + URLEncoder.encode("\"" + name + "\"", "UTF-8") + "&n=0";
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(queryUrl);
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            if (ConfigUtil.VERBOSE) 
            {
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
            	if (ConfigUtil.VERBOSE) 
            	{
            		System.out.println(line);
            	}
            }
            if (httpResponse.getStatusLine().getStatusCode() == 200)
            {
                queryUrl = ConfigUtil.CONE_URL + ConfigUtil.PERSON_MODEL + "/query?format=jquery&dcterms:alternative=" + URLEncoder.encode("\"" + name + "\"", "UTF-8") + "&n=0";
                httpGet = new HttpGet(queryUrl);
                httpResponse = httpClient.execute(httpGet);
                if (ConfigUtil.VERBOSE)
                {
	                System.out.println("\n--------------------\nCoNE query: " + queryUrl + " returned ");
	                System.out.println("--------------------\nHeader\n--------------------");
	                System.out.println(httpResponse.toString());
	                System.out.println("--------------------\nContent\n--------------------");
                }
                in = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
                while ((line = in.readLine()) != null)
                {
                	possibleDublicates.add(line);
                	if (ConfigUtil.VERBOSE) 
                	{
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
        	System.out.println("Error querying CoNE service [" + ConeUtils.class.getEnclosingMethod() + "]");
            e.printStackTrace();
        }
        return possibleDublicates;
    }
    
    /**
     * gets a Map of all CoNE persons (each person only once)
     * @return Map <String CoNE-ID, String [String Person-CompleteName, String Person-Organization]
     */
    public static Map <String, String[]> getAllCone()
    {
    	Map <String, String[]> persons = new HashMap <String, String[]> ();
    	String queryUrl = ConfigUtil.CONE_URL + ConfigUtil.PERSON_MODEL + "/all?format=jquery";
//    	String queryUrl = ConfigUtil.CONE_URL + ConfigUtil.PERSON_MODEL + "/query?format=jquery&q=a*&n=200"; // for testing issues
    	CloseableHttpClient httpClient = HttpClients.createDefault();
    	HttpGet httpGet = new HttpGet(queryUrl);
    	
    	try 
    	{
			CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
			if (ConfigUtil.VERBOSE) 
			{
            	System.out.println("\n--------------------\nCoNE query: " + queryUrl + " returned ");
                System.out.println("--------------------\nHeader\n--------------------");
                System.out.println(httpResponse.toString());
                System.out.println("--------------------\nContent\n--------------------");
            }
			BufferedReader in = new BufferedReader(new InputStreamReader (httpResponse.getEntity().getContent(), "UTF-8"));
			String line = null;
			String[] splitedQuery = new String[2];
			
			
			while((line = in.readLine()) != null)
			{
				splitedQuery = line.split("\\|");
				if (!persons.containsKey(splitedQuery[1]))
				{
					String[] nameAndOrganization = new String[2];
					if (splitedQuery[0].indexOf("(") >= 0)
					{
						nameAndOrganization[0] = splitedQuery[0].substring(0, (splitedQuery[0].indexOf("(") - 1)).trim();
						nameAndOrganization[1] = splitedQuery[0].substring((splitedQuery[0].indexOf("(") + 1) , splitedQuery[0].lastIndexOf(")")).trim();
						persons.put(splitedQuery[1].trim(), nameAndOrganization);
						if (ConfigUtil.VERBOSE) 
						{
							System.out.println("Added [" + splitedQuery[1].trim() + " | {" +nameAndOrganization[0] + "," + nameAndOrganization[1] + "}] to persons");
						}
					}
					else {
						nameAndOrganization[0] = splitedQuery[0].trim();
						nameAndOrganization[1] = "";
						persons.put(splitedQuery[1].trim(), nameAndOrganization);
						if (ConfigUtil.VERBOSE) 
						{
							System.out.println("Added [" + splitedQuery[1].trim() + " | {" +nameAndOrganization[0] + "," + nameAndOrganization[1] + "}] to persons");
						}
					}
					
				}
				else if (ConfigUtil.VERBOSE) {
					System.out.println("[" + splitedQuery[1] + "] already contained in persons");
				}
			}
		} 
    	catch (ClientProtocolException e) 
		{
			System.out.println("Error querying CoNE service [" + ConeUtils.class.getEnclosingMethod() + "]");
			e.printStackTrace();
		} 
    	catch (IOException e) 
    	{
			System.out.println("Error querying CoNE service [" + ConeUtils.class.getEnclosingMethod() + "]");
			e.printStackTrace();
		}
    	return persons;
    }
    
    public static String getSearchTermForCompleteName (String completeName) 
    {
    	if(!completeName.contains(".") && completeName.contains(", "))
    	{
    		if (ConfigUtil.VERBOSE) 
			{
    			System.out.println("SearchTerm returned: " + completeName.substring(0, (completeName.indexOf(", ") + 3)) + ".");
			}
    		return (completeName.substring(0, (completeName.indexOf(", ") + 3)) + ".");
    	}
    	else 
    	{
    		return "";
    	}
    }
    
}
