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

import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion.CriterionType;


/**
 * genre criterion vo for the advanced search.
 * 
 * @created 15-Mai-2007 15:06:37
 * @author NiH
 * @version 1.0
 * @updated 17-Jul-2007 17:46:17 Revised by NiH: 13.09.2007
 */
public class GenreCriterion extends Criterion {
  // the genre for the search criterion
  private List<MdsPublicationVO.Genre> genreList;

  /**
   * constructor.
   */
  public GenreCriterion() {}

  public List<MdsPublicationVO.Genre> getGenre() {
    return this.genreList;
  }

  public void setGenre(List<MdsPublicationVO.Genre> genre) {
    this.genreList = genre;
  }

  private String getSearchIdentifierByGenre(MdsPublicationVO.Genre g) {
    return g.getUri();
  }

  public String getSearchIdentifier(int position) {
    if (this.genreList.size() <= position) {
      return "";
    }

    return this.getSearchIdentifierByGenre(this.genreList.get(position));
  }

  private String getGenresAsStringList() {
    final StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < this.genreList.size(); i++) {
      buffer.append(this.getSearchIdentifierByGenre(this.genreList.get(i)));
      if (i != this.genreList.size() - 1) {
        buffer.append(" OR ");
      }
    }
    return buffer.toString();
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public ArrayList<MetadataSearchCriterion> createSearchCriterion() throws TechnicalException {
    final ArrayList<MetadataSearchCriterion> criterions = new ArrayList<MetadataSearchCriterion>();
    final MetadataSearchCriterion criterion =
        new MetadataSearchCriterion(CriterionType.GENRE, this.getGenresAsStringList());
    criterions.add(criterion);
    return criterions;
  }
}
