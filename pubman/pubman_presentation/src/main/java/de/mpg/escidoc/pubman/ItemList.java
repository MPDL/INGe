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
import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectBoolean;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;
import com.sun.rave.web.ui.appbase.AbstractFragmentBean;
import com.sun.rave.web.ui.component.DropDown;
import com.sun.rave.web.ui.component.RadioButtonGroup;
import com.sun.rave.web.ui.component.StaticText;
import com.sun.rave.web.ui.model.Option;
import de.mpg.escidoc.pubman.depositorWS.DepositorWS;
import de.mpg.escidoc.pubman.itemList.ui.ItemListUI;
import de.mpg.escidoc.pubman.search.SearchResultList;
import de.mpg.escidoc.pubman.submitItem.SubmitItem;
import de.mpg.escidoc.pubman.submitItem.SubmitItemSessionBean;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.pubman.viewItem.ViewItemFull;
import de.mpg.escidoc.pubman.viewItem.ViewItemSessionBean;
import de.mpg.escidoc.pubman.withdrawItem.WithdrawItem;
import de.mpg.escidoc.pubman.withdrawItem.WithdrawItemSessionBean;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.comparator.PubItemVOComparator;

/**
 * Superclass for all classes dealing with item lists (e.g. DepositorWS, SearchResultList)
 * This class provides all functionality for showing, sorting and choosing one or more items out of a list.  
 *
 * @author:  Thomas Diebäcker, created 10.05.2007
 * @version: $Revision: 1695 $ $LastChangedDate: 2007-12-18 14:25:56 +0100 (Tue, 18 Dec 2007) $
 * Revised by DiT: 14.08.2007
 */
public abstract class ItemList extends AbstractFragmentBean
{
    private static Logger logger = Logger.getLogger(ItemList.class);

    // for handling the resource bundles (i18n)
    protected Application application = null;
    // get the selected language...
    protected InternationalizationHelper i18nHelper = null;
    // ... and set the refering resource bundle
    protected ResourceBundle bundleLabel = null;
    protected ResourceBundle bundleMessage = null;

    // binded components in JSP
    private StaticText valMessage = null;
    private DropDown cboSortBy = null;
    private RadioButtonGroup rbgSortOrder = null;

    // panel for dynamic components
    HtmlPanelGrid panDynamicItemList = null;

    // constants for comboBoxes
    public Option SORTBY_DATE = null;    
    public Option SORTBY_TITLE = null;
    public Option SORTBY_GENRE = null;
    public Option SORTBY_CREATOR = null;
    public Option SORTBY_PUBLISHING_INFO = null;
    public Option SORTBY_REVIEWMETHOD = null;
    public Option SORTBY_SOURCECREATOR = null;
    public Option SORTBY_SOURCETITLE = null;
    public Option SORTBY_EVENTTITLE = null;
    public Option[] SORTBY_OPTIONS = null;
    public Option SORTORDER_ASCENDING = null;
    public Option SORTORDER_DESCENDING = null;
    public Option[] SORTORDER_OPTIONS = null;

    // constants for error and status messages
    public static final String MESSAGE_NO_ITEM_SELECTED = "depositorWS_NoItemSelected";
    public static final String MESSAGE_WRONG_ITEM_STATE = "depositorWS_wrongItemState";
    public static final String MESSAGE_SUCCESSFULLY_SUBMITTED = "depositorWS_SuccessfullySubmitted";
    public static final String MESSAGE_NOT_SUCCESSFULLY_SUBMITTED = "depositorWS_NotSuccessfullySubmitted";
    public static final String MESSAGE_SUCCESSFULLY_WITHDRAWN = "depositorWS_SuccessfullyWithdrawn";
    public static final String MESSAGE_SUCCESSFULLY_DELETED = "depositorWS_SuccessfullyDeleted";
    public static final String MESSAGE_SUCCESSFULLY_SAVED = "depositorWS_SuccessfullySaved";
    public static final String MESSAGE_SUCCESSFULLY_ACCEPTED = "depositorWS_SuccessfullyAccepted";
    public static final String MESSAGE_MANY_ITEMS_SELECTED = "depositorWS_ManyItemsSelected";
    public static final String NO_WITHDRAWAL_COMMENT_GIVEN = "depositorWS_NoWithdrawalCommentGiven";

    
    public ItemList() 
    {
        // for handling the resource bundles (i18n)
        application = FacesContext.getCurrentInstance().getApplication();
        // get the selected language...
        i18nHelper = (InternationalizationHelper) application.getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
        // ... and set the refering resource bundle
        bundleLabel = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
        bundleMessage = ResourceBundle.getBundle(i18nHelper.getSelectedMessagesBundle());

        // binded components in JSP
        valMessage = new StaticText();
        cboSortBy = new DropDown();
        rbgSortOrder = new RadioButtonGroup();

        // panel for dynamic components
        panDynamicItemList = new HtmlPanelGrid();

        // constants for comboBoxes
        SORTBY_DATE = new Option("DATE", bundleLabel.getString("ItemListSortingCriteria_SORTBY_DATE"));    
        SORTBY_TITLE = new Option("TITLE", bundleLabel.getString("ItemListSortingCriteria_SORTBY_TITLE"));
        SORTBY_GENRE = new Option("GENRE", bundleLabel.getString("ItemListSortingCriteria_SORTBY_GENRE"));
        SORTBY_CREATOR = new Option("CREATOR", bundleLabel.getString("ItemListSortingCriteria_SORTBY_CREATOR"));
        SORTBY_PUBLISHING_INFO = new Option("PUBLISHING_INFO", bundleLabel.getString("ItemListSortingCriteria_SORTBY_PUBLISHING_INFO"));
        SORTBY_REVIEWMETHOD = new Option("REVIEW_METHOD", bundleLabel.getString("ItemListSortingCriteria_SORTBY_REVIEWMETHOD"));
        SORTBY_SOURCECREATOR = new Option("SOURCE_CREATOR", bundleLabel.getString("ItemListSortingCriteria_SORTBY_SOURCECREATOR"));
        SORTBY_SOURCETITLE = new Option("SOURCE_TITLE", bundleLabel.getString("ItemListSortingCriteria_SORTBY_SOURCETITLE"));
        SORTBY_EVENTTITLE = new Option("EVENT_TITLE", bundleLabel.getString("ItemListSortingCriteria_SORTBY_EVENTTITLE"));
        SORTBY_OPTIONS = new Option[]{SORTBY_DATE, SORTBY_TITLE, SORTBY_GENRE, SORTBY_CREATOR, SORTBY_PUBLISHING_INFO, SORTBY_REVIEWMETHOD, SORTBY_SOURCECREATOR, SORTBY_SOURCETITLE, SORTBY_EVENTTITLE};
        SORTORDER_ASCENDING = new Option("ASCENDING", bundleLabel.getString("ItemListSortingOrder_SORTORDER_ASCENDING"));
        SORTORDER_DESCENDING = new Option("DESCENDING", bundleLabel.getString("ItemListSortingOrder_SORTORDER_DESCENDING"));
        SORTORDER_OPTIONS = new Option[]{SORTORDER_ASCENDING, SORTORDER_DESCENDING};
    }
    
    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation.
     */
    public void init()
    {
        super.init();
        //re-init the resources and combo-boxes due to direct language switch 
        this.application = FacesContext.getCurrentInstance().getApplication();
        i18nHelper = (InternationalizationHelper) application.getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
        bundleLabel = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
        bundleMessage = ResourceBundle.getBundle(i18nHelper.getSelectedMessagesBundle());
        
        SORTBY_DATE = new Option("DATE", bundleLabel.getString("ItemListSortingCriteria_SORTBY_DATE"));
        SORTBY_TITLE = new Option("TITLE", bundleLabel.getString("ItemListSortingCriteria_SORTBY_TITLE"));
        SORTBY_GENRE = new Option("GENRE", bundleLabel.getString("ItemListSortingCriteria_SORTBY_GENRE"));
        SORTBY_CREATOR = new Option("CREATOR", bundleLabel.getString("ItemListSortingCriteria_SORTBY_CREATOR"));
        SORTBY_PUBLISHING_INFO = new Option("PUBLISHING_INFO", bundleLabel.getString("ItemListSortingCriteria_SORTBY_PUBLISHING_INFO"));
        SORTBY_REVIEWMETHOD = new Option("REVIEW_METHOD", bundleLabel.getString("ItemListSortingCriteria_SORTBY_REVIEWMETHOD"));
        SORTBY_SOURCECREATOR = new Option("SOURCE_CREATOR", bundleLabel.getString("ItemListSortingCriteria_SORTBY_SOURCECREATOR"));
        SORTBY_SOURCETITLE = new Option("SOURCE_TITLE", bundleLabel.getString("ItemListSortingCriteria_SORTBY_SOURCETITLE"));
        SORTBY_EVENTTITLE = new Option("EVENT_TITLE", bundleLabel.getString("ItemListSortingCriteria_SORTBY_EVENTTITLE"));
        SORTBY_OPTIONS = new Option[]{SORTBY_DATE, SORTBY_TITLE, SORTBY_GENRE, SORTBY_CREATOR, SORTBY_PUBLISHING_INFO, SORTBY_REVIEWMETHOD, SORTBY_SOURCECREATOR, SORTBY_SOURCETITLE, SORTBY_EVENTTITLE};
        SORTORDER_ASCENDING = new Option("ASCENDING", bundleLabel.getString("ItemListSortingOrder_SORTORDER_ASCENDING"));
        SORTORDER_DESCENDING = new Option("DESCENDING", bundleLabel.getString("ItemListSortingOrder_SORTORDER_DESCENDING"));
        SORTORDER_OPTIONS = new Option[]{SORTORDER_ASCENDING, SORTORDER_DESCENDING};
    }

    /**
     * Shows an item identified by the itemID in the parameters of the FacesContext.
     * This method is called when a user directly clicks on a link of an item.
     * @param navigationStringToGoBack the navigationString that should be returned when
     * in ViewItem the user wants to go back to the list he came from.
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String showItem(String navigationStringToGoBack)
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ArrayList<PubItemVO> selectedPubItems = new ArrayList<PubItemVO>();

        if (logger.isDebugEnabled())
        {
            logger.debug("Show item");
        }

        // try to get the itemID out of the parameters
        String itemID = (String) facesContext.getExternalContext().getRequestParameterMap().get("itemID");
        if (itemID != null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("ItemID in parameter: " + itemID);
            }
        
            // set the item as current one in ItemController
            PubItemVO puItemVO = CommonUtils.getItemByID(this.getSessionBean().getCurrentPubItemList(), itemID);
            this.getItemControllerSessionBean().setCurrentPubItem(puItemVO);            
            selectedPubItems.add(puItemVO);
            // set all selectedItems in SessionBean
            this.getSessionBean().setSelectedPubItems(selectedPubItems);
    
            // initialize viewItem
            this.getViewItemSessionBean().setNavigationStringToGoBack(navigationStringToGoBack);
            this.getViewItemSessionBean().setItemListSessionBean(getSessionBean());
            
            /*
            try
            {
                facesContext.getExternalContext().redirect("viewItem/viewItemFullPage.jsp?itemId="+this.getItemControllerSessionBean().getCurrentPubItem().getReference().getObjectId());
            }
            catch (IOException e)
            {
                logger.debug("Cannot redirect to view Item Page: " + e.toString());
            }
            */
            if (this.getSessionBean().getSelectedPubItems().size() != 0)
            {
                return ViewItemFull.LOAD_VIEWITEM;
            }
            else
            {
                this.showMessage(DepositorWS.MESSAGE_NO_ITEM_SELECTED);
                return null;
            }            
        }
        else
        {
            logger.error("ItemID has not been set as parameter in link.");
        }
        
        return "";
    }
    
    /**
     * View the selected items.
     * This method is called when the user selects one or more items and then clicks on the view-link in the DepositorWS.
     * @param navigationStringToGoBack the navigationString that should be returned when in ViewItem the user wants to go back to the list he came from. 
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String viewItem(String navigationStringToGoBack) 
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("View item(s)");
        }
        
        // set the currently selected items in the SessionBean
        this.setSelectedItemsAndCurrentItem();
        
        if (this.getSessionBean().getSelectedPubItems().size() != 0)
        {
            // initialize viewItem
            this.getViewItemSessionBean().setNavigationStringToGoBack(navigationStringToGoBack);
            this.getViewItemSessionBean().setItemListSessionBean(getSessionBean());
            
            /*
            try
            {
                facesContext.getExternalContext().redirect("viewItemPage.jsp?itemId="+this.getItemControllerSessionBean().getCurrentPubItem().getReference().getObjectId());
            }
            catch (IOException e)
            {
                logger.debug("Cannot redirect to view Item Page: " + e.toString());
            }
            
            return "";
            */
            if (this.getSessionBean().getSelectedPubItems().size() != 0)
            {
                return ViewItemFull.LOAD_VIEWITEM;
            }
            else
            {
                this.showMessage(DepositorWS.MESSAGE_NO_ITEM_SELECTED);
                return null;
            }                        
        }
        else
        {            
            logger.warn("No item selected.");
            this.showMessage(DepositorWS.MESSAGE_NO_ITEM_SELECTED);
        }
        
        return null;
    }
    
    /**
     * Submits the selected items.
     * This method is called when the user selects one or more items and then clicks on the submit-link in the DepositorWS.
     * @author Michael Franke
     * @param navigationStringToGoBack the navigationString that should be returned when in ViewItem the user wants to go back to the list he came from. 
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String submitItem(String navigationStringToGoBack) 
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Submit item");
        }
        
        this.getSubmitItemSessionBean().setNavigationStringToGoBack(navigationStringToGoBack);
        
        return SubmitItem.LOAD_SUBMITITEM;
    }
    
    /**
     * Withdraws the selected items.
     * This method is called when the user selects one or more items and then clicks on the withdraw-link in the DepositorWS.
     * @author Michael Franke
     * @param navigationStringToGoBack the navigationString that should be returned when in ViewItem the user wants to go back to the list he came from. 
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String withdrawItem(String navigationStringToGoBack) 
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Withdraw item");
        }
        
        this.getWithdrawItemSessionBean().setNavigationStringToGoBack(navigationStringToGoBack);
        this.getWithdrawItemSessionBean().setItemListSessionBean(getSessionBean());
        
        return WithdrawItem.LOAD_WITHDRAWITEM;
    }

    /**
     * Sets the selected items in the SessionBean.
     * TODO ScT: Wenn zwei items existieren und man auf einem Rechner (mit dem gleichen Login) ein item löscht/submitted
     * und auf dem anderen versucht, dieses zu laden, gibt's eine ArrayIndexOutOfBoundsExc, bzw. es wird das zweite item
     * angezeigt (bei biew/edit), obwohl man das erste ausgewählt hat.
     * Um dies zu beheben, darf an dieser Stelle nicht über den Index gegangen werden, sondern es müssen anhand der ID 
     * die selektierten Items ausgewählt werden. Außerdem muß die OutOfBoundsExc vernünftig abgefangen werden.
     */
    protected void setSelectedItemsAndCurrentItem()
    {
        UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();    
        ArrayList<PubItemVO> selectedPubItems = new ArrayList<PubItemVO>();

        // find the component in ViewRoot
        String componentID = null;
        if (this instanceof DepositorWS)
        {
            componentID = "form1:DepositorWS:panItemList";
        }
        else if (this instanceof SearchResultList)
        {
            componentID = "form1:SearchResultList:panItemList";
        }        
        UIComponent panItemList = viewRoot.findComponent(componentID);
        
        // find all checkBoxes that are currently selected
        for (int i=0; i<panItemList.getChildCount(); i++)
        {
            // old version for old lists
            if (panItemList.getChildren().get(i) instanceof HtmlPanelGrid)
            {
                HtmlPanelGrid panel = (HtmlPanelGrid)panItemList.getChildren().get(i);
                for (int j=0; j<panel.getChildCount(); j++)
                {
                    if (panel.getChildren().get(j) instanceof UISelectBoolean)
                    {
                        if (((UISelectBoolean)panel.getChildren().get(j)).isSelected())
                        {
                            selectedPubItems.add(this.getSessionBean().getCurrentPubItemList().get(i));
                            
                            if (logger.isDebugEnabled())
                            {
                                logger.debug("Selected item #" + i + ", ID: " + this.getSessionBean().getCurrentPubItemList().get(i).getReference().getObjectId());
                            }
                        }
                    }
                }
            }
            else if (panItemList.getChildren().get(i) instanceof ItemListUI)
            {
                // new version for ListUI-lists
                ItemListUI itemListUI = (ItemListUI)panItemList.getChildren().get(i);
                selectedPubItems.addAll(itemListUI.getSelectedObjects());
            }
        }
        
        // set all selectedItems in SessionBean
        this.getSessionBean().setSelectedPubItems(selectedPubItems);
        
        if (this.getSessionBean().getSelectedPubItems().size() > 0)
        {
            // set the first selected item as current one in ItemController
            this.getItemControllerSessionBean().setCurrentPubItem(this.getSessionBean().getSelectedPubItems().get(0));
        }
        else
        {
            this.getItemControllerSessionBean().setCurrentPubItem(null);
            logger.warn("No item selected.");
        }
    }
    
    /**
     * Shows the given Message below the itemList. 
     * @param message the message to be displayed
     */
    public void showMessage(String message)
    {
        message = this.bundleMessage.getString(message);
        this.getSessionBean().setMessage(message);
        
        // instantly make this message visible as the page is likely not reloaded and so the message is not set visible via the enableLinks()-method
        this.valMessage.setText(message);
        this.valMessage.setVisible(true);
    }
    
    /** 
     * Removes the message below the itemList
     */
    public void deleteMessage()
    {
    	this.valMessage.setText( "" );
    	this.valMessage.setVisible( false );
    }

    /**
     * Creates the panel newly according to the values in the itemArray.
     */
    protected abstract void createDynamicItemList();

    /**
     * Sorts the result item list.
     * @return string, identifying the page that should be navigated to after this methodcall
     */    
    public String sortItemList()
    {
        ArrayList<PubItemVO> itemList = this.getSessionBean().getCurrentPubItemList();

        // get the sorting order by the SessionBean
        String sortOrderString = this.getSessionBean().getSortOrder(); 
        
        if (logger.isDebugEnabled())
        {
            logger.debug("New sort order: " + sortOrderString);
        }
        
        // get the sorting criteria by the SessionBean
        PubItemVOComparator.Criteria sortByCriteria = PubItemVOComparator.Criteria.valueOf(PubItemVOComparator.Criteria.class, this.getSessionBean().getSortBy());
        
        if (logger.isDebugEnabled())
        {
            logger.debug("New sorting criteria: " + sortByCriteria.toString());
        }

        // instanciate the comparator with the sorting criteria
        PubItemVOComparator pubItemVOComparator = new PubItemVOComparator(sortByCriteria);
        
        // sort ascending or descending
        if (sortOrderString.equals((String)this.SORTORDER_ASCENDING.getValue()))
        {
            Collections.sort(itemList, pubItemVOComparator);
        }
        else if (sortOrderString.equals((String)this.SORTORDER_DESCENDING.getValue()))
        {
            Collections.sort(itemList, Collections.reverseOrder(pubItemVOComparator));
        }
        
        // refresh the item array in the session bean
        this.getSessionBean().setCurrentPubItemList(itemList);
        
        this.createDynamicItemList();
        
        return null;
    }

    /**
     * Adds and removes messages concerning item lists.
     * @author Michael Franke
     */
    public void handleMessage() 
    {

        String message = this.getSessionBean().getMessage();
        
        this.valMessage.setText(message);
        this.valMessage.setVisible(message != null);
        
        // keep the message just once
        this.getSessionBean().setMessage(null);
    }

    /**
     * Has to be implemented by the inheriting classes to return the specialized subclass for the SessionBean.
     * @return a reference to the scoped data bean
     */
    protected abstract ItemListSessionBean getSessionBean();

    /**
     * Returns a reference to the scoped data bean (the ItemControllerSessionBean). 
     * @return a reference to the scoped data bean
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean)getBean(ItemControllerSessionBean.BEAN_NAME);
    }

    /**
     * Returns a reference to the scoped data bean (the ViewItemSessionBean). 
     * @return a reference to the scoped data bean
     */
    protected ViewItemSessionBean getViewItemSessionBean()
    {
        return (ViewItemSessionBean)getBean(ViewItemSessionBean.BEAN_NAME);
    }

    /**
     * Returns a reference to the scoped data bean (the SubmitItemSessionBean). 
     * @return a reference to the scoped data bean
     */
    protected SubmitItemSessionBean getSubmitItemSessionBean()
    {
        return (SubmitItemSessionBean)getBean(SubmitItemSessionBean.BEAN_NAME);
    }

    /**
     * Returns a reference to the scoped data bean (the WithdrawItemSessionBean). 
     * @return a reference to the scoped data bean
     */
    protected WithdrawItemSessionBean getWithdrawItemSessionBean()
    {
        return (WithdrawItemSessionBean)getBean(WithdrawItemSessionBean.BEAN_NAME);
    }

    /**
     * Returns the panel for items.
     * @return the panel for the items
     */
    public HtmlPanelGrid getPanDynamicItemList()
    {
        return panDynamicItemList;
    }

    /**
     * Sets the panel for items.
     * @param panDynamicTitle the new panel
     */
    public void setPanDynamicItemList(HtmlPanelGrid panDynamicItemList)
    {
        this.panDynamicItemList = panDynamicItemList;
    }

    public Option[] getSORTBY_OPTIONS()
    {
        return this.SORTBY_OPTIONS;
    }

    public Option[] getSORTORDER_OPTIONS()
    {
        return this.SORTORDER_OPTIONS;
    }

    public StaticText getValMessage()
    {
        return valMessage;
    }

    public void setValMessage(StaticText valMessage)
    {
        this.valMessage = valMessage;
    }

    public DropDown getCboSortBy()
    {
        return cboSortBy;
    }

    public void setCboSortBy(DropDown cboSortBy)
    {
        this.cboSortBy = cboSortBy;
    }

    public RadioButtonGroup getRbgSortOrder()
    {
        return rbgSortOrder;
    }

    public void setRbgSortOrder(RadioButtonGroup rbgSortOrder)
    {
        this.rbgSortOrder = rbgSortOrder;
    }
    
    public ResourceBundle getBundleLabel()
    {
        return this.bundleLabel;
    }

    public void setBundleLabel(ResourceBundle bundleLabel)
    {
        this.bundleLabel = bundleLabel;
    }
}
