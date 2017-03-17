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

package de.mpg.mpdl.inge.pubman.web.appbase;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.pubman.web.desktop.Login;

/**
 * TODO Description
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class PubManSessionListener implements HttpSessionListener {
  private static final Logger logger = Logger.getLogger(PubManSessionListener.class);

  // public static final String LOGOUT_URL = "/aa/logout/clear.jsp";

  @Override
  public void sessionCreated(HttpSessionEvent arg0) {
    // Do nothing here
  }

  @Override
  public void sessionDestroyed(HttpSessionEvent event) {
    logger.debug("Session timed out.");
    Login login = (Login) event.getSession().getAttribute(Login.BEAN_NAME);
    if (login != null) {
      try {
        login.logout();
      } catch (Exception e) {
        // Suppress stacktrace
        logger.warn("Error logging out user: " + e.getMessage());
      }
    }
  }
}
