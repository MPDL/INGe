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

package de.mpg.mpdl.inge.pubman.web;

import javax.faces.bean.ManagedBean;

import de.mpg.mpdl.inge.pubman.web.breadcrumb.BreadcrumbPage;

/**
 * Fragment class for the AffiliationSearchResultListPage. This class provides all functionality for
 * choosing and viewing one or more items out of a list of SearchResults.
 * 
 * @author: Tobias Schraut, created 14.08.2007
 * @version: $Revision$ $LastChangedDate$ Revised by NiH: 13.09.2007
 */
@ManagedBean(name = "AffiliationSearchResultListPage")
@SuppressWarnings("serial")
public class AffiliationSearchResultListPage extends BreadcrumbPage {
  public AffiliationSearchResultListPage() {
    this.init();
  }

  /**
   * Callback method that is called whenever a page is navigated to, either directly via a URL, or
   * indirectly via page navigation. Customize this method to acquire resources that will be needed
   * for event handlers and lifecycle methods, whether or not this page is performing post back
   * processing. Note that, if the current request is a postback, the property values of the
   * components do <strong>not</strong> represent any values submitted with this request. Instead,
   * they represent the property values that were saved for this view when it was rendered.
   */
  public void init() {
    super.init();
  }

  @Override
  public boolean isItemSpecific() {
    return false;
  }
}
