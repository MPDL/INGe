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
package test.framework.om;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import de.escidoc.core.common.exceptions.application.invalid.InvalidContextException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Testcases for the basic service ItemHandler.
 * 
 * @author Johannes Mueller (initial creation)
 * @author $Author: wfrank $ (last modification)
 * @version $Revision: 325 $ $LastChangedDate: 2007-11-28 18:07:29 +0100 (Wed, 28 Nov 2007) $
 * @revised by FrW: 10.03.2008
 */
public class TestItem extends TestItemBase
{
    private Logger logger = Logger.getLogger(getClass());

    /**
     * Test method for {@link de.escidoc.www.services.om.ItemHandlerLocal#create(java.lang.String)}.
     */
    @Test
    public void createContentItem() throws Exception
    {
	String item = readFile(ITEM_FILE);
        long zeit = -System.currentTimeMillis();
        item = ServiceLocator.getItemHandler(userHandle).create(item);
        zeit += System.currentTimeMillis();
        logger.info("createContentItem()->" + zeit + "ms");
        logger.debug("ContentItem()=" + item);
        assertNotNull(item);
    }

     /**
     * Test method for {@link de.escidoc.www.services.om.ItemHandlerLocal#retrieve(java.lang.String)}.
     */
    @Test
    public void retrieveContentItem() throws Exception
    {
        String item = readFile(ITEM_FILE);
        item = ServiceLocator.getItemHandler(userHandle).create(item);
        String id = getId(item);
        long zeit = -System.currentTimeMillis();
        item = ServiceLocator.getItemHandler(userHandle).retrieve(id);
        zeit += System.currentTimeMillis();
        logger.info("retrieveContentItem(" + id + ")->" + zeit + "ms");
        logger.debug("ContentItem(" + id + ")=" + item);
        assertNotNull(item);
    }

     /**
     * Test method for {@link de.escidoc.www.services.om.ItemHandlerLocal#retrieve(java.lang.String)}.
     */
    @Test(expected = ItemNotFoundException.class)
    public void retrieveContentItemNotExisting() throws Exception
    {
        String id = ILLEGAL_ID;
        long zeit = -System.currentTimeMillis();
        try
        {
            String item = ServiceLocator.getItemHandler(userHandle).retrieve(id);
            assertTrue(item, false);
        }
        finally
        {
            zeit += System.currentTimeMillis();
            logger.info("retrieveContentItemNotExisting(" + id + ")->" + zeit + "ms");
        }
    }

    /**
     * Test method for {@link de.escidoc.www.services.om.ItemHandlerLocal#retrieve(java.lang.String)}.
     */
    @Ignore("Because of bug #xxx")
    @Test
    public void retrieveContentItemByPid() throws Exception
    {
        String item = readFile(ITEM_FILE);
        item = ServiceLocator.getItemHandler(userHandle).create(item);
        String id = getId(item);
        String md = getModificationDate(item);
        String param = "<param last-modification-date=\"" + md + "\">" +
        "    <url>http://localhost</url>" +
        "</param>";
        logger.debug("Param=" + param);
        String pid = ServiceLocator.getItemHandler(userHandle).assignVersionPid(id, param);
        //TODO BUN: Workaround for testing.
        pid = "hdl:someHandle/test/escidoc:145";
        logger.debug("PID=" + pid);
        long zeit = -System.currentTimeMillis();
        item = ServiceLocator.getItemHandler(userHandle).retrieve(pid);
        zeit += System.currentTimeMillis();
        logger.info("retrieveContentItembyPid(" + pid + ")->" + zeit + "ms");
        logger.debug("ContentItem(" + pid + ")=" + item);
        assertNotNull(item);
    }

    /**
     * Test method for {@link de.escidoc.www.services.om.ItemHandlerLocal#update(java.lang.String,java.lang.String)}.
     */
    @Test
    public void updateContentItem() throws Exception
    {
        String item = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_FILE));
        String id = getId(item);
        long zeit = -System.currentTimeMillis();
        ServiceLocator.getItemHandler(userHandle).update(id, item);
        zeit += System.currentTimeMillis();
        logger.info("updateContentItem(" + id + ")->" + zeit + "ms");
    }

    /**
     * Test method for {@link de.escidoc.www.services.om.ItemHandlerLocal#update(java.lang.String,java.lang.String)}.
     */
    @Test(expected = ItemNotFoundException.class)
    public void updateContentItemNotExisting() throws Exception
    {
        String item = readFile(ITEM_FILE);
        String id = ILLEGAL_ID;
        long zeit = -System.currentTimeMillis();
        try
        {
            ServiceLocator.getItemHandler(userHandle).update(id, item);
        }
        finally
        {
            zeit += System.currentTimeMillis();
            logger.info("updateContentItemNotExisting(" + id + ")->" + zeit + "ms");
        }
    }

    /**
     * Test method for {@link de.escidoc.www.services.om.ItemHandlerLocal#delete(java.lang.String)}.
     */
    @Test
    public void deleteContentItem() throws Exception
    {
        String item = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_FILE));
        String id = getId(item);
        long zeit = -System.currentTimeMillis();
        ServiceLocator.getItemHandler(userHandle).delete(id);
        zeit += System.currentTimeMillis();
        logger.info("deleteContentItem(" + id + ")->" + zeit + "ms");
    }

    /**
     * Test method for {@link de.escidoc.www.services.om.ItemHandlerLocal#delete(java.lang.String)}.
     */
    @Test(expected = ItemNotFoundException.class)
    public void deleteContentItemNotExisting() throws Exception
    {
        String id = ILLEGAL_ID;
        long zeit = -System.currentTimeMillis();
        try
        {
            ServiceLocator.getItemHandler(userHandle).delete(id);
        }
        finally
        {
            zeit += System.currentTimeMillis();
            logger.info("deleteContentItemNotExisting(" + id + ")->" + zeit + "ms");
        }
    }

    /**
     * Test method for {@link de.escidoc.www.services.om.ItemHandlerLocal#submit(java.lang.String,java.lang.String)}.
     */
    @Test
    public void submitContentItem() throws Exception
    {
        String item = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_FILE));
        String id = getId(item);
        String md = getModificationDate(item);
        long zeit = -System.currentTimeMillis();
        ServiceLocator.getItemHandler(userHandle).submit(id, createModificationDate(md));
        zeit += System.currentTimeMillis();
        logger.info("submitContentItem(" + id + ")->" + zeit + "ms");
    }

    /**
     * Test method for {@link de.escidoc.www.services.om.ItemHandlerLocal#submit(java.lang.String,java.lang.String)}.
     */
    @Test(expected = ItemNotFoundException.class)
    public void submitContentItemNotExisting() throws Exception
    {
        String id = ILLEGAL_ID;
        String md = createModificationDate("1967-08-13T12:00:00.000+01:00");
        long zeit = -System.currentTimeMillis();
        try
        {
            ServiceLocator.getItemHandler(userHandle).submit(id, md);
        }
        finally
        {
            zeit += System.currentTimeMillis();
            logger.info("submitContentItemNotExisting(" + id + ")->" + zeit + "ms");
        }
    }

    /**
     * Test method for {@link de.escidoc.www.services.om.ItemHandlerLocal#release(java.lang.String,java.lang.String)}.
     */
    @Test
    public void releaseContentItem() throws Exception
    {
        userHandle = loginLibrarian();
        String item = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_FILE));
        String id = getId(item);
        String md = getModificationDate(item);
        ServiceLocator.getItemHandler(userHandle).submit(id, createModificationDate(md));
        item = ServiceLocator.getItemHandler(userHandle).retrieve(id);
        md = getModificationDate(item);
        String param = "<param last-modification-date=\"" + md + "\">" + "<url>http://localhost</url>" + "</param>";
        ServiceLocator.getItemHandler(userHandle).assignObjectPid(id, param);
        item = ServiceLocator.getItemHandler(userHandle).retrieve(id);
        md = getModificationDate(item);
        param = "<param last-modification-date=\"" + md + "\">" + "<url>http://localhost</url>" + "</param>";
        ServiceLocator.getItemHandler(userHandle).assignVersionPid(id+":1", param);
        item = ServiceLocator.getItemHandler(userHandle).retrieve(id);
        md = getModificationDate(item);
        long zeit = -System.currentTimeMillis();
        ServiceLocator.getItemHandler(userHandle).release(id, createModificationDate(md));
        zeit += System.currentTimeMillis();
        logger.info("releaseContentItem(" + id + ")->" + zeit + "ms");
    }

    /**
     * Test method for {@link de.escidoc.www.services.om.ItemHandlerLocal#release(java.lang.String,java.lang.String)}.
     */
    @Test(expected = ItemNotFoundException.class)
    public void releaseContentItemNotExisting() throws Exception
    {
        String id = ILLEGAL_ID;
        String md = createModificationDate("1967-08-13T12:00:00.000+01:00");
        long zeit = -System.currentTimeMillis();
        try
        {
            ServiceLocator.getItemHandler(userHandle).release(id, md);
            assertTrue(false);
        }
        finally
        {
            zeit += System.currentTimeMillis();
            logger.info("releaseContentItemNotExisting(" + id + ")->" + zeit + "ms");
        }
    }

    /**
     * Test method for {@link de.escidoc.www.services.om.ItemHandlerLocal#update(java.lang.String,java.lang.String)}.
     */
    @Test
    public void modifyContentItem() throws Exception
    {
        userHandle = loginLibrarian();
        String item = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_FILE));
        String id = getId(item);
        String md = getModificationDate(item);
        ServiceLocator.getItemHandler(userHandle).submit(id, createModificationDate(md));
        item = ServiceLocator.getItemHandler(userHandle).retrieve(id);
        md = getModificationDate(item);
        String param = "<param last-modification-date=\"" + md + "\">" + "    <url>http://localhost</url>" + "</param>";
        ServiceLocator.getItemHandler(userHandle).assignObjectPid(id, param);
        item = ServiceLocator.getItemHandler(userHandle).retrieve(id);
        md = getModificationDate(item);
        param = "<param last-modification-date=\"" + md + "\">" + "    <url>http://localhost</url>" + "</param>";
        ServiceLocator.getItemHandler(userHandle).assignVersionPid(id+":1", param);
        item = ServiceLocator.getItemHandler(userHandle).retrieve(id);
        md = getModificationDate(item);
        ServiceLocator.getItemHandler(userHandle).release(id, createModificationDate(md));
        item = ServiceLocator.getItemHandler(userHandle).retrieve(id);
        long zeit = -System.currentTimeMillis();
        item = item.replace("NEW", "UPDATED");
        item = ServiceLocator.getItemHandler(userHandle).update(id, item);
        assertFalse(md.equals(getModificationDate(item)));
        zeit += System.currentTimeMillis();
        logger.info("modifyContentItem(" + id + ")->" + zeit + "ms");
        logger.debug("Item=" + item);
    }

    /**
     * Test method for {@link de.escidoc.www.services.om.ItemHandlerLocal#assignVersionPid
     */
    @Test
    public void assignPidToContentItem() throws Exception
    {
        userHandle = loginLibrarian();
        String item = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_FILE));
        String id = getId(item);
        String md = getModificationDate(item);
        ServiceLocator.getItemHandler(userHandle).submit(id, createModificationDate(md));
        item = ServiceLocator.getItemHandler(userHandle).retrieve(id);
        md = getModificationDate(item);
        String param = "<param last-modification-date=\"" + md + "\">" + "    <url>http://localhost</url>" + "</param>";
        ServiceLocator.getItemHandler(userHandle).assignObjectPid(id, param);
        item = ServiceLocator.getItemHandler(userHandle).retrieve(id);
        logger.info(item);
        md = getModificationDate(item);
        param = "<param last-modification-date=\"" + md + "\">" + "    <url>http://localhost</url>" + "</param>";
        ServiceLocator.getItemHandler(userHandle).assignVersionPid(id+":1", param);
        item = ServiceLocator.getItemHandler(userHandle).retrieve(id);
        md = getModificationDate(item);
        ServiceLocator.getItemHandler(userHandle).release(id, createModificationDate(md));
        item = ServiceLocator.getItemHandler(userHandle).retrieve(id);
        id = getVersion(item);
        md = getModificationDate(item);
        String param2 = "<param last-modification-date=\"" + md + "\">" +
                       "    <url>http://localhost</url>" +
                       "</param>";
        logger.debug("Param=" + param2);
        long zeit = -System.currentTimeMillis();
        //String pid = ServiceLocator.getItemHandler(userHandle).assignVersionPid(id, param);
        //logger.debug("PID=" + pid);
        zeit += System.currentTimeMillis();
        logger.info("assignPidToContentItem(" + id + ")->" + zeit + "ms");
    }

    /**
     * Test method for {@link de.escidoc.www.services.om.ItemHandlerLocal#assignVersionPid
     */
    @Test(expected = ItemNotFoundException.class)
    public void assignPidToContentItemNotExisting() throws Exception
    {
        String id = ILLEGAL_ID;
        String md = createModificationDate("1967-08-13T12:00:00.000+01:00");
        String param = "<param last-modification-date=\"" + md + "\">" +
                       "    <url>http://localhost</url>" +
                       "</param>";
        long zeit = -System.currentTimeMillis();
        try
        {
            ServiceLocator.getItemHandler(userHandle).assignVersionPid(id, param);
            assertTrue(false);
        }
        finally
        {
            zeit += System.currentTimeMillis();
            logger.info("assignPidToContentItemNotExisting(" + id + ")->" + zeit + "ms");
        }
    }

    /**
     * Test method for {@link de.escidoc.www.services.om.ItemHandlerLocal#release(java.lang.String,java.lang.String)}.
     */
    @Test
    public void withdrawContentItem() throws Exception
    {
        userHandle = loginLibrarian();
        String item = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_FILE));
        String id = getId(item);
        String md = getModificationDate(item);
        ServiceLocator.getItemHandler(userHandle).submit(id, createModificationDate(md));
        item = ServiceLocator.getItemHandler(userHandle).retrieve(id);
        md = getModificationDate(item);
        //md = createModificationDate(md);
        logger.info("release(" + id + "," + md + ")");
        String param = "<param last-modification-date=\"" + md + "\">" + "<url>http://localhost</url>" + "</param>";
        logger.info(param);
        ServiceLocator.getItemHandler(userHandle).assignObjectPid(id, param);
        item = ServiceLocator.getItemHandler(userHandle).retrieve(id);
        md = getModificationDate(item);
        param = "<param last-modification-date=\"" + md + "\">" + "<url>http://localhost</url>" + "</param>";
        ServiceLocator.getItemHandler(userHandle).assignVersionPid(id+":1", param);
        item = ServiceLocator.getItemHandler(userHandle).retrieve(id);
        md = getModificationDate(item);
        md = createModificationDate(md);
        ServiceLocator.getItemHandler(userHandle).release(id, md);
        item = ServiceLocator.getItemHandler(userHandle).retrieve(id);
        md = getModificationDate(item);
        md = createModificationDate(md);
        md = md.substring(0, md.length() - 2) + "><withdraw-comment>The item is withdrawn.</withdraw-comment></param>";
        logger.info("withdraw(" + id + "," + md + ")");
        long zeit = -System.currentTimeMillis();
        ServiceLocator.getItemHandler(userHandle).withdraw(id, md);
        zeit += System.currentTimeMillis();
        logger.info("withdrawContentItem(" + id + "," + md + ")->" + zeit + "ms");
    }

    /**
     * Test method for {@link de.escidoc.www.services.om.ItemHandlerLocal#release(java.lang.String,java.lang.String)}.
     */
    @Test(expected = ItemNotFoundException.class)
    public void withdrawContentItemNotExisting() throws Exception
    {
        String id = ILLEGAL_ID;
        String md = createModificationDate("1967-08-13T12:00:00.000+01:00");
        md = md.substring(0, md.length() - 2) + "><withdraw-comment>The item is withdrawn.</withdraw-comment></param>";
        logger.info("withdraw(" + id + "," + md + ")");
        long zeit = -System.currentTimeMillis();
        try
        {
            ServiceLocator.getItemHandler(userHandle).withdraw(id, md);
            assertTrue(false);
        }
        finally
        {
            zeit += System.currentTimeMillis();
            logger.info("withdrawContentItemNotExisting(" + id + "," + md + ")->" + zeit + "ms");
        }
    }

    /**
     * Test method for {@link de.escidoc.www.services.om.ItemHandlerLocal#release(java.lang.String,java.lang.String)}.
     */
    @Test(expected = InvalidXmlException.class)
    // CHAGED IN RC1.20 and above
    // @Test(expected = AuthorizationException.class)
    public void withdrawContentItemNotAuthorized() throws Exception
    {
        String item = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_FILE));
        String id = getId(item);
        String md = getModificationDate(item);
        // log in as user X, submit+release an item
        ServiceLocator.getItemHandler(userHandle).submit(id, createModificationDate(md));
        item = ServiceLocator.getItemHandler(userHandle).retrieve(id);
        md = getModificationDate(item);
        md = createModificationDate(md);
        logger.info("release(" + id + "," + md + ")");
        String param = "<param last-modification-date=\"" + md + "\">" + "    <url>http://localhost</url>" + "</param>";
        ServiceLocator.getItemHandler(userHandle).assignObjectPid(id, param);
        ServiceLocator.getItemHandler(userHandle).release(id, md);
        item = ServiceLocator.getItemHandler(userHandle).retrieve(id);
        md = getModificationDate(item);
        md = createModificationDate(md);
        md = md.substring(0, md.length() - 2) + "><withdraw-comment>The item is withdrawn.</withdraw-comment></param>";
        logger.info("withdraw(" + id + "," + md + ")");
        // log in as user Y (without administrator privileges!) and try to withdraw the item
        String otherUserHandle = null;
        otherUserHandle = loginAuthor();
        long zeit = -System.currentTimeMillis();
        ServiceLocator.getItemHandler(otherUserHandle).withdraw(id, md);
        zeit += System.currentTimeMillis();
        logger.info("withdrawContentItem(" + id + "," + md + ")->" + zeit + "ms");
    }

    /**
     * Test method for {@link de.escidoc.www.services.om.ItemHandlerLocal#retrieveVersionHistory(java.lang.String)}.
     */
    @Test
    public void retrieveContentItemHistory() throws Exception
    {
        String item = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_FILE));
        String id = getId(item);
        item = ServiceLocator.getItemHandler(userHandle).update(id, item);
        item = ServiceLocator.getItemHandler(userHandle).update(id, item);
        String md = getModificationDate(item);
        md = createModificationDate(md);
        logger.info("submit(" + id + "," + md + ")");
        ServiceLocator.getItemHandler(userHandle).submit(id, md);
        item = ServiceLocator.getItemHandler(userHandle).retrieve(id);
        md = getModificationDate(item);
        md = createModificationDate(md);
        logger.info("release(" + id + "," + md + ")");
        String param = "<param last-modification-date=\"" + md + "\">" + "    <url>http://localhost</url>" + "</param>";
        //ServiceLocator.getItemHandler(userHandle).assignObjectPid(id, param);
        //ServiceLocator.getItemHandler(userHandle).release(id, md);
        long zeit = -System.currentTimeMillis();
        String history = ServiceLocator.getItemHandler(userHandle).retrieveVersionHistory(id);
        zeit += System.currentTimeMillis();
        logger.info("retrieveContentItemHistory(" + id + ")->" + zeit + "ms");
        logger.debug("ContentItemHistory(" + id + ")=" + history);
        assertNotNull(item);
    }

    /**
     * Test method for {@link de.escidoc.www.services.om.ItemHandlerLocal#retrieveVersionHistory(java.lang.String)}.
     */
    @Test(expected = ItemNotFoundException.class)
    public void retrieveContentItemHistoryNotExisting() throws Exception
    {
        String id = ILLEGAL_ID;
        long zeit = -System.currentTimeMillis();
        try
        {
            ServiceLocator.getItemHandler(userHandle).retrieveVersionHistory(id);
            assertTrue(false);
        }
        finally
        {
            zeit += System.currentTimeMillis();
            logger.info("retrieveContentItemHistoryNotExisting(" + id + ")->" + zeit + "ms");
        }
    }
}
