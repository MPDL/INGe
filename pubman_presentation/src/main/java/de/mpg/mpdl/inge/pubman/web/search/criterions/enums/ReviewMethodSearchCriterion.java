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
package de.mpg.mpdl.inge.pubman.web.search.criterions.enums;

import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;


@SuppressWarnings("serial")
public class ReviewMethodSearchCriterion extends EnumSearchCriterion<MdsPublicationVO.ReviewMethod> {


  public ReviewMethodSearchCriterion() {
    super(MdsPublicationVO.ReviewMethod.class);
  }

  //  @Override
  //  public String[] getCqlIndexes(Index indexName) {
  //    switch (indexName) {
  //      case ESCIDOC_ALL:
  //        return new String[] {"escidoc.publication.review-method"};
  //      case ITEM_CONTAINER_ADMIN:
  //        return new String[] {"\"/md-records/md-record/publication/review-method\""};
  //    }
  //    return null;
  //
  //  }

  /*
   * @Override public SearchCriterion getSearchCriterion() { return SearchCriterion.REVIEW_METHOD; }
   */

  @Override
  public String getSearchString(MdsPublicationVO.ReviewMethod selectedEnum) {
    return selectedEnum.name();
  }

  @Override
  public String[] getElasticIndexes() {
    return new String[] {PubItemServiceDbImpl.INDEX_METADATA_REVIEW_METHOD};
  }

  @Override
  public String getElasticSearchNestedPath() {
    return null;
  }



}
