/*
*
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

package test.common.xmltransforming.integration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.loc.www.zing.srw.RecordType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.StringOrXmlFragment;
import gov.loc.www.zing.srw.diagnostic.DiagnosticType;

import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.rpc.ServiceException;

import org.apache.axis.message.MessageElement;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.common.xmltransforming.XmlTransformingTestBase;
import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.ItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Test of {@link PubManTransforming} methods for transforming and integration with common_logic and the framework.
 * 
 * @author Johannes M&uuml;ller (initial creation)
 * @author $Author: jmueller $ (last change)
 * @version $Revision: 635 $ $LastChangedDate: 2007-11-21 17:12:27 +0100 (Wed, 21 Nov 2007) $
 * @revised by MuJ: 20.09.2007
 */
public class TransformPubItemResultListIntegrationTest extends XmlTransformingTestBase
{
    /**
     * Logger for this class.
     */
    private Logger logger = Logger.getLogger(getClass());
    private static XmlTransforming xmlTransforming;
    private String userHandle;
    private String adminUserHandle;
    private static final String ITEM_LIST_SCHEMA_FILE = "xsd/soap/item/0.7/item-list.xsd";

    /**
     * Get an {@link XmlTransforming} instance once.
     * 
     * @throws Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        xmlTransforming = (XmlTransforming) getService(XmlTransforming.SERVICE_NAME);        
    }

    /**
     * Logs in as depositor and retrieves his grants (before every single test method).
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception
    {
        // get user handles
        userHandle = loginScientist();
        adminUserHandle = loginSystemAdministrator();
    }

    /**
     * Logs out (after every single test method).
     * 
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception
    {
        logout(userHandle);
        logout(adminUserHandle);
    }

    /**
     * Extracts the modification date of a item from the given XML.
     * 
     * @param item The item XML as a String.
     * @return The modification date of the item.
     */
    protected String getModificationDate(String item)
    {
        String md = "";
        int index = item.indexOf("last-modification-date=\"");
        if (index > 0)
        {
            item = item.substring(index + 24);
            index = item.indexOf('\"');
            if (index > 0)
            {
                md = item.substring(0, index);
            }
        }
        return md;
    }

    private PubItemVO createAndReleaseItem(String userHandle, String itemTitle) throws TechnicalException, ServiceException, RemoteException, URISyntaxException
    {
        PubItemVO itemVO = getComplexPubItemWithoutFiles();
        itemVO.getMetadata().setTitle(new TextVO(itemTitle));
        String itemXml = xmlTransforming.transformToItem(itemVO);
        ItemHandler ihr = ServiceLocator.getItemHandler(userHandle);
        String createdItemXml = ihr.create(itemXml);
        PubItemVO createdItemVO = xmlTransforming.transformToPubItem(createdItemXml);
        String createdItemId = createdItemVO.getVersion().getObjectId();
        String md = getModificationDate(createdItemXml);
        logger.info("PubItem '" + createdItemId + "' created. Title: " + createdItemVO.getMetadata().getTitle());
        ihr.submit(createdItemId, "<param last-modification-date=\"" + md + "\"/>");
        createdItemXml = ihr.retrieve(createdItemId);
        md = getModificationDate(createdItemXml);
        String param = "<param last-modification-date=\"" + md + "\">" + "<url>http://localhost</url>" + "</param>";
        ihr.assignObjectPid(createdItemId, param);
        createdItemXml = ihr.retrieve(createdItemId);
        md = getModificationDate(createdItemXml);
        param = "<param last-modification-date=\"" + md + "\">" + "<url>http://localhost</url>" + "</param>";
        ihr.assignVersionPid(createdItemId + ":1", param);
        createdItemXml = ihr.retrieve(createdItemId);
        md = getModificationDate(createdItemXml);
        ihr.release(createdItemId, "<param last-modification-date=\"" + md + "\"/>");
        logger.info("PubItem '" + createdItemId + "' released.");
        return createdItemVO;
    }

    /**
     * @throws Exception
     */
    @Test
    public void testTransformPubItemResultListToItemList() throws Exception
    {
        final String itemTitle = "testTransformPubItemResultListToItemList";
        // create some items
        int itemCount = 2;
        List<String> itemRefs = new ArrayList<String>();
        for (int i = 0; i < itemCount; i++)
        {
            PubItemVO item = createAndReleaseItem(adminUserHandle, itemTitle);
            String itemRef = item.getVersion().getObjectId();
            assertNotNull(itemRef);
            itemRefs.add(itemRef);
        }
        
        // wait a little bit for indexing...
        logger.info("Waiting 5 seconds to let the framework indexing happen...");
        Thread.sleep(5000);   

        try
        {
            // define CQL query string
            //String extendedCqlSearchString = "(escidoc.metadata=testTransformPubItemResultListToItemList) and escidoc.content-model.objid=\"escidoc:persistent4\"";
            String extendedCqlSearchString = "(escidoc.metadata=testTransformPubItemResultListToItemList)";

            // call framework Search service
            SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
            searchRetrieveRequest.setVersion("1.1");
            searchRetrieveRequest.setQuery(extendedCqlSearchString);
            // take only the first 100 search results
            NonNegativeInteger nni = new NonNegativeInteger("100");
            searchRetrieveRequest.setMaximumRecords(nni);
            searchRetrieveRequest.setRecordPacking("xml");
            SearchRetrieveResponseType searchResult = null;
            try
            {
                searchResult = ServiceLocator.getSearchHandler(null).searchRetrieveOperation(searchRetrieveRequest);
            }
            catch (Exception e)
            {
                throw new TechnicalException(e);
            }

            logger.debug("Search result (number of records): " + searchResult.getNumberOfRecords());

            // look for errors
            if (searchResult.getDiagnostics() != null)
            {
                // something went wrong
                for (DiagnosticType diagnostic : searchResult.getDiagnostics().getDiagnostic())
                {
                    logger.warn(diagnostic.getUri());
                    logger.warn(diagnostic.getMessage());
                    logger.warn(diagnostic.getDetails());
                }
                throw new TechnicalException("Search request failed for query " + extendedCqlSearchString + ". Diagnostics returned. See log for details.");
            }
            logger.info("Search with CQL query String '" + extendedCqlSearchString + "' done.");

            // transform to PubItemResult list
            List<ItemResultVO> pubItemResultList = new ArrayList<ItemResultVO>();
            if (searchResult.getRecords() != null)
            {
                for (RecordType record : searchResult.getRecords().getRecord())
                {
                    StringOrXmlFragment data = record.getRecordData();
                    MessageElement[] messages = data.get_any();
                    // Data is in the first record
                    if (messages.length == 1)
                    {
                        try
                        {
                            String searchResultItem = messages[0].getAsString();
                            logger.debug("Search result: " + searchResultItem);
                            ItemResultVO pubItemResult = xmlTransforming.transformToItemResultVO(searchResultItem);
                            pubItemResultList.add(pubItemResult);
                        }
                        catch (Exception e)
                        {
                            throw new TechnicalException(e);
                        }
                    }
                    if (messages.length > 1)
                    {
                        // what should be in the further message?!
                        logger.warn("SEARCH_TOO_MANY_RESULT_MESSAGES");
                    }
                }
            }
            logger.info("Search result converted to List<ItemResultVO>.");
            assertTrue(pubItemResultList.size() > 0);

            // CORE OF THE TEST: check if transforming works with subclass "ItemResultVO" of "PubItemVO"
            logger.info("Trying to transform to item list XML...");
            List<ItemVO> pubItemList = new ArrayList<ItemVO>();
            for (ItemVO item : pubItemResultList)
            {
                pubItemList.add(item);
            }
            String itemListXml = xmlTransforming.transformToItemList(pubItemList);
            assertNotNull(itemListXml);
            logger.info(toString(getDocument(itemListXml, false), false));
            assertXMLValid(itemListXml);
        }
        catch (AssertionError e)
        {
            // the 'catch' part is not interesting, but the 'finally' part
            throw (e);
        }
        finally
        {
            // withdraw items (even in case of error)
            ItemHandler ihr = ServiceLocator.getItemHandler(adminUserHandle);
            for (String itemRef : itemRefs)
            {
                String releasedItemXml = ihr.retrieve(itemRef);
                String md = getModificationDate(releasedItemXml);
                ihr.withdraw(itemRef, "<param last-modification-date=\"" + md + "\"><withdraw-comment>The item is withdrawn.</withdraw-comment></param>");
                logger.info("PubItem '" + itemRef + "' withdrawn.");
            }
        }
    }
}
