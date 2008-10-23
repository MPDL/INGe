package de.mpg.escidoc.pubman.ui;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;
import javax.faces.model.SelectItem;

import de.mpg.escidoc.pubman.appbase.FacesBean;

/**
 * This ListControlSessionBean is used as counterpart for the listControl.jspf and can be
 * used to handle navigation through a list of items or objects. Page navigation is delegated
 * to the PaginatorControlSessionBean.
 *
 * @author Mario Wagner
 */
public class ListControlSessionBean extends FacesBean implements ValueChangeListener
{
    public static final String BEAN_NAME = "ListControlSessionBean";

    private boolean sortOrderAscending = true;

    private String selectMultipleItems;
    private String selectSortBy;

    PaginatorControlSessionBean paginatorControlSessionBean = (PaginatorControlSessionBean)getBean(PaginatorControlSessionBean.class);

    public ListControlSessionBean()
    {
        paginatorControlSessionBean.addValueChangeListener(this);
        processModelAndDisplayUpdate();
    }

    /**
     * Process ValueChangeEvent derived from PaginatorControlSessionBean
     */
    public void processValueChange(ValueChangeEvent event) throws AbortProcessingException
    {
        // call your List update action here

        // the number of the page displayed may have changed
        // or the number of items to be displayed per page 

    }

    /**
     * ActionListener method which will be invoked, when the sortButton has been pressed.
     * @param event
     */
    public void sortButtonPressed(ActionEvent event)
    {
        sortOrderAscending = !sortOrderAscending;
        // call your List update action here

    }

    /**
     * Navigation method to be called after the sortButtonPressed actionListener.
     * Handles the internal model update and leads navigation back to redisplay the 
     * current view.
     * @return null
     */
    public String sortButtonAction()
    {
        processModelAndDisplayUpdate();
        return null;
    }

    /**
     * ActionListener method which will be invoked, when the shortButton has been pressed.
     * @param event
     */
    public void shortButtonPressed(ActionEvent event)
    {
        // call your List update action here

    }

    /**
     * Navigation method to be called after the shortButtonPressed actionListener.
     * Handles the internal model update and leads navigation back to redisplay the
     * current view.
     * @return null
     */
    public String shortButtonAction()
    {
        processModelAndDisplayUpdate();
        return null;
    }

    /**
     * ActionListener method which will be invoked, when the mediumButton has been pressed.
     * @param event
     */
    public void mediumButtonPressed(ActionEvent event)
    {
        // call your List update action here

    }

    /**
     * Navigation method to be called after the mediumButtonPressed actionListener.
     * Handles the internal model update and leads navigation back to redisplay the
     * current view.
     * @return null
     */
    public String mediumButtonAction()
    {
        processModelAndDisplayUpdate();
        return null;
    }

    private void processModelAndDisplayUpdate()
    {
        // call your List update action here

    }

    public void processSelectMultipleItemsChanged(ValueChangeEvent event)
    {
        selectMultipleItems = (String)event.getNewValue();
        processModelAndDisplayUpdate();
    }

    public void processSelectSortByChanged(ValueChangeEvent event)
    {
        selectSortBy = (String)event.getNewValue();
        processModelAndDisplayUpdate();
    }

    public SelectItem[] getSelectMultipleItemOptions()
    {
        // SELECT_ITEMS, SELECT_ALL, DESELECT_ALL, SELECT_VISIBLE
        return this.i18nHelper.getSelectItemsItemListSelectMultipleItems();
    }

    public SelectItem[] getSelectSortByOptions()
    {
        // PubItemVOComparator.Criteria enum
        return this.i18nHelper.getSelectItemsItemListSortBy();
    }

    public String getSelectMultipleItems()
    {
        return selectMultipleItems;
    }

    public void setSelectMultipleItems(String selectMultipleItems)
    {
        this.selectMultipleItems = selectMultipleItems;
    }

    public String getSelectSortBy()
    {
        return selectSortBy;
    }

    public void setSelectSortBy(String selectSortBy)
    {
        this.selectSortBy = selectSortBy;
    }

    public boolean isSortOrderAscending()
    {
        return sortOrderAscending;
    }

    public void setSortOrderAscending(boolean sortOrderAscending)
    {
        this.sortOrderAscending = sortOrderAscending;
    }

}
