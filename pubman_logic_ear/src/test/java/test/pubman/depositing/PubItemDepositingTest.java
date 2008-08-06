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

package test.pubman.depositing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import test.pubman.TestBase;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.util.ObjectComparator;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.TaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.pubman.PubItemDepositing;
import de.mpg.escidoc.services.pubman.exceptions.PubItemNotFoundException;
import de.mpg.escidoc.services.pubman.logging.ApplicationLog;
import de.mpg.escidoc.services.pubman.logging.PMLogicMessages;

/**
 * Test class for {@link PubItemDepositing}
 * 
 * @author Miriam Doelle (initial creation)
 * @author $Author: pbroszei $ (last modification)
 * @version $Revision: 445 $ $LastChangedDate: 2007-11-22 09:43:02 +0100 (Thu, 22 Nov 2007) $
 * @revised by MuJ: 19.09.2007
 */
public class PubItemDepositingTest extends TestBase
{
    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(PubItemDepositingTest.class);

    private PubItemDepositing pmDepositing;
    private XmlTransforming xmlTransforming;
    private AccountUserVO user;

    /**
     * Helper: Saves the given pubItem using {@link PubItemDepositing#savePubItem(PubItemVO, AccountUserVO)} Changed by
     * Peter Broszeit, 18.10.2007: asserts on item properties removed, because all items should be saved.
     */
    private PubItemVO savePubItem(PubItemVO initPubItem, AccountUserVO accountUser) throws Exception
    {
        PubItemVO savedItem = pmDepositing.savePubItem(initPubItem, accountUser);
        assertNotNull(savedItem);
        assertNotNull(savedItem.getVersion());

        // compare metadata
        ObjectComparator oc = null;
        try
        {
            oc = new ObjectComparator(initPubItem.getMetadata(), savedItem.getMetadata());
            assertEquals(0, oc.getDiffs().size());
        }
        catch (AssertionError e)
        {
            logger.error(oc);
            throw (e);
        }

        // retrieve item again
        PubItemVO retrievedPubItem = getPubItemFromFramework(savedItem.getVersion(), accountUser);

        // compare pubItem properties
        oc = new ObjectComparator(savedItem.getVersion(), retrievedPubItem.getVersion());
        assertEquals(0, oc.getDiffs().size());
        //assertNull(retrievedPubItem.getPid());
        // compare the whole object
        oc = new ObjectComparator(retrievedPubItem, savedItem);
        assertTrue(oc.toString(), oc.isEqual());
        // compare metadata
        assertEquals(initPubItem.getMetadata(), retrievedPubItem.getMetadata());

        return savedItem;
    }

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception
    {
        pmDepositing = (PubItemDepositing)getService(PubItemDepositing.SERVICE_NAME);
        xmlTransforming = (XmlTransforming)getService(XmlTransforming.SERVICE_NAME);
        user = getUserTestDepLibWithHandle();
    }

    /**
     * Test for
     * {@link PubItemDepositing#createPubItem(ContextRO, de.mpg.escidoc.services.common.valueobjects.AccountUserVO)}.
     * 
     * @throws Exception
     */
    @Test
    public void testCreatePubItem() throws Exception
    {
        ContextRO pmCollectionRef = new ContextRO();
        pmCollectionRef.setObjectId(PUBMAN_TEST_COLLECTION_ID);
        PubItemVO pubItem = pmDepositing.createPubItem(pmCollectionRef, user);
        assertNotNull(pubItem);
        assertEquals(PUBMAN_TEST_COLLECTION_ID, pubItem.getContext().getObjectId());

        String context = ServiceLocator.getContextHandler(user.getHandle()).retrieve(pmCollectionRef.getObjectId());
        ContextVO pubCollection = xmlTransforming.transformToContext(context);
        assertNotNull(pubItem.getMetadata());
        if (pubCollection.getDefaultMetadata() != null)
        {
            assertEquals(pubCollection.getDefaultMetadata(), pubItem.getMetadata());
        }
    }

    /**
     * Test for
     * {@link PubItemDepositing#createPubItem(ContextRO, de.mpg.escidoc.services.common.valueobjects.AccountUserVO)}.
     * with empty PubCollectionRef.
     * 
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreatePubItemWithoutCollection() throws Exception
    {
        pmDepositing.createPubItem(null, user);
    }

    /**
     * Test for
     * {@link PubItemDepositing#savePubItem(PubItemVO, de.mpg.escidoc.services.common.valueobjects.AccountUserVO)} with
     * new item.
     * 
     * @throws Exception
     */
    @Test
    public void testSaveNewPubItem() throws Exception
    {
        PubItemVO initPubItem = getNewPubItemWithoutFiles();
        savePubItem(initPubItem, user);
    }

    /**
     * Test for
     * {@link PubItemDepositing#savePubItem(PubItemVO, de.mpg.escidoc.services.common.valueobjects.AccountUserVO)} with
     * new complex item.
     * 
     * @throws Exception
     */
    @Test
    public void testSaveNewComplexPubItem() throws Exception
    {
        PubItemVO initPubItem = getComplexPubItemWithoutFiles();
        try
        {
            savePubItem(initPubItem, user);
            
        }
        catch (AssertionError e)
        {
            fail("This failure is due to FIZ bug #288");
        }
    }

    /**
     * Test for
     * {@link PubItemDepositing#savePubItem(PubItemVO, de.mpg.escidoc.services.common.valueobjects.AccountUserVO)} with
     * new complex item and file.
     * 
     * @throws Exception
     */
    @Ignore("See http://www.escidoc-project.de/issueManagement/show_bug.cgi?id=596")
    @Test
    public void testSaveNewComplexPubItemWithFile() throws Exception
    {
        // new item
        PubItemVO initPubItem = getComplexPubItemWithoutFiles();
        // Add file to item
        FileVO initPubFile = new FileVO();
        String testfile = "src/test/resources/depositing/pubItemDepositingTest/farbtest_B6.gif";
        initPubFile.setDescription("Sehen Sie B6?");
        initPubFile.setVisibility(Visibility.PUBLIC);
        initPubFile.setContentCategoryString("abstract");
        initPubFile.setContent(uploadFile(testfile, "image/gif", user.getHandle()).toString());
        initPubFile.setName("farbtest_B6.gif");
        initPubFile.setMimeType("image/gif");
        //initPubFile.setSize((int)new File("src/test/resources/depositing/pubItemDepositingTest/farbtest_B6.gif").length());
        initPubFile.setStorage(FileVO.Storage.INTERNAL_MANAGED);
        initPubItem.getFiles().add(initPubFile);

        
        logger.debug("ITEM to be saved: " + xmlTransforming.transformToItem(initPubItem));
        
        PubItemVO savedItem = null;
        try
        {
            savedItem = savePubItem(initPubItem, user);
            logger.debug("savedItem:" + savedItem);
        }
        catch (AssertionError e)
        {
            fail("This failure is due to FIZ bug #288");
        }
        assertEquals("Wrong number of files", 1, savedItem.getFiles().size());
        FileVO pubFile = savedItem.getFiles().get(0);
        assertNotNull(pubFile.getContent());
        assertNotNull(pubFile.getReference());
        assertEquals(initPubFile.getName(), pubFile.getName());
        assertEquals(initPubFile.getContentCategory(), pubFile.getContentCategory());
        assertEquals(initPubFile.getDescription(), pubFile.getDescription());
        assertEquals(initPubFile.getMimeType(), pubFile.getMimeType());
        assertEquals(initPubFile.getVisibility(), pubFile.getVisibility());
        //assertEquals(37564L, initPubFile.getSize());
    }

    /**
     * Test for
     * {@link PubItemDepositing#savePubItem(PubItemVO, de.mpg.escidoc.services.common.valueobjects.AccountUserVO)} with
     * already existing item.
     * 
     * @throws Exception
     */
    @Test
    public void testSaveExistingPendingPubItem() throws Exception
    {
        assertTrue(user.isDepositor());
        // create pubItem to get Reference
        PubItemVO item = getNewPubItemWithoutFiles();
        PubItemVO savedItem = savePubItem(item, user);

        // change some values
        savedItem.getMetadata().setGenre(Genre.ARTICLE);
        savedItem.getMetadata().setDatePublishedInPrint(getActualDateString());

        // save changed item
        savePubItem(savedItem, user);
    }

    /**
     * Test for
     * {@link PubItemDepositing#savePubItem(PubItemVO, de.mpg.escidoc.services.common.valueobjects.AccountUserVO)} with
     * already existing item.
     * 
     * @author Peter Broszeit
     * @throws Exception
     */
    @Ignore("Does not work in R2, because submitPubItem also releases it.")
    @Test
    public void testSaveExistingSubmittedPubItem() throws Exception
    {
        assertTrue(user.isDepositor());
        // create pubItem to get Reference
        PubItemVO pubItem = getNewPubItemWithoutFiles();
        // submit the item
        pubItem = pmDepositing.submitPubItem(pubItem, "testSaveExistingSubmittedPubItem", user);
        assertEquals(PubItemVO.State.SUBMITTED, pubItem.getVersion().getState());

        assertTrue(user.isModerator(new ContextRO(PUBMAN_TEST_COLLECTION_ID)));
        // change some values
        pubItem.getMetadata().setGenre(Genre.ARTICLE);
        pubItem.getMetadata().setDatePublishedInPrint(getActualDateString());
        // save changed item
        savePubItem(pubItem, user);
        assertEquals(PubItemVO.State.SUBMITTED, pubItem.getVersion().getState());
    }

    /**
     * Test for
     * {@link PubItemDepositing#savePubItem(PubItemVO, de.mpg.escidoc.services.common.valueobjects.AccountUserVO)} with
     * already existing item.
     * 
     * @author Peter Broszeit
     * @throws Exception
     */
    @Test
    public void testSaveExistingReleasedPubItem() throws Exception
    {
        user = getUserTestDepLibWithHandle();
        assertTrue(user.isDepositor());
        // create pubItem to get Reference
        PubItemVO item = getNewPubItemWithoutFiles();
        // submit the item
        PubItemVO savedItem = pmDepositing.submitAndReleasePubItem(item, "testSaveExistingReleasedPubItem", user);
        assertEquals(PubItemVO.State.RELEASED, savedItem.getVersion().getState());

        assertTrue(user.isModerator(new ContextRO(PUBMAN_TEST_COLLECTION_ID)));
        // change some values
        savedItem.getMetadata().setGenre(Genre.ARTICLE);
        savedItem.getMetadata().setDatePublishedInPrint(getActualDateString());
        // save changed item
        savedItem = savePubItem(savedItem, user);
        assertEquals(PubItemVO.State.SUBMITTED, savedItem.getVersion().getState());
    }

    /**
     * Test for
     * {@link PubItemDepositing#savePubItem(PubItemVO, de.mpg.escidoc.services.common.valueobjects.AccountUserVO)} with
     * already existing item.
     * 
     * @throws Exception
     */
    @Ignore
    @Test(expected = AuthorizationException.class)
    public void testSaveExistingPubItemWithOtherUser() throws Exception
    {
        // create pubItem to get Reference
        PubItemVO item = getNewPubItemWithoutFiles();
        PubItemVO savedItem = savePubItem(item, user);

        // change some values
        savedItem.getMetadata().setGenre(Genre.ARTICLE);
        savedItem.getMetadata().setDatePublishedInPrint(getActualDateString());
        savedItem.getMetadata().getAlternativeTitles().add(new TextVO("A new alternative title for this beautiful item", "en"));

        // save changed item with other user
        AccountUserVO depSci = getUserTestDepScientistWithHandle();
        savePubItem(savedItem, depSci);
    }

    /**
     * Test for
     * {@link PubItemDepositing#savePubItem(PubItemVO, de.mpg.escidoc.services.common.valueobjects.AccountUserVO)} with
     * already existing item.
     * 
     * @throws Exception
     */
    @Test(expected = PubItemNotFoundException.class)
    public void testSaveExistingPubItemWithWrongId() throws Exception
    {
        // create pubItem to get Reference
        PubItemVO item = getNewPubItemWithoutFiles();
        PubItemVO savedItem = savePubItem(item, user);

        // change id
        savedItem.getVersion().setObjectId("Xdoesnotexist");

        // save changed item
        savePubItem(savedItem, user);
    }

    /**
     * Test for
     * {@link PubItemDepositing#deletePubItem(ItemRO, de.mpg.escidoc.services.common.valueobjects.AccountUserVO)}
     * 
     * @throws Exception
     */
    @Test(expected = ItemNotFoundException.class)
    public void testDeletePubItem() throws Exception
    {
        // create pubItem to get Reference
        PubItemVO item = savePubItem(getNewPubItemWithoutFiles(), user);

        // delete the pubItem
        pmDepositing.deletePubItem(item.getVersion(), user);
        ServiceLocator.getItemHandler(user.getHandle()).retrieve(item.getVersion().getObjectId());
    }

    /**
     * Test for
     * {@link PubItemDepositing#deletePubItem(ItemRO, de.mpg.escidoc.services.common.valueobjects.AccountUserVO)}
     * 
     * @throws Exception
     */
    @Ignore("Does not work in R2, because submitPubItem alreday releases it.")
    @Test
    // (expected = PubItemStatusInvalidException.class)
    public void testDeleteSubmittedPubItem() throws Exception
    {
        // create pubItem to get Reference
        PubItemVO item = getNewPubItemWithoutFiles();
        item.setVersion(null);
        PubItemVO pubItem = pmDepositing.submitPubItem(item, "Test Submit", user);
        assertNotNull(pubItem);
        assertNotNull(pubItem.getVersion());

        // delete the pubItem
        pmDepositing.deletePubItem(pubItem.getVersion(), user);
    }

    /**
     * Checks if a pending item, that was released in a prior version, can be deleted.
     * 
     * @author Johannes Mueller
     * @throws Exception
     */
    @Test
    public void testDeletePendingAfterReleasedPubItem() throws Exception
    {
        AccountUserVO libUser = getUserTestDepLibWithHandle();
        // check user role
        assertTrue(libUser.isModerator(new ContextRO(PUBMAN_TEST_COLLECTION_ID)));

        // create pubItem
        PubItemVO item = getNewPubItemWithoutFiles();
        item.setVersion(null);

        // submit and release the item
        // in R2, the PubItemDepositing.submit method automatically releases the item, too!
        PubItemVO releasedItem = pmDepositing.submitAndReleasePubItem(item, "Test Submit", libUser);
        assertNotNull(releasedItem);
        assertEquals(PubItemVO.State.RELEASED, releasedItem.getVersion().getState());
        logger.info("Item was submitted and released. ObjId: " + releasedItem.getVersion().getObjectId());

        // try to delete the pubItem
        // and check whether it cannot be deleted (which is correct)
        try
        {
            pmDepositing.deletePubItem(releasedItem.getVersion(), libUser);
            fail("The item could be deleted although it is released!");
        }
        catch (Exception e)
        {
        }

        // remember the version number of the released item
        int releasedItemVersion = releasedItem.getVersion().getVersionNumber();

        // change item slightly
        // the item has to be changed to be able to really update it, see FIZ bug #376:
        // http://www.escidoc-project.de/issueManagement/show_bug.cgi?id=376
        String oldTitleValue = releasedItem.getMetadata().getTitle().getValue();
        releasedItem.getMetadata().setTitle(new TextVO(oldTitleValue + " MODIFIED"));

        // update the item
        String releasedItemXml = xmlTransforming.transformToItem(releasedItem);
        ItemHandler ihr = ServiceLocator.getItemHandler(libUser.getHandle());
        String updatedItemXml = ihr.update(releasedItem.getVersion().getObjectId(), releasedItemXml);
        PubItemVO updatedItem = xmlTransforming.transformToPubItem(updatedItemXml);

        // check whether the version number of the updated item higher than before
        int updatedItemVersion = updatedItem.getVersion().getVersionNumber();
        assertTrue("Updated item version: " + updatedItemVersion + "; released item version: " + releasedItemVersion, updatedItemVersion > releasedItemVersion);

        // check whether its status is pending now
        assertEquals("Status: " + updatedItem.getVersion().getState(), PubItemVO.State.PENDING, updatedItem.getVersion().getState());

        // TODO tendres: check whether this deletion of the item should be checked at all. according bug was stated as invalid
//        // try to delete the item
//        String itemVersionObjectId = updatedItem.getVersion().getObjectId() + ":" + updatedItemVersion;
//        logger.info("Trying to delete item '" + itemVersionObjectId + "'...");
//        try
//        {
//            ihr.delete(itemVersionObjectId);
//        }
//        catch (InvalidStatusException e)
//        {
//            fail("This problem will be solved with resolution of FIZ bug #377 (see http://www.escidoc-project.de/issueManagement/show_bug.cgi?id=377).");
//        }
    }

    /**
     * Test for
     * {@link PubItemDepositing#submitPubItem(PubItemVO, de.mpg.escidoc.services.common.valueobjects.AccountUserVO)}
     * 
     * @throws Exception
     */
    @Test
    public void testSubmitNewPubItem() throws Exception
    {
        // create pubItem to get Reference
        PubItemVO item = getNewPubItemWithoutFiles();
        item.setVersion(null);
        PubItemVO submittedPubItem = pmDepositing.submitPubItem(item, "Test Submit", user);
        assertNotNull(submittedPubItem);
        assertNotNull(submittedPubItem.getVersion());

        // retrieve item to verify state
        PubItemVO retrievedSubmittedPubItem = getPubItemFromFramework(submittedPubItem.getVersion(), user);

        // as long as no workflow is used, item is released after submission!!!!!
        assertEquals(PubItemVO.State.SUBMITTED, retrievedSubmittedPubItem.getVersion().getState());
    }

    /**
     * Test for
     * {@link PubItemDepositing#submitPubItem(PubItemVO, de.mpg.escidoc.services.common.valueobjects.AccountUserVO)}
     * 
     * @throws Exception
     */
    @Test
    public void testSubmitExistingPubItem() throws Exception
    {
        // create pubItem to get Reference
        PubItemVO item = savePubItem(getNewPubItemWithoutFiles(), user);
        PubItemVO submittedPubItem = pmDepositing.submitPubItem(item, "Test Submit", user);
        assertNotNull(submittedPubItem);
        assertNotNull(submittedPubItem.getVersion());

        // retrieve item to verify state
        PubItemVO retrievedSubmittedPubItem = getPubItemFromFramework(submittedPubItem.getVersion(), user);

        // as long as no workflow is used, item is released after submission!!!!!
        assertEquals(PubItemVO.State.SUBMITTED, retrievedSubmittedPubItem.getVersion().getState());
    }

    /**
     * Test for
     * {@link PubItemDepositing#submitPubItem(PubItemVO, de.mpg.escidoc.services.common.valueobjects.AccountUserVO)}
     * 
     * @throws Exception
     */
    @Test(expected = AuthorizationException.class)
    public void testSubmitExistingPubItemWithOtherUser() throws Exception
    {
        // create pubItem to get Reference
        PubItemVO item = savePubItem(getNewPubItemWithoutFiles(), user);

        AccountUserVO depSci = getUserTestDepScientistWithHandle();
        pmDepositing.submitPubItem(item, "Test Submit", depSci);
    }

    /**
     * Test for
     * {@link PubItemDepositing#acceptPubItem(PubItemVO, String, de.mpg.escidoc.services.common.valueobjects.AccountUserVO)}
     * 
     * @author Peter Broszeit
     * @throws Exception
     */
    @Test
    public void testAcceptExistingSubmittedPubItem() throws Exception
    {
        user = getUserTestDepLibWithHandle();
        assertTrue(user.isDepositor());
        // create pubItem to get Reference
        PubItemVO pubItem = savePubItem(getNewPubItemWithoutFiles(), user);

        // The next line can not be used at the moment, because the item is also released.
        // pubItem = pmDepositing.submitPubItem(pubItem, "testAcceptExistingSubmittedPubItem", user);
        TaskParamVO taskParam = new TaskParamVO(pubItem.getModificationDate(), "testAcceptExistingSubmittedPubItem");
        ServiceLocator.getItemHandler(user.getHandle()).submit(pubItem.getVersion().getObjectId(), xmlTransforming.transformToTaskParam(taskParam));
        ApplicationLog.info(PMLogicMessages.PUBITEM_SUBMITTED, new Object[] { pubItem.getVersion().getObjectId(), user.getUserid() });
        // item has to be retrieved again to get actual modification date
        String item = ServiceLocator.getItemHandler(user.getHandle()).retrieve(pubItem.getVersion().getObjectId());
        pubItem = xmlTransforming.transformToPubItem(item);
        assertEquals(PubItemVO.State.SUBMITTED, pubItem.getVersion().getState());

        assertTrue(user.isModerator(new ContextRO(PUBMAN_TEST_COLLECTION_ID)));
        // Accept the Pubitem
        pubItem = pmDepositing.acceptPubItem(pubItem, "Test Accept", user);
        assertNotNull(pubItem);
        assertNotNull(pubItem.getVersion());
        // as long as no workflow is used, item is released after accept!!!!!
        assertEquals(PubItemVO.State.RELEASED, pubItem.getVersion().getState());
    }

    /**
     * Test for
     * {@link PubItemDepositing#acceptPubItem(PubItemVO, String, de.mpg.escidoc.services.common.valueobjects.AccountUserVO)}
     * 
     * @author Peter Broszeit
     * @throws Exception
     */
    @Test
    public void testAcceptExistingReleasedPubItem() throws Exception
    {
        user = getUserTestDepLibWithHandle();
        assertTrue(user.isDepositor());
        // create pubItem to get Reference
        PubItemVO pubItem = getNewPubItemWithoutFiles();
        // submit the item
        pubItem = pmDepositing.submitAndReleasePubItem(pubItem, "testAcceptExistingReleasedPubItem", user);
        assertEquals(PubItemVO.State.RELEASED, pubItem.getVersion().getState());

        assertTrue(user.isModerator(new ContextRO(PUBMAN_TEST_COLLECTION_ID)));
        // save changed item
        pubItem.getMetadata().setGenre(Genre.ISSUE);
        pubItem = savePubItem(pubItem, user);
        
        logger.debug("Object state: " + pubItem.getVersion().getState());
        
        assertEquals(PubItemVO.State.SUBMITTED, pubItem.getVersion().getState());
        // accept the item
        pubItem = pmDepositing.acceptPubItem(pubItem, "Test Accept", user);
        assertNotNull(pubItem);
        assertNotNull(pubItem.getVersion());
        // as long as no workflow is used, item is released after accept!!!!!
        assertEquals(PubItemVO.State.RELEASED, pubItem.getVersion().getState());
    }

    /**
     * Test for {@link PubItemDepositing#createRevisionOfItem(PubItemVO, String, ContextRO, AccountUserVO)}
     * 
     * @author Peter Broszeit
     * @throws Exception
     */
    @Test
    public void testCreateRevisionOfPubItem() throws Exception
    {
        assertTrue(user.isDepositor());
        // First create a new PubItem
        PubItemVO pubItem = getNewPubItemWithoutFiles();
        pubItem = savePubItem(pubItem, user);
        assertNotNull(pubItem);
        // and submit (release) it
        pubItem = pmDepositing.submitPubItem(pubItem, "Test Create Revision", user);
        assertNotNull(pubItem);
        assertNotNull(pubItem.getVersion());
        // Create a revision of this item.
        pubItem = pmDepositing.createRevisionOfItem(pubItem, "This is a isRevisionOf relation.", pubItem.getContext(), user);
        pubItem = pmDepositing.savePubItem(pubItem, user);
        assertNotNull(pubItem);
        assertNotNull(pubItem.getVersion());
        assertEquals(PubItemVO.State.PENDING, pubItem.getVersion().getState());
        assertEquals(pubItem.getVersion().getVersionNumber(), 1);
    }

    /**
     * Test for {@link PubItemDepositing#createRevisionOfItem(PubItemVO, String, ContextRO, AccountUserVO)}
     * 
     * @author Peter Broszeit
     * @throws Exception
     */
    @Test
    public void testUpdateRevisionOfPubItem() throws Exception
    {
        assertTrue(user.isDepositor());
        // First create a new PubItem
        PubItemVO pubItem = getNewPubItemWithoutFiles();
        pubItem = savePubItem(pubItem, user);
        assertNotNull(pubItem);
        // and submit (release) it
        pubItem = pmDepositing.submitPubItem(pubItem, "Test Create Revision", user);
        assertNotNull(pubItem);
        assertNotNull(pubItem.getVersion());
        // Create a revision of this item.
        pubItem = pmDepositing.createRevisionOfItem(pubItem, "This is a isRevisionOf relation.", pubItem.getContext(), user);
        pubItem = pmDepositing.savePubItem(pubItem, user);
        assertNotNull(pubItem);
        assertNotNull(pubItem.getVersion());
        assertEquals(PubItemVO.State.PENDING, pubItem.getVersion().getState());
        // Update the revision.
        pubItem.getMetadata().getTitle().setValue("This is a revision.");
        pubItem.getMetadata().getTitle().setLanguage("en");
        pubItem = pmDepositing.savePubItem(pubItem, user);
        assertNotNull(pubItem);
        assertEquals(PubItemVO.State.PENDING, pubItem.getVersion().getState());
    }

    /**
     * Test for {@link PubItemDepositing#getPubCollectionListForDepositing(AccountUserVO)}
     * 
     * @throws Exception
     */
    @Test
    public void testGetPubCollectionListForDepositing() throws Exception
    {
        List<ContextVO> pubCollectionList = pmDepositing.getPubCollectionListForDepositing(user);
        assertNotNull(pubCollectionList);
        assertEquals(1, pubCollectionList.size());
        ContextVO pubCollection = pubCollectionList.get(0);
        assertNotNull(pubCollection.getReference());
        assertEquals(PUBMAN_TEST_COLLECTION_NAME, pubCollection.getName());
        assertEquals(PUBMAN_TEST_COLLECTION_DESCRIPTION, pubCollection.getDescription());
    }
}
