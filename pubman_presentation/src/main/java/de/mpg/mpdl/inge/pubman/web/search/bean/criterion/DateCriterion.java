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

import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.search.query.MetadataDateSearchCriterion;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion.CriterionType;

/**
 * date criterion vo for the advanced search
 * 
 * @created 15-Mai-2007 15:46:13
 * @author NiH
 * @version 1.0 Revised by NiH: 13.09.2007
 */
public class DateCriterion extends Criterion {

  public enum DateType {
    ACCEPTED, CREATED, MODIFIED, PUBLISHED_ONLINE, PUBLISHED_PRINT, SUBMITTED, EVENT_START, EVENT_END
  }

  // date range for the search criterion
  private String from;
  private String to;
  // type of date
  private List<DateType> dateTypeList;

  /**
   * constructor.
   */
  public DateCriterion() {}

  public String getFrom() {
    return this.from;
  }

  public String getTo() {
    return this.to;
  }

  public void setFrom(String newVal) {
    this.from = newVal;
  }

  public void setTo(String newVal) {
    this.to = newVal;
  }

  public List<DateType> getDateType() {
    return this.dateTypeList;
  }

  public void setDateType(List<DateType> dateType) {
    this.dateTypeList = dateType;
  }

  /**
   * Checks if 'to' search term is empty
   * 
   * @return true if empty, false if not
   */
  private boolean isFromEmpty() {
    if (this.from == null || this.from.trim().equals("")) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Checks if 'to' search term is empty
   * 
   * @return true if empty, false if not
   */
  private boolean isToEmpty() {
    if (this.to == null || this.to.trim().equals("")) {
      return true;
    } else {
      return false;
    }
  }

  private CriterionType getCriterionByDateType(DateType dateType) throws TechnicalException {
    switch (dateType) {
      case ACCEPTED:
        return CriterionType.DATE_ACCEPTED;
      case CREATED:
        return CriterionType.DATE_CREATED;
      case MODIFIED:
        return CriterionType.DATE_MODIFIED;
      case PUBLISHED_ONLINE:
        return CriterionType.DATE_PUBLISHED_ONLINE;
      case PUBLISHED_PRINT:
        return CriterionType.DATE_ISSUED;
      case SUBMITTED:
        return CriterionType.DATE_SUBMITTED;
      case EVENT_START:
        return CriterionType.DATE_EVENT_START;
      case EVENT_END:
        return CriterionType.DATE_EVENT_END;
      default:
        throw new TechnicalException("DateType is unknown. Cannot map.");
    }
  }

  private ArrayList<CriterionType> getCriterionsList() throws TechnicalException {
    final ArrayList<CriterionType> list = new ArrayList<CriterionType>();
    if (this.dateTypeList.size() == 0) {
      final List<DateType> dateList = new ArrayList<DateType>();
      dateList.add(DateType.ACCEPTED);
      dateList.add(DateType.CREATED);
      dateList.add(DateType.MODIFIED);
      dateList.add(DateType.PUBLISHED_ONLINE);
      dateList.add(DateType.PUBLISHED_PRINT);
      dateList.add(DateType.SUBMITTED);
      dateList.add(DateType.EVENT_START);
      dateList.add(DateType.EVENT_END);
      for (int i = 0; i < dateList.size(); i++) {
        list.add(this.getCriterionByDateType(dateList.get(i)));
      }
    } else {
      for (int i = 0; i < this.dateTypeList.size(); i++) {
        list.add(this.getCriterionByDateType(this.dateTypeList.get(i)));
      }
    }
    return list;
  }

  @Override
  public ArrayList<MetadataSearchCriterion> createSearchCriterion() throws TechnicalException {

    final ArrayList<MetadataSearchCriterion> criterions = new ArrayList<MetadataSearchCriterion>();

    String fromQuery = null;
    String toQuery = null;

    if (!this.isFromEmpty()) {
      fromQuery = this.from;
    }
    if (!this.isToEmpty()) {
      toQuery = this.to;
    }

    final MetadataDateSearchCriterion criterion =
        new MetadataDateSearchCriterion(this.getCriterionsList(), fromQuery, toQuery);
    criterions.add(criterion);

    return criterions;
  }
}
