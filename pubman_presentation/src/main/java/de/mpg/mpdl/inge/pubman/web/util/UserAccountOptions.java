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

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import de.escidoc.www.services.aa.UserAccountHandler;
import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.pubman.web.appbase.FacesBean;

/**
 * TODO Description
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@SuppressWarnings("serial")
public class UserAccountOptions extends FacesBean {
  public static final String BEAN_NAME = "UserAccountOptionsBean";

  private static final Logger logger = Logger.getLogger(FileLocatorUploadBean.class);

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
          SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
          formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
          String paramXml =
              "<param last-modification-date=\""
                  + formatter.format(getLoginHelper().getAccountUser().getLastModificationDate())
                  + "\"><password>" + this.getPassword() + "</password></param>";
          UserAccountHandler userAccountHandler =
              ServiceLocator.getUserAccountHandler(getLoginHelper().getAccountUser().getHandle());
          userAccountHandler.updatePassword(getLoginHelper().getAccountUser().getReference()
              .getObjectId(), paramXml);
          getLoginHelper().fetchAccountUser(getLoginHelper().getAccountUser().getHandle());
          info(getMessage("userAccountOptions_PasswordUpdated"));
        } else {
          error(getMessage("userAccountOptions_DifferentPasswords"));
        }
      } else {
        error(getMessage("userAccountOptions_emptyPassword"));
      }
    } catch (ServiceException e) {
      logger.error("Problem retrieving UserAcountHandler", e);
    } catch (URISyntaxException e) {
      logger.error("Problem retrieving UserAcountHandler", e);
    } catch (Exception e) {
      logger.error("Problem updating Password", e);
    }

    return "";
  }
}
