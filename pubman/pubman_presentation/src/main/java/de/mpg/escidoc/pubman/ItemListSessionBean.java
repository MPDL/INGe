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

package de.mpg.escidoc.pubman;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.el.ELContext;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.comparator.PubItemVOComparator;

/**
 * Superclass for keeping all attributes that are used for the whole session by ItemLists.
 * @author:  Thomas Diebäcker, created 10.01.2007
 * @version: $Revision: 1629 $ $LastChangedDate: 2007-11-29 12:01:41 +0100 (Do, 29 Nov 2007) $
 * Revised by DiT: 14.08.2007
 */
public class ItemListSessionBean extends FacesBean
{
	
	public static final String BEAN_NAME = "ItemListSessionBean";
	
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(ItemListSessionBean.class);

    private List<PubItemVOPresentation> currentPubItemList = new ArrayList<PubItemVOPresentation>();
    private boolean isListDirty = true;
    private String message = null;
    private String sortBy = "DATE";
    private String sortOrder = "DESCENDING";
    private String type = null;
    
    private int itemsPerPage = 10;
    private int currentPubItemListPointer = 0;

    /**
     * True if the list is shown as  revisions list, additional information is displayed then (release date, description)
     */
    private boolean isRevisionView;

    /**
     * Default constructor.
     */
//    public ItemListSessionBean()
//    {
//        this.init();
//    }
    
    /**
     * This method is called when this bean is initially added to session scope. Typically, this occurs as a result of
     * evaluating a value binding or method binding expression, which utilizes the managed bean facility to instantiate
     * this bean and store it into session scope.
     */
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
        
        this.isListDirty = true;
        this.isRevisionView = false;
        
    }

    /**
     * Remove an item by the given ID from the current item list.
     * @param itemToBeRemoved the RO of the item to be removed
     */
    public void removeFromCurrentListByRO(ItemRO itemToBeRemoved)
    {
        for (int i = 0; i < this.currentPubItemList.size(); i++)
        {
            PubItemVO pubItem = this.currentPubItemList.get(i);
            if (pubItem.getVersion().getObjectId().equals(itemToBeRemoved.getObjectId())
                    && pubItem.getVersion().getVersionNumber() == itemToBeRemoved.getVersionNumber())
            {
                this.currentPubItemList.remove(pubItem);
            }
        }
    }
    
    public List<PubItemVOPresentation> getCurrentPubItemList()
    {
    	logger.debug("ILSB.getCurrentPubItemList: " + this);
    	logger.debug("Accessing item list.");
        return this.currentPubItemList;
    }

    public void setCurrentPubItemList(List<PubItemVOPresentation> currentPubItemList)
    {
    	logger.debug("ILSB.setCurrentPubItemList: " + this);
    	
    	resetPresentationFlags();
    	
        this.currentPubItemList = currentPubItemList;
        
        // clear the selectedList
        this.getSelectedPubItems().clear();
    }

    /**
     * Resets the flags for specialized type of listViews, e.g. the revision list. Called every time when a new pubItem List is set.
     */
    private void resetPresentationFlags()
    {
        setIsRevisionView(false);
        
    }

    public List<PubItemVOPresentation> getSelectedPubItems()
    {

    	List<PubItemVOPresentation> selectedPubItems = new ArrayList<PubItemVOPresentation>();
    	for (PubItemVOPresentation item : currentPubItemList) {
			if (item.getSelected())
			{
				selectedPubItems.add(item);
			}
		}
        return selectedPubItems;
    }
    
    public boolean getIsSearchResultList()
    {
    	boolean isSearchresultList = false;
    	
    	// check in the list of one item is of type search result
    	if(this.currentPubItemList != null && this.currentPubItemList.size() > 0)
    	{
    		if(this.currentPubItemList.get(0).isSearchResult() == true)
    		{
    			isSearchresultList = true;
    		}
    	}
    	return isSearchresultList;
    }

    public String getMessage()
    {
        return this.message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getSortBy()
    {
        return sortBy;
    }

    public void setSortBy(String sortBy)
    {
        this.sortBy = sortBy;
    }

    public String getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public boolean isListDirty()
    {
        return isListDirty;
    }

    public void setListDirty(boolean isListDirty)
    {
        this.isListDirty = isListDirty;
        
    }
    
    public int getCurrentPubItemListSize()
    {
    	logger.debug("ILSB: " + this);
    	return currentPubItemList.size();
    }

	public int getItemsPerPage() {
		return itemsPerPage;
	}

	public void setItemsPerPage(int itemsPerPage) {
		
	}

	public int getCurrentPubItemListPointer() {
		return currentPubItemListPointer;
	}

	public void setCurrentPubItemListPointer(int currentPubItemListPointer) {
		this.currentPubItemListPointer = currentPubItemListPointer;
	}
    
	public List<Page> getPages()
	{
		List<Page> pages = new ArrayList<Page>();
		for (int i = 1; i <= ((currentPubItemList.size() - 1) / itemsPerPage + 1); i++)
		{
			pages.add(new Page(i));
		}
		return pages;
	}
	
	public boolean getIsFirstPage()
	{
		return currentPubItemListPointer == 0;
	}
	
	public boolean getIsLastPage()
	{
		int lastPage = (currentPubItemList.size() - 1) / itemsPerPage + 1;
		return currentPubItemListPointer / itemsPerPage == lastPage - 1;
	}

	public boolean getIsAscending()
	{
		return SortOrder.ASCENDING.toString().equals(sortOrder);
	}

	public void gotoFirstPage()
	{
		this.currentPubItemListPointer = 0;
	}
	
	public void gotoLastPage()
	{
		int lastPage = (currentPubItemList.size() - 1) / itemsPerPage + 1;
		this.currentPubItemListPointer = itemsPerPage * (lastPage - 1);
	}
	
	public void gotoPrecedingPage()
	{
		if (this.currentPubItemListPointer >= itemsPerPage)
		{
			this.currentPubItemListPointer -= itemsPerPage;
		}
	}
	
	public void gotoFollowingPage()
	{
		int lastPage = (currentPubItemList.size() - 1) / itemsPerPage + 1;
		if (this.currentPubItemListPointer <= itemsPerPage * (lastPage - 1))
			this.currentPubItemListPointer += itemsPerPage;
	}
	
	public void changePage(ActionEvent event)
	{
		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		try
		{
			int page = Integer.parseInt(event.getComponent().getValueExpression("text").getValue(elContext).toString());
			int lastPage = (currentPubItemList.size() - 1) / itemsPerPage + 1;
			
			if (page >= 1 && page <= lastPage)
			{
				this.currentPubItemListPointer = itemsPerPage * (page - 1);
			}
		} catch (Exception e) {
			logger.error("Unable to change page", e);
		}
	}
	
    /**
     * ValueChange handler for comboBoxes.
     * @param event the event of the valueChange
     */
    public void setSortBy(final ValueChangeEvent event)
    {
    	if (logger.isDebugEnabled())
        {
            logger.debug("New value of sortBy: "
                    + event.getNewValue() + " [" + event.getNewValue().getClass() + "]");
        }
    	sortBy = event.getNewValue().toString();
    	
    	sortItemList();
    	
    	// Delete list from component tree to let it be refreshed.
        UIComponent component = FacesContext.getCurrentInstance().getViewRoot().findComponent("form1:content:list:listtable");
        if (component != null)
        {
        	component.getParent().getChildren().remove(component);
        }
        
    }

    /**
     * Action handler for comboBoxes.
     * @param event the event of the action
     */
    public void setSortOrder(final ActionEvent event)
    {
    	if (SortOrder.ASCENDING.toString().endsWith(sortOrder))
    	{
    		sortOrder = SortOrder.DESCENDING.toString();
    	}
    	else
    	{
    		sortOrder = SortOrder.ASCENDING.toString();
    	}
    	
        sortItemList();
        
        // Delete list from component tree to let it be refreshed.
        UIComponent component = FacesContext.getCurrentInstance().getViewRoot().findComponent("form1:content:list:listtable");
        if (component != null)
        {
        	component.getParent().getChildren().remove(component);
        }
    }
    
    /**
     * Action handler for comboBoxes.
     * @param event the event of the action
     */
    public void switchToShortView(final ActionEvent event)
    {
    	for (PubItemVOPresentation item : currentPubItemList) {
			item.setShortView(true);
		}
    }
    
    /**
     * Action handler for comboBoxes.
     * @param event the event of the action
     */
    public void switchToMediumView(final ActionEvent event)
    {
    	for (PubItemVOPresentation item : currentPubItemList) {
			item.setMediumView(true);
		}
    }
    
    /**
     * ValueChange handler for comboBoxes.
     * @param event the event of the valueChange
     */
    public void setSelection(final ValueChangeEvent event)
    {

        if (logger.isDebugEnabled())
        {
            logger.debug("New value of cboSelectItems: "
                    + event.getNewValue() + " [" + event.getNewValue().getClass() + "]");
        }

        if (event.getNewValue().equals(InternationalizationHelper.SelectMultipleItems.SELECT_ALL.toString()))
        {
        	for (PubItemVOPresentation item : currentPubItemList) {
				item.setSelected(true);
			}
        }
        else if (event.getNewValue().equals(InternationalizationHelper.SelectMultipleItems.SELECT_VISIBLE.toString()))
        {
        	int size = currentPubItemList.size();
        	for (int i = 0; i < itemsPerPage; i++)
        	{
        		if (currentPubItemListPointer + i <= size)
        		{
        			currentPubItemList.get(currentPubItemListPointer + i).setSelected(true);
        		}
        		else
        		{
        			break;
        		}
			}
        }
        else if (event.getNewValue().equals(InternationalizationHelper.SelectMultipleItems.DESELECT_ALL.toString()))
        {
        	for (PubItemVOPresentation item : currentPubItemList) {
				item.setSelected(false);
			}
        }

        // Delete list from component tree to let it be refreshed.
        UIComponent component = FacesContext.getCurrentInstance().getViewRoot().findComponent("form1:content:list:listtable");
        if (component != null)
        {
        	component.getParent().getChildren().remove(component);
        }

    }

    /**
     * Sorts the result item list.
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String sortItemList()
    {

        if (logger.isDebugEnabled())
        {
            logger.debug("New sort order: " + sortBy);
        }

        // get the sorting criteria by the FacesBean
        PubItemVOComparator.Criteria sortByCriteria = PubItemVOComparator.Criteria.valueOf(PubItemVOComparator.Criteria.class, sortBy);

        if (logger.isDebugEnabled())
        {
            logger.debug("New sorting criteria: " + sortByCriteria.toString());
        }

        // Instanciate the comparator with the sorting criteria
        PubItemVOComparator pubItemVOComparator = new PubItemVOComparator(sortByCriteria);

        // sort ascending or descending
        if (sortOrder.equals(SortOrder.ASCENDING.toString()))
        {
            Collections.sort(currentPubItemList, pubItemVOComparator);
        }
        else if (sortOrder.equals(SortOrder.DESCENDING.toString()))
        {
            Collections.sort(currentPubItemList, Collections.reverseOrder(pubItemVOComparator));
        }

        return null;
    }
    
    public void changeItemsPerPage(ValueChangeEvent event)
    {
    	if (event.getOldValue() != null && !event.getOldValue().equals(event.getNewValue()))
    	{
    		this.itemsPerPage = Integer.parseInt(event.getNewValue().toString());
    		this.currentPubItemListPointer = this.currentPubItemListPointer - this.currentPubItemListPointer % this.itemsPerPage;
    	}
    }
    
	public class Page
	{
		int value = 0;

		public Page(int i)
		{
			value = i;
		}
		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}
		
		public String toString()
		{
			return value + "";
		}
		
	}
	
	public enum SortOrder
	{
		ASCENDING, DESCENDING
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	 public boolean getIsRevisionView()
	    {
	        return isRevisionView;
	    }

	    public void setIsRevisionView(boolean isRevisionView)
	    {
	        this.isRevisionView = isRevisionView;
	    }
}
