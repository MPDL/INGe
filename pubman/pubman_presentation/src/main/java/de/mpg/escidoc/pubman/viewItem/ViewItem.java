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

package de.mpg.escidoc.pubman.viewItem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIColumn;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.html.HtmlMessages;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.CommonSessionBean;
import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.ItemListSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.createItem.CreateItem;
import de.mpg.escidoc.pubman.depositorWS.DepositorWS;
import de.mpg.escidoc.pubman.desktop.Login;
import de.mpg.escidoc.pubman.editItem.EditItem;
import de.mpg.escidoc.pubman.search.SearchResultList;
import de.mpg.escidoc.pubman.search.SearchResultListSessionBean;
import de.mpg.escidoc.pubman.submitItem.SubmitItem;
import de.mpg.escidoc.pubman.submitItem.SubmitItemSessionBean;
import de.mpg.escidoc.pubman.util.AffiliationVOPresentation;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.ObjectFormatter;
import de.mpg.escidoc.pubman.viewItem.ui.FileUI;
import de.mpg.escidoc.pubman.viewItem.ui.SourceUI;
import de.mpg.escidoc.pubman.withdrawItem.WithdrawItem;
import de.mpg.escidoc.pubman.withdrawItem.WithdrawItemSessionBean;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.validation.ItemValidating;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportItemVO;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportVO;

/**
 * This class provides all functionality for viewing one specified item.
 * 
 * @author Tobias Schraut (created on 08.02.2007)
 * @author $Author: tdiebaec $ (last modification)
 * @version $Revision: 1632 $ $LastChangedDate: 2007-11-29 15:01:44 +0100 (Do, 29 Nov 2007) $ Revised by ScT: 30.08.2007
 */
public class ViewItem extends FacesBean
{
    // <editor-fold defaultstate="collapsed" desc="Creator-managed Component
    // Definition">
    private static Logger logger = Logger.getLogger(ViewItem.class);
    final public static String BEAN_NAME = "ViewItem";
    // Faces navigation string
    public final static String LOAD_VIEWITEM = "loadViewItem";

    private String valWithdrawalComment;
    private HtmlMessages valMessage = new HtmlMessages();
    public static final String PARAMETERNAME_ITEM_ID = "itemId";

    // Validation Service
    private ItemValidating itemValidating = null; 
    
    /**
     * The Text for the more button of the event titles of the item.
     */
    private String buttonMoreItemEventTitles;
    /**
     * The list of formatted event titles in an ArrayList.
     */
    private ArrayList<String> eventTitleArray;
    /**
     * number of event titles which should be displayed when shown collapsed.
     */
    private int numberEventTitlesCollapsed;
    /**
     * a flag for showing the event titles collapsed or expanded.
     */
    private boolean eventTitlesCollapsed;
    /**
     * The title of the item to bookmark
     */
    private String itemBookmarkTitle;
    /**
     * The URL where the item can be retrieved
     */
    private String itemCitation;
    /**
     * ArrayList for the UI Component to examine which embedded elements of the Source are collapsed or expanded.
     */
    private ArrayList<ViewItemSource> itemSourceList;
    /**
     * Panel grid for dynamic Source output.
     */
    private HtmlPanelGrid panSources = new HtmlPanelGrid();
    /**
     * Panel grid for dynamic File output.
     */
    private HtmlPanelGrid panFiles = new HtmlPanelGrid();
    /**
     * The source(s) of the item.
     */
    private ArrayList<SourceVO> sourceArray;
    /**
     * The file(s) of the item.
     */
    private ArrayList<FileVO> fileArray;
    private UIColumn fileColumn = new UIColumn();
    private HtmlOutputText fileText = new HtmlOutputText();
    private HtmlDataTable fileTable = new HtmlDataTable();
    /**
     * the owner of the item.
     */
    private AccountUserVO owner;
    /**
     * A ViewItemOrganization object for displaying the organization�s information.
     */
    private ViewItemOrganization viewOrganisation;
    /**
     * The abstract(s) of the item.
     */
    private ArrayList<String> abstractsArray;
    private UIColumn abstractsColumn = new UIColumn();
    private HtmlOutputText abstractsText = new HtmlOutputText();
    private HtmlDataTable abstractsTable = new HtmlDataTable();
    /**
     * The Text for the more button of the abstract.
     */
    private String buttonMoreAbstracts;
    /**
     * a flag for showing the abstract collapsed or expanded.
     */
    private boolean abstractsCollapsed;
    /**
     * number of characters which should be displayed when abstract is shown collapsed.
     */
    private int numberCharsAbstractsCollapsed;
    /**
     * The list of formatted relations of the item.
     */
    private ArrayList<String> relationArray;
    private UIColumn relationColumn = new UIColumn();
    private HtmlOutputText relationText = new HtmlOutputText();
    private HtmlDataTable relationTable = new HtmlDataTable();
    /**
     * The list of formatted identifiers of the item.
     */
    private ArrayList<String> itemIdentifierArray;
    private UIColumn itemIdentifierColumn = new UIColumn();
    private HtmlOutputText itemIdentifierText = new HtmlOutputText();
    private HtmlDataTable itemIdentifierTable = new HtmlDataTable();
    /**
     * The the content language of the item.
     */
    private String contentLanguage;
    /**
     * The list of the alternative titles of the item.
     */
    private ArrayList<String> alternativeTitlesArray;
    /**
     * The Text for the more button of the alternative titles of the item.
     */
    private String buttonMoreItemAlternativeTitles;
    /**
     * a flag for showing the alternative titles collapsed or expanded.
     */
    private boolean itemAlternativeTitlesCollapsed;
    /**
     * number of entries which should be displayed when alternative titles is shown collapsed.
     */
    private int numberItemAlternativeTitlesCollapsed;
    /**
     * The affiliation of the context.
     */
    private String affiliationOfContext;
    /**
     * The table of contents of the item.
     */
    private String tableOfContents;
    /**
     * The Text for the more button of the table of contents.
     */
    private String buttonMoreTOC;
    /**
     * a flag for showing the TOC collapsed or expanded.
     */
    private boolean tocCollapsed;
    /**
     * number of characters which should be displayed when toc is shown collapsed.
     */
    private int numberCharsTOCCollapsed;
    /**
     * The Text for the more button of the creators list.
     */
    private String buttonMoreCreatorList;
    /**
     * The PubItem to be displayed.
     */
    private PubItemVO pubItem;
    /**
     * The related PubCollection.
     */
    private ContextVO context;
    /**
     * The list of creators in one string.
     */
    private String creators;
    /**
     * The list of creators in a list.
     */
    private List<CreatorVO> creatorsList;
    /**
     * The list of formatted creators in an ArrayList.
     */
    private ArrayList<String> creatorArray;
    /**
     * The list of formatted creators which are organizations in an ArrayList.
     */
    private ArrayList<ViewItemCreatorOrganization> creatorOrganizationsArray;
    /**
     * The list of affiliated organizations as VO List.
     */
    private ArrayList<ViewItemOrganization> organizationList;
    /**
     * The list of affiliated organizations in one string.
     */
    private String affiliatedOrganizations;
    /**
     * The list of affiliated organizations in a list.
     */
    private List<OrganizationVO> affiliatedOrganizationsList;
    /**
     * The list of formatted organzations in an ArrayList.
     */
    private ArrayList<String> organizationArray;
    /**
     * a flag for showing the creatorList collapsed or expanded.
     */
    private boolean creatorListCollapsed;
    /**
     * number of ceators which should be displayed when shown collapsed.
     */
    private int numberCreatorListCollapsed;
    // the pub context the item is related to
    private HtmlCommandButton btnPreviousItem1 = new HtmlCommandButton();
    private HtmlCommandButton btnNextItem1 = new HtmlCommandButton();
    private HtmlCommandButton btnBackToList1 = new HtmlCommandButton();
    private HtmlCommandLink lnkEdit = new HtmlCommandLink();
    private HtmlCommandLink lnkSubmit = new HtmlCommandLink();
    private HtmlCommandLink lnkDelete = new HtmlCommandLink();
    private HtmlCommandLink lnkWithdraw = new HtmlCommandLink();
    private HtmlCommandLink lnkNewSubmission = new HtmlCommandLink();

    /**
     * Public constructor
     */
    public ViewItem()
    {
        this.init();
    }

    /**
     * Callback method that is called whenever a page is navigated to, either directly via a URL, or indirectly via page
     * navigation.
     */
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
        // set some initial values
        this.numberCreatorListCollapsed = 1;
        this.numberCharsTOCCollapsed = 300;
        this.numberItemAlternativeTitlesCollapsed = 1;
        this.numberCharsAbstractsCollapsed = 300;
        
        try
        {
            InitialContext initialContext = new InitialContext();
            this.itemValidating = (ItemValidating)initialContext.lookup(ItemValidating.SERVICE_NAME);
        }
        catch (NamingException ne)
        {
            throw new RuntimeException("Validation service not initialized", ne);
        }
    }

    /**
     * gets the parameters out of the faces context
     * 
     * @param name name of the parameter in the faces context
     * @return the value of the parameter as string
     */
    public static String getFacesParamValue(String name)
    {
        return (String)FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(name);
    }

    /**
     * Fetches the requested pubitem from the Framework and prepares it to display. The method also distinguishs between
     * the different possibilities of getting the item's ID (out of the faces context or as URL parameter)
     * 
     * @return String navigation string (null for just reloadinh the page)
     */
    public String loadItem()
    {
        // initially set the number of elements which should be displayed if the
        // property is collapsed
        this.numberCreatorListCollapsed = 1;
        this.numberCharsTOCCollapsed = 300;
        this.numberItemAlternativeTitlesCollapsed = 1;
        this.numberCharsAbstractsCollapsed = 300;
        this.numberEventTitlesCollapsed = 1;
        // set the buttons disabled (if no list is available to be thrown back)
        this.btnBackToList1.setDisabled(this.getViewItemSessionBean().getNavigationStringToGoBack() == null);
        LoginHelper loginHelper = (LoginHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver()
                .resolveVariable(FacesContext.getCurrentInstance(), "LoginHelper");
        Login login = (Login)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(
                FacesContext.getCurrentInstance(), Login.BEAN_NAME);
        if (login == null)
        {
            login = new Login();
        }
        // enable or disable the action links according to the login state
        if (loginHelper.getESciDocUserHandle() != null)
        {
            this.lnkNewSubmission.setRendered(true);
            this.lnkEdit.setRendered(true);
            this.lnkSubmit.setRendered(true);
            this.lnkDelete.setRendered(true);
            this.lnkWithdraw.setRendered(true);
        }
        else
        {
            this.lnkNewSubmission.setRendered(false);
            this.lnkEdit.setRendered(false);
            this.lnkSubmit.setRendered(false);
            this.lnkDelete.setRendered(false);
            this.lnkWithdraw.setRendered(false);
        }
        String itemID = "";
        // try to get the itemID from the session Bean (URL Parameter)
        if (this.getViewItemSessionBean().getItemIdViaURLParam() != null)
        {
            itemID = this.getViewItemSessionBean().getItemIdViaURLParam();
        }
        // otherwise get the ID of the current item from ItemController
        else
        {
            itemID = this.getItemControllerSessionBean().getCurrentPubItem().getVersion().getObjectId();
        }
        // setting the URL parameter for redirecting in authentication error
        // case
        try
        {
            this.pubItem = this.getItemControllerSessionBean().retrieveItem(itemID);
        }
        catch (Exception e)
        {
            logger.error("Could not retrieve the requested item." + "\n" + e.toString());
            ((ErrorPage)getBean(ErrorPage.class)).setException(e);
            return ErrorPage.LOAD_ERRORPAGE;
        }
        // set the action links in the action menu according to the item state
        if (this.pubItem.getVersion().getState().toString().equals(PubItemVO.State.RELEASED.toString()))
        {
            this.lnkDelete.setRendered(false);
            this.lnkEdit.setRendered(false);
            this.lnkSubmit.setRendered(false);
            if (loginHelper.getESciDocUserHandle() != null)
            {
                this.lnkWithdraw.setRendered(true);
            }
            else
            {
                this.lnkWithdraw.setRendered(false);
            }
        }
        else if (this.pubItem.getVersion().getState().toString().equals(PubItemVO.State.SUBMITTED.toString())
                || this.pubItem.getVersion().getState().toString().equals(PubItemVO.State.WITHDRAWN.toString()))
        {
            this.lnkDelete.setRendered(false);
            this.lnkEdit.setRendered(false);
            this.lnkSubmit.setRendered(false);
            this.lnkWithdraw.setRendered(false);
        }
        else
        {
            this.lnkDelete.setRendered(true);
            this.lnkEdit.setRendered(true);
            this.lnkSubmit.setRendered(true);
            this.lnkWithdraw.setRendered(false);
        }
        // set the collapsed flags to true if this is the first call of the page
        this.creatorListCollapsed = true;
        this.buttonMoreCreatorList = getLabel("ViewItem_lnkCreatorMore");
        this.tocCollapsed = true;
        this.buttonMoreTOC = getLabel("ViewItem_lnkTocMore");
        this.itemAlternativeTitlesCollapsed = true;
        this.buttonMoreItemAlternativeTitles = getLabel("ViewItem_lnkAlternativeTitleMore");
        this.abstractsCollapsed = true;
        this.buttonMoreAbstracts = getLabel("ViewItem_lnkAbstractMore");
        // set the event titles to collapsed
        this.eventTitlesCollapsed = true;
        // initialize the table of contents
        this.tableOfContents = getTOC();
        // get the withdrawal informationif item is withdrawn
        this.valWithdrawalComment = getWithdrawalComment();
        // initialize the alternative titles of the item
        this.alternativeTitlesArray = getAlternativeTitles();
        // initialize the identifiers of the item
        this.itemIdentifierArray = getItemIdentifiers();
        // intialize the relations of the item
        this.relationArray = getRelations();
        // intialize the abstract(s) of the item
        this.abstractsArray = getAbstract();
        // intialize expand / collapse link for the event titles
        this.buttonMoreItemEventTitles = "";
        // intialize the event title(s) of the item
        this.eventTitleArray = getEventTitles();
        // initialize the file list of the item
        this.fileArray = getFiles();
        // intialize the list of collapse / expand information for the sources
        // and their embedded elements
        initializeItemSourceView();
        // initialize the source list of the item
        this.sourceArray = getSources();
        // get the content languages
        this.contentLanguage = getLanguages();
        // get the referenced PubCollection
        try
        {
            this.context = this.getItemControllerSessionBean().retrieveContext(
                    this.pubItem.getContext().getObjectId());
        }
        catch (Exception e)
        {
            logger.error("Could not retrieve the requested collection." + "\n" + e.toString());
            ((ErrorPage)getBean(ErrorPage.class)).setException(e);
            return ErrorPage.LOAD_ERRORPAGE;
        }
        // get the affiliated organization
        // NOTE: For R1 only retrieve the first affiliation, List is only for R2
        // TODO (ScT): Change in R2 to get all affiliations!
        try
        {
            // this.affiliationOfCollection = getAffiliation(this.pubCollection
            // .getResponsibleAffiliations().get(0).getObjectId());
            // TODO ScT: Workaround, solange der Bug im Framework noch besteht, der im obigen Ausdruck
            // "escidoc:persistent3" liefert
            this.affiliationOfContext = getAffiliation("escidoc:persistent1");
        }
        catch (Exception e)
        {
            logger.error("Could not retrieve the requested affiliation." + "\n" + e.toString());
            ((ErrorPage)getBean(ErrorPage.class)).setException(e);
            return ErrorPage.LOAD_ERRORPAGE;
        }
        // fetch referenced organizations
        this.affiliatedOrganizations = "";
        getAffiliatedOrganizationList();
        // fetch creators according to the collapsed flag
        this.creators = "";
        this.creators = getCreatorList();
        this.createFiles();
        this.createSources();
        // set the item citation URL
        this.itemCitation = getItemCitationURL();
        // set the title of the item bookmark
        this.itemBookmarkTitle = getItemBMTitle();
        this.btnPreviousItem1.setDisabled(false);
        this.btnNextItem1.setDisabled(false);
        // Disable back and previous buttons according to the psoition the
        // viewed item is in the list
        if (this.getViewItemSessionBean().getNavigationStringToGoBack().equals(DepositorWS.LOAD_DEPOSITORWS))
        {
            if (this.getItemListSessionBean().getSelectedPubItems().size() > 0)
            {
                // disable the 'previous' button if the currently viewed item is
                // the first in the list
                if (this.pubItem.getVersion().getObjectId().equals(
                        this.getItemListSessionBean().getSelectedPubItems().get(0).getVersion().getObjectId()))
                {
                    this.btnPreviousItem1.setDisabled(true);
                }
                if (this.pubItem.getVersion().getObjectId().equals(
                        this.getItemListSessionBean().getSelectedPubItems().get(
                                this.getItemListSessionBean().getSelectedPubItems().size() - 1).getVersion()
                                .getObjectId()))
                {
                    this.btnNextItem1.setDisabled(true);
                }
            }
        }
        else if (this.getViewItemSessionBean().getNavigationStringToGoBack().equals(
                SearchResultList.LOAD_SEARCHRESULTLIST))
        {
            if (this.getItemListSessionBean().getSelectedPubItems().size() > 0)
            {
                if (this.pubItem.getVersion().getObjectId()
                        .equals(
                                this.getItemListSessionBean().getSelectedPubItems().get(0).getVersion()
                                        .getObjectId()))
                {
                    this.btnPreviousItem1.setDisabled(true);
                }
                if (this.pubItem.getVersion().getObjectId().equals(
                        this.getItemListSessionBean().getSelectedPubItems().get(
                                this.getItemListSessionBean().getSelectedPubItems().size() - 1).getVersion()
                                .getObjectId()))
                {
                    this.btnNextItem1.setDisabled(true);
                }
            }
        }
        return null;
    }

    /**
     * generates the creator list as one string for presenting it in the jsp
     * 
     * @return String formatted creator list as string
     */
    private String getCreatorList()
    {
        StringBuffer creatorList = new StringBuffer();
        String formattedCreator = "";
        this.creatorArray = new ArrayList<String>();
        this.creatorOrganizationsArray = new ArrayList<ViewItemCreatorOrganization>();
        // counter for organization array
        int counterOrganization = 0;
        StringBuffer annotation;
        ObjectFormatter formatter = new ObjectFormatter();
        // if creator list should be displayed colapsed, only the first creator
        // is selected. Otherwise all creators are
        // fetched
        if (this.creatorListCollapsed == true)
        {
            for (int i = 0; i < this.numberCreatorListCollapsed; i++)
            {
                CreatorVO creator = new CreatorVO();
                creator = this.pubItem.getMetadata().getCreators().get(i);
                annotation = new StringBuffer();
                int organizationsFound = 0;
                for (int j = 0; j < this.affiliatedOrganizationsList.size(); j++)
                {
                    if (creator.getPerson() != null)
                    {
                        if (creator.getPerson().getOrganizations().contains(this.affiliatedOrganizationsList.get(j)))
                        {
                            if (organizationsFound == 0)
                            {
                                annotation.append("   [");
                            }
                            if (organizationsFound > 0 && j < this.affiliatedOrganizationsList.size())
                            {
                                annotation.append(",");
                            }
                            annotation.append(new Integer(j + 1).toString());
                            organizationsFound++;
                        }
                    }
                }
                if (annotation.length() > 0)
                {
                    annotation.append("]");
                }
                formattedCreator = formatter.formatCreator(creator) + annotation.toString();
                if (creator.getPerson() != null)
                {
                    this.creatorArray.add(formattedCreator);
                }
                if (creator.getOrganization() != null)
                {
                    ViewItemCreatorOrganization creatorOrganization = new ViewItemCreatorOrganization();
                    creatorOrganization.setOrganizationName(formattedCreator);
                    creatorOrganization.setPosition(new Integer(counterOrganization).toString());
                    creatorOrganization.setOrganizationAddress(creator.getOrganization().getAddress());
                    creatorOrganization.setOrganizationInfoPage(formattedCreator, creator.getOrganization()
                            .getAddress());
                    this.creatorOrganizationsArray.add(creatorOrganization);
                    counterOrganization++;
                }
                creatorList.append(formattedCreator);
            }
        }
        else
        {
            for (int i = 0; i < this.pubItem.getMetadata().getCreators().size(); i++)
            {
                CreatorVO creator = new CreatorVO();
                creator = this.pubItem.getMetadata().getCreators().get(i);
                annotation = new StringBuffer();
                int organizationsFound = 0;
                for (int j = 0; j < this.affiliatedOrganizationsList.size(); j++)
                {
                    if (creator.getPerson() != null)
                    {
                        if (creator.getPerson().getOrganizations().contains(this.affiliatedOrganizationsList.get(j)))
                        {
                            if (organizationsFound == 0)
                            {
                                annotation.append("   [");
                            }
                            if (organizationsFound > 0 && j < this.affiliatedOrganizationsList.size())
                            {
                                annotation.append(",");
                            }
                            annotation.append(new Integer(j + 1).toString());
                            organizationsFound++;
                        }
                    }
                }
                if (annotation.length() > 0)
                {
                    annotation.append("]");
                }
                formattedCreator = formatter.formatCreator(creator) + annotation.toString();
                if (creator.getPerson() != null)
                {
                    this.creatorArray.add(formattedCreator);
                }
                if (creator.getOrganization() != null)
                {
                    ViewItemCreatorOrganization creatorOrganization = new ViewItemCreatorOrganization();
                    creatorOrganization.setOrganizationName(formattedCreator);
                    creatorOrganization.setPosition(new Integer(counterOrganization).toString());
                    creatorOrganization.setOrganizationAddress(creator.getOrganization().getAddress());
                    creatorOrganization.setOrganizationInfoPage(formattedCreator, creator.getOrganization()
                            .getAddress());
                    this.creatorOrganizationsArray.add(creatorOrganization);
                    counterOrganization++;
                }
                creatorList.append(formattedCreator);
            }
        }
        // set the more / less button (if there is nothing to expand, the link
        // should not appear....)
        if (this.pubItem.getMetadata().getCreators().size() < 2)
        {
            this.buttonMoreCreatorList = "";
        }
        return creatorList.toString();
    }

    /**
     * generates the affiliated organization list as one string for presenting it in the jsp
     */
    private void getAffiliatedOrganizationList()
    {
        String formattedOrganization = "";
        List<CreatorVO> tempCreatorList;
        List<OrganizationVO> tempOrganizationList = null;
        List<OrganizationVO> sortOrganizationList = null;
        this.organizationArray = new ArrayList<String>();
        this.organizationList = new ArrayList<ViewItemOrganization>();
        tempOrganizationList = new ArrayList<OrganizationVO>();
        sortOrganizationList = new ArrayList<OrganizationVO>();
        tempCreatorList = this.pubItem.getMetadata().getCreators();
        for (int i = 0; i < tempCreatorList.size(); i++)
        {
            CreatorVO creator = new CreatorVO();
            creator = tempCreatorList.get(i);
            if (creator.getPerson() != null)
            {
                if (creator.getPerson().getOrganizations().size() > 0)
                {
                    for (int listSize = 0; listSize < creator.getPerson().getOrganizations().size(); listSize++)
                    {
                        tempOrganizationList.add(creator.getPerson().getOrganizations().get(listSize));
                    }
                    for (int j = 0; j < tempOrganizationList.size(); j++)
                    {
                        // if the organization is not in the list already, put
                        // it in.
                        if (!sortOrganizationList.contains(tempOrganizationList.get(j)))
                        {
                            sortOrganizationList.add(tempOrganizationList.get(j));
                            ViewItemOrganization viewOrganization = new ViewItemOrganization();
                            viewOrganization.setOrganizationName(tempOrganizationList.get(j).getName().getValue());
                            viewOrganization.setOrganizationAddress(tempOrganizationList.get(j).getAddress());
                            viewOrganization.setPosition(new Integer(j + 1).toString());
                            viewOrganization.setOrganizationInfoPage(tempOrganizationList.get(j).getName().getValue(),
                                    tempOrganizationList.get(j).getAddress());
                            this.organizationList.add(viewOrganization);
                        }
                    }
                }
            }
        }
        // save the List in the backing bean for later use.
        this.affiliatedOrganizationsList = sortOrganizationList;
        // generate a 'well-formed' list for presentation in the jsp
        for (int k = 0; k < sortOrganizationList.size(); k++)
        {
            formattedOrganization = (k + 1) + ": " + sortOrganizationList.get(k).toString();
            this.organizationArray.add(formattedOrganization);
        }
    }

    /**
     * gets the table of content of the item
     * 
     * @return String the formatted TOC as String (either collapsed or expanded)
     */
    private String getTOC()
    {
        String toc = "";
        if (this.pubItem.getMetadata().getTableOfContents() != null)
        {
            if (this.tocCollapsed == true)
            {
                if (this.pubItem.getMetadata().getTableOfContents().getValue().length() > this.numberCharsTOCCollapsed)
                {
                    this.tableOfContents = this.pubItem.getMetadata().getTableOfContents().getValue().substring(0,
                            this.numberCharsTOCCollapsed)
                            + "...";
                    toc = this.pubItem.getMetadata().getTableOfContents().getValue().substring(0, this.numberCharsTOCCollapsed)
                            + "...";
                }
                else
                {
                    this.tableOfContents = this.pubItem.getMetadata().getTableOfContents().getValue();
                    toc = this.pubItem.getMetadata().getTableOfContents().getValue();
                }
            }
            else
            {
                this.tableOfContents = this.pubItem.getMetadata().getTableOfContents().getValue();
                toc = this.pubItem.getMetadata().getTableOfContents().getValue();
            }
            // set the more/less link (link should not appear if toc is smaller
            // than max toc length)
            if (this.pubItem.getMetadata().getTableOfContents().getValue().length() < this.numberCharsTOCCollapsed)
            {
                this.buttonMoreTOC = "";
            }
        }
        else
        {
            this.buttonMoreTOC = "";
        }
        return toc;
    }

    /**
     * gets the alternative title(s) of the item
     * 
     * @return ArrayList<String> the list of formatted alternative titles (either collapsed or expanded)
     */
    private ArrayList<String> getAlternativeTitles()
    {
        ArrayList<String> alternativeTitles = new ArrayList<String>();
        this.alternativeTitlesArray = new ArrayList<String>();
        if (this.pubItem.getMetadata().getAlternativeTitles() != null)
        {
            if (this.pubItem.getMetadata().getAlternativeTitles().size() > 0)
            {
                if (this.itemAlternativeTitlesCollapsed == true)
                {
                    for (int i = 0; i < this.numberItemAlternativeTitlesCollapsed; i++)
                    {
                        this.alternativeTitlesArray.add(this.pubItem.getMetadata().getAlternativeTitles().get(i)
                                .getValue());
                        alternativeTitles.add(this.pubItem.getMetadata().getAlternativeTitles().get(i).getValue());
                    }
                }
                else
                {
                    for (int i = 0; i < this.pubItem.getMetadata().getAlternativeTitles().size(); i++)
                    {
                        this.alternativeTitlesArray.add(this.pubItem.getMetadata().getAlternativeTitles().get(i)
                                .getValue());
                        alternativeTitles.add(this.pubItem.getMetadata().getAlternativeTitles().get(i).getValue());
                    }
                }
            }
        }
        // set the more/less button
        if (this.pubItem.getMetadata().getAlternativeTitles().size() < 2)
        {
            this.buttonMoreItemAlternativeTitles = "";
        }
        return alternativeTitles;
    }

    /**
     * gets the event title(s) of the item
     * 
     * @return ArrayList<String> formatted event titles according the current collapsed or expanded state
     */
    private ArrayList<String> getEventTitles()
    {
        ArrayList<String> eventTitles = new ArrayList<String>();
        if (this.pubItem.getMetadata().getEvent() != null)
        {
            if (this.pubItem.getMetadata().getEvent().getTitle() != null)
            {
                eventTitles.add(this.pubItem.getMetadata().getEvent().getTitle().getValue());
            }
            if (this.pubItem.getMetadata().getEvent().getAlternativeTitles() != null
                    && this.pubItem.getMetadata().getEvent().getAlternativeTitles().size() > 0)
            {
                if (this.eventTitlesCollapsed == true)
                {
                    for (int i = 0; i < this.numberEventTitlesCollapsed - 1; i++)
                    {
                        eventTitles.add(this.pubItem.getMetadata().getEvent().getAlternativeTitles().get(i).getValue());
                    }
                    this.buttonMoreItemEventTitles = this.getLabel("ViewItem_lnkEventTitleMore");
                }
                else
                {
                    for (int i = 0; i < this.pubItem.getMetadata().getEvent().getAlternativeTitles().size(); i++)
                    {
                        eventTitles.add(this.pubItem.getMetadata().getEvent().getAlternativeTitles().get(i).getValue());
                    }
                    this.buttonMoreItemEventTitles = this.getLabel("ViewItem_lnkEventTitleLess");
                }
            }
        }
        return eventTitles;
    }

    /**
     * sets the event titles collapsed flag to true or false
     * 
     * @return String faces navigation string to reload the view item page
     */
    public String expandCollapseEventTitles()
    {
        if (this.eventTitlesCollapsed == true)
        {
            this.eventTitlesCollapsed = false;
            this.buttonMoreItemEventTitles = this.getLabel("ViewItem_lnkEventTitleLess");
        }
        else
        {
            this.eventTitlesCollapsed = true;
            this.buttonMoreItemEventTitles = this.getLabel("ViewItem_lnkEventTitleMore");
        }
        this.eventTitleArray = getEventTitles();
        return "loadViewItem";
    }

    /**
     * gets the responsible affiliation of the collection the pubitem is in
     * 
     * @return String the affiliation
     */
    public String getAffiliationOfCollection()
    {
        return this.affiliationOfContext;
    }

    /**
     * sets the creatorList collapsed flag to true or false
     * 
     * @return String faces navigation string to reload the view item page
     */
    public String expandCollapseCreatorList()
    {
        if (this.creatorListCollapsed == true)
        {
            this.creatorListCollapsed = false;
            this.buttonMoreCreatorList = this.getLabel("ViewItem_lnkCreatorLess");
        }
        else
        {
            this.creatorListCollapsed = true;
            this.buttonMoreCreatorList = this.getLabel("ViewItem_lnkCreatorMore");
        }
        getCreatorList();
        return "loadViewItem";
    }

    /**
     * sets the TOC collapsed flag to true or false
     * 
     * @return String faces navigation string to reload the view item page
     */
    public String expandCollapseTOC()
    {
        if (this.tocCollapsed == true)
        {
            this.tocCollapsed = false;
            this.buttonMoreTOC = this.getLabel("ViewItem_lnkTocLess");
        }
        else
        {
            this.tocCollapsed = true;
            this.buttonMoreTOC = this.getLabel("ViewItem_lnkTocMore");
        }
        getTOC();
        return "loadViewItem";
    }

    /**
     * sets the alternative titles of the item collapsed flag to true or false and switches the label of the
     * expand/collapse link
     * 
     * @return String faces navigation string to reload the view item page
     */
    public String expandCollapseItemAlternativeTitles()
    {
        if (this.itemAlternativeTitlesCollapsed == true)
        {
            this.itemAlternativeTitlesCollapsed = false;
            this.buttonMoreItemAlternativeTitles = this.getLabel("ViewItem_lnkAlternativeTitleLess");
        }
        else
        {
            this.itemAlternativeTitlesCollapsed = true;
            this.buttonMoreItemAlternativeTitles = this.getLabel("ViewItem_lnkAlternativeTitleMore");
        }
        getAlternativeTitles();
        return "loadViewItem";
    }

    /**
     * gets language(s) of the item' s content
     * 
     * @return String formatted languages
     */
    private String getLanguages()
    {
        StringBuffer language = new StringBuffer();
        if (this.pubItem.getMetadata().getLanguages() != null)
        {
            for (int i = 0; i < this.pubItem.getMetadata().getLanguages().size(); i++)
            {
                language.append(this.pubItem.getMetadata().getLanguages().get(i));
                if (i < this.pubItem.getMetadata().getLanguages().size() - 1)
                {
                    language.append(", ");
                }
            }
        }
        return language.toString();
    }

    /**
     * gets formatted identifier(s) of the item
     * 
     * @return ArrayList<String> formatted identifier(s)
     */
    private ArrayList<String> getItemIdentifiers()
    {
        ArrayList<String> identifiers = new ArrayList<String>();
        if (this.pubItem.getMetadata().getIdentifiers() != null)
        {
            if (this.pubItem.getMetadata().getIdentifiers().size() > 0)
            {
                for (int i = 0; i < this.pubItem.getMetadata().getIdentifiers().size(); i++)
                {
                    identifiers.add(this.pubItem.getMetadata().getIdentifiers().get(i).getType().toString() + " "
                            + this.pubItem.getMetadata().getIdentifiers().get(i).getId());
                }
            }
        }
        this.itemIdentifierArray = identifiers;
        return identifiers;
    }

    /**
     * gets the formatted relations of an item
     * 
     * @return ArrayList<String> formatted relation(s)
     */
    private ArrayList<String> getRelations()
    {
        ArrayList<String> relations = new ArrayList<String>();
        //Note: relations were removed from the metadata
        /*if (this.pubItem.getMetadata().getRelations() != null)
        {
            if (this.pubItem.getMetadata().getRelations().size() > 0)
            {
                for (int i = 0; i < this.pubItem.getMetadata().getRelations().size(); i++)
                {
                    RelationVO rel = new RelationVO();
                    rel = this.pubItem.getMetadata().getRelations().get(i);
                    // insert 2 new lines
                    if (i > 0)
                    {
                        relations.add(" ");
                        relations.add(" ");
                    }
                    relations.add(this.pubItem.getMetadata().getRelations().get(i).getType().toString() + ": "
                            + this.pubItem.getMetadata().getRelations().get(i).getIdentifier().getType().name()
                            + this.pubItem.getMetadata().getRelations().get(i).getIdentifier().getId());
                    for (int j = 0; j < rel.getShortDescriptions().size(); j++)
                    {
                        String desc = rel.getShortDescriptions().get(j).getValue();
                        relations.add(desc);
                        // relations.add(rel.getShortDescriptions().get(j).getValue());
                    }
                }
            }
        }*/
        this.relationArray = relations;
        return relations;
    }

    /**
     * sets the abstracts collapsed flag to true or false and switches the expand/collapsed link
     * 
     * @return String faces navigation string to reload the view item page
     */
    public String expandCollapseAbstracts()
    {
        if (this.abstractsCollapsed == true)
        {
            this.abstractsCollapsed = false;
            this.buttonMoreAbstracts = this.getLabel("ViewItem_lnkAbstractLess");
        }
        else
        {
            this.abstractsCollapsed = true;
            this.buttonMoreAbstracts = this.getLabel("ViewItem_lnkAbstractMore");
        }
        getAbstract();
        return "loadViewItem";
    }

    /**
     * gets the table of content of the item
     * 
     * @return ArrayList<String> list of abstracts according to the expand/collapsed state
     */
    private ArrayList<String> getAbstract()
    {
        String abstracts = "";
        ArrayList<String> abstractsArrayList = new ArrayList<String>();
        this.abstractsArray = new ArrayList<String>();
        if (this.pubItem.getMetadata().getAbstracts() != null)
        {
            if (this.pubItem.getMetadata().getAbstracts().size() > 0)
            {
                if (this.abstractsCollapsed == true)
                {
                    abstracts = this.pubItem.getMetadata().getAbstracts().get(0).getValue();
                    if (abstracts.length() > this.numberCharsAbstractsCollapsed)
                    {
                        abstracts = abstracts.substring(0, numberCharsAbstractsCollapsed) + "...";
                    }
                    abstractsArrayList.add(abstracts);
                    this.abstractsArray.add(abstracts);
                }
                else
                {
                    for (int i = 0; i < this.pubItem.getMetadata().getAbstracts().size(); i++)
                    {
                        abstracts = this.pubItem.getMetadata().getAbstracts().get(i).getValue();
                        if (i > 0)
                        {
                            abstractsArrayList.add(" ");
                            this.abstractsArray.add(" ");
                        }
                        abstractsArrayList.add(abstracts);
                        this.abstractsArray.add(abstracts);
                    }
                }
            }
        }
        // set the more/less link (if there is nothing to expand, the link
        // should not appear...)
        if (this.pubItem.getMetadata().getAbstracts().size() < 2)
        {
            this.buttonMoreAbstracts = "";
        }
        return abstractsArrayList;
    }

    /**
     * selects the organization the detailed information should be displayed.
     * 
     * @return String faces navigation string to open the organisation information page
     */
    public String showOrganization()
    {
        int position = new Integer(getFacesParamValue("positionInList")).intValue() - 1;
        // set the values of the object to be displayed in the jsp
        this.viewOrganisation = new ViewItemOrganization();
        this.viewOrganisation.setOrganizationName(this.organizationList.get(position).getOrganizationName());
        this.viewOrganisation.setOrganizationAddress(this.organizationList.get(position).getOrganizationAddress());
        this.viewOrganisation.setOrganizationInfoPage(this.organizationList.get(position).getOrganizationName(),
                this.organizationList.get(position).getOrganizationAddress());
        return "showOrganizationInformation";
    }

    /**
     * selects the organization (which is a creator) the detailed information should be displayed.
     * 
     * @return String faces navigation string to open the organisation information page
     */
    public String showCreatorOrganization()
    {
        int position = new Integer(getFacesParamValue("positionInList")).intValue();
        // set the values of the object to be displayed in the jsp
        this.viewOrganisation = new ViewItemOrganization();
        this.viewOrganisation.setOrganizationName(this.creatorOrganizationsArray.get(position).getOrganizationName());
        this.viewOrganisation.setOrganizationAddress(this.creatorOrganizationsArray.get(position)
                .getOrganizationAddress());
        return "showOrganizationInformation";
    }

    /**
     * gets the list of files of the item
     * 
     * @return ArrayList<FileVO> the list of files
     */
    private ArrayList<FileVO> getFiles()
    {
        ArrayList<FileVO> pubFileList = new ArrayList<FileVO>();
        this.fileArray = new ArrayList<FileVO>();
        if (this.pubItem.getFiles() != null)
        {
            for (int i = 0; i < this.pubItem.getFiles().size(); i++)
            {
                pubFileList.add(this.pubItem.getFiles().get(i));
                this.fileArray.add(this.pubItem.getFiles().get(i));
            }
        }
        return pubFileList;
    }

    /**
     * gets the list of sources of the item
     * 
     * @return ArrayList<SourceVO> the list of sources
     */
    private ArrayList<SourceVO> getSources()
    {
        ArrayList<SourceVO> sourceList = new ArrayList<SourceVO>();
        this.sourceArray = new ArrayList<SourceVO>();
        if (this.pubItem.getMetadata().getSources() != null)
        {
            for (int i = 0; i < this.pubItem.getMetadata().getSources().size(); i++)
            {
                sourceList.add(this.pubItem.getMetadata().getSources().get(i));
                this.sourceArray.add(this.pubItem.getMetadata().getSources().get(i));
            }
        }
        return sourceList;
    }

    /**
     * downloads the file the user wants to download directly from FIZ Framework
     * 
     * @param filePosition the position of the file in the list
     * @throws IOException, Exception
     */
    public void downloadFile(int filePosition) throws IOException, Exception
    {
        LoginHelper loginHelper = (LoginHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver()
                .resolveVariable(FacesContext.getCurrentInstance(), "LoginHelper");
        // extract the location of the file
        String fileLocation = ServiceLocator.getFrameworkUrl() + this.fileArray.get(filePosition).getContent();
        String filename = this.fileArray.get(filePosition).getName(); // Filename
        // suggested
        // in
        // browser
        // Save
        // As
        // dialog
        filename = filename.replace(" ", "_"); // replace empty spaces because
        // they cannot be procesed by
        // the
        // http-response (filename will be cutted after the first empty space)
        String contentType = this.fileArray.get(filePosition).getMimeType(); // For
        // dialog,
        // try
        // application/x-download
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse)fc.getExternalContext().getResponse();
        response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
        if(this.fileArray.get(filePosition).getDefaultMetadata() != null)
        {
        	response.setContentLength(this.fileArray.get(filePosition).getDefaultMetadata().getSize());
        }
        
        response.setContentType(contentType);
        byte[] buffer = null;
        if (filePosition != -1 && this.fileArray.get(filePosition).getDefaultMetadata() != null)
        {
            try
            {
                GetMethod method = new GetMethod(fileLocation);
                method.setFollowRedirects(false);
                if (loginHelper.getESciDocUserHandle() != null)
                {
                    // downloading by account user
                    addHandleToMethod(method, loginHelper.getESciDocUserHandle());
                }
                // Execute the method with HttpClient.
                HttpClient client = new HttpClient();
                client.executeMethod(method);
                OutputStream out = response.getOutputStream();
                InputStream input = method.getResponseBodyAsStream();
                try
                {
                    if(this.fileArray.get(filePosition).getDefaultMetadata() != null)
                    {
                    	buffer = new byte[this.fileArray.get(filePosition).getDefaultMetadata().getSize()];
                    	int numRead;
                        long numWritten = 0;
                        while ((numRead = input.read(buffer)) != -1)
                        {
                            out.write(buffer, 0, numRead);
                            out.flush();
                            numWritten += numRead;
                        }
                        fc.responseComplete();
                    }
                    
                }
                catch (IOException e1)
                {
                    logger.debug("Download IO Error: " + e1.toString());
                }
                input.close();
                out.close();
            }
            catch (FileNotFoundException e)
            {
                logger.debug("File not found: " + e.toString());
            }
        }
    }

    /**
     * Method is called in jsp by the action handler. Triggers download action
     * 
     * @return String faces navigation rule to open the file download page
     * @throws
     */
    public String generateDownloadLink(ActionEvent event) throws Exception
    {
    	HtmlCommandButton btnDownload = (HtmlCommandButton)event.getSource();
        // get the position of the file within the item by analyzing the
        // button's id
        int filePosition = new Integer(btnDownload.getId().substring(4));
        try
        {
            downloadFile(filePosition);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return "viewItemFileDownload";
    }

    /**
     * Creates a list of files that are related to the item
     */
    private void createFiles()
    {
        // this.panFiles = new HtmlPanelGrid();
        this.panFiles.getChildren().clear();
        if (this.fileArray != null)
        {
            for (int i = 0; i < this.fileArray.size(); i++)
            {
                FileUI fileUI = new FileUI(this.fileArray.get(i), i);
                this.panFiles.getChildren().add(fileUI.getUIComponent());
            }
        }
    }

    /**
     * Creates the structure of the sources tree attached to the item (currently limited to 5 levels)
     */
    private void initializeItemSourceView()
    {
        this.itemSourceList = new ArrayList<ViewItemSource>();
        if (this.pubItem.getMetadata() != null)
        {
            if (this.pubItem.getMetadata().getSources() != null)
            {
                for (int i = 0; i < this.pubItem.getMetadata().getSources().size(); i++)
                {
                    ViewItemSource itemSourceView = new ViewItemSource(new Integer(i).toString(), true, true, true,
                            false);
                    itemSourceView.setPaddingLeft(0);
                    if (this.pubItem.getMetadata().getSources().get(i).getSources() != null)
                    {
                        itemSourceView.setViewItemSourceEmbedded(new ArrayList<ViewItemSource>());
                    }
                    if (itemSourceView.getViewItemSourceEmbedded() != null)
                    {
                        for (int j = 0; j < this.pubItem.getMetadata().getSources().get(i).getSources().size(); j++)
                        {
                            ViewItemSource itemSourceViewEmbedded1 = new ViewItemSource(new Integer(i).toString() + "_"
                                    + new Integer(j).toString(), true, true, true, false);
                            itemSourceViewEmbedded1.setPaddingLeft(itemSourceView.getPaddingLeft() + 45);
                            if (this.pubItem.getMetadata().getSources().get(i).getSources().get(j).getSources() != null)
                            {
                                itemSourceView.setViewItemSourceEmbedded(new ArrayList<ViewItemSource>());
                            }
                            if (itemSourceViewEmbedded1.getViewItemSourceEmbedded() != null)
                            {
                                for (int k = 0; k < this.pubItem.getMetadata().getSources().get(i).getSources().get(j)
                                        .getSources().size(); k++)
                                {
                                    ViewItemSource itemSourceViewEmbedded2 = new ViewItemSource(new Integer(i)
                                            .toString()
                                            + "_" + new Integer(j).toString() + "_" + new Integer(k).toString(), true,
                                            true, true, false);
                                    itemSourceViewEmbedded2
                                            .setPaddingLeft(itemSourceViewEmbedded1.getPaddingLeft() + 45);
                                    if (this.pubItem.getMetadata().getSources().get(i).getSources().get(j).getSources()
                                            .get(k).getSources() != null)
                                    {
                                        itemSourceView.setViewItemSourceEmbedded(new ArrayList<ViewItemSource>());
                                    }
                                    if (itemSourceViewEmbedded2.getViewItemSourceEmbedded() != null)
                                    {
                                        for (int l = 0; l < this.pubItem.getMetadata().getSources().get(i).getSources()
                                                .get(j).getSources().get(k).getSources().size(); l++)
                                        {
                                            ViewItemSource itemSourceViewEmbedded3 = new ViewItemSource(new Integer(i)
                                                    .toString()
                                                    + "_"
                                                    + new Integer(j).toString()
                                                    + "_"
                                                    + new Integer(k).toString()
                                                    + "_" + new Integer(l).toString(), true, true, true, false);
                                            itemSourceViewEmbedded3.setPaddingLeft(itemSourceViewEmbedded2
                                                    .getPaddingLeft() + 45);
                                            if (this.pubItem.getMetadata().getSources().get(i).getSources().get(j)
                                                    .getSources().get(k).getSources().get(l).getSources() != null)
                                            {
                                                itemSourceView
                                                        .setViewItemSourceEmbedded(new ArrayList<ViewItemSource>());
                                            }
                                            if (itemSourceViewEmbedded3.getViewItemSourceEmbedded() != null)
                                            {
                                                for (int m = 0; m < this.pubItem.getMetadata().getSources().get(i)
                                                        .getSources().get(j).getSources().get(k).getSources().get(l)
                                                        .getSources().size(); m++)
                                                {
                                                    ViewItemSource itemSourceViewEmbedded4 = new ViewItemSource(
                                                            new Integer(i).toString() + "_" + new Integer(j).toString()
                                                                    + "_" + new Integer(k).toString() + "_"
                                                                    + new Integer(l).toString() + "_"
                                                                    + new Integer(m).toString(), true, true, true, true);
                                                    itemSourceViewEmbedded4.setPaddingLeft(itemSourceViewEmbedded3
                                                            .getPaddingLeft() + 45);
                                                    itemSourceViewEmbedded3.getViewItemSourceEmbedded().add(
                                                            itemSourceViewEmbedded4);
                                                }
                                            }
                                            itemSourceViewEmbedded2.getViewItemSourceEmbedded().add(
                                                    itemSourceViewEmbedded3);
                                        }
                                    }
                                    itemSourceViewEmbedded1.getViewItemSourceEmbedded().add(itemSourceViewEmbedded2);
                                }
                            }
                            itemSourceView.getViewItemSourceEmbedded().add(itemSourceViewEmbedded1);
                        }
                    }
                    this.itemSourceList.add(itemSourceView);
                }
            }
        }
    }

    /**
     * Creates a list of sources
     */
    private void createSources()
    {
        this.panSources.getChildren().clear();
        this.panSources.setId(CommonUtils.createUniqueId(this.panSources));
        this.panSources.setWidth("100%");
        if (this.sourceArray != null)
        {
            for (int i = 0; i < this.sourceArray.size(); i++)
            {
                SourceUI sourceUI = new SourceUI(this.sourceArray.get(i), this.itemSourceList.get(i));
                this.panSources.getChildren().add(sourceUI.getUIComponent());
            }
        }
    }

    /**
     * expands or collapses the source's elements of type alternative title the user has selected (currently limited to
     * 5 levels)
     * 
     * @param sourceID the ID of the source to be collapsed or expanded
     */
    public void expandCollapseSourceAlternativeTitle(String sourceID)
    {
        for (int i = 0; i < this.itemSourceList.size(); i++)
        {
            if (this.itemSourceList.get(i).getSourceID().equals(sourceID))
            {
                if (this.itemSourceList.get(i).isAlternativeTitlesCollapsed() == true)
                {
                    this.itemSourceList.get(i).setAlternativeTitlesCollapsed(false);
                }
                else
                {
                    this.itemSourceList.get(i).setAlternativeTitlesCollapsed(true);
                }
            }
            else
            {
                if (this.itemSourceList.get(i).getViewItemSourceEmbedded() != null)
                {
                    for (int j = 0; j < this.itemSourceList.get(i).getViewItemSourceEmbedded().size(); j++)
                    {
                        if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j).getSourceID()
                                .equals(sourceID))
                        {
                            if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                    .isAlternativeTitlesCollapsed() == true)
                            {
                                this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                        .setAlternativeTitlesCollapsed(false);
                            }
                            else
                            {
                                this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                        .setAlternativeTitlesCollapsed(true);
                            }
                        }
                        else
                        {
                            if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                    .getViewItemSourceEmbedded() != null)
                            {
                                for (int k = 0; k < this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                        .getViewItemSourceEmbedded().size(); k++)
                                {
                                    if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                            .getViewItemSourceEmbedded().get(k).getSourceID().equals(sourceID))
                                    {
                                        if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                .getViewItemSourceEmbedded().get(k).isAlternativeTitlesCollapsed() == true)
                                        {
                                            this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                    .getViewItemSourceEmbedded().get(k).setAlternativeTitlesCollapsed(
                                                            false);
                                        }
                                        else
                                        {
                                            this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                    .getViewItemSourceEmbedded().get(k).setAlternativeTitlesCollapsed(
                                                            true);
                                        }
                                    }
                                    else
                                    {
                                        if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                .getViewItemSourceEmbedded().get(k).getViewItemSourceEmbedded() != null)
                                        {
                                            for (int l = 0; l < this.itemSourceList.get(i).getViewItemSourceEmbedded()
                                                    .get(j).getViewItemSourceEmbedded().get(k)
                                                    .getViewItemSourceEmbedded().size(); l++)
                                            {
                                                if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                        .getViewItemSourceEmbedded().get(k).getViewItemSourceEmbedded()
                                                        .get(l).getSourceID().equals(sourceID))
                                                {
                                                    if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                            .getViewItemSourceEmbedded().get(k)
                                                            .getViewItemSourceEmbedded().get(l)
                                                            .isAlternativeTitlesCollapsed() == true)
                                                    {
                                                        this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                                .getViewItemSourceEmbedded().get(k)
                                                                .getViewItemSourceEmbedded().get(l)
                                                                .setAlternativeTitlesCollapsed(false);
                                                    }
                                                    else
                                                    {
                                                        this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                                .getViewItemSourceEmbedded().get(k)
                                                                .getViewItemSourceEmbedded().get(l)
                                                                .setAlternativeTitlesCollapsed(true);
                                                    }
                                                }
                                                else
                                                {
                                                    if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                            .getViewItemSourceEmbedded().get(k)
                                                            .getViewItemSourceEmbedded().get(l)
                                                            .getViewItemSourceEmbedded() != null)
                                                    {
                                                        for (int m = 0; m < this.itemSourceList.get(i)
                                                                .getViewItemSourceEmbedded().get(j)
                                                                .getViewItemSourceEmbedded().get(k)
                                                                .getViewItemSourceEmbedded().get(l)
                                                                .getViewItemSourceEmbedded().size(); m++)
                                                        {
                                                            if (this.itemSourceList.get(i).getViewItemSourceEmbedded()
                                                                    .get(j).getViewItemSourceEmbedded().get(k)
                                                                    .getViewItemSourceEmbedded().get(l)
                                                                    .getViewItemSourceEmbedded().get(m).getSourceID()
                                                                    .equals(sourceID))
                                                            {
                                                                if (this.itemSourceList.get(i)
                                                                        .getViewItemSourceEmbedded().get(j)
                                                                        .getViewItemSourceEmbedded().get(k)
                                                                        .getViewItemSourceEmbedded().get(l)
                                                                        .getViewItemSourceEmbedded().get(m)
                                                                        .isAlternativeTitlesCollapsed() == true)
                                                                {
                                                                    this.itemSourceList.get(i)
                                                                            .getViewItemSourceEmbedded().get(j)
                                                                            .getViewItemSourceEmbedded().get(k)
                                                                            .getViewItemSourceEmbedded().get(l)
                                                                            .getViewItemSourceEmbedded().get(m)
                                                                            .setAlternativeTitlesCollapsed(false);
                                                                }
                                                                else
                                                                {
                                                                    this.itemSourceList.get(i)
                                                                            .getViewItemSourceEmbedded().get(j)
                                                                            .getViewItemSourceEmbedded().get(k)
                                                                            .getViewItemSourceEmbedded().get(l)
                                                                            .getViewItemSourceEmbedded().get(m)
                                                                            .setAlternativeTitlesCollapsed(true);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * expands or collapses the source's elements of type creator the user has selected (currently limited to 5 levels)
     * 
     * @param sourceID the ID of the source in which the creators should be expanded or collapsed
     */
    public void expandCollapseSourceCreators(String sourceID)
    {
        for (int i = 0; i < this.itemSourceList.size(); i++)
        {
            if (this.itemSourceList.get(i).getSourceID().equals(sourceID))
            {
                if (this.itemSourceList.get(i).isCreatorsCollapsed() == true)
                {
                    this.itemSourceList.get(i).setCreatorsCollapsed(false);
                }
                else
                {
                    this.itemSourceList.get(i).setCreatorsCollapsed(true);
                }
            }
            else
            {
                if (this.itemSourceList.get(i).getViewItemSourceEmbedded() != null)
                {
                    for (int j = 0; j < this.itemSourceList.get(i).getViewItemSourceEmbedded().size(); j++)
                    {
                        if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j).getSourceID()
                                .equals(sourceID))
                        {
                            if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j).isCreatorsCollapsed() == true)
                            {
                                this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j).setCreatorsCollapsed(
                                        false);
                            }
                            else
                            {
                                this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                        .setCreatorsCollapsed(true);
                            }
                        }
                        else
                        {
                            if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                    .getViewItemSourceEmbedded() != null)
                            {
                                for (int k = 0; k < this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                        .getViewItemSourceEmbedded().size(); k++)
                                {
                                    if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                            .getViewItemSourceEmbedded().get(k).getSourceID().equals(sourceID))
                                    {
                                        if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                .getViewItemSourceEmbedded().get(k).isCreatorsCollapsed() == true)
                                        {
                                            this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                    .getViewItemSourceEmbedded().get(k).setCreatorsCollapsed(false);
                                        }
                                        else
                                        {
                                            this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                    .getViewItemSourceEmbedded().get(k).setCreatorsCollapsed(true);
                                        }
                                    }
                                    else
                                    {
                                        if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                .getViewItemSourceEmbedded().get(k).getViewItemSourceEmbedded() != null)
                                        {
                                            for (int l = 0; l < this.itemSourceList.get(i).getViewItemSourceEmbedded()
                                                    .get(j).getViewItemSourceEmbedded().get(k)
                                                    .getViewItemSourceEmbedded().size(); l++)
                                            {
                                                if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                        .getViewItemSourceEmbedded().get(k).getViewItemSourceEmbedded()
                                                        .get(l).getSourceID().equals(sourceID))
                                                {
                                                    if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                            .getViewItemSourceEmbedded().get(k)
                                                            .getViewItemSourceEmbedded().get(l).isCreatorsCollapsed() == true)
                                                    {
                                                        this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                                .getViewItemSourceEmbedded().get(k)
                                                                .getViewItemSourceEmbedded().get(l)
                                                                .setCreatorsCollapsed(false);
                                                    }
                                                    else
                                                    {
                                                        this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                                .getViewItemSourceEmbedded().get(k)
                                                                .getViewItemSourceEmbedded().get(l)
                                                                .setCreatorsCollapsed(true);
                                                    }
                                                }
                                                else
                                                {
                                                    if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                            .getViewItemSourceEmbedded().get(k)
                                                            .getViewItemSourceEmbedded().get(l)
                                                            .getViewItemSourceEmbedded() != null)
                                                    {
                                                        for (int m = 0; m < this.itemSourceList.get(i)
                                                                .getViewItemSourceEmbedded().get(j)
                                                                .getViewItemSourceEmbedded().get(k)
                                                                .getViewItemSourceEmbedded().get(l)
                                                                .getViewItemSourceEmbedded().size(); m++)
                                                        {
                                                            if (this.itemSourceList.get(i).getViewItemSourceEmbedded()
                                                                    .get(j).getViewItemSourceEmbedded().get(k)
                                                                    .getViewItemSourceEmbedded().get(l)
                                                                    .getViewItemSourceEmbedded().get(m).getSourceID()
                                                                    .equals(sourceID))
                                                            {
                                                                if (this.itemSourceList.get(i)
                                                                        .getViewItemSourceEmbedded().get(j)
                                                                        .getViewItemSourceEmbedded().get(k)
                                                                        .getViewItemSourceEmbedded().get(l)
                                                                        .getViewItemSourceEmbedded().get(m)
                                                                        .isCreatorsCollapsed() == true)
                                                                {
                                                                    this.itemSourceList.get(i)
                                                                            .getViewItemSourceEmbedded().get(j)
                                                                            .getViewItemSourceEmbedded().get(k)
                                                                            .getViewItemSourceEmbedded().get(l)
                                                                            .getViewItemSourceEmbedded().get(m)
                                                                            .setCreatorsCollapsed(false);
                                                                }
                                                                else
                                                                {
                                                                    this.itemSourceList.get(i)
                                                                            .getViewItemSourceEmbedded().get(j)
                                                                            .getViewItemSourceEmbedded().get(k)
                                                                            .getViewItemSourceEmbedded().get(l)
                                                                            .getViewItemSourceEmbedded().get(m)
                                                                            .setCreatorsCollapsed(true);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * expands or collapses the source's elements of type source the user has selected
     * 
     * @param sourceID the ID of the source in which the sources (source of source) should be expanded or collapsed
     */
    public void expandCollapseSourceOfSource(String sourceID)
    {
        for (int i = 0; i < this.itemSourceList.size(); i++)
        {
            if (this.itemSourceList.get(i).getSourceID().equals(sourceID))
            {
                if (this.itemSourceList.get(i).isSourcesOfSourceCollapsed() == true)
                {
                    this.itemSourceList.get(i).setSourcesOfSourceCollapsed(false);
                }
                else
                {
                    this.itemSourceList.get(i).setSourcesOfSourceCollapsed(true);
                }
            }
            else
            {
                if (this.itemSourceList.get(i).getViewItemSourceEmbedded() != null)
                {
                    for (int j = 0; j < this.itemSourceList.get(i).getViewItemSourceEmbedded().size(); j++)
                    {
                        if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j).getSourceID()
                                .equals(sourceID))
                        {
                            if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                    .isSourcesOfSourceCollapsed() == true)
                            {
                                this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                        .setSourcesOfSourceCollapsed(false);
                            }
                            else
                            {
                                this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                        .setSourcesOfSourceCollapsed(true);
                            }
                        }
                        else
                        {
                            if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                    .getViewItemSourceEmbedded() != null)
                            {
                                for (int k = 0; k < this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                        .getViewItemSourceEmbedded().size(); k++)
                                {
                                    if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                            .getViewItemSourceEmbedded().get(k).getSourceID().equals(sourceID))
                                    {
                                        if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                .getViewItemSourceEmbedded().get(k).isSourcesOfSourceCollapsed() == true)
                                        {
                                            this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                    .getViewItemSourceEmbedded().get(k).setSourcesOfSourceCollapsed(
                                                            false);
                                        }
                                        else
                                        {
                                            this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                    .getViewItemSourceEmbedded().get(k).setSourcesOfSourceCollapsed(
                                                            true);
                                        }
                                    }
                                    else
                                    {
                                        if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                .getViewItemSourceEmbedded().get(k).getViewItemSourceEmbedded() != null)
                                        {
                                            for (int l = 0; l < this.itemSourceList.get(i).getViewItemSourceEmbedded()
                                                    .get(j).getViewItemSourceEmbedded().get(k)
                                                    .getViewItemSourceEmbedded().size(); l++)
                                            {
                                                if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                        .getViewItemSourceEmbedded().get(k).getViewItemSourceEmbedded()
                                                        .get(l).getSourceID().equals(sourceID))
                                                {
                                                    if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                            .getViewItemSourceEmbedded().get(k)
                                                            .getViewItemSourceEmbedded().get(l)
                                                            .isSourcesOfSourceCollapsed() == true)
                                                    {
                                                        this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                                .getViewItemSourceEmbedded().get(k)
                                                                .getViewItemSourceEmbedded().get(l)
                                                                .setSourcesOfSourceCollapsed(false);
                                                    }
                                                    else
                                                    {
                                                        this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                                .getViewItemSourceEmbedded().get(k)
                                                                .getViewItemSourceEmbedded().get(l)
                                                                .setSourcesOfSourceCollapsed(true);
                                                    }
                                                }
                                                else
                                                {
                                                    if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                            .getViewItemSourceEmbedded().get(k)
                                                            .getViewItemSourceEmbedded().get(l)
                                                            .getViewItemSourceEmbedded() != null)
                                                    {
                                                        for (int m = 0; m < this.itemSourceList.get(i)
                                                                .getViewItemSourceEmbedded().get(j)
                                                                .getViewItemSourceEmbedded().get(k)
                                                                .getViewItemSourceEmbedded().get(l)
                                                                .getViewItemSourceEmbedded().size(); m++)
                                                        {
                                                            if (this.itemSourceList.get(i).getViewItemSourceEmbedded()
                                                                    .get(j).getViewItemSourceEmbedded().get(k)
                                                                    .getViewItemSourceEmbedded().get(l)
                                                                    .getViewItemSourceEmbedded().get(m).getSourceID()
                                                                    .equals(sourceID))
                                                            {
                                                                if (this.itemSourceList.get(i)
                                                                        .getViewItemSourceEmbedded().get(j)
                                                                        .getViewItemSourceEmbedded().get(k)
                                                                        .getViewItemSourceEmbedded().get(l)
                                                                        .getViewItemSourceEmbedded().get(m)
                                                                        .isSourcesOfSourceCollapsed() == true)
                                                                {
                                                                    this.itemSourceList.get(i)
                                                                            .getViewItemSourceEmbedded().get(j)
                                                                            .getViewItemSourceEmbedded().get(k)
                                                                            .getViewItemSourceEmbedded().get(l)
                                                                            .getViewItemSourceEmbedded().get(m)
                                                                            .setSourcesOfSourceCollapsed(false);
                                                                }
                                                                else
                                                                {
                                                                    this.itemSourceList.get(i)
                                                                            .getViewItemSourceEmbedded().get(j)
                                                                            .getViewItemSourceEmbedded().get(k)
                                                                            .getViewItemSourceEmbedded().get(l)
                                                                            .getViewItemSourceEmbedded().get(m)
                                                                            .setSourcesOfSourceCollapsed(true);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * expands or collapses the source's elements the user has selected
     * 
     * @return String faces navigation string to reload the view item page
     */
    public String expandCollapseSourceElements()
    {
        String sourceID = "";
        String element = "";
        sourceID = getFacesParamValue("sourceID");
        element = getFacesParamValue("element");
        if (element.equals(SourceUI.getElementAlternativeTitle()))
        {
            expandCollapseSourceAlternativeTitle(sourceID);
        }
        if (element.equals(SourceUI.getElementCreator()))
        {
            expandCollapseSourceCreators(sourceID);
        }
        if (element.equals(SourceUI.getElementSources()))
        {
            expandCollapseSourceOfSource(sourceID);
        }
        createFiles();
        this.panSources = (HtmlPanelGrid)FacesContext.getCurrentInstance().getViewRoot().findComponent(
                "form1:panSources");
        if (this.panSources != null)
        {
            this.panSources.getChildren().clear();
            for (int i = 0; i < this.sourceArray.size(); i++)
            {
                de.mpg.escidoc.pubman.viewItem.ui.SourceUI sourceUI = new de.mpg.escidoc.pubman.viewItem.ui.SourceUI(
                        this.sourceArray.get(i), this.itemSourceList.get(i));
                this.panSources.getChildren().add(sourceUI.getUIComponent());
                if (this.itemSourceList.get(i).isSourcesOfSourceCollapsed() == false)
                {
                    if (this.sourceArray.get(i).getSources() != null)
                    {
                        for (int j = 0; j < this.sourceArray.get(i).getSources().size(); j++)
                        {
                            sourceUI = new de.mpg.escidoc.pubman.viewItem.ui.SourceUI(this.sourceArray.get(i)
                                    .getSources().get(j), this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j));
                            this.panSources.getChildren().add(sourceUI.getUIComponent());
                            if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                    .isSourcesOfSourceCollapsed() == false)
                            {
                                if (this.sourceArray.get(i).getSources().get(j).getSources() != null)
                                {
                                    for (int k = 0; k < this.sourceArray.get(i).getSources().get(j).getSources().size(); k++)
                                    {
                                        sourceUI = new de.mpg.escidoc.pubman.viewItem.ui.SourceUI(this.sourceArray.get(
                                                i).getSources().get(j).getSources().get(k), this.itemSourceList.get(i)
                                                .getViewItemSourceEmbedded().get(j).getViewItemSourceEmbedded().get(k));
                                        this.panSources.getChildren().add(sourceUI.getUIComponent());
                                        if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                .getViewItemSourceEmbedded().get(k).isSourcesOfSourceCollapsed() == false)
                                        {
                                            if (this.sourceArray.get(i).getSources().get(j).getSources().get(k)
                                                    .getSources() != null)
                                            {
                                                for (int l = 0; l < this.sourceArray.get(i).getSources().get(j)
                                                        .getSources().get(k).getSources().size(); l++)
                                                {
                                                    sourceUI = new de.mpg.escidoc.pubman.viewItem.ui.SourceUI(
                                                            this.sourceArray.get(i).getSources().get(j).getSources()
                                                                    .get(k).getSources().get(l), this.itemSourceList
                                                                    .get(i).getViewItemSourceEmbedded().get(j)
                                                                    .getViewItemSourceEmbedded().get(k)
                                                                    .getViewItemSourceEmbedded().get(l));
                                                    this.panSources.getChildren().add(sourceUI.getUIComponent());
                                                    if (this.itemSourceList.get(i).getViewItemSourceEmbedded().get(j)
                                                            .getViewItemSourceEmbedded().get(k)
                                                            .getViewItemSourceEmbedded().get(l)
                                                            .isSourcesOfSourceCollapsed() == false)
                                                    {
                                                        if (this.sourceArray.get(i).getSources().get(j).getSources()
                                                                .get(k).getSources().get(l).getSources() != null)
                                                        {
                                                            for (int m = 0; m < this.sourceArray.get(i).getSources()
                                                                    .get(j).getSources().get(k).getSources().get(l)
                                                                    .getSources().size(); m++)
                                                            {
                                                                sourceUI = new de.mpg.escidoc.pubman.viewItem.ui.SourceUI(
                                                                        this.sourceArray.get(i).getSources().get(j)
                                                                                .getSources().get(k).getSources()
                                                                                .get(l).getSources().get(m),
                                                                        this.itemSourceList.get(i)
                                                                                .getViewItemSourceEmbedded().get(j)
                                                                                .getViewItemSourceEmbedded().get(k)
                                                                                .getViewItemSourceEmbedded().get(l)
                                                                                .getViewItemSourceEmbedded().get(m));
                                                                this.panSources.getChildren().add(
                                                                        sourceUI.getUIComponent());
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        this.panSources.setId(CommonUtils.createUniqueId(this.panSources));
        return "loadViewItem";
    }

    /**
     * Adds a cookie named "escidocCookie" that holds the eScidoc user handle to the provided http method object.
     * 
     * @param method The http method to add the cookie to.
     */
    private void addHandleToMethod(final HttpMethod method, String eSciDocUserHandle)
    {
        // Staging file resource is protected, access needs authentication and
        // authorization. Therefore, the eSciDoc user handle must be provided.
        // Put the handle in the cookie "escidocCookie"
        method.setRequestHeader("Cookie", "escidocCookie=" + eSciDocUserHandle);
    }

    /**
     * Returns a reference to the scoped data bean (the ViewItemSessionBean).
     * 
     * @return a reference to the scoped data bean
     */
    protected WithdrawItemSessionBean getWithdrawItemSessionBean()
    {
        return (WithdrawItemSessionBean)getBean(WithdrawItemSessionBean.class);
    }

    /**
     * Returns the affiliation refered in the collection of the item.
     * 
     * @return the requested affiliation name as string
     * @throws Exception if framework access in the itemControllerSessionBean fails
     */
    private String getAffiliation(String affiliationID) throws Exception
    {
        String affiliationName = "";
        AffiliationVOPresentation affiliation;
        // get the requested affiliation
        affiliation = new AffiliationVOPresentation(this.getItemControllerSessionBean().retrieveAffiliation(affiliationID));
        // extract the name of it
        affiliationName = affiliation.getDetails().getName();
        return affiliationName;
    }

    /**
     * Redirects the user to the edit item page
     * 
     * @return Sring nav rule to load the edit item page
     */
    public String editItem()
    {
        return EditItem.LOAD_EDITITEM;
    }

    /**
     * Redirects the user to the withdraw item page
     * 
     * @return Sring nav rule to load the withdraw item page
     */
    public String withdrawItem()
    {
        WithdrawItemSessionBean withdrawItemSessionBean = getWithdrawItemSessionBean();
        withdrawItemSessionBean.setNavigationStringToGoBack(getViewItemSessionBean().getNavigationStringToGoBack());
        withdrawItemSessionBean.setItemListSessionBean(getViewItemSessionBean().getItemListSessionBean());
        return WithdrawItem.LOAD_WITHDRAWITEM;
    }

    /**
     * submits the selected item(s) an redirects the user to the page he came from (depositor workspace or search result
     * list)
     * Changed by FrM: Inserted validation and call to "enter submission comment" page.
     * 
     * @return String nav rule to load the page the user came from
     */
    public String submitItem()
    {
        /*
         * FrM: Validation with validation point "submit_item"
         */
        
        ValidationReportVO report = null;
        try
        {
            report = this.itemValidating.validateItemObject(pubItem, "submit_item");
        }
        catch (Exception e)
        {
            throw new RuntimeException("Validation error", e);
        }
        logger.debug("Validation Report: " + report);
        
        if (report.isValid() && !report.hasItems()) {
       
            if (logger.isDebugEnabled())
            {
                logger.debug("Submitting item...");
            }
            getSubmitItemSessionBean().setNavigationStringToGoBack(getViewItemSessionBean().getNavigationStringToGoBack());
            return SubmitItem.LOAD_SUBMITITEM;
        }
        else if (report.isValid())
        {
            // TODO FrM: Informative messages
            getSubmitItemSessionBean().setNavigationStringToGoBack(getViewItemSessionBean().getNavigationStringToGoBack());
            return SubmitItem.LOAD_SUBMITITEM;
        }
        else
        {           
            // Item is invalid, do not submit anything.
            this.showValidationMessages(report);
            return null;
        }        
    }

    /**
     * deletes the selected item(s) an redirects the user to the page he came from (depositor workspace or search result
     * list)
     * 
     * @return String nav rule to load the page the user came from
     */
    public String deleteItem()
    {
        String retVal = this.getItemControllerSessionBean().deleteCurrentPubItem(
                this.getViewItemSessionBean().getNavigationStringToGoBack());
        // show message
        if (retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0)
        {
            if (this.getViewItemSessionBean().getNavigationStringToGoBack().equals(DepositorWS.LOAD_DEPOSITORWS))
            {
                info(getMessage(DepositorWS.MESSAGE_SUCCESSFULLY_DELETED));
            }
            else if (this.getViewItemSessionBean().getNavigationStringToGoBack().equals(
                    SearchResultList.LOAD_SEARCHRESULTLIST))
            {
                info(getMessage(DepositorWS.MESSAGE_SUCCESSFULLY_DELETED));
            
            }
        }
        return retVal;
    }

    /**
     * shows the next item in the list
     * 
     * @return String nav rule for reloading the view item page
     */
    public String showNextItem()
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        String selection = getSelectedItem();
        String origin = selection.substring(0, 3);
        String itemList = selection.substring(4, 7);
        int selected = new Integer(selection.substring(8)).intValue();
        if (origin.equals("DWS"))
        {
            if (itemList.equals("SPI"))
            {
                if (selected < this.getItemListSessionBean().getSelectedPubItems().size() - 1)
                {
                    this.getItemControllerSessionBean().setCurrentPubItem(
                            this.getItemListSessionBean().getSelectedPubItems().get(selected + 1));
                }
                else
                {
                    this.getItemControllerSessionBean().setCurrentPubItem(
                            this.getItemListSessionBean().getSelectedPubItems().get(0));
                }
            }
            else
            {
                if (selected < this.getItemListSessionBean().getCurrentPubItemList().size() - 1)
                {
                    this.getItemControllerSessionBean().setCurrentPubItem(
                            this.getItemListSessionBean().getCurrentPubItemList().get(selected + 1));
                }
                else
                {
                    this.getItemControllerSessionBean().setCurrentPubItem(
                            this.getItemListSessionBean().getCurrentPubItemList().get(0));
                }
            }
        }
        else if (origin.equals("SRL"))
        {
            if (itemList.equals("SPI"))
            {
                if (selected < this.getItemListSessionBean().getSelectedPubItems().size() - 1)
                {
                    this.getItemControllerSessionBean().setCurrentPubItem(
                            this.getItemListSessionBean().getSelectedPubItems().get(selected + 1));
                }
                else
                {
                    this.getItemControllerSessionBean().setCurrentPubItem(
                            this.getItemListSessionBean().getSelectedPubItems().get(0));
                }
            }
            else
            {
                if (selected < this.getItemListSessionBean().getCurrentPubItemList().size() - 1)
                {
                    this.getItemControllerSessionBean().setCurrentPubItem(
                            this.getItemListSessionBean().getCurrentPubItemList().get(selected + 1));
                }
                else
                {
                    this.getItemControllerSessionBean().setCurrentPubItem(
                            this.getItemListSessionBean().getCurrentPubItemList().get(0));
                }
            }
        }
        loadItem();
        try
        {
            fc.getExternalContext().redirect(
                    "viewItemPage.jsp?itemId="
                            + this.getItemControllerSessionBean().getCurrentPubItem().getVersion().getObjectId());
        }
        catch (IOException e)
        {
            logger.debug("Cannot redirect to view Item Page: " + e.toString());
        }
        return "loadViewItem";
    }

    /**
     * shows the previous item in the list
     * 
     * @return String nav rule for reloading the view item page
     */
    public String showPreviousItem()
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        String selection = getSelectedItem();
        String origin = selection.substring(0, 3);
        String itemList = selection.substring(4, 7);
        int selected = new Integer(selection.substring(8)).intValue();
        this.btnPreviousItem1.setDisabled(false);
        if (selected > 0)
        {
            if (origin.equals("DWS"))
            {
                if (itemList.equals("SPI"))
                {
                    this.getItemControllerSessionBean().setCurrentPubItem(
                            this.getItemListSessionBean().getSelectedPubItems().get(selected - 1));
                }
                else
                {
                    this.getItemControllerSessionBean().setCurrentPubItem(
                            this.getItemListSessionBean().getCurrentPubItemList().get(selected - 1));
                }
            }
            else if (origin.equals("SRL"))
            {
                if (itemList.equals("SPI"))
                {
                    this.getItemControllerSessionBean().setCurrentPubItem(
                            this.getItemListSessionBean().getSelectedPubItems().get(selected - 1));
                }
                else
                {
                    this.getItemControllerSessionBean().setCurrentPubItem(
                            this.getItemListSessionBean().getCurrentPubItemList().get(selected - 1));
                }
            }
        }
        else
        {
            if (origin.equals("DWS"))
            {
                if (itemList.equals("SPI"))
                {
                    this.getItemControllerSessionBean().setCurrentPubItem(
                            this.getItemListSessionBean().getSelectedPubItems().get(
                                    this.getItemListSessionBean().getSelectedPubItems().size() - 1));
                }
                else
                {
                    this.getItemControllerSessionBean().setCurrentPubItem(
                            this.getItemListSessionBean().getCurrentPubItemList().get(
                                    this.getItemListSessionBean().getCurrentPubItemList().size() - 1));
                }
            }
            else if (origin.equals("SRL"))
            {
                if (itemList.equals("SPI"))
                {
                    this.getItemControllerSessionBean().setCurrentPubItem(
                            this.getItemListSessionBean().getSelectedPubItems().get(
                                    this.getItemListSessionBean().getSelectedPubItems().size() - 1));
                }
                else
                {
                    this.getItemControllerSessionBean().setCurrentPubItem(
                            this.getItemListSessionBean().getCurrentPubItemList().get(
                                    this.getItemListSessionBean().getCurrentPubItemList().size() - 1));
                }
            }
        }
        loadItem();
        // disable the previous button if the first element in the list is
        // already displayed
        if (selected == 1)
        {
            this.btnPreviousItem1.setDisabled(true);
        }
        try
        {
            fc.getExternalContext().redirect(
                    "viewItemPage.jsp?itemId="
                            + this.getItemControllerSessionBean().getCurrentPubItem().getVersion().getObjectId());
        }
        catch (IOException e)
        {
            logger.debug("Cannot redirect to view Item Page: " + e.toString());
        }
        return "loadViewItem";
    }

    /**
     * redirects the user to the list he came from
     * 
     * @return String nav rule for loading the page the user came from
     */
    public String backToList()
    {
        return (this.getViewItemSessionBean().getNavigationStringToGoBack() != null ? this.getViewItemSessionBean()
                .getNavigationStringToGoBack() : ViewItem.LOAD_VIEWITEM);
    }

    /**
     * Returns the position of the selected item in the session bean's list.
     * 
     * @return String the position of the selected item as string
     */
    private String getSelectedItem()
    {
        String selection = "";
        // if the user has selected the items in the depositor workspace before
        if (this.getViewItemSessionBean().getNavigationStringToGoBack().equals(DepositorWS.LOAD_DEPOSITORWS))
        {
            if (this.getItemListSessionBean().getSelectedPubItems().size() > 0)
            {
                for (int i = 0; i < this.getItemListSessionBean().getSelectedPubItems().size(); i++)
                {
                    if (this.getItemListSessionBean().getSelectedPubItems().get(i).getVersion().getObjectId()
                            .equals(this.pubItem.getVersion().getObjectId()))
                    {
                        selection = "DWS_SPI_" + i;
                    }
                }
            }
            else
            {
                for (int i = 0; i < this.getItemListSessionBean().getCurrentPubItemList().size(); i++)
                {
                    if (this.getItemListSessionBean().getCurrentPubItemList().get(i).getVersion().getObjectId()
                            .equals(this.pubItem.getVersion().getObjectId()))
                    {
                        selection = "DWS_CPI_" + i;
                    }
                }
            }
        }
        // if the user has selected the items in the search result list before
        else if (this.getViewItemSessionBean().getNavigationStringToGoBack().equals(
                SearchResultList.LOAD_SEARCHRESULTLIST))
        {
            if (this.getItemListSessionBean().getSelectedPubItems().size() > 0)
            {
                for (int i = 0; i < this.getItemListSessionBean().getSelectedPubItems().size(); i++)
                {
                    if (this.getItemListSessionBean().getSelectedPubItems().get(i).getVersion().getObjectId()
                            .equals(this.pubItem.getVersion().getObjectId()))
                    {
                        selection = "SRL_SPI_" + i;
                    }
                }
            }
            else
            {
                for (int i = 0; i < this.getItemListSessionBean().getCurrentPubItemList().size(); i++)
                {
                    if (this.getItemListSessionBean().getCurrentPubItemList().get(i).getVersion()
                            .getObjectId().equals(this.pubItem.getVersion().getObjectId()))
                    {
                        selection = "SRL_CPI_" + i;
                    }
                }
            }
        }
        return selection;
    }

    /**
     * Displays validation messages.
     * 
     * @param report The Validation report object.
     * @author Michael Franke
     */
    private void showValidationMessages(ValidationReportVO report)
    {
        for (Iterator<ValidationReportItemVO> iter = report.getItems().iterator(); iter.hasNext();)
        {
            ValidationReportItemVO element = (ValidationReportItemVO)iter.next();
            if (element.isRestrictive())
            {
                error(getMessage(element.getContent()));
            }
            else
            {
                info(getMessage(element.getContent()));
            }
        }
        valMessage.setRendered(true);
    }

    /**
     * initiates a new submission and redirects the user to the edit item page
     * 
     * @return String nav rule for loading the edit item page
     */
    public String newSubmission()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("New Submission");
        }
        
        // if there is only one collection for this user we can skip the CreateItem-Dialog and create the new item directly
        if (this.getCollectionListSessionBean().getDepositorContextList().size() == 0)
        {
            logger.warn("The user does not have privileges for any context.");
            return null;
        }
        if (this.getCollectionListSessionBean().getDepositorContextList().size() == 1)
        {            
            ContextVO contextVO = this.getCollectionListSessionBean().getDepositorContextList().get(0);
            if (logger.isDebugEnabled())
            {
                logger.debug("The user has only privileges for one context (ID: " 
                        + contextVO.getReference().getObjectId() + ")");
            }
            
            return this.getItemControllerSessionBean().createNewPubItem(EditItem.LOAD_EDITITEM, contextVO.getReference());
        }
        else
        {
            // more than one context exists for this user; let him choose the right one
            if (logger.isDebugEnabled())
            {
                logger.debug("The user has privileges for " + this.getCollectionListSessionBean().getDepositorContextList().size() 
                        + " different contexts.");
            }

            return CreateItem.LOAD_CREATEITEM;
        }
    }

    /**
     * Generates the item citation URL (workaround!). This method has to be changed when PID can be assigned and stored
     * in the framework
     * 
     * @return String complete citation URL
     */
    private String getItemCitationURL()
    {
        String itemCitation = "";
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest)fc.getExternalContext().getRequest();
        // Path must be hardcoded due to jsf error (unsynchronized URL location
        // bar)
        CommonSessionBean commonBean = getCommonSessionBean();
        if (commonBean.isRunAsGUITool() == true)
        {
            itemCitation = "http://" + request.getLocalName() + ":" + request.getLocalPort() + request.getContextPath()
                    + "/tools/viewing/?" + PARAMETERNAME_ITEM_ID + "=" + this.pubItem.getVersion().getObjectId();
        }
        else
        {
            itemCitation = "http://" + request.getLocalName() + ":" + request.getLocalPort() + request.getContextPath()
                    + "/faces/viewItemPage.jsp?" + PARAMETERNAME_ITEM_ID + "="
                    + this.pubItem.getVersion().getObjectId();
        }
        return itemCitation;
    }

    /**
     * Generates the name for the item to be bookmarked
     * 
     * @return String bookmark title
     */
    private String getItemBMTitle()
    {
        String bookmarkTitle = "";
        bookmarkTitle = "PubMan Item " + this.pubItem.getMetadata().getTitle().getValue() + " (ID: "
                + this.pubItem.getVersion().getObjectId() + ")";
        return bookmarkTitle;
    }

    /**
     * Gets the withdrawal comment if the item to be viewed is already withdrawn
     * 
     * @return String formatted withdrawal comment and adte of withdrawal
     */
    private String getWithdrawalComment()
    {
        String comment = "";
        if (this.pubItem.getWithdrawalComment() != null && this.pubItem.getVersion().getState().equals(PubItemVO.State.WITHDRAWN))
        {
            if (!this.pubItem.getWithdrawalComment().equals(""))
            {
                comment = this.getLabel("ViewItem_lblWithdrawalComment") + " ("
                        + CommonUtils.format(this.pubItem.getModificationDate()) + "): "
                        + this.pubItem.getWithdrawalComment();
            }
        }
        return comment;
    }


    /**
     * Returns the ItemControllerSessionBean.
     * 
     * @return a reference to the scoped data bean (ItemControllerSessionBean)
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean)getBean(ItemControllerSessionBean.class);
    }

    /**
     * Returns a reference to the scoped data bean (the DepositorWSSessionBean).
     * 
     * @return a reference to the scoped data bean
     */
    protected ItemListSessionBean getItemListSessionBean()
    {
        return (ItemListSessionBean)getSessionBean(ItemListSessionBean.class);
    }

    /**
     * Returns the SearchResultListSessionBean.
     * 
     * @return a reference to the scoped data bean (SearchResultListSessionBean)
     */
    protected SearchResultListSessionBean getSearchResultListSessionBean()
    {
        return (SearchResultListSessionBean)getBean(SearchResultListSessionBean.class);
    }

    /**
     * Returns a reference to the scoped data bean (the ViewItemSessionBean).
     * 
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
     * Returns the ContextListSessionBean.
     * 
     * @return a reference to the scoped data bean (ContextListSessionBean)
     */
    protected ContextListSessionBean getCollectionListSessionBean()
    {
        return (ContextListSessionBean)getBean(ContextListSessionBean.class);
    }

    /**
     * Returns a reference to the scoped data bean (the CommonSessionBean).
     * 
     * @return a reference to the scoped data bean
     */
    protected CommonSessionBean getCommonSessionBean()
    {
        return (CommonSessionBean)getBean(CommonSessionBean.class);
    }

    // Getters and Setters
    public static String getBEAN_NAME()
    {
        return BEAN_NAME;
    }

    public static Logger getLogger()
    {
        return logger;
    }

    public static void setLogger(Logger logger)
    {
        ViewItem.logger = logger;
    }

    public ArrayList<String> getAbstractsArray()
    {
        return abstractsArray;
    }

    public void setAbstractsArray(ArrayList<String> abstractsArray)
    {
        this.abstractsArray = abstractsArray;
    }

    public boolean isAbstractsCollapsed()
    {
        return abstractsCollapsed;
    }

    public void setAbstractsCollapsed(boolean abstractsCollapsed)
    {
        this.abstractsCollapsed = abstractsCollapsed;
    }

    public UIColumn getAbstractsColumn()
    {
        return abstractsColumn;
    }

    public void setAbstractsColumn(UIColumn abstractsColumn)
    {
        this.abstractsColumn = abstractsColumn;
    }

    public HtmlDataTable getAbstractsTable()
    {
        return abstractsTable;
    }

    public void setAbstractsTable(HtmlDataTable abstractsTable)
    {
        this.abstractsTable = abstractsTable;
    }

    public HtmlOutputText getAbstractsText()
    {
        return abstractsText;
    }

    public void setAbstractsText(HtmlOutputText abstractsText)
    {
        this.abstractsText = abstractsText;
    }

    public String getAffiliatedOrganizations()
    {
        return affiliatedOrganizations;
    }

    public void setAffiliatedOrganizations(String affiliatedOrganizations)
    {
        this.affiliatedOrganizations = affiliatedOrganizations;
    }

    public List<OrganizationVO> getAffiliatedOrganizationsList()
    {
        return affiliatedOrganizationsList;
    }

    public void setAffiliatedOrganizationsList(List<OrganizationVO> affiliatedOrganizationsList)
    {
        this.affiliatedOrganizationsList = affiliatedOrganizationsList;
    }

    public ArrayList<String> getAlternativeTitlesArray()
    {
        return alternativeTitlesArray;
    }

    public void setAlternativeTitlesArray(ArrayList<String> alternativeTitlesArray)
    {
        this.alternativeTitlesArray = alternativeTitlesArray;
    }

    public HtmlCommandButton getBtnBackToList1()
    {
        return btnBackToList1;
    }

    public void setBtnBackToList1(HtmlCommandButton btnBackToList1)
    {
        this.btnBackToList1 = btnBackToList1;
    }

    public HtmlCommandButton getBtnNextItem1()
    {
        return btnNextItem1;
    }

    public void setBtnNextItem1(HtmlCommandButton btnNextItem1)
    {
        this.btnNextItem1 = btnNextItem1;
    }

    public HtmlCommandButton getBtnPreviousItem1()
    {
        return btnPreviousItem1;
    }

    public void setBtnPreviousItem1(HtmlCommandButton btnPreviousItem1)
    {
        this.btnPreviousItem1 = btnPreviousItem1;
    }

    public String getButtonMoreAbstracts()
    {
        return buttonMoreAbstracts;
    }

    public void setButtonMoreAbstracts(String buttonMoreAbstracts)
    {
        this.buttonMoreAbstracts = buttonMoreAbstracts;
    }

    public String getButtonMoreCreatorList()
    {
        return buttonMoreCreatorList;
    }

    public void setButtonMoreCreatorList(String buttonMoreCreatorList)
    {
        this.buttonMoreCreatorList = buttonMoreCreatorList;
    }

    public String getButtonMoreItemAlternativeTitles()
    {
        return buttonMoreItemAlternativeTitles;
    }

    public void setButtonMoreItemAlternativeTitles(String buttonMoreItemAlternativeTitles)
    {
        this.buttonMoreItemAlternativeTitles = buttonMoreItemAlternativeTitles;
    }

    public String getButtonMoreTOC()
    {
        return buttonMoreTOC;
    }

    public void setButtonMoreTOC(String buttonMoreTOC)
    {
        this.buttonMoreTOC = buttonMoreTOC;
    }

    public String getContentLanguage()
    {
        return contentLanguage;
    }

    public void setContentLanguage(String contentLanguage)
    {
        this.contentLanguage = contentLanguage;
    }

    public ArrayList<String> getCreatorArray()
    {
        return creatorArray;
    }

    public void setCreatorArray(ArrayList<String> creatorArray)
    {
        this.creatorArray = creatorArray;
    }

    public boolean isCreatorListCollapsed()
    {
        return creatorListCollapsed;
    }

    public void setCreatorListCollapsed(boolean creatorListCollapsed)
    {
        this.creatorListCollapsed = creatorListCollapsed;
    }

    public ArrayList<ViewItemCreatorOrganization> getCreatorOrganizationsArray()
    {
        return creatorOrganizationsArray;
    }

    public void setCreatorOrganizationsArray(ArrayList<ViewItemCreatorOrganization> creatorOrganizationsArray)
    {
        this.creatorOrganizationsArray = creatorOrganizationsArray;
    }

    public String getCreators()
    {
        return creators;
    }

    public void setCreators(String creators)
    {
        this.creators = creators;
    }

    public List<CreatorVO> getCreatorsList()
    {
        return creatorsList;
    }

    public void setCreatorsList(List<CreatorVO> creatorsList)
    {
        this.creatorsList = creatorsList;
    }

    public ArrayList<FileVO> getFileArray()
    {
        return fileArray;
    }

    public void setFileArray(ArrayList<FileVO> fileArray)
    {
        this.fileArray = fileArray;
    }

    public UIColumn getFileColumn()
    {
        return fileColumn;
    }

    public void setFileColumn(UIColumn fileColumn)
    {
        this.fileColumn = fileColumn;
    }

    public HtmlDataTable getFileTable()
    {
        return fileTable;
    }

    public void setFileTable(HtmlDataTable fileTable)
    {
        this.fileTable = fileTable;
    }

    public HtmlOutputText getFileText()
    {
        return fileText;
    }

    public void setFileText(HtmlOutputText fileText)
    {
        this.fileText = fileText;
    }

    public boolean isItemAlternativeTitlesCollapsed()
    {
        return itemAlternativeTitlesCollapsed;
    }

    public void setItemAlternativeTitlesCollapsed(boolean itemAlternativeTitlesCollapsed)
    {
        this.itemAlternativeTitlesCollapsed = itemAlternativeTitlesCollapsed;
    }

    public ArrayList<String> getItemIdentifierArray()
    {
        return itemIdentifierArray;
    }

    public void setItemIdentifierArray(ArrayList<String> itemIdentifierArray)
    {
        this.itemIdentifierArray = itemIdentifierArray;
    }

    public UIColumn getItemIdentifierColumn()
    {
        return itemIdentifierColumn;
    }

    public void setItemIdentifierColumn(UIColumn itemIdentifierColumn)
    {
        this.itemIdentifierColumn = itemIdentifierColumn;
    }

    public HtmlDataTable getItemIdentifierTable()
    {
        return itemIdentifierTable;
    }

    public void setItemIdentifierTable(HtmlDataTable itemIdentifierTable)
    {
        this.itemIdentifierTable = itemIdentifierTable;
    }

    public HtmlOutputText getItemIdentifierText()
    {
        return itemIdentifierText;
    }

    public void setItemIdentifierText(HtmlOutputText itemIdentifierText)
    {
        this.itemIdentifierText = itemIdentifierText;
    }

    public ArrayList<ViewItemSource> getItemSourceList()
    {
        return itemSourceList;
    }

    public void setItemSourceList(ArrayList<ViewItemSource> itemSourceList)
    {
        this.itemSourceList = itemSourceList;
    }

    public HtmlCommandLink getLnkDelete()
    {
        return lnkDelete;
    }

    public void setLnkDelete(HtmlCommandLink lnkDelete)
    {
        this.lnkDelete = lnkDelete;
    }

    public HtmlCommandLink getLnkEdit()
    {
        return lnkEdit;
    }

    public void setLnkEdit(HtmlCommandLink lnkEdit)
    {
        this.lnkEdit = lnkEdit;
    }

    public HtmlCommandLink getLnkWithdraw()
    {
        return lnkWithdraw;
    }

    public void setLnkWithdraw(HtmlCommandLink lnkWithdraw)
    {
        this.lnkWithdraw = lnkWithdraw;
    }

    public HtmlCommandLink getLnkNewSubmission()
    {
        return lnkNewSubmission;
    }

    public void setLnkNewSubmission(HtmlCommandLink lnkNewSubmission)
    {
        this.lnkNewSubmission = lnkNewSubmission;
    }

    public HtmlCommandLink getLnkSubmit()
    {
        return lnkSubmit;
    }

    public void setLnkSubmit(HtmlCommandLink lnkSubmit)
    {
        this.lnkSubmit = lnkSubmit;
    }

    public int getNumberCharsAbstractsCollapsed()
    {
        return numberCharsAbstractsCollapsed;
    }

    public void setNumberCharsAbstractsCollapsed(int numberCharsAbstractsCollapsed)
    {
        this.numberCharsAbstractsCollapsed = numberCharsAbstractsCollapsed;
    }

    public int getNumberCharsTOCCollapsed()
    {
        return numberCharsTOCCollapsed;
    }

    public void setNumberCharsTOCCollapsed(int numberCharsTOCCollapsed)
    {
        this.numberCharsTOCCollapsed = numberCharsTOCCollapsed;
    }

    public int getNumberCreatorListCollapsed()
    {
        return numberCreatorListCollapsed;
    }

    public void setNumberCreatorListCollapsed(int numberCreatorListCollapsed)
    {
        this.numberCreatorListCollapsed = numberCreatorListCollapsed;
    }

    public int getNumberItemAlternativeTitlesCollapsed()
    {
        return numberItemAlternativeTitlesCollapsed;
    }

    public void setNumberItemAlternativeTitlesCollapsed(int numberItemAlternativeTitlesCollapsed)
    {
        this.numberItemAlternativeTitlesCollapsed = numberItemAlternativeTitlesCollapsed;
    }

    public ArrayList<String> getOrganizationArray()
    {
        return organizationArray;
    }

    public void setOrganizationArray(ArrayList<String> organizationArray)
    {
        this.organizationArray = organizationArray;
    }

    public ArrayList<ViewItemOrganization> getOrganizationList()
    {
        return organizationList;
    }

    public void setOrganizationList(ArrayList<ViewItemOrganization> organizationList)
    {
        this.organizationList = organizationList;
    }

    public AccountUserVO getOwner()
    {
        return owner;
    }

    public void setOwner(AccountUserVO owner)
    {
        this.owner = owner;
    }

    public HtmlPanelGrid getPanFiles()
    {
        return panFiles;
    }

    public void setPanFiles(HtmlPanelGrid panFiles)
    {
        this.panFiles = panFiles;
    }

    public HtmlPanelGrid getPanSources()
    {
        return panSources;
    }

    public void setPanSources(HtmlPanelGrid panSources)
    {
        this.panSources = panSources;
    }

    public ContextVO getPubCollection()
    {
        return context;
    }

    public void setPubCollection(ContextVO context)
    {
        this.context = context;
    }

    public PubItemVO getPubItem()
    {
        return pubItem;
    }

    public void setPubItem(PubItemVO pubItem)
    {
        this.pubItem = pubItem;
    }

    public ArrayList<String> getRelationArray()
    {
        return relationArray;
    }

    public void setRelationArray(ArrayList<String> relationArray)
    {
        this.relationArray = relationArray;
    }

    public UIColumn getRelationColumn()
    {
        return relationColumn;
    }

    public void setRelationColumn(UIColumn relationColumn)
    {
        this.relationColumn = relationColumn;
    }

    public HtmlDataTable getRelationTable()
    {
        return relationTable;
    }

    public void setRelationTable(HtmlDataTable relationTable)
    {
        this.relationTable = relationTable;
    }

    public HtmlOutputText getRelationText()
    {
        return relationText;
    }

    public void setRelationText(HtmlOutputText relationText)
    {
        this.relationText = relationText;
    }

    public ArrayList<SourceVO> getSourceArray()
    {
        return sourceArray;
    }

    public void setSourceArray(ArrayList<SourceVO> sourceArray)
    {
        this.sourceArray = sourceArray;
    }

    public String getTableOfContents()
    {
        return tableOfContents;
    }

    public void setTableOfContents(String tableOfContents)
    {
        this.tableOfContents = tableOfContents;
    }

    public boolean isTocCollapsed()
    {
        return tocCollapsed;
    }

    public void setTocCollapsed(boolean tocCollapsed)
    {
        this.tocCollapsed = tocCollapsed;
    }

    public ViewItemOrganization getViewOrganisation()
    {
        return viewOrganisation;
    }

    public void setViewOrganisation(ViewItemOrganization viewOrganisation)
    {
        this.viewOrganisation = viewOrganisation;
    }

    public void setAffiliationOfCollection(String affiliationOfCollection)
    {
        this.affiliationOfContext = affiliationOfCollection;
    }

    public String getItemCitation()
    {
        return itemCitation;
    }

    public void setItemCitation(String itemCitation)
    {
        this.itemCitation = itemCitation;
    }

    public String getItemBookmarkTitle()
    {
        return itemBookmarkTitle;
    }

    public void setItemBookmarkTitle(String itemBookmarkTitle)
    {
        this.itemBookmarkTitle = itemBookmarkTitle;
    }

    public String getButtonMoreItemEventTitles()
    {
        return buttonMoreItemEventTitles;
    }

    public void setButtonMoreItemEventTitles(String buttonMoreItemEventTitles)
    {
        this.buttonMoreItemEventTitles = buttonMoreItemEventTitles;
    }

    public ArrayList<String> getEventTitleArray()
    {
        return eventTitleArray;
    }

    public void setEventTitleArray(ArrayList<String> eventTitleArray)
    {
        this.eventTitleArray = eventTitleArray;
    }

    /**
     * Gets the formatted event date of the pubitem
     * 
     * @return String formatted event date
     */
    public String getDateEvent()
    {
        String dateEvent = new String();
        if (this.pubItem.getMetadata().getEvent() != null)
        {
            if (this.pubItem.getMetadata().getEvent().getStartDate() != null)
            {
                dateEvent = this.pubItem.getMetadata().getEvent().getStartDate();
            }
            if (this.pubItem.getMetadata().getEvent().getEndDate() != null)
            {
                if (!this.pubItem.getMetadata().getEvent().getEndDate().toString().equals(""))
                {
                    dateEvent += " - ";
                    dateEvent += this.pubItem.getMetadata().getEvent().getEndDate();
                }
            }
        }
        return dateEvent;
    }

    public HtmlMessages getValMessage()
    {
        return valMessage;
    }

    public void setValMessage(HtmlMessages valMessage)
    {
        this.valMessage = valMessage;
    }

    public String getValWithdrawalComment()
    {
        return valWithdrawalComment;
    }

    public void setValWithdrawalComment(String valWithdrawalComment)
    {
        this.valWithdrawalComment = valWithdrawalComment;
    }
}
