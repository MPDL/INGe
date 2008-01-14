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

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import com.sun.rave.web.ui.component.Hyperlink;
import com.sun.rave.web.ui.component.ImageHyperlink;
import com.sun.rave.web.ui.component.StaticText;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.pubman.util.ObjectFormatter;
import de.mpg.escidoc.pubman.viewItem.ViewItemCreatorOrganization;
import de.mpg.escidoc.pubman.viewItem.ViewItemOrganization;
import de.mpg.escidoc.pubman.viewItem.ViewItemSource;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;

/**
 * Class for representing one source element within the view item page.
 * 
 * @author: Tobias Schraut, created 30.05.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Tue, 20 Nov 2007) $ Revised by ScT: 20.08.2007
 */
public class SourceUI
{
    ObjectFormatter formatter = new ObjectFormatter();
    /**
     * Texts for more and less links.
     */
    private static String elementCreator = "creator";
    private static String elementAlternativeTitle = "alternativeTitle";
    private static String elementSources = "sources";
    private HtmlPanelGrid panGrid = new HtmlPanelGrid();
    private Hyperlink lnkAlternativeTitleMore = new Hyperlink();
    private Hyperlink lnkCreatorMore = new Hyperlink();
    private Hyperlink lnkSourceOfSourceMore = new Hyperlink();
    private StaticText lblSourceTitle = new StaticText();
    private StaticText valSourceTitle = new StaticText();
    private StaticText lblSourceAlternativeTitle = new StaticText();
    private StaticText valSourceAlternativeTitle;
    private StaticText lblSourceCreator = new StaticText();
    private StaticText valSourceCreator;
    private StaticText lblSourceVolume = new StaticText();
    private StaticText valSourceVolume = new StaticText();
    private StaticText lblSourceIssue = new StaticText();
    private StaticText valSourceIssue = new StaticText();
    private StaticText lblSourceStartPage = new StaticText();
    private StaticText valSourceStartPage = new StaticText();
    private StaticText lblSourceEndPage = new StaticText();
    private StaticText valSourceEndPage = new StaticText();
    private StaticText lblSourceSequenceNo = new StaticText();
    private StaticText valSourceSequenceNo = new StaticText();
    private StaticText lblSourceIdentifier = new StaticText();
    private StaticText valSourceIdentifier;
    private StaticText lblSourceofSource = new StaticText();
    private StaticText lblSourcePublisher = new StaticText();
    private StaticText valSourcePublisher = new StaticText();
    private StaticText lblSourcePlace = new StaticText();
    private StaticText valSourcePlace = new StaticText();
    private StaticText lblSourceEdition = new StaticText();
    private StaticText valSourceEdition = new StaticText();
    private StaticText emptySpace;
    private UIParameter paramSourceID;
    private UIParameter paramElement;
    private ImageHyperlink organizationInformation = new ImageHyperlink();
    /**
     * The list of formatted organzations in an ArrayList.
     */
    private ArrayList<String> organizationArray;
    /**
     * The list of affiliated organizations as VO List.
     */
    private ArrayList<ViewItemOrganization> organizationList;
    /**
     * The list of formatted creators which are organizations in an ArrayList.
     */
    private ArrayList<ViewItemCreatorOrganization> creatorOrganizationsArray;
    /**
     * The list of formatted creators in an ArrayList.
     */
    private ArrayList<String> creatorArray;
    /**
     * The list of affiliated organizations in a list.
     */
    private List<OrganizationVO> affiliatedOrganizationsList;

    /**
     * Public constructor.
     */
    public SourceUI(SourceVO source, ViewItemSource itemViewSource)
    {
        initialize(source, itemViewSource);
    }

    /**
     * Initialization method that is called every time a source element is generated. Fills the HtmlPanelGroup with a
     * list of source elements.
     * 
     * @param source source to be generated
     * @param itemViewSource element that stores additional information (e.g. nesting position in the pubitem)
     */
    protected void initialize(SourceVO source, ViewItemSource itemViewSource)
    {
        String organizationName;
        String organizationAddress = "";
        String organizationInfoPage;
        getAffiliatedOrganizationList(source);
        getCreatorList(source);
        Application application = FacesContext.getCurrentInstance().getApplication();
        // get the selected language...
        InternationalizationHelper i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance()
                .getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                        InternationalizationHelper.BEAN_NAME);
        // ... and set the refering resource bundle
        ResourceBundle bundle = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
        this.panGrid.setId(CommonUtils.createUniqueId(this.panGrid));
        this.panGrid.setBorder(0);
        this.panGrid.setColumns(2);
        this.panGrid.setWidth("100%");
        this.panGrid.setStyle("padding-left: " + new Integer(itemViewSource.getPaddingLeft()).toString() + "px");
        this.panGrid.setColumnClasses("viewItemColumnLeft,viewItemColumnRight");
        this.panGrid.setRowClasses("viewItemRow");
        if (source != null)
        {
            this.lblSourceTitle = new StaticText();
            this.lblSourceTitle.setId(CommonUtils.createUniqueId(this.lblSourceTitle));
            this.lblSourceTitle.setText(bundle.getString("ViewItem_lblSourceTitle"));
            this.lblSourceTitle.setStyle("height: 20px; width: 360px");
            this.lblSourceTitle.setStyleClass("valueMetadata");
            this.panGrid.getChildren().add(this.lblSourceTitle);
            this.valSourceTitle = new StaticText();
            this.valSourceTitle.setId(CommonUtils.createUniqueId(this.valSourceTitle));
            this.valSourceTitle.setText(source.getTitle().getValue());
            this.valSourceTitle.setStyle("height: 20px; width: 360px");
            this.valSourceTitle.setStyleClass("valueMetadata");
            this.panGrid.getChildren().add(this.valSourceTitle);
            this.lblSourceAlternativeTitle = new StaticText();
            this.lblSourceAlternativeTitle.setId(CommonUtils.createUniqueId(this.lblSourceAlternativeTitle));
            this.lblSourceAlternativeTitle.setText(bundle.getString("ViewItem_lblSourceAlternativeTitle"));
            this.lblSourceAlternativeTitle.setStyle("height: 20px; width: 360px");
            this.lblSourceAlternativeTitle.setStyleClass("valueMetadata");
            this.panGrid.getChildren().add(this.lblSourceAlternativeTitle);
            if (source.getAlternativeTitles() != null && source.getAlternativeTitles().size() > 0)
            {
                if (itemViewSource.isLastSource() == true)
                {
                    for (int i = 0; i < source.getAlternativeTitles().size(); i++)
                    {
                        if (i > 0)
                        {
                            this.emptySpace = new StaticText();
                            this.emptySpace.setId(CommonUtils.createUniqueId(this.emptySpace));
                            this.emptySpace.setText(" ");
                            this.emptySpace.setStyle("height: 20px; width: 360px");
                            this.panGrid.getChildren().add(this.emptySpace);
                        }
                        this.valSourceAlternativeTitle = new StaticText();
                        this.valSourceAlternativeTitle
                                .setId(CommonUtils.createUniqueId(this.valSourceAlternativeTitle));
                        this.valSourceAlternativeTitle.setText(source.getAlternativeTitles().get(i).getValue());
                        this.valSourceAlternativeTitle.setStyle("height: 20px; width: 360px");
                        this.valSourceAlternativeTitle.setStyleClass("valueMetadata");
                        this.panGrid.getChildren().add(this.valSourceAlternativeTitle);
                    }
                }
                else
                {
                    if (itemViewSource.isAlternativeTitlesCollapsed() == true)
                    {
                        this.valSourceAlternativeTitle = new StaticText();
                        this.valSourceAlternativeTitle
                                .setId(CommonUtils.createUniqueId(this.valSourceAlternativeTitle));
                        this.valSourceAlternativeTitle.setText(source.getAlternativeTitles().get(0).getValue());
                        this.valSourceAlternativeTitle.setStyle("height: 20px; width: 360px");
                        this.valSourceAlternativeTitle.setStyleClass("valueMetadata");
                        this.panGrid.getChildren().add(this.valSourceAlternativeTitle);
                    }
                    else
                    {
                        for (int i = 0; i < source.getAlternativeTitles().size(); i++)
                        {
                            if (i > 0)
                            {
                                this.emptySpace = new StaticText();
                                this.emptySpace.setId(CommonUtils.createUniqueId(this.emptySpace));
                                this.emptySpace.setText(" ");
                                this.emptySpace.setStyle("height: 20px; width: 360px");
                                this.panGrid.getChildren().add(this.emptySpace);
                            }
                            this.valSourceAlternativeTitle = new StaticText();
                            this.valSourceAlternativeTitle.setId(CommonUtils
                                    .createUniqueId(this.valSourceAlternativeTitle));
                            this.valSourceAlternativeTitle.setText(source.getAlternativeTitles().get(i).getValue());
                            this.valSourceAlternativeTitle.setStyle("height: 20px; width: 360px");
                            this.valSourceAlternativeTitle.setStyleClass("valueMetadata");
                            this.panGrid.getChildren().add(this.valSourceAlternativeTitle);
                        }
                    }
                }
            }
            else
            {
                this.emptySpace = new StaticText();
                this.emptySpace.setId(CommonUtils.createUniqueId(this.emptySpace));
                this.emptySpace.setText(" ");
                this.emptySpace.setStyle("height: 20px; width: 360px");
                this.panGrid.getChildren().add(this.emptySpace);
            }
            this.emptySpace = new StaticText();
            this.emptySpace.setId(CommonUtils.createUniqueId(this.emptySpace));
            this.emptySpace.setText(" ");
            this.emptySpace.setStyle("height: 20px; width: 360px");
            this.panGrid.getChildren().add(this.emptySpace);
            this.lnkAlternativeTitleMore = new Hyperlink();
            this.lnkAlternativeTitleMore.setId(CommonUtils.createUniqueId(this.lnkAlternativeTitleMore));
            if (source.getAlternativeTitles() != null)
            {
                if (source.getAlternativeTitles().size() > 1)
                {
                    if (itemViewSource.isAlternativeTitlesCollapsed() == true)
                    {
                        this.lnkAlternativeTitleMore.setText(bundle.getString("ViewItem_lnkAlternativeTitleMore"));
                    }
                    else
                    {
                        this.lnkAlternativeTitleMore.setText(bundle.getString("ViewItem_lnkAlternativeTitleLess"));
                    }
                }
                else
                {
                    this.lnkAlternativeTitleMore.setText("");
                }
            }
            this.lnkAlternativeTitleMore.setStyle("height: 20px; width: 360px");
            this.lnkAlternativeTitleMore.setAction((MethodBinding)application.createMethodBinding(
                    "#{viewItem$ViewItem.expandCollapseSourceElements}", new Class[0]));
            this.paramSourceID = new UIParameter();
            this.paramSourceID.setId(CommonUtils.createUniqueId(this.paramSourceID));
            this.paramSourceID.setName("sourceID");
            this.paramSourceID.setValue(itemViewSource.getSourceID());
            this.lnkAlternativeTitleMore.getChildren().add(this.paramSourceID);
            this.paramElement = new UIParameter();
            this.paramElement.setId(CommonUtils.createUniqueId(this.paramElement));
            this.paramElement.setName("element");
            this.paramElement.setValue(elementAlternativeTitle);
            this.lnkAlternativeTitleMore.getChildren().add(this.paramElement);
            if (itemViewSource.isLastSource() == false)
            {
                this.panGrid.getChildren().add(this.lnkAlternativeTitleMore);
            }
            else
            {
                this.emptySpace = new StaticText();
                this.emptySpace.setId(CommonUtils.createUniqueId(this.emptySpace));
                this.emptySpace.setText(" ");
                this.emptySpace.setStyle("height: 20px; width: 360px");
                this.panGrid.getChildren().add(this.emptySpace);
            }
            this.lblSourceCreator = new StaticText();
            this.lblSourceCreator.setId(CommonUtils.createUniqueId(this.lblSourceCreator));
            this.lblSourceCreator.setText(bundle.getString("ViewItem_lblSourceCreator"));
            this.lblSourceCreator.setStyle("height: 20px; width: 360px");
            this.lblSourceCreator.setStyleClass("valueMetadata");
            this.lblSourceCreator.setStyleClass("valueMetadata");
            this.panGrid.getChildren().add(this.lblSourceCreator);
            if (source.getCreators() != null)
            {
                if (this.creatorArray.size() > 0)
                {
                    for (int i = 0; i < this.creatorArray.size(); i++)
                    {
                        if (i > 0)
                        {
                            this.emptySpace = new StaticText();
                            this.emptySpace.setId(CommonUtils.createUniqueId(this.emptySpace));
                            this.emptySpace.setText(" ");
                            this.emptySpace.setStyle("height: 20px; width: 360px");
                            this.panGrid.getChildren().add(this.emptySpace);
                        }
                        this.valSourceCreator = new StaticText();
                        this.valSourceCreator.setId(CommonUtils.createUniqueId(this.valSourceCreator));
                        this.valSourceCreator.setText(this.creatorArray.get(i));
                        this.valSourceCreator.setStyle("height: 20px; width: 360px");
                        this.lblSourceCreator.setStyleClass("valueMetadata");
                        this.panGrid.getChildren().add(this.valSourceCreator);
                    }
                }
                else if (this.creatorOrganizationsArray.size() == 0)
                {
                    // DiT added this else, so the structure won't get messed up
                    // when there is no creator for this source
                    this.emptySpace = new StaticText();
                    this.emptySpace.setId(CommonUtils.createUniqueId(this.emptySpace));
                    this.emptySpace.setText(" ");
                    this.emptySpace.setStyle("height: 20px; width: 360px");
                    this.panGrid.getChildren().add(this.emptySpace);
                }
                if (this.creatorOrganizationsArray.size() > 0)
                {
                    if (this.organizationArray.size() > 0)
                    {
                        this.emptySpace = new StaticText();
                        this.emptySpace.setId(CommonUtils.createUniqueId(this.emptySpace));
                        this.emptySpace.setText(" ");
                        this.emptySpace.setStyle("height: 20px; width: 360px");
                        this.panGrid.getChildren().add(this.emptySpace);
                    }
                    for (int i = 0; i < this.creatorOrganizationsArray.size(); i++)
                    {
                        if (i > 0)
                        {
                            this.emptySpace = new StaticText();
                            this.emptySpace.setId(CommonUtils.createUniqueId(this.emptySpace));
                            this.emptySpace.setText(" ");
                            this.emptySpace.setStyle("height: 20px; width: 360px");
                            this.panGrid.getChildren().add(this.emptySpace);
                        }
                        organizationName = this.creatorOrganizationsArray.get(i).getOrganizationName();
                        if (this.creatorOrganizationsArray.get(i).getOrganizationAddress() != null)
                        {
                            organizationAddress = this.creatorOrganizationsArray.get(i).getOrganizationAddress();
                        }
                        organizationInfoPage = "<html><head><title>Organisation Information</title></head><body scroll=no bgcolor=#FFFFFC><br/><p style=font-family:verdana,arial;font-size:12px>"
                                + CommonUtils.htmlEscape(organizationName)
                                + "</p><p style=font-family:verdana,arial;font-size:12px>"
                                + CommonUtils.htmlEscape(organizationAddress) + "</p></body></html>";
                        this.organizationInformation = new ImageHyperlink();
                        this.organizationInformation.setId(CommonUtils.createUniqueId(this.organizationInformation));
                        this.organizationInformation.setImageURL("/images/info.gif");
                        this.organizationInformation.setStyle("padding-right: 5px");
                        this.organizationInformation.setOnClick("orgInformationPopUp(400, 150, '"
                                + organizationInfoPage + "'); return false");
                        this.valSourceCreator = new StaticText();
                        this.valSourceCreator.setId(CommonUtils.createUniqueId(this.valSourceCreator));
                        this.valSourceCreator.setText(this.creatorOrganizationsArray.get(i).getOrganizationName());
                        this.valSourceCreator.setStyle("height: 20px; width: 360px");
                        this.lblSourceCreator.setStyleClass("valueMetadata");
                        this.valSourceCreator.getChildren().add(this.organizationInformation);
                        this.panGrid.getChildren().add(this.valSourceCreator);
                    }
                }
            }
            this.lnkCreatorMore = new Hyperlink();
            this.lnkCreatorMore.setId(CommonUtils.createUniqueId(this.lnkCreatorMore));
            if (source.getCreators() != null)
            {
                if (source.getCreators().size() > 1)
                {
                    if (itemViewSource.isCreatorsCollapsed() == true)
                    {
                        this.lnkCreatorMore.setText(bundle.getString("ViewItem_lnkCreatorMore"));
                    }
                    else
                    {
                        this.lnkCreatorMore.setText(bundle.getString("ViewItem_lnkCreatorLess"));
                    }
                }
                else
                {
                    this.lnkCreatorMore.setText("");
                }
            }
            this.lnkCreatorMore.setStyle("height: 20px; width: 360px");
            this.lnkCreatorMore.setAction((MethodBinding)application.createMethodBinding(
                    "#{viewItem$ViewItem.expandCollapseSourceElements}", new Class[0]));
            this.paramSourceID = new UIParameter();
            this.paramSourceID.setId(CommonUtils.createUniqueId(this.paramSourceID));
            this.paramSourceID.setName("sourceID");
            this.paramSourceID.setValue(itemViewSource.getSourceID());
            this.lnkCreatorMore.getChildren().add(this.paramSourceID);
            this.paramElement = new UIParameter();
            this.paramElement.setId(CommonUtils.createUniqueId(this.paramElement));
            this.paramElement.setName("element");
            this.paramElement.setValue(elementCreator);
            this.lnkCreatorMore.getChildren().add(this.paramElement);
            // the affiliated organizations
            if (this.organizationArray.size() > 0)
            {
                this.emptySpace = new StaticText();
                this.emptySpace.setId(CommonUtils.createUniqueId(this.emptySpace));
                this.emptySpace.setText(" ");
                this.emptySpace.setStyle("height: 20px; width: 360px");
                this.panGrid.getChildren().add(this.emptySpace);
                for (int i = 0; i < this.organizationArray.size(); i++)
                {
                    if (i > 0)
                    {
                        this.emptySpace = new StaticText();
                        this.emptySpace.setId(CommonUtils.createUniqueId(this.emptySpace));
                        this.emptySpace.setText(" ");
                        this.emptySpace.setStyle("height: 20px; width: 360px");
                        this.panGrid.getChildren().add(this.emptySpace);
                    }
                    organizationName = this.organizationList.get(i).getOrganizationName();
                    if (this.organizationList.get(i).getOrganizationAddress() != null)
                    {
                        organizationAddress = this.organizationList.get(i).getOrganizationAddress();
                    }
                    organizationInfoPage = "<html><head><title>Organisation Information</title></head><body scroll=no bgcolor=#FFFFFC><br/><p style=font-family:verdana,arial;font-size:12px>"
                            + CommonUtils.htmlEscape(organizationName)
                            + "</p><p style=font-family:verdana,arial;font-size:12px>"
                            + CommonUtils.htmlEscape(organizationAddress) + "</p></body></html>";
                    this.organizationInformation = new ImageHyperlink();
                    this.organizationInformation.setId(CommonUtils.createUniqueId(this.organizationInformation));
                    this.organizationInformation.setImageURL("/images/info.gif");
                    this.organizationInformation.setStyle("padding-right: 5px");
                    this.organizationInformation.setOnClick("orgInformationPopUp(400, 150, '" + organizationInfoPage
                            + "'); return false");
                    this.valSourceCreator = new StaticText();
                    this.valSourceCreator.setId(CommonUtils.createUniqueId(this.valSourceCreator));
                    this.valSourceCreator.setText(this.organizationArray.get(i));
                    this.valSourceCreator.setStyle("height: 20px; width: 360px");
                    this.valSourceCreator.setStyleClass("valueMetadata");
                    this.valSourceCreator.getChildren().add(this.organizationInformation);
                    this.panGrid.getChildren().add(this.valSourceCreator);
                }
            }
            // Publishing info section
            if (source.getPublishingInfo() != null)
            {
                this.lblSourcePublisher = new StaticText();
                this.lblSourcePublisher.setId(CommonUtils.createUniqueId(this.lblSourcePublisher));
                this.lblSourcePublisher.setText(bundle.getString("ViewItem_lblSourcePublisher"));
                this.lblSourcePublisher.setStyle("height: 20px; width: 360px");
                this.lblSourcePublisher.setStyleClass("valueMetadata");
                this.panGrid.getChildren().add(this.lblSourcePublisher);
                this.valSourcePublisher = new StaticText();
                this.valSourcePublisher.setId(CommonUtils.createUniqueId(this.valSourcePublisher));
                this.valSourcePublisher.setText(source.getPublishingInfo().getPublisher());
                this.valSourcePublisher.setStyle("height: 20px; width: 360px");
                this.valSourcePublisher.setStyleClass("valueMetadata");
                this.panGrid.getChildren().add(this.valSourcePublisher);
                this.lblSourcePlace = new StaticText();
                this.lblSourcePlace.setId(CommonUtils.createUniqueId(this.lblSourcePlace));
                this.lblSourcePlace.setText(bundle.getString("ViewItem_lblSourcePlace"));
                this.lblSourcePlace.setStyle("height: 20px; width: 360px");
                this.lblSourcePlace.setStyleClass("valueMetadata");
                this.panGrid.getChildren().add(this.lblSourcePlace);
                this.valSourcePlace = new StaticText();
                this.valSourcePlace.setId(CommonUtils.createUniqueId(this.valSourcePlace));
                this.valSourcePlace.setText(source.getPublishingInfo().getPlace());
                this.valSourcePlace.setStyle("height: 20px; width: 360px");
                this.valSourcePlace.setStyleClass("valueMetadata");
                this.panGrid.getChildren().add(this.valSourcePlace);
                this.lblSourceEdition = new StaticText();
                this.lblSourceEdition.setId(CommonUtils.createUniqueId(this.lblSourceEdition));
                this.lblSourceEdition.setText(bundle.getString("ViewItem_lblSourceEdition"));
                this.lblSourceEdition.setStyle("height: 20px; width: 360px");
                this.lblSourceEdition.setStyleClass("valueMetadata");
                this.panGrid.getChildren().add(this.lblSourceEdition);
                this.valSourceEdition = new StaticText();
                this.valSourceEdition.setId(CommonUtils.createUniqueId(this.valSourcePlace));
                this.valSourceEdition.setText(source.getPublishingInfo().getEdition());
                this.valSourceEdition.setStyle("height: 20px; width: 360px");
                this.valSourceEdition.setStyleClass("valueMetadata");
                this.panGrid.getChildren().add(this.valSourceEdition);
            }
            this.lblSourceVolume = new StaticText();
            this.lblSourceVolume.setId(CommonUtils.createUniqueId(this.lblSourceVolume));
            this.lblSourceVolume.setText(bundle.getString("ViewItem_lblSourceVolume"));
            this.lblSourceVolume.setStyle("height: 20px; width: 360px");
            this.lblSourceVolume.setStyleClass("valueMetadata");
            this.panGrid.getChildren().add(this.lblSourceVolume);
            this.valSourceVolume = new StaticText();
            this.valSourceVolume.setId(CommonUtils.createUniqueId(this.valSourceVolume));
            this.valSourceVolume.setText(source.getVolume());
            this.valSourceVolume.setStyle("height: 20px; width: 360px");
            this.valSourceVolume.setStyleClass("valueMetadata");
            this.panGrid.getChildren().add(this.valSourceVolume);
            this.lblSourceIssue = new StaticText();
            this.lblSourceIssue.setId(CommonUtils.createUniqueId(this.lblSourceIssue));
            this.lblSourceIssue.setText(bundle.getString("ViewItem_lblSourceIssue"));
            this.lblSourceIssue.setStyle("height: 20px; width: 360px");
            this.lblSourceIssue.setStyleClass("valueMetadata");
            this.panGrid.getChildren().add(this.lblSourceIssue);
            this.valSourceIssue = new StaticText();
            this.valSourceIssue.setId(CommonUtils.createUniqueId(this.valSourceIssue));
            this.valSourceIssue.setText(source.getIssue());
            this.valSourceIssue.setStyle("height: 20px; width: 360px");
            this.valSourceIssue.setStyleClass("valueMetadata");
            this.panGrid.getChildren().add(this.valSourceIssue);
            this.lblSourceStartPage = new StaticText();
            this.lblSourceStartPage.setId(CommonUtils.createUniqueId(this.lblSourceStartPage));
            this.lblSourceStartPage.setText(bundle.getString("ViewItem_lblSourceStartPage"));
            this.lblSourceStartPage.setStyle("height: 20px; width: 360px");
            this.lblSourceStartPage.setStyleClass("valueMetadata");
            this.panGrid.getChildren().add(this.lblSourceStartPage);
            this.valSourceStartPage = new StaticText();
            this.valSourceStartPage.setId(CommonUtils.createUniqueId(this.valSourceStartPage));
            this.valSourceStartPage.setText(source.getStartPage());
            this.valSourceStartPage.setStyle("height: 20px; width: 360px");
            this.valSourceStartPage.setStyleClass("valueMetadata");
            this.panGrid.getChildren().add(this.valSourceStartPage);
            this.lblSourceEndPage = new StaticText();
            this.lblSourceEndPage.setId(CommonUtils.createUniqueId(this.lblSourceEndPage));
            this.lblSourceEndPage.setText(bundle.getString("ViewItem_lblSourceEndPage"));
            this.lblSourceEndPage.setStyle("height: 20px; width: 360px");
            this.lblSourceEndPage.setStyleClass("valueMetadata");
            this.panGrid.getChildren().add(this.lblSourceEndPage);
            this.valSourceEndPage = new StaticText();
            this.valSourceEndPage.setId(CommonUtils.createUniqueId(this.valSourceEndPage));
            this.valSourceEndPage.setText(source.getEndPage());
            this.valSourceEndPage.setStyle("height: 20px; width: 360px");
            this.valSourceEndPage.setStyleClass("valueMetadata");
            this.panGrid.getChildren().add(this.valSourceEndPage);
            this.lblSourceSequenceNo = new StaticText();
            this.lblSourceSequenceNo.setId(CommonUtils.createUniqueId(this.lblSourceSequenceNo));
            this.lblSourceSequenceNo.setText(bundle.getString("ViewItem_lblSourceSequenceNo"));
            this.lblSourceSequenceNo.setStyle("height: 20px; width: 360px");
            this.lblSourceSequenceNo.setStyleClass("valueMetadata");
            this.panGrid.getChildren().add(this.lblSourceSequenceNo);
            this.valSourceSequenceNo = new StaticText();
            this.valSourceSequenceNo.setId(CommonUtils.createUniqueId(this.valSourceSequenceNo));
            this.valSourceSequenceNo.setText(source.getSequenceNumber());
            this.valSourceSequenceNo.setStyle("height: 20px; width: 360px");
            this.valSourceSequenceNo.setStyleClass("valueMetadata");
            this.panGrid.getChildren().add(this.valSourceSequenceNo);
            this.lblSourceIdentifier = new StaticText();
            this.lblSourceIdentifier.setId(CommonUtils.createUniqueId(this.lblSourceIdentifier));
            this.lblSourceIdentifier.setText(bundle.getString("ViewItem_lblSourceIdentifier"));
            this.lblSourceIdentifier.setStyle("height: 20px; width: 360px");
            this.lblSourceIdentifier.setStyleClass("valueMetadata");
            this.panGrid.getChildren().add(this.lblSourceIdentifier);
            if (source.getIdentifiers() != null)
            {
                for (int i = 0; i < source.getIdentifiers().size(); i++)
                {
                    if (i > 0)
                    {
                        this.emptySpace = new StaticText();
                        this.emptySpace.setId(CommonUtils.createUniqueId(this.emptySpace));
                        this.emptySpace.setText(" ");
                        this.emptySpace.setStyle("height: 20px; width: 360px");
                        this.panGrid.getChildren().add(this.emptySpace);
                    }
                    this.valSourceIdentifier = new StaticText();
                    this.valSourceIdentifier.setId(CommonUtils.createUniqueId(this.valSourceIdentifier));
                    this.valSourceIdentifier.setText(source.getIdentifiers().get(i).getType() + " "
                            + source.getIdentifiers().get(i).getId());
                    this.valSourceIdentifier.setStyle("height: 20px; width: 360px");
                    this.panGrid.getChildren().add(this.valSourceIdentifier);
                }
            }
            this.lblSourceofSource = new StaticText();
            this.lblSourceofSource.setId(CommonUtils.createUniqueId(this.lblSourceofSource));
            this.lblSourceofSource.setText(bundle.getString("ViewItem_lblSourceofSource"));
            this.lblSourceofSource.setStyle("height: 20px; width: 360px");
            this.lblSourceofSource.setStyleClass("valueMetadata");
            if (source.getSources() != null)
            {
                this.emptySpace = new StaticText();
                this.emptySpace.setId(CommonUtils.createUniqueId(this.emptySpace));
                this.emptySpace.setText(" ");
                this.emptySpace.setStyle("height: 20px; width: 360px");
                this.panGrid.getChildren().add(this.emptySpace);
                this.panGrid.getChildren().add(this.lblSourceofSource);
            }
            this.lnkSourceOfSourceMore = new Hyperlink();
            this.lnkSourceOfSourceMore.setId(CommonUtils.createUniqueId(this.lnkSourceOfSourceMore));
            if (source.getSources() != null)
            {
                if (source.getSources().size() > 1)
                {
                    if (itemViewSource.isSourcesOfSourceCollapsed() == true)
                    {
                        this.lnkSourceOfSourceMore.setText(bundle.getString("ViewItem_lnkSourceOfSourceMore"));
                    }
                    else
                    {
                        this.lnkSourceOfSourceMore.setText(bundle.getString("ViewItem_lnkSourceOfSourceLess"));
                    }
                }
                else
                {
                    this.lnkSourceOfSourceMore.setText("");
                }
            }
            this.lnkSourceOfSourceMore.setStyle("height: 20px; width: 360px");
            this.lnkSourceOfSourceMore.setAction((MethodBinding)application.createMethodBinding(
                    "#{viewItem$ViewItem.expandCollapseSourceElements}", new Class[0]));
            this.paramSourceID = new UIParameter();
            this.paramSourceID.setId(CommonUtils.createUniqueId(this.paramSourceID));
            this.paramSourceID.setName("sourceID");
            this.paramSourceID.setValue(itemViewSource.getSourceID());
            this.lnkSourceOfSourceMore.getChildren().add(this.paramSourceID);
            this.paramElement = new UIParameter();
            this.paramElement.setId(CommonUtils.createUniqueId(this.paramElement));
            this.paramElement.setName("element");
            this.paramElement.setValue(elementSources);
            this.lnkSourceOfSourceMore.getChildren().add(this.paramElement);
            if (itemViewSource.isLastSource() == false)
            {
                this.panGrid.getChildren().add(this.lnkSourceOfSourceMore);
            }
            else
            {
                this.emptySpace = new StaticText();
                this.emptySpace.setId(CommonUtils.createUniqueId(this.emptySpace));
                this.emptySpace.setText(" ");
                this.emptySpace.setStyle("height: 20px; width: 360px");
                this.panGrid.getChildren().add(this.emptySpace);
            }
            this.emptySpace = new StaticText();
            this.emptySpace.setId(CommonUtils.createUniqueId(this.emptySpace));
            this.emptySpace.setText(" ");
            this.emptySpace.setStyle("height: 20px; width: 360px");
            this.panGrid.getChildren().add(this.emptySpace);
            this.emptySpace = new StaticText();
            this.emptySpace.setId(CommonUtils.createUniqueId(this.emptySpace));
            this.emptySpace.setText(" ");
            this.emptySpace.setStyle("height: 20px; width: 360px");
            this.panGrid.getChildren().add(this.emptySpace);
            this.emptySpace = new StaticText();
            this.emptySpace.setId(CommonUtils.createUniqueId(this.emptySpace));
            this.emptySpace.setText(" ");
            this.emptySpace.setStyle("height: 20px; width: 360px");
            this.panGrid.getChildren().add(this.emptySpace);
        }
        else
        {
            this.emptySpace = new StaticText();
            this.emptySpace.setId(CommonUtils.createUniqueId(this.emptySpace));
            this.emptySpace.setText(" ");
            this.emptySpace.setStyle("height: 20px; width: 360px");
            this.panGrid.getChildren().add(this.emptySpace);
            this.emptySpace = new StaticText();
            this.emptySpace.setId(CommonUtils.createUniqueId(this.emptySpace));
            this.emptySpace.setText(" ");
            this.emptySpace.setStyle("height: 20px; width: 360px");
            this.panGrid.getChildren().add(this.emptySpace);
            this.emptySpace = new StaticText();
            this.emptySpace.setId(CommonUtils.createUniqueId(this.emptySpace));
            this.emptySpace.setText(" ");
            this.emptySpace.setStyle("height: 20px; width: 360px");
            this.panGrid.getChildren().add(this.emptySpace);
        }
    }

    /**
     * generates the creator list as one string for presenting it in the jsp.
     * 
     * @param source source in which the creators can be found
     * @return String the formatted creator list
     */
    private String getCreatorList(SourceVO source)
    {
        StringBuffer creatorList = new StringBuffer();
        String formattedCreator = "";
        this.creatorArray = new ArrayList<String>();
        this.creatorOrganizationsArray = new ArrayList<ViewItemCreatorOrganization>();
        // counter for organization array
        int counterOrganization = 0;
        StringBuffer annotation;
        ObjectFormatter formatter = new ObjectFormatter();
        for (int i = 0; i < source.getCreators().size(); i++)
        {
            CreatorVO creator = new CreatorVO();
            creator = source.getCreators().get(i);
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
                if (creator.getOrganization().getAddress() != null)
                {
                    creatorOrganization.setOrganizationAddress(creator.getOrganization().getAddress());
                }
                else
                {
                    creatorOrganization.setOrganizationAddress("");
                }
                this.creatorOrganizationsArray.add(creatorOrganization);
                counterOrganization++;
            }
            creatorList.append(formattedCreator);
        }
        return creatorList.toString();
    }

    /**
     * generates the affiliated organization list as one string for presenting it in the jsp.
     * 
     * @param source source in which the affiliated organizations can be found
     */
    private void getAffiliatedOrganizationList(SourceVO source)
    {
        String formattedOrganization = "";
        List<CreatorVO> tempCreatorList;
        List<OrganizationVO> tempOrganizationList = null;
        List<OrganizationVO> sortOrganizationList = null;
        this.organizationArray = new ArrayList<String>();
        this.organizationList = new ArrayList<ViewItemOrganization>();
        tempOrganizationList = new ArrayList<OrganizationVO>();
        sortOrganizationList = new ArrayList<OrganizationVO>();
        // tempOrganizationList = new ArrayList<OrganizationVO>();
        tempCreatorList = source.getCreators();
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
                            if(tempOrganizationList.get(j).getName() != null)
                            {
                                viewOrganization.setOrganizationName(tempOrganizationList.get(j).getName().getValue());
                            }
                            if (tempOrganizationList.get(j).getAddress() != null)
                            {
                                viewOrganization.setOrganizationAddress(tempOrganizationList.get(j).getAddress());
                            }
                            else
                            {
                                viewOrganization.setOrganizationAddress("");
                            }
                            viewOrganization.setPosition(new Integer(j + 1).toString());
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
            formattedOrganization = (k + 1) + ": " + sortOrganizationList.get(k).getName();
            this.organizationArray.add(formattedOrganization);
        }
    }

    // Getters and Setters
    public UIComponent getUIComponent()
    {
        return this.panGrid;
    }

    public static String getElementAlternativeTitle()
    {
        return elementAlternativeTitle;
    }

    public static void setElementAlternativeTitle(String elementAlternativeTitle)
    {
        SourceUI.elementAlternativeTitle = elementAlternativeTitle;
    }

    public static String getElementCreator()
    {
        return elementCreator;
    }

    public static void setElementCreator(String elementCreator)
    {
        SourceUI.elementCreator = elementCreator;
    }

    public static String getElementSources()
    {
        return elementSources;
    }

    public static void setElementSources(String elementSources)
    {
        SourceUI.elementSources = elementSources;
    }
}
