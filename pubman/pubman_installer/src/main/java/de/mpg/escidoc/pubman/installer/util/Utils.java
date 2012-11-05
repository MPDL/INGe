package de.mpg.escidoc.pubman.installer.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utils
{
    
    /**
     * Search the given String for the first occurence of "key" and return its value.
     * 
     * @param item A (XML) String
     * @return The kvalue
     */
    public static String getValueFromXml(String key, String item)
    {
        String result = "";
        String searchString = key;
        int index = item.indexOf(searchString);
        if (index > 0)
        {
            item = item.substring(index + searchString.length());
            index = item.indexOf('\"');
            if (index > 0)
            {
                result = item.substring(0, index);
            }
        }
        return result;
    } 
    
    public static String getValueFromXml(String key, char endChar, String item)
    {
        String result = "";
        String searchString = key;
        int index = item.indexOf(searchString);
        if (index > 0)
        {
            item = item.substring(index + searchString.length());
            index = item.indexOf(endChar);
            if (index > 0)
            {
                result = item.substring(0, index);
            }
        }
        return result;
    } 
    
    public static String getResourceAsXml(final String fileName) throws FileNotFoundException, Exception
    {
        StringBuffer buffer = new StringBuffer();
        InputStream is = null;
        BufferedReader br = null;
        String line;

        try
        {
            is = Utils.class.getClassLoader().getResourceAsStream(fileName);
            br = new BufferedReader(new InputStreamReader(is));
            while (null != (line = br.readLine()))
            {
                buffer.append(line);
                buffer.append("\n");
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (br != null)
                    br.close();
                if (is != null)
                    is.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return buffer.toString();
    }
}
