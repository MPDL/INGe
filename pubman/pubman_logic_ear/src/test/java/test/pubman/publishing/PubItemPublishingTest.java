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

package test.pubman.publishing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import test.pubman.TestBase;
import de.fiz.escidoc.common.exceptions.application.notfound.ItemNotFoundException;
import de.fiz.escidoc.common.exceptions.application.security.SecurityException;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO.ContentType;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.pubman.PubItemDepositing;
import de.mpg.escidoc.services.pubman.PubItemPublishing;
import de.mpg.escidoc.services.pubman.exceptions.PubItemStatusInvalidException;

/**
 * Test class for {@link PubItemPublishing}
 * 
 * @author Miriam Doelle (initial creation)
 * @author $Author: pbroszei $ (last modification)
 * @version $Revision: 445 $ $LastChangedDate: 2007-11-22 09:43:02 +0100 (Thu, 22 Nov 2007) $
 * Revised by StG: 24.08.2007
 */
public class PubItemPublishingTest extends TestBase
{
    private static PubItemPublishing pmPublishing;
    private static PubItemDepositing pmDepositing;

    private AccountUserVO user;
    private AccountUserVO otherUser;

    /**
     * The setUp method for setting the needed values of the variables used in that test.
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception
    {
        pmPublishing = (PubItemPublishing)getService(PubItemPublishing.SERVICE_NAME);
        pmDepositing = (PubItemDepositing)getService(PubItemDepositing.SERVICE_NAME);
        user = getUserTestDepScientistWithHandle();
        otherUser = getUserTestDepLibWithHandle();
    }

    /**
     * Test of {@link PubItemPublishing#releasePubItem(ItemRO, java.util.Date, AccountUserVO)}
     * 
     * @throws Exception
     */
    @Test
    public void testReleasePubItem() throws Exception
    {
        // create pubItem to get Reference
        PubItemVO item = getNewPubItemWithoutFiles();
        ItemRO pubItemRef = pmDepositing.submitPubItem(item, "Test Submit", user).getVersion();
        assertNotNull(pubItemRef);

        Thread.sleep(3000);

        // retrieve item to verify state
        PubItemVO releasedPubItem = getPubItemFromFramework(pubItemRef, user);
        assertEquals(PubItemVO.State.RELEASED, releasedPubItem.getVersion().getState());

        // TODO FRM: uncomment after framework bugfix #188
        // assertNotNull("PID is null", releasedPubItem.getPid());
    }

    /**
     * Test of {@link PubItemPublishing#releasePubItem(ItemRO, java.util.Date, AccountUserVO)}
     * 
     * @throws Exception
     */
    @Test
    public void testReleasePubItemWithFile() throws Exception
    {

        // new item
        PubItemVO item = getNewPubItemWithoutFiles();
        // Add file to item
        FileVO file = new FileVO();
        String testfile = "src/test/resources/publishing/pubItemPublishingTest/farbtest.gif";
        file.setContent(uploadFile(testfile, "image/gif", user.getHandle()).toString());
        file.setMimeType("image/gif");
        file.setContentType(ContentType.PUBLISHER_VERSION);
        file.setMimeType("application/gif");
        file.setVisibility(Visibility.PUBLIC);
        file.setName("farbtest.gif");
        file.setDescription("Ein Farbtest.");
        item.getFiles().add(file);

        ItemRO pubItemRef = pmDepositing.submitPubItem(item, "Test Submit", user).getVersion();
        assertNotNull(pubItemRef);

        Thread.sleep(3000);

        // retrieve item to verify state
        PubItemVO releasedPubItem = getPubItemFromFramework(pubItemRef, user);
        assertEquals(PubItemVO.State.RELEASED, releasedPubItem.getVersion().getState());
        assertNotNull("PID is null (is okay, because PID concept not yet implemented by FIZ, see FIZ bugs 270,271)", releasedPubItem.getVersion().getPid());

        assertEquals(1, releasedPubItem.getFiles().size());
        FileVO pubFile = releasedPubItem.getFiles().get(0);
        assertNotNull("PID of file is null (is okay, because PID concept not yet implemented by FIZ, see FIZ bugs 270,271)", pubFile.getPid());

    }

    /**
     * Test of {@link PubItemPublishing#releasePubItem(ItemRO, java.util.Date, AccountUserVO)} with invald state.
     * 
     * @throws Exception
     */
    @Test
    public void testReleasePendingPubItem() throws Exception
    {
        // create pubItem to get Reference
        PubItemVO item = getNewPubItemWithoutFiles();
        PubItemVO pubItemSaved = pmDepositing.savePubItem(item, user);
        assertNotNull(pubItemSaved);
        try
        {
            pmPublishing.releasePubItem(pubItemSaved.getVersion(), pubItemSaved.getModificationDate(), "Test Release", user);
            fail("Exception expected!!!");

        }
        catch (PubItemStatusInvalidException e)
        {

        }
    }

    /**
     * Test of {@link PubItemPublishing#withdrawPubItem(ItemRO, java.util.Date, String, AccountUserVO)}
     * 
     * @throws Exception
     */
    @Ignore("Expected error not clear.")
    @Test(expected = ItemNotFoundException.class)
    public void testWithdrawPubItem() throws Exception
    {
        // create pubItem to get Reference
        PubItemVO item = getNewPubItemWithoutFiles();
        ItemRO pubItemRef = pmDepositing.submitPubItem(item, "Test Submit", user).getVersion();
        assertNotNull(pubItemRef);

        // retrieve item
        PubItemVO submittedPubItem = getPubItemFromFramework(pubItemRef, user);
        assertNotNull(submittedPubItem);

        // release not necessary, is included in submit
        assertEquals(PubItemVO.State.RELEASED, submittedPubItem.getVersion().getState());

        pmPublishing.withdrawPubItem(submittedPubItem, submittedPubItem.getModificationDate(), "That is why", user);

        // retrieve item to verify state
        try
        {
            PubItemVO withdrawnPubItem = getPubItemFromFramework(pubItemRef, user);
            // The following code cannot be reached, because an exception will be thrown before. Reason:
            // Withdrawn items cannot be retrieved (by objectId)
            // TODO FRM: Check whether this behaviour of the framework is okay.
            assertEquals(PubItemVO.State.WITHDRAWN, withdrawnPubItem.getVersion().getState());
            assertEquals("That is why", withdrawnPubItem.getWithdrawalComment());
        }
        catch (ItemNotFoundException e)
        {
            assertTrue(e.getFaultString().indexOf("Item is found but withdrawn.") > -1);
            throw e;
        }

    }

    /**
     * Test of {@link PubItemPublishing#withdrawPubItem(ItemRO, java.util.Date, String, AccountUserVO)}
     * with valid state.
     *
     * @throws Exception Any exception.
     */
    @Test
    public final void testWithdrawReleasedPubItem() throws Exception
    {
        // create pubItem to get Reference
        PubItemVO item = getNewPubItemWithoutFiles();
        ItemRO pubItemRef = pmDepositing.submitPubItem(item, "Test Submit", user).getVersion();
        assertNotNull(pubItemRef);

        Thread.sleep(3000);

        // retrieve item to verify state
        PubItemVO releasedPubItem = getPubItemFromFramework(pubItemRef, user);
        assertEquals(PubItemVO.State.RELEASED, releasedPubItem.getVersion().getState());

        pmPublishing.withdrawPubItem(releasedPubItem, releasedPubItem.getModificationDate(),
                "Dies ist dör withdrawal comment", user); //"Dies ist dör „withdrawal comment“"

        PubItemVO withdrawnPubItem = getPubItemFromFramework(pubItemRef, user);
        assertNotNull(withdrawnPubItem);
        assertEquals(withdrawnPubItem.getVersion().getState(), PubItemVO.State.WITHDRAWN);
    }

    /**
     * Test of {@link PubItemPublishing#withdrawPubItem(ItemRO, java.util.Date, String, AccountUserVO)}
     * with valid state.
     *
     * @throws Exception Any exception exept a security exception.
     */
    @Test
    public final void testWithdrawReleasedPubItemWithAdminUser() throws Exception
    {
        // create pubItem to get Reference
        PubItemVO item = getNewPubItemWithoutFiles();
        ItemRO pubItemRef = pmDepositing.submitPubItem(item, "Test Submit", user).getVersion();
        assertNotNull(pubItemRef);

        Thread.sleep(3000);

        // retrieve item to verify state
        PubItemVO releasedPubItem = getPubItemFromFramework(pubItemRef, user);
        assertEquals(PubItemVO.State.RELEASED, releasedPubItem.getVersion().getState());
        String withdrawalComment = "Dies ist dör withdrawal comment"; //"Dies ist dör „withdrawal comment“"
        pmPublishing.withdrawPubItem(releasedPubItem, releasedPubItem.getModificationDate(),
                withdrawalComment, otherUser);

        PubItemVO withdrawnPubItem = getPubItemFromFramework(pubItemRef, user);
        assertNotNull(withdrawnPubItem);
        assertEquals(withdrawnPubItem.getVersion().getState(), PubItemVO.State.WITHDRAWN);
        //assertEquals(withdrawnPubItem.getCurrentVersion().getComment(), withdrawalComment);
    }

    /**
     * Test of {@link PubItemPublishing#withdrawPubItem(ItemRO, java.util.Date, String, AccountUserVO)}
     * with valid state.
     *
     * @throws Exception Any exception exept a security exception.
     */
    @Ignore("At the moment, no user exists in the framework who hasn't the rights to withdraw an item.")
    @Test(expected = SecurityException.class)
    public final void testWithdrawReleasedPubItemWithWrongUser() throws Exception
    {
        // create pubItem to get Reference
        PubItemVO item = getNewPubItemWithoutFiles();
        ItemRO pubItemRef = pmDepositing.submitPubItem(item, "Test Submit", user).getVersion();
        assertNotNull(pubItemRef);

        Thread.sleep(3000);

        // retrieve item to verify state
        PubItemVO releasedPubItem = getPubItemFromFramework(pubItemRef, user);
        assertEquals(PubItemVO.State.RELEASED, releasedPubItem.getVersion().getState());
        String withdrawalComment = "Dies ist dör withdrawal comment"; //"Dies ist dör „withdrawal comment“"
        // Here, a SecurityExeption should be thrown
        pmPublishing.withdrawPubItem(releasedPubItem, releasedPubItem.getModificationDate(),
                withdrawalComment, otherUser);

    }

    /**
     * Test of {@link PubItemPublishing#withdrawPubItem(ItemRO, java.util.Date, String, AccountUserVO)} with invalid
     * state.
     * 
     * @throws Exception
     */
    @Test(expected = PubItemStatusInvalidException.class)
    public void testWithdrawPendingPubItem() throws Exception
    {
        // create pubItem to get Reference
        PubItemVO item = getNewPubItemWithoutFiles();
        item.setVersion(null);
        PubItemVO pubItemSaved = pmDepositing.savePubItem(item, user);
        assertNotNull(pubItemSaved);

        pmPublishing.withdrawPubItem(pubItemSaved, pubItemSaved.getModificationDate(),
                "That is why", user);
        fail("Exception expected!!!");

    }
}
