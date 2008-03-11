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
package test.framework;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.TransformerException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import test.framework.om.TestItemBase;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
// import de.fiz.escidoc.common.exceptions.system.KowariSystemException;
// import de.escidoc.core.common.exceptions.system.PIDGeneratorSystemException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Test cases for the schindlmayr-springer item.
 *
 * @author Peter (initial creation)
 * @author $Author: wfrank $ (last modification)
 * @version $Revision: 314 $ $LastChangedDate: 2007-11-07 13:12:14 +0100 (Wed, 07 Nov 2007) $
 * @revised by FrW: 10.03.2008
 */ 
public class TestSchindlmayrSpringer extends TestItemBase
{
    private static final String ITEM_FILE = "src/test/resources/test/TestFile/schindlmayr-springer.xml";
    private static String COMPONENT_FILE = "src/test/resources/test/TestFile/schindlmayr-springer.pdf";
    private static String MIME_TYPE = "application/pdf";

    private Logger logger = Logger.getLogger(getClass());

    private String upload() throws ServiceException, HttpException, IOException, ParserConfigurationException,
            SAXException, TransformerException, Exception
    {
        // Upload the file.
        PutMethod method = new PutMethod(ServiceLocator.getFrameworkUrl() + "/st/staging-file");
        method.setRequestEntity(new InputStreamRequestEntity(new FileInputStream(COMPONENT_FILE)));
        method.setRequestHeader("Content-Type", MIME_TYPE);
        method.setRequestHeader("Cookie", "escidocCookie=" + userHandle);
        HttpClient client = new HttpClient();
        client.executeMethod(method);
        logger.debug("Status=" + method.getStatusCode());
        assertEquals(HttpServletResponse.SC_OK, method.getStatusCode());
        String response = method.getResponseBodyAsString();
        logger.debug("Response=\n" + toString(getDocument(response, false), false));

        // Create a document from the response.
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document document = docBuilder.parse(method.getResponseBodyAsStream());
        document.getDocumentElement().normalize();

        // Extract the file information.
        String href = getValue(document, "/staging-file/@href");
        assertNotNull(href);
        return href;
    }

    private int download(String item) throws ServiceException, HttpException, IOException,
            ParserConfigurationException, SAXException, TransformerException
    {
        // Create a document from the response.
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document document = docBuilder.parse(new ByteArrayInputStream(item.getBytes()));
        document.getDocumentElement().normalize();
        // Extract the href and create the method.
        String href = getValue(
                document,
                "//*[local-name() = 'content' and namespace-uri() = 'http://www.escidoc.de/schemas/components/0.3']/@*[local-name() = 'href' and namespace-uri() = 'http://www.w3.org/1999/xlink']");
        assertNotNull(href);
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
        return count;
    }

    private String create() throws ServiceException, HttpException, IOException, ParserConfigurationException,
            SAXException, TransformerException, Exception
    {
        String item = readFile(ITEM_FILE);
        String fileid = upload();
        item = item.replaceFirst("XXX_CONTENT_REF_XXX", ServiceLocator.getFrameworkUrl() + fileid);
        item = ServiceLocator.getItemHandler(userHandle).create(item);
        logger.debug("Item created=\n" + toString(getDocument(item, false), false));
        return item;
    }

    private String read(String id) throws Exception
    {
        String item = ServiceLocator.getItemHandler(userHandle).retrieve(id);
        logger.debug("Item read=\n" + toString(getDocument(item, false), false));
        return item;
    }

    private String update(String item) throws LockingException, MissingMethodParameterException,
            InvalidStatusException, ItemNotFoundException, XmlParserSystemException, WebserverSystemException,
            SqlDatabaseSystemException, AuthenticationException, FedoraSystemException, AuthorizationException,
            RemoteException, ServiceException
    {
        String id = getId(item);
        String updatedItem = ServiceLocator.getItemHandler(userHandle).update(id, item);
        logger.debug("Item updated=" + updatedItem);
        return updatedItem;
    }

    private void submit(String item) throws LockingException, MissingMethodParameterException,
            InvalidStatusException, ItemNotFoundException, XmlParserSystemException, WebserverSystemException,
            SqlDatabaseSystemException, AuthenticationException, FedoraSystemException, AuthorizationException,
            RemoteException, ServiceException
    {
        String id = getId(item);
        String md = getModificationDate(item);
        ServiceLocator.getItemHandler(userHandle).submit(id, createModificationDate(md));
        logger.debug("Item submitted");
        return;
    }

    private String assignPid(String item) throws OptimisticLockingException, LockingException, MissingMethodParameterException, InvalidStatusException, ItemNotFoundException, AuthenticationException, AuthorizationException, SystemException, RemoteException, ServiceException
    {
        String id = getVersion(item);
        String md = getModificationDate(item);
        String param = "<param last-modification-date=\"" + md + "\">" +
                       "    <url>http://localhost</url>" +
                       "</param>";
        logger.debug("Version=" + id);
        logger.debug("Param=" + param);
        String pid = ServiceLocator.getItemHandler(userHandle).assignVersionPid(id, param);
        logger.debug("PID assigned=" + pid);
        return pid;
    }
    
    private void release(String item) throws OptimisticLockingException, LockingException,
            MissingMethodParameterException, InvalidStatusException, ItemNotFoundException, AuthenticationException,
            AuthorizationException, SystemException, RemoteException, ServiceException
    {
        String id = getId(item);
        String md = getModificationDate(item);
        ServiceLocator.getItemHandler(userHandle).release(id, createModificationDate(md));
        logger.debug("Item released");
        return;
    }

    /**
     * Test method for {@link de.fiz.escidoc.item.ItemHandlerLocal#create(java.lang.String)}.
     */
    @Test
    public void createItem() throws Exception
    {
        String item = create();
        assertNotNull(item);
    }

    /**
     * Test method for {@link de.fiz.escidoc.item.ItemHandlerLocal#update(String, String)}.
     */
    @Test
    public void updateItem() throws Exception
    {
        String item = create();
        assertNotNull(item);

        item = update(item);
        assertNotNull(item);

        int count = download(item);
        assertEquals(649066, count);
    }

    /**
     * Test method for {@link de.fiz.escidoc.item.ItemHandlerLocal#submit(java.lang.String,java.lang.String)}.
     */
    @Test
    public void submitItem() throws Exception
    {
        String item = create();
        assertNotNull(item);

        submit(item);
        String id = getId(item);
        assertNotNull(id);

        item = read(id);
        assertNotNull(item);
    }

    /**
     * Test method for {@link de.fiz.escidoc.item.ItemHandlerLocal#release(java.lang.String,java.lang.String)}.
     */
    @Test
    public void releaseItem() throws Exception
    {
        String item = create();
        assertNotNull(item);

        String id = getId(item);
        assertNotNull(id);
        submit(item);

        item = read(id);
        assertNotNull(item);
        release(item);

        item = read(id);
        String pid = assignPid(item);
        assertNotNull(pid);

        item = read(id);
        assertNotNull(item);
    }

    /**
     * Logs in as the Librarian before each test case.
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception
    {
        userHandle = loginLibrarian();
    }
}
