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

package de.mpg.escidoc.pubman.desktop;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.ViewItemRevisionsPage;
import de.mpg.escidoc.pubman.affiliation.AffiliationSessionBean;
import de.mpg.escidoc.pubman.affiliation.AffiliationTree;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.collectionList.CollectionListSessionBean;
import de.mpg.escidoc.pubman.createItem.CreateItem;
import de.mpg.escidoc.pubman.depositorWS.DepositorWS;
import de.mpg.escidoc.pubman.depositorWS.DepositorWSSessionBean;
import de.mpg.escidoc.pubman.editItem.EditItem;
import de.mpg.escidoc.pubman.home.Home;
import de.mpg.escidoc.pubman.releases.ReleaseHistory;
import de.mpg.escidoc.pubman.releases.ReleasesSessionBean;
import de.mpg.escidoc.pubman.revisions.CreateRevision;
import de.mpg.escidoc.pubman.revisions.RevisionListSessionBean;
import de.mpg.escidoc.pubman.search.AdvancedSearchEdit;
import de.mpg.escidoc.pubman.search.SearchResultList;
import de.mpg.escidoc.pubman.util.NavigationRule;
import de.mpg.escidoc.pubman.viewItem.ViewItemFull;
import de.mpg.escidoc.services.common.valueobjects.PubCollectionVO;

/**
 * Navigation.java Backing Bean for the Navigation side bar of pubman. Additionally there is some internationalization
 * functionality (language switching).
 *
 * @author: Tobias Schraut, created 30.05.2007
 * @version: $Revision: 1695 $ $LastChangedDate: 2007-12-18 14:25:56 +0100 (Di, 18 Dez 2007) $ Revised by ScT: 16.08.2007
 */
public class Navigation extends FacesBean
{
    private static Logger logger = Logger.getLogger(Navigation.class);
    private List<NavigationRule> navRules;

    /** identifier from the breadcrump component in the page */

    private DepositorWSSessionBean depositorWSSessionBean
        = (DepositorWSSessionBean) getBean(DepositorWSSessionBean.class);
    /**
     * Public constructor.
     */
    public Navigation()
    {
        this.init();
    }

    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation.
     */
    public void init()
    {

        // Perform initializations inherited from our superclass
        super.init();
        // initially sets the navigation rules for redirecting after changing the language
        navRules = new ArrayList<NavigationRule>();
        this.navRules.add(new NavigationRule("/faces/HomePage.jsp",
                Home.LOAD_HOME));
        this.navRules.add(new NavigationRule("/faces/DepositorWSPage.jsp",
                DepositorWS.LOAD_DEPOSITORWS));
        this.navRules.add(new NavigationRule("/faces/EditItemPage.jsp",
                EditItem.LOAD_EDITITEM));
        this.navRules.add(new NavigationRule("/faces/viewItemFullPage.jsp",
                ViewItemFull.LOAD_VIEWITEM));
        this.navRules.add(new NavigationRule("/faces/SearchResultListPage.jsp",
                SearchResultList.LOAD_SEARCHRESULTLIST));
        this.navRules.add(new NavigationRule("/faces/AffiliationTreePage.jsp",
                AffiliationTree.LOAD_AFFILIATIONTREE));
        this.navRules.add(new NavigationRule("/faces/AffiliationSearchResultListPage.jsp",
                SearchResultList.LOAD_AFFILIATIONSEARCHRESULTLIST));
        this.navRules.add(new NavigationRule("/faces/ViewItemRevisionsPage.jsp",
                ViewItemRevisionsPage.LOAD_VIEWREVISIONS));
        this.navRules.add(new NavigationRule("/faces/ViewItemReleaseHistoryPage.jsp",
                ReleaseHistory.LOAD_RELEASE_HISTORY));
        this.navRules.add(new NavigationRule("/faces/AdvancedSearchPage.jsp",
                AdvancedSearchEdit.LOAD_SEARCHPAGE));
    }

    /**
     * loads the home page.
     *
     * @return String navigation string (JSF navigation) to load the home page.
     */
    public String loadHome()
    {
        return Home.LOAD_HOME;
    }

    /**
     * loads the affiliation tree page.
     *
     * @return String navigation string (JSF navigation) to load the affiliation tree page.
     */
    public String loadAffiliationTree()
    {
        this.getAffiliationSessionBean().setBrowseByAffiliation(false);
        this.getAffiliationSessionBean().setWasInit(false);
        return AffiliationTree.LOAD_AFFILIATIONTREE;
    }

    /**
     * loads the help page.
     *
     * @return String navigation string (JSF navigation) to load the help page.
     */
    public String loadHelp()
    {
        return "loadHelp";
    }

    /**
     * Changes the language within the application. Some classes have to be treated especially.
     *
     * @return String navigation string (JSF navigation) to reload the page the user has been when changing the language
     */
    public String changeLanguage()
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
        // initialize the nav string with null. if it won't be changed the page would just be reloaded
        String navigationString = "";

        DepositorWS depositorWorkspace;
        ViewItemFull viewItem;
        SearchResultList searchResultList;
        EditItem editItem;
        AffiliationTree affiliationTree;
        CreateRevision createRevision;
        ReleaseHistory releaseHistory;

        // special re-initializaion for pages with dynamic page elements which
        // must be re-inited

        logger.debug("Resolving current page URI: " + request.getRequestURI());

        for (int i = 0; i < navRules.size(); i++)
        {
            if (request.getRequestURI().equals(navRules.get(i).getRequestURL()))
            {
                navigationString = navRules.get(i).getNavigationString();
                break;
            }
        }
        if (navigationString.equals(EditItem.LOAD_EDITITEM))
        {
            editItem = (EditItem) getBean(EditItem.class);
            editItem.init();
            editItem.resetDynamicPanels();
        }
        else if (navigationString.equals(DepositorWS.LOAD_DEPOSITORWS))
        {
            depositorWorkspace = (DepositorWS) getBean(DepositorWS.class);
            this.getDepositorWSSessionBean().setListDirty(true);
            depositorWorkspace.init();
        }
        else if (navigationString.equals(ViewItemFull.LOAD_VIEWITEM))
        {
            viewItem = (ViewItemFull) getBean(ViewItemFull.class);
            viewItem.init();
        }
        else if (navigationString.equals(SearchResultList.LOAD_SEARCHRESULTLIST))
        {
            searchResultList = (SearchResultList) getBean(SearchResultList.class);
            searchResultList.init();
        }
        else if (navigationString.equals(AffiliationTree.LOAD_AFFILIATIONTREE))
        {
            // DiT: added for reload of the AffiliationTree
            affiliationTree = (AffiliationTree) getBean(AffiliationTree.class);
            this.getAffiliationSessionBean().setBrowseByAffiliation(false);
            affiliationTree.init();
        }
        else if (navigationString.equals(ViewItemRevisionsPage.LOAD_VIEWREVISIONS))
        {
            this.getRevisionListSessionBean().setRevisisonListUI(null);
            createRevision = (CreateRevision) getBean(CreateRevision.class);
            createRevision.init();
        }
        else if (navigationString.equals(ReleaseHistory.LOAD_RELEASE_HISTORY))
        {
            this.getReleasesSessionBean().setReleaseListUI(null);
            releaseHistory = (ReleaseHistory) getBean(ReleaseHistory.class);
            releaseHistory.init();
        }
        else
        {
            navigationString = null;
        }
        return navigationString;
    }

    /**
     * Starts a new submission.
     *
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String newSubmission()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("New Submission");
        }

        // force reload of list next time this page is navigated to
        this.getDepositorWSSessionBean().setListDirty(true);

        // if there is only one collection for this user we can skip the
        // CreateItem-Dialog and create the new item directly
        if (this.getCollectionListSessionBean().getCollectionList().size() == 0)
        {
            logger.warn("The user does not have privileges for any collection.");
            return null;
        }
        if (this.getCollectionListSessionBean().getCollectionList().size() == 1)
        {
            PubCollectionVO pubCollectionVO = this.getCollectionListSessionBean().getCollectionList().get(0);
            if (logger.isDebugEnabled())
            {
                logger.debug("The user has only privileges for one collection (ID: "
                        + pubCollectionVO.getReference().getObjectId() + ")");
            }

            return this.getItemControllerSessionBean().createNewPubItem(EditItem.LOAD_EDITITEM,
                    pubCollectionVO.getReference());
        }
        else
        {
            // more than one collection exists for this user; let him choose the right one
            if (logger.isDebugEnabled())
            {
                logger.debug("The user has privileges for "
                        + this.getCollectionListSessionBean().getCollectionList().size()
                        + " different collections.");
            }

            //refresh ListUI
            this.getCollectionListSessionBean().setCollectionListUI(null);

            return CreateItem.LOAD_CREATEITEM;
        }
    }

    /**
     * Gets the parameters out of the faces context.
     *
     * @return The value
     */
    public static String getFacesParamValue(final String name)
    {
        return (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(name);
    }


    /**
     * Returns the AffiliationSessionBean.
     *
     * @return a reference to the scoped data bean (AffiliationSessionBean)
     */
    protected AffiliationSessionBean getAffiliationSessionBean()
    {
        return (AffiliationSessionBean) getBean(AffiliationSessionBean.class);
    }

    /**
     * Returns the ReleasesSessionBean.
     *
     * @return a reference to the scoped data bean (ReleasesSessionBean)
     */
    protected ReleasesSessionBean getReleasesSessionBean()
    {
        return (ReleasesSessionBean) getBean(ReleasesSessionBean.class);
    }

    /**
     * Returns the RevisionListSessionBean.
     *
     * @return a reference to the scoped data bean (RevisionListSessionBean)
     */
    protected RevisionListSessionBean getRevisionListSessionBean()
    {
        return (RevisionListSessionBean) getBean(RevisionListSessionBean.class);
    }

    /**
     * Returns the DepositorWSSessionBean.
     *
     * @return a reference to the scoped data bean (DepositorWSSessionBean)
     */
    protected DepositorWSSessionBean getDepositorWSSessionBean()
    {
        return depositorWSSessionBean;
    }

    /**
     * Returns the CollectionListSessionBean.
     *
     * @return a reference to the scoped data bean (CollectionListSessionBean)
     */
    protected CollectionListSessionBean getCollectionListSessionBean()
    {
        return (CollectionListSessionBean) getBean(CollectionListSessionBean.class);
    }

    /**
     * Returns a reference to the scoped data bean (the ItemControllerSessionBean).
     * @return a reference to the scoped data bean.
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean) getBean(ItemControllerSessionBean.class);
    }

    /**
     * Returns the AdvancedSearchEdit session bean.
     * @return AdvancedSearchEdit session bean.
     */
    protected AdvancedSearchEdit getAdvancedSearchEdit()
    {
        return (AdvancedSearchEdit) getBean(AdvancedSearchEdit.class);
    }

    /**
     * Returns the SearchResultList session bean.
     * @return AdvancedSearchEdit session bean.
     */
    protected SearchResultList getSearchResultList()
    {
        return (SearchResultList) getBean(SearchResultList.class);
    }

}
