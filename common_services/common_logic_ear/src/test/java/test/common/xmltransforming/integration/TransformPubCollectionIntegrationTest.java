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

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.common.TestBase;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.AccountUserRO;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.util.ObjectComparator;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.GrantVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PublicationAdminDescriptorVO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Test for pubCollection transforming of {@link XmlTransforming}.
 * 
 * @author Johannes Mueller (initial creation)
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $
 * @revised by MuJ: 03.09.2007
 */
public class TransformPubCollectionIntegrationTest extends TestBase
{
    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(TransformPubCollectionIntegrationTest.class);

    private AccountUserVO user;
    String userHandle;

    /**
     * An instance of XmlTransforming.
     */
    private static XmlTransforming xmlTransforming;

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception
    {
        xmlTransforming = (XmlTransforming) getService(XmlTransforming.SERVICE_NAME);


        userHandle = loginScientist();
        String userXML = ServiceLocator.getUserAccountHandler(userHandle).retrieve("escidoc:user1");
        user = xmlTransforming.transformToAccountUser(userXML);
        String userGrantXML = ServiceLocator
                .getUserAccountHandler(userHandle)
                .retrieveCurrentGrants(user.getReference()
                        .getObjectId());
        List<GrantVO> grants = xmlTransforming.transformToGrantVOList(userGrantXML);
        List<GrantVO> userGrants = user.getGrants();
        for (GrantVO grant:grants)
        {
            userGrants.add(grant);
        }
        user.setHandle(userHandle);
    }

    /**
     * @throws Exception
     */
    @After
    public final void tearDown() throws Exception
    {
        logout(userHandle);
    }

    /**
     * Test for {@link XmlTransforming#transformToPubCollection(String)}.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToPubCollection() throws Exception
    {
        logger.info("### testTransformToPubCollection ###");
        
        String context = ServiceLocator.getContextHandler(userHandle).retrieve(PUBMAN_TEST_COLLECTION_ID);
        logger.info("Retrieved pubman collection '" + PUBMAN_TEST_COLLECTION_ID + "':\n" + toString(getDocument(context, false), false));        
        assertNotNull(context);

        ContextVO pubCollection = xmlTransforming.transformToContext(context);
        assertNotNull(pubCollection);
        assertEquals(getExpectedPubCollection().getDefaultMetadata(), pubCollection.getDefaultMetadata());

        ObjectComparator oc = new ObjectComparator(getExpectedPubCollection(), pubCollection);
        assertTrue(oc.toString(), oc.isEqual());
    }

    /**
     * Helper to retrieve the expected pubCollection.
     * 
     * @return the expected pubCollectionVO
     */
    private ContextVO getExpectedPubCollection()
    {
        ContextVO expected = new ContextVO();
        expected.setName(PUBMAN_TEST_COLLECTION_NAME);
        expected.setDescription(PUBMAN_TEST_COLLECTION_DESCRIPTION);
        expected.setState(ContextVO.State.OPENED);
        expected.setReference(new ContextRO("escidoc:persistent3"));
        expected.setCreator(new AccountUserRO("escidoc:user42"));
        expected.setDefaultMetadata(null);
        expected.getResponsibleAffiliations().add(new AffiliationRO("escidoc:persistent13"));
        
        PublicationAdminDescriptorVO adminDescriptorVO = new PublicationAdminDescriptorVO();
        expected.getAdminDescriptors().add(adminDescriptorVO);
        
        MdsPublicationVO.Genre[] allowed =
            new MdsPublicationVO.Genre[]{MdsPublicationVO.Genre.ARTICLE, MdsPublicationVO.Genre.BOOK, MdsPublicationVO.Genre.BOOK_ITEM, MdsPublicationVO.Genre.PROCEEDINGS, MdsPublicationVO.Genre.CONFERENCE_PAPER, MdsPublicationVO.Genre.TALK_AT_EVENT, MdsPublicationVO.Genre.CONFERENCE_REPORT, MdsPublicationVO.Genre.POSTER, MdsPublicationVO.Genre.COURSEWARE_LECTURE, MdsPublicationVO.Genre.THESIS, MdsPublicationVO.Genre.PAPER, MdsPublicationVO.Genre.REPORT, MdsPublicationVO.Genre.JOURNAL, MdsPublicationVO.Genre.ISSUE, MdsPublicationVO.Genre.SERIES, MdsPublicationVO.Genre.OTHER};
        for (int i = 0; i < allowed.length; i++)
        {
            adminDescriptorVO.getAllowedGenres().add(allowed[i]);
        }
        
        return expected;
    }
}
