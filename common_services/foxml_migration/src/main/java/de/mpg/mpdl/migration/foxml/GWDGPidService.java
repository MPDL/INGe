package de.mpg.mpdl.migration.foxml;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.framework.ProxyHelper;

import sun.net.www.protocol.http.HttpURLConnection;

/**
 * Class containing utility methods.
 *
 * @author frank (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class GWDGPidService implements MigrationConstants
{
    private static HttpURLConnection conn = null;
    private static Logger pidlogger = Logger.getLogger("pidreplacement");
    
    private GWDGPidService()
    {
        
    }
    
    /**
     * 
     * @param args {@link String[]}
     * @throws MalformedURLException 
     */
    public static void main(String[] args) throws MalformedURLException
    {
        // TODO Auto-generated method stub
        //getSetDefinitions();
        String url2register = "http://sample.com/Ölfilter/";
        try
        {
            String urlParameter = URLEncoder.encode(url2register, URL_ENCODING_SCHEME);
            System.out.println(urlParameter);
            URL url = new URL(URLDecoder.decode(urlParameter, URL_ENCODING_SCHEME));
            System.out.println(url);
        }
        catch (UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
    public static String getSetDefinitions()
    {
        String body = null;
        
        try
        {
            body = "<param><limit>100</limit><offset>0</offset></param>";
            URL url = new URL("http://coreservice.mpdl.mpg.de:8080/oai/set-definitions/filter");
            conn = (HttpURLConnection) ProxyHelper.openConnection(url);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", URL_ENCODING_FORMAT);
            conn.setRequestProperty("Content-Length", "" + Integer.toString(body.getBytes().length));
            conn.setRequestProperty("Content-Language", "en-US");  
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            Authenticator.setDefault(new GWDGAuthentication(GWDG_PIDSERVICE_USER, GWDG_PIDSERVICE_PASS));
            
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(body);
            wr.flush();
            wr.close();

            InputStream is = conn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null)
            {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
            
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 
     * @param url2find {@link String}
     * @return the hamdle
     */
    public static String findHandle4URL(String url2find)
    {
        String handle = null;
        String urlParameter = null;
        
        try
        {
            urlParameter = "?url=" + URLEncoder.encode(url2find, URL_ENCODING_SCHEME);
            URL url = new URL(GWDG_PIDSERVICE_FIND + urlParameter);
            System.out.println(url.toExternalForm());
            conn = (HttpURLConnection) ProxyHelper.openConnection(url);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                System.out.println("HttpURLConnection response message: " + conn.getResponseMessage());
            }
            else
            {
                String contentEncoding = conn.getContentEncoding();
                if (contentEncoding == null)
                {
                    contentEncoding = URL_ENCODING_SCHEME;
                }
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), contentEncoding));
                String line;
                StringBuffer response = new StringBuffer(); 

                while ((line = in.readLine()) != null)
                {
                    response.append(line);
                    response.append('\r');
                }
                in.close();
                return response.toString();
            }
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 
     * @param url2register {@link String}
     * @return a handle
     */
    public static String registerNewPID(String url2register)
    {
        String urlParameter = null;
        
        try
        {
            urlParameter = "url=" + URLEncoder.encode(url2register, URL_ENCODING_SCHEME);
            URL url = new URL(GWDG_PIDSERVICE_CREATE);
            conn = (HttpURLConnection) ProxyHelper.openConnection(url);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", URL_ENCODING_FORMAT);
            conn.setRequestProperty("Content-Length", "" + Integer.toString(urlParameter.getBytes().length));
            conn.setRequestProperty("Content-Language", "en-US");  
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            Authenticator.setDefault(new GWDGAuthentication(GWDG_PIDSERVICE_USER, GWDG_PIDSERVICE_PASS));
            
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(urlParameter);
            wr.flush();
            wr.close();

            InputStream is = conn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null)
            {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
            
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
