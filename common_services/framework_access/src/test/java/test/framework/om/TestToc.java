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

import de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.TocNotFoundException;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Testcases for the basic service TocHandler.
 * 
 * @author Wilhelm Frank (initial creation)
 * @author $Author: wfrank $ (last modification)
 */
public class TestToc extends TestItemBase
{
    private Logger logger = Logger.getLogger(getClass());

    /**
     * Test method for {@link de.fiz.escidoc.om.TocHandler#create(java.lang.String)}.
     */
    @Test
    public void createToc() throws Exception
    {
        userHandle = loginSystemAdministrator();
        String toc = readFile(TOC_FILE);
        long zeit = -System.currentTimeMillis();
        toc = ServiceLocator.getTocHandler(userHandle).create(toc);
        zeit += System.currentTimeMillis();
        logger.info("createToc()->" + zeit + "ms");
        logger.debug("Toc()=" + toc);
        assertNotNull(toc);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.TocHandler#retrieve(java.lang.String)}.
     */
    @Test
    public void retrieveToc() throws Exception
    {
        userHandle = loginSystemAdministrator();
        String toc = readFile(TOC_FILE);
        toc = ServiceLocator.getTocHandler(userHandle).create(toc);
        String id = getId(toc);
        long zeit = -System.currentTimeMillis();
        toc = ServiceLocator.getTocHandler(userHandle).retrieve(id);
        zeit += System.currentTimeMillis();
        logger.info("retrieveToc(" + id + ")->" + zeit + "ms");
        logger.debug("Toc(" + id + ")=" + toc);
        assertNotNull(toc);
    }

     /**
     * Test method for {@link de.fiz.escidoc.om.TocHandler#retrieve(java.lang.String)}.
     */
    @Test(expected = TocNotFoundException.class)
    public void retrieveTocNotExisting() throws Exception
    {
        String id = ILLEGAL_ID;
        long zeit = -System.currentTimeMillis();
        try
        {
            String toc = ServiceLocator.getTocHandler(userHandle).retrieve(id);
            assertTrue(toc, false);
        }
        finally
        {
            zeit += System.currentTimeMillis();
            logger.info("retrieveTocNotExisting(" + id + ")->" + zeit + "ms");
        }
    }

    
    /**
     * Test method for {@link de.fiz.escidoc.om.TocHandler#update(java.lang.String,java.lang.String)}.
     */
    @Test
    public void updateToc() throws Exception
    {
        userHandle = loginSystemAdministrator();
        String toc = ServiceLocator.getTocHandler(userHandle).create(readFile(TOC_FILE));
        String id = getId(toc);
        long zeit = -System.currentTimeMillis();
        ServiceLocator.getTocHandler(userHandle).update(id, toc);
        zeit += System.currentTimeMillis();
        logger.info("updateToc(" + id + ")->" + zeit + "ms");
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.TocHandler#update(java.lang.String,java.lang.String)}.
     */
    @Test(expected = TocNotFoundException.class)
    public void updateTocNotExisting() throws Exception
    {
        String toc = readFile(TOC_FILE);
        String id = ILLEGAL_ID;
        long zeit = -System.currentTimeMillis();
        try
        {
            ServiceLocator.getTocHandler(userHandle).update(id, toc);
        }
        finally
        {
            zeit += System.currentTimeMillis();
            logger.info("updateTocNotExisting(" + id + ")->" + zeit + "ms");
        }
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.TocHandler#delete(java.lang.String)}.
     */
    @Test
    public void deleteToc() throws Exception
    {
        userHandle = loginSystemAdministrator();
        String toc = ServiceLocator.getTocHandler(userHandle).create(readFile(TOC_FILE));
        String id = getId(toc);
        long zeit = -System.currentTimeMillis();
        ServiceLocator.getTocHandler(userHandle).delete(id);
        zeit += System.currentTimeMillis();
        logger.info("deleteToc(" + id + ")->" + zeit + "ms");
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.TocHandler#delete(java.lang.String)}.
     */
    @Test(expected = TocNotFoundException.class)
    public void deleteTocNotExisting() throws Exception
    {
        String id = ILLEGAL_ID;
        long zeit = -System.currentTimeMillis();
        try
        {
            ServiceLocator.getTocHandler(userHandle).delete(id);
        }
        finally
        {
            zeit += System.currentTimeMillis();
            logger.info("deleteTocNotExisting(" + id + ")->" + zeit + "ms");
        }
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.TocHandler#update(java.lang.String,java.lang.String)}.
     */
    @Test
    public void modifyToc() throws Exception
    {
        userHandle = loginSystemAdministrator();
        String toc = ServiceLocator.getTocHandler(userHandle).create(readFile(TOC_FILE));
        String id = getId(toc);
        String md = getModificationDate(toc);
        ServiceLocator.getTocHandler(userHandle).submit(id, createModificationDate(md));
        toc = ServiceLocator.getTocHandler(userHandle).retrieve(id);
        md = getModificationDate(toc);
        // String param = "<param last-modification-date=\"" + md + "\">" + "<url>http://localhost</url>" + "</param>";
        // ServiceLocator.getTocHandler(userHandle).assignVersionPid(id+":1", param);
        // ServiceLocator.getTocHandler(userHandle).assignObjectPid(id, param);
        ServiceLocator.getTocHandler(userHandle).release(id, createModificationDate(md));
        long zeit = -System.currentTimeMillis();
        toc = toc.replace("NEW", "UPDATED");
        toc = ServiceLocator.getTocHandler(userHandle).update(id, toc);
        toc = ServiceLocator.getTocHandler(userHandle).retrieve(id);
        assertFalse(md.equals(getModificationDate(toc)));
        zeit += System.currentTimeMillis();
        logger.info("modifyToc(" + id + ")->" + zeit + "ms");
        logger.debug("Toc=" + toc);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.TocHandler#retrieveVersionHistory(java.lang.String)}.
     */
    @Test
    public void retrieveTocHistory() throws Exception
    {
        userHandle = loginSystemAdministrator();
        String toc = ServiceLocator.getTocHandler(userHandle).create(readFile(TOC_FILE));
        String id = getId(toc);
        toc = ServiceLocator.getTocHandler(userHandle).update(id, toc);
        toc = ServiceLocator.getTocHandler(userHandle).update(id, toc);
        String md = getModificationDate(toc);
        md = createModificationDate(md);
        logger.info("submit(" + id + "," + md + ")");
        ServiceLocator.getTocHandler(userHandle).submit(id, md);
        toc = ServiceLocator.getTocHandler(userHandle).retrieve(id);
        md = getModificationDate(toc);
        // String param = "<param last-modification-date=\"" + md + "\">" + "<url>http://localhost</url>" + "</param>";
        // ServiceLocator.getTocHandler(userHandle).assignVersionPid(id+":1", param);
        // ServiceLocator.getTocHandler(userHandle).assignObjectPid(id, param);
        md = createModificationDate(md);
        logger.info("release(" + id + "," + md + ")");
        ServiceLocator.getTocHandler(userHandle).release(id, md);
        long zeit = -System.currentTimeMillis();
        String history = ServiceLocator.getTocHandler(userHandle).retrieveVersionHistory(id);
        zeit += System.currentTimeMillis();
        logger.info("retrieveTocHistory(" + id + ")->" + zeit + "ms");
        logger.debug("TocHistory(" + id + ")=" + history);
        assertNotNull(toc);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.TocHandler#retrieveVersionHistory(java.lang.String)}.
     */
    @Test(expected = TocNotFoundException.class)
    public void retrieveTocHistoryNotExisting() throws Exception
    {
        String id = ILLEGAL_ID;
        long zeit = -System.currentTimeMillis();
        try
        {
            ServiceLocator.getTocHandler(userHandle).retrieveVersionHistory(id);
            assertTrue(false);
        }
        finally
        {
            zeit += System.currentTimeMillis();
            logger.info("retrieveTocHistoryNotExisting(" + id + ")->" + zeit + "ms");
        }
    }
}
