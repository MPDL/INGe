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

package de.mpg.mpdl.inge.pubman.web.search.bean.criterion;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion.CriterionType;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion.LogicalOperator;


/**
 * person criterion vo for the advanced search.
 * 
 * @created 15-Mai-2007 15:15:07
 * @author NiH
 * @version 1.0 Revised by NiH: 13.09.2007
 */
public class PersonCriterion extends Criterion {
  // creator role for the search criterion
  private List<CreatorRole> creatorRole;
  private String identifier;

  /**
   * constructor.
   */
  public PersonCriterion() {}

  public List<CreatorRole> getCreatorRole() {
    return this.creatorRole;
  }

  public void setCreatorRole(List<CreatorRole> creatorRole) {
    this.creatorRole = creatorRole;
  }

  public String getIdentifier() {
    System.out.println(this.identifier);
    return this.identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  private String getRolesAsStringList() {
    final StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < this.creatorRole.size(); i++) {
      buffer.append(this.creatorRole.get(i).getUri());
      if (i != this.creatorRole.size() - 1) {
        buffer.append(" OR ");
      }
    }
    return buffer.toString();
  }

  @Override
  public ArrayList<MetadataSearchCriterion> createSearchCriterion() throws TechnicalException {
    final ArrayList<MetadataSearchCriterion> criterions = new ArrayList<MetadataSearchCriterion>();
    if (this.isSearchStringEmpty()) {
      return criterions;
    } else {
      if (this.identifier == null || "".equals(this.identifier)) {
        final MetadataSearchCriterion criterion =
            new MetadataSearchCriterion(CriterionType.PERSON, this.getSearchString());
        criterions.add(criterion);
      } else {
        final MetadataSearchCriterion criterion =
            new MetadataSearchCriterion(CriterionType.PERSON_IDENTIFIER, this.identifier);
        criterions.add(criterion);
      }
      if (this.creatorRole.size() != 0) {
        final MetadataSearchCriterion criterion1 =
            new MetadataSearchCriterion(CriterionType.PERSON_ROLE, this.getRolesAsStringList(),
                LogicalOperator.AND);
        criterions.add(criterion1);
      }
    }
    return criterions;
  }
}
