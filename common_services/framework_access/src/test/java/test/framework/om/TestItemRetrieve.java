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
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
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
        final String xPath = "//*[local-name() = 'item']";
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
    @Ignore
    public void retrieveAllItems() throws Exception
    {     
        filterMap.put(OPERATION, new String[]{SEARCH_RETRIEVE});
        filterMap.put(VERSION, new String[]{"1.1"});
        
        long zeit = -System.currentTimeMillis();
        String items = ServiceLocator.getItemHandler().retrieveItems(filterMap);
        zeit += System.currentTimeMillis();
        logger.info("retrieveAllItems(" + filterMap.entrySet().toString() + ")->" + zeit + "ms");
        logger.debug("ContentItems = " + items);
        assertNotNull(items);
        assertTrue(countItems(items) > 0);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveItems(java.lang.String)}.
     */
    @Test
    @Ignore
    public void retrievePublicReleasedItems() throws Exception
    {
        filterMap.put(OPERATION, new String[]{SEARCH_RETRIEVE});
        filterMap.put(VERSION, new String[]{"1.1"});
        filterMap.put(QUERY, new String[]{"\"/properties/public-status\"=released"});
        
        logger.debug("Filter=" + filterMap.entrySet().toString());
        long zeit = -System.currentTimeMillis();
        String items = ServiceLocator.getItemHandler().retrieveItems(filterMap);
        zeit += System.currentTimeMillis();
        logger.info("retrievePublicReleasedItems(" + filterMap + ")->" + zeit + "ms");
        logger.debug("ContentItems =" + items);
        assertNotNull(items);
        assertTrue(countItems(items) > 0);
    }
    
    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveItems(java.lang.String)}.
     */
    @Test
    @Ignore
    public void retrieveItemById() throws Exception
    {
        filterMap.put(OPERATION, new String[]{SEARCH_RETRIEVE});
        filterMap.put(VERSION, new String[]{"1.1"});
        filterMap.put(QUERY, new String[]{"\"/id\"=escidoc:2006"});
        
        logger.debug("Filter=" + filterMap.entrySet().toString());
        long zeit = -System.currentTimeMillis();
        String items = ServiceLocator.getItemHandler().retrieveItems(filterMap);
        zeit += System.currentTimeMillis();
        logger.info("retrieveItemById(" + filterMap + ")->" + zeit + "ms");
        logger.debug("ContentItems =" + items);
        assertNotNull(items);
        assertTrue(countItems(items) == 1);
    }


    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveItems(java.lang.String)}.
     */
    @Test
    public void retrievePendingContentItems() throws Exception
    {
        filterMap.put(OPERATION, new String[]{SEARCH_RETRIEVE});
        filterMap.put(VERSION, new String[]{"1.1"});

        String q1 = "\"/properties/public-status\"=pending";
        filterMap.put(QUERY, new String[]{q1});
        
        logger.debug("Filter=" + filterMap.entrySet().toString());
        long zeit = -System.currentTimeMillis();
        String items = ServiceLocator.getItemHandler(userHandle).retrieveItems(filterMap);
        zeit += System.currentTimeMillis();
        logger.info("retrievePendingContentItems(" + filterMap.entrySet().toString() + ")->" + zeit + "ms");
        logger.debug("ContentItems =" + items);
        assertNotNull(items);
        assertTrue(countItems(items) > 0);
    }
    
    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveItems(java.lang.String)}.
     */
    @Test
    public void retrievePendingContentItemsSortByDescending() throws Exception
    {
        filterMap.put(OPERATION, new String[]{SEARCH_RETRIEVE});
        filterMap.put(VERSION, new String[]{"1.1"});

        String q1 = "\"/properties/public-status\"=pending";
        String q2 = "sortBy " + "\"/id\"/sort.descending";
        filterMap.put(QUERY, new String[]{q1 + " " + q2});
        
        logger.debug("Filter=" + filterMap.entrySet().toString());
        long zeit = -System.currentTimeMillis();
        String items = ServiceLocator.getItemHandler(userHandle).retrieveItems(filterMap);
        zeit += System.currentTimeMillis();
        logger.info("retrievePendingContentItemsSortByDescending(" + filterMap.entrySet().toString() + ")->" + zeit + "ms");
        logger.info("ContentItems =" + items);
        assertNotNull(items);
        assertTrue(countItems(items) > 0);
    }
    
    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveItems(java.lang.String)}.
     */
    @Test
    public void retrievePendingContentItemsSortByAscending() throws Exception
    {
        filterMap.put(OPERATION, new String[]{SEARCH_RETRIEVE});
        filterMap.put(VERSION, new String[]{"1.1"});

        String q1 = "\"/properties/public-status\"=pending";
        String q2 = "sortBy " + "\"/id\"/sort.ascending";
        filterMap.put(QUERY, new String[]{q1 + " " + q2});
        
        logger.debug("Filter=" + filterMap.entrySet().toString());
        long zeit = -System.currentTimeMillis();
        String items = ServiceLocator.getItemHandler(userHandle).retrieveItems(filterMap);
        zeit += System.currentTimeMillis();
        logger.info("retrievePendingContentItemsSortByAscending(" + filterMap.entrySet().toString() + ")->" + zeit + "ms");
        logger.info("ContentItems =" + items);
        assertNotNull(items);
        assertTrue(countItems(items) > 0);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveItems(java.lang.String)}.
     */
    @Test
    public void retrieveOwnContentItems() throws Exception
    {
        filterMap.put(OPERATION, new String[]{SEARCH_RETRIEVE});
        filterMap.put(VERSION, new String[]{"1.1"});
        filterMap.put(QUERY, new String[]{"\"/properties/created-by/id\"=escidoc:3013" + " and " + "\"/properties/public-status\"=pending"});    

        logger.debug("Filter=" + filterMap);
        long zeit = -System.currentTimeMillis();
        String items = ServiceLocator.getItemHandler(userHandle).retrieveItems(filterMap);
        zeit += System.currentTimeMillis();
        logger.info("retrieveOwnContentItems(" + filterMap + ")->" + zeit + "ms");
        logger.info("ContentItems =" + items);
        assertNotNull(items);
        assertTrue(countItems(items) > 1);
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
        
        String query = "\"/id\"=" + ids[0] + " or " + "\"/id\"=" + ids[1] + " or "+ "\"/id\"=" + ids[2];
        filterMap.put(OPERATION, new String[]{SEARCH_RETRIEVE});
        filterMap.put(VERSION, new String[]{"1.1"});       
        filterMap.put(QUERY, new String[]{query});
        
        logger.debug("Filter=" + filterMap);
        long zeit = -System.currentTimeMillis();
        Thread.sleep(3000);
        String items = ServiceLocator.getItemHandler(userHandle).retrieveItems(filterMap);
        zeit += System.currentTimeMillis();
        logger.info("retrieveDefinedContentItems(" + filterMap + ")->" + zeit + "ms");
        logger.debug("ContentItems(" + filterMap + ")=" + items);
        assertNotNull(items);
        assertTrue(countItems(items) == 3);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveItems(java.lang.String)}.
     */
    @Test
    public void retrieveContentItemsOfTypePublication() throws Exception
    {
        logger.info(readFile(ITEM_FILE));
        String item = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_FILE));
        String query = "\"/properties/content-model/id\"=" + PUBITEM_TYPE_ID;
        
        filterMap.put(OPERATION, new String[]{SEARCH_RETRIEVE});
        filterMap.put(VERSION, new String[]{"1.1"});       
        filterMap.put(QUERY, new String[]{query});
        
        long zeit = -System.currentTimeMillis();
        String items = ServiceLocator.getItemHandler(userHandle).retrieveItems(filterMap);
        zeit += System.currentTimeMillis();
        logger.info("retrieveContentItemsOfTypePublication()->" + zeit + "ms");
        logger.debug("ContentItems(" + query + ")=" + items);
        assertNotNull(items);
        assertTrue(countItems(items) > 0);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveItems(java.lang.String)}.
     */
    @Test
    public void retrieveContentItemsOfNotExistingType() throws Exception
    {
        filterMap.put(OPERATION, new String[]{SEARCH_RETRIEVE});
        filterMap.put(VERSION, new String[]{"1.1"});
        filterMap.put(QUERY, new String[]{"\"/type\"=XXXX"});
        
        long zeit = -System.currentTimeMillis();
        String items = ServiceLocator.getItemHandler(userHandle).retrieveItems(filterMap);
        zeit += System.currentTimeMillis();
        logger.info("retrieveContentItemsOfNotExistingType(" + filterMap.entrySet().toString() + ")->" + zeit + "ms");
        logger.debug("ContentItems" + items);
        assertNotNull(items);
        assertTrue(countItems(items) == 0);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveItems(java.lang.String)}.
     */
    @Test
    public void retrieveContentItemsNotExisting() throws Exception
    {
        String query = "\"/properties/created-by\"=" + "MisterX";
        
        filterMap.put(OPERATION, new String[]{SEARCH_RETRIEVE});
        filterMap.put(VERSION, new String[]{"1.1"});
        filterMap.put(QUERY, new String[]{query});
        
        long zeit = -System.currentTimeMillis();
        String items = ServiceLocator.getItemHandler(userHandle).retrieveItems(filterMap);
        zeit += System.currentTimeMillis();
        logger.info("retrieveContentItemsNotExisting()->" + zeit + "ms");
        assertNotNull(items);
        assertTrue(countItems(items) == 0);
    }
    
    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveItems(java.lang.String)}.
     */
    @Test
    public void retrieveContentItemsUsingOrAndBrackets() throws Exception
    {
        String ids[] = new String[3];
        String item = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_FILE));
        ids[0] = getId(item);
        item = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_FILE));
        ids[1] = getId(item);
        item = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_FILE));
        ids[2] = getId(item);
        
        String query1 = "\"/properties/public-status\"=pending";
        String query2 = "\"/id\"=" + ids[0] + " or " + "\"/id\"=" + ids[1] + " or "+ "\"/id\"=" + ids[2];
        String query = query1 + " and " + "(" + query2 + ")";
        
        filterMap.put(OPERATION, new String[]{SEARCH_RETRIEVE});
        filterMap.put(VERSION, new String[]{"1.1"});       
        filterMap.put(QUERY, new String[]{query});
        
        long zeit = -System.currentTimeMillis();
        Thread.sleep(3000);
        String items = ServiceLocator.getItemHandler(userHandle).retrieveItems(filterMap);
        zeit += System.currentTimeMillis();
        logger.info("retrieveContentItemsUsingOrAndBrackets()->" + zeit + "ms");
        logger.debug("ContentItems()=" + items);
        assertNotNull(items);
        assertTrue(countItems(items) == 3);
    }
    
    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveItems(java.lang.String)}.
     */
    @Test
    public void retrievePendingContentItemsUsingMaxRecords() throws Exception
    {
        filterMap.put(OPERATION, new String[]{SEARCH_RETRIEVE});
        filterMap.put(VERSION, new String[]{"1.1"});
        filterMap.put(QUERY, new String[]{"\"/properties/public-status\"=pending"});
        filterMap.put("maximumRecords", new String[]{"10"});
        
        logger.debug("Filter=" + filterMap.entrySet().toString());
        long zeit = -System.currentTimeMillis();
        String items = ServiceLocator.getItemHandler(userHandle).retrieveItems(filterMap);
        zeit += System.currentTimeMillis();
        logger.info("retrievePendingContentItemsUsingMaxRecords(" + filterMap.entrySet().toString() + ")->" + zeit + "ms");
        logger.debug("ContentItems =" + items);
        assertNotNull(items);
        assertTrue(countItems(items) == 10);
    }

    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveItemRefss(java.lang.String)}.
     */
    @Test
    @Ignore
    public void retrievePendingContentItemRefs() throws Exception
    {
/*        String filter = "<param><filter name=\"http://escidoc.de/core/01/properties/public-status\">pending</filter></param>";
        logger.debug("Filter=" + filter);
        long zeit = -System.currentTimeMillis();
        String items = ServiceLocator.getItemHandler(userHandle).retrieveItems(filter);
        zeit += System.currentTimeMillis();
        logger.info("retrievePendingContentItemRefs(" + filter + ")->" + zeit + "ms");
        logger.debug("ContentItems(" + filter + ")=" + items);
        assertNotNull(items);
        assertTrue(countItemRefs(items) > 0);*/
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
/*        String items = ServiceLocator.getItemHandler(userHandle).retrieveItems(filter);
        zeit += System.currentTimeMillis();
        logger.info("retrieveContentItemRefsNotExisting(" + filter + ")->" + zeit + "ms");
        logger.debug("ContentItems(" + filter + ")=" + items);
        assertNotNull(items);
        assertTrue(countItemRefs(items) == 0);*/
    }
}
