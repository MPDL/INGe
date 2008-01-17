/*
*
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

package test.common.xmltransforming.integration;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import test.common.TestBase;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.PubFileVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.framework.ServiceLocator;


/**
 * Test for pubCollection transforming of {@link XmlTransforming}.
 * 
 * @author Peter Broszeit (initial creation)
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $
 * @revised by MuJ: 20.09.2007
 */
public class TransformSchindlMayrIntegrationTest extends TestBase
{
    private static XmlTransforming xmlTransforming;

    private Logger logger = Logger.getLogger(getClass());
    
    /**
     * Get an XmlTransforming instance once for all tests.
     * 
     * @throws Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        xmlTransforming = (XmlTransforming) getService(XmlTransforming.SERVICE_NAME);
    }

    /**
     * Tests the correct transforming of an item[XML] with a file in conjunction with creating it in the framework. 
     * 
     * @throws Exception
     */
    @Ignore("Not implemented yet")
    public void testCreateItemWithFileAndTransformToPubItem() throws Exception
    {
        PubItemVO pubItem = xmlTransforming.transformToPubItem(createItemWithFile(loginScientist()));
        List<PubFileVO> files = pubItem.getFiles();
        logger.debug("#" + files.size() + " files");
        for (PubFileVO file : files)
        {
            StringBuffer sb = new StringBuffer();
            sb.append("Name=" + file.getName()+"\n");
            sb.append("Size=" + file.getSize()+"\n");
            sb.append("Content=" + file.getContent()+"\n");
            sb.append("Locator=" + file.getLocator()+"\n");
            logger.debug(sb.toString());            
        }
        // TODO MuJ: expand test.
    }
    
    /**
     * Tests the correct transforming of an item[XML] with a file in conjunction with creating and updating it in the framework. 
     * 
     * @throws Exception
     */
    @Test
    public void testCreateAndUpdateItemWithFileAndTransformToPubItem() throws Exception
    {
        String userHandle = loginScientist();
        // Create the item
        String item = createItemWithFile(userHandle);
        logger.debug("create=" + item);
        PubItemVO pubItem = xmlTransforming.transformToPubItem(item);
        List<PubFileVO> files = pubItem.getFiles();
        logger.debug("#" + files.size() + " files");
        for (PubFileVO file : files)
        {
            StringBuffer sb = new StringBuffer();
            sb.append("Name=" + file.getName()+"\n");
            sb.append("Size=" + file.getSize()+"\n");
            sb.append("Content=" + file.getContent()+"\n");
            sb.append("Locator=" + file.getLocator()+"\n");
            logger.debug(sb.toString());  
        }
        
        // Update the item
        item = xmlTransforming.transformToItem(pubItem);
        logger.debug("transform=" + item);
        item = ServiceLocator.getItemHandler(userHandle).update(pubItem.getReference().getObjectId(),item);
        logger.debug("update=" + item);
        pubItem = xmlTransforming.transformToPubItem(item);
        
        // Download files
        files = pubItem.getFiles();
        logger.debug("#" + files.size() + " files");
        for (PubFileVO file : files)
        {
            logger.debug("Name=" + file.getName());
            logger.debug("Size=" + file.getSize());
            logger.debug("Content=" + file.getContent());
            logger.debug("Locator=" + file.getLocator());

            String urlSuffix = file.getContent().toString();
            String url = ServiceLocator.getFrameworkUrl() + urlSuffix;
            GetMethod method = new GetMethod(url);
            
            method.setFollowRedirects(false);
            method.setRequestHeader("Cookie", "escidocCookie=" + userHandle);            
    
            // Execute the method with HttpClient.
            HttpClient client = new HttpClient();
            client.executeMethod(method);
            logger.debug("Status=" + method.getStatusCode());
            assertEquals(HttpServletResponse.SC_OK,method.getStatusCode());
            
            Header contentTypeHeader = method.getResponseHeader("Content-Type");
            assertEquals(MIME_TYPE,contentTypeHeader.getValue());
            
            InputStream input = method.getResponseBodyAsStream();
            File tempFile = File.createTempFile("download", ".pdf");
            logger.debug("Write content to "+tempFile.getName());
            FileOutputStream output = new FileOutputStream(tempFile);
            byte buffer[] = new byte[1];
            long count = 0;         
            while (input.read(buffer) > 0)
            {
                output.write(buffer);
                ++count;
            }
    
            output.close();
            logger.debug("File length=" + count);
            assertEquals(file.getSize(),count);
        }
    }
}
