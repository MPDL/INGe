/*
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

package de.mpg.mpdl.inge.pubman.web.search;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion;

/**
 * Keeps all attributes that are used for the whole session by the SearchResultList.
 * 
 * @author: Thomas Diebäcker; Tobias Schraut, created 10.01.2007
 * @version: $Revision$ $LastChangedDate$ Revised by DiT: 14.08.2007
 */
@ManagedBean(name = "SearchResultListSessionBean")
@SessionScoped
@SuppressWarnings("serial")
public class SearchResultListSessionBean extends FacesBean {
  public enum SearchType {
    NORMAL_SEARCH, ADVANCED_SEARCH, AFFILIATION_SEARCH;
  }
  
  private AffiliationVO affiliation;
  private ArrayList<MetadataSearchCriterion> criteria;
  private SearchType type;
  private String searchString;
  private boolean includeFiles = false;
  
  public SearchResultListSessionBean() {}

  public String getSearchString() {
    return this.searchString;
  }

  public void setSearchString(String searchString) {
    this.searchString = searchString;
  }

  public boolean getIncludeFiles() {
    return this.includeFiles;
  }

  public void setIncludeFiles(boolean includeFiles) {
    this.includeFiles = includeFiles;
  }

  public SearchType getType() {
    return this.type;
  }

  public void setType(SearchType type) {
    this.type = type;
  }

  public ArrayList<MetadataSearchCriterion> getSearchCriteria() {
    return this.criteria;
  }

  public void setSearchCriteria(ArrayList<MetadataSearchCriterion> criteria) {
    this.criteria = criteria;
  }

  public AffiliationVO getAffiliation() {
    return this.affiliation;
  }

  public void setAffiliation(AffiliationVO affiliation) {
    this.affiliation = affiliation;
  }
}
