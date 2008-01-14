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

package de.mpg.escidoc.pubman.itemList.ui;

import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;
import org.apache.log4j.Logger;
import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.ui.ContainerPanelUI;
import de.mpg.escidoc.pubman.ui.HTMLElementUI;
import de.mpg.escidoc.pubman.ui.ListUI;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.pubman.util.PubItemVOWrapper;
import de.mpg.escidoc.pubman.util.ValueObjectWrapper;
import de.mpg.escidoc.pubman.viewItem.ui.ViewItemPanelUI;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.comparator.PubItemVOComparator;
import de.mpg.escidoc.services.common.valueobjects.comparator.PubItemVOComparator.Criteria;

/**
 * UI for viewing a list of items.
 *
 * @author: Thomas Diebäcker, created 30.08.2007
 * @version: $Revision: 1694 $ $LastChangedDate: 2007-12-18 10:43:39 +0100 (Tue, 18 Dec 2007) $
 */
public class ItemListUI extends ListUI implements ActionListener, ValueChangeListener
{
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ItemListUI.class);
    private static final String IMAGE_ORDER_ASCENDING = "images/sort_icon_ascending.jpg";
    private static final String IMAGE_ORDER_DESCENDING = "images/sort_icon_descending.jpg";

    // item list controls
    private HTMLElementUI htmlElementUI = new HTMLElementUI();
    private HtmlPanelGroup panItemListControls = new HtmlPanelGroup();
    private HtmlSelectOneMenu cboSelectItems = new HtmlSelectOneMenu();
    private HtmlOutputLabel lblSortBy = new HtmlOutputLabel();
    private HtmlSelectOneMenu cboSortBy = new HtmlSelectOneMenu();
    private HtmlOutputLabel lblOrder = new HtmlOutputLabel();
    private HtmlCommandButton btToggleOrder = new HtmlCommandButton();
    private HtmlOutputLabel lblItemDetail = new HtmlOutputLabel();
    private HtmlCommandButton btViewItemShort = new HtmlCommandButton();
    private HtmlCommandButton btViewItemMedium = new HtmlCommandButton();

    // constant for default search criteria
    public static final PubItemVOComparator.Criteria SORTBY_DEFAULT = PubItemVOComparator.Criteria.DATE;

    private boolean isOrderAscending = false;
    
    /**
     * Public constructor.
     * @param allPubItems the PubItems that should be shown in this list
     */
    public ItemListUI(List<PubItemVOWrapper> allPubItems, String actionMethodForTitle)
    {
        // call constructor of super class
        super(allPubItems, actionMethodForTitle);  
        
        // get the selected language...
        i18nHelper = (InternationalizationHelper) application.getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
        // ... and set the refering resource bundle
        bundleLabel = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());

        // initialize list controls
        this.panItemListControls.getChildren().clear();
        this.panItemListControls.setId(CommonUtils.createUniqueId(this.panItemListControls));
        this.panItemListControls.getChildren().add(this.htmlElementUI.getStartTagWithStyleClass("div", "listHeader dark"));

        this.cboSelectItems.setId(CommonUtils.createUniqueId(this.cboSelectItems));
        this.cboSelectItems.getChildren().add(CommonUtils.convertToSelectItemsUI(this.getApplicationBean().getSelectItemsItemListSelectMultipleItems()));
        this.cboSelectItems.setOnchange("submit();");
        this.cboSelectItems.addValueChangeListener(this);
        this.panItemListControls.getChildren().add(this.cboSelectItems);

        this.lblSortBy.setId(CommonUtils.createUniqueId(this.lblSortBy));
        this.lblSortBy.setValue(bundleLabel.getString("ItemList_SortBy"));
        this.panItemListControls.getChildren().add(this.lblSortBy);

        this.cboSortBy.setId(CommonUtils.createUniqueId(this.cboSortBy));
        this.cboSortBy.getChildren().add(CommonUtils.convertToSelectItemsUI(this.getApplicationBean().getSelectItemsItemListSortBy()));
        this.cboSortBy.setOnchange("submit();");
        this.cboSortBy.addValueChangeListener(this);
        this.cboSortBy.setValue(ItemListUI.SORTBY_DEFAULT.toString());
        this.panItemListControls.getChildren().add(this.cboSortBy);

        this.lblOrder.setId(CommonUtils.createUniqueId(this.lblOrder));
        this.lblOrder.setValue(this.isOrderAscending ? bundleLabel.getString("ItemList_SortOrderAscending") : bundleLabel.getString("ItemList_SortOrderDescending"));
        this.panItemListControls.getChildren().add(this.lblOrder);

        this.btToggleOrder.setId(CommonUtils.createUniqueId(this.btToggleOrder));
        this.btToggleOrder.setImage(this.isOrderAscending ? ItemListUI.IMAGE_ORDER_ASCENDING : ItemListUI.IMAGE_ORDER_DESCENDING);
        this.btToggleOrder.setImmediate(true);
        this.btToggleOrder.addActionListener(this);
        this.panItemListControls.getChildren().add(this.btToggleOrder);

        this.panItemListControls.getChildren().add(this.htmlElementUI.getStartTagWithStyleClass("div", "displayControls"));
        
        this.lblItemDetail.setId(CommonUtils.createUniqueId(this.lblItemDetail));
        this.lblItemDetail.setValue(bundleLabel.getString("ItemList_Details"));        
        this.panItemListControls.getChildren().add(this.lblItemDetail);

        this.btViewItemShort.setId(CommonUtils.createUniqueId(this.btViewItemShort));
        this.btViewItemShort.setImage("images/short_view_item.jpg");
        this.btViewItemShort.setImmediate(true);
        this.btViewItemShort.addActionListener(this);
        this.panItemListControls.getChildren().add(this.btViewItemShort);

        this.btViewItemMedium.setId(CommonUtils.createUniqueId(this.btViewItemMedium));
        this.btViewItemMedium.setImage("images/medium_view_item.jpg");
        this.btViewItemMedium.setImmediate(true);
        this.btViewItemMedium.addActionListener(this);
        this.panItemListControls.getChildren().add(this.btViewItemMedium);
        
        this.panItemListControls.getChildren().add(htmlElementUI.getEndTag("div"));
        this.panItemListControls.getChildren().add(htmlElementUI.getEndTag("div"));

        this.panTitleBar.getChildren().add(this.panItemListControls);

        // display the first items
        this.displayObjects();
    }

    /**
     * Instanciates a new single item and adds it to the container for display.
     * @param valueObjectWrapper the wrapper with the ValueObject which should be displayed in the UI
     * @return the ContainerPanelUI in which the new Item is displayed
     */
    protected ContainerPanelUI displayObject(ValueObjectWrapper valueObjectWrapper)
    {
        ViewItemPanelUI viewItemPanelUI = new ViewItemPanelUI((PubItemVOWrapper)valueObjectWrapper, this.actionMethodForTitle);
        this.addToContainer(viewItemPanelUI);

        return viewItemPanelUI;
    }

    /**
     * Shows all items in short view.
     */
    public void showAsViewItemShort()
    {
        this.showAs(PubItemVOWrapper.SHOW_AS_SHORT);
    }

    /**
     * Shows all items in medium view.
     */
    public void showAsViewItemMedium()
    {
        this.showAs(PubItemVOWrapper.SHOW_AS_MEDIUM);
    }

    /**
     * Shows all items in the given view.
     * @param showAs the view that all items should have
     */
    private void showAs(int showAs)
    {
        for (int i = 0; i < this.getAllObjects().size(); i++)
        {
            this.getAllObjects().get(i).setItemView(showAs);
        }

        this.displayObjects();
    }

    /**
     * Toggles the sort order of the items.
     */
    protected void toggleSortOrder()
    {
        // toggle variable
        this.isOrderAscending = !this.isOrderAscending;

        // change label
        this.lblOrder.setValue(this.isOrderAscending ? bundleLabel.getString("ItemList_SortOrderAscending") : bundleLabel.getString("ItemList_SortOrderDescending"));

        // change graphics for toggle button
        this.btToggleOrder.setImage(this.isOrderAscending ? ItemListUI.IMAGE_ORDER_ASCENDING : ItemListUI.IMAGE_ORDER_DESCENDING);

        // sort the list
        this.sortItemList((String)this.cboSortBy.getSubmittedValue(), this.isOrderAscending);
        
        if (logger.isDebugEnabled())
        {
            logger.debug("SortOrder has been changed from " + !this.isOrderAscending + " to " + this.isOrderAscending + ".");
        }
    }

    /**
     * Selects all items.
     */
    private void selectAllItems(boolean selected)
    {
        // set the selected attribute in the PubItemVOWrapper for all items
        this.selectItems(this.getAllObjects(), selected);
    }

    /**
     * Selects all currently visible items.
     */
    private void selectVisibleItems(boolean selected)
    {
        // set the selected attribute in the PubItemVOWrapper for all currently displayed items
        this.selectItems(this.getObjectsToDisplay(), selected);
    }

    /**
     * Sets the selected attribute for every item in the given list.
     * @param list the list with items
     * @param selected the value for the selected attribute
     */
    private void selectItems(List<PubItemVOWrapper> list, boolean selected)
    {
        // set the selected attribute in the PubItemVOWrapper for the given items
        for (int i = 0; i < list.size(); i++)
        {
            list.get(i).setSelected(selected);
        }
        
        // Inserted by FrM, 8.11.07: Set number of selected items for delete confirmation.
        logger.debug("Setting noso to " + getNumberOfSelectedObjects());
        this.numberSelectedObjects.setText(getNumberOfSelectedObjects());
        
        // redisplay the visible items
        this.displayObjects();
    }

    /**
     * Sorts the item list.
     * @param sortBy the search criteria
     * @param sortOrder the sorting order
     */    
    public void sortItemList(String sortBy, boolean sortOrderAscending)
    {
        PubItemVOComparator.Criteria sortByCriteria = PubItemVOComparator.Criteria.valueOf(PubItemVOComparator.Criteria.class, sortBy);
        
        if (logger.isDebugEnabled())
        {
            logger.debug("New sorting criteria: " + sortByCriteria.toString());
        }

        // instanciate the comparator with the sorting criteria
        PubItemVOWrapperComparator pubItemVOWrapperComparator = new PubItemVOWrapperComparator(sortByCriteria);

        this.sortObjectList(pubItemVOWrapperComparator, sortOrderAscending);
    }

    /**
     * Action handler for user actions.
     * @param event the event of the action
     */
    public void processAction(ActionEvent event)
    {
        super.processAction(event);

        if (event.getComponent() == this.btToggleOrder)
        {
            this.toggleSortOrder();
        }
        else if (event.getComponent() == this.btViewItemShort)
        {
            this.showAsViewItemShort();
        }
        else if (event.getComponent() == this.btViewItemMedium)
        {
            this.showAsViewItemMedium();
        }
    }

    /**
     * ValueChange handler for comboBoxes.
     * @param event the event of the valueChange
     */
    public void processValueChange(ValueChangeEvent event)
    {
        super.processValueChange(event);

        if (event.getComponent() == this.cboSelectItems)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("New value of cboSelectItems: " + event.getNewValue() + " [" + event.getNewValue().getClass() + "]");
            }

            if (event.getNewValue().equals(ApplicationBean.SelectMultipleItems.SELECT_ALL.toString()))
            {
                this.selectAllItems(true);
            }
            else if (event.getNewValue().equals(ApplicationBean.SelectMultipleItems.SELECT_VISIBLE.toString()))
            {
                this.selectVisibleItems(true);
            }
            else if (event.getNewValue().equals(ApplicationBean.SelectMultipleItems.DESELECT_ALL.toString()))
            {
                this.selectAllItems(false);
            }

            // switch back to old value
            this.cboSelectItems.setValue(event.getOldValue());

        }
        else if (event.getComponent() == this.cboSortBy)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("New value of cboSortBy: " + event.getNewValue() + " [" + event.getNewValue().getClass() + "]");
            }
            
            this.sortItemList((String)event.getNewValue(), this.isOrderAscending);
        }
    }

    /**
     * Casts all objects stored in the super class to PubItemVO, so we don't have to do this every time.
     * @return all PubItemVOs in this list
     */
    protected List<PubItemVOWrapper> getAllObjects()
    {
        return (List<PubItemVOWrapper>) super.getAllObjects();
    }

    /**
     * Sets all Objects in the lists and initially set the first objects to be displayed.
     * Overrides method of superclass and additionally sorts the new list.
     * @param allObjects
     */
    @Override
    protected void setAllObjects(List<? extends ValueObjectWrapper> allObjects)
    {        
        // set objects in superclass
        super.setAllObjects(allObjects);
        
        // sort the new list
        // sort by default if cboSortBy has no value yet
        String sortBy = ((this.cboSortBy != null && this.cboSortBy.getSubmittedValue() != null) ? 
                            ((PubItemVOComparator.Criteria)this.cboSortBy.getSubmittedValue()).toString() : ItemListUI.SORTBY_DEFAULT.toString());        
        this.sortItemList(sortBy, this.isOrderAscending);
    }

    /**
     * Casts all displayed objects stored in the super class to PubItemVOWrapper, so we don't have to do this every time.
     * @return all displayed PubItemVOWrappers in this list
     */
    @Override
    protected List<PubItemVOWrapper> getObjectsToDisplay()
    {
        return (List<PubItemVOWrapper>) super.getObjectsToDisplay();
    }
    
    /**
     * Casts all selected objects stored in the super class to PubItemVO, so we don't have to do this every time.
     * @return all selected PubItemVOs in this list
     */
    @Override
    public List<PubItemVO> getSelectedObjects()
    {
        return (List<PubItemVO>) super.getSelectedObjects();
    }
    
    /**
     * Returns the ApplicationBean.
     * 
     * @return a reference to the scoped data bean (ApplicationBean)
     */
    protected ApplicationBean getApplicationBean()
    {
        Application application = FacesContext.getCurrentInstance().getApplication();
        return (ApplicationBean)application.getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), ApplicationBean.BEAN_NAME);
    }    
    
    /**
     * Inner class for comparator that sorts PubItemVOWrappers.
     * @author Thomas Diebaecker
     */
    public class PubItemVOWrapperComparator implements Comparator<PubItemVOWrapper>
    {
        PubItemVOComparator pubItemVOComparator = null;
        
        public PubItemVOWrapperComparator(Criteria criteria)
        {
            this.pubItemVOComparator = new PubItemVOComparator(criteria);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(PubItemVOWrapper pubItemVOWrapper1, PubItemVOWrapper pubItemVOWrapper2)
        {
            return this.pubItemVOComparator.compare(pubItemVOWrapper1.getValueObject(), pubItemVOWrapper2.getValueObject());
        }
    }
}
