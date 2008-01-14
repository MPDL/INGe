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

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.apache.log4j.Logger;
import com.sun.rave.web.ui.appbase.AbstractApplicationBean;
import com.sun.rave.web.ui.model.Option;
import de.mpg.escidoc.pubman.exceptions.PubManVersionNotAvailableException;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.util.CommonUtils;
import de.mpg.escidoc.services.common.valueobjects.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.comparator.PubItemVOComparator;
import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;

/**
 * ApplicationBean which stores all application wide values.
 * 
 * @author: Thomas Diebäcker, created 09.08.2007
 * @version: $Revision: 1700 $ $LastChangedDate: 2007-12-18 16:18:16 +0100 (Tue, 18 Dec 2007) $
 * Revised by DiT: 09.08.2007
 */
public class ApplicationBean extends AbstractApplicationBean
{
    public static final String BEAN_NAME = "ApplicationBean";
    private static Logger logger = Logger.getLogger(ApplicationBean.class);

    private final String APP_TITLE = "Publication Manager";
    private String appTitle = null;
    private String appContext = "";
    
    // for handling the resource bundles (i18n)
    private Application application = FacesContext.getCurrentInstance().getApplication();
    // get the selected language...
    private InternationalizationHelper i18nHelper = (InternationalizationHelper)application.getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
    // ... and set the refering resource bundle 
    private ResourceBundle bundleLabel = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());

    // entry when no item in the comboBox is selected
    private SelectItem NO_ITEM_SET = new Option("", bundleLabel.getString("EditItem_NO_ITEM_SET"));

    // enum for select items
    public enum SelectMultipleItems
    {
        SELECT_ITEMS, SELECT_ALL, DESELECT_ALL, SELECT_VISIBLE
    }
    
    // HashMaps for Mapping values to localized strings
    private HashMap<MdsPublicationVO.Genre, String> genreToResourceBundleString = new HashMap<MdsPublicationVO.Genre, String>();
    private HashMap<MdsPublicationVO.DegreeType, String> degreeTypeToResourceBundleString = new HashMap<MdsPublicationVO.DegreeType, String>();
    private HashMap<MdsPublicationVO.ReviewMethod, String> reviewMethodToResourceBundleString = new HashMap<MdsPublicationVO.ReviewMethod, String>();
    private HashMap<EventVO.InvitationStatus, String> invitationStatusToResourceBundleString = new HashMap<EventVO.InvitationStatus, String>();
    private HashMap<PubItemVO.State, String> stateToResourceBundleString = new HashMap<PubItemVO.State, String>();
    private HashMap<PubItemVOComparator.Criteria, String> itemListSortByToResourceBundleString = new HashMap<PubItemVOComparator.Criteria, String>();    
    private HashMap<ApplicationBean.SelectMultipleItems, String> itemListSelectMultipleItemsToResourceBundleString = new HashMap<ApplicationBean.SelectMultipleItems, String>();
    private HashMap<SourceVO.Genre, String> genreOfSourceToResourceBundleString = new HashMap<SourceVO.Genre, String>();

    /**
     * Public constructor.
     */
    public ApplicationBean()
    {
        this.createHashMapsForEnums();
    }

    /**
     * This method is called when this bean is initially added to application scope. Typically, this occurs as a result of
     * evaluating a value binding or method binding expression, which utilizes the managed bean facility to instantiate
     * this bean and store it into application scope.
     */    
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
    }

    /**
     * Creates the HashMaps for enums for ComboBoxes.
     */
    private void createHashMapsForEnums()
    {
        // genre    
        this.genreToResourceBundleString.put(MdsPublicationVO.Genre.ARTICLE, "EditItem_GENRE_ARTICLE");
        this.genreToResourceBundleString.put(MdsPublicationVO.Genre.BOOK, "EditItem_GENRE_BOOK");
        this.genreToResourceBundleString.put(MdsPublicationVO.Genre.BOOK_ITEM, "EditItem_GENRE_BOOK_ITEM");
        this.genreToResourceBundleString.put(MdsPublicationVO.Genre.PROCEEDINGS, "EditItem_GENRE_PROCEEDINGS");
        this.genreToResourceBundleString.put(MdsPublicationVO.Genre.CONFERENCE_PAPER, "EditItem_GENRE_CONFERENCE_PAPER");
        this.genreToResourceBundleString.put(MdsPublicationVO.Genre.TALK_AT_EVENT, "EditItem_GENRE_TALK_AT_EVENT");
        this.genreToResourceBundleString.put(MdsPublicationVO.Genre.CONFERENCE_REPORT, "EditItem_GENRE_CONFERENCE_REPORT");
        this.genreToResourceBundleString.put(MdsPublicationVO.Genre.POSTER, "EditItem_GENRE_POSTER");
        this.genreToResourceBundleString.put(MdsPublicationVO.Genre.COURSEWARE_LECTURE, "EditItem_GENRE_COURSEWARE_LECTURE");
        this.genreToResourceBundleString.put(MdsPublicationVO.Genre.THESIS, "EditItem_GENRE_THESIS");
        this.genreToResourceBundleString.put(MdsPublicationVO.Genre.PAPER, "EditItem_GENRE_PAPER");
        this.genreToResourceBundleString.put(MdsPublicationVO.Genre.REPORT, "EditItem_GENRE_REPORT");
        this.genreToResourceBundleString.put(MdsPublicationVO.Genre.ISSUE, "EditItem_GENRE_ISSUE");
        this.genreToResourceBundleString.put(MdsPublicationVO.Genre.JOURNAL, "EditItem_GENRE_JOURNAL");
        this.genreToResourceBundleString.put(MdsPublicationVO.Genre.SERIES, "EditItem_GENRE_SERIES");
        this.genreToResourceBundleString.put(MdsPublicationVO.Genre.OTHER, "EditItem_GENRE_OTHER");
        this.genreToResourceBundleString.put(MdsPublicationVO.Genre.MANUSCRIPT, "EditItem_GENRE_MANUSCRIPT");
        // degreeType
        this.degreeTypeToResourceBundleString.put(MdsPublicationVO.DegreeType.MASTER, "EditItem_DEGREETYPE_MASTER");
        this.degreeTypeToResourceBundleString.put(MdsPublicationVO.DegreeType.DIPLOMA, "EditItem_DEGREETYPE_DIPLOMA");
        this.degreeTypeToResourceBundleString.put(MdsPublicationVO.DegreeType.MAGISTER, "EditItem_DEGREETYPE_MAGISTER");
        this.degreeTypeToResourceBundleString.put(MdsPublicationVO.DegreeType.PHD, "EditItem_DEGREETYPE_PHD");
        this.degreeTypeToResourceBundleString.put(MdsPublicationVO.DegreeType.STAATSEXAMEN, "EditItem_DEGREETYPE_STAATSEXAMEN");
        this.degreeTypeToResourceBundleString.put(MdsPublicationVO.DegreeType.HABILITATION, "EditItem_DEGREETYPE_HABILITATION");
        // reviewMethod
        this.reviewMethodToResourceBundleString.put(MdsPublicationVO.ReviewMethod.INTERNAL, "EditItem_REVIEWMETHOD_INTERNAL");
        this.reviewMethodToResourceBundleString.put(MdsPublicationVO.ReviewMethod.PEER, "EditItem_REVIEWMETHOD_PEER");
        this.reviewMethodToResourceBundleString.put(MdsPublicationVO.ReviewMethod.NO_REVIEW, "EditItem_REVIEWMETHOD_NO_REVIEW");
        // invitationstatus
        this.invitationStatusToResourceBundleString.put(EventVO.InvitationStatus.INVITED, "EditItem_INVITATIONSTATUS_INVITED");
        // state
        this.stateToResourceBundleString.put(PubItemVO.State.PENDING, "depositorWS_ItemState_pending");
        this.stateToResourceBundleString.put(PubItemVO.State.SUBMITTED, "depositorWS_ItemState_submitted");
        this.stateToResourceBundleString.put(PubItemVO.State.RELEASED, "depositorWS_ItemState_released");
        this.stateToResourceBundleString.put(PubItemVO.State.WITHDRAWN, "depositorWS_ItemState_withdrawn");
        this.stateToResourceBundleString.put(PubItemVO.State.IN_REVISION, "depositorWS_ItemState_inRevision");
        // sortBy
        this.itemListSortByToResourceBundleString.put(PubItemVOComparator.Criteria.DATE, "ItemListSortingCriteria_SORTBY_DATE");
        this.itemListSortByToResourceBundleString.put(PubItemVOComparator.Criteria.TITLE, "ItemListSortingCriteria_SORTBY_TITLE");
        this.itemListSortByToResourceBundleString.put(PubItemVOComparator.Criteria.GENRE, "ItemListSortingCriteria_SORTBY_GENRE");
        this.itemListSortByToResourceBundleString.put(PubItemVOComparator.Criteria.CREATOR, "ItemListSortingCriteria_SORTBY_CREATOR");
        this.itemListSortByToResourceBundleString.put(PubItemVOComparator.Criteria.PUBLISHING_INFO, "ItemListSortingCriteria_SORTBY_PUBLISHING_INFO");
        this.itemListSortByToResourceBundleString.put(PubItemVOComparator.Criteria.REVIEW_METHOD, "ItemListSortingCriteria_SORTBY_REVIEWMETHOD");
        this.itemListSortByToResourceBundleString.put(PubItemVOComparator.Criteria.SOURCE_CREATOR, "ItemListSortingCriteria_SORTBY_SOURCECREATOR");
        this.itemListSortByToResourceBundleString.put(PubItemVOComparator.Criteria.SOURCE_TITLE, "ItemListSortingCriteria_SORTBY_SOURCETITLE");
        this.itemListSortByToResourceBundleString.put(PubItemVOComparator.Criteria.EVENT_TITLE, "ItemListSortingCriteria_SORTBY_EVENTTITLE");
        // select multiple items
        this.itemListSelectMultipleItemsToResourceBundleString.put(ApplicationBean.SelectMultipleItems.SELECT_ITEMS, "ItemList_SelectItems");
        this.itemListSelectMultipleItemsToResourceBundleString.put(ApplicationBean.SelectMultipleItems.SELECT_ALL, "ItemList_SelectAll");
        this.itemListSelectMultipleItemsToResourceBundleString.put(ApplicationBean.SelectMultipleItems.DESELECT_ALL, "ItemList_DeselectAll");
        this.itemListSelectMultipleItemsToResourceBundleString.put(ApplicationBean.SelectMultipleItems.SELECT_VISIBLE, "ItemList_SelectVisible");
        // genre of source
        this.genreOfSourceToResourceBundleString.put(SourceVO.Genre.BOOK, "EditItem_GENRE_BOOK");        
        this.genreOfSourceToResourceBundleString.put(SourceVO.Genre.ISSUE, "EditItem_GENRE_ISSUE");
        this.genreOfSourceToResourceBundleString.put(SourceVO.Genre.JOURNAL, "EditItem_GENRE_JOURNAL");
        this.genreOfSourceToResourceBundleString.put(SourceVO.Genre.PROCEEDINGS, "EditItem_GENRE_PROCEEDINGS");
        this.genreOfSourceToResourceBundleString.put(SourceVO.Genre.SERIES, "EditItem_GENRE_SERIES");
    }
    
    /**
     * Returns an array of SelectItems for the enum genre.
     * @return array of SelectItems for genre
     */
    public SelectItem[] getSelectItemsGenre()
    {
        return this.getSelectItemsGenre(false);
    }

    /**
     * Returns an array of SelectItems for the enum genre.
     * @param includeNoItemSelectedEntry if true an entry for NoItemSelected is added
     * @return array of SelectItems for genre
     */
    public SelectItem[] getSelectItemsGenre(boolean includeNoItemSelectedEntry)
    {
        MdsPublicationVO.Genre[] values = MdsPublicationVO.Genre.values();
        
        SelectItem[] selectItems = new SelectItem[values.length];
        
        for (int i = 0; i < values.length; i++)
        {
            SelectItem selectItem = new SelectItem(values[i].toString(), this.convertEnumToString(values[i]));
            selectItems[i] = selectItem;
        }
        
        if (includeNoItemSelectedEntry)
        {
            selectItems = this.addNoItemSelectedEntry(selectItems);
        }

        return selectItems;
    }
    
    /**
     * Returns an array of SelectItems for the enum genre.
     * @return array of SelectItems for genre
     */
    public SelectItem[] getSelectItemsDegreeType()
    {
        return this.getSelectItemsGenre(false);
    }

    /**
     * Returns an array of SelectItems for the enum DegreeType.
     * @param includeNoItemSelectedEntry if true an entry for NoItemSelected is added
     * @return array of SelectItems for DegreeType
     */
    public SelectItem[] getSelectItemsDegreeType(boolean includeNoItemSelectedEntry)
    {
        MdsPublicationVO.DegreeType[] values = MdsPublicationVO.DegreeType.values();
        
        SelectItem[] selectItems = new SelectItem[values.length];

        for (int i = 0; i < values.length; i++)
        {
            SelectItem selectItem = new SelectItem(values[i].toString(), this.convertEnumToString(values[i]));
            selectItems[i] = selectItem;
        }
        
        if (includeNoItemSelectedEntry)
        {
            selectItems = this.addNoItemSelectedEntry(selectItems);
        }

        return selectItems;
    }

    /**
     * Returns an array of SelectItems for the enum genre.
     * @return array of SelectItems for genre
     */
    public SelectItem[] getSelectItemsReviewMethod()
    {
        return this.getSelectItemsGenre(false);
    }

    /**
     * Returns an array of SelectItems for the enum ReviewMethod.
     * @param includeNoItemSelectedEntry if true an entry for NoItemSelected is added
     * @return array of SelectItems for ReviewMethod
     */
    public SelectItem[] getSelectItemsReviewMethod(boolean includeNoItemSelectedEntry)
    {
        MdsPublicationVO.ReviewMethod[] values = MdsPublicationVO.ReviewMethod.values();
        
        SelectItem[] selectItems = new SelectItem[values.length];

        for (int i = 0; i < values.length; i++)
        {
            SelectItem selectItem = new SelectItem(values[i].toString(), this.convertEnumToString(values[i]));
            selectItems[i] = selectItem;
        }
        
        if (includeNoItemSelectedEntry)
        {
            selectItems = this.addNoItemSelectedEntry(selectItems);
        }

        return selectItems;
    }

    /**
     * Returns an array of SelectItems for the enum genre.
     * @return array of SelectItems for genre
     */
    public SelectItem[] getSelectItemsInvitationStatus()
    {
        return this.getSelectItemsGenre(false);
    }

    /**
     * Returns an array of SelectItems for the enum InvitationStatus.
     * @param includeNoItemSelectedEntry if true an entry for NoItemSelected is added
     * @return array of SelectItems for InvitationStatus
     */
    public SelectItem[] getSelectItemsInvitationStatus(boolean includeNoItemSelectedEntry)
    {
        EventVO.InvitationStatus[] values = EventVO.InvitationStatus.values();
        
        SelectItem[] selectItems = new SelectItem[values.length];

        for (int i = 0; i < values.length; i++)
        {
            SelectItem selectItem = new SelectItem(values[i].toString(), this.convertEnumToString(values[i]));
            selectItems[i] = selectItem;
        }
        
        if (includeNoItemSelectedEntry)
        {
            selectItems = this.addNoItemSelectedEntry(selectItems);
        }

        return selectItems;
    }

    /**
     * Returns an array of SelectItems for the enum ItemState.
     * @return array of SelectItems for ItemState
     */
    public SelectItem[] getSelectItemsItemState()
    {
        PubItemVO.State[] values = PubItemVO.State.values();
        
        SelectItem[] selectItems = new SelectItem[values.length + 1];

//      get the selected language
        Application application = FacesContext.getCurrentInstance().getApplication();
        // get the selected language...
        InternationalizationHelper i18nHelper = (InternationalizationHelper)application.getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
        // ... and set the refering resource bundle       
        ResourceBundle bundleLabel = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
                
        // ad an extra 'all', since it's not member of the enum
        selectItems[0] = new SelectItem("all", bundleLabel.getString("depositorWS_ItemState_all"));
        
        for (int i = 0; i < values.length; i++)
        {
            SelectItem selectItem = new SelectItem(values[i].toString(), this.convertEnumToString(values[i]));
            selectItems[i + 1] = selectItem;
        }

        return selectItems;
    }
    
    /**
     * Returns an array of SelectItems for the enum ItemListSortBy.
     * @return array of SelectItems for ItemListSortBy
     */
    public SelectItem[] getSelectItemsItemListSortBy()
    {
        PubItemVOComparator.Criteria[] values = PubItemVOComparator.Criteria.values();
        
        SelectItem[] selectItems = new SelectItem[values.length];

        for (int i = 0; i < values.length; i++)
        {
            SelectItem selectItem = new SelectItem(values[i].toString(), this.convertEnumToString(values[i]));
            selectItems[i] = selectItem;
        }
        
        return selectItems;
    }
        
    /**
     * Returns an array of SelectItems for the enum SelectMultipleItems.
     * @return array of SelectItems for SelectMultipleItems
     */
    public SelectItem[] getSelectItemsItemListSelectMultipleItems()
    {
        ApplicationBean.SelectMultipleItems[] values = ApplicationBean.SelectMultipleItems.values();
        
        SelectItem[] selectItems = new SelectItem[values.length];

        for (int i = 0; i < values.length; i++)
        {
            SelectItem selectItem = new SelectItem(values[i].toString(), this.convertEnumToString(values[i]));
            selectItems[i] = selectItem;
        }
        
        return selectItems;
    }

    /**
     * Adds an entry for NoItemSelected in front of the given array
     * @param selectItems the array where the entry should be added
     * @return a new array with an entry for NoItemSelected
     */
    private SelectItem[] addNoItemSelectedEntry(SelectItem[] selectItems)
    {
        SelectItem[] newSelectItems = new SelectItem[selectItems.length + 1];
        
        // add the entry for NoItemSelected in front of the array
        newSelectItems[0] = this.NO_ITEM_SET;
        for (int i = 0; i < selectItems.length; i++)
        {
            newSelectItems[i + 1] = selectItems[i];
        }
        
        return newSelectItems;
    }

    /**
     * Converts an enum to a String for output.
     * @param enumObject the enum to convert
     * @return the converted String for output
     */
    public String convertEnumToString(Object enumObject)
    {        
        String convertedEnum = null;
        
        // get the string for the resource bundle
        String resourceBundleString = this.convertEnumToResourceBundleString(enumObject);
        
        // get the selected language
        Application application = FacesContext.getCurrentInstance().getApplication();
        // get the selected language...
        InternationalizationHelper i18nHelper = (InternationalizationHelper)application.getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
        ResourceBundle bundleLabel = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());

        
        // get the output string from the resource bundle
        if (resourceBundleString != null)
        {
            convertedEnum = bundleLabel.getString(resourceBundleString);
        }
        
        return convertedEnum;
    }

    /**
     * Converts an enum to a Resource-Bundle-String.
     * @param enumObject the enum to convert
     * @return the String for the resource bundle
     */
    public String convertEnumToResourceBundleString(Object enumObject)
    {
        String resourceBundleString = null;
                        
        if (enumObject instanceof MdsPublicationVO.Genre)
        {
            resourceBundleString = this.genreToResourceBundleString.get((MdsPublicationVO.Genre)enumObject);
        }
        else if (enumObject instanceof MdsPublicationVO.ReviewMethod)
        {
            resourceBundleString = this.reviewMethodToResourceBundleString.get((MdsPublicationVO.ReviewMethod)enumObject);
        }
        else if (enumObject instanceof MdsPublicationVO.DegreeType)
        {
            resourceBundleString = this.degreeTypeToResourceBundleString.get((MdsPublicationVO.DegreeType)enumObject);
        }
        else if (enumObject instanceof EventVO.InvitationStatus)
        {
            resourceBundleString = this.invitationStatusToResourceBundleString.get((EventVO.InvitationStatus)enumObject);
        }
        else if (enumObject instanceof PubItemVO.State)
        {
            resourceBundleString = this.stateToResourceBundleString.get((PubItemVO.State)enumObject);
        }
        else if (enumObject instanceof PubItemVOComparator.Criteria)
        {
            resourceBundleString = this.itemListSortByToResourceBundleString.get((PubItemVOComparator.Criteria)enumObject);
        }        
        else if (enumObject instanceof ApplicationBean.SelectMultipleItems)
        {
            resourceBundleString = this.itemListSelectMultipleItemsToResourceBundleString.get((ApplicationBean.SelectMultipleItems)enumObject);
        }        
        else if (enumObject instanceof SourceVO.Genre)
        {
            resourceBundleString = this.genreOfSourceToResourceBundleString.get((SourceVO.Genre)enumObject);
        }
        else
        {
            logger.warn("Enum class for given object (" + enumObject + ") cannot be found.");
        }
        
        if (resourceBundleString == null)
        {
            logger.warn("ResourceBundleString for given object (" + enumObject + ") cannot be found.");
        }
        
        return resourceBundleString;
    }

    /**
     * Returns an appropriate character encoding based on the Locale defined for the current JavaServer
     * Faces view. If no more suitable encoding can be found, return "UTF-8" as a general purpose default.
     * The default implementation uses the implementation from our superclass, AbstractApplicationBean.
     * 
     * @return the local character encoding
     */
    public String getLocaleCharacterEncoding()
    {
        return super.getLocaleCharacterEncoding();
    }

    /**
     * Returns the title and version of the application, shown in the header.
     * 
     * @return applicationtitle, including version
     */
    public String getAppTitle()
    {
        // retrieve version once
        if (this.appTitle == null)
        {
            this.appTitle = this.APP_TITLE;
            
            try
            {
                this.appTitle += " " + this.getVersion();
            }
            catch (PubManVersionNotAvailableException e)
            {
                // version cannot be retrieved; just show the application title
                logger.warn("The version of the application cannot be retrieved.");                
            }
        }
        
        return appTitle;
    }

    /**
     * Provides the escidoc version string.
     * 
     * @return the escidoc version
     * @throws PubManVersionNotAvailableException if escidoc version can not be retrieved.
     */
    private String getVersion() throws PubManVersionNotAvailableException
    {
        try
        {
            Properties properties = CommonUtils.getProperties("escidoc.properties");
            return properties.getProperty("escidoc.version");
        }
        catch (IOException e)
        {
            throw new PubManVersionNotAvailableException(e);
        }
    }

    /**
     * Returns the current application context.
     * 
     * @return the application context
     */
    public String getAppContext()
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        this.appContext = fc.getExternalContext().getRequestContextPath()+"/faces/";
        
        return appContext;
    }

    /**
     * Sets the application context.
     * 
     * @param appContext the new application context
     */
    public void setAppContext(String appContext)
    {
        this.appContext = appContext;
    }
}
