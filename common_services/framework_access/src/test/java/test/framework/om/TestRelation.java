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

import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Testcases for the relation part of the basic service ItemHandler.
 *
 * @author pbroszei (initial creation)
 * @author $Author: wfrank $ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class TestRelation extends TestItemBase
{
    private Logger logger = Logger.getLogger(getClass());

    /** 
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#addContentRelations
     */
    @Test
    public void addRelationToContentItem() throws Exception
    {
        String target = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_FILE));
        String targetId = getId(target);
        String targetMd = getModificationDate(target);
        String source = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_FILE));
        String sourceId = getId(source);
        String sourceMd = getModificationDate(source);
        String param = "<param last-modification-date=\"" + sourceMd + "\">" +
                       "    <relation>" +
                       "        <targetId>" + targetId + "</targetId>" +
                       "        <predicate>" + PREDICATE_ISREVISIONOF + "</predicate>" +
                       "    </relation>" +
                       "</param>";
        logger.debug("Param=" + param);
        long zeit = -System.currentTimeMillis();
        ServiceLocator.getItemHandler(userHandle).addContentRelations(sourceId, param);
        zeit += System.currentTimeMillis();
        logger.info("addRelationToContentItem(" + sourceId + ")->" + zeit + "ms");
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#addContentRelations
     */
    @Test(expected = ItemNotFoundException.class)
    public void addRelationToContentItemNotExisting() throws Exception
    {
        String id = ILLEGAL_ID;
        //String md = createModificationDate("1967-08-13T12:00:00.000+01:00");
        String md = "1967-08-13T12:00:00.000+01:00";
        String param = "<param last-modification-date=\"" + md + "\">" +
                       "    <relation>" +
                       "        <targetId>" + id + "</targetId>" +
                       "        <predicate>" + PREDICATE_ISREVISIONOF + "</predicate>" +
                       "    </relation>" +
                       "</param>";
        logger.debug("Param=" + param);
        long zeit = -System.currentTimeMillis();
        try
        {
            ServiceLocator.getItemHandler(userHandle).addContentRelations(id, param);
            assertTrue(false);
        }
        finally
        {
            zeit += System.currentTimeMillis();
            logger.info("addRelationToContentItemNotExisting(" + id + ")->" + zeit + "ms");
        }
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveRelations
     */
    @Test
    public void retrieveRelationOfContentItem() throws Exception
    {
        userHandle = loginLibrarian();
        String target = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_FILE));
        String targetId = getId(target);
        String targetMd = getModificationDate(target);
        logger.debug("targetId=" + targetId);
        userHandle = loginScientist();
        String source = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_FILE));
        String sourceId = getId(source);
        String sourceMd = getModificationDate(source);
        logger.debug("sourceId=" + sourceId);
        String param = "<param last-modification-date=\"" + sourceMd + "\">" +
                       "    <relation>" +
                       "        <targetId>" + targetId + "</targetId>" +
                       "        <predicate>" + PREDICATE_ISREVISIONOF + "</predicate>" +
                       "    </relation>" +
                       "</param>";
        logger.debug("Param=" + param);
        ServiceLocator.getItemHandler(userHandle).addContentRelations(sourceId, param);
        long zeit = -System.currentTimeMillis();
        String relations = ServiceLocator.getItemHandler(userHandle).retrieveRelations(sourceId);
        zeit += System.currentTimeMillis();
        logger.debug("Relations=" + relations);
        logger.info("retrieveRelationOfContentItem(" + sourceId + ")->" + zeit + "ms");
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveRelations
     */
    @Test(expected = ItemNotFoundException.class)
    public void retrieveRelationOfContentItemNotExisting() throws Exception
    {
        String sourceId = ILLEGAL_ID;
        String sourceMd = createModificationDate("1967-08-13T12:00:00.000+01:00");
        long zeit = -System.currentTimeMillis();
        try
        {
            ServiceLocator.getItemHandler(userHandle).retrieveRelations(sourceId);
        }
        finally
        {
            zeit += System.currentTimeMillis();
            logger.info("retrieveRelationOfContentItemNotExisting(" + sourceId + ")->" + zeit + "ms");
        }
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#removeContentRelations
     */
    @Test
    public void removeRelationFromContentItem() throws Exception
    {
        String target = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_FILE));
        String targetId = getId(target);
        String targetMd = getModificationDate(target);
        String source = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_FILE));
        String sourceId = getId(source);
        String sourceMd = getModificationDate(source);
        String param = "<param last-modification-date=\"" + sourceMd + "\">" +
                       "    <relation>" +
                       "        <targetId>" + targetId + "</targetId>" +
                       "        <predicate>" + PREDICATE_ISREVISIONOF + "</predicate>" +
                       "    </relation>" +
                       "</param>";
        logger.debug("Param=" + param);
        ServiceLocator.getItemHandler(userHandle).addContentRelations(sourceId, param);
        source = ServiceLocator.getItemHandler(userHandle).retrieve(sourceId);
        sourceMd = getModificationDate(source);
        param = "<param last-modification-date=\"" + sourceMd + "\">" +
                "    <relation>" +
                "        <targetId>" + targetId + "</targetId>" +
                "        <predicate>" + PREDICATE_ISREVISIONOF + "</predicate>" +
                "    </relation>" +
                "</param>";
        logger.debug("Param=" + param);
        long zeit = -System.currentTimeMillis();
        ServiceLocator.getItemHandler(userHandle).removeContentRelations(sourceId, param);
        zeit += System.currentTimeMillis();
        logger.info("removeRelationFromContentItem(" + sourceId + ")->" + zeit + "ms");
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#removeContentRelations
     */
    @Test(expected = ItemNotFoundException.class)
    public void removeRelationFromContentItemNotExisting() throws Exception
    {
        String sourceId = ILLEGAL_ID;
        //String sourceMd = createModificationDate("1967-08-13T12:00:00.000+01:00");
        String sourceMd = "1967-08-13T12:00:00.000+01:00";
        String param = "<param last-modification-date=\"" + sourceMd + "\">" +
                       "    <relation>" +
                       "        <targetId>" + sourceId + "</targetId>" +
                       "        <predicate>" + PREDICATE_ISREVISIONOF + "</predicate>" +
                       "    </relation>" +
                       "</param>";
        logger.debug("Param=" + param);
        long zeit = -System.currentTimeMillis();
        try
        {
            ServiceLocator.getItemHandler(userHandle).removeContentRelations(sourceId, param);
            assertTrue(false);
        }
        finally
        {
            zeit += System.currentTimeMillis();
            logger.info("removeRelationFromContentItemNotExisting(" + sourceId + ")->" + zeit + "ms");
        }
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#removeContentRelations
     */
    @Test(expected = ContentRelationNotFoundException.class)
    public void removeRelationFromContentItemRelationNotExisting() throws Exception
    {
        String source = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_FILE));
        String sourceId = getId(source);
        String sourceMd = getModificationDate(source);
        String targetId = ILLEGAL_ID;
        String param = "<param last-modification-date=\"" + sourceMd + "\">" +
                       "    <relation>" +
                       "        <targetId>" + targetId + "</targetId>" +
                       "        <predicate>" + PREDICATE_ISREVISIONOF + "</predicate>" +
                       "    </relation>" +
                       "</param>";
        logger.debug("Param=" + param);
        long zeit = -System.currentTimeMillis();
        try
        {
            ServiceLocator.getItemHandler(userHandle).removeContentRelations(sourceId, param);
            assertTrue(false);
        }
        finally
        {
            zeit += System.currentTimeMillis();
            logger.info("removeRelationFromContentItemRelationNotExisting(" + sourceId + ")->" + zeit + "ms");
        }
    }
}
