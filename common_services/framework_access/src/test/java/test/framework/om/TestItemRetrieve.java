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
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Testcases for retrieving items of the basic service ItemHandler.
 *
 * @author pbroszei (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class TestItemRetrieve extends TestItemBase
{
    private static final String FILTER_NONE = "<param><filter name=\"http://escidoc.de/core/01/structural-relations/created-by\">" + ILLEGAL_ID + "</filter></param>";

    private Logger logger = Logger.getLogger(getClass());

    private int countItems(String items) throws Exception
    {
        final String xPath = "//item-list/item";
        Document doc = getDocument(items, false);
        NodeList list = selectNodeList(doc, xPath);
        int number = list.getLength();
        logger.debug("#Items=" + number);
        return number;
    }

    private int countItemRefs(String refs) throws Exception
    {
        final String xPath = "//item-ref-list/item-ref";
        Document doc = getDocument(refs, false);
        NodeList list = selectNodeList(doc, xPath);
        int number = list.getLength();
        logger.debug("#Items=" + number);
        return number;
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveItems(java.lang.String)}.
     */
    @Test
    public void retrievePublicReleasedItems() throws Exception
    {
        String filter = "<param><filter name=\"http://escidoc.de/core/01/properties/public-status\">released</filter></param>";
        logger.debug("Filter=" + filter);
        long zeit = -System.currentTimeMillis();
        String items = ServiceLocator.getItemHandler().retrieveItems(filter);
        zeit += System.currentTimeMillis();
        logger.info("retrievePublicContentItems(" + filter + ")->" + zeit + "ms");
        logger.debug("ContentItems(" + filter + ")=" + items);
        assertNotNull(items);
        assertTrue(countItems(items) > 0);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveItems(java.lang.String)}.
     */
    @Test
    public void retrievePendingContentItems() throws Exception
    {
        String filter = "<param><filter name=\"http://escidoc.de/core/01/properties/public-status\">pending</filter></param>";
        logger.debug("Filter=" + filter);
        long zeit = -System.currentTimeMillis();
        String items = ServiceLocator.getItemHandler(userHandle).retrieveItems(filter);
        zeit += System.currentTimeMillis();
        logger.info("retrievePendingContentItems(" + filter + ")->" + zeit + "ms");
        logger.debug("ContentItems(" + filter + ")=" + items);
        assertNotNull(items);
        assertTrue(countItems(items) > 0);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveItems(java.lang.String)}.
     */
    @Test
    public void retrieveOwnContentItems() throws Exception
    {
        String filter = "<param><filter name=\"http://escidoc.de/core/01/structural-relations/created-by\">" + USERID + "</filter></param>";
        logger.debug("Filter=" + filter);
        long zeit = -System.currentTimeMillis();
        String items = ServiceLocator.getItemHandler(userHandle).retrieveItems(filter);
        zeit += System.currentTimeMillis();
        logger.info("retrieveOwnContentItems(" + filter + ")->" + zeit + "ms");
        logger.debug("ContentItems(" + filter + ")=" + items);
        assertNotNull(items);
        assertTrue(countItems(items) > 0);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveItems(java.lang.String)}.
     */
    @Test
    public void retrieveDefinedContentItems() throws Exception
    {
        String ids[] = new String[3];
        String item = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_FILE));
        ids[0] = getId(item);
        item = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_FILE));
        ids[1] = getId(item);
        item = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_FILE));
        ids[2] = getId(item);
        String filter = "<param><filter name=\"http://purl.org/dc/elements/1.1/identifier\">" + "<id>" + ids[0] + "</id>" + "<id>" + ids[1] + "</id>" + "<id>" + ids[2] + "</id>" + "</filter></param>";
        logger.debug("Filter=" + filter);
        long zeit = -System.currentTimeMillis();
        String items = ServiceLocator.getItemHandler(userHandle).retrieveItems(filter);
        zeit += System.currentTimeMillis();
        logger.info("retrieveDefinedContentItems(" + filter + ")->" + zeit + "ms");
        logger.debug("ContentItems(" + filter + ")=" + items);
        assertNotNull(items);
        assertTrue(countItems(items) == 3);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveItems(java.lang.String)}.
     */
    @Test
    public void retrieveContentItemsOfTypePublication() throws Exception
    {
        String item = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_FILE));
        String filter = "<param><filter name=\"http://escidoc.de/core/01/structural-relations/content-model\">" + PUBITEM_TYPE_ID + "</filter></param>";
        logger.debug("Filter=" + filter);
        long zeit = -System.currentTimeMillis();
        String items = ServiceLocator.getItemHandler(userHandle).retrieveItems(filter);
        zeit += System.currentTimeMillis();
        logger.info("retrieveContentItemsOfTypePublication(" + filter + ")->" + zeit + "ms");
        logger.debug("ContentItems(" + filter + ")=" + items);
        assertNotNull(items);
        assertTrue(countItems(items) > 0);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveItems(java.lang.String)}.
     */
    @Test
    public void retrieveContentItemsOfNotExistingType() throws Exception
    {
        String filter = "<param><filter name=\"http://escidoc.de/core/01/structural-relations/content-model\">" + ILLEGAL_ID + "</filter></param>";
        logger.debug("Filter=" + filter);
        long zeit = -System.currentTimeMillis();
        String items = ServiceLocator.getItemHandler(userHandle).retrieveItems(filter);
        zeit += System.currentTimeMillis();
        logger.info("retrieveContentItemsOfNotExistingType(" + filter + ")->" + zeit + "ms");
        logger.debug("ContentItems(" + filter + ")=" + items);
        assertNotNull(items);
        assertTrue(countItems(items) == 0);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveItems(java.lang.String)}.
     */
    @Test
    public void retrieveContentItemsNotExisting() throws Exception
    {
        String filter = FILTER_NONE;
        logger.debug("Filter=" + filter);
        long zeit = -System.currentTimeMillis();
        String items = ServiceLocator.getItemHandler(userHandle).retrieveItems(filter);
        zeit += System.currentTimeMillis();
        logger.info("retrieveContentItemsNotExisting(" + filter + ")->" + zeit + "ms");
        logger.debug("ContentItems(" + filter + ")=" + items);
        assertNotNull(items);
        assertTrue(countItems(items) == 0);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveItemRefss(java.lang.String)}.
     */
    @Test
    @Ignore
    public void retrievePendingContentItemRefs() throws Exception
    {
        String filter = "<param><filter name=\"http://escidoc.de/core/01/properties/public-status\">pending</filter></param>";
        logger.debug("Filter=" + filter);
        long zeit = -System.currentTimeMillis();
        String items = ServiceLocator.getItemHandler(userHandle).retrieveItems(filter);
        zeit += System.currentTimeMillis();
        logger.info("retrievePendingContentItemRefs(" + filter + ")->" + zeit + "ms");
        logger.debug("ContentItems(" + filter + ")=" + items);
        assertNotNull(items);
        assertTrue(countItemRefs(items) > 0);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveItemRefs(java.lang.String)}.
     */
    @Test
    @Ignore
    public void retrieveContentItemRefsNotExisting() throws Exception
    {
        String filter = FILTER_NONE;
        logger.debug("Filter=" + filter);
        long zeit = -System.currentTimeMillis();
        String items = ServiceLocator.getItemHandler(userHandle).retrieveItems(filter);
        zeit += System.currentTimeMillis();
        logger.info("retrieveContentItemRefsNotExisting(" + filter + ")->" + zeit + "ms");
        logger.debug("ContentItems(" + filter + ")=" + items);
        assertNotNull(items);
        assertTrue(countItemRefs(items) == 0);
    }
}
