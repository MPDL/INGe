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

import jakarta.faces.bean.ManagedBean;

import de.mpg.mpdl.inge.pubman.web.breadcrumb.BreadcrumbPage;

/**
 * BackingBean for DepositorWSPage.jsp.
 * 
 * @author: Thomas Diebäcker, created 10.01.2007
 * @version: $Revision$ $LastChangedDate$ Revised by DiT: 09.08.2007
 */
@ManagedBean(name = "DepositorWSPage")
@SuppressWarnings("serial")
public class DepositorWSPage extends BreadcrumbPage {
  public static final String MESSAGE_SUCCESSFULLY_SUBMITTED = "depositorWS_SuccessfullySubmitted";
  public static final String MESSAGE_SUCCESSFULLY_RELEASED = "depositorWS_SuccessfullyReleased";
  public static final String MESSAGE_SUCCESSFULLY_WITHDRAWN = "depositorWS_SuccessfullyWithdrawn";
  public static final String MESSAGE_SUCCESSFULLY_DELETED = "depositorWS_SuccessfullyDeleted";
  public static final String MESSAGE_SUCCESSFULLY_SAVED = "depositorWS_SuccessfullySaved";
  public static final String MESSAGE_SUCCESSFULLY_ACCEPTED = "depositorWS_SuccessfullyAccepted";
  public static final String MESSAGE_SUCCESSFULLY_REVISED = "depositorWS_SuccessfullyRevised";

  public static final String NO_WITHDRAWAL_COMMENT_GIVEN = "depositorWS_NoWithdrawalCommentGiven";

  public DepositorWSPage() {}

  @Override
  public void init() {
    this.checkForLogin();
    super.init();
  }

  @Override
  public boolean isItemSpecific() {
    return false;
  }
}
