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
package de.mpg.mpdl.inge.pubman.web.search.criterions.component;

import java.util.HashMap;
import java.util.Map;

import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;

@SuppressWarnings("serial")
public class ComponentVisibilityListSearchCriterion extends MapListSearchCriterion<String> {



  public ComponentVisibilityListSearchCriterion() {

    super(ComponentVisibilityListSearchCriterion.getVisibilityMap());

  }


  private static Map<String, String> getVisibilityMap() {
    final Map<String, String> visibilityMap = new HashMap<>();
    for (final FileDbVO.Visibility v : FileDbVO.Visibility.values()) {
      visibilityMap.put(v.name(), v.name());
    }
    return visibilityMap;
  }


  /*
   * @Override public SearchCriterion getSearchCriterion() { return
   * SearchCriterion.COMPONENT_VISIBILITY_LIST; }
   */
  //  @Override
  //  public String[] getCqlIndexes(Index indexName, String value) {
  //    switch (indexName) {
  //      case ESCIDOC_ALL:
  //        return new String[] {"escidoc.component.internal-managed.visibility"};
  //      case ITEM_CONTAINER_ADMIN:
  //        return new String[] {"\"/components/component/internal-managed/visibility\""};
  //    }
  //
  //    return null;
  //  }


  @Override
  public String getCqlValue(Index indexName, String value) {
    return value;
  }

  @Override
  public String[] getElasticIndexes(String value) {
    return new String[] {"files.visibility"};
  }

  @Override
  public String getElasticSearchNestedPath() {
    return "files";
  }


}
