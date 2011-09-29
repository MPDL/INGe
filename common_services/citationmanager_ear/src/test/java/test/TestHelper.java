/*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.citationmanager.utils.ResourceUtil;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Helper class for all test classes.
 *
 * @author Johannes M&uuml;ller (initial) 
 * @author $Author$ (last change)
 * @version $Revision$ $LastChangedDate$
 */
public class TestHelper
{

	private static Logger logger = Logger.getLogger(TestHelper.class);
	
	public static final String ITEMS_LIMIT = "10"; 
	
	public static final String CONTEXT_TITLE = "Citation Style Testing Context";
	public static final String PROPERTY_CONTENT_MODEL = "escidoc.framework_access.content-model.id.publication"; 
    private static final String PROPERTY_USERNAME_ADMIN = "framework.admin.username";
    private static final String PROPERTY_PASSWORD_ADMIN = "framework.admin.password";
	
	/**
     * Retrieve resource based on a path relative to the classpath.
     * @param fileName The path of the resource.
     * @return The file defined by The given path.
     * @throws FileNotFoundException File not there.
     */
    public final File findFileInClasspath(final String fileName) throws FileNotFoundException
    {
        URL url = getClass().getClassLoader().getResource(fileName);
        if (url == null)
        {
            throw new FileNotFoundException(fileName);
        }
        return new File(url.getFile());
    }

    /**
     * Reads contents from text file and returns it as String.
     *
     * @param fileName Name of input file
     * @return Entire contents of filename as a String
     */
    public static String readFile(final String fileName)
    {
        boolean isFileNameNull = (fileName == null);
        StringBuffer fileBuffer;
        String fileString = null;
        String line;
        if (!isFileNameNull)
        {
            try
            {
                InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName));
                BufferedReader br = new BufferedReader(isr);
                fileBuffer = new StringBuffer();
                while ((line = br.readLine()) != null)
                {
                    fileBuffer.append(line + "\n");
                }
                isr.close();
                fileString = fileBuffer.toString();
            }
            catch (IOException e)
            {
                return null;
            }
        }
        return fileString;
    }

    
    public static byte[] readBinFile(final String fileName)
    {
    	boolean isFileNameNull = (fileName == null);
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	if (!isFileNameNull)
    	{
    		try {
    			int b;                // the byte read from the file
    			BufferedInputStream is = new BufferedInputStream(new FileInputStream(fileName));
    			BufferedOutputStream os = new BufferedOutputStream(baos);
    			while ((b = is.read( )) != -1) {
    				os.write(b);
    			}
    			is.close( );
    			os.close( );
    		}
    		catch (IOException e)
    		{
    			return null;
    		}
    	}
    	return baos.toByteArray();
    }
    
    public static void writeBinFile(byte[] content, String fileName)
    {
    	boolean isFileNameNull = (fileName == null);
    	boolean isEmptyContent = (content.length == 0);
    	if (!isFileNameNull && !isEmptyContent)
    	{
    		try {
    	        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName));
    	        for (byte b : content)
    	            bos.write(b);
    	        bos.close( );
    		}
    		catch (IOException e)
    		{
    		}
    	}
    }
    
    public static Properties getTestProperties(String csName) throws FileNotFoundException, IOException 
    {
    	InputStream is = ResourceUtil.getResourceAsStream(
    			ResourceUtil.getPathToCitationStyles()
    			+ ResourceUtil.CITATIONSTYLES_DIRECTORY 
    			+ csName
    			+ "/test.properties" 
    	); 
    	Properties props = new Properties();
    	try {
			props.load(is);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return props;
    }
    
    public static String cleanCit(String str) {
    	if (str!=null && !str.trim().equals("")) 
    		str = str.replaceAll("[\\s\t\r\n]+", " ");
    	return str;
    }

 
   
    /**
     * Get itemList from the current Framework instance
     * @throws IOException 
     * @throws URISyntaxException 
     * @throws ServiceException 
     */    
    public static String getTestItemListFromFramework() throws IOException, ServiceException, URISyntaxException
    {
        HashMap<String, String[]> filter = new HashMap<String, String[]>();
        String q = "\"/properties/content-model/id\"=" + PropertyReader.getProperty(PROPERTY_CONTENT_MODEL) 
                + " and " + "\"/properties/public-status\"=pending" + " and " + "\"/properties/context/title\"=" + CONTEXT_TITLE;
        filter.put("version", new String[]{"1.1"});
        filter.put("operation", new String[]{"searchRetrieve"});
        filter.put("query", new String[] {q});
       
    	return getItemListFromFrameworkBase(PropertyReader.getProperty(PROPERTY_USERNAME_ADMIN), PropertyReader.getProperty(PROPERTY_PASSWORD_ADMIN), filter);	
    }
    
    public static String getItemListFromFrameworkBase(String user, String passwd, HashMap<String, String[]> filter) throws IOException, ServiceException, URISyntaxException
    {
    	String userHandle = AdminHelper.loginUser(user, passwd); 
    	logger.info("Retrieve filter:" + filter);
    	
    	ItemHandler ch = ServiceLocator.getItemHandler(userHandle);
    	return ch.retrieveItems(filter);
    }
    
    
    
    

}
