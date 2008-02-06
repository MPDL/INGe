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

package de.mpg.escidoc.pubman.breadCrumb;

import java.util.ArrayList;
import java.util.List;

/**
 * BreadCrumbItem history is stored in this session bean for advanced page navigation.
 * @author Mario Wagner
 * @version: 
 */
public class BreadCrumbItemHistorySessionBean 
{
	public static final String BEAN_NAME = "breadCrumb$BreadCrumbItemHistorySessionBean";
	
	// the List of BreadCrumbs representing JSP's that have been viewed
	private List<BreadCrumbItem> breadcrumbs = new ArrayList<BreadCrumbItem>();
	
	
	/**
	 * Initializes this BreadCrumbItemHistory
	 */
	public void clear()
	{
		breadcrumbs.clear();
	}
	
	/**
	 * Register will be done smart: if the BreadCrumbItem is already registered, the old 
	 * BreadCrumbItem will be replaced AND all following BreadCrumbItem are deleted !!!
	 * @param newItem BreadCrumbItem to be added to the history
	 */
	public void push(BreadCrumbItem newItem)
	{
		BreadCrumbItem lastItem = null;
		int index = breadcrumbs.size() - 1;
		if (index >= 0)
		{
			boolean remove = false;
			int position = 0;

			for (int i = 0; i < breadcrumbs.size(); i++)
			{
				lastItem = (BreadCrumbItem) breadcrumbs.get(i);
				if (lastItem.equals(newItem))
				{
					// replaces the actual item
					remove = true;
					position = i;
					break;
				}
			}
			if (remove)
			{
				for (int i = breadcrumbs.size()-1; i >= position; i--)
				{
					breadcrumbs.remove(i);
				}
			}
		}
		breadcrumbs.add(newItem);
	}
	
	/**
	 * get and remove the last BreadCrumbItem from history
	 * @return BreadCrumbItem
	 */
	public BreadCrumbItem pop()
	{ 
		return get(true);
	}

	/**
	 * get the last BreadCrumbItem from history
	 * @return BreadCrumbItem
	 */
	public BreadCrumbItem get()
	{
		return get(false);
	}

	
	private BreadCrumbItem get(boolean remove)
	{
		BreadCrumbItem returnItem = null;
		int index = breadcrumbs.size() - 1;
		if (index >= 0)
		{
			returnItem = breadcrumbs.get(index);
			if (remove)
			{
				breadcrumbs.remove(index);
			}
		}
		return returnItem;
	}
    
	// //////////////////////////////////////////////////////////////////////////
	//
	// Foo's and Boo's
	//
	// //////////////////////////////////////////////////////////////////////////

	public List<BreadCrumbItem> getBreadCrumbItemHistory()
	{
		return breadcrumbs;
	}

	public void setBreadCrumbItemHistory(List<BreadCrumbItem> breadcrumbs)
	{
		this.breadcrumbs = breadcrumbs;
	}

}
