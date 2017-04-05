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
package de.mpg.mpdl.inge.pubman.web.search.criterions.stringOrHiddenId;

import de.mpg.mpdl.inge.pubman.web.search.criterions.ElasticSearchIndexField;

@SuppressWarnings("serial")
public class ModifiedBySearchCriterion extends StringOrHiddenIdSearchCriterion {

  public ModifiedBySearchCriterion() {

  }

  @Override
  public String[] getCqlIndexForHiddenId(Index indexName) {

    switch (indexName) {
      case ESCIDOC_ALL:
        return new String[] {"escidoc.property.version.modified-by.href"};
      case ITEM_CONTAINER_ADMIN:
        return new String[] {"\"/properties/version/modified-by/id\""};
    }
    return null;
  }

  @Override
  public String[] getCqlIndexForSearchString(Index indexName) {
    switch (indexName) {
      case ESCIDOC_ALL:
        return new String[] {"escidoc.property.version.modified-by.title"};
      case ITEM_CONTAINER_ADMIN:
        return new String[] {"\"/properties/version/modified-by/xLinkTitle\""};
    }
    return null;
  }


  @Override
  public ElasticSearchIndexField[] getElasticSearchFieldForHiddenId() {
    return new ElasticSearchIndexField[] {new ElasticSearchIndexField(
        "version.modifiedByRO.objectId")};

  }

  @Override
  public ElasticSearchIndexField[] getElasticSearchFieldForSearchString() {
    return new ElasticSearchIndexField[] {new ElasticSearchIndexField("version.modifiedByRO.title")};
  }

  @Override
  public String getElasticSearchNestedPath() {
    return null;
  }



}