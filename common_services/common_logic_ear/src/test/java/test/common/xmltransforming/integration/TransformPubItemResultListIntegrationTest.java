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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import gov.loc.www.zing.srw.RecordType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.StringOrXmlFragment;
import gov.loc.www.zing.srw.diagnostic.DiagnosticType;

import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;
import javax.xml.rpc.ServiceException;

import org.apache.axis.message.MessageElement;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.common.AffiliationCreator;
import test.common.xmltransforming.XmlTransformingTestBase;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.OrganizationalUnitNameNotUniqueException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.www.services.om.ContainerHandler;
import de.escidoc.www.services.om.ItemHandler;
import de.escidoc.www.services.oum.OrganizationalUnitHandler;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.util.ObjectComparator;
import de.mpg.escidoc.services.common.valueobjects.AffiliationResultVO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.ContainerResultVO;
import de.mpg.escidoc.services.common.valueobjects.ContainerVO;
import de.mpg.escidoc.services.common.valueobjects.ItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.face.MdsFacesContainerVO;
import de.mpg.escidoc.services.common.valueobjects.interfaces.SearchResult;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.exceptions.MarshallingException;
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

    /**
     * Get an {@link XmlTransforming} instance once.
     * 
     * @throws Exception Any exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        xmlTransforming = (XmlTransforming) getService(XmlTransforming.SERVICE_NAME);        
    }

    /**
     * Logs in as depositor and retrieves his grants (before every single test method).
     * 
     * @throws Exception Any exception
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
     * @throws Exception Any exception
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
     * 
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

    private PubItemVO createAndReleaseItem(String userHandle, String itemTitle)
        throws TechnicalException, ServiceException, RemoteException, URISyntaxException
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

    private ContainerVO createAndReleaseContainer(String userHandle, String containerTitle)
        throws TechnicalException, ServiceException, RemoteException, URISyntaxException
    {
        ContainerVO containerVO = getFacesAlbumContainer();
        containerVO.getMetadataSets().get(0).setTitle(new TextVO(containerTitle));
        String containerXml = xmlTransforming.transformToContainer(containerVO);
        ContainerHandler ihr = ServiceLocator.getContainerHandler(userHandle);
        String createdContainerXml = ihr.create(containerXml);
        ContainerVO createdContainerVO = xmlTransforming.transformToContainer(createdContainerXml);
        String createdContainerId = createdContainerVO.getVersion().getObjectId();
        String md = getModificationDate(createdContainerXml);
        logger.info("Container '"
                + createdContainerId
                + "' created. Title: "
                + createdContainerVO.getMetadataSets().get(0).getTitle());
        ihr.submit(createdContainerId, "<param last-modification-date=\"" + md + "\"/>");
        createdContainerXml = ihr.retrieve(createdContainerId);
        md = getModificationDate(createdContainerXml);
        String param = "<param last-modification-date=\"" + md + "\">" + "<url>http://localhost</url>" + "</param>";
        ihr.assignObjectPid(createdContainerId, param);
        createdContainerXml = ihr.retrieve(createdContainerId);
        md = getModificationDate(createdContainerXml);
        param = "<param last-modification-date=\"" + md + "\">" + "<url>http://localhost</url>" + "</param>";
        ihr.assignVersionPid(createdContainerId + ":1", param);
        createdContainerXml = ihr.retrieve(createdContainerId);
        md = getModificationDate(createdContainerXml);
        ihr.release(createdContainerId, "<param last-modification-date=\"" + md + "\"/>");
        logger.info("Container '" + createdContainerId + "' released.");
        return createdContainerVO;
    }

    /**
     * Creates released items, then searches for them and transforms the result.
     * 
     * @throws Exception Any Exception
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
            SearchRetrieveResponseType searchResult = search(itemTitle, "escidoc_all");

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
            else
            {
                fail("The search returned no results.");
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
                ihr.withdraw(
                        itemRef,
                        "<param last-modification-date=\""
                        + md
                        + "\"><withdraw-comment>The item is withdrawn."
                        + "</withdraw-comment></param>");
                logger.info("PubItem '" + itemRef + "' withdrawn.");
            }
        }
    }

    /**
     * Creates released a container, then searches for it and transforms the result.
     * Afterwards, the container is withdrawn.
     * 
     * @throws Exception Any Exception
     */
    @Test
    public void testTransformContainerResultToContainerVO() throws Exception
    {
        final String containerTitle = "testTransformContainerResultToContainerVO";
        
        // create a container
        ContainerVO container = createAndReleaseContainer(adminUserHandle, containerTitle);
        MdsFacesContainerVO expected = (MdsFacesContainerVO) container.getMetadataSets().get(0);
        String containerRef = container.getVersion().getObjectId();
        assertNotNull(containerRef);

        // wait a little bit for indexing...
        logger.info("Waiting 5 seconds to let the framework indexing happen...");
        Thread.sleep(5000);   

        try
        {
            SearchRetrieveResponseType searchResult =
                search(containerTitle, "escidoc_all");

            if (searchResult.getRecords() != null)
            {
                NonNegativeInteger exp = new NonNegativeInteger("1");
                
                assertEquals(exp, searchResult.getNumberOfRecords());
                RecordType record = searchResult.getRecords().getRecord(0);

                StringOrXmlFragment data = record.getRecordData();
                MessageElement[] messages = data.get_any();
                // Data is in the first record
                if (messages.length == 1)
                {

                    String searchResultContainer = messages[0].getAsString();
                    logger.debug("Search result: " + searchResultContainer);
                    SearchResult containerResult = xmlTransforming.transformToSearchResult(searchResultContainer);
                    assertTrue(containerResult instanceof ContainerResultVO);

                    // CORE OF THE TEST: check if transforming works with subclass "ContainerResultVO"

                    ContainerResultVO containerResultVO = (ContainerResultVO) containerResult;
                    
                    assertTrue(containerResultVO.getMetadataSets().get(0) instanceof MdsFacesContainerVO);
                    
                    MdsFacesContainerVO actual = (MdsFacesContainerVO) containerResultVO.getMetadataSets().get(0);
                    
                    assertEquals(expected.getName(), actual.getName());
                    assertEquals(expected.getDescription(), actual.getDescription());
                    assertTrue(new ObjectComparator(expected.getCreators(), actual.getCreators()).isEqual());
                    
                }
                else
                {
                    // what should be in the further message?!
                    logger.warn("SEARCH_TOO_MANY_RESULT_MESSAGES");
                    fail();
                }

            }
            else
            {
                fail("The search returned no results.");
            }
        }
        catch (AssertionError e)
        {
            // the 'catch' part is not interesting, but the 'finally' part
            throw (e);
        }
        finally
        {
            // withdraw items (even in case of error)
            ContainerHandler chr = ServiceLocator.getContainerHandler(adminUserHandle);

            String releasedContainerXml = chr.retrieve(containerRef);
            String md = getModificationDate(releasedContainerXml);
            chr.withdraw(
                    containerRef,
                    "<param last-modification-date=\""
                    + md
                    + "\"><withdraw-comment>The container is withdrawn."
                    + "</withdraw-comment></param>");
            logger.info("Container '" + containerRef + "' withdrawn.");

        }
    }

    /**
     * Creates released an organizational unit, then searches for it and transforms the result.
     * Afterwards, the organizational unit is closed.
     * 
     * @throws Exception Any Exception
     */
    @Test
    public void testTransformAffiliationResultToAffiliationVO() throws Exception
    {
        final String affiliationTitle = "testTransformAffiliationResultToAffiliationVO" + (new Date().getTime()) + "";
        
        // create an ou
        AffiliationVO affiliation = createAndOpenAffiliation(adminUserHandle, affiliationTitle);
        MdsOrganizationalUnitDetailsVO expected = (MdsOrganizationalUnitDetailsVO) affiliation.getMetadataSets().get(0);
        String affiliationRef = affiliation.getReference().getObjectId();
        assertNotNull(affiliationRef);

        // wait a little bit for indexing...
        logger.info("Waiting 5 seconds to let the framework indexing happen...");
        Thread.sleep(5000);   

        try
        {
            SearchRetrieveResponseType searchResult =
                search(affiliationTitle, "escidocou_all");

            if (searchResult.getRecords() != null)
            {
                NonNegativeInteger exp = new NonNegativeInteger("1");
                
                assertEquals(exp, searchResult.getNumberOfRecords());
                RecordType record = searchResult.getRecords().getRecord(0);

                StringOrXmlFragment data = record.getRecordData();
                MessageElement[] messages = data.get_any();
                // Data is in the first record
                if (messages.length == 1)
                {

                    String searchResultAffiliation = messages[0].getAsString();
                    logger.debug("Search result: " + searchResultAffiliation);
                    SearchResult affiliationResult = xmlTransforming.transformToSearchResult(searchResultAffiliation);
                    assertTrue(affiliationResult instanceof AffiliationResultVO);

                    // CORE OF THE TEST: check if transforming works with subclass "AffiliationResultVO"

                    AffiliationResultVO affiliationResultVO = (AffiliationResultVO) affiliationResult;
                    
                    assertTrue(affiliationResultVO.getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO);
                    
                    MdsOrganizationalUnitDetailsVO actual = (MdsOrganizationalUnitDetailsVO) affiliationResultVO.getMetadataSets().get(0);
                    
                    assertEquals(expected.getName(), actual.getName());
                    assertEquals(expected.getTitle(), actual.getTitle());
                    assertTrue(new ObjectComparator(expected, actual).isEqual());
                    
                }
                else
                {
                    // what should be in the further message?!
                    logger.warn("SEARCH_TOO_MANY_RESULT_MESSAGES");
                    fail();
                }

            }
            else
            {
                fail("The search returned no results.");
            }
        }
        catch (AssertionError e)
        {
            // the 'catch' part is not interesting, but the 'finally' part
            throw (e);
        }
        finally
        {
            // withdraw items (even in case of error)
            OrganizationalUnitHandler ouhr = ServiceLocator.getOrganizationalUnitHandler(adminUserHandle);

            String releasedAffiliationXml = ouhr.retrieve(affiliationRef);
            String md = getModificationDate(releasedAffiliationXml);
            ouhr.close(
                    affiliationRef,
                    "<param last-modification-date=\""
                    + md
                    + "\"></param>");
            logger.info("Affiliation '" + affiliationRef + "' closed.");

        }
    }

    private AffiliationVO createAndOpenAffiliation(
            String adminUserHandle2,
            String affiliationTitle) throws Exception
    {
        AffiliationVO affiliationVO = AffiliationCreator.getAffiliationMPIFG();
        
        affiliationVO.getMetadataSets().get(0).setTitle(new TextVO(affiliationTitle));
        ((MdsOrganizationalUnitDetailsVO)affiliationVO.getMetadataSets().get(0)).setName(affiliationTitle);
        
        String affiliationXml = xmlTransforming.transformToOrganizationalUnit(affiliationVO);
        OrganizationalUnitHandler ouhr = ServiceLocator.getOrganizationalUnitHandler(adminUserHandle2);
        String createdAffiliationXml = ouhr.create(affiliationXml);
        AffiliationVO createdAffiliationVO = xmlTransforming.transformToAffiliation(createdAffiliationXml);
        String createdAffiliationId = createdAffiliationVO.getReference().getObjectId();
        String md = getModificationDate(createdAffiliationXml);
        logger.info("OU '"
                + createdAffiliationId
                + "' created. Title: "
                + createdAffiliationVO.getMetadataSets().get(0).getTitle());
        ouhr.open(createdAffiliationId, "<param last-modification-date=\"" + md + "\"/>");
        createdAffiliationXml = ouhr.retrieve(createdAffiliationId);
        logger.info("OU '" + createdAffiliationId + "' opened.");
        return createdAffiliationVO;
    }

    /**
     * @return
     * @throws TechnicalException
     */
    private SearchRetrieveResponseType search(String query, String index) throws TechnicalException
    {
        // define CQL query string
        String extendedCqlSearchString = "(escidoc.metadata=" + query + ")";

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
            searchResult =
                ServiceLocator
                    .getSearchHandler(index)
                    .searchRetrieveOperation(searchRetrieveRequest);
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
            throw new TechnicalException(
                    "Search request failed for query "
                    + extendedCqlSearchString
                    + ". Diagnostics returned. See log for details.");
        }
        logger.info("Search with CQL query String '" + extendedCqlSearchString + "' done.");
        return searchResult;
    }
}
