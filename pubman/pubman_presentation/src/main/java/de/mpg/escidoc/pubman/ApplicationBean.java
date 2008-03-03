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
import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.exceptions.PubManVersionNotAvailableException;
import de.mpg.escidoc.services.common.util.CommonUtils;
import de.mpg.escidoc.services.common.valueobjects.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.PubFileVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.comparator.PubItemVOComparator;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO;

/**
 * ApplicationBean which stores all application wide values.
 *
 * @author: Thomas Diebäcker, created 09.08.2007
 * @version: $Revision: 1700 $ $LastChangedDate: 2007-12-18 16:18:16 +0100 (Di, 18 Dez 2007) $
 * Revised by DiT: 09.08.2007
 */
public class ApplicationBean extends FacesBean
{
    public static final String BEAN_NAME = "ApplicationBean";
    private static Logger logger = Logger.getLogger(ApplicationBean.class);

    private final String APP_TITLE = "Publication Manager";
    private String appTitle = null;
    private String appContext = "";

    // entry when no item in the comboBox is selected
    private SelectItem NO_ITEM_SET = new SelectItem("", getLabel("EditItem_NO_ITEM_SET"));

    /**
     * enum for select items.
     */
    public enum SelectMultipleItems
    {
        SELECT_ITEMS, SELECT_ALL, DESELECT_ALL, SELECT_VISIBLE
    }

    /**
     * Public constructor.
     */
    public ApplicationBean()
    {
        this.init();
        this.createHashMapsForEnums();
    }

    /**
     * This method is called when this bean is initially added to application scope.
     * Typically, this occurs as a result of
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
    public SelectItem[] getSelectItemsGenre(final boolean includeNoItemSelectedEntry)
    {
        MdsPublicationVO.Genre[] values = MdsPublicationVO.Genre.values();

        return getSelectItemsForEnum(includeNoItemSelectedEntry, values);
    }

    /**
     * Returns an array of SelectItems for the enum CreatorType.
     * @param includeNoItemSelectedEntry if true an entry for NoItemSelected is added
     * @return array of SelectItems for CreatorType
     */
    public SelectItem[] getSelectItemsCreatorType(final boolean includeNoItemSelectedEntry)
    {
        CreatorVO.CreatorType[] values = CreatorVO.CreatorType.values();

        return getSelectItemsForEnum(includeNoItemSelectedEntry, values);
    }

    /**
     * Returns an array of SelectItems for the enum CreatorRole.
     * @param includeNoItemSelectedEntry if true an entry for NoItemSelected is added
     * @return array of SelectItems for CreatorRole
     */
    public SelectItem[] getSelectItemsCreatorRole(final boolean includeNoItemSelectedEntry)
    {
        CreatorVO.CreatorRole[] values = CreatorVO.CreatorRole.values();

        return getSelectItemsForEnum(includeNoItemSelectedEntry, values);
    }

    /**
     * Turn the values of an enum to an array of SelectItem.
     *
     * @param includeNoItemSelectedEntry Decide if a SelectItem with null value should be inserted.
     * @param values The values of an enum.
     * @return An array of SelectItem.
     */
    public SelectItem[] getSelectItemsForEnum(final boolean includeNoItemSelectedEntry, final Object[] values)
    {
        SelectItem[] selectItems = new SelectItem[values.length];

        for (int i = 0; i < values.length; i++)
        {
            SelectItem selectItem = new SelectItem(values[i].toString(), getLabel(convertEnumToString(values[i])));
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
    public SelectItem[] getSelectItemsDegreeType(final boolean includeNoItemSelectedEntry)
    {
        MdsPublicationVO.DegreeType[] values = MdsPublicationVO.DegreeType.values();

        return getSelectItemsForEnum(includeNoItemSelectedEntry, values);
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
    public SelectItem[] getSelectItemsReviewMethod(final boolean includeNoItemSelectedEntry)
    {
        MdsPublicationVO.ReviewMethod[] values = MdsPublicationVO.ReviewMethod.values();

        return getSelectItemsForEnum(includeNoItemSelectedEntry, values);
    }
    
    /**
     * Returns an array of SelectItems for the enum visibility.
     * @return array of SelectItems for visibility
     */
    public SelectItem[] getSelectItemsVisibility()
    {
        return this.getSelectItemsVisibility(false);
    }
    
    /**
     * Returns an array of SelectItems for the enum visibility.
     * @param includeNoItemSelectedEntry if true an entry for NoItemSelected is added
     * @return array of SelectItems for visibility
     */
    public SelectItem[] getSelectItemsVisibility(final boolean includeNoItemSelectedEntry)
    {
    	PubFileVO.Visibility[] values = PubFileVO.Visibility.values();

        return getSelectItemsForEnum(includeNoItemSelectedEntry, values);
    }
    
    /**
     * Returns an array of SelectItems for the enum ContentType.
     * @return array of SelectItems for ContentType
     */
    public SelectItem[] getSelectItemsContentType()
    {
        return this.getSelectItemsContentType(false);
    }


    /**
     * Returns an array of SelectItems for the enum ContentType.
     * @param includeNoItemSelectedEntry if true an entry for NoItemSelected is added
     * @return array of SelectItems for ReviewMethod
     */
    public SelectItem[] getSelectItemsContentType(final boolean includeNoItemSelectedEntry)
    {
        PubFileVO.ContentType[] values = PubFileVO.ContentType.values();

        return getSelectItemsForEnum(includeNoItemSelectedEntry, values);
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
    public SelectItem[] getSelectItemsInvitationStatus(final boolean includeNoItemSelectedEntry)
    {
        EventVO.InvitationStatus[] values = EventVO.InvitationStatus.values();

        return getSelectItemsForEnum(includeNoItemSelectedEntry, values);
    }

    /**
     * Returns an array of SelectItems for the enum ItemState.
     * @return array of SelectItems for ItemState
     */
    public SelectItem[] getSelectItemsItemState()
    {
        PubItemVO.State[] values = PubItemVO.State.values();

        // TODO FrM: add an extra 'all', since it's not member of the enum
        // selectItems[0] = new SelectItem("all", getLabel("depositorWS_ItemState_all"));

        return getSelectItemsForEnum(false, values);
    }

    /**
     * Returns an array of SelectItems for the enum ItemListSortBy.
     * @return array of SelectItems for ItemListSortBy
     */
    public SelectItem[] getSelectItemsItemListSortBy()
    {
        PubItemVOComparator.Criteria[] values = PubItemVOComparator.Criteria.values();

        return getSelectItemsForEnum(false, values);
    }

    /**
     * Returns an array of SelectItems for the enum SelectMultipleItems.
     * @return array of SelectItems for SelectMultipleItems
     */
    public SelectItem[] getSelectItemsItemListSelectMultipleItems()
    {
        ApplicationBean.SelectMultipleItems[] values = ApplicationBean.SelectMultipleItems.values();

        return getSelectItemsForEnum(false, values);
    }

    /**
     * Adds an entry for NoItemSelected in front of the given array.
     * @param selectItems the array where the entry should be added
     * @return a new array with an entry for NoItemSelected
     */
    private SelectItem[] addNoItemSelectedEntry(final SelectItem[] selectItems)
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
     * TODO FrM: Check this
     * Converts an enum to a String for output.
     * @param enumObject the enum to convert
     * @return the converted String for output
     */
    public String convertEnumToString(final Object enumObject)
    {
        return "ENUM_" + enumObject.getClass().getSimpleName().toUpperCase() + "_" + enumObject;
    }

    /**
     * Returns an appropriate character encoding based on the Locale defined for the current JavaServer
     * Faces view. If no more suitable encoding can be found, return "UTF-8" as a general purpose default.
     * The default implementation uses the implementation from our superclass, FacesBean.
     *
     * @return the local character encoding
     */
    public String getLocaleCharacterEncoding()
    {
        return System.getProperty("file.encoding"); // super.getLocaleCharacterEncoding();
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
                logger.info("Version retrieved.");
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
            Properties properties = CommonUtils.getProperties("version.properties");
            return properties.getProperty("escidoc.pubman.version");
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
        this.appContext = fc.getExternalContext().getRequestContextPath() + "/faces/";

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

    /**
     * Generate a string for displaying file sizes.
     * Added by FrM to compute a better result for values < 1024.
     * 
     * @param size The size of an uploaded file.
     * @return A string representing the file size in a readable format.
     */
	public String computeFileSize(long size) {
		if (size < 1024)
		{
			return size + getLabel("ViewItemMedium_lblFileSizeB");
		}
		else if (size < 1024 * 1024)
		{
			return ((size - 1) / 1024 + 1) + getLabel("ViewItemMedium_lblFileSizeKB");
		}
		else
		{
			return ((size - 1) / 1024 * 1024 + 1) + getLabel("ViewItemMedium_lblFileSizeMB");
		}
	}

}
