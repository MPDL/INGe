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

package de.mpg.escidoc.pubman.viewItem.ui;

import java.util.ResourceBundle;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;
import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.ui.HTMLElementUI;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.pubman.util.PubItemVOWrapper;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;

/**
 * UI for viewing items in a brief context. 
 * 
 * @author: Tobias Schraut, created 30.08.2007
 * @version: $Revision: 1646 $ $LastChangedDate: 2007-12-05 17:48:05 +0100 (Wed, 05 Dec 2007) $
 */
public class ViewItemShortUI extends HtmlPanelGroup
{
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ViewItemShortUI.class);
    
    // get the selected language...
    private InternationalizationHelper i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance()
    .getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
            InternationalizationHelper.BEAN_NAME);
    // ... and set the refering resource bundle
    private ResourceBundle bundleLabel = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
    
    private HTMLElementUI htmlElement = new HTMLElementUI();
    private COinSUI coins = new COinSUI();
    
    /**
     * Public constructor.
     */
    public ViewItemShortUI(PubItemVOWrapper pubItemVOWrapper)
    {
        // get the selected language...
        this.i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance()
        .getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                InternationalizationHelper.BEAN_NAME);
        // ... and set the refering resource bundle
        this.bundleLabel = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
        ApplicationBean applicationBean = (ApplicationBean)FacesContext.getCurrentInstance()
        .getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                ApplicationBean.BEAN_NAME);
        
        this.getChildren().clear();
        this.setId(CommonUtils.createUniqueId(this));
        
        // *** CREATORS ***
        //label
        this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
        
        this.getChildren().add(coins.getCOinSTag(pubItemVOWrapper.getValueObject()));
        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemShort_lblCreators")));
        
        this.getChildren().add(htmlElement.getEndTag("div"));
        
        // value
        this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
        
        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getCreators(pubItemVOWrapper.getValueObject())));
        
        this.getChildren().add(htmlElement.getEndTag("div"));
        
        
        // *** DATES ***
        //label
        this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
        
        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemShort_lblDates")));
        
        this.getChildren().add(htmlElement.getEndTag("div"));
        
        // value
        this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));
        
        boolean empty = true;
        empty = displayDate(pubItemVOWrapper.getValueObject().getMetadata().getDatePublishedInPrint(), "ViewItem_lblDatePublishedInPrint", empty);
        empty = displayDate(pubItemVOWrapper.getValueObject().getMetadata().getDatePublishedOnline(), "ViewItem_lblDatePublishedOnline", empty);
        empty = displayDate(pubItemVOWrapper.getValueObject().getMetadata().getDateAccepted(), "ViewItem_lblDateAccepted", empty);
        empty = displayDate(pubItemVOWrapper.getValueObject().getMetadata().getDateSubmitted(), "ViewItem_lblDateSubmitted", empty);
        empty = displayDate(pubItemVOWrapper.getValueObject().getMetadata().getDateModified(), "ViewItem_lblDateModified", empty);
        empty = displayDate(pubItemVOWrapper.getValueObject().getMetadata().getDateCreated(), "ViewItem_lblDateCreated", empty);
        
        this.getChildren().add(htmlElement.getEndTag("div"));
        
        // *** GENRE ***
        //label
        this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
        
        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemShort_lblGenre")));
        
        this.getChildren().add(htmlElement.getEndTag("div"));
        
        // value
        this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
        
        if(pubItemVOWrapper.getValueObject().getMetadata().getGenre() != null)
        {
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(applicationBean.convertEnumToString(pubItemVOWrapper.getValueObject().getMetadata().getGenre())));
        }
        else
        {
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
        }
        
        this.getChildren().add(htmlElement.getEndTag("div"));
        
        
        // *** FILES ***
        //label
        this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
        
        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemShort_lblFile")));
        
        this.getChildren().add(htmlElement.getEndTag("div"));
        
        // value
        this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));
        
        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getFiles(pubItemVOWrapper.getValueObject())));
        
        this.getChildren().add(htmlElement.getEndTag("div"));
    }
    
    /**
     * Distinguish between Persons and organization as creators and returns them formatted as string
     * @param pubitemVo the pubitem that contains the creators
     * @return String the  formatted creators
     */
    private String getCreators(PubItemVO pubitemVo)
    {
        StringBuffer creators = new StringBuffer();
        
        if(pubitemVo.getMetadata().getCreators() != null)
        {
            for(int i = 0; i < pubitemVo.getMetadata().getCreators().size(); i++)
            {
                if (pubitemVo.getMetadata().getCreators().get(i).getPerson() != null)
                {
                    if(pubitemVo.getMetadata().getCreators().get(i).getPerson().getFamilyName() != null)
                    {
                        creators.append(pubitemVo.getMetadata().getCreators().get(i).getPerson().getFamilyName());
                    }
                    if(pubitemVo.getMetadata().getCreators().get(i).getPerson().getFamilyName() != null && pubitemVo.getMetadata().getCreators().get(i).getPerson().getGivenName() != null)
                    {
                        creators.append(", ");
                    }
                    if(pubitemVo.getMetadata().getCreators().get(i).getPerson().getGivenName() != null)
                    {
                        creators.append(pubitemVo.getMetadata().getCreators().get(i).getPerson().getGivenName());
                    }
                }
                else if (pubitemVo.getMetadata().getCreators().get(i).getOrganization() != null)
                {
                    if(pubitemVo.getMetadata().getCreators().get(i).getOrganization().getName().getValue() != null)
                    {
                        creators.append(pubitemVo.getMetadata().getCreators().get(i).getOrganization().getName().getValue());
                    }
                }
                if(i < pubitemVo.getMetadata().getCreators().size() -1)
                {
                    creators.append("; ");
                }
            }
        }
        return creators.toString();
    }

    private boolean displayDate(String date, String label, boolean empty)
    {
        if(empty && date != null && !date.trim().equals(""))
        {
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString(label) + ": " + date));
            return false;
        }
        else
        {
            return empty;
        }

    }
    
    /**
     * This method examines the pubitem concerning its files and generates a display string for the page according to the number of files detected
     * @param pubitemVo the pubitem to be examined
     * @return String the formatted String to display the occurencies of files
     */
    private String getFiles(PubItemVO pubitemVo)
    {
        StringBuffer files = new StringBuffer();
        
        if (pubitemVo.getFiles() != null)
        {
            files.append(pubitemVo.getFiles().size());
            
            // if there is only 1 file, display "File attached", otherwise display "Files attached" (plural)
            if (pubitemVo.getFiles().size() == 1)
            {
                files.append(" " + this.bundleLabel.getString("ViewItemShort_lblFileAttached"));
            }
            else
            {
                files.append(" " + this.bundleLabel.getString("ViewItemShort_lblFilesAttached"));
            }
        }
        return files.toString();
    }
}
