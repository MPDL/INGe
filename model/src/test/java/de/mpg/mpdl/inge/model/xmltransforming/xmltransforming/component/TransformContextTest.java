/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.mpg.mpdl.inge.model.referenceobjects.AccountUserRO;
import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.referenceobjects.ContextRO;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.ValueObject;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PublicationAdminDescriptorVO;
import de.mpg.mpdl.inge.model.xmltransforming.TestBase;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.util.ObjectComparator;

/**
 * Test for pubCollection transforming of {@link XmlTransforming}.
 * 
 * @author Miriam Doelle (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @revised by MuJ: 20.09.2007
 */
public class TransformContextTest extends TestBase {
  private static final String TEST_FILE_ROOT = "xmltransforming/component/transformContextTest/";
  private static final String CONTEXT_SAMPLE_FILE = TEST_FILE_ROOT + "context_sample.xml";
  private static final String CONTEXT_FULL_SAMPLE_FILE = TEST_FILE_ROOT + "context_full_sample.xml";
  private static final String CONTEXT_LIST_SAMPLE_FILE = TEST_FILE_ROOT + "context-list_sample.xml";
  private static final String CONTEXT_LIST_SAMPLE_FILE_SEARCH_RETRIEVE = TEST_FILE_ROOT + "context-list_sample_search_retrieve.xml";
  private static final String MEMBER_LIST_SAMPLE_FILE = TEST_FILE_ROOT + "member-list_sample.xml";

  private static final Logger logger = Logger.getLogger(TransformContextTest.class);

  /**
   * Test for {@link XmlTransforming#transformToPubCollection(String)}. Reads pubCollection
   * (=context) [XML] from file, transforms the pubCollection to ContextVO and checks the results.
   * 
   * @throws Exception Any exception.
   */
  @Test
  public void testTransformToContext() throws Exception {

    System.out.println((new File(".")).getAbsolutePath());

    // read pubCollection (=context) [XML] from file
    String context = readFile(CONTEXT_SAMPLE_FILE);
    assertNotNull(context);

    logger.info("Context sample: " + context);

    // transform the pubCollection to ContextVO
    ContextVO contextVO = XmlTransformingService.transformToContext(context);



    assertNotNull(contextVO);
    assertEquals(getExpectedContext().getDefaultMetadata(), contextVO.getDefaultMetadata());

    // check results
    ObjectComparator oc = new ObjectComparator(getExpectedContext(), contextVO);
    assertTrue(oc.toString(), oc.isEqual());
  }

  /**
   * Test for {@link XmlTransforming#transformToPubCollection(String)}. Reads pubCollection
   * (=context) [XML] from file, transforms the pubCollection to ContextVO and checks the results.
   * 
   * @throws Exception Any exception.
   */
  @Test
  public void testTransformToContextFull() throws Exception {
    // read pubCollection (=context) [XML] from file
    String context = readFile(CONTEXT_FULL_SAMPLE_FILE);
    assertNotNull(context);

    // transform the pubCollection to ContextVO
    ContextVO contextVO = XmlTransformingService.transformToContext(context);
    assertNotNull(contextVO);

    // check results
    ContextVO expectedContext = getExpectedContextFull();


    List<MdsPublicationVO.SubjectClassification> allowedSubjectClassifications =
        expectedContext.getAdminDescriptor().getAllowedSubjectClassifications();
    allowedSubjectClassifications.add(MdsPublicationVO.SubjectClassification.DDC);
    allowedSubjectClassifications.add(MdsPublicationVO.SubjectClassification.MPIPKS);


    // add all additional expected values from full sample
    assertEquals(expectedContext.getDefaultMetadata(), contextVO.getDefaultMetadata());
    ObjectComparator oc = new ObjectComparator(expectedContext, contextVO);
    assertTrue(oc.toString(), oc.isEqual());
  }

  /**
   * Test for {@link XmlTransforming#transformToPubCollectionList(String)}. Reads list of
   * pubCollections [XML] from file, transforms the list to a {@link List&lt;ContextVO>} and checks
   * the results.
   * 
   * @throws Exception Any exception.
   */
  @Test
  public void testTransformToContextListSearchRetrieve() throws Exception {
    logger.info("## testTransformToContextListSearchRetrieve ##");
    // read pubCollection list [XML] from file
    String contextList = readFile(CONTEXT_LIST_SAMPLE_FILE_SEARCH_RETRIEVE);
    assertNotNull(contextList);

    logger.info("contextList:" + contextList);

    // transform the list to a List<ContextVO>
    List<ContextVO> contextVOList = XmlTransformingService.transformToContextList(contextList);
    assertNotNull(contextVOList);

    // check results
    assertEquals(1, contextVOList.size());

    ContextVO contextVO = contextVOList.get(0);

    assertEquals(contextVO.getReference().getObjectId(), "escidoc:2001");
    assertEquals(contextVO.getName(), "PubMan Default Context");
    assertEquals(contextVO.getType(), "PubMan");
  }


  /**
   * Test for {@link XmlTransforming#transformToMemberList(String)}. Reads member list [XML] from
   * file, transforms the list to a {@link List&lt;MemberListVO>} and checks the results.
   * 
   * @throws Exception Any exception.
   */

  @Test
  public void testTransformToMemberList() throws Exception {
    // read pubCollection list [XML] from file
    String memberList = readFile(MEMBER_LIST_SAMPLE_FILE);
    assertNotNull(memberList);

    logger.info("memberList:" + memberList);

    // transform the list to a MemberList
    List<? extends ValueObject> mList = XmlTransformingService.transformToMemberList(memberList);
    assertNotNull(mList);

    assertEquals(2, mList.size());

  }


  /**
   * Delivers a well-defined pubCollection.
   * 
   * @return The well-defined pubCollection.
   * @throws ParseException
   */
  private ContextVO getExpectedContextFull() throws ParseException {
    ContextVO expected = new ContextVO();
    expected.setName(PUBMAN_TEST_COLLECTION_NAME);
    expected.setDescription(PUBMAN_TEST_COLLECTION_DESCRIPTION);
    expected.setType("PubMan");
    expected.setState(ContextVO.State.OPENED);
    expected.setReference(new ContextRO("escidoc:persistent3"));
    expected.setCreator(new AccountUserRO("escidoc:user42"));

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd'T'hh:mm:ss.SSS'Z'", Locale.GERMANY);
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    String dateInString = "2007-03-26T11:11:58.853Z";
    expected.setLastModificationDate(sdf.parse(dateInString));

    String creationDateInString = "2007-01-16T11:23:24.359Z";
    expected.setCreationDate(sdf.parse(creationDateInString));

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
    // adminDescriptor.setVisibilityOfReferences("standard");

    adminDescriptor.setTemplateItem(new ItemRO("escidoc:123"));
    adminDescriptor.setValidationSchema("publication");
    adminDescriptor.setWorkflow(PublicationAdminDescriptorVO.Workflow.STANDARD);
    adminDescriptor.setContactEmail("pubman@mpdl.mpg.de");
    return expected;
  }

  /**
   * Delivers a well-defined pubCollection.
   * 
   * @return The well-defined pubCollection.
   * @throws ParseException
   */
  private ContextVO getExpectedContext() throws ParseException {
    ContextVO expected = new ContextVO();
    expected.setName(PUBMAN_TEST_COLLECTION_NAME);
    expected.setDescription(PUBMAN_TEST_COLLECTION_DESCRIPTION);
    expected.setType("PubMan");
    expected.setState(ContextVO.State.OPENED);
    expected.setReference(new ContextRO("escidoc:persistent3"));
    expected.setCreator(new AccountUserRO("escidoc:user42"));

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd'T'hh:mm:ss.SSS'Z'", Locale.GERMANY);
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    String dateInString = "2007-07-18T07:48:33.656Z";
    expected.setLastModificationDate(sdf.parse(dateInString));

    String creationDateInString = "2007-01-16T11:23:24.359Z";
    expected.setCreationDate(sdf.parse(creationDateInString));

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
    // adminDescriptor.setVisibilityOfReferences("standard");

    adminDescriptor.setTemplateItem(new ItemRO("escidoc:123"));
    adminDescriptor.setValidationSchema("publication");
    adminDescriptor.setWorkflow(PublicationAdminDescriptorVO.Workflow.STANDARD);
    adminDescriptor.setContactEmail("pubman@mpdl.mpg.de");
    return expected;
  }

}
