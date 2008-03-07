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

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectBoolean;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.component.html.HtmlSelectOneRadio;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.depositorWS.DepositorWS;
import de.mpg.escidoc.pubman.search.SearchResultList;
import de.mpg.escidoc.pubman.submitItem.SubmitItem;
import de.mpg.escidoc.pubman.submitItem.SubmitItemSessionBean;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.pubman.viewItem.ViewItemFull;
import de.mpg.escidoc.pubman.viewItem.ViewItemSessionBean;
import de.mpg.escidoc.pubman.withdrawItem.WithdrawItem;
import de.mpg.escidoc.pubman.withdrawItem.WithdrawItemSessionBean;
import de.mpg.escidoc.services.common.valueobjects.comparator.PubItemVOComparator;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * Superclass for all classes dealing with item lists (e.g. DepositorWS, SearchResultList)
 * This class provides all functionality for showing, sorting and choosing one or more items out of a list.
 *
 * @author:  Thomas Diebäcker, created 10.05.2007
 * @version: $Revision: 1695 $ $LastChangedDate: 2007-12-18 14:25:56 +0100 (Di, 18 Dez 2007) $
 * Revised by DiT: 14.08.2007
 */
public abstract class ItemList extends FacesBean
{
    private static Logger logger = Logger.getLogger(ItemList.class);

    // bound components in JSP
    private String valMessage = null;
    private boolean showMessage = false;
    private HtmlSelectOneMenu cboSortBy = new HtmlSelectOneMenu();
    private HtmlSelectOneRadio rbgSortOrder = new HtmlSelectOneRadio();

    // constants for comboBoxes
    //public SelectItem[] SORTBY_OPTIONS = null;
    public SelectItem SORTORDER_ASCENDING = null;
    public SelectItem SORTORDER_DESCENDING = null;
    public SelectItem[] SORTORDER_OPTIONS = null;

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

    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation.
     */
    public void init()
    {
        super.init();

        //SORTBY_OPTIONS = getSelectItemArrayFromEnum(PubItemVOComparator.Criteria.class);

        SORTORDER_ASCENDING = new SelectItem("ASCENDING", getLabel("ENUM_SORTORDER_ASCENDING"));
        SORTORDER_DESCENDING = new SelectItem("DESCENDING", getLabel("ENUM_SORTORDER_DESCENDING"));
        SORTORDER_OPTIONS = new SelectItem[]{SORTORDER_ASCENDING, SORTORDER_DESCENDING};
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
        ArrayList<PubItemVOPresentation> selectedPubItems = new ArrayList<PubItemVOPresentation>();

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
            PubItemVOPresentation puItemVO = CommonUtils.getItemByID(this.getItemListSessionBean().getCurrentPubItemList(), itemID);
            this.getItemControllerSessionBean().setCurrentPubItem(puItemVO);            
            selectedPubItems.add(puItemVO);
            // set all selectedItems in FacesBean
//            this.getSessionBean().setSelectedPubItems(selectedPubItems);

            // initialize viewItem
            this.getViewItemSessionBean().setNavigationStringToGoBack(navigationStringToGoBack);
            this.getViewItemSessionBean().setItemListSessionBean(getItemListSessionBean());

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
            if (this.getItemListSessionBean().getSelectedPubItems().size() != 0)
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
     * This method is called when the user selects one or more items and
     * then clicks on the view-link in the DepositorWS.
     * @param navigationStringToGoBack the navigationString that should be returned
     * when in ViewItem the user wants to go back to the list he came from.
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String viewItem(final String navigationStringToGoBack)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("View item(s)");
        }

        // set the currently selected items in the FacesBean
//        this.setSelectedItemsAndCurrentItem();

        List<PubItemVOPresentation> selectedItems = this.getItemListSessionBean().getSelectedPubItems();
        
        if (selectedItems.size() != 0)
        {
            // initialize viewItem
            this.getViewItemSessionBean().setNavigationStringToGoBack(navigationStringToGoBack);
            this.getViewItemSessionBean().setItemListSessionBean(getItemListSessionBean());

            try
            {
            	String viewItemPage = PropertyReader.getProperty("escidoc.pubman.item.pattern").replaceFirst("\\$1", selectedItems.get(0).getReference().getObjectId());
            	FacesContext.getCurrentInstance().getExternalContext().redirect(viewItemPage);
            }
            catch (Exception e) {
				logger.error("Error building view item page url", e);
			}
            return null;
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
     * This method is called when the user selects one or more items and
     * then clicks on the submit-link in the DepositorWS.
     * @author Michael Franke
     * @param navigationStringToGoBack the navigationString that should be
     *  returned when in ViewItem the user wants to go back to the list he came from.
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
     * This method is called when the user selects one or more items and then clicks
     * on the withdraw-link in the DepositorWS.
     * 
     * @author Michael Franke
     * @param navigationStringToGoBack the navigationString that should be returned
     * when in ViewItem the user wants to go back to the list he came from. 
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String withdrawItem(String navigationStringToGoBack)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Withdraw item");
        }

        this.getWithdrawItemSessionBean().setNavigationStringToGoBack(navigationStringToGoBack);
        this.getWithdrawItemSessionBean().setItemListSessionBean(getItemListSessionBean());

        return WithdrawItem.LOAD_WITHDRAWITEM;
    }

    /**
     * Shows the given Message below the itemList.
     * 
     * @param message the message to be displayed
     */
    public void showMessage(String message)
    {
        message = getMessage(message);
        this.getItemListSessionBean().setMessage(message);

        // instantly make this message visible as the page is likely not reloaded and so
        // the message is not set visible via the enableLinks()-method.
        this.valMessage = message;
        this.showMessage = true;
    }

    /**
     * Removes the message below the itemList
     */
    public void deleteMessage()
    {
    	this.valMessage = null;
        this.showMessage = false;
    }

    /**
     * Creates the panel newly according to the values in the itemArray.
     */
    protected abstract void createDynamicItemList2();

    /**
     * Sorts the result item list.
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String sortItemList()
    {
        List<PubItemVOPresentation> itemList = this.getItemListSessionBean().getCurrentPubItemList();

        // get the sorting order by the FacesBean
        String sortOrderString = this.getItemListSessionBean().getSortOrder(); 

        if (logger.isDebugEnabled())
        {
            logger.debug("New sort order: " + sortOrderString);
        }

        // get the sorting criteria by the FacesBean
        PubItemVOComparator.Criteria sortByCriteria = PubItemVOComparator.Criteria.valueOf(PubItemVOComparator.Criteria.class, this.getItemListSessionBean().getSortBy());

        if (logger.isDebugEnabled())
        {
            logger.debug("New sorting criteria: " + sortByCriteria.toString());
        }

        // instanciate the comparator with the sorting criteria
        PubItemVOComparator pubItemVOComparator = new PubItemVOComparator(sortByCriteria);

        // sort ascending or descending
        if (sortOrderString.equals((String) this.SORTORDER_ASCENDING.getValue()))
        {
            Collections.sort(itemList, pubItemVOComparator);
        }
        else if (sortOrderString.equals((String) this.SORTORDER_DESCENDING.getValue()))
        {
            Collections.sort(itemList, Collections.reverseOrder(pubItemVOComparator));
        }

        // refresh the item array in the session bean
        this.getItemListSessionBean().setCurrentPubItemList(itemList);

        //this.createDynamicItemList();

        return null;
    }

    /**
     * Adds and removes messages concerning item lists.
     * @author Michael Franke
     */
    public void handleMessage() 
    {

        String message = this.getItemListSessionBean().getMessage();

        this.valMessage = message;
        this.showMessage = (message != null);

        // keep the message just once
        this.getItemListSessionBean().setMessage(null);
    }

    /**
     * Has to be implemented by the inheriting classes to return the specialized subclass for the FacesBean.
     * @return a reference to the scoped data bean
     */
    protected abstract ItemListSessionBean getItemListSessionBean();

    /**
     * Returns a reference to the scoped data bean (the ItemControllerSessionBean). 
     * @return a reference to the scoped data bean
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean)getBean(ItemControllerSessionBean.class);
    }

    /**
     * Returns a reference to the scoped data bean (the ViewItemSessionBean). 
     * @return a reference to the scoped data bean
     */
    protected ViewItemSessionBean getViewItemSessionBean()
    {
        return (ViewItemSessionBean)getBean(ViewItemSessionBean.class);
    }

    /**
     * Returns a reference to the scoped data bean (the SubmitItemSessionBean). 
     * @return a reference to the scoped data bean
     */
    protected SubmitItemSessionBean getSubmitItemSessionBean()
    {
        return (SubmitItemSessionBean)getBean(SubmitItemSessionBean.class);
    }

    /**
     * Returns a reference to the scoped data bean (the WithdrawItemSessionBean). 
     * @return a reference to the scoped data bean
     */
    protected WithdrawItemSessionBean getWithdrawItemSessionBean()
    {
        return (WithdrawItemSessionBean)getBean(WithdrawItemSessionBean.class);
    }

    public SelectItem[] getSORTORDER_OPTIONS()
    {
        return this.SORTORDER_OPTIONS;
    }

    public HtmlSelectOneMenu getCboSortBy()
    {
        return cboSortBy;
    }

    public void setCboSortBy(HtmlSelectOneMenu cboSortBy)
    {
        this.cboSortBy = cboSortBy;
    }

    public HtmlSelectOneRadio getRbgSortOrder()
    {
        return rbgSortOrder;
    }

    public void setRbgSortOrder(HtmlSelectOneRadio rbgSortOrder)
    {
        this.rbgSortOrder = rbgSortOrder;
    }

    /**
     * Generate the options for a dropdown by defining an enum.
     *
     * @param enumClass The enum to fill the list with.
     * @return An arry of SelectItems containing the content of the given enum.
     */
    public SelectItem[] getSelectItemArrayFromEnum(final Class enumClass)
    {
        ArrayList<SelectItem> list = new ArrayList<SelectItem>();
        for (Object element : enumClass.getEnumConstants())
        {
            list.add(
                    new SelectItem(
                            element.toString(),
                            getLabel("ENUM_" + enumClass.getSimpleName().toUpperCase() + "_" + element)));
        }
        return list.toArray(new SelectItem[]{});
    }

	public String getValMessage() {
		return valMessage;
	}

	public void setValMessage(String valMessage) {
		this.valMessage = valMessage;
	}

	public boolean getShowMessage() {
		return showMessage;
	}

	public void setShowMessage(boolean showMessage) {
		this.showMessage = showMessage;
	}

}
