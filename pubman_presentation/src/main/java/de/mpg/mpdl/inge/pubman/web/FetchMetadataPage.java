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

import java.lang.reflect.Method;

import jakarta.faces.bean.ManagedBean;

import de.mpg.mpdl.inge.pubman.web.breadcrumb.BreadcrumbPage;
import de.mpg.mpdl.inge.pubman.web.easySubmission.EasySubmission;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;

/**
 * BackingBean for EasySubmissionPage.jsp, Step3Import.
 * 
 * @author: mfranke
 * @author: $Author: mfranke$
 * @version: $Revision: 4295 $ $LastChangedDate: 2011-03-14 16:32:34 +0100 (Mo, 14 Mrz 2011) $
 */
@ManagedBean(name = "FetchMetadataPage")
@SuppressWarnings("serial")
public class FetchMetadataPage extends BreadcrumbPage {
  public FetchMetadataPage() {}

  @Override
  public void init() {
    this.checkForLogin();
    super.init();
  }

  @Override
  protected Method getDefaultAction() throws NoSuchMethodException {
    final EasySubmission easySubmission = (EasySubmission) FacesTools.findBean("EasySubmission");
    return easySubmission.getClass().getMethod("newImport", null);
  }

  @Override
  public boolean isItemSpecific() {
    return true;
  }
}
