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

import javax.faces.bean.ManagedBean;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.util.PropertyReader;

@ManagedBean(name = "PubManRequestBean")
@SuppressWarnings("serial")
public class PubManRequestBean extends FacesBean {
  private static final Logger logger = Logger.getLogger(PubManRequestBean.class);

  private String helpAnchor = "";
  private String requestedPage = "";

  public PubManRequestBean() {
    this.init();
  }

  public void init() {
    if (FacesTools.getExternalContext().getRequestPathInfo() != null) {
      this.helpAnchor = FacesTools.getExternalContext().getRequestPathInfo().replace("/", "");
      this.requestedPage = this.helpAnchor.replaceAll(".jsp", "");
      this.helpAnchor = "#" + this.helpAnchor.replaceAll(".jsp", "");
    }
  }

  public String getHelpAnchor() {
    return this.helpAnchor;
  }

  public void setHelpAnchor(String helpAnchor) {
    this.helpAnchor = helpAnchor;
  }

  public String getRequestedPage() {
    return this.requestedPage;
  }

  public void setRequestedPage(String requestedPage) {
    this.requestedPage = requestedPage;
  }

  /**
   * Reads the policy URL from the properties file.
   * 
   * @return policyUrl as String
   */
  public String getPolicyUrl() {
    String url = "";
    try {
      url = PropertyReader.getProperty("inge.pubman.policy.url");
    } catch (final Exception e) {
      PubManRequestBean.logger.error("Could not read property: 'inge.pubman.policy.url' from properties file.", e);
    }

    return url;
  }


  /**
   * Reads the contact URL from the properties file.
   * 
   * @return contactUrl as String
   */
  public String getContactUrl() {
    String url = "";
    try {
      url = PropertyReader.getProperty("inge.pubman.contact.url");
    } catch (final Exception e) {
      PubManRequestBean.logger.error("Could not read property: 'inge.pubman.contact.url' from properties file.", e);
    }

    return url;
  }
}
