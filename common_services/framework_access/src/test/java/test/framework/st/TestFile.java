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
package test.framework.st;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.w3c.dom.Document;
import test.framework.TestBase;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Testcases for the staging area of the basic service ItemHandler.
 *
 * @author Peter (initial creation)
 * @author $Author: pbroszei $ (last modification)
 * @version $Revision: 314 $ $LastChangedDate: 2007-11-07 13:12:14 +0100 (Wed, 07 Nov 2007) $
 * @revised by BrP: 04.09.2007
 */
public class TestFile extends TestBase
{ 
    private static final String ITEM_FILE = "src/test/resources/test/TestFile/new_schindlmayr-springer.xml";
    private static final String COMPONENT_FILE = "src/test/resources/test/TestFile/schindlmayr-springer.pdf";
    private static final String MIME_TYPE = "application/pdf";

    private Logger logger = Logger.getLogger(getClass());

    private String createItemWithFile(String handle) throws Exception
    {
        // Prepare the HttpMethod.
        PutMethod method = new PutMethod(ServiceLocator.getFrameworkUrl() + "/st/staging-file");
        method.setRequestEntity(new InputStreamRequestEntity(new FileInputStream(COMPONENT_FILE)));
        method.setRequestHeader("Content-Type", MIME_TYPE);
        method.setRequestHeader("Cookie", "escidocCookie=" + handle);
        // Execute the method with HttpClient.
        HttpClient client = new HttpClient();
        client.executeMethod(method);
        logger.debug("Status=" + method.getStatusCode()); // >= HttpServletResponse.SC_MULTIPLE_CHOICE 300 ???
        assertEquals(HttpServletResponse.SC_OK, method.getStatusCode());
        String response = method.getResponseBodyAsString();
        logger.debug("Response=" + response);
        // Create a document from the response.
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document document = docBuilder.parse(method.getResponseBodyAsStream());
        document.getDocumentElement().normalize();
        // Extract the file information.
        String href = getValue(document, "/staging-file/@href");
        assertNotNull(href);
        // Create an item with the href in the component.
        String item = readFile(ITEM_FILE);
        item = item.replaceFirst("XXX_CONTENT_REF_XXX", ServiceLocator.getFrameworkUrl() + href);
        logger.debug("Item (before create)=" + item);
        item = ServiceLocator.getItemHandler(handle).create(item);
        assertNotNull(item);
        logger.debug("Item (after create)=" + item);
        return item;
    }

    /**
     * Tests the upload of a file to the staging area.
     */
    @Test
    public void uploadFile() throws Exception
    {
        createItemWithFile(userHandle);
    }

    /**
     * Tests the download of a file from the staging area.
     */
    @Test
    public void downloadFile() throws Exception
    {
        // Create a document from the response.
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document document = docBuilder.parse(new ByteArrayInputStream(createItemWithFile(userHandle).getBytes()));
        document.getDocumentElement().normalize();
        // Extract the href and create the method.
        String href = getValue(
                document,
                "//*[local-name() = 'content' and namespace-uri() = 'http://www.escidoc.de/schemas/components/0.4']/@*[local-name() = 'href' and namespace-uri() = 'http://www.w3.org/1999/xlink']");
        assertNotNull(href);
        logger.debug("href=" + href);
        String url = ServiceLocator.getFrameworkUrl() + href;
        logger.debug("url=" + url);
        GetMethod method = new GetMethod(url);
        method.setFollowRedirects(false);
        method.setRequestHeader("Cookie", "escidocCookie=" + userHandle);
        // Execute the method with HttpClient.
        HttpClient client = new HttpClient();
        client.executeMethod(method);
        logger.debug("Status=" + method.getStatusCode());
        assertEquals(HttpServletResponse.SC_OK, method.getStatusCode());
        Header contentTypeHeader = method.getResponseHeader("Content-Type");
        assertEquals(MIME_TYPE, contentTypeHeader.getValue());
        InputStream input = method.getResponseBodyAsStream();
        File tempFile = File.createTempFile("download", ".pdf");
        logger.debug("Write content to " + tempFile.getName());
        FileOutputStream output = new FileOutputStream(tempFile);
        byte buffer[] = new byte[1];
        int count = 0;
        while (input.read(buffer) > 0)
        {
            output.write(buffer);
            ++count;
        }
        output.close();
        logger.debug("File length=" + count);
        assertEquals(649066, count);
    }
}
