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

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import test.common.xmltransforming.XmlTransformingTestBase;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.GrantVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsFileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Test class for framework bug #213
 * 
 * @author Johannes M&uuml;ller (initial creation)
 * @author $Author: jmueller $ (last change)
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $
 * @revised by MuJ: 03.09.2007
 */
public class Bug213SetContentTypeToNullTest extends XmlTransformingTestBase
{
    private Logger logger = Logger.getLogger(getClass());
    private static XmlTransforming xmlTransforming;
    String userHandle;
    private AccountUserVO user;

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception
    {
        xmlTransforming = (XmlTransforming)getService(XmlTransforming.SERVICE_NAME);
        userHandle = loginScientist();
        String userXML = ServiceLocator.getUserAccountHandler(userHandle).retrieve("escidoc:user1");
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
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception
    {
        logout(userHandle);
    }

    /**
     * Checks if FIZ bugzilla issue #213 still exists (When you add components to an item and update it, the framework
     * sets the component-type of the added components to "null".) The test directly accessed the framework (i.e. does
     * not use the intermediate services in the logic layer.)
     * 
     * @throws Exception
     */
    @Test
    public void testBug213SetContentTypeToNull() throws Exception
    {
        logger.info("### testBug213SetContentTypeToNull ###");

        // log in as scientist
        String scientistUserHandle = loginScientist();

        // create a new item with one component using framework_access directly

        // obtain a new PubItemVO containing some metadata content from the base class
        PubItemVO pubItemVOPreCreate = getPubItemWithoutFiles();

        // add file to PubItemVO
        FileVO fileVO = new FileVO();
        String testFileName = "test/xmltransforming/integration/bug213SetContentTypeToNullTest/Bundhose_PLU-213.jpg";
        // first upload the file to the framework
        fileVO.setContent(uploadFile(testFileName, "image/jpeg", scientistUserHandle).toString());
        // set some properties of the FileVO (mandatory fields first of all)
        fileVO.setContentCategory("post-print");
        fileVO.setName("farbtest_wasserfarben.jpg");
        fileVO.setDescription("Ein Farbtest mit Wasserfarben.");
        fileVO.setVisibility(Visibility.PUBLIC);
        MdsFileVO mdsFileVO = new MdsFileVO();
        mdsFileVO.setSize((int)ResourceUtil.getResourceAsFile(testFileName).length());
        mdsFileVO.setTitle(new TextVO(fileVO.getName()));
        fileVO.getMetadataSets().add(mdsFileVO);
        // and add it to the PubItemVO's files list
        pubItemVOPreCreate.getFiles().add(fileVO);

        // transform the PubItemVO into an item (for create)
        String pubItemXMLPreCreate = xmlTransforming.transformToItem(pubItemVOPreCreate);
        logger.info("PubItemVO with one file transformed to item(XML) for create." + "Content type of file: " + pubItemVOPreCreate.getFiles().get(0).getContentCategory());

        // create the item in the framework
        String pubItemXMLPostCreate = ServiceLocator.getItemHandler(scientistUserHandle).create(pubItemXMLPreCreate);
        logger.info("item(XML) created in the framework.");
        logger.debug("Response from framework =" + pubItemXMLPostCreate);

        // check the component type of the first component
        Document itemDoc = getDocument(pubItemXMLPostCreate, false);
        Node componentType = selectSingleNode(itemDoc, "/escidocItem:item/escidocComponents:components");// /escidocComponents:component/escidocComponents:properties/escidocComponents:component-type");
        logger.info("Content type of first file in returned item: " + componentType);

        // transform the returned item to a PubItemVO
        PubItemVO pubItemVOPreUpdate = xmlTransforming.transformToPubItem(pubItemXMLPostCreate);
        logger.debug("Returned item transformed back to PubItemVO.");

        // add another file to PubItemVO
        fileVO = new FileVO();
        testFileName = "test/xmltransforming/integration/bug213SetContentTypeToNullTest/Koax-System PCE 213.jpg";
        // first upload the file to the framework
        fileVO.setContent(uploadFile(testFileName, "image/jpeg", scientistUserHandle).toString());
        // set some properties of the FileVO (mandatory fields first of all)
        fileVO.setContentCategory("supplementary-material");
        fileVO.setName("farbtest_wasserfarben.jpg");
        fileVO.setDescription("Ein Farbtest mit Wasserfarben.");
        fileVO.setVisibility(Visibility.PUBLIC);
        //fileVO.setSize((int)new File(testFileName).length());
        // and add it to the PubItemVO's files list
        pubItemVOPreUpdate.getFiles().add(fileVO);

        // transform the PubItemVO into an item again
        String pubItemXMLPreUpdate = xmlTransforming.transformToItem(pubItemVOPreUpdate);
        String id = getObjid(pubItemXMLPreUpdate);
        // logger.info("PubItemVO with added second file transformed to item(XML) for update." + "Content type of file:
        // "
        // + pubItemVOPreCreate.getFiles().get(1).getContentType());

        // update the item in the framework
        logger.info("Trying to update the item in the framework...");
        String pubItemXMLPostUpdate = ServiceLocator.getItemHandler(user.getHandle()).update(id, pubItemXMLPreUpdate);
        assertNotNull(pubItemXMLPostUpdate);
        logger.info("item(XML) updated in the framework.");
        if (logger.isDebugEnabled())
        {
            logger.debug("Item objid: " + getObjid(pubItemXMLPostUpdate) + "\nResponse from framework =\n######\n" + pubItemXMLPostUpdate + "\n######\n");
        }

        // check the component type of both components
        itemDoc = getDocument(pubItemXMLPostUpdate, false);
        Node componentType1 = selectSingleNode(itemDoc, "/escidocItem:item/escidocComponents:components/escidocComponents:component/escidocComponents:properties/escidocComponents:component-type");
        logger.info("Content type of first file in returned item: " + componentType1);
        Node componentType2 = selectSingleNode(itemDoc, "/escidocItem:item/escidocComponents:components/escidocComponents:component/escidocComponents:properties/escidocComponents:component-type");
        logger.info("Content type of second file in returned item: " + componentType2);
    }
}
