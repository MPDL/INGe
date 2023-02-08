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

package de.mpg.mpdl.inge.pubman.web;

import java.util.Map;

import jakarta.faces.bean.ManagedBean;

import de.mpg.mpdl.inge.pubman.web.breadcrumb.BreadcrumbPage;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;

@ManagedBean(name = "SearchResultListPage")
@SuppressWarnings("serial")
public class SearchResultListPage extends BreadcrumbPage {
  public SearchResultListPage() {}

  @Override
  public void init() {
    super.init();
  }

  @Override
  public boolean isItemSpecific() {
    return false;
  }

  public String rest() {
    Map<String, String> params = FacesTools.getExternalContext().getRequestParameterMap();
    String query = params.get("query");
    ((SearchAndExportPage) FacesTools.findBean("SearchAndExportPage")).setEsQuery(query);

    return "SearchAndExportPage.jsp";
  }
}
