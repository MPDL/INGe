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

import java.util.List;

import org.junit.Test;

import de.mpg.mpdl.inge.model.valueobjects.AffiliationResultVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemResultVO;
import de.mpg.mpdl.inge.model.valueobjects.interfaces.SearchResultElement;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.XmlTransformingTestBase;

/**
 * TODO Description
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class TransformSearchResultTest extends XmlTransformingTestBase {
  private static final String TEST_FILE_ROOT = "xmltransforming/component/transformSearchResultTest/";
  private static final String SEARCH_SAMPLE_FILE1 = TEST_FILE_ROOT + "search-result_sample.xml";
  private static final String SEARCH_SAMPLE_FILE3 = TEST_FILE_ROOT + "search-result_sample3.xml";
  private static final String SEARCH_SAMPLE_FILE4 = TEST_FILE_ROOT + "search-retrieve-response_sample.xml";

  @Test
  public void testItemSearchResult() throws Exception {
    String searchResultXML = readFile(SEARCH_SAMPLE_FILE1);
    SearchResultElement itemResultVO = XmlTransformingService.transformToSearchResult(searchResultXML);
    assertNotNull(itemResultVO);
    assertTrue(itemResultVO instanceof ItemResultVO);

    assertTrue(((ItemResultVO) itemResultVO).getFiles().size() == 1);

  }

  @Test
  public void testAffiliationSearchResult() throws Exception {
    String searchResultXML = readFile(SEARCH_SAMPLE_FILE3);
    SearchResultElement affiliationResultVO = XmlTransformingService.transformToSearchResult(searchResultXML);
    assertNotNull(affiliationResultVO);
    assertTrue(affiliationResultVO instanceof AffiliationResultVO);

    assertTrue(((AffiliationResultVO) affiliationResultVO).getMetadataSets().size() == 1);
    assertTrue(((AffiliationResultVO) affiliationResultVO).getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO);
    assertEquals("MPI for the History of Science",
        ((MdsOrganizationalUnitDetailsVO) ((AffiliationResultVO) affiliationResultVO).getMetadataSets().get(0)).getName());
  }

  @Test
  public void testContextListSearchRetrieveResponse() throws Exception {
    String searchResultXML = readFile(SEARCH_SAMPLE_FILE4);
    List<ContextVO> contextListVO = XmlTransformingService.transformToContextList(searchResultXML);
    assertNotNull(contextListVO);
    assertTrue(contextListVO.size() == 1);

    ContextVO contextVO = contextListVO.get(0);
    assertTrue(contextVO instanceof ContextVO);

    assertEquals("Wrong Context name", contextVO.getName(), "PubMan Default Context");
    assertEquals("Wrong Context Id", contextVO.getReference().getObjectId(), "escidoc:2001");
    assertEquals("Wrong Context Created-by", contextVO.getCreator().getObjectId(), "escidoc:exuser1");
  }
}
