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

package test.xmltransforming.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import test.TestBase;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.AccountUserRO;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.util.ObjectComparator;
import de.mpg.escidoc.services.common.valueobjects.ContainerVO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.ValueObject;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PublicationAdminDescriptorVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;

/**
 * Test for pubCollection transforming of {@link XmlTransforming}.
 * 
 * @author Miriam Doelle (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @revised by MuJ: 20.09.2007
 */
public class TransformContextTest extends TestBase
{
    private static final String TEST_FILE_ROOT
        = "xmltransforming/component/transformContextTest/";
    private static final String CONTEXT_SAMPLE_FILE
        = TEST_FILE_ROOT + "context_sample.xml";
    private static final String CONTEXT_FULL_SAMPLE_FILE
        = TEST_FILE_ROOT + "context_full_sample.xml";
    private static final String CONTEXT_LIST_SAMPLE_FILE
        = TEST_FILE_ROOT + "context-list_sample.xml";
    private static final String MEMBER_LIST_SAMPLE_FILE
        = TEST_FILE_ROOT + "member-list_sample.xml";

    private Logger logger = Logger.getLogger(getClass());
    
    /**
     * An instance of XmlTransforming.
     */
    private static XmlTransforming xmlTransforming = new XmlTransformingBean();

    /**
     * Test for {@link XmlTransforming#transformToPubCollection(String)}. Reads pubCollection (=context) [XML] from
     * file, transforms the pubCollection to ContextVO and checks the results.
     * 
     * @throws Exception Any exception.
     */
    @Test
    public void testTransformToContext() throws Exception
    {
        
        System.out.println((new File(".")).getAbsolutePath());
        
        // read pubCollection (=context) [XML] from file
        String context = readFile(CONTEXT_SAMPLE_FILE);
        assertNotNull(context);

        logger.info("Context sample: " + context);
        
        // transform the pubCollection to ContextVO
        ContextVO contextVO = xmlTransforming.transformToContext(context);
        
        
        
        assertNotNull(contextVO);
        assertEquals(getExpectedContext().getDefaultMetadata(), contextVO.getDefaultMetadata());

        // check results
        ObjectComparator oc = new ObjectComparator(getExpectedContext(), contextVO);
        assertTrue(oc.toString(), oc.isEqual());
    }

    /**
     * Test for {@link XmlTransforming#transformToPubCollection(String)}. Reads pubCollection (=context) [XML] from
     * file, transforms the pubCollection to ContextVO and checks the results.
     * 
     * @throws Exception Any exception.
     */
    @Test
    public void testTransformToContextFull() throws Exception
    {
        // read pubCollection (=context) [XML] from file
        String context = readFile(CONTEXT_FULL_SAMPLE_FILE);
        assertNotNull(context);

        // transform the pubCollection to ContextVO
        ContextVO contextVO = xmlTransforming.transformToContext(context);
        assertNotNull(contextVO);
        
        // check results
        ContextVO expectedContext = getExpectedContext();
        // add all additional expected values from full sample
        assertEquals(expectedContext.getDefaultMetadata(), contextVO.getDefaultMetadata());
        ObjectComparator oc = new ObjectComparator(expectedContext, contextVO);
        assertTrue(oc.toString(), oc.isEqual());
    }

    /**
     * Test for {@link XmlTransforming#transformToPubCollectionList(String)}. Reads list of pubCollections [XML] from
     * file, transforms the list to a {@link List&lt;ContextVO>} and checks the results.
     * 
     * @throws Exception Any exception.
     */
    @Test
    public void testTransformToContextList() throws Exception
    {
        // read pubCollection list [XML] from file
        String contextList = readFile(CONTEXT_LIST_SAMPLE_FILE);
        assertNotNull(contextList);

        logger.info("contextList:"  + contextList);
        
        // transform the list to a List<ContextVO>
        List<ContextVO> contextVOList = xmlTransforming.transformToContextList(contextList);
        assertNotNull(contextVOList);
        
        // check results
        assertEquals(2, contextVOList.size());
        ContextVO expectedPubCollection = getExpectedContext();
        for (ContextVO pubCollection : contextVOList)
        {
            assertEquals(expectedPubCollection.getDefaultMetadata(), pubCollection.getDefaultMetadata());

            ObjectComparator oc = new ObjectComparator(expectedPubCollection, pubCollection);
            assertTrue(oc.toString(), oc.isEqual());
        }
        List<ContextVO> expectedPubCollectionList = new ArrayList<ContextVO>();
        expectedPubCollectionList.add(expectedPubCollection);
        expectedPubCollectionList.add(expectedPubCollection);
        ObjectComparator oc = new ObjectComparator(expectedPubCollectionList, contextVOList);
        assertTrue(oc.toString(), oc.isEqual());
    }

    
    /**
     * Test for {@link XmlTransforming#transformToMemberList(String)}. Reads member list [XML] from
     * file, transforms the list to a {@link List&lt;MemberListVO>} and checks the results.
     * 
     * @throws Exception Any exception.
     */
    
    @Test
    public void testTransformToMemberList() throws Exception
    {
        // read pubCollection list [XML] from file
        String memberList = readFile(MEMBER_LIST_SAMPLE_FILE);
        assertNotNull(memberList);

        logger.info("memberList:"  + memberList);
        
        // transform the list to a MemberList
        List<? extends ValueObject> mList = xmlTransforming.transformToMemberList(memberList);
        assertNotNull(mList);

        assertEquals(2, mList.size());
        
    }
    
    
    /**
     * Delivers a well-defined pubCollection.
     * 
     * @return The well-defined pubCollection.
     */
    private ContextVO getExpectedContext()
    {
        ContextVO expected = new ContextVO();
        expected.setName(PUBMAN_TEST_COLLECTION_NAME);
        expected.setDescription(PUBMAN_TEST_COLLECTION_DESCRIPTION);
        expected.setType("PubMan");
        expected.setState(ContextVO.State.OPENED);
        expected.setReference(new ContextRO("escidoc:persistent3"));
        expected.setCreator(new AccountUserRO("escidoc:user42"));
        PublicationAdminDescriptorVO adminDescriptor = new PublicationAdminDescriptorVO();
        expected.getAdminDescriptors().add(adminDescriptor);
        expected.setDefaultMetadata(null);
        expected.getResponsibleAffiliations().add(new AffiliationRO("escidoc:persistent13"));
        List<MdsPublicationVO.Genre> allowedGenres = adminDescriptor.getAllowedGenres();
        // MdsPublicationVO.Genre.MANUSCRIPT must not be added!
        allowedGenres.add(MdsPublicationVO.Genre.ARTICLE);
        allowedGenres.add(MdsPublicationVO.Genre.BOOK);
        allowedGenres.add(MdsPublicationVO.Genre.BOOK_ITEM);
        allowedGenres.add(MdsPublicationVO.Genre.PROCEEDINGS);
        allowedGenres.add(MdsPublicationVO.Genre.CONFERENCE_PAPER);
        allowedGenres.add(MdsPublicationVO.Genre.TALK_AT_EVENT);
        allowedGenres.add(MdsPublicationVO.Genre.CONFERENCE_REPORT);
        allowedGenres.add(MdsPublicationVO.Genre.POSTER);
        allowedGenres.add(MdsPublicationVO.Genre.COURSEWARE_LECTURE);
        allowedGenres.add(MdsPublicationVO.Genre.THESIS);
        allowedGenres.add(MdsPublicationVO.Genre.PAPER);
        allowedGenres.add(MdsPublicationVO.Genre.REPORT);
        allowedGenres.add(MdsPublicationVO.Genre.JOURNAL);
        allowedGenres.add(MdsPublicationVO.Genre.ISSUE);
        allowedGenres.add(MdsPublicationVO.Genre.SERIES);
        allowedGenres.add(MdsPublicationVO.Genre.OTHER);
        //adminDescriptor.setVisibilityOfReferences("standard");
        
        adminDescriptor.setTemplateItem(new ItemRO("escidoc:123"));
        adminDescriptor.setValidationSchema("publication");
        adminDescriptor.setWorkflow(PublicationAdminDescriptorVO.Workflow.STANDARD);
        adminDescriptor.setContactEmail("pubman@mpdl.mpg.de");
        return expected;
    }

}
