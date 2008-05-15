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

package test.common.encoding;

import java.util.List;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import test.common.TestBase;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.GrantVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * JUnit tests for the encoding of the communication between the framework and the common logic
 * and inside the transforming.
 * @author Michael Franke (initial creation)
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 436 $ $LastChangedDate: 2007-09-04 11:39:58 +0200 (Di, 04 Sep 2007) $
 *
 */
public class EncodingTest extends TestBase
{

    private String userHandle;
    private AccountUserVO userVO;
    private String userXml;
    private XmlTransforming xmlTransforming;

    private static final Logger LOGGER = Logger.getLogger(EncodingTest.class);

    /**
     * Login user and map transforming.
     * @throws Exception Any exception.
     */
    @Before
    public final void setUp() throws Exception
    {
        InitialContext context = new InitialContext();
        xmlTransforming = (XmlTransforming) context.lookup(XmlTransforming.SERVICE_NAME);
        // get user handle for user "test_dep_scientist"
        userHandle = loginScientist();
        // use this handle to retrieve user "escidoc:user1"
        userXml = ServiceLocator.getUserAccountHandler(userHandle).retrieve("escidoc:user1");
        // transform userXML to AccountUserVO
        userVO = xmlTransforming.transformToAccountUser(userXml);
        String userGrantXML = ServiceLocator
                .getUserAccountHandler(userHandle)
                .retrieveCurrentGrants(userVO.getReference()
                        .getObjectId());
        List<GrantVO> grants = ((XmlTransforming) getService(XmlTransforming.SERVICE_NAME)).transformToGrantVOList(userGrantXML);
        List<GrantVO> userGrants = userVO.getGrants();
        for (GrantVO grant:grants)
        {
            userGrants.add(grant);
        }
        userVO.setHandle(userHandle);
    }

    /**
     * Logout user after tests.
     * @throws Exception Any exception.
     */
    @After
    public final void tearDown() throws Exception
    {
        logout(userHandle);
    }

    /**
     * Retrieve an item from the framework.
     * @param itemId The id of the item.
     * @return The XML representation of this item.
     */
    // TODO FRM: Eliminate warning
    private String retrieveItem(final String itemId) throws Exception
    {
        return ServiceLocator.getItemHandler(userHandle).retrieve(itemId);
    }

    /**
     * Save an item to the framework.
     * @param itemId The id of the item.
     */
    // TODO FRM: Eliminate warning
    private String saveItem(final String itemId, final String itemXml) throws Exception
    {
        return ServiceLocator.getItemHandler(userHandle).update(itemId, itemXml);
    }

    /**
     * Creates an item in the framework.
     * @param itemId The id of the item.
     */
    private String createItem(final String itemXml) throws Exception
    {
        return ServiceLocator.getItemHandler(userHandle).create(itemXml);
    }

    /**
     * Test.
     * @throws Exception Any exception.
     */
    @Test
    public final void createItem() throws Exception
    {
        PubItemVO itemVO = getPubItem2();

        String encodingCharacters = "Achtung: \uc3a4\uc3b6\uc3bc\uc39f"+" éèç";

        Assert.assertTrue(encodingCharacters.length() == 17);

        itemVO.getMetadata().setTitle(new TextVO(encodingCharacters));
        String itemXml = xmlTransforming.transformToItem(itemVO);
        LOGGER.debug("itemXml: " + itemXml);
        Assert.assertTrue(itemXml.length() != itemXml.getBytes("UTF-8").length);
        Assert.assertTrue(itemXml.contains("ä"));
        String savedItemXml = createItem(itemXml);
        LOGGER.debug("Result: " + savedItemXml);
        Assert.assertTrue(savedItemXml.length() != savedItemXml.getBytes("UTF-8").length);
        Assert.assertTrue(savedItemXml.contains("ä"));
        Assert.assertTrue(!savedItemXml.contains("&#x"));

        PubItemVO savedItemVO = xmlTransforming.transformToPubItem(savedItemXml);

        Assert.assertTrue(savedItemVO.getMetadata().getTitle().getValue().length() == encodingCharacters.length());
    }
}
