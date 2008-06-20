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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.common.xmltransforming.XmlTransformingTestBase;
import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.util.ObjectComparator;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.GrantVO;
import de.mpg.escidoc.services.common.valueobjects.ItemRelationVO;
import de.mpg.escidoc.services.common.valueobjects.TaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Storage;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.ItemRefFilter;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO.InvitationStatus;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.DegreeType;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.ReviewMethod;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.common.xmltransforming.exceptions.UnmarshallingException;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Test of {@link PubManTransforming} methods for transforming and integration with common_logic and the framework.
 * 
 * @author Johannes M&uuml;ller (initial creation)
 * @author $Author: mfranke $ (last change)
 * @version $Revision: 645 $ $LastChangedDate: 2007-11-26 15:06:58 +0100 (Mon, 26 Nov 2007) $
 * @revised by MuJ: 20.09.2007
 */
public class TransformPubItemIntegrationTest extends XmlTransformingTestBase
{
    private Logger logger = Logger.getLogger(getClass());
    private static XmlTransforming xmlTransforming;
    private AccountUserVO user;
    private String userHandle;
    private static String TEST_FILE_ROOT = "xmltransforming/integration/transformPubItemIntegrationTest/";
    private static String ITEM_WITHOUT_COMPONENTS_FILE = TEST_FILE_ROOT + "item_without_components.xml";
    private static String JPG_FARBTEST_FILE = TEST_FILE_ROOT + "farbtest_wasserfarben.jpg";
    private static String PDF_RUNAWAY_FILE = TEST_FILE_ROOT + "RunawayMassiveBinariesAndClusterEjectionScenarios.pdf";
    private static final String ITEM_SCHEMA_FILE = "xsd/soap/item/0.7/item.xsd";
    private static final String ITEM_LIST_SCHEMA_FILE = "xsd/soap/item/0.7/item-list.xsd";
    private static final String PREDICATE_ISREVISIONOF = "http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#isRevisionOf";
    private static final String PREDICATE_FEDORARELATIONSHIP = "http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#fedoraRelationship";
    private static final String PREDICATE_ISMEMBEROF = "http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#isMemberOf";
    
    private static final String WITHDRAWAL_COMMENT = "Withdrawal comment";

    /**
     * Get an {@link XmlTransforming} instance once.
     * 
     * @throws Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        // TODO FrM: Wech
        // xmlTransforming = (XmlTransforming) getService(XmlTransforming.SERVICE_NAME);
        xmlTransforming = new XmlTransformingBean();
    }

    /**
     * Logs in as depositor and retrieves his grants (before every single test method).
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception
    {
        // get user handle for user "test_dep_scientist"
        userHandle = loginScientist();
        // use this handle to retrieve user "escidoc:user1"
        String userXML = ServiceLocator.getUserAccountHandler(userHandle).retrieve("escidoc:user1");
        // transform userXML to AccountUserVO
        user = xmlTransforming.transformToAccountUser(userXML);
        String userGrantXML = ServiceLocator.getUserAccountHandler(userHandle).retrieveCurrentGrants(user.getReference().getObjectId());
        List<GrantVO> grants = xmlTransforming.transformToGrantVOList(userGrantXML);
        List<GrantVO> userGrants = user.getGrants();
        for (GrantVO grant : grants)
        {
            userGrants.add(grant);
        }
        user.setHandle(userHandle);
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
    }

    /**
     * Tests the transformation of item[XML] (without components) to PubItemVO with a self-created item retrieved from
     * the framework.
     * 
     * @throws Exception
     */
    @Test
    public void transformToPubItemWithoutComponents1() throws Exception
    {
        logger.debug("### transformToPubItem ###");
        // read item[XML] from file
        String itemPreCreate = readFile(ITEM_WITHOUT_COMPONENTS_FILE);
        logger.debug("itemPreCreate(XML) =" + itemPreCreate);
        // create the item in the framework, framework gives back the created item
        String itemPostCreate = ServiceLocator.getItemHandler(userHandle).create(itemPreCreate);
        logger.info("itemPostCreate: " + toString(getDocument(itemPostCreate, false), false));
        // transform the item given back by the framework to a PubItemVO
        PubItemVO pubItem = xmlTransforming.transformToPubItem(itemPostCreate);
        // check results
        String expectedObjid = getObjid(itemPostCreate);
        assertEquals(expectedObjid, pubItem.getVersion().getObjectId());
        assertEquals(1, pubItem.getVersion().getVersionNumber());
        assertEquals(PubItemVO.State.PENDING, pubItem.getVersion().getState());
        assertEquals(null, pubItem.getPid());
        assertNotNull(pubItem.getVersion().getModificationDate());
        assertEquals("escidoc:persistent3", pubItem.getContext().getObjectId());
        assertEquals("escidoc:user1", pubItem.getOwner().getObjectId());
        assertTrue(0 == pubItem.getFiles().size());
        MdsPublicationVO md = pubItem.getMetadata();
        assertEquals(MdsPublicationVO.Genre.BOOK, md.getGenre());
        assertTrue(0 < md.getCreators().size());
        // convert title string to UTF-8 before comparing
        String title = "Über den Wölken. The first of all. Das Maß aller Dinge.";
        // UTF-8 Test
        assertEquals(title, md.getTitle().getValue());
        assertNotNull("md.getLanguages is null!", md.getLanguages());
        assertTrue(3 == md.getLanguages().size());
        assertEquals("de", md.getLanguages().get(0));
        assertTrue(2 == md.getAlternativeTitles().size());
        assertEquals("Die Erste von allen.", md.getAlternativeTitles().get(0).getValue());
        assertTrue(2 == md.getIdentifiers().size());
        assertEquals("0815", md.getIdentifiers().get(0).getId());
        assertEquals(IdType.ISSN, md.getIdentifiers().get(1).getType());
        assertEquals("This problem should disappear with the resolution of FIZ bug #288", "O'Reilly Media Inc., 1005 Gravenstein Highway North, Sebastopol".trim(), md.getPublishingInfo()
                .getPublisher().trim());
        assertEquals("Garching-Itzehoe-Capreton", md.getPublishingInfo().getPlace());
        assertEquals("One and a half", md.getPublishingInfo().getEdition());
        assertEquals("2005-02", md.getDateCreated());
        assertEquals("2007-02-27", md.getDateModified());
        assertEquals("2005-08-31", md.getDateSubmitted());
        assertEquals("2005", md.getDateAccepted());
        assertEquals("2006-02-01", md.getDatePublishedInPrint());
        assertEquals(ReviewMethod.INTERNAL, md.getReviewMethod());
        assertTrue(1 == md.getSources().size());
        assertEquals("Weekly progress meeting", md.getEvent().getTitle().getValue());
        assertEquals("en", md.getEvent().getTitle().getLanguage());
        assertEquals(InvitationStatus.INVITED, md.getEvent().getInvitationStatus());
        assertEquals("999", md.getTotalNumberOfPages());
        assertEquals(DegreeType.MASTER, md.getDegree());
        assertTrue(2 == md.getAbstracts().size());
        assertEquals(new TextVO("wichtig,wissenschaftlich,spannend", "jp"), md.getSubject());
        assertEquals(new TextVO("1.Einleitung 2.Inhalt", "fr"), md.getTableOfContents());
        assertEquals("IPP, Garching", md.getLocation());
    }

    /**
     * Tests the transformation of item[XML] (without components) to PubItemVO with a self-created item retrieved from
     * the framework.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToPubItemWithoutComponents2() throws Exception
    {
        logger.debug("### testTransformToPubItemWithoutComponents ###");
        // read item[XML] from file
        String pubItemXMLPreCreate = readFile(ITEM_WITHOUT_COMPONENTS_FILE);
        logger.info("Item[XML] read from file.");
        // create the item in the framework, framework gives back the created item
        String pubItemXMLPostCreate = ServiceLocator.getItemHandler(userHandle).create(pubItemXMLPreCreate);
        logger.info("Item[XML] created in the framework.");
        assertNotNull(pubItemXMLPostCreate);
        logger.debug("item(XML) (after creation) =" + pubItemXMLPostCreate);
        // use the item given back by the framework to test the transforming
        long zeit = -System.currentTimeMillis();
        PubItemVO pubItemVO = xmlTransforming.transformToPubItem(pubItemXMLPostCreate);
        zeit += System.currentTimeMillis();
        logger.info("transformToPubItem()->" + zeit + "ms");
        logger.info("Transformed returned item to PubItemVO.");
        // check results
        assertNotNull(pubItemVO);
        if (pubItemVO.getContext() != null)
        {
            logger.debug("pubItemVO.pubItemVO.getPubCollection().getObjectId(): " + pubItemVO.getContext().getObjectId());
        }
        if (pubItemVO.getVersion() != null)
        {
            logger.debug("pubItemVO.pubItemVO.getVersion().getObjectId(): " + pubItemVO.getVersion().getObjectId());
        }
    }

    /**
     * Tests the transformation of item[XML] (containing one component) to PubItemVO with a self-created item retrieved
     * from the framework.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToItemWithOneComponentCreate() throws Exception
    {
        logger.debug("### testTransformToItemWithOneComponentCreate ###");
        // create a new item with one component using framework_access directly
        // create new PubItemVO containing some metadata content
        PubItemVO pubItemVOPreCreate = getPubItemWithoutFiles();
        // add file to PubItemVO
        FileVO fileVO = new FileVO();
        // first upload the file to the framework
        fileVO.setContent(uploadFile(JPG_FARBTEST_FILE, "image/jpeg", userHandle).toString());
        // set some properties of the FileVO (mandatory fields first of all)

        logger.info("Content: " + fileVO.getContent());

        fileVO.setContentCategory("post-print");
        fileVO.setName("farbtest_wasserfarben.jpg");
        fileVO.setDescription("Ein Farbtest mit Wasserfarben.");
        fileVO.setVisibility(Visibility.PUBLIC);
        fileVO.setStorage(Storage.INTERNAL_MANAGED);
        fileVO.setSize((int)new File(JPG_FARBTEST_FILE).length());
        // and add it to the PubItemVO's files list
        pubItemVOPreCreate.getFiles().add(fileVO);
        // transform the PubItemVO into an item (for create)
        long zeit = -System.currentTimeMillis();
        String pubItemXMLPreCreate = xmlTransforming.transformToItem(pubItemVOPreCreate);
        zeit += System.currentTimeMillis();
        logger.info("transformToItem() (with component)->" + zeit + "ms");
        logger.info("PubItemVO with file transformed to item(XML) for create.");
        logger.info("item(XML) =" + pubItemXMLPreCreate);
        // create the item in the framework
        String pubItemXMLPostCreate = ServiceLocator.getItemHandler(userHandle).create(pubItemXMLPreCreate);
        assertNotNull(pubItemXMLPostCreate);
        logger.info("item(XML) created in the framework.");
        logger.debug("Item objid: " + getObjid(pubItemXMLPostCreate));
        logger.debug("Response from framework =" + pubItemXMLPostCreate);
    }

    /**
     * Tests the transformation of item[XML] (containing two components) to PubItemVO with a self-created item retrieved
     * from the framework.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToItemWithTwoComponentsCreate() throws Exception
    {
        logger.debug("### testTransformToItemWithTwoComponentsCreate ###");
        // create a new item with two components using framework_access directly
        // create new PubItemVO containing some metadata content
        PubItemVO pubItemVOPreCreate = getPubItemWithoutFiles();
        // add first file to PubItemVO
        FileVO fileVO1 = new FileVO();
        // first upload the file to the framework
        fileVO1.setContent(uploadFile(JPG_FARBTEST_FILE, "image/jpeg", userHandle).toString());
        // set some properties of the FileVO (mandatory fields first of all)
        fileVO1.setContentCategory("post-print");
        fileVO1.setName("farbtest_wasserfarben.jpg");
        fileVO1.setDescription("Ein Farbtest mit Wasserfarben.");
        fileVO1.setVisibility(Visibility.PUBLIC);
        fileVO1.setStorage(Storage.INTERNAL_MANAGED);
        fileVO1.setSize(new File(JPG_FARBTEST_FILE).length());
        // and add it to the PubItemVO's files list
        pubItemVOPreCreate.getFiles().add(fileVO1);
        // add second file to PubItemVO
        FileVO fileVO2 = new FileVO();
        // first upload the file to the framework
        fileVO2.setContent(uploadFile(PDF_RUNAWAY_FILE, "application/pdf", userHandle).toString());
        // set some properties of the FileVO (mandatory fields first of all)
        fileVO2.setContentCategory("copyright-transfer-agreement");
        fileVO2.setName("RunawayMassiveBinariesAndClusterEjectionScenarios.pdf");
        fileVO2.setDescription("The production of runaway massive binaries offers key insights into the evolution of close "
                + "binary stars and open clusters. The stars HD 14633 and HD 15137 are rare examples of such "
                + "runaway systems, and in this work we investigate the mechanism by which they were ejected "
                + "from their parent open cluster, NGC 654. We discuss observational characteristics that can "
                + "be used to distinguish supernova ejected systems from those ejected by dynamical interactions, "
                + "and we present the results of a new radio pulsar search of these systems as well as estimates of "
                + "their predicted X-ray flux assuming that each binary contains a compact object. Since neither "
                + "pulsars nor X-ray emission are observed in these systems, we cannot conclude that these binaries "
                + "contain compact companions. We also consider whether they may have been ejected by dynamical "
                + "interactions in the dense environment where they formed, and our simulations of four-body "
                + "interactions suggest that a dynamical origin is possible but unlikely. We recommend further X-"
                + "ray observations that will conclusively identify whether HD 14633 or HD 15137 contain neutron " + "stars.");
        fileVO2.setVisibility(Visibility.PUBLIC);
        fileVO2.setStorage(Storage.INTERNAL_MANAGED);
        fileVO2.setSize(new File(PDF_RUNAWAY_FILE).length());
        // and add it to the PubItemVO's files list
        pubItemVOPreCreate.getFiles().add(fileVO2);
        // transform the PubItemVO into an item (for create)
        long zeit = -System.currentTimeMillis();
        String pubItemXMLPreCreate = xmlTransforming.transformToItem(pubItemVOPreCreate);
        zeit += System.currentTimeMillis();
        logger.info("transformToItem() (with component)->" + zeit + "ms");
        logger.info("PubItemVO with file transformed to item(XML) for create.");
        logger.debug("ContentItem() (item after transformation from PubItemVO) =" + pubItemXMLPreCreate);
        // create the item in the framework
        String pubItemXMLPostCreate = ServiceLocator.getItemHandler(userHandle).create(pubItemXMLPreCreate);
        assertNotNull(pubItemXMLPostCreate);
        logger.info("item(XML) created in the framework.");
        logger.debug("Item objid: " + getObjid(pubItemXMLPostCreate));
        logger.debug("Response from framework =" + pubItemXMLPostCreate);
    }

    /**
     * Creates an item with a file in the framework and updates the item.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToItemWithOneComponentUpdate() throws Exception
    {
        logger.debug("### testTransformToItemWithOneComponentUpdate ###");
        // create a new item with one component using framework_access directly
        // create new PubItemVO containing some metadata content
        PubItemVO pubItemVOPreCreate = getPubItemWithoutFiles();
        // add file to PubItemVO
        FileVO fileVO = new FileVO();
        // first upload the file to the framework
        fileVO.setContent(uploadFile(JPG_FARBTEST_FILE, "image/jpeg", userHandle).toString());
        // set some properties of the FileVO (mandatory fields first of all)
        fileVO.setContentCategory("supplementary-material");
        fileVO.setName("farbtest_wasserfarben.jpg");
        fileVO.setDescription("Ein Farbtest mit Wasserfarben.");
        fileVO.setVisibility(Visibility.PUBLIC);
        fileVO.setStorage(Storage.INTERNAL_MANAGED);
        fileVO.setSize((int)new File(JPG_FARBTEST_FILE).length());
        // and add it to the PubItemVO's files list
        pubItemVOPreCreate.getFiles().add(fileVO);
        // transform the PubItemVO into an item (for create)
        String pubItemXMLPreCreate = xmlTransforming.transformToItem(pubItemVOPreCreate);
        logger.info("PubItemVO with file transformed to item(XML) for create." + "\nContentItem() (item after transformation from PubItemVO) =" + pubItemXMLPreCreate);
        // create the item in the framework
        String pubItemXMLPostCreate = ServiceLocator.getItemHandler(userHandle).create(pubItemXMLPreCreate);
        assertNotNull(pubItemXMLPostCreate);
        logger.info("item(XML) created in the framework." + "\nItem objid: " + getObjid(pubItemXMLPostCreate) + "\nResponse from framework =" + pubItemXMLPostCreate);
        // transform the returned item to a PubItemVO
        PubItemVO pubItemVOPostCreate = xmlTransforming.transformToPubItem(pubItemXMLPostCreate);
        logger.debug("Create: Returned item transformed back to PubItemVO.");
        if (pubItemVOPostCreate.getVersion() != null)
        {
            logger.debug("pubItemVOPostCreate.getVersion().getObjectId() (objid): " + pubItemVOPostCreate.getVersion().getObjectId());
        }
        else
        {
            fail("pubItemVOPostCreate.getVersion() is null!");
        }
        
        logger.debug("pubItemVOPostCreate.getModificationDate(): " + pubItemVOPostCreate.getVersion().getModificationDate());
        // transform the PubItemVO into an item again
        String pubItemXMLPreUpdate = xmlTransforming.transformToItem(pubItemVOPostCreate);
        String id = getObjid(pubItemXMLPreUpdate);
        logger.info("PubItemVO transfored back to item(XML) for update." + "\nContentItem() =\n######\n" + pubItemXMLPreUpdate + "\n######\nItem id: " + id);
        // update the item in the framework
        logger.info("Trying to update the item in the framework...");

        logger.debug("pubItemXMLPreUpdate: " + pubItemXMLPreUpdate);

        String pubItemXMLPostUpdate = ServiceLocator.getItemHandler(userHandle).update(id, pubItemXMLPreUpdate);
        assertNotNull(pubItemXMLPostUpdate);
        logger.info("item(XML) updated in the framework." + "\nItem objid: " + getObjid(pubItemXMLPostUpdate) + "\nResponse from framework =\n######\n" + pubItemXMLPostUpdate + "\n######\n");
        // transform the returned item to a PubItemVO
        PubItemVO pubItemVOPostUpdate = xmlTransforming.transformToPubItem(pubItemXMLPostUpdate);
        logger.debug("Update: Returned item transformed back to PubItemVO.");
        // check results
        assertTrue(pubItemVOPostUpdate.getLatestVersion().getVersionNumber() > 1);        
        // compare the metadata sets peu a peu (good for bug tracking)
        MdsPublicationVO mdsPublication1 = pubItemVOPreCreate.getMetadata();
        MdsPublicationVO mdsPublication2 = pubItemVOPostUpdate.getMetadata();
        ObjectComparator oc = new ObjectComparator(mdsPublication1, mdsPublication2);
        for (String diff : oc.getDiffs())
        {
            logger.error(diff);
        }
        assertEquals("This problem should disappear with the resolution of FIZ bug #288", 0, oc.getDiffs().size());
        // compare the metadata sets as a whole
        oc = new ObjectComparator(mdsPublication1, pubItemVOPostCreate.getMetadata());
        assertEquals(0, oc.getDiffs().size());
    }

    /**
     * Creates an item with a file in the framework, changes the content-category (=content type) and updates the item.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToItemWithOneComponentUpdateWithChangedContentCategory() throws Exception
    {
        logger.debug("### testTransformToItemWithOneComponentUpdateWithChangedContentCategory ###");
        // create a new item with one component using framework_access directly
        // create new PubItemVO containing some metadata content
        PubItemVO pubItemVOPreCreate = getPubItemWithoutFiles();
        // add file to PubItemVO
        FileVO fileVO = new FileVO();
        // first upload the file to the framework
        fileVO.setContent(uploadFile(JPG_FARBTEST_FILE, "image/jpeg", userHandle).toString());
        // set some properties of the FileVO (mandatory fields first of all)
        fileVO.setContentCategory("supplementary-material");
        fileVO.setName("farbtest_wasserfarben.jpg");
        fileVO.setDescription("Ein Farbtest mit Wasserfarben.");
        fileVO.setVisibility(Visibility.PUBLIC);
        fileVO.setStorage(Storage.INTERNAL_MANAGED);
        fileVO.setSize((int)new File(JPG_FARBTEST_FILE).length());
        // and add it to the PubItemVO's files list
        pubItemVOPreCreate.getFiles().add(fileVO);
        // transform the PubItemVO into an item (for create)
        String pubItemXMLPreCreate = xmlTransforming.transformToItem(pubItemVOPreCreate);
        logger.info("PubItemVO with file transformed to item(XML) for create." + "\nContentItem() (item after transformation from PubItemVO) =" + pubItemXMLPreCreate);
        // create the item in the framework
        String pubItemXMLPostCreate = ServiceLocator.getItemHandler(userHandle).create(pubItemXMLPreCreate);
        assertNotNull(pubItemXMLPostCreate);
        logger.info("item(XML) created in the framework." + "\nItem objid: " + getObjid(pubItemXMLPostCreate) + "\nResponse from framework =" + pubItemXMLPostCreate);
        // transform the returned item to a PubItemVO
        PubItemVO pubItemVOPostCreate = xmlTransforming.transformToPubItem(pubItemXMLPostCreate);
        logger.debug("Create: Returned item transformed back to PubItemVO.");
        if (pubItemVOPostCreate.getVersion() != null)
        {
            logger.debug("pubItemVOPostCreate.getVersion().getObjectId() (objid): " + pubItemVOPostCreate.getVersion().getObjectId());
        }
        else
        {
            logger.debug("pubItemVOPostCreate.getVersion() is null!");
        }
        logger.debug("pubItemVOPostCreate.getModificationDate(): " + pubItemVOPostCreate.getVersion().getModificationDate());
        // switch content type to another value
        List<FileVO> files = pubItemVOPostCreate.getFiles();
        assertEquals("Item does not contain exactly one file as expected.", 1, files.size());
        FileVO file = files.get(0);
        String currentContentType = file.getContentCategory();
        if ("abstract".equals(currentContentType))
        {
            file.setContentCategory("supplementary-material");
        }
        else
        {
            file.setContentCategory("abstract");
        }
        // transform the PubItemVO into an item again
        String pubItemXMLPreUpdate = xmlTransforming.transformToItem(pubItemVOPostCreate);
        String id = getObjid(pubItemXMLPreUpdate);
        logger.info("PubItemVO transformed back to item(XML) for update." + "\nContentItem() =\n######\n" + pubItemXMLPreUpdate + "\n######\nItem id: " + id);
        // update the item in the framework
        logger.info("Trying to update the item in the framework...");
        String pubItemXMLPostUpdate = null;
        try
        {
            pubItemXMLPostUpdate = ServiceLocator.getItemHandler(userHandle).update(id, pubItemXMLPreUpdate);
            assertNotNull(pubItemXMLPostUpdate);
            logger.info("item(XML) updated in the framework." + "\nItem objid: " + getObjid(pubItemXMLPostUpdate) + "\nResponse from framework =\n######\n" + pubItemXMLPostUpdate + "\n######\n");
        }
        catch (Exception e)
        {
            logger.error("Test Error", e);
            fail("This problem disappeared with the resolution of FIZ bug #299.");
        }
        // transform the returned item to a PubItemVO
        PubItemVO pubItemVOPostUpdate = xmlTransforming.transformToPubItem(pubItemXMLPostUpdate);
        logger.debug("Update: Returned item transformed back to PubItemVO.");
        // check results
        // compare the metadata sets peu a peu (good for bug tracking)
        MdsPublicationVO mdsPublication1 = pubItemVOPreCreate.getMetadata();
        MdsPublicationVO mdsPublication2 = pubItemVOPostUpdate.getMetadata();
        ObjectComparator oc = new ObjectComparator(mdsPublication1, mdsPublication2);
        assertEquals(0, oc.getDiffs().size());
        // compare the metadata sets as a whole
        oc = new ObjectComparator(mdsPublication1, pubItemVOPostCreate.getMetadata());
        assertEquals(0, oc.getDiffs().size());
    }

    /**
     * Creates an item with two files in the framework and updates the item.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToItemWithTwoComponentsUpdate() throws Exception
    {
        logger.debug("### testTransformToItemWithTwoComponentsUpdate ###");

        // create a new item with one component using framework_access directly
        // create new PubItemVO containing some metadata content
        PubItemVO pubItemVOPreCreate = getPubItemWithoutFiles();
        // add first file to PubItemVO
        FileVO fileVO1 = new FileVO();
        // first upload the file to the framework
        fileVO1.setContent(uploadFile(JPG_FARBTEST_FILE, "image/jpeg", userHandle).toString());
        // set some properties of the FileVO (mandatory fields first of all)
        fileVO1.setContentCategory("post-print");
        fileVO1.setName("farbtest_wasserfarben.jpg");
        fileVO1.setDescription("Ein Farbtest mit Wasserfarben.");
        fileVO1.setVisibility(Visibility.PUBLIC);
        fileVO1.setStorage(Storage.INTERNAL_MANAGED);
        fileVO1.setSize(new File(JPG_FARBTEST_FILE).length());
        // and add it to the PubItemVO's files list
        pubItemVOPreCreate.getFiles().add(fileVO1);
        // add second file to PubItemVO
        FileVO fileVO2 = new FileVO();
        // first upload the file to the framework
        fileVO2.setContent(uploadFile(PDF_RUNAWAY_FILE, "application/pdf", userHandle).toString());
        // set some properties of the FileVO (mandatory fields first of all)
        fileVO2.setContentCategory("copyright-transfer-agreement");
        fileVO2.setName("RunawayMassiveBinariesAndClusterEjectionScenarios.pdf");
        fileVO2.setDescription("The production of runaway massive binaries offers key insights into the evolution of close "
                + "binary stars and open clusters. The stars HD 14633 and HD 15137 are rare examples of such "
                + "runaway systems, and in this work we investigate the mechanism by which they were ejected "
                + "from their parent open cluster, NGC 654. We discuss observational characteristics that can "
                + "be used to distinguish supernova ejected systems from those ejected by dynamical interactions, "
                + "and we present the results of a new radio pulsar search of these systems as well as estimates of "
                + "their predicted X-ray flux assuming that each binary contains a compact object. Since neither "
                + "pulsars nor X-ray emission are observed in these systems, we cannot conclude that these binaries "
                + "contain compact companions. We also consider whether they may have been ejected by dynamical "
                + "interactions in the dense environment where they formed, and our simulations of four-body "
                + "interactions suggest that a dynamical origin is possible but unlikely. We recommend further X-"
                + "ray observations that will conclusively identify whether HD 14633 or HD 15137 contain neutron " + "stars.");
        fileVO2.setVisibility(Visibility.PUBLIC);
        fileVO2.setStorage(Storage.INTERNAL_MANAGED);
        fileVO2.setSize(new File(PDF_RUNAWAY_FILE).length());
        // and add it to the PubItemVO's files list
        pubItemVOPreCreate.getFiles().add(fileVO2);
        // transform the PubItemVO into an item (for create)
        String pubItemXMLPreCreate = xmlTransforming.transformToItem(pubItemVOPreCreate);
        logger.info("PubItemVO with file transformed to item(XML) for create." + "\nContentItem() (item after transformation from PubItemVO) =" + pubItemXMLPreCreate);
        // create the item in the framework
        String pubItemXMLPostCreate = ServiceLocator.getItemHandler(userHandle).create(pubItemXMLPreCreate);
        assertNotNull(pubItemXMLPostCreate);
        logger.info("item(XML) created in the framework." + "\nItem objid: " + getObjid(pubItemXMLPostCreate) + "\nResponse from framework =" + pubItemXMLPostCreate);
        // transform the returned item to a PubItemVO
        PubItemVO pubItemVOPostCreate = xmlTransforming.transformToPubItem(pubItemXMLPostCreate);
        logger.debug("Create: Returned item transformed back to PubItemVO.");
        if (pubItemVOPostCreate.getVersion() != null)
        {
            logger.debug("pubItemVOPostCreate.getVersion().getObjectId() (objid): " + pubItemVOPostCreate.getVersion().getObjectId());
        }
        else
        {
            logger.debug("pubItemVOPostCreate.getVersion() is null!");
        }
        logger.debug("pubItemVOPostCreate.getModificationDate(): " + pubItemVOPostCreate.getVersion().getModificationDate());
        // transform the PubItemVO into an item again
        String pubItemXMLPreUpdate = xmlTransforming.transformToItem(pubItemVOPostCreate);
        String id = getObjid(pubItemXMLPreUpdate);
        logger.info("PubItemVO transformed back to item(XML) for update." + "\nContentItem() =" + pubItemXMLPreUpdate + "Item id: " + id);
        // update the item in the framework
        logger.info("Trying to update the item in the framework...");
        String pubItemXMLPostUpdate = ServiceLocator.getItemHandler(userHandle).update(id, pubItemXMLPreUpdate);
        assertNotNull(pubItemXMLPostUpdate);
        logger.info("done!");
        // transform the returned item to a PubItemVO
        PubItemVO pubItemVOPostUpdate = xmlTransforming.transformToPubItem(pubItemXMLPostUpdate);
        logger.debug("Update: Returned item transformed back to PubItemVO.");
        // check results
        // compare the metadata sets peu a peu (good for bug tracking)
        MdsPublicationVO mdsPublication1 = pubItemVOPreCreate.getMetadata();
        MdsPublicationVO mdsPublication2 = pubItemVOPostUpdate.getMetadata();
        ObjectComparator oc = new ObjectComparator(mdsPublication1, mdsPublication2);
        assertEquals("This problem should disappear with the resolution of FIZ bug #288", 0, oc.getDiffs().size());
        // compare the metadata sets as a whole
        oc = new ObjectComparator(mdsPublication1, pubItemVOPostCreate.getMetadata());
        assertEquals(0, oc.getDiffs().size());
    }

    /**
     * Test method #1 for {@link de.mpg.escidoc.services.common.XmlTransforming#transformToItem(PubItemVO)}.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToItemWithoutComponentsCreate() throws Exception
    {
        logger.debug("### testTransformToItemWithoutComponentsCreate ###");
        // create a complex PubItemVO from scratch
        PubItemVO pubItemVO = getComplexPubItemWithoutFiles();
        logger.debug("pubItemVO.getMetadata().getCreators().get(0).getPerson().getAlternativeNames().get(1): " + pubItemVO.getMetadata().getCreators().get(0).getPerson().getAlternativeNames().get(1));
        logger.info("Complex PubItemVO created from scratch.");
        // remember metadata set
        MdsPublicationVO mdsPublication1 = pubItemVO.getMetadata();
        logger.debug("size of sources: " + mdsPublication1.getSources().size());
        logger.debug("size of files: " + pubItemVO.getFiles().size());
        // transform the PubItemVO into an item (for create)
        String pubItemXMLPreCreate = xmlTransforming.transformToItem(pubItemVO);
        logger.info("PubItemVO transformed to item(XML) for create: " + pubItemXMLPreCreate);
        logger.debug("ContentItem() (item after transformation from PubItemVO) =\n" + toString(getDocument(pubItemXMLPreCreate, false), false));
        // create the item in the framework
        ItemHandler ihr = ServiceLocator.getItemHandler(userHandle);
        logger.debug("ItemHandlerRemote successfully obtained.");
        String pubItemXMLPostCreate = ihr.create(pubItemXMLPreCreate);
        assertNotNull(pubItemXMLPostCreate);
        logger.info("Item created.");
        logger.debug("Item objid: " + getObjid(pubItemXMLPostCreate));
        logger.debug("ContentItem() (after creation) =\n" + toString(getDocument(pubItemXMLPostCreate, false), false));
        // transform the returned item to a PubItemVO
        PubItemVO pubItemVOPostCreate = xmlTransforming.transformToPubItem(pubItemXMLPostCreate);
        logger.info("Returned item transformed back to PubItemVO.");
        // check results
        // compare the metadata sets peu a peu (good for bug tracking)
        MdsPublicationVO mdsPublication2 = pubItemVOPostCreate.getMetadata();
        logger.debug("mdsPublication2.getCreators().get(0).getPerson().getAlternativeNames().get(1): " + mdsPublication2.getCreators().get(0).getPerson().getAlternativeNames().get(1));
        ObjectComparator oc = new ObjectComparator(mdsPublication1, mdsPublication2);
        for (String diff : oc.getDiffs())
        {
            logger.error("Found difference: " + diff);
        }
        assertEquals("See FIZ bug #286, #288", 0, oc.getDiffs().size());
        // compare the metadata sets as a whole
        oc = new ObjectComparator(mdsPublication1, pubItemVOPostCreate.getMetadata());
        assertEquals(0, oc.getDiffs().size());
    }

    /**
     * Test method for {@link de.mpg.escidoc.services.common.XmlTransforming#transformToItem(PubItemVO)}.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToItemWithoutComponentsUpdate() throws Exception
    {
        logger.debug("### testTransformToItemWithoutComponentsUpdate ###");
        // create a minimal PubItemVO from scratch
        PubItemVO pubItemVOPreCreate = getPubItemWithoutFiles();
        logger.info("PubItemVO created from scratch.");
        // test the transformation: transform the PubItemVO into an item (for create)
        long zeit = -System.currentTimeMillis();
        String pubItemXMLPreCreate = xmlTransforming.transformToItem(pubItemVOPreCreate);
        zeit += System.currentTimeMillis();
        logger.info("transformToItem()->" + zeit + "ms");
        logger.info("PubItemVO transformed to item(XML) for create.");
        logger.info("item(XML) =" + pubItemXMLPreCreate);
        // is the created item suitable for create?
        String pubItemXMLPostCreate = ServiceLocator.getItemHandler(userHandle).create(pubItemXMLPreCreate);
        assertNotNull(pubItemXMLPostCreate);
        logger.info("Item created.");
        logger.debug("Item objid: " + getObjid(pubItemXMLPostCreate));
        logger.debug("item(XML) (after creation) =" + pubItemXMLPostCreate);
        // transform the returned item to a PubItemVO
        PubItemVO pubItemVOPostCreate = xmlTransforming.transformToPubItem(pubItemXMLPostCreate);
        logger.debug("Create: Returned item transformed back to PubItemVO.");
        if (pubItemVOPostCreate.getVersion() != null)
        {
            logger.debug("pubItemVOPostCreate.getVersion().getObjectId() (objid): " + pubItemVOPostCreate.getVersion().getObjectId());
        }
        else
        {
            logger.debug("pubItemVOPostCreate.getVersion() is null!");
        }
        logger.debug("pubItemVOPostCreate.getModificationDate(): " + pubItemVOPostCreate.getVersion().getModificationDate());
        // test the transformation: transform the PubItemVO into an item
        zeit = -System.currentTimeMillis();
        String pubItemXMLPreUpdate = xmlTransforming.transformToItem(pubItemVOPostCreate);
        zeit += System.currentTimeMillis();
        logger.info("transformToItem()->" + zeit + "ms");
        logger.info("PubItemVO transformed back to item(XML) for update.");
        logger.debug("item(XML) =" + pubItemXMLPreUpdate);
        // is the created item suitable for update too?
        String id = getObjid(pubItemXMLPreUpdate);
        logger.debug("Item id: " + id);
        String pubItemXMLPostUpdate = ServiceLocator.getItemHandler(userHandle).update(id, pubItemXMLPreUpdate);
        assertNotNull(pubItemXMLPostUpdate);
        logger.info("Item succeddfully updated!");
        // transform the returned item to a PubItemVO
        PubItemVO pubItemVOPostUpdate = xmlTransforming.transformToPubItem(pubItemXMLPostUpdate);
        logger.debug("Update: Returned item transformed back to PubItemVO.");
        // check results
        // compare the metadata sets peu a peu (good for bug tracking)
        MdsPublicationVO mdsPublication1 = pubItemVOPreCreate.getMetadata();
        MdsPublicationVO mdsPublication2 = pubItemVOPostUpdate.getMetadata();
        ObjectComparator oc = new ObjectComparator(mdsPublication1, mdsPublication2);
        assertEquals("This problem should disappear with the resolution of FIZ bug #288", 0, oc.getDiffs().size());
        // compare the metadata sets as a whole
        oc = new ObjectComparator(mdsPublication1, pubItemVOPostCreate.getMetadata());
        assertEquals(0, oc.getDiffs().size());
    }

    /**
     * Checks whether the transforming of an item with relations works properly.
     * 
     * @throws Exception
     */
    @Test
    public void testRoundtripPubItemWithRelations() throws Exception
    {
        // create a "target" item
        PubItemVO targetItemPreCreate = getPubItemWithoutFiles();
        String targetItemPreCreateXml = xmlTransforming.transformToItem(targetItemPreCreate);
        String targetItemPostCreateXml = ServiceLocator.getItemHandler(userHandle).create(targetItemPreCreateXml);
        PubItemVO targetItemPostCreate = xmlTransforming.transformToPubItem(targetItemPostCreateXml);
        ItemRO targetItemRef = targetItemPostCreate.getVersion();

        // create a "source" item and add some fancy relations to the "target" item
        PubItemVO sourceItemPreCreate = getPubItemWithoutFiles();
        List<ItemRelationVO> sourceItemRelations = sourceItemPreCreate.getRelations();
        sourceItemRelations.add(new ItemRelationVO(PREDICATE_ISREVISIONOF, targetItemRef));
        sourceItemRelations.add(new ItemRelationVO(PREDICATE_FEDORARELATIONSHIP, targetItemRef));
        sourceItemRelations.add(new ItemRelationVO(PREDICATE_ISMEMBEROF, targetItemRef));

        // validate and create the "source" item
        String sourceItemPreCreateXml = xmlTransforming.transformToItem(sourceItemPreCreate);
        logger.debug("The source item (with relations):" + sourceItemPreCreateXml);
        assertXMLValid(sourceItemPreCreateXml);
        String sourceItemPostCreateXml = ServiceLocator.getItemHandler(userHandle).create(sourceItemPreCreateXml);

        // transform back to PubItemVO
        PubItemVO sourceItemPostCreate = xmlTransforming.transformToPubItem(sourceItemPostCreateXml);
        assertNotNull(sourceItemPostCreate);

        // check relations
        List<ItemRelationVO> relations = sourceItemPostCreate.getRelations();
        assertEquals(3, relations.size());
        int containsExpectedRelations = 0;
        for (ItemRelationVO relation : relations)
        {
            if (relation.getType().equals(PREDICATE_ISREVISIONOF))
            {
                containsExpectedRelations |= 1;
            }
            if (relation.getType().equals(PREDICATE_FEDORARELATIONSHIP))
            {
                containsExpectedRelations |= 2;
            }
            if (relation.getType().equals(PREDICATE_ISMEMBEROF))
            {
                containsExpectedRelations |= 4;
            }
        }
        assertEquals(7, containsExpectedRelations);

    }

    /**
     * Test method for {@link de.mpg.escidoc.services.common.XmlTransforming#transformToPubItemList(java.lang.String)}.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToPubItemList() throws Exception
    {
        logger.debug("### testTransformToPubItemList ###");
        // first item: create a minimal PubItemVO from scratch and transform it to an item(XML)
        String pubItem1 = xmlTransforming.transformToItem(getPubItemWithoutFiles());
        logger.debug("pubItem1 created from scratch and transformed to XML.");
        assertNotNull(pubItem1);
        // second item: read item[XML] from file
        String pubItem2 = readFile(ITEM_WITHOUT_COMPONENTS_FILE);
        logger.debug("pubItem2(XML) read from file.");
        assertNotNull(pubItem2);
        // create the two items in the framework, remember object ids
        String pubItem1Response = ServiceLocator.getItemHandler(userHandle).create(pubItem1);
        String objid1 = getObjid(pubItem1Response);
        logger.debug("pubItem1 created. objid: " + objid1);
        String pubItem2Response = ServiceLocator.getItemHandler(userHandle).create(pubItem2);
        String objid2 = getObjid(pubItem2Response);
        logger.debug("pubItem2 created. objid: " + objid2);
        // retrieve the two items from the framework using a FilterTaskParamVO
        FilterTaskParamVO filter = new FilterTaskParamVO();
        ItemRefFilter f1 = filter.new ItemRefFilter();
        f1.getIdList().add(new ItemRO(objid1));
        f1.getIdList().add(new ItemRO(objid2));
        filter.getFilterList().add(f1);
        String filterXML = xmlTransforming.transformToFilterTaskParam(filter);
        logger.debug("Used filter to retrieve the items: \n" + filterXML);
        String pubItemListXML = ServiceLocator.getItemHandler(userHandle).retrieveItems(filterXML);
        logger.debug(pubItemListXML);
        assertXMLValid(pubItemListXML);
        List<PubItemVO> pubItemList = xmlTransforming.transformToPubItemList(pubItemListXML);
        assertNotNull(pubItemList);
    }

    /**
     * Tests the transforming of "illegal" ("<" and "&") and problematic (">", "'", "?") XML characters into escaped
     * XML tokens and back.
     * 
     * @throws Exception
     */
    @Test
    public void testIllegalXMLCharacterTransforming() throws Exception
    {
        logger.debug("### testIllegalXMLCharacterTransforming ###");
        // create a complex PubItemVO without files
        PubItemVO pubItemVOPreCreate = getComplexPubItemWithoutFiles();
        MdsPublicationVO mdsPreCreate = pubItemVOPreCreate.getMetadata();
        // add file to PubItemVO
        FileVO fileVO = new FileVO();
        // first upload the file to the framework
        fileVO.setContent(uploadFile(JPG_FARBTEST_FILE, "image/jpeg", userHandle).toString());
        // set some properties of the FileVO (mandatory fields first of all)
        fileVO.setContentCategory("post-print");
        fileVO.setName("farbtest_wasserfarben.jpg");
        fileVO.setDescription("Ein <a href=\"http://www.escidoc.de/farbtest_wasserfarben.jpg\"> Farbtest mit Wasserfarben.</a>");
        fileVO.setVisibility(Visibility.PUBLIC);
        fileVO.setStorage(Storage.INTERNAL_MANAGED);
        fileVO.setSize((int)new File(JPG_FARBTEST_FILE).length());
        // and add it to the PubItemVO's files list
        pubItemVOPreCreate.getFiles().add(fileVO);
        // transform the PubItemVO into an item (for create)
        long zeit = -System.currentTimeMillis();
        String pubItemXMLPreCreate = xmlTransforming.transformToItem(pubItemVOPreCreate);
        zeit += System.currentTimeMillis();
        logger.info("transformToItem() (with component)->" + zeit + "ms");
        logger.info("PubItemVO with file transformed to item(XML) for create.");
        logger.debug("ContentItem() (item after transformation from PubItemVO) =\n" + toString(getDocument(pubItemXMLPreCreate, false), false));
        // create the item in the framework
        String pubItemXMLPostCreate = ServiceLocator.getItemHandler(userHandle).create(pubItemXMLPreCreate);
        assertNotNull(pubItemXMLPostCreate);
        logger.info("item(XML) created in the framework.");
        logger.debug("Item objid: " + getObjid(pubItemXMLPostCreate) + "\nResponse from framework =\n" + toString(getDocument(pubItemXMLPostCreate, false), false));
        PubItemVO pubItemPostCreate = null;
        try
        {
            pubItemPostCreate = xmlTransforming.transformToPubItem(pubItemXMLPostCreate);
        }
        catch (UnmarshallingException e)
        {
            fail("Known bug: see FIZ bug #288.");
        }
        // check if metadata is the same
        MdsPublicationVO mdsPostCreate = pubItemPostCreate.getMetadata();
        ObjectComparator oc = new ObjectComparator(mdsPreCreate, mdsPostCreate);
        for (String diff : oc.getDiffs())
        {
            logger.error("Found difference: " + diff);
        }
        assertEquals("See FIZ bug #288", 0, oc.getDiffs().size());
    }

    /**
     * Test method #1 for {@link de.mpg.escidoc.services.common.XmlTransforming#transformToItem(PubItemVO)}.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformWithdrawnItemAndCheckComment() throws Exception
    {
        logger.debug("### testTransformWithdrawnItemAndCheckComment ###");
        // create a complex PubItemVO from scratch
        PubItemVO pubItemVO = getComplexPubItemWithoutFiles();
        logger.info("Complex PubItemVO created from scratch.");
        // transform the PubItemVO into an item (for create)
        String pubItemXMLPreCreate = xmlTransforming.transformToItem(pubItemVO);
        logger.info("PubItemVO transformed to item(XML) for create: " + pubItemXMLPreCreate);
        logger.debug("ContentItem() (item after transformation from PubItemVO) =\n" + toString(getDocument(pubItemXMLPreCreate, false), false));
        // create the item in the framework
        String usrHandle = loginLibrarian();
        ItemHandler ihr = ServiceLocator.getItemHandler(usrHandle);
        logger.debug("ItemHandlerRemote successfully obtained.");
        String pubItemXMLPostCreate = ihr.create(pubItemXMLPreCreate);
        assertNotNull(pubItemXMLPostCreate);
        logger.info("Item created.");
        logger.debug("Item objid: " + getObjid(pubItemXMLPostCreate));
        logger.debug("ContentItem() (after creation) =\n" + toString(getDocument(pubItemXMLPostCreate, false), false));
        // transform the returned item to a PubItemVO
        PubItemVO pubItemVOPostCreate = xmlTransforming.transformToPubItem(pubItemXMLPostCreate);

        // TaskParam for submission
        TaskParamVO submitParam = new TaskParamVO(pubItemVOPostCreate.getVersion().getModificationDate(), "Submission comment");
        
        // Submit the item
        ihr.submit(pubItemVOPostCreate.getVersion().getObjectId(), xmlTransforming.transformToTaskParam(submitParam));
        
        // Retrieve the item again
        String pubItemXMLPostSubmission = ihr.retrieve(pubItemVOPostCreate.getVersion().getObjectId());
        PubItemVO pubItemVOPostSubmission = xmlTransforming.transformToPubItem(pubItemXMLPostSubmission);
        
        // Param for assignement of Object PID
        String md = getLastModificationDate(pubItemXMLPostSubmission);
        String objectPidParam = "<param last-modification-date=\"" + md + "\">" + "<url>http://localhost</url>" + "</param>";
        
        logger.debug("objectPidParam: " + objectPidParam);
        
        // Assign object pid
        ihr.assignObjectPid(pubItemVOPostSubmission.getVersion().getObjectId(), objectPidParam);
        
        // Retrieve the item again
        String pubItemXMLPostOPidAssignement = ihr.retrieve(pubItemVOPostCreate.getVersion().getObjectId());
        PubItemVO pubItemVOPostOPidAssignement = xmlTransforming.transformToPubItem(pubItemXMLPostOPidAssignement);
        
        // Param for assignement of Version PID
        md = getLastModificationDate(pubItemXMLPostOPidAssignement);
        String versionPidParam = "<param last-modification-date=\"" + md + "\">" + "<url>http://localhost</url>" + "</param>";
        
        // Assign version pid
        ihr.assignVersionPid(pubItemVOPostSubmission.getVersion().getObjectId() + ":1", versionPidParam);
        
        // Retrieve the item again
        String pubItemXMLPostVPidAssignement = ihr.retrieve(pubItemVOPostCreate.getVersion().getObjectId());
        PubItemVO pubItemVOPostVPidAssignement = xmlTransforming.transformToPubItem(pubItemXMLPostVPidAssignement);
        
        // TaskParam for release
        TaskParamVO releaseParam = new TaskParamVO(pubItemVOPostVPidAssignement.getVersion().getModificationDate(), "Release comment");
        
        // Release the item
        ihr.release(pubItemVOPostSubmission.getVersion().getObjectId(), xmlTransforming.transformToTaskParam(releaseParam));
        
        // Retrieve the item again
        String pubItemXMLPostRelease = ihr.retrieve(pubItemVOPostCreate.getVersion().getObjectId());
        PubItemVO pubItemVOPostRelease = xmlTransforming.transformToPubItem(pubItemXMLPostRelease);
        
        // TaskParam for withdrawal
        TaskParamVO withDrawalparam = new TaskParamVO(pubItemVOPostRelease.getVersion().getModificationDate(), WITHDRAWAL_COMMENT);
 
        // Withdraw the item
        ihr.withdraw(pubItemVOPostRelease.getVersion().getObjectId(), xmlTransforming.transformToTaskParam(withDrawalparam));
        
        // Retrieve the item again
        String pubItemXMLPostWithdrawal = ihr.retrieve(pubItemVOPostRelease.getVersion().getObjectId());
        PubItemVO pubItemVOPostWithdrawal = xmlTransforming.transformToPubItem(pubItemXMLPostWithdrawal);
                
        logger.info("Returned item transformed back to PubItemVO.");
        // check results
        assertEquals(WITHDRAWAL_COMMENT, pubItemVOPostWithdrawal.getWithdrawalComment());
    }
}
