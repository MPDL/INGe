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

import javax.faces.bean.ManagedBean;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.pubman.web.breadcrumb.BreadcrumbPage;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.LoginHelper;

/**
 * BackingBean for Quality Assurance Page (QAWSPage.jsp).
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@ManagedBean(name = "QAWSPage")
@SuppressWarnings("serial")
public class QAWSPage extends BreadcrumbPage {
  // public static final String BEAN_NAME = "QAWSPage";

  private static Logger logger = Logger.getLogger(QAWSPage.class);

  public QAWSPage() {
    this.init();
  }

  /**
   * Callback method that is called whenever a page containing this page fragment is navigated to,
   * either directly via a URL, or indirectly via page navigation.
   */
  @Override
  public void init() {
    super.init();

    String userHandle = FacesTools.getRequest().getParameter(LoginHelper.PARAMETERNAME_USERHANDLE);

    if (logger.isDebugEnabled()) {
      logger.debug("UserHandle: " + userHandle);
    }

    try {
      checkForLogin();
    } catch (Exception e) {
      logger.error("Could not login." + "\n" + e.toString());
    }
  }

  public boolean getIsModerator() {
    boolean isModerator = false;

    if (getLoginHelper().isLoggedIn()) {
      isModerator = getLoginHelper().getAccountUser().isModerator();
    }

    return isModerator;
  }

  @Override
  public boolean isItemSpecific() {
    return false;
  }
}
