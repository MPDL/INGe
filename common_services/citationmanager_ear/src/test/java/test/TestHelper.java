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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
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
import java.net.URL;
import java.util.Properties;

import de.mpg.escidoc.services.citationmanager.ProcessCitationStyles;
import de.mpg.escidoc.services.citationmanager.ResourceUtil;

/**
 * Helper class for all test classes.
 *
 * @author Johannes M&uuml;ller (initial) 
 * @author $Author: mfranke $ (last change)
 * @version $Revision: 63 $ $LastChangedDate: 2007-08-09 17:36:48 +0200 (Do, 09 Aug 2007) $
 */
public class TestHelper
{
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

    

}
