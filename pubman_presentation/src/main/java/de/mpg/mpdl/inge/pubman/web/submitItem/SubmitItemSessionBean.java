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

package de.mpg.mpdl.inge.pubman.web.submitItem;

import de.mpg.mpdl.inge.pubman.web.util.FacesBean;

/**
 * Keeps all attributes that are used for the whole session by SubmitItem.
 * 
 * @author: Michael Franke, created 06.09.2007
 * @author: $Author$
 * @version: $Revision$ $LastChangedDate$
 */
@SuppressWarnings("serial")
public class SubmitItemSessionBean extends FacesBean {
  public static final String BEAN_NAME = "SubmitItemSessionBean";

  private String navigationStringToGoBack = null;
  private String message;

  public SubmitItemSessionBean() {}

  public final String getNavigationStringToGoBack() {
    return navigationStringToGoBack;
  }

  public final void setNavigationStringToGoBack(final String navigationStringToGoBack) {
    this.navigationStringToGoBack = navigationStringToGoBack;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
