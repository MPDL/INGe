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

package de.mpg.escidoc.pubman.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.component.html.HtmlSelectOneRadio;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.InternationalizedImpl;
import de.mpg.escidoc.pubman.contextList.PubContextVOWrapper;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.RelationVO;
import de.mpg.escidoc.services.common.valueobjects.ValueObject;

/**
 * Provides different utilities for all kinds of stuff.
 * 
 * @author: Thomas Diebäcker, created 25.04.2007
 * @version: $Revision: 1633 $ $LastChangedDate: 2007-11-29 15:16:57 +0100 (Do, 29 Nov 2007) $
 * Revised by DiT: 07.08.2007
 */
public class CommonUtils extends InternationalizedImpl
{
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(CommonUtils.class);
    private static final String NO_ITEM_SET = "-";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    //HTML escaped characters mapping
    private static final String[] problematicCharacters = { "&", ">", "<", "\"", "\'", "\n", "\r" };
    private static final String[] escapedCharacters = { "&amp;", "&gt;", "&lt;", "&quot;", "&apos;", "&lt;br&gt;", "&lt;br&gt;" };

    /**
     * Converts a Set to an Array of SelectItems (an empty SelectItem is included at the beginning).
     * This method is used to convert Enums into SelectItems for dropDownLists.
     *
     * @param set the Set to be converted
     * @return an Array of SelectItems
     */
    public static SelectItem[] convertToOptions(Set set)
    {
        return convertToOptions(set, true);
    }

    /**
     * Converts a Set to an Array of SelectItems. This method is used to convert Enums into SelectItems for dropDownLists.
     * @param set the Set to be converted
     * @param includeEmptyOption if TRUE an empty SelectItem is added at the beginning of the list
     * @return an Array of SelectItems
     */
    public static SelectItem[] convertToOptions(Set set, boolean includeEmptyOption)
    {
        List<SelectItem> options = new ArrayList<SelectItem>();

        if (includeEmptyOption)
        {
            options.add(new SelectItem("", NO_ITEM_SET));
        }

        Iterator iter = set.iterator();
        while (iter.hasNext())
        {
            options.add(new SelectItem(iter.next()));
        }

        return (SelectItem[]) options.toArray(new SelectItem[options.size()]);
    }

    /**
     * Converts an Array of Objects to an Array of SelectItems (an empty SelectItem is included at the beginning). This method
     * is used to convert Objects into SelectItems for dropDownLists.
     * @param objects the Array of Objects to be converted
     * @return an Array of SelectItems
     */
    public static SelectItem[] convertToOptions(Object[] objects)
    {
        return convertToOptions(objects, true);
    }

    /**
     * Converts an Array of Objects to an Array of SelectItems. This method is used to convert Objects into SelectItems for
     * dropDownLists.
     * @param objects the Array of Objects to be converted
     * @return an Array of SelectItems
     */
    public static SelectItem[] convertToOptions(Object[] objects, boolean includeEmptyOption)
    {
        List<SelectItem> options = new ArrayList<SelectItem>();

        if (includeEmptyOption)
        {
            options.add(new SelectItem("", NO_ITEM_SET));
        }

        for (int i = 0; i < objects.length; i++)
        {
            options.add(new SelectItem(objects[i]));
        }

        return (SelectItem[]) options.toArray(new SelectItem[options.size()]);
    }

    /**
     * Returns all ISOLanguages, with "de" and "en" at the first positions.
     * @return all ISOLanguages, with "de" and "en" at the first positions
     */
    public static SelectItem[] getLanguageOptions()
    {
        SelectItem[] isoLanguages = CommonUtils.convertToOptions(Locale.getISOLanguages(), false);

        SelectItem[] options = new SelectItem[isoLanguages.length + 4];
        options[0] = new SelectItem("", NO_ITEM_SET);
        options[1] = new SelectItem("en");
        options[2] = new SelectItem("de");
        options[3] = new SelectItem("", NO_ITEM_SET);

        for (int i = 0; i < isoLanguages.length; i++)
        {
            options[i + 4] = isoLanguages[i]; 
        }

        return options;
    }

    /**
     * Returns the current value of a comboBox. Used in UIs.
     * @param comboBox the comboBox for which the value should be returned
     * @return the current value of the comboBox
     */
    public static String getUIValue(HtmlSelectOneMenu comboBox)
    {
        if (comboBox.getSubmittedValue() != null
                && comboBox.getSubmittedValue() instanceof String[]
                && ((String[]) comboBox.getSubmittedValue()).length > 0)
        {
            return ((String[]) comboBox.getSubmittedValue())[0];
        }

        return (String) comboBox.getValue();
    }
    
    /**
     * Returns the current value of a comboBox. Used in UIs.
     * @param comboBox the comboBox for which the value should be returned
     * @return the current value of the comboBox
     */
    public static String getUIValue(HtmlSelectOneRadio radioButton)
    {
        if (radioButton.getSubmittedValue() != null
                && radioButton.getSubmittedValue() instanceof String[]
                && ((String[]) radioButton.getSubmittedValue()).length > 0)
        {
            return ((String[]) radioButton.getSubmittedValue())[0];
        }

        return (String) radioButton.getValue();
    }

    /**
     * Returns the current value of a textfield. Used in UIs.
     * @param textField the textField for which the value should be returned
     * @return the current value of the textfield
     */
    public static String getUIValue(HtmlInputText textField)
    {
        if (textField.getSubmittedValue() != null
                && textField.getSubmittedValue() instanceof String
                && ((String) textField.getSubmittedValue()).length() > 0)
        {
            return ((String) textField.getSubmittedValue());
        }

        return (String) textField.getValue();
    }

    /**
     * Returns the current value of a textArea. Used in UIs.
     * @param textArea the textArea for which the value should be returned
     * @return the current value of the textArea
     */
    public static String getUIValue(HtmlInputTextarea textArea)
    {
        if (textArea.getSubmittedValue() != null
                && textArea.getSubmittedValue() instanceof String
                && ((String) textArea.getSubmittedValue()).length() > 0)
        {
            return ((String) textArea.getSubmittedValue());
        }

        return (String) textArea.getValue();
    }

    /**
     * Creates a unique id for GUI components.
     * @param uiComponent the uiComponent for which an id should be created
     * @return a unique id
     */
    public static String createUniqueId(UIComponent uiComponent)
    {
        UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();

        if (viewRoot == null)
        {
            viewRoot = new UIViewRoot();
            FacesContext.getCurrentInstance().setViewRoot(viewRoot);
        }

        String id = viewRoot.createUniqueId() + "_" + uiComponent.getClass().getSimpleName() + "_" + uiComponent.hashCode() + "_" + Calendar.getInstance().getTimeInMillis();

        return id;
    }

    /**
     * Formats a date with the default format.
     * @param date the date to be formated
     * @return a formated String
     */
    public static String format(Date date)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CommonUtils.DATE_FORMAT);
        String dateString = simpleDateFormat.format(date);

        return dateString;
    }

    /**
     * Escapes problematic HTML characters ("less than", "greater than", ampersand, apostrophe and quotation mark).
     *
     * @param cdata A String that might contain problematic HTML characters.
     * @return The escaped string.
     */
    public static String htmlEscape(String cdata)
    {
        // The escaping has to start with the ampsersand (&amp;, '&') !
        for (int i = 0; i < problematicCharacters.length; i++)
        {
            cdata = change(cdata, problematicCharacters[i], escapedCharacters[i]);

        }
        return cdata;
    }

    /**
     * Changes all occurrences of oldPat to newPat.
     *
     * @param in A String that might contain problematic HTML characters.
     * @param oldPat the old pattern to be escaped.
     * @param newPat the new pattern to escape with.
     * @return The escaped string.
     */
    private static String change(String in, String oldPat, String newPat)
    {
        if (in == null)
        {
            return null;
        }
        if (oldPat.length() == 0)
        {
            return in;
        }
        if (oldPat.length() == 1 && newPat.length() == 1)
        {
            return in.replace(oldPat.charAt(0), newPat.charAt(0));
        }
        if (in.indexOf(oldPat) < 0)
        {
            return in;
        }
        int lastIndex = 0;
        int newIndex = 0;
        StringBuffer newString = new StringBuffer();
        for (;;)
        {
            newIndex = in.indexOf(oldPat, lastIndex);
            if (newIndex != -1)
            {
                newString.append(in.substring(lastIndex, newIndex) + newPat);
                lastIndex = newIndex + oldPat.length();

            }
            else
            {
                newString.append(in.substring(lastIndex));
                break;
            }
        }
        return newString.toString();
    }

    /**
     * Converts an array of SelectItems to a SelectItemUI. This is used for items for comboboxes.
     * @param selectItems the array of SelectItems that should be converted
     * @return a UISelectItems which can be added to a HtmlSelectOneMenu with HtmlSelectOneMenu.getChildren.add()
     */
    public static List<UISelectItem> convertToSelectItemsUI(final SelectItem[] selectItems)
    {
        List<UISelectItem> uiSelectItems = new ArrayList<UISelectItem>();

        for (int i = 0; i < selectItems.length; i++)
        {
            UISelectItem uiSelectItem = new UISelectItem();
            uiSelectItem.setItemValue(selectItems[i].getValue());
            uiSelectItem.setItemLabel(selectItems[i].getLabel());
            uiSelectItems.add(uiSelectItem);
        }

//        UISelectItems items = new UISelectItems();
//        items.setValue(uiSelectItems);

        return uiSelectItems;
    }

    /**
     * generates an HTML OutputText element. The method also tests if the string that
     * should be placed into the element is empty.
     * If it is, a "&nbsp;" string is placed into.
     * @author Tobias Schraut
     * @param elementText the text that should placed into the html text element
     * @return HtmlOutputText the generated and prepared html text element
     */
    public static HtmlOutputText getTextElementConsideringEmpty(String elementText)
    {
        HtmlOutputText text = new HtmlOutputText();
        text.setId(CommonUtils.createUniqueId(text));
        if (elementText != null)
        {
            if (!elementText.trim().equals(""))
            {
                text.setEscape(false);
                elementText = elementText.replace("<", "&lt;");
                elementText = elementText.replace(">", "&gt;");
                elementText = elementText.replace("\n", "<br/>");
                text.setValue(elementText);
            }
            else
            {
                text.setEscape(false);
                text.setValue("&nbsp;");
            }
        }
        else
        {
            text.setEscape(false);
            text.setValue("&nbsp;");
        }
        return text;
    }

    /**
     * Converts a list of valueObjects to a list of ValueObjectWrappers.
     * @param valueObjectList the list of valueObjects
     * @return the list of ValueObjectWrappers
     */
    public static List<PubItemVOWrapper> convertToWrapperList(final List<PubItemVO> valueObjectList)
    {
        List wrapperList = new ArrayList<ValueObjectWrapper>();

        for (int i = 0; i < valueObjectList.size(); i++)
        {
            wrapperList.add(new PubItemVOWrapper(valueObjectList.get(i)));
        }

        return wrapperList;
    }

    /**
     * Converts a list of PubItemVOWrappers to a list of PubItemVOs.
     * @param wrapperList the list of PubItemVOWrappers
     * @return the list of PubItemVOs
     */
    public static List<PubItemVO> convertToPubItemList(List<PubItemVOWrapper> wrapperList)
    {
        List pubItemList = new ArrayList<ValueObject>();

        for (int i = 0; i < wrapperList.size(); i++)
        {
            pubItemList.add(wrapperList.get(i).getValueObject());
        }

        return pubItemList;
    }

    /**
     * Converts a list of PubItemVOPresentations to a list of PubItems.
     * @param list the list of PubItemVOPresentations
     * @return the list of PubItemVOs
     */
    public static ArrayList<PubItemVO> convertToPubItemVOList(List<PubItemVOPresentation> list)
    {
        ArrayList<PubItemVO> pubItemList = new ArrayList<PubItemVO>();

        for (int i = 0; i < list.size(); i++)
        {
            pubItemList.add(new PubItemVO(list.get(i)));
        }

        return pubItemList;
    }

    /**
     * Converts a list of PubCollectionVOPresentations to a list of PubCollections.
     * @param list the list of PubCollectionVOPresentations
     * @return the list of ContextVOs
     */
    public static ArrayList<ContextVO> convertToContextVOList(List<PubContextVOPresentation> list)
    {
        ArrayList<ContextVO> contextList = new ArrayList<ContextVO>();

        for (int i = 0; i < list.size(); i++)
        {
            contextList.add(new ContextVO(list.get(i)));
        }

        return contextList;
    }

    /**
     * Converts a list of PubItems to a list of PubItemVOPresentations.
     * @param list the list of PubItemVOs
     * @return the list of PubItemVOPresentations
     */
    public static List<PubItemVOPresentation> convertToPubItemVOPresentationList(List<? extends PubItemVO> list)
    {
        List<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();

        for (int i = 0; i < list.size(); i++)
        {
            pubItemList.add(new PubItemVOPresentation(list.get(i)));
        }

        return pubItemList;
    }
    
    /**
     * Converts a list of PubItems to a list of PubItemVOPresentations.
     * @param list the list of PubItemVOs
     * @return the list of PubItemVOPresentations
     */
    public static List<PubFileVOPresentation> convertToPubFileVOPresentationList(List<? extends FileVO> list)
    {
        List<PubFileVOPresentation> pubFileList = new ArrayList<PubFileVOPresentation>();

        for (int i = 0; i < list.size(); i++)
        {
            pubFileList.add(new PubFileVOPresentation(i, list.get(i)));
        }

        return pubFileList;
    }

    /**
     * Converts a list of Relations to a list of RelationVOPresentation.
     * @param list the list of RelationVO
     * @return the list of RelationVOPresentation
     */
    public static List<RelationVOPresentation> convertToRelationVOPresentationList(List<RelationVO> list)
    {
        List<RelationVOPresentation> relationList = new ArrayList<RelationVOPresentation>();

        for (int i = 0; i < list.size(); i++)
        {
            relationList.add(new RelationVOPresentation(list.get(i)));
        }

        return relationList;
    }

    /**
     * Converts a list of RelationVOPresentation to a list of Relations.
     * @param list the list of RelationVOPresentation
     * @return the list of RelationVO
     */
    public static ArrayList<RelationVO> convertToRelationVOList(List<RelationVOPresentation> list)
    {
        ArrayList<RelationVO> pubItemList = new ArrayList<RelationVO>();

        for (int i = 0; i < list.size(); i++)
        {
            pubItemList.add(new RelationVO(list.get(i)));
        }

        return pubItemList;
    }

    /**
     * Converts a list of PubCollections to a list of PubCollectionVOPresentations.
     * @param list the list of ContextVOs
     * @return the list of PubCollectionVOPresentations
     */
    public static List<PubContextVOPresentation> convertToPubCollectionVOPresentationList(List<ContextVO> list)
    {
        List<PubContextVOPresentation> contextList = new ArrayList<PubContextVOPresentation>();

        for (int i = 0; i < list.size(); i++)
        {
            contextList.add(new PubContextVOPresentation(list.get(i)));
        }

        return contextList;
    }

    /**
     * Converts a list of AffiliationVOs to a list of AffiliationVOPresentations.
     * @param list the list of AffiliationVOs
     * @return the list of AffiliationVOPresentations
     */
    public static List<AffiliationVOPresentation> convertToAffiliationVOPresentationList(List<AffiliationVO> list)
    {
        List<AffiliationVOPresentation> affiliationList = new ArrayList<AffiliationVOPresentation>();

        for (int i = 0; i < list.size(); i++)
        {
        	affiliationList.add(new AffiliationVOPresentation(list.get(i)));
        }

        return affiliationList;
    }

    /**
     * Converts a list of valueObjects to a list of ValueObjectWrappers.
     * @param valueObjectList the list of valueObjects
     * @return the list of ValueObjectWrappers
     */
    public static List<PubContextVOWrapper> convertToPubCollectionVOWrapperList(List<ContextVO> valueObjectList)
    {
        List wrapperList = new ArrayList<PubContextVOWrapper>();

        for (int i = 0; i < valueObjectList.size(); i++)
        {
            wrapperList.add(new PubContextVOWrapper(valueObjectList.get(i)));
        }

        return wrapperList;
    }

    /**
     * Searches the given list for the item with the given ID.
     * @param itemList the list to be searched
     * @param itemID the itemID that is searched for
     * @return the pubItem with the given ID or null if the item cannot be found in the given list
     */
    public static PubItemVOPresentation getItemByID(final List<PubItemVOPresentation> itemList, final String itemID)
    {
        for (int i = 0; i < itemList.size(); i++)
        {
            if (itemList.get(i).getVersion().getObjectId().equals(itemID))
            {
                return itemList.get(i);
            }
        }

        logger.warn("Item with ID: " + itemID + " cannot be found in the list.");
        return null;
    }

    /**
     * Limits a string to the given length (on word basis).
     * @param string the string to be limited
     * @param length the maximum length of the string
     * @return the limited String
     */
    public static String limitString(final String string, final int length)
    {
        String limitedString = new String();
        String[] splittedString = string.split(" ");

        if (splittedString != null && splittedString.length > 0)
        {
            limitedString = splittedString[0];

            for (int i = 1; i < splittedString.length; i++)
            {
                String newLimitedString = limitedString + " " + splittedString[i];
                if (newLimitedString.length() <= length)
                {
                    limitedString = newLimitedString;
                }
                else
                {
                    return limitedString.concat("...");
                }
            }
        }

        return limitedString;
    }

}
