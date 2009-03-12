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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;

/**
 * BreadcrumbItem history is stored in this session bean for advanced page navigation.
 *
 * @author Mario Wagner
 * @version:
 */
public class BreadcrumbItemHistorySessionBean extends FacesBean
{
    public static final String BEAN_NAME = "BreadcrumbItemHistorySessionBean";
    // the List of BreadCrumbs representing JSP's that have been viewed
    private List<BreadcrumbItem> breadcrumbs = new ArrayList<BreadcrumbItem>();

    private static Logger logger = Logger.getLogger(BreadcrumbItemHistorySessionBean.class);

    /**
     * Initializes this BreadcrumbItemHistory.
     */
    public void clear()
    {
        logger.debug("Clearing BC");
        breadcrumbs.clear();
    }

    /**
     * Register will be done smart: if the BreadcrumbItem is already registered,
     * the old BreadcrumbItem will be replaced.
     * AND all following BreadcrumbItem are deleted !!!
     *
     * @param newItem BreadcrumbItem to be added to the history
     */
    public void push(final BreadcrumbItem newItem)
    {
    	
    	if ("HomePage".equals(newItem.getPageLabel()))
    	{
    		breadcrumbs.clear();
    	}
    	
        BreadcrumbItem lastItem = null;
        boolean keepold = false;
        if (breadcrumbs.size() >= 1)
        {
            boolean remove = false;
            
            int position = 0;
            for (int i = 0; i < breadcrumbs.size(); i++)
            {
               lastItem = (BreadcrumbItem) breadcrumbs.get(i);
                
                lastItem.setIsLast(false);
                
                if (lastItem.equals(newItem))
                {
                    // replaces the actual item
                    remove = true;
                    position = i;
                    
                    //in particular for ViewItemFullPage, when an ID is added to the URL
                    keepold = lastItem.getPage().startsWith(newItem.getPage()) && !newItem.getPage().contains("itemId=");
                    
                    break;
                }
            }
            
            if (remove)
            {
                for (int i = breadcrumbs.size() - 1; i >= position; i--)
                {
                    breadcrumbs.remove(i);
                }
               
            }
            
        }
       
        if (!keepold)
        {
            breadcrumbs.add(newItem);
            logger.debug("Pushing breadcrumb item: " + newItem);
        }
        else
        {
            breadcrumbs.add(lastItem);
            logger.debug("Pushing breadcrumb item: " + lastItem);
        }
        

        
        breadcrumbs.get(breadcrumbs.size()-1).setIsLast(true);
    }

    /**
     * get and remove the last BreadcrumbItem from history.
     *
     * @return BreadcrumbItem
     */
    public BreadcrumbItem pop()
    {
        return get(true);
    }

    /**
     * get the last BreadcrumbItem from history
     *
     * @return BreadcrumbItem
     */
    public BreadcrumbItem get()
    {
        return get(false);
    }

    private BreadcrumbItem get(boolean remove)
    {
        BreadcrumbItem returnItem = null;
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


    public List<BreadcrumbItem> getBreadcrumbItemHistory()
    {
        logger.debug("Accessing BC:" + breadcrumbs + ":" + this);

        // return only the last 3 items of the breadcrumb list
        if( breadcrumbs.size() > 3 ) {
            List<BreadcrumbItem> breadcrumbsLimited = new ArrayList<BreadcrumbItem>();
            breadcrumbsLimited.add(breadcrumbs.get(breadcrumbs.size()-3));
            breadcrumbsLimited.add(breadcrumbs.get(breadcrumbs.size()-2));
            breadcrumbsLimited.add(breadcrumbs.get(breadcrumbs.size()-1));
            return breadcrumbsLimited;
        }
        else 
        {
            return breadcrumbs;
        }
    }

    public void setBreadcrumbItemHistory(List<BreadcrumbItem> breadcrumbs)
    {
        this.breadcrumbs = breadcrumbs;
    }

    public BreadcrumbItem getCurrentItem()
    {
        if (breadcrumbs.size() > 0)
        {
            return breadcrumbs.get(breadcrumbs.size() - 1);
        }
        else
        {
            return new BreadcrumbItem("HomePage", "HomePage", null, false);
        }
    }

	public BreadcrumbItem getPreviousItem() {
		if (breadcrumbs.size() > 1)
		{
			return breadcrumbs.get(breadcrumbs.size() - 2);
		}
		else
		{
			return new BreadcrumbItem("HomePage", "HomePage", null, false);
		}
	}
	
	/**
	 * Returns the display value of the last breadcrumb entry. If the breadcrumbs are
	 * empty, the 'Homepage' value is returned.
	 * @return display value of the last breadcrumb entry
	 */
	public String getLastPageIdentifier() {
	    if (breadcrumbs.size() >= 1)
        {
            return breadcrumbs.get(breadcrumbs.size() - 1).getDisplayValue();
        }
        else
        {
            return new BreadcrumbItem("HomePage", "HomePage", null, false).getDisplayValue();
        }
	}

}
