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

package de.mpg.escidoc.pubman.breadcrumb;

import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.Internationalized;
import de.mpg.escidoc.pubman.appbase.InternationalizedImpl;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;

/**
 * Class for single breadcrumbs. Each breadcrumb is represented with this class.
 * 
 * @author: Tobias Schraut, created 30.05.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Di, 20 Nov 2007) $ Revised by ScT: 16.08.2007
 */
public class BreadcrumbItem extends InternationalizedImpl
{

	// The String that should be displayed in the breadcrumb menu, e.g. "ViewItem"
    private String displayValue;
    
    // The jsp page that should be addressed when the link in the breadcrumb
    // navigation is clicked, e.g. "ViewItem.jsp"
    private String page;
    
    // Flag indicating that this item is the last one.
    private boolean isLast = false;

    /**
     * Default constructor.
     */
    public BreadcrumbItem()
    {
    }

    /**
     * Public constructor(with two parameters, the value to display and the page name that should be displayed).
     * You may only use one of the public static final BreadcrumbItem's defined above.
     */
    public BreadcrumbItem(String displayValue, String page)
    {
        this.displayValue = displayValue;
        this.page = page;
    }

    /**
     * Internationalization is supported by this getter.
     * @return displayValue to label this BreadcrumbItem
     */
    public String getDisplayValue()
    {
        return getLabel(displayValue);
    }

    public void setDisplayValue(String displayValue)
    {
        this.displayValue = displayValue;
    }

    public String getPage()
    {
        return page;
    }

    public void setPage(String page)
    {
        this.page = page;
    }

    @Override
    public String toString()
    {
        return "[" + displayValue + "]";
    }

    @Override
    public boolean equals(final Object other)
    {
        if (page == null || !(other instanceof BreadcrumbItem))
        {
            return false;
        }

        return (page.equals(((BreadcrumbItem) other).getPage()));
    }

	public boolean getIsLast() {
		return isLast;
	}

	public void setIsLast(boolean isLast) {
		this.isLast = isLast;
	}
}