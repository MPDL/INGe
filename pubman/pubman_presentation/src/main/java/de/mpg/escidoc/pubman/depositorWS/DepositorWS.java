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
package de.mpg.escidoc.pubman.depositorWS;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlOutputLink;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemList;
import de.mpg.escidoc.pubman.ItemListSessionBean;
import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.editItem.EditItem;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.validation.ItemValidating;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportVO;

/**
 * Fragment class for the Depositor Workspace. This class provides all functionality for choosing one or more items out
 * of a list, depending on the status of the items. Items can be viewed, edited, deleted or submitted from this point.
 *
 * @author: Tobias Schraut; Thomas Diebäcker, created 10.01.2007
 * @version: $Revision: 1641 $ $LastChangedDate: 2007-12-04 16:52:04 +0100 (Di, 04 Dez 2007) $ Revised by DiT:
 *           09.08.2007
 */
public class DepositorWS extends ItemList
{
    public static final String BEAN_NAME = "DepositorWS";
    private static Logger logger = Logger.getLogger(DepositorWS.class);
    // Faces navigation string
    public static final String LOAD_DEPOSITORWS = "loadDepositorWS";
    // bound components in JSP
    private HtmlCommandLink lnkEdit = new HtmlCommandLink();
    private HtmlCommandLink lnkModify = new HtmlCommandLink();
    private HtmlCommandLink lnkWithdraw = new HtmlCommandLink();
    private HtmlCommandLink lnkView = new HtmlCommandLink();
    private HtmlCommandLink lnkSubmit = new HtmlCommandLink();
    private HtmlOutputLink lnkDelete = new HtmlOutputLink();
    private HtmlSelectOneMenu cboItemstate = new HtmlSelectOneMenu();
    private boolean enableNoItemMsg = false;

    /**
     * Public constructor.
     */
    public DepositorWS()
    {
        this.init();
    }

    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation.
     */
    public void init()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initializing DepositorWS...");
        }
        
        if (!"DepositorWS".equals(getItemListSessionBean().getType()))
        {
        	getItemListSessionBean().setListDirty(true);
        }
        
        // Perform initializations inherited from our superclass
        super.init();
        // create the itemList if neccessary
        if (this.getItemListSessionBean().isListDirty())
        {
            String retVal = this.createItemList(this.getSessionBean().getSelectedItemState());
            // if createItemList returns an error, force JSF to load the ErrorPage
            if (retVal == ErrorPage.LOAD_ERRORPAGE)
            {
                try
                {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("ErrorPage.jsp");
                }
                catch (Exception e)
                {
                    logger.error(e.toString(), e);
                }
            }
        }

    }

    /**
     * Shows an item identified by the itemID in the parameters of the FacesContext. This method is called when a user
     * directly clicks on a link of an item. @return string, identifying the page that should be navigated to after this
     * methodcall
     */
    public String showItem()
    {
        // force reload of list next time this page is navigated to
        this.getItemListSessionBean().setListDirty(true);
        return this.showItem(DepositorWS.LOAD_DEPOSITORWS);
    }

    /**
     * View the selected items. This method is called when the user selects one or more items and then clicks on the
     * view-link in the DepositorWS.
     *
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String viewItem()
    {
        // force reload of list next time this page is navigated to
        this.getItemListSessionBean().setListDirty(true);
        return this.viewItem(DepositorWS.LOAD_DEPOSITORWS);
    }

    /**
     * Edit the selected items.
     *
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String editItem()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Edit item(s)");
        }
        // set the currently selected items in the ItemController
//        this.setSelectedItemsAndCurrentItem();

        if (this.getItemListSessionBean().getSelectedPubItems().size() == 1)
        {
            //      Inserted by FrM to check item State
            for (PubItemVO item : this.getItemListSessionBean().getSelectedPubItems())
            {
                logger.debug("Checking item: " + item.getVersion().getObjectId() + ":" + item.getVersion().getState());
                if (item.getVersion().getState() != PubItemVO.State.PENDING && item.getVersion().getState() != PubItemVO.State.RELEASED)
                {
                    this.showMessage(DepositorWS.MESSAGE_WRONG_ITEM_STATE);
                    return null;
                }
                // TODO FrM: Survey this change.
                //getItemControllerSessionBean().setCurrentPubItem(new PubItemVO(item));
                getItemControllerSessionBean().setCurrentPubItem(item);
            }
            // force reload of list next time this page is navigated to
            this.getItemListSessionBean().setListDirty(true);
            return EditItem.LOAD_EDITITEM;
        }
        else if (this.getItemListSessionBean().getSelectedPubItems().size() > 1)
        {
            this.showMessage(DepositorWS.MESSAGE_MANY_ITEMS_SELECTED);
            return null;
        }
        else
        {
            this.showMessage(DepositorWS.MESSAGE_NO_ITEM_SELECTED);
            return null;
        }
    }

    /**
     * Submits the selected items. Changed by FrM: Inserted validation and call to "enter submission comment" page.
     *
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String submitSelectedItems()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Submit selected item(s)");
        }
        // set the currently selected items in the ItemController
//        this.setSelectedItemsAndCurrentItem();

        // Inserted by FrM to check item State
        for (PubItemVO item : this.getItemListSessionBean().getSelectedPubItems())
        {
            logger.debug("Checking item: " + item.getVersion().getObjectId() + ":" + item.getVersion().getState());
            if (item.getVersion().getState() != PubItemVO.State.PENDING)
            {
                this.showMessage(DepositorWS.MESSAGE_WRONG_ITEM_STATE);
                return null;
            }
        }

        if (this.getItemListSessionBean().getSelectedPubItems().size() == 1)
        {
            /*
             * FrM: Validation with validation point "submit_item"
             */
            ItemValidating itemValidating = null;
            try
            {
                InitialContext initialContext = new InitialContext();
                itemValidating = (ItemValidating) initialContext.lookup(ItemValidating.SERVICE_NAME);
            }
            catch (NamingException ne)
            {
                throw new RuntimeException("Validation service not initialized", ne);
            }
            PubItemVO pubItem = this.getItemListSessionBean().getSelectedPubItems().get(0);
            ValidationReportVO report = null;
            try
            {
                report = itemValidating.validateItemObject(pubItem, "submit_item");
            }
            catch (Exception e)
            {
                throw new RuntimeException("Validation error", e);
            }
            logger.debug("Validation Report: " + report);
            if (report.isValid() && !report.hasItems())
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Submitting item...");
                }
                getItemControllerSessionBean().setCurrentPubItem(pubItem);
                // force reload of list next time this page is navigated to
                this.getItemListSessionBean().setListDirty(true);
                return submitItem(DepositorWS.LOAD_DEPOSITORWS);
            }
            else if (report.isValid())
            {
                // force reload of list next time this page is navigated to
                this.getItemListSessionBean().setListDirty(true);
                // TODO FrM: Informative messages
                return submitItem(DepositorWS.LOAD_DEPOSITORWS);
            }
            else
            {
                // Item is invalid, do not submit anything.
                this.showMessage(DepositorWS.MESSAGE_NOT_SUCCESSFULLY_SUBMITTED);
                return null;
            }
        }
        else if (this.getItemListSessionBean().getSelectedPubItems().size() > 1)
        {
            this.showMessage(DepositorWS.MESSAGE_MANY_ITEMS_SELECTED);
            return null;
        }
        else
        {
            this.showMessage(DepositorWS.MESSAGE_NO_ITEM_SELECTED);
            return null;
        }
    }

    /**
     * Withdraws the selected item.
     *
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String withdrawSelectedItem()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Withdraw selected item");
        }

        //      Inserted by FrM to check item State
        for (PubItemVO item : this.getItemListSessionBean().getSelectedPubItems())
        {
            logger.debug("Checking item: " + item.getVersion().getObjectId() + ":" + item.getVersion().getState());
            if (item.getVersion().getState() != PubItemVO.State.RELEASED)
            {
                this.showMessage(DepositorWS.MESSAGE_WRONG_ITEM_STATE);
                return null;
            }
        }

        if (this.getItemListSessionBean().getSelectedPubItems().size() == 1)
        {
            // force reload of list next time this page is navigated to
            this.getItemListSessionBean().setListDirty(true);
            return withdrawItem(DepositorWS.LOAD_DEPOSITORWS);
        }
        else if (this.getItemListSessionBean().getSelectedPubItems().size() > 1)
        {
            this.showMessage(DepositorWS.MESSAGE_MANY_ITEMS_SELECTED);
            return null;
        }
        else
        {
            this.showMessage(DepositorWS.MESSAGE_NO_ITEM_SELECTED);
            return null;
        }
    }

    /**
     * Deletes the selected items.
     *
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String deleteSelectedItems()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Delete item(s)");
        }
        
        // Inserted by FrM to check item State
        for (PubItemVO item : this.getItemListSessionBean().getSelectedPubItems())
        {
            if (item.getVersion().getState() != PubItemVO.State.PENDING)
            {
                this.showMessage(DepositorWS.MESSAGE_WRONG_ITEM_STATE);
                return null;
            }
        }

        if (this.getItemListSessionBean().getSelectedPubItems().size() != 0)
        {
            String retVal = this.getItemControllerSessionBean().deletePubItemList(
                    this.getItemListSessionBean().getSelectedPubItems(), DepositorWS.LOAD_DEPOSITORWS);
            // normally it should be sufficient to return the retVal ("loadDepositorWS") and let the FacesNavigation
            // newly initialize this Page; for some reason this doesn't work and we have to initialize manually...
            this.init();
            // show message
            if (retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0)
            {
                this.showMessage(DepositorWS.MESSAGE_SUCCESSFULLY_DELETED);
            }
            // force reload of list next time this page is navigated to
            this.getItemListSessionBean().setListDirty(true);
            return retVal;
        }
        else
        {
            this.showMessage(DepositorWS.MESSAGE_NO_ITEM_SELECTED);
            return null;
        }
    }

    /**
     * Called when the itemState is changed. Creates a new list according to the new state.
     *
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String changeItemState()
    {
        // force reload of list next time this page is navigated to
        this.getItemListSessionBean().setListDirty(true);
        String newItemState = ((String) this.cboItemstate.getSubmittedValue());
        return (this.createItemList(newItemState));
    }

    /**
     * Creates a new itemList in the FacesBean and forces the UI component to create a new itemList.
     *
     * @param newItemState the new state
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    private String createItemList(final String newItemState)
    {
        // clear all itemLists stored in the FacesBean
        getItemListSessionBean().getCurrentPubItemList().clear();
        getItemListSessionBean().getSelectedPubItems().clear();
        getItemListSessionBean().setCurrentPubItemListPointer(0);
        ArrayList<PubItemVO> itemsForAccountUser = new ArrayList<PubItemVO>();
        // set the new State in the FacesBean
        if (logger.isDebugEnabled())
        {
            logger.debug("New item state: " + newItemState);
        }
        this.getSessionBean().setSelectedItemState(newItemState);
        // retrieve the items
        try
        {
            itemsForAccountUser = this.getItemControllerSessionBean().retrieveItems(
                    this.getSessionBean().getSelectedItemState());
        }
        catch (Exception e)
        {
            logger.error("Could not create item list.", e);
            ((ErrorPage) this.getBean(ErrorPage.class)).setException(e);
            return ErrorPage.LOAD_ERRORPAGE;
        }
        // set new list in FacesBean
        this.getItemListSessionBean().setCurrentPubItemList(CommonUtils.convertToPubItemVOPresentationList(itemsForAccountUser));
        // sort the items and force the UI to update
        this.sortItemList();
        // enable or disable the action links according to item state and availability of items
        this.enableLinks(this.getSessionBean().getSelectedItemState(), itemsForAccountUser.size());
        // no reload necessary next time this page is navigated to
        this.getItemListSessionBean().setListDirty(false);
        this.getItemListSessionBean().setType("DepositorWS");
        getViewItemSessionBean().setNavigationStringToGoBack(DepositorWS.LOAD_DEPOSITORWS);
        return DepositorWS.LOAD_DEPOSITORWS;
    }

    /**
     * Creates the panel newly according to the values in the FacesBean.
     */
    protected void createDynamicItemList2()
    {
        if (this.getItemListSessionBean().getCurrentPubItemList() != null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Creating dynamic item list with " + this.getItemListSessionBean().getCurrentPubItemList().size()
                        + " entries.");
            }
            // create an ItemListUI for all PubItems
            List<PubItemVOPresentation> pubItemList = this.getItemListSessionBean().getCurrentPubItemList();

        }
    }

    /**
     * Enables or disables the action links according to item state and availability of items.
     *
     * @param itemState the currently selected state
     * @param itemListSize the size of the list displayed
     */
    protected void enableLinks(final String itemState, final int itemListSize)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Enable links and messages...");
        }
        boolean enableDelete = (itemListSize > 0 && ("all".equals(itemState) || itemState
                .equals(PubItemVO.State.PENDING.toString())));
        boolean enableEdit = (itemListSize > 0 && ("all".equals(itemState) || itemState.equals(PubItemVO.State.PENDING
                .toString())));
        boolean enableSubmit = (itemListSize > 0 && ("all".equals(itemState) || itemState
                .equals(PubItemVO.State.PENDING.toString())));
        boolean enableWithdraw = (itemListSize > 0 && ("all".equals(itemState) || itemState
                .equals(PubItemVO.State.RELEASED.toString())));
        boolean enableModify = (itemListSize > 0 && ("all".equals(itemState) || itemState
                .equals(PubItemVO.State.RELEASED.toString())));
        boolean enableView = (itemListSize > 0);
        enableNoItemMsg = (itemListSize <= 0);
        this.lnkDelete.setRendered(enableDelete);
        this.lnkEdit.setRendered(enableEdit);
        this.lnkModify.setRendered(enableModify);
        this.lnkWithdraw.setRendered(enableWithdraw);
        this.lnkSubmit.setRendered(enableSubmit);
        this.lnkView.setRendered(enableView);
    }

    /**
     * Returns the ItemListSessionBean.
     *
     * @return a reference to the scoped data bean (ItemListSessionBean)
     */
    protected ItemListSessionBean getItemListSessionBean()
    {
        return (ItemListSessionBean) getSessionBean(ItemListSessionBean.class);
    }

    /**
     * Returns the DepositorWSSessionBean.
     *
     * @return a reference to the scoped data bean (DepositorWSSessionBean)
     */
    protected DepositorWSSessionBean getSessionBean()
    {
        return (DepositorWSSessionBean) getSessionBean(DepositorWSSessionBean.class);
    }

    /**
     * Returns the ContextListSessionBean.
     *
     * @return a reference to the scoped data bean (ContextListSessionBean)
     */
    protected ContextListSessionBean getCollectionListSessionBean()
    {
        return (ContextListSessionBean)getBean(ContextListSessionBean.class);
    }

    /**
     * Returns the ApplicationBean.
     *
     * @return a reference to the scoped data bean (ApplicationBean)
     */
    protected ApplicationBean getApplicationBean()
    {
        return (ApplicationBean) getBean(ApplicationBean.class);
    }

    public SelectItem[] getITEMSTATE_OPTIONS()
    {
        return this.getApplicationBean().getSelectItemsItemState();
    }

    public HtmlOutputLink getLnkDelete()
    {
        return lnkDelete;
    }

    public void setLnkDelete(HtmlOutputLink lnkDelete)
    {
        this.lnkDelete = lnkDelete;
    }

    public HtmlCommandLink getLnkEdit()
    {
        return lnkEdit;
    }

    public HtmlCommandLink getLnkModify()
    {
        return lnkModify;
    }

    public void setLnkModify(HtmlCommandLink lnkModify)
    {
        this.lnkModify = lnkModify;
    }

    public HtmlCommandLink getLnkWithdraw()
    {
        return lnkWithdraw;
    }

    public void setLnkWithdraw(HtmlCommandLink lnkWithdraw)
    {
        this.lnkWithdraw = lnkWithdraw;
    }

    public void setLnkEdit(HtmlCommandLink lnkEdit)
    {
        this.lnkEdit = lnkEdit;
    }

    public HtmlCommandLink getLnkSubmit()
    {
        return lnkSubmit;
    }

    public void setLnkSubmit(HtmlCommandLink lnkSubmit)
    {
        this.lnkSubmit = lnkSubmit;
    }

    public HtmlCommandLink getLnkView()
    {
        return lnkView;
    }

    public void setLnkView(HtmlCommandLink lnkView)
    {
        this.lnkView = lnkView;
    }

    public HtmlSelectOneMenu getCboItemstate()
    {
        return cboItemstate;
    }

    public void setCboItemstate(HtmlSelectOneMenu cboItemstate)
    {
        this.cboItemstate = cboItemstate;
    }

	public boolean getEnableNoItemMsg() {
		return enableNoItemMsg;
	}

	public void setEnableNoItemMsg(boolean enableNoItemMsg) {
		this.enableNoItemMsg = enableNoItemMsg;
	}

}
