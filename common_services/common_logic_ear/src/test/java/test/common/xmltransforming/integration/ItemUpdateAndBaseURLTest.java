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

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import test.common.xmltransforming.XmlTransformingTestBase;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.GrantVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO.ContentType;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Checks the behaviour of the item.@xml:base attribute during creation and update of items.
 * 
 * @author Johannes Mueller (initial creation)
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $
 * @revised by MuJ: 03.09.2007
 */
public class ItemUpdateAndBaseURLTest extends XmlTransformingTestBase
{
    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(ItemUpdateAndBaseURLTest.class);

    private static XmlTransforming xmlTransforming;
    private AccountUserVO user;
    String userhandle;

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception
    {
        xmlTransforming = (XmlTransforming) getService(XmlTransforming.SERVICE_NAME);
        userhandle = loginScientist();
        String userXML = ServiceLocator.getUserAccountHandler(userhandle).retrieve("escidoc:user1");
        user = xmlTransforming.transformToAccountUser(userXML);
        String userGrantXML = ServiceLocator
                .getUserAccountHandler(userhandle)
                .retrieveCurrentGrants(user.getReference()
                        .getObjectId());
        List<GrantVO> grants = xmlTransforming.transformToGrantVOList(userGrantXML);
        List<GrantVO> userGrants = user.getGrants();
        for (GrantVO grant:grants)
        {
            userGrants.add(grant);
        }

        user.setHandle(userhandle);
    }
    
    /**
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception
    {
        logout(userhandle);
    }

    /**
     * Checks whether the attribute item.@xml:base that is given back by the framework contains the correct framework
     * URL.
     * 
     * @throws Exception
     */
    @Test
    public void testItemUpdateViaStagingServletXmlBase() throws Exception
    {
        logger.info("### testItemUpdateViaStagingServletXmlBase ###");

        // log in as scientist
        String scientistUserHandle = loginScientist();

        // create a new item with one component using framework_access directly

        // obtain a new PubItemVO containing some metadata content from the base class
        PubItemVO pubItemVOPreCreate = getPubItemWithoutFiles();
        logger.info("PubItemVO created from the scratch.");

        // add file to PubItemVO
        FileVO fileVO = new FileVO();
        String testFileName = "test/xmltransforming/ItemUpdateAndBaseURLTest/galaxy.gif";
        // first upload the file to the framework
        String stagingURL = uploadFile(testFileName, "image/gif", scientistUserHandle).toString();
        fileVO.setContent(stagingURL);
        logger.info("File uploaded to staging area. Retrieved URL stored in FileVO.content[String]: " + stagingURL);
        // set some properties of the FileVO (mandatory fields first of all)
        fileVO.setContentType(ContentType.SUPPLEMENTARY_MATERIAL);
        fileVO.setName("galaxy.gif");
        fileVO.setDescription("The Universe within 50000 Light Years");
        fileVO.setVisibility(Visibility.PRIVATE);
        fileVO.setSize((int)new File(testFileName).length());
        // and add it to the PubItemVO's files list
        pubItemVOPreCreate.getFiles().add(fileVO);

        // transform the PubItemVO into an item (for create)
        long zeit = -System.currentTimeMillis();
        String pubItemXMLPreCreate = xmlTransforming.transformToItem(pubItemVOPreCreate);
        zeit += System.currentTimeMillis();

        logger.info("PubItemVO with file transformed to item(XML) for create.");
        logger.info("The item containts one file.");
        Document itemDoc = getDocument(pubItemXMLPreCreate, false);
        Node stagingServletURL = selectSingleNode(itemDoc,
                "/escidocItem:item/escidocComponents:components/escidocComponents:component/escidocComponents:content/@xlink:href");
        logger.info("Link on file in staging servlet:\n"
                + "(XPath /escidocItem:item/escidocComponents:components/escidocComponents:component/escidocComponents:content/@xlink:href):\n"
                + stagingServletURL);
        logger.debug("item(XML) =" + pubItemXMLPreCreate);

        // create the item in the framework
        String pubItemXMLPostCreate = ServiceLocator.getItemHandler(scientistUserHandle).create(pubItemXMLPreCreate);
        assertNotNull(pubItemXMLPostCreate);
        logger.info("item(XML) created in the framework. Framework URL: " + ServiceLocator.getFrameworkUrl());
        String objid = getObjid(pubItemXMLPostCreate);
        logger.info("Objid: " + objid);
        logger.debug("Response from framework =" + pubItemXMLPostCreate);

        // check the item.@xml:base attribute
        itemDoc = getDocument(pubItemXMLPostCreate, false);
        Node itemXmlBase = selectSingleNode(itemDoc, "/escidocItem:item/@xml:base");
        logger.info("/escidocItem:item/@xml:base: " + itemXmlBase);

        // retrieve the item from the framework
        String pubItemXMLRetrieved = ServiceLocator.getItemHandler(scientistUserHandle).retrieve(objid);
        assertNotNull(pubItemXMLRetrieved);
        logger.debug("Retrieved item =" + pubItemXMLRetrieved);
        itemDoc = null;

        // check the item.@xml:base attribute again
        itemDoc = getDocument(pubItemXMLRetrieved, false);
        itemXmlBase = selectSingleNode(itemDoc, "/escidocItem:item/@xml:base");
        logger.info("/escidocItem:item/@xml:base: " + itemXmlBase);
    }
}
