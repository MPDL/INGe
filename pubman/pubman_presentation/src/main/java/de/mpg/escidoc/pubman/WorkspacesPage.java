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
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */

package de.mpg.escidoc.pubman;

import java.io.IOException;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.BreadcrumbPage;
import de.mpg.escidoc.pubman.viewItem.ViewItemSessionBean;


/**
 * BackingBean for Workspaces Page (WorkspacesPage.jsp).
 *
 */
public class WorkspacesPage extends BreadcrumbPage
{
	private static Logger logger = Logger.getLogger(WorkspacesPage.class);
	public static final String BEAN_NAME = "WorkspacesPage";
	// The referring GUI Tool Page
	public static final String GT_WORKSPACES_PAGE = "GTWorkspacesPage.jsp";



	/**
	 * Public constructor.
	 */
	public WorkspacesPage()
	{
		this.init();
	}

	/**
	 * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
	 * a URL, or indirectly via page navigation.
	 */
	@Override
	public void init()
	{
		super.init();
		checkForLogin();

	}

	/**
	 * Redirets to the referring GUI Tool page.
	 * 
	 * @return a navigation string
	 */
	protected String redirectToGUITool()
	{
		FacesContext fc = FacesContext.getCurrentInstance();
		try
		{
			fc.getExternalContext().redirect(GT_WORKSPACES_PAGE);
		}
		catch (IOException e)
		{
			logger.error("Could not redirect to GUI Tool View item page." + "\n" + e.toString());
		}
		return "";
	}


	/**
	 * Returns the ViewItemSessionBean.
	 * 
	 * @return a reference to the scoped data bean (ViewItemSessionBean)
	 */
	protected ViewItemSessionBean getViewItemSessionBean()
	{
		return (ViewItemSessionBean)getSessionBean(ViewItemSessionBean.class);
	}


	@Override
	public boolean isItemSpecific()
	{
		return false;
	}

}
