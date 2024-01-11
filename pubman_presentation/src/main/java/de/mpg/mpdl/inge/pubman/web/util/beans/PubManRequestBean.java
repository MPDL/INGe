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

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.util.PropertyReader;
import jakarta.faces.bean.ManagedBean;

@ManagedBean(name = "PubManRequestBean")
@SuppressWarnings("serial")
public class PubManRequestBean extends FacesBean {
  private static final Logger logger = Logger.getLogger(PubManRequestBean.class);

  public PubManRequestBean() {}

  /**
   * Reads the policy URL from the properties file.
   * 
   * @return policyUrl as String
   */
  public String getPolicyUrl() {
    String url = "";
    try {
      url = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_POLICY_URL);
    } catch (final Exception e) {
      PubManRequestBean.logger.error("Could not read property: '" + PropertyReader.INGE_PUBMAN_POLICY_URL + "' from properties file.", e);
    }

    return url;
  }

  /**
   * Reads the policy URL from the properties file.
   * 
   * @return policyUrl as String
   */
  public String getPrivacyPolicyUrl() {
    String url = "";
    try {
      url = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_PRIVACY_URL);
    } catch (Exception e) {
      logger.error("Could not read property: '" + PropertyReader.INGE_PUBMAN_PRIVACY_URL + "' from properties file.", e);
    }

    return url;
  }


  /**
   * Reads the help URL from the properties file.
   * 
   * @return handbookUrl as String
   */
  public String getHelpUrl() {
    String url = "";
    try {
      url = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_HELP_URL);
    } catch (final Exception e) {
      PubManRequestBean.logger.error("Could not read property: '" + PropertyReader.INGE_PUBMAN_HELP_URL + "' from properties file.", e);
    }

    return url;
  }
}
