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

package de.mpg.mpdl.inge.pubman.web.desktop;

import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.util.PropertyReader;
import jakarta.faces.bean.ManagedBean;

/**
 * Fragment class for the corresponding Header-JSP.
 *
 * @author: Thomas Diebäcker, created 24.01.2007
 * @version: $Revision$ $LastChangedDate$ Revised by DiT: 14.08.2007
 */
@ManagedBean(name = "Header")
@SuppressWarnings("serial")
public class Header extends FacesBean {
  private static final String LOGO_DEV = "overlayDev";
  private static final String LOGO_QA = "overlayQA";
  private static final String LOGO_TEST = "overlayTest";

  private String type;

  public Header() {}

  /**
   * Getter for the logo definition f the type of the server. E.g a dev server gets another logo
   * than an demo server.
   *
   * @return logo definition
   */
  public String getServerLogo() {
    String serverLogo = "";
    try {
      if (this.getType().equals("dev")) {
        serverLogo = Header.LOGO_DEV;
      } else if (this.getType().equals("test")) {
        serverLogo = Header.LOGO_TEST;
      } else if (this.getType().equals("qa")) {
        serverLogo = Header.LOGO_QA;
      }
    } catch (final Exception e) {
    }

    return serverLogo;
  }

  /**
   * Get instance type property. Return an empty string if it is not defined.
   *
   * @return A string representing the instance type, e.g. "dev", "qa".
   */
  public String getType() {
    if (this.type == null) {
      this.type = PropertyReader.getProperty(PropertyReader.INGE_SYSTEMTYPE);
      if (this.type == null) {
        this.type = "";
      }
    }

    return this.type;
  }
}
