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
package test.framework.om;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.Test;

import test.framework.TestBase;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Test cases for the basic service ContextHandler.
 * 
 * @author Johannes Mueller (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @revised by FrW: 10.03.2008
 */
public class TestContext extends TestBase
{
    private static final String PUBMAN_COLLECTION_ID = "escidoc:2001";
    private static final String SWB_INSTANCE = "escidoc:2002";
    private static final String ILLEGAL_CONTEXT_ID = "escidoc:persistentX";

    private static final String FILTER_USER = "user";
    private static final String FILTER_ROLE = "role";
    private static final String NAME_VALUE_ESIDOCX = "escidoc:X";
    private static final String ROLE_VALUE_DEPOSITOR = "Depositor";

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
        long zeit = -System.currentTimeMillis();
        String contexts = ServiceLocator.getContextHandler(userHandle).retrieveContexts(filterMap);
        zeit += System.currentTimeMillis();
        logger.info("retrieveContexts(" + filterMap + ")->" + zeit + "ms");
        logger.debug("Contexts()=" + contexts);        
        assertNotNull(contexts);
        System.out.println(contexts);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveContexts(java.lang.String,java.lang.String)}.
     */
    @Test
    public void retrievePublicContexts() throws Exception
    {
        long zeit = -System.currentTimeMillis();
        String contexts = ServiceLocator.getContextHandler().retrieveContexts(filterMap);
        zeit += System.currentTimeMillis();
        logger.info("retrievePublicContexts(" + filterMap + ")->" + zeit + "ms");
        logger.debug("Contexts()=" + contexts);
        assertNotNull(contexts);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveContexts(java.lang.String,java.lang.String)}.
     */
    @Test
    public void retrieveContextsForDepositor() throws Exception
    {
        filterMap.put(FILTER_USER, new String[]{USERID});
        filterMap.put(FILTER_ROLE, new String[]{ROLE_VALUE_DEPOSITOR});
        
        long zeit = -System.currentTimeMillis();
        String contexts = ServiceLocator.getContextHandler(userHandle).retrieveContexts(filterMap);
        zeit += System.currentTimeMillis();
        logger.info("retrieveContextsForDepositor(" + filterMap + ")->" + zeit + "ms");
        logger.debug("Contexts()=" + contexts);        
        assertNotNull(contexts);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveContexts(java.lang.String,java.lang.String)}.
     */
    @Test
    public void retrieveContextsNotExisting() throws Exception
    {
        filterMap.put(FILTER_USER, new String[]{NAME_VALUE_ESIDOCX});
        
        long zeit = -System.currentTimeMillis();
        String contexts = ServiceLocator.getContextHandler(userHandle).retrieveContexts(filterMap);
        zeit += System.currentTimeMillis();
        logger.info("retrieveContextsNotExisting(" + filterMap + ")->" + zeit + "ms");
        logger.debug("Contexts(" + filterMap + ")=" + contexts);
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
        long zeit = -System.currentTimeMillis();
        try
        {
            String members = ServiceLocator.getContextHandler(userHandle).retrieveMembers(ILLEGAL_CONTEXT_ID, filterMap);
            assertTrue(members, false);
        }
        finally
        {
            zeit += System.currentTimeMillis();
            logger.info("retrieveMembersContextNotFound(" + ILLEGAL_CONTEXT_ID + "," + filterMap + ")->" + zeit + "ms");
        }
    }
}
