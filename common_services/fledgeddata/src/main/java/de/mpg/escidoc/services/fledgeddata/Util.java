/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.escidoc.services.fledgeddata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class Util
{
	private static final Logger LOGGER = Logger.getLogger(Util.class);
	
	public static List createSets (Document doc, List nodeList, String type)
	{
		if (nodeList == null) {nodeList= new ArrayList();}
		
		if (type.equals("collection"))
		{
			NodeList collNodes = doc.getElementsByTagName("imeji:collection");
			NodeList titleNodes = doc.getElementsByTagName("dcterms:title");
			NodeList descNodes = doc.getElementsByTagName("dcterms:description");
			for (int i=0; i< collNodes.getLength(); i++)
			{
				HashMap<String, String> nodeMap = new HashMap<String, String>();
				nodeMap.put("setSpec", collNodes.item(i).getAttributes().item(0).getNodeValue());
				nodeMap.put("setName", titleNodes.item(i).getTextContent());
				nodeMap.put("setDescription", descNodes.item(i).getTextContent());
				nodeList.add(nodeMap);
			}
		}
		
		if (type.equals("album"))
		{
			NodeList albNodes = doc.getElementsByTagName("imeji:album");
			NodeList titleNodes = doc.getElementsByTagName("dcterms:title");
			NodeList descNodes = doc.getElementsByTagName("dcterms:description");
			for (int i=0; i< albNodes.getLength(); i++)
			{
				HashMap<String, String> nodeMap = new HashMap<String, String>();
				nodeMap.put("setSpec", albNodes.item(i).getAttributes().item(0).getNodeValue());
				nodeMap.put("setName", titleNodes.item(i).getTextContent());
				nodeMap.put("setDescription", descNodes.item(i).getTextContent());
				nodeList.add(nodeMap);
			}
		}
		
		return nodeList;
	}
	
    /**
     * Gets a resource as InputStream.
     *
     * @param fileName The path and name of the file relative from the working directory.
     * @return The resource as InputStream.
     * @throws FileNotFoundException Thrown if the resource cannot be located.
     */
    public static File getResourceAsFile(final String fileName) throws FileNotFoundException
    {
        URL url = Util.class.getClassLoader().getResource(resolveFileName(fileName));
        
        // Maybe it's in a WAR file
        if (url == null)
        {
            url = Util.class.getClassLoader().getResource(resolveFileName("WEB-INF/classes/" + fileName));
        }
        
        File file = null;
        if (url != null)
        {
            LOGGER.debug("Resource found: " + url.getFile());
            try
            {
                //Decode necessary for windows paths
                file = new File(URLDecoder.decode(url.getFile(), "cp1253"));
            }
            catch(UnsupportedEncodingException e){LOGGER.warn(e);}
        }
        
        if (file == null)
        {

        	LOGGER.debug("Resource not found, getting file.");

            file = new File(fileName);
            if (!file.exists())
            {
                throw new FileNotFoundException("File '" + fileName + "' not found.");
            }
        }
        return file;
    }
    
    /**
     * This method resolves /.. in uris
     * @param name
     * @return
     */
    public static String resolveFileName (String name)
    {
        if (name != null && (name.contains("/..") || name.contains("\\..")))
        {
            Pattern pattern1 = Pattern.compile("(\\\\|/)\\.\\.");
            Matcher matcher1 = pattern1.matcher(name);
            if (matcher1.find())
            {
                int pos1 = matcher1.start();
                Pattern pattern2 = Pattern.compile("(\\\\|/)[^\\\\/]*$");
                Matcher matcher2 = pattern2.matcher(name.substring(0, pos1));
                if (matcher2.find())
                {
                    int pos2 = matcher2.start();
                    return resolveFileName(name.substring(0, pos2) + name.substring(pos1 + 3));
                }
            }
        }
        
        return name;
    }
	
}