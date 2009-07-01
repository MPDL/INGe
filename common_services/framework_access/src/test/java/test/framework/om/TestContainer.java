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

import java.sql.Date;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Testcases for the basic service ContainerHandler.
 * 
 * @author Johannes Mueller (initial creation)
 * @author $Author: wfrank $ (last modification)
 * @version $Revision: 325 $ $LastChangedDate: 2007-11-28 18:07:29 +0100 (Mi, 28 Nov 2007) $
 * @revised by FrW: 10.03.2008
 */
public class TestContainer extends TestItemBase
{
    private Logger logger = Logger.getLogger(getClass());

    /**
     * Test method for {@link de.fiz.escidoc.om.ContainerHandlerLocal#create(java.lang.String)}.
     */
    @Test
    public void createContainer() throws Exception
    {
        userHandle = loginSystemAdministrator();
        String container = readFile(CONTAINER_FILE);
        long zeit = -System.currentTimeMillis();
        container = ServiceLocator.getContainerHandler(userHandle).create(container);
        zeit += System.currentTimeMillis();
        logger.info("createContainer()->" + zeit + "ms");
        logger.debug("Container()=" + container);
        assertNotNull(container);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ContainerHandlerLocal#retrieve(java.lang.String)}.
     */
    @Test
    public void retrieveContainer() throws Exception
    {
        userHandle = loginSystemAdministrator();
        String container = readFile(CONTAINER_FILE);
        container = ServiceLocator.getContainerHandler(userHandle).create(container);
        String id = getId(container);
        long zeit = -System.currentTimeMillis();
        container = ServiceLocator.getContainerHandler(userHandle).retrieve(id);
        zeit += System.currentTimeMillis();
        logger.info("retrieveContainer(" + id + ")->" + zeit + "ms");
        logger.debug("Container(" + id + ")=" + container);
        assertNotNull(container);
    }

     /**
     * Test method for {@link de.fiz.escidoc.om.ContainerHandlerLocal#retrieve(java.lang.String)}.
     */
    @Test(expected = ContainerNotFoundException.class)
    public void retrieveContainerNotExisting() throws Exception
    {
        String id = ILLEGAL_ID;
        long zeit = -System.currentTimeMillis();
        try
        {
            String container = ServiceLocator.getContainerHandler(userHandle).retrieve(id);
            assertTrue(container, false);
        }
        finally
        {
            zeit += System.currentTimeMillis();
            logger.info("retrieveContainerNotExisting(" + id + ")->" + zeit + "ms");
        }
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ContainerHandlerLocal#retrieve(java.lang.String)}.
     */
    @Ignore("Because of bug #xxx")
    @Test
    public void retrieveContainerByPid() throws Exception
    {
        String container = readFile(CONTAINER_FILE);
        container = ServiceLocator.getContainerHandler(userHandle).create(container);
        String id = getId(container);
        String md = getModificationDate(container);
        String param = "<param last-modification-date=\"" + md + "\">" +
        "    <url>http://localhost/" + System.currentTimeMillis() + "</url>" +
        "</param>";
        logger.debug("Param=" + param);
        String pid = ServiceLocator.getContainerHandler(userHandle).assignVersionPid(id, param);
        //TODO BUN: Workaround for testing.
        pid = "hdl:someHandle/test/escidoc:145";
        logger.debug("PID=" + pid);
        long zeit = -System.currentTimeMillis();
        container = ServiceLocator.getContainerHandler(userHandle).retrieve(pid);
        zeit += System.currentTimeMillis();
        logger.info("retrieveContainerbyPid(" + pid + ")->" + zeit + "ms");
        logger.debug("Container(" + pid + ")=" + container);
        assertNotNull(container);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ContainerHandlerLocal#update(java.lang.String,java.lang.String)}.
     */
    @Test
    public void updateContainer() throws Exception
    {
        userHandle = loginSystemAdministrator();
        String container = ServiceLocator.getContainerHandler(userHandle).create(readFile(CONTAINER_FILE));
        String id = getId(container);
        long zeit = -System.currentTimeMillis();
        ServiceLocator.getContainerHandler(userHandle).update(id, container);
        zeit += System.currentTimeMillis();
        logger.info("updateContainer(" + id + ")->" + zeit + "ms");
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ContainerHandlerLocal#update(java.lang.String,java.lang.String)}.
     */
    @Test(expected = ContainerNotFoundException.class)
    public void updateContainerNotExisting() throws Exception
    {
        String container = readFile(CONTAINER_FILE);
        String id = ILLEGAL_ID;
        long zeit = -System.currentTimeMillis();
        try
        {
            ServiceLocator.getContainerHandler(userHandle).update(id, container);
        }
        finally
        {
            zeit += System.currentTimeMillis();
            logger.info("updateContainerNotExisting(" + id + ")->" + zeit + "ms");
        }
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ContainerHandlerLocal#delete(java.lang.String)}.
     */
    @Test
    public void deleteContainer() throws Exception
    {
        userHandle = loginSystemAdministrator();
        String container = ServiceLocator.getContainerHandler(userHandle).create(readFile(CONTAINER_FILE));
        String id = getId(container);
        long zeit = -System.currentTimeMillis();
        ServiceLocator.getContainerHandler(userHandle).delete(id);
        zeit += System.currentTimeMillis();
        logger.info("deleteContainer(" + id + ")->" + zeit + "ms");
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ContainerHandlerLocal#delete(java.lang.String)}.
     */
    @Test(expected = ContainerNotFoundException.class)
    public void deleteContainerNotExisting() throws Exception
    {
        String id = ILLEGAL_ID;
        long zeit = -System.currentTimeMillis();
        try
        {
            ServiceLocator.getContainerHandler(userHandle).delete(id);
        }
        finally
        {
            zeit += System.currentTimeMillis();
            logger.info("deleteContainerNotExisting(" + id + ")->" + zeit + "ms");
        }
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ContainerHandlerLocal#update(java.lang.String,java.lang.String)}.
     */
    @Test
    public void modifyContainer() throws Exception
    {
        userHandle = loginSystemAdministrator();
        String container = ServiceLocator.getContainerHandler(userHandle).create(readFile(CONTAINER_FILE));
        String id = getId(container);
        String md = getModificationDate(container);
        ServiceLocator.getContainerHandler(userHandle).submit(id, createModificationDate(md));
        container = ServiceLocator.getContainerHandler(userHandle).retrieve(id);
        md = getModificationDate(container);
        //logger.info("md "+md);
        String param = "<param last-modification-date=\"" + md + "\">" +
        "    <url>http://localhost/" + System.currentTimeMillis() + "</url>" +
        "</param>";
        ServiceLocator.getContainerHandler(userHandle).assignVersionPid(id+":1", param);
        container = ServiceLocator.getContainerHandler(userHandle).retrieve(id);
        md = getModificationDate(container);
        //logger.info("md "+md);
        param = "<param last-modification-date=\"" + md + "\">" +
        "    <url>http://localhost/" + System.currentTimeMillis() + "</url>" +
        "</param>";
        ServiceLocator.getContainerHandler(userHandle).assignObjectPid(id, param);
        container = ServiceLocator.getContainerHandler(userHandle).retrieve(id);
        md = getModificationDate(container);
        ServiceLocator.getContainerHandler(userHandle).release(id, createModificationDate(md));
        long zeit = -System.currentTimeMillis();
        container = ServiceLocator.getContainerHandler(userHandle).retrieve(id);
        container = container.replace("NEW", "UPDATED");
        container = ServiceLocator.getContainerHandler(userHandle).update(id, container);
        container = ServiceLocator.getContainerHandler(userHandle).retrieve(id);
        //logger.info("other md "+getModificationDate(container));
        assertFalse(md.equals(getModificationDate(container)));
        zeit += System.currentTimeMillis();
        logger.info("modifyContainer(" + id + ")->" + zeit + "ms");
        logger.debug("Container=" + container);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ContainerHandlerLocal#retrieveVersionHistory(java.lang.String)}.
     */
    @Test
    public void retrieveContainerHistory() throws Exception
    {
        userHandle = loginSystemAdministrator();
        String container = ServiceLocator.getContainerHandler(userHandle).create(readFile(CONTAINER_FILE));
        String id = getId(container);
        container = ServiceLocator.getContainerHandler(userHandle).update(id, container);
        container = ServiceLocator.getContainerHandler(userHandle).update(id, container);
        String md = getModificationDate(container);
        md = createModificationDate(md);
        logger.info("submit(" + id + "," + md + ")");
        ServiceLocator.getContainerHandler(userHandle).submit(id, md);
        container = ServiceLocator.getContainerHandler(userHandle).retrieve(id);
        md = getModificationDate(container);
        String param = "<param last-modification-date=\"" + md + "\">" +
        "    <url>http://localhost/" + System.currentTimeMillis() + "</url>" +
        "</param>";
        logger.info("param(" + param + ")");
        ServiceLocator.getContainerHandler(userHandle).assignVersionPid(id+":1", param);
        container = ServiceLocator.getContainerHandler(userHandle).retrieve(id);
        md = getModificationDate(container);
        String param2 = "<param last-modification-date=\"" + md + "\">" +
        "    <url>http://localhost/" + System.currentTimeMillis() + "</url>" +
        "</param>";
        ServiceLocator.getContainerHandler(userHandle).assignObjectPid(id, param2);
        container = ServiceLocator.getContainerHandler(userHandle).retrieve(id);
        md = getModificationDate(container);
        md = createModificationDate(md);
        logger.info("release(" + id + "," + md + ")");
        ServiceLocator.getContainerHandler(userHandle).release(id, md);
        long zeit = -System.currentTimeMillis();
        String history = ServiceLocator.getContainerHandler(userHandle).retrieveVersionHistory(id);
        zeit += System.currentTimeMillis();
        logger.info("retrieveContainerHistory(" + id + ")->" + zeit + "ms");
        logger.debug("ContainerHistory(" + id + ")=" + history);
        assertNotNull(container);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ContainerHandlerLocal#retrieveVersionHistory(java.lang.String)}.
     */
    @Test(expected = ContainerNotFoundException.class)
    public void retrieveContainerHistoryNotExisting() throws Exception
    {
        String id = ILLEGAL_ID;
        long zeit = -System.currentTimeMillis();
        try
        {
            ServiceLocator.getContainerHandler(userHandle).retrieveVersionHistory(id);
            assertTrue(false);
        }
        finally
        {
            zeit += System.currentTimeMillis();
            logger.info("retrieveContainerHistoryNotExisting(" + id + ")->" + zeit + "ms");
        }
    }
}
