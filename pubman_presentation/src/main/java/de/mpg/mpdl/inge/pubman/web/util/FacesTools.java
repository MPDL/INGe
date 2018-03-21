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

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FacesTools {
  @SuppressWarnings("unchecked")
  public static <T> T findBean(String beanName) {
    final FacesContext context = FacesContext.getCurrentInstance();

    return (T) context.getApplication().evaluateExpressionGet(context, "#{" + beanName + "}", Object.class);
  }

  public static FacesContext getCurrentInstance() {
    return FacesContext.getCurrentInstance();
  }

  public static ExternalContext getExternalContext() {
    return FacesContext.getCurrentInstance().getExternalContext();
  }

  public static HttpServletRequest getRequest() {
    return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
  }

  public static HttpServletResponse getResponse() {
    return (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
  }

  public static UIComponent findComponent(final String id) {
    FacesContext context = FacesContext.getCurrentInstance();
    UIViewRoot root = context.getViewRoot();
    final UIComponent[] found = new UIComponent[1];

    root.visitTree(VisitContext.createVisitContext(context), new VisitCallback() {
      @Override
      public VisitResult visit(VisitContext context, UIComponent component) {
        if (component.getId().equals(id)) {
          found[0] = component;
          return VisitResult.COMPLETE;
        }
        return VisitResult.ACCEPT;
      }
    });

    return found[0];
  }

}
