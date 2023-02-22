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

package de.mpg.mpdl.inge.model.valueobjects.comparator;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;

/**
 * Test cases for PubItemVOComparator with criterion REVIEW_METHOD.
 * 
 * @author Peter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$ Revised by BrP: 03.09.2007
 */
public class ReviewMethodComparatorTest extends ComparatorTestBase {
  private static final Logger logger = Logger.getLogger(ReviewMethodComparatorTest.class);

  /**
   * Test for sorting ascending.
   */
  @Test
  public void sortReviewMethodAscending() {
    ArrayList<PubItemVO> list = getPubItemList();
    Collections.sort(list, new PubItemVOComparator(PubItemVOComparator.Criteria.REVIEW_METHOD));
    for (PubItemVO itemVO : list) {
      logger.debug(itemVO.getMetadata().getReviewMethod() + " (" + itemVO.getVersion().getObjectId() + ")");
    }
    String[] expectedIdOrder = new String[] {"1", "1", "3", "2", "4"};
    assertObjectIdOrder(list, expectedIdOrder);
  }

  /**
   * Test for sorting descending.
   */
  @Test
  public void sortReviewMethodDescending() {
    ArrayList<PubItemVO> list = getPubItemList();
    Collections.sort(list, Collections.reverseOrder(new PubItemVOComparator(PubItemVOComparator.Criteria.REVIEW_METHOD)));
    for (PubItemVO itemVO : list) {
      logger.debug(itemVO.getMetadata().getReviewMethod() + " (" + itemVO.getVersion().getObjectId() + ")");
    }
    String[] expectedIdOrder = new String[] {"4", "2", "3", "1", "1"};
    assertObjectIdOrder(list, expectedIdOrder);
  }

  /**
   * Test for comoparing two null values.
   */
  @Test
  public void compareTwoNullValues() {
    int rc = new PubItemVOComparator(PubItemVOComparator.Criteria.REVIEW_METHOD).compare(getPubItemVO4(), getPubItemVO4());
    assertEquals(0, rc);
  }
}
