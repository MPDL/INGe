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

package test.common.xmltransforming.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import test.common.TestBase;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.AccountUserRO;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.referenceobjects.PubCollectionRO;
import de.mpg.escidoc.services.common.util.ObjectComparator;
import de.mpg.escidoc.services.common.valueobjects.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.PubCollectionVO;
import de.mpg.escidoc.services.common.valueobjects.PubFileVO;
import de.mpg.escidoc.services.common.valueobjects.PubCollectionVO.SubmissionMethod;
import de.mpg.escidoc.services.common.valueobjects.PubFileVO.Visibility;

/**
 * Test for pubCollection transforming of {@link XmlTransforming}.
 * 
 * @author Miriam Doelle (initial creation)
 * @author $Author: tendres $ (last modification)
 * @version $Revision: 663 $ $LastChangedDate: 2007-12-12 14:18:51 +0100 (Wed, 12 Dec 2007) $
 * @revised by MuJ: 20.09.2007
 */
public class TransformPubCollectionTest extends TestBase
{
    private static String TEST_FILE_ROOT = "test/xmltransforming/component/transformPubCollectionTest/";
    private static String CONTEXT_PUBCOLLECTION_SAMPLE_FILE = TEST_FILE_ROOT + "context_pubcollection_sample.xml";
    private static String CONTEXT_PUBCOLLECTION_FULL_SAMPLE_FILE = TEST_FILE_ROOT + "context_pubcollection_full_sample.xml";
    private static String CONTEXT_LIST_SAMPLE_FILE = TEST_FILE_ROOT + "context-list_sample.xml";

    private Logger logger = Logger.getLogger(getClass());
    
    /**
     * An instance of XmlTransforming.
     */
    private static XmlTransforming xmlTransforming;

    /**
     * Get an XmlTransforming instance once for all tests.
     * 
     * @throws Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        xmlTransforming = (XmlTransforming) getService(XmlTransforming.SERVICE_NAME);
    }

    /**
     * Test for {@link XmlTransforming#transformToPubCollection(String)}. Reads pubCollection (=context) [XML] from
     * file, transforms the pubCollection to PubCollectionVO and checks the results.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToPubCollection() throws Exception
    {
    	
    	System.out.println((new File(".")).getAbsolutePath());
    	
        // read pubCollection (=context) [XML] from file
        String context = readFile(CONTEXT_PUBCOLLECTION_SAMPLE_FILE);
        assertNotNull(context);

        // transform the pubCollection to PubCollectionVO
        PubCollectionVO pubCollection = xmlTransforming.transformToPubCollection(context);
        assertNotNull(pubCollection);
        assertEquals(getExpectedPubCollection().getDefaultMetadata(), pubCollection.getDefaultMetadata());

        // check results
        ObjectComparator oc = new ObjectComparator(getExpectedPubCollection(), pubCollection);
        assertTrue(oc.toString(), oc.isEqual());
    }

    /**
     * Test for {@link XmlTransforming#transformToPubCollection(String)}. Reads pubCollection (=context) [XML] from
     * file, transforms the pubCollection to PubCollectionVO and checks the results.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToPubCollectionFull() throws Exception
    {
        // read pubCollection (=context) [XML] from file
        String context = readFile(CONTEXT_PUBCOLLECTION_FULL_SAMPLE_FILE);
        assertNotNull(context);

        // transform the pubCollection to PubCollectionVO
        PubCollectionVO pubCollection = xmlTransforming.transformToPubCollection(context);
        assertNotNull(pubCollection);
        
        // check results
        PubCollectionVO expectedPubCollection = getExpectedPubCollection();
        // add all additional expected values from full sample
        expectedPubCollection.setDefaultFileVisibility(Visibility.PRIVATE);
        assertEquals(expectedPubCollection.getDefaultMetadata(), pubCollection.getDefaultMetadata());
        ObjectComparator oc = new ObjectComparator(expectedPubCollection, pubCollection);
        assertTrue(oc.toString(), oc.isEqual());
    }

    /**
     * Test for {@link XmlTransforming#transformToPubCollectionList(String)}. Reads list of pubCollections [XML] from
     * file, transforms the list to a List<PubCollectionVO> and checks the results.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToPubCollectionList() throws Exception
    {
        // read pubCollection list [XML] from file
        String contextList = readFile(CONTEXT_LIST_SAMPLE_FILE);
        assertNotNull(contextList);

        logger.debug("contextList:"  + contextList);
        
        // transform the list to a List<PubCollectionVO>
        List<PubCollectionVO> pubCollectionList = xmlTransforming.transformToPubCollectionList(contextList);
        assertNotNull(pubCollectionList);
        
        // check results
        assertEquals(2, pubCollectionList.size());
        PubCollectionVO expectedPubCollection = getExpectedPubCollection();
        expectedPubCollection.setDefaultFileVisibility(Visibility.PRIVATE);
        for (PubCollectionVO pubCollection : pubCollectionList)
        {
            assertEquals(expectedPubCollection.getDefaultMetadata(), pubCollection.getDefaultMetadata());

            ObjectComparator oc = new ObjectComparator(expectedPubCollection, pubCollection);
            assertTrue(oc.toString(), oc.isEqual());
        }
        List<PubCollectionVO> expectedPubCollectionList = new ArrayList<PubCollectionVO>();
        expectedPubCollectionList.add(expectedPubCollection);
        expectedPubCollectionList.add(expectedPubCollection);
        ObjectComparator oc = new ObjectComparator(expectedPubCollectionList, pubCollectionList);
        assertTrue(oc.toString(), oc.isEqual());
    }

    /**
     * Delivers a well-defined pubCollection.
     * 
     * @return The well-defined pubCollection.
     */
    private PubCollectionVO getExpectedPubCollection()
    {
        PubCollectionVO expected = new PubCollectionVO();
        expected.setName(PUBMAN_TEST_COLLECTION_NAME);
        expected.setDescription(PUBMAN_TEST_COLLECTION_DESCRIPTION);
        expected.setState(PubCollectionVO.State.OPENED);
        expected.setReference(new PubCollectionRO("escidoc:persistent3"));
        expected.setCreator(new AccountUserRO("escidoc:user42"));
        expected.setDefaultFileVisibility(PubFileVO.Visibility.PUBLIC);
        expected.setDefaultMetadata(null);
        expected.getResponsibleAffiliations().add(new AffiliationRO("escidoc:persistent13"));
        expected.getAllowedSubmissionMethods().add(SubmissionMethod.SINGLE_SUBMISSION);
        List<MdsPublicationVO.Genre> allowedGenres = expected.getAllowedGenres();
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
        return expected;
    }

}
