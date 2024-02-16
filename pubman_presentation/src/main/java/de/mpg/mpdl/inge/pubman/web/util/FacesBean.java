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

import java.io.Serializable;
import java.util.Iterator;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.pubman.web.util.beans.InternationalizationHelper;
import de.mpg.mpdl.inge.pubman.web.util.beans.LoginHelper;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.application.FacesMessage.Severity;
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.component.UIComponent;

/**
 * The FacesBean provides common features for bean and facesMessage handling. Designed to replace
 * inheritance from FacesBean and others.
 *
 * @author Mario Wagner
 * @version
 */
@ManagedBean(name = "FacesBean")
@SuppressWarnings("serial")
public class FacesBean implements Serializable {
  private static final Logger logger = Logger.getLogger(FacesBean.class);

  private final InternationalizationHelper i18nHelper;

  public FacesBean() {
    this.i18nHelper = FacesTools.findBean("InternationalizationHelper");
  }

  /**
   * Enqueue a global <code>FacesMessage</code> (not associated with any particular component)
   * containing the specified summary text and a message severity level of
   * <code>FacesMessage.SEVERITY_ERROR</code>.
   *
   * @param summary summary text
   */
  public void info(String summary) {
    this.info(summary, null, null);
  }

  /**
   * Enqueue a global <code>FacesMessage</code> (not associated with any particular component)
   * containing the specified summary text, a detailed description and a message severity level of
   * <code>FacesMessage.SEVERITY_ERROR</code>.
   *
   * @param summary summary text
   */
  public void info(String summary, String detail) {
    this.info(summary, detail, null);
  }

  /**
   * Enqueue a <code>FacesMessage</code> associated with the specified component, containing the
   * specified summary text and a message severity level of <code>FacesMessage.SEVERITY_ERROR</code>
   * .
   *
   * @param component associated <code>UIComponent</code>
   * @param summary summary text
   */
  public void info(UIComponent component, String summary) {
    this.info(summary, null, component);
  }

  /**
   * Enqueue a global <code>FacesMessage</code> (not associated with any particular component)
   * containing the specified summary text, a detailed description and a message severity level of
   * <code>FacesMessage.SEVERITY_ERROR</code>.
   *
   * @param summary summary text
   */
  public void info(String summary, String detail, UIComponent component) {
    this.message(summary, detail, component, FacesMessage.SEVERITY_INFO);
  }

  /**
   * Enqueue a global <code>FacesMessage</code> (not associated with any particular component)
   * containing the specified summary text and a message severity level of
   * <code>FacesMessage.SEVERITY_ERROR</code>.
   *
   * @param summary summary text
   */
  public void warn(String summary) {
    this.warn(summary, null, null);
  }

  /**
   * Enqueue a global <code>FacesMessage</code> (not associated with any particular component)
   * containing the specified summary text, a detailed description and a message severity level of
   * <code>FacesMessage.SEVERITY_ERROR</code>.
   *
   * @param summary summary text
   */
  public void warn(String summary, String detail) {
    this.warn(summary, detail, null);
  }

  /**
   * Enqueue a <code>FacesMessage</code> associated with the specified component, containing the
   * specified summary text and a message severity level of <code>FacesMessage.SEVERITY_ERROR</code>
   * .
   *
   * @param component associated <code>UIComponent</code>
   * @param summary summary text
   */
  public void warn(UIComponent component, String summary) {
    this.warn(summary, null, component);
  }

  /**
   * Enqueue a global <code>FacesMessage</code> (not associated with any particular component)
   * containing the specified summary text, a detailed description and a message severity level of
   * <code>FacesMessage.SEVERITY_ERROR</code>.
   *
   * @param summary summary text
   */
  public void warn(String summary, String detail, UIComponent component) {
    this.message(summary, detail, component, FacesMessage.SEVERITY_WARN);
  }

  /**
   * Enqueue a global <code>FacesMessage</code> (not associated with any particular component)
   * containing the specified summary text and a message severity level of
   * <code>FacesMessage.SEVERITY_ERROR</code>.
   *
   * @param summary summary text
   */
  public void error(String summary) {
    this.error(summary, null, null);
  }

  /**
   * Enqueue a global <code>FacesMessage</code> (not associated with any particular component)
   * containing the specified summary text, a detailed description and a message severity level of
   * <code>FacesMessage.SEVERITY_ERROR</code>.
   *
   * @param summary summary text
   */
  public void error(String summary, String detail) {
    this.error(summary, detail, null);
  }

  /**
   * Enqueue a <code>FacesMessage</code> associated with the specified component, containing the
   * specified summary text and a message severity level of <code>FacesMessage.SEVERITY_ERROR</code>
   * .
   *
   * @param component associated <code>UIComponent</code>
   * @param summary summary text
   */
  public void error(UIComponent component, String summary) {
    this.error(summary, null, component);
  }

  /**
   * Enqueue a global <code>FacesMessage</code> (not associated with any particular component)
   * containing the specified summary text, a detailed description and a message severity level of
   * <code>FacesMessage.SEVERITY_ERROR</code>.
   *
   * @param summary summary text
   */
  public void error(String summary, String detail, UIComponent component) {
    this.message(summary, detail, component, FacesMessage.SEVERITY_ERROR);
  }

  /**
   * Enqueue a global <code>FacesMessage</code> (not associated with any particular component)
   * containing the specified summary text and a message severity level of
   * <code>FacesMessage.SEVERITY_ERROR</code>.
   *
   * @param summary summary text
   */
  public void fatal(String summary) {
    this.fatal(summary, null, null);
  }

  /**
   * Enqueue a global <code>FacesMessage</code> (not associated with any particular component)
   * containing the specified summary text, a detailed description and a message severity level of
   * <code>FacesMessage.SEVERITY_ERROR</code>.
   *
   * @param summary summary text
   */
  public void fatal(String summary, String detail) {
    this.fatal(summary, detail, null);
  }

  /**
   * Enqueue a <code>FacesMessage</code> associated with the specified component, containing the
   * specified summary text and a message severity level of <code>FacesMessage.SEVERITY_ERROR</code>
   * .
   *
   * @param component associated <code>UIComponent</code>
   * @param summary summary text
   */
  public void fatal(UIComponent component, String summary) {
    this.fatal(summary, null, component);
  }

  /**
   * Enqueue a global <code>FacesMessage</code> (not associated with any particular component)
   * containing the specified summary text, a detailed description and a message severity level of
   * <code>FacesMessage.SEVERITY_ERROR</code>.
   *
   * @param summary summary text
   */
  public void fatal(String summary, String detail, UIComponent component) {
    this.message(summary, detail, component, FacesMessage.SEVERITY_FATAL);
  }

  /**
   * Enqueue a global <code>FacesMessage</code> (not associated with any particular component)
   * containing the specified summary text, a detailed description and a message severity level of
   * <code>FacesMessage.SEVERITY_ERROR</code>.
   *
   * @param summary summary text
   */
  public void message(String summary, String detail, UIComponent component, Severity severity) {
    final FacesMessage fm = new FacesMessage(severity, summary, StringEscapeUtils.escapeHtml4(detail));

    if (component == null) {
      FacesTools.getCurrentInstance().addMessage(null, fm);
    } else {
      FacesTools.getCurrentInstance().addMessage(component.getId(), fm);
    }
  }

  public boolean getHasMessages() {
    return FacesTools.getCurrentInstance().getMessages().hasNext();
  }

  public boolean getHasErrorMessages() {
    for (final Iterator<FacesMessage> i = FacesTools.getCurrentInstance().getMessages(); i.hasNext();) {
      final FacesMessage fm = i.next();

      FacesBean.logger.info("Message (" + fm.getSeverity() + "): " + fm.getSummary() + ":\n" + fm.getDetail());

      if (fm.getSeverity().equals(FacesMessage.SEVERITY_ERROR) || fm.getSeverity().equals(FacesMessage.SEVERITY_WARN)
          || fm.getSeverity().equals(FacesMessage.SEVERITY_FATAL)) {
        return true;
      }
    }

    return false;
  }

  public int getNumberOfMessages() {
    int number = 0;

    for (final Iterator<FacesMessage> i = FacesTools.getCurrentInstance().getMessages(); i.hasNext();) {
      i.next();
      number++;
    }

    return number;
  }

  public void checkForLogin() {
    if (!this.getLoginHelper().isLoggedIn()) {
      this.info(this.getMessage("NotLoggedIn"));
    }
  }

  public LoginHelper getLoginHelper() {
    return FacesTools.findBean("LoginHelper");
  }

  public InternationalizationHelper getI18nHelper() {
    return this.i18nHelper;
  }

  public String getMessage(String placeholder) {
    return this.getI18nHelper().getMessage(placeholder);
  }

  public String getLabel(String placeholder) {
    return this.getI18nHelper().getLabel(placeholder);
  }
}
