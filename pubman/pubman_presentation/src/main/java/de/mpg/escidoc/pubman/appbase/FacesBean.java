/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.appbase;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * The FacesBean provides common features for bean and facesMessage handling.
 * Designed to replace inheritance from AbstractFragmentBean and others.
 * 
 * @author Mario Wagner
 * @version
 */
public class FacesBean
{
	/**
	 * Return the <code>Application</code> instance for the current web application.
	 * @return <code>Application</code>
	 */
	public static Application getApplication()
	{
		ApplicationFactory factory = (ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
		return factory.getApplication();
	}

	/**
	 * Return the <code>FacesContext</code> instance for the current request.
	 * @return <code>FacesContext</code>
	 */
	public static FacesContext getFacesContext()
	{
		return FacesContext.getCurrentInstance();
	}

	/**
	 * Return the <code>ExternalContext</code> instance for the current request.
	 * @return <code>ExternalContext</code>
	 */
	public static ExternalContext getExternalContext()
	{
		return getFacesContext().getExternalContext();
	}

	/**
	 * Return any bean stored in request, session or application scope under the specified name.
	 * @return the actual or new bean instance
	 */
	public static synchronized Object getBean(String name)
	{
		return getApplication().createValueBinding("#{" + name + "}").getValue(getFacesContext());
	}

	
	/**
	 * Enqueue a global <code>FacesMessage</code> (not associated with any particular componen) containing 
	 * the specified summary text and a message severity level of <code>FacesMessage.SEVERITY_ERROR</code>.
	 * @param summary summary text
	 */
	public static void error(String summary)
	{
		FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, null);
		getFacesContext().addMessage(null, fm);
	}
	
	/**
	 * Enqueue a <code>FacesMessage</code> associated with the specified component, containing the 
	 * specified summary text and a message severity level of <code>FacesMessage.SEVERITY_ERROR</code>.
	 * @param component associated <code>UIComponent</code>
	 * @param summary summary text
	 */
	public static void error(UIComponent component, String summary)
	{
		FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, null);
		getFacesContext().addMessage(component.getId(), fm);
	}

	/**
	 * Enqueue a global <code>FacesMessage</code> (not associated with any particular componen) containing 
	 * the specified summary text and a message severity level of <code>FacesMessage.SEVERITY_FATAL</code>.
	 * @param summary summary text
	 */
	public static void fatal(String summary)
	{
		FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_FATAL, summary, null);
		getFacesContext().addMessage(null, fm);
	}
	
	/**
	 * Enqueue a <code>FacesMessage</code> associated with the specified component, containing the 
	 * specified summary text and a message severity level of <code>FacesMessage.SEVERITY_FATAL</code>.
	 * @param component associated <code>UIComponent</code>
	 * @param summary summary text
	 */
	public static void fatal(UIComponent component, String summary)
	{
		FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_FATAL, summary, null);
		getFacesContext().addMessage(component.getId(), fm);
	}

	/**
	 * Enqueue a global <code>FacesMessage</code> (not associated with any particular componen) containing 
	 * the specified summary text and a message severity level of <code>FacesMessage.SEVERITY_INFO</code>.
	 * @param summary summary text
	 */
	public static void info(String summary)
	{
		FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null);
		getFacesContext().addMessage(null, fm);
	}
	
	/**
	 * Enqueue a <code>FacesMessage</code> associated with the specified component, containing the 
	 * specified summary text and a message severity level of <code>FacesMessage.SEVERITY_INFO</code>.
	 * @param component associated <code>UIComponent</code>
	 * @param summary summary text
	 */
	public static void info(UIComponent component, String summary)
	{
		FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null);
		getFacesContext().addMessage(component.getId(), fm);
	}

	/**
	 * Enqueue a global <code>FacesMessage</code> (not associated with any particular componen) containing 
	 * the specified summary text and a message severity level of <code>FacesMessage.SEVERITY_WARN</code>.
	 * @param summary summary text
	 */
	public static void warn(String summary)
	{
		FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_WARN, summary, null);
		getFacesContext().addMessage(null, fm);
	}
	
	/**
	 * Enqueue a <code>FacesMessage</code> associated with the specified component, containing the 
	 * specified summary text and a message severity level of <code>FacesMessage.SEVERITY_WARN</code>.
	 * @param component associated <code>UIComponent</code>
	 * @param summary summary text
	 */
	public static void warn(UIComponent component, String summary)
	{
		FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_WARN, summary, null);
		getFacesContext().addMessage(component.getId(), fm);
	}

}