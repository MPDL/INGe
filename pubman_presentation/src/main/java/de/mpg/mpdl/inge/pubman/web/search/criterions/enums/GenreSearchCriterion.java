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
public class GenreSearchCriterion extends EnumSearchCriterion<MdsPublicationVO.Genre> {



  public GenreSearchCriterion() {
    super(MdsPublicationVO.Genre.class);
    // TODO Auto-generated constructor stub
  }

  //  @Override
  //  public String[] getCqlIndexes(Index indexName) {
  //
  //    switch (indexName) {
  //      case ESCIDOC_ALL:
  //        return new String[] {"escidoc.publication.type"};
  //      case ITEM_CONTAINER_ADMIN:
  //        return new String[] {"\"/md-records/md-record/publication/type\""};
  //    }
  //    return null;
  //
  //
  //  }

  /*
   * @Override public SearchCriterion getSearchCriterion() { return SearchCriterion.GENRE; }
   */

  @Override
  public String getSearchString(MdsPublicationVO.Genre selectedEnum) {
    return selectedEnum.name();
  }

  @Override
  public String[] getElasticIndexes() {
    return new String[] {PubItemServiceDbImpl.INDEX_METADATA_GENRE};
  }

  @Override
  public String getElasticSearchNestedPath() {
    return null;
  }



}
