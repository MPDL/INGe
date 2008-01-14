/*
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

package de.mpg.escidoc.pubman.search.ui;

import java.util.Calendar;
import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.component.UISelectBoolean;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;

import com.sun.rave.web.ui.component.Hyperlink;
import com.sun.rave.web.ui.component.StaticText;

import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.PubItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.SearchHitVO.SearchHitType;

/**
 * LoginErrorPage.java Backing bean for the LoginErrorPage.jsp
 * 
 * @author: Tobias Schraut, created 24.01.2007
 * @version: $Revision: 1536 $ $LastChangedDate: 2007-11-13 10:54:07 +0100 (Tue, 13 Nov 2007) $ Revised by ScT: 22.08.2007
 */
public class SearchResultItemUI
{
    
    private static Logger logger = Logger.getLogger(SearchResultItemUI.class);
    
    // The dynamic HTML Components
    HtmlPanelGrid panel = new HtmlPanelGrid();
    UISelectBoolean chkSelect = new UISelectBoolean();
    UIParameter parameter = new UIParameter();
    Hyperlink lnkViewItem = new Hyperlink();
    private StaticText valItemID = new StaticText();
    private StaticText emptySpace = new StaticText();
    private StaticText valAuthors = new StaticText();
    private StaticText valHitList = new StaticText();
    private StaticText valHighlightedBefore = new StaticText();
    private StaticText valHighlighted = new StaticText();
    private StaticText valHighlightedAfter = new StaticText();
    private StaticText lblFileName = new StaticText();
    private StaticText valItemIndex = new StaticText();
    private StaticText valFileIndex = new StaticText();
    private Hyperlink lnkFileName = new Hyperlink();
    
    private HtmlCommandButton btnDownload = new HtmlCommandButton();

    /**
     * Public constructor with two parameters
     * 
     * @param resultItem the search result item to be transformed to dynamic HTML elements
     * @param position the position of the single search result item within the complete serach result list
     */
    public SearchResultItemUI(PubItemResultVO resultItem, int position)
    {
        initialize(resultItem, position);
    }

    /**
     * Initializes the UI and sets all attributes of the GUI components.
     * 
     * @param item the search result item to be transformed to dynamic HTML elements
     * @param position the position of the single search result item within the complete serach result list
     */
    protected void initialize(PubItemResultVO item, int position)
    {
        UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
        if (viewRoot == null)
        {
            viewRoot = new UIViewRoot();
            FacesContext.getCurrentInstance().setViewRoot(viewRoot);
        }
        Application application = FacesContext.getCurrentInstance().getApplication();
        // get the selected language...
        InternationalizationHelper i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance()
                .getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                        InternationalizationHelper.BEAN_NAME);
        // ... and set the refering resource bundle
        ResourceBundle bundle = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
        // get all the authors of the item
        StringBuffer authorList = new StringBuffer();
        for (int i = 0; i < item.getMetadata().getCreators().size(); i++)
        {
            if (item.getMetadata().getCreators().get(i).getPerson() != null)
            {
                if (item.getMetadata().getCreators().get(i).getPerson().getFamilyName() != null)
                {
                    authorList.append(item.getMetadata().getCreators().get(i).getPerson().getFamilyName());
                }
                if(item.getMetadata().getCreators().get(i).getPerson().getGivenName() != null && item.getMetadata().getCreators().get(i).getPerson().getFamilyName() != null)
                {
                    authorList.append(", ");
                }
                if (item.getMetadata().getCreators().get(i).getPerson().getGivenName() != null)
                {
                    authorList.append(item.getMetadata().getCreators().get(i).getPerson().getGivenName());
                }
            }
            else if (item.getMetadata().getCreators().get(i).getOrganization() != null && item.getMetadata().getCreators().get(i).getOrganization().getName() != null)
            {
                authorList.append(item.getMetadata().getCreators().get(i).getOrganization().getName().getValue());
            }
            if (i < item.getMetadata().getCreators().size() - 1)
            {
                authorList.append("; ");
            }
        }
        // The search 'hit string' (has to be formatted in different ways)
        String beforeHighlight = "";
        String highlighted = "";
        String afterHighlight = "";
        int startPosition = 0;
        int endPosition = 0;
        
        logger.debug("viewRoot: " + viewRoot);
        
        panel.setId(viewRoot.createUniqueId() + "_panel" + Calendar.getInstance().getTimeInMillis());
        panel.setColumns(3);
        panel.setBorder(0);
        panel.setCellspacing("5");
        panel.setCellpadding("0");
        chkSelect.setId(viewRoot.createUniqueId() + "_chkSelect" + Calendar.getInstance().getTimeInMillis());
        panel.getChildren().add(chkSelect);
        valItemID.setId(viewRoot.createUniqueId() + "_valItemID" + Calendar.getInstance().getTimeInMillis());
        valItemID.setText(item.getReference().getObjectId());
        panel.getChildren().add(valItemID);
        parameter.setId(viewRoot.createUniqueId() + "_parameter" + Calendar.getInstance().getTimeInMillis());
        parameter.setName("itemID");
        parameter.setValue(new Integer(position).toString());
        lnkViewItem.getChildren().add(parameter);
        lnkViewItem.setId("item_" + new Integer(position).toString());
        lnkViewItem.setText(item.getMetadata().getTitle().getValue());
        lnkViewItem.setImmediate(true);
        lnkViewItem.setAction(application.createMethodBinding("#{search$SearchResultList.showItem}", null));
        panel.getChildren().add(lnkViewItem);
        // The authors
        this.emptySpace = new StaticText();
        this.emptySpace.setId(viewRoot.createUniqueId());
        this.emptySpace.setText(" ");
        this.emptySpace.setStyle("height: 20px; width: 360px");
        this.panel.getChildren().add(this.emptySpace);
        this.emptySpace = new StaticText();
        this.emptySpace.setId(viewRoot.createUniqueId());
        this.emptySpace.setText(" ");
        this.emptySpace.setStyle("height: 20px; width: 360px");
        this.panel.getChildren().add(this.emptySpace);
        this.valAuthors.setId(viewRoot.createUniqueId());
        this.valAuthors.setText(authorList.toString());
        this.panel.getChildren().add(this.valAuthors);
        // the hitlist
        for (int i = 0; i < item.getSearchHitList().size(); i++)
        {
            for (int j = 0; j < item.getSearchHitList().get(i).getTextFragmentList().size(); j++)
            {
                // only show search hits in the fulltext
                if (item.getSearchHitList().get(i).getType() == SearchHitType.FULLTEXT)
                {
                    startPosition = item.getSearchHitList().get(i).getTextFragmentList().get(j).getHitwordList().get(0)
                            .getStartIndex();
                    if (startPosition > 1)
                    {
                        startPosition--;
                    }
                    endPosition = item.getSearchHitList().get(i).getTextFragmentList().get(j).getHitwordList().get(0)
                            .getEndIndex() + 1;
                    // if psoitions are not out of range, highlight the hit
                    // words#
                    if (startPosition > -1
                            && endPosition <= item.getSearchHitList().get(i).getTextFragmentList().get(j).getData()
                                    .length())
                    {
                        beforeHighlight = "..."
                                + item.getSearchHitList().get(i).getTextFragmentList().get(j).getData().substring(0,
                                        startPosition) + " ";
                        highlighted = item.getSearchHitList().get(i).getTextFragmentList().get(j).getData().substring(
                                startPosition, endPosition);
                        afterHighlight = item.getSearchHitList().get(i).getTextFragmentList().get(j).getData()
                                .substring(endPosition)
                                + "...  ";
                    }
                    // otherwise do not highlight anything
                    else
                    {
                        beforeHighlight = "..." + item.getSearchHitList().get(i).getTextFragmentList().get(j).getData()
                                + "...";
                    }
                    this.emptySpace = new StaticText();
                    this.emptySpace.setId(viewRoot.createUniqueId());
                    this.emptySpace.setText(" ");
                    this.emptySpace.setStyle("height: 20px; width: 360px");
                    this.panel.getChildren().add(this.emptySpace);
                    this.emptySpace = new StaticText();
                    this.emptySpace.setId(viewRoot.createUniqueId());
                    this.emptySpace.setText(" ");
                    this.emptySpace.setStyle("height: 20px; width: 360px");
                    this.panel.getChildren().add(this.emptySpace);
                    this.valHighlightedBefore = new StaticText();
                    this.valHighlightedBefore.setId(viewRoot.createUniqueId());
                    this.valHighlightedBefore.setText(beforeHighlight);
                    this.valHighlightedBefore.setStyleClass("searchHitResult");
                    this.valHighlighted = new StaticText();
                    this.valHighlighted.setId(viewRoot.createUniqueId());
                    this.valHighlighted.setText(highlighted);
                    this.valHighlighted.setStyleClass("searchHitResultHighlighted");
                    this.valHighlightedAfter = new StaticText();
                    this.valHighlightedAfter.setId(viewRoot.createUniqueId());
                    this.valHighlightedAfter.setText(afterHighlight);
                    this.valHighlightedAfter.setStyleClass("searchHitResult");
                    for (int k = 0; k < item.getFiles().size(); k++)
                    {
                        if (item.getSearchHitList().get(i).getHitReference().equals(
                                item.getFiles().get(k).getReference()))
                        {
                            this.emptySpace = new StaticText();
                            this.emptySpace.setId(viewRoot.createUniqueId());
                            this.emptySpace.setText(" ");
                            this.emptySpace.setStyle("height: 20px; width: 360px");
                            this.panel.getChildren().add(this.emptySpace);
                            this.lblFileName = new StaticText();
                            this.lblFileName.setId(viewRoot.createUniqueId());
                            this.lblFileName.setText(bundle.getString("SearchResultList_lblFileName") + " ");
                            this.lblFileName.setStyleClass("searchHitResultFileName");
                            // Hidden download button and other fields due to
                            // jsf bug (download action cannot be called by
                            // hyperlink)
                            this.parameter = new UIParameter();
                            this.parameter.setId(viewRoot.createUniqueId());
                            this.parameter.setName("file");
                            this.parameter.setValue(item.getFiles().get(k).getReference().getObjectId());
                            this.valItemIndex = new StaticText();
                            this.valItemIndex.setId(viewRoot.createUniqueId());
                            this.valItemIndex.setText(position);
                            this.valItemIndex.setVisible(false);
                            this.valFileIndex = new StaticText();
                            this.valFileIndex.setId(viewRoot.createUniqueId());
                            this.valFileIndex.setText(k);
                            this.valFileIndex.setVisible(false);
                            this.btnDownload = new HtmlCommandButton();
                            this.btnDownload.getChildren().add(parameter);
                            this.btnDownload.setId(viewRoot.createUniqueId() + "item" + position + "_file" + k + "_hit"
                                    + i + "_" + Calendar.getInstance().getTimeInMillis());
                            this.btnDownload.setValue("Download...");
                            this.btnDownload.setActionListener(application.createMethodBinding(
                                    "#{search$SearchResultList.handleDownloadAction}",
                                    new Class[] { ActionEvent.class }));
                            this.btnDownload.setRendered(false);
                            this.lnkFileName = new Hyperlink();
                            this.lnkFileName.setId(viewRoot.createUniqueId());
                            this.lnkFileName.setValue(item.getFiles().get(k).getName());
                            // this.lnkFileName.setOnClick("downloadFile('item"
                            // + position + "_file" + k + "_hit" +i
                            // +"_"+Calendar.getInstance().getTimeInMillis()+"');
                            // return false");
                            this.lnkFileName.setOnClick("downloadFile('" + this.btnDownload.getId()
                                    + "'); return false");
                            this.lnkFileName.setImmediate(true);
                        }
                    }
                    this.valHitList = new StaticText();
                    this.valHitList.setId(viewRoot.createUniqueId());
                    this.valHitList.getChildren().add(valHighlightedBefore);
                    this.valHitList.getChildren().add(valHighlighted);
                    this.valHitList.getChildren().add(valHighlightedAfter);
                    this.valHitList.getChildren().add(emptySpace);
                    this.valHitList.getChildren().add(lblFileName);
                    this.valHitList.getChildren().add(lnkFileName);
                    this.valHitList.getChildren().add(valItemIndex);
                    this.valHitList.getChildren().add(valFileIndex);
                    this.valHitList.getChildren().add(btnDownload);
                    this.panel.getChildren().add(this.valHitList);
                }
            }
        }
    }

    public UIComponent getUIComponent()
    {
        return this.panel;
    }
}
