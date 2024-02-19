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
package de.mpg.mpdl.inge.pubman.web.util.beans;

import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.bean.SessionScoped;
import jakarta.faces.event.ValueChangeEvent;

@ManagedBean(name = "PubManSessionBean")
@SessionScoped
@SuppressWarnings("serial")
public class PubManSessionBean extends FacesBean {
  private String locale = this.getI18nHelper().getLocale();

  public PubManSessionBean() {}

  public void changeLanguage(ValueChangeEvent event) {
    if (null != event) {
      this.getI18nHelper().changeLanguage(event);
    }

    this.locale = this.getI18nHelper().getLocale();
  }

  public String getLocaleString() {
    return this.getLabel("ENUM_LANGUAGE_" + this.locale.toUpperCase());
  }

  public String getLocale() {
    return this.locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

  public boolean isLoggedIn() {
    return this.getLoginHelper().getLoggedIn();
  }
}
