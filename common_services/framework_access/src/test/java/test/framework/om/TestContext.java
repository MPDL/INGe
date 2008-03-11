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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.Test;

import test.framework.TestBase;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Testcases for the basic service ContextHandler.
 * 
 * @author Johannes Mueller (initial creation)
 * @author $Author: wfrank $ (last modification)
 * @version $Revision: 314 $ $LastChangedDate: 2007-11-07 13:12:14 +0100 (Wed, 07 Nov 2007) $
 * @revised by FrW: 10.03.2008
 */
public class TestContext extends TestBase
{
    private static final String PUBMAN_COLLECTION_ID = "escidoc:persistent3";
    private static final String SWB_INSTANCE = "escidoc:persistent5";
    private static final String ILLEGAL_CONTEXT_ID = "escidoc:persistentX";
    private static final String FILTER_ALL = "<param></param>";
    private static final String FILTER_NONE = "<param><filter name=\"user\">escidoc:X</filter></param>";
    private static final String FILTER_DEPOSITOR = "<param>" + 
                                                   "<filter name=\"user\">" + USERID + "</filter>" +
                                                   "<filter name=\"role\">Depositor</filter>" +
                                                   "</param>"; 

    private Logger logger = Logger.getLogger(getClass());

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieve(java.lang.String)}.
     */
    @Test
    public void retrievePubCollection() throws Exception
    {
        String id = PUBMAN_COLLECTION_ID;
        long zeit = -System.currentTimeMillis();
        String context = ServiceLocator.getContextHandler(userHandle).retrieve(id);
        zeit += System.currentTimeMillis();
        logger.info("retrievePubCollection(" + id + ")->" + zeit + "ms");
        logger.debug("PubCollection(" + id + ")=" + context);
        assertNotNull(context);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieve(java.lang.String)}.
     */
    @Test
    public void retrieveSwbInstance() throws Exception
    {
        String id = SWB_INSTANCE;
        long zeit = -System.currentTimeMillis();
        String context = ServiceLocator.getContextHandler(userHandle).retrieve(id);
        zeit += System.currentTimeMillis();
        logger.info("retrieveSwbInstance(" + id + ")->" + zeit + "ms");
        logger.debug("SwbInstance(" + id + ")=" + context);
        assertNotNull(context);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieve(java.lang.String)}.
     */
    @Test(expected = ContextNotFoundException.class)
    public void retrieveContextNotExisting() throws Exception
    {
        String id = ILLEGAL_CONTEXT_ID;
        long zeit = -System.currentTimeMillis();
        try
        {
            String context = ServiceLocator.getContextHandler(userHandle).retrieve(id);
            assertTrue(context, false);
        }
        finally
        {
            zeit += System.currentTimeMillis();
            logger.info("retrieveContextNotExisting(" + id + ")->" + zeit + "ms");
        }
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveContexts(java.lang.String,java.lang.String)}.
     */
    @Test
    public void retrieveContexts() throws Exception
    {
        String filter = FILTER_ALL;
        long zeit = -System.currentTimeMillis();
        String contexts = ServiceLocator.getContextHandler(userHandle).retrieveContexts(filter);
        zeit += System.currentTimeMillis();
        logger.info("retrieveContexts(" + filter + ")->" + zeit + "ms");
        logger.debug("Contexts()=" + contexts);        
        assertNotNull(contexts);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveContexts(java.lang.String,java.lang.String)}.
     */
    @Test
    public void retrievePublicContexts() throws Exception
    {
        String filter = FILTER_ALL;
        long zeit = -System.currentTimeMillis();
        String contexts = ServiceLocator.getContextHandler().retrieveContexts(filter);
        zeit += System.currentTimeMillis();
        logger.info("retrievePublicContexts(" + filter + ")->" + zeit + "ms");
        logger.debug("Contexts()=" + contexts);
        assertNotNull(contexts);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveContexts(java.lang.String,java.lang.String)}.
     */
    @Test
    public void retrieveContextsForDepositor() throws Exception
    {
        String filter = FILTER_DEPOSITOR;
        long zeit = -System.currentTimeMillis();
        String contexts = ServiceLocator.getContextHandler(userHandle).retrieveContexts(filter);
        zeit += System.currentTimeMillis();
        logger.info("retrieveContextsForDepositor(" + filter + ")->" + zeit + "ms");
        logger.debug("Contexts()=" + contexts);        
        assertNotNull(contexts);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveContexts(java.lang.String,java.lang.String)}.
     */
    @Test
    public void retrieveContextsNotExisting() throws Exception
    {
        String filter = FILTER_NONE;
        long zeit = -System.currentTimeMillis();
        String contexts = ServiceLocator.getContextHandler(userHandle).retrieveContexts(filter);
        zeit += System.currentTimeMillis();
        logger.info("retrieveContextsNotExisting(" + filter + ")->" + zeit + "ms");
        logger.debug("Contexts(" + filter + ")=" + contexts);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveMembers(java.lang.String,java.lang.String)}.
     */
/*    @Test
    public void retrieveMembers() throws Exception
    {
        String id = PUBMAN_COLLECTION_ID;
        String filter = FILTER_ALL;
        long zeit = -System.currentTimeMillis();
        String members = ServiceLocator.getContextHandler(userHandle).retrieveMembers(id, filter);
        zeit += System.currentTimeMillis();
        logger.info("retrieveMembers(" + id + "," + filter + ")->" + zeit + "ms");
        logger.debug("Members(" + id + ")=" + members);
        assertNotNull(members);
    }
*/
    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveContexts(java.lang.String,java.lang.String)}.
     */
/*    @Test
    public void retrieveMembersNotExisting() throws Exception
    {
        String id = PUBMAN_COLLECTION_ID;
        String filter = FILTER_NONE;
        long zeit = -System.currentTimeMillis();
        String members = ServiceLocator.getContextHandler(userHandle).retrieveMembers(id, filter);
        zeit += System.currentTimeMillis();
        logger.info("retrieveMembersNotExisting(" + id + "," + filter + ")->" + zeit + "ms");
        logger.debug("Members(" + id + ")=" + members);
        assertNotNull(members);
    }
*/
    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveContexts(java.lang.String,java.lang.String)}.
     */
    @Test(expected = ContextNotFoundException.class)
    public void retrieveMembersContextNotFound() throws Exception
    {
        String id = ILLEGAL_CONTEXT_ID;
        String filter = FILTER_ALL;
        long zeit = -System.currentTimeMillis();
        try
        {
            String members = ServiceLocator.getContextHandler(userHandle).retrieveMembers(id, filter);
            assertTrue(members, false);
        }
        finally
        {
            zeit += System.currentTimeMillis();
            logger.info("retrieveMembersContextNotFound(" + id + "," + filter + ")->" + zeit + "ms");
        }
    }
}
