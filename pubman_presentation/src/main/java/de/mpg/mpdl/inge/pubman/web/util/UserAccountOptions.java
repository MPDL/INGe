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

package de.mpg.mpdl.inge.pubman.web.util;

import javax.faces.bean.ManagedBean;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.beans.LoginHelper;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;


/**
 * TODO Description
 *
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
@ManagedBean(name = "UserAccountOptions")
public class UserAccountOptions extends FacesBean {
  private static final long serialVersionUID = 1L;
  public static final String BEAN_NAME = "UserAccountOptionsBean";
  private Logger logger = Logger.getLogger(UserAccountOptions.class);
  private LoginHelper loginHelper;

  private String password;
  private String secondPassword;


  public String getPassword() {
    return this.password;
  }

  public void setPassword(String newPassword) {
    this.password = newPassword.trim();
  }

  public String getSecondPassword() {
    return this.secondPassword;
  }

  public void setSecondPassword(String newSecondPassword) {
    this.secondPassword = newSecondPassword.trim();
  }

  public String updatePassword() {
    try {
      if (this.password != null && !("").equals(this.password.trim())) {
        if (this.password.equals(this.secondPassword)) {
          this.loginHelper = (LoginHelper) FacesTools.findBean("LoginHelper");
          UserAccountService userAccountService = ApplicationBean.INSTANCE.getUserAccountService();
          userAccountService.changePassword(this.loginHelper.getAccountUser().getObjectId(),
              this.loginHelper.getAccountUser().getLastModificationDate(), this.password, this.loginHelper.getAuthenticationToken());
          info(getMessage("userAccountOptions_PasswordUpdated"));
        } else {
          error(getMessage("userAccountOptions_DifferentPasswords"));
        }
      } else {
        error(getMessage("userAccountOptions_emptyPassword"));
      }
    } catch (Exception e) {
      error(e.getMessage());
      logger.error("Problem updating Password", e);
    }

    return "";
  }

}
