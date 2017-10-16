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
package de.mpg.mpdl.inge.pubman.web.search.criterions.standard;

@SuppressWarnings("serial")
public class FlexibleStandardSearchCriterion extends StandardSearchCriterion {

  private final String[] cqlIndexes;
  private String[] elIndexes;


  public FlexibleStandardSearchCriterion(String[] cqlIndexes, String searchString) {
    this.cqlIndexes = cqlIndexes;
    this.setSearchString(searchString);

  }

  public FlexibleStandardSearchCriterion(String[] cqlIndexes, String[] elIndexes,
      String searchString) {
    this.setSearchString(searchString);
    this.elIndexes = elIndexes;
    this.cqlIndexes = cqlIndexes;

  }

  @Override
  public String[] getCqlIndexes(Index indexName) {
    return this.cqlIndexes;
  }

  /*
   * @Override public SearchCriterion getSearchCriterion() { return SearchCriterion.FLEXIBLE; }
   */

  @Override
  public String getElasticSearchNestedPath() {
    return null;
  }

  @Override
  public String[] getElasticIndexes() {
    return this.elIndexes;
  }


}
