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

package de.mpg.escidoc.pubman.editItem.ui;

import java.util.Calendar;
import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import org.apache.log4j.Logger;
import com.sun.rave.web.ui.component.Button;
import com.sun.rave.web.ui.component.DropDown;
import com.sun.rave.web.ui.component.Label;
import com.sun.rave.web.ui.component.TextArea;
import com.sun.rave.web.ui.component.TextField;
import com.sun.rave.web.ui.model.Option;
import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.editItem.EnumConverter;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PersonVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PublishingInfoVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;

/**
 * UI component for editing content languages. 
 * 
 * @author: Thomas Diebäcker, created 14.06.2007
 * @version: $Revision: 1624 $ $LastChangedDate: 2007-11-27 20:55:04 +0100 (Tue, 27 Nov 2007) $
 * Revised by DiT: 14.08.2007
 */
public class SourceUI extends HtmlPanelGrid implements ActionListener
{
    private static Logger logger = Logger.getLogger(SourceUI.class);
    private Application application = FacesContext.getCurrentInstance().getApplication();
    
    protected PubItemVO pubItem = null;
    protected HtmlPanelGrid panDynamicParentPanel = null;
    protected HtmlPanelGrid panAttributes = new HtmlPanelGrid();
    protected HtmlPanelGrid panDynamicTitle = new HtmlPanelGrid();
    protected HtmlPanelGrid panPublishingInfo = new HtmlPanelGrid();
    protected HtmlPanelGrid panFurtherAttributes = new HtmlPanelGrid();
    protected HtmlPanelGrid panDynamicCreator = new HtmlPanelGrid();
    protected HtmlPanelGrid panDynamicIdentifier = new HtmlPanelGrid();
    protected int indexComponent = 0;        
    
    // for handling the resource bundles (i18n)    
    private InternationalizationHelper i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
    private ResourceBundle labelBundle = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
    
    // GUI components
    protected Label lblSource = new Label();
    protected Label lblGenre = new Label();
    protected Button btAdd = new Button();
    protected Button btRemove = new Button();
    
    protected Label lblPublishingInfo = new Label();
    protected Label lblPublisher = new Label();
    protected Label lblPlace = new Label();
    protected Label lblEdition = new Label();
    protected Label lblVolume = new Label();
    protected Label lblIssue = new Label();
    protected Label lblStartpage = new Label();
    protected Label lblEndpage = new Label();
    protected Label lblSequenceNumber = new Label();
    protected Label lblSpace = new Label();
    protected Label lblSpace2 = new Label();
    
    protected DropDown cboGenre = new DropDown();
    protected TextArea txtaPublisher = new TextArea();
    protected TextField txtPlace = new TextField();
    protected TextField txtEdition = new TextField();        
    protected TextField txtVolume = new TextField();
    protected TextField txtIssue = new TextField();
    protected TextField txtStartpage = new TextField();
    protected TextField txtEndpage = new TextField();
    protected TextField txtSequenceNumber = new TextField();
    
    // constants for comboBoxes
    private Option NO_ITEM_SET = new Option("", labelBundle.getString("EditItem_NO_ITEM_SET"));
    private Option GENRE_BOOK = new Option(SourceVO.Genre.BOOK, labelBundle.getString("EditItem_GENRE_BOOK"));
    private Option GENRE_PROCEEDINGS = new Option(SourceVO.Genre.PROCEEDINGS, labelBundle.getString("EditItem_GENRE_PROCEEDINGS"));
    private Option GENRE_ISSUE = new Option(SourceVO.Genre.ISSUE, labelBundle.getString("EditItem_GENRE_ISSUE"));
    private Option GENRE_JOURNAL = new Option(SourceVO.Genre.JOURNAL, labelBundle.getString("EditItem_GENRE_JOURNAL"));
    private Option GENRE_SERIES = new Option(SourceVO.Genre.SERIES, labelBundle.getString("EditItem_GENRE_SERIES"));
    private Option[] GENRE_OPTIONS = new Option[] { NO_ITEM_SET, GENRE_BOOK, GENRE_PROCEEDINGS, GENRE_ISSUE, GENRE_JOURNAL, GENRE_SERIES };

    /**
     * Public constructor.
     * Initializes the UI.
     * @param pubItem the pubItem that is being edited
     * @param indexComponent index of the source in PubItemVO
     * @param panDynamicParentPanel the parent panel to which this UI should be be added
     */
    public SourceUI(PubItemVO pubItem, int indexComponent, HtmlPanelGrid panDynamicParentPanel)
    {
        this.pubItem = pubItem;
        this.indexComponent = indexComponent;
        this.panDynamicParentPanel = panDynamicParentPanel;
        UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();   
        
        i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
        labelBundle = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
        
        // set attributes for all GUI components
        this.setId(viewRoot.createUniqueId() + "_SourceUI" + Calendar.getInstance().getTimeInMillis());
        this.setColumns(1);
        this.setCellspacing("0");
        this.setCellpadding("0");
        
        this.panAttributes.setId(viewRoot.createUniqueId() + "_panAttributes" + Calendar.getInstance().getTimeInMillis());
        this.panAttributes.setColumns(4);
        this.panAttributes.setStyle("margin-left: 0px; margin-top: 0px");
        this.panAttributes.setBorder(0);
        this.panAttributes.setCellspacing("0");
        this.panAttributes.setCellpadding("0");
        this.panAttributes.setColumnClasses("editItemLabelColumn, editItemFieldColumn, editItemButtonColumn, editItemButtonColumn");

        this.lblSource.setId(viewRoot.createUniqueId() + "_lblSource" + Calendar.getInstance().getTimeInMillis());
        this.lblSource.setValue(labelBundle.getString("EditItem_lblSource"));
        this.lblSource.setLabelLevel(2);
        this.panAttributes.getChildren().add(this.lblSource);

        this.lblSpace2.setId(viewRoot.createUniqueId() + "_lblSpace2" + Calendar.getInstance().getTimeInMillis());
        this.panAttributes.getChildren().add(this.lblSpace2);

        this.btAdd.setId(viewRoot.createUniqueId() + "_btAdd" + Calendar.getInstance().getTimeInMillis());
        this.btAdd.setValue(labelBundle.getString("EditItem_btAdd"));
        this.btAdd.setStyle("margin-left: 240px");
        this.btAdd.setImmediate(true);
        this.btAdd.addActionListener(this);
        this.panAttributes.getChildren().add(this.btAdd);
        
        this.btRemove.setId(viewRoot.createUniqueId() + "_btRemove" + Calendar.getInstance().getTimeInMillis());
        this.btRemove.setValue(labelBundle.getString("EditItem_btRemove"));
        this.btRemove.setStyleClass("editDynamicButton");
        this.btRemove.setImmediate(true);
        this.btRemove.addActionListener(this);
        this.btRemove.setVisible(this.isRemoveButtonVisible());
        this.panAttributes.getChildren().add(this.btRemove);     
        
        this.lblGenre.setId(CommonUtils.createUniqueId(lblGenre));
        this.lblGenre.setValue(labelBundle.getString("EditItem_lblGenre"));
        this.lblGenre.setLabelLevel(3);
        this.panAttributes.getChildren().add(this.lblGenre);
        
        this.cboGenre.setId(CommonUtils.createUniqueId(cboGenre));
        this.cboGenre.setItems(this.GENRE_OPTIONS);
        this.cboGenre.setConverter(new EnumConverter(SourceVO.Genre.values())); // ValueList for converter has to be set explicitly 
                                                                                // because the guessValueList()-method would find the 
                                                                                // string in MdsPublicationVO.Genre not in 
                                                                                // SourceVO.Genre as both strings are the same!
        
        this.panAttributes.getChildren().add(this.cboGenre);

        this.getChildren().add(this.panAttributes);
        
        this.panDynamicTitle.setId(viewRoot.createUniqueId() + "_panDynamicTitle" + Calendar.getInstance().getTimeInMillis());        
        this.panDynamicTitle.setCellspacing("0");
        this.panDynamicTitle.setCellpadding("0");
        this.getChildren().add(this.getPanDynamicTitle());
        
        this.panPublishingInfo.setId(viewRoot.createUniqueId() + "_panPublishingInfo" + Calendar.getInstance().getTimeInMillis());
        this.panPublishingInfo.setStyleClass("editItemInnerPanel");
        this.panPublishingInfo.setColumns(2);
        this.panPublishingInfo.setColumnClasses("editItemLabelColumn, editItemFieldColumn");
        this.panPublishingInfo.setCellspacing("0");
                
        this.lblPublishingInfo.setId(viewRoot.createUniqueId() + "_lblPublishingInfo" + Calendar.getInstance().getTimeInMillis());
        this.lblPublishingInfo.setValue(labelBundle.getString("EditItem_lblPublishingInfo"));
        this.lblPublishingInfo.setLabelLevel(2);
        this.panPublishingInfo.getChildren().add(this.lblPublishingInfo);

        this.lblSpace.setId(viewRoot.createUniqueId() + "_lblSpace" + Calendar.getInstance().getTimeInMillis());
        this.panPublishingInfo.getChildren().add(this.lblSpace);
        
        this.lblPublisher.setId(viewRoot.createUniqueId() + "_lblPublisher" + Calendar.getInstance().getTimeInMillis());
        this.lblPublisher.setValue(labelBundle.getString("EditItem_lblPublisher"));
        this.lblPublisher.setFor(this.txtaPublisher.getId());
        this.lblPublisher.setLabelLevel(3);
        this.panPublishingInfo.getChildren().add(this.lblPublisher);

        this.txtaPublisher.setId(viewRoot.createUniqueId() + "_txtaPublisher" + Calendar.getInstance().getTimeInMillis());
        this.txtaPublisher.setStyleClass("editItemTextArea");
        this.panPublishingInfo.getChildren().add(this.txtaPublisher);        
        
        this.lblPlace.setId(viewRoot.createUniqueId() + "_lblPlace" + Calendar.getInstance().getTimeInMillis());
        this.lblPlace.setValue(labelBundle.getString("EditItem_lblPlace"));
        this.lblPlace.setFor(this.txtPlace.getId());
        this.lblPlace.setLabelLevel(3);
        this.panPublishingInfo.getChildren().add(this.lblPlace);

        this.txtPlace.setId(viewRoot.createUniqueId() + "_txtPlace" + Calendar.getInstance().getTimeInMillis());
        this.txtPlace.setStyleClass("editItemTextFieldMedium");
        this.panPublishingInfo.getChildren().add(this.txtPlace);        

        this.lblEdition.setId(viewRoot.createUniqueId() + "_lblEdition" + Calendar.getInstance().getTimeInMillis());
        this.lblEdition.setValue(labelBundle.getString("EditItem_lblEdition"));
        this.lblEdition.setFor(this.txtEdition.getId());
        this.lblEdition.setLabelLevel(3);
        this.panPublishingInfo.getChildren().add(this.lblEdition);

        this.txtEdition.setId(viewRoot.createUniqueId() + "_txtEdition" + Calendar.getInstance().getTimeInMillis());
        this.txtEdition.setStyleClass("editItemTextFieldMedium");
        this.panPublishingInfo.getChildren().add(this.txtEdition);        

        this.getChildren().add(this.panPublishingInfo);
        
        this.panFurtherAttributes.setId(viewRoot.createUniqueId() + "_panFurtherAttributes" + Calendar.getInstance().getTimeInMillis());
        this.panFurtherAttributes.setStyleClass("editItemInnerPanel");
        this.panFurtherAttributes.setColumns(2);
        this.panFurtherAttributes.setColumnClasses("editItemLabelColumn, editItemFieldColumn");
        this.panFurtherAttributes.setCellspacing("0");

        this.lblVolume.setId(viewRoot.createUniqueId() + "_lblVolume" + Calendar.getInstance().getTimeInMillis());
        this.lblVolume.setValue(labelBundle.getString("EditItem_lblVolume"));
        this.lblVolume.setFor(this.txtVolume.getId());
        this.lblVolume.setLabelLevel(3);
        this.panFurtherAttributes.getChildren().add(this.lblVolume);

        this.txtVolume.setId(viewRoot.createUniqueId() + "_txtVolume" + Calendar.getInstance().getTimeInMillis());
        this.txtVolume.setStyleClass("editItemTextFieldShort");
        this.panFurtherAttributes.getChildren().add(this.txtVolume);       
        
        this.lblIssue.setId(viewRoot.createUniqueId() + "_lblIssue" + Calendar.getInstance().getTimeInMillis());
        this.lblIssue.setValue(labelBundle.getString("EditItem_lblIssue"));
        this.lblIssue.setFor(this.txtIssue.getId());
        this.lblIssue.setLabelLevel(3);
        this.panFurtherAttributes.getChildren().add(this.lblIssue);

        this.txtIssue.setId(viewRoot.createUniqueId() + "_txtIssue" + Calendar.getInstance().getTimeInMillis());
        this.txtIssue.setStyleClass("editItemTextFieldShort");
        this.panFurtherAttributes.getChildren().add(this.txtIssue);       
        
        this.lblStartpage.setId(viewRoot.createUniqueId() + "_lblStartpage" + Calendar.getInstance().getTimeInMillis());
        this.lblStartpage.setValue(labelBundle.getString("EditItem_lblStartpage"));
        this.lblStartpage.setFor(this.txtStartpage.getId());
        this.lblStartpage.setLabelLevel(3);
        this.panFurtherAttributes.getChildren().add(this.lblStartpage);

        this.txtStartpage.setId(viewRoot.createUniqueId() + "_txtStartpage" + Calendar.getInstance().getTimeInMillis());
        this.txtStartpage.setStyleClass("editItemTextFieldShort");
        this.panFurtherAttributes.getChildren().add(this.txtStartpage);       
        
        this.lblEndpage.setId(viewRoot.createUniqueId() + "_lblEndpage" + Calendar.getInstance().getTimeInMillis());
        this.lblEndpage.setValue(labelBundle.getString("EditItem_lblEndpage"));
        this.lblEndpage.setFor(this.txtEndpage.getId());
        this.lblEndpage.setLabelLevel(3);
        this.panFurtherAttributes.getChildren().add(this.lblEndpage);

        this.txtEndpage.setId(viewRoot.createUniqueId() + "_txtEndpage" + Calendar.getInstance().getTimeInMillis());
        this.txtEndpage.setStyleClass("editItemTextFieldShort");
        this.panFurtherAttributes.getChildren().add(this.txtEndpage);       

        this.lblSequenceNumber.setId(viewRoot.createUniqueId() + "_lblSequenceNumber" + Calendar.getInstance().getTimeInMillis());
        this.lblSequenceNumber.setValue(labelBundle.getString("EditItem_lblSequenceNumber"));
        this.lblSequenceNumber.setFor(this.txtSequenceNumber.getId());
        this.lblSequenceNumber.setLabelLevel(3);
        this.panFurtherAttributes.getChildren().add(this.lblSequenceNumber);

        this.txtSequenceNumber.setId(viewRoot.createUniqueId() + "_txtSequenceNumber" + Calendar.getInstance().getTimeInMillis());
        this.txtSequenceNumber.setStyleClass("editItemTextFieldShort");
        this.panFurtherAttributes.getChildren().add(this.txtSequenceNumber);       

        this.getChildren().add(this.panFurtherAttributes);
        
        this.panDynamicIdentifier.setId(viewRoot.createUniqueId() + "_panDynamicIdentifier" + Calendar.getInstance().getTimeInMillis());        
        this.panDynamicIdentifier.setCellspacing("0");
        this.panDynamicIdentifier.setCellpadding("0");
        this.getChildren().add(this.getPanDynamicIdentifier());

        this.panDynamicCreator.setId(viewRoot.createUniqueId() + "_panDynamicCreator" + Calendar.getInstance().getTimeInMillis());
        this.panDynamicCreator.setStyleClass("editItemInnerPanel");
        this.panDynamicCreator.setCellspacing("0");
        this.panDynamicCreator.setCellpadding("0");
        this.getChildren().add(this.getPanDynamicCreator());

        // set the valuebinding for all fields
        this.createValueBinding();
    }
    
    /**
     * Stores all values of the UI in the corresponding VO. 
     */
    public void storeValues()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Storing values of source: " + this.indexComponent);
        }
        
        // store genre
        this.pubItem.getMetadata().getSources().get(this.indexComponent).setGenre(SourceVO.Genre.valueOf(CommonUtils.getUIValue(this.cboGenre)));
        
        // storing all titles
        for (int i=0; i<this.panDynamicTitle.getChildCount(); i++)
        {
            ((TitleUI)this.panDynamicTitle.getChildren().get(i)).storeValues();
        }
        
        // store PublishingInfo
        this.pubItem.getMetadata().getSources().get(this.indexComponent).getPublishingInfo().setPublisher(CommonUtils.getUIValue(this.txtaPublisher));
        this.pubItem.getMetadata().getSources().get(this.indexComponent).getPublishingInfo().setPlace(CommonUtils.getUIValue(this.txtPlace));
        this.pubItem.getMetadata().getSources().get(this.indexComponent).getPublishingInfo().setEdition(CommonUtils.getUIValue(this.txtEdition));
        
        // store further attributes
        this.pubItem.getMetadata().getSources().get(this.indexComponent).setVolume(CommonUtils.getUIValue(this.txtVolume));
        this.pubItem.getMetadata().getSources().get(this.indexComponent).setIssue(CommonUtils.getUIValue(this.txtIssue));
        this.pubItem.getMetadata().getSources().get(this.indexComponent).setStartPage(CommonUtils.getUIValue(this.txtStartpage));
        this.pubItem.getMetadata().getSources().get(this.indexComponent).setEndPage(CommonUtils.getUIValue(this.txtEndpage));
        this.pubItem.getMetadata().getSources().get(this.indexComponent).setSequenceNumber(CommonUtils.getUIValue(this.txtSequenceNumber));

        // storing all identifiers
        for (int i=0; i<this.panDynamicIdentifier.getChildCount(); i++)
        {
            ((IdentifierUI)this.panDynamicIdentifier.getChildren().get(i)).storeValues();
        }

        // storing all creators
        for (int i=0; i<this.panDynamicCreator.getChildCount(); i++)
        {
            ((CreatorUI)this.panDynamicCreator.getChildren().get(i)).storeValues();
        }
    }    

    /**
     * Action handler when the user pushes a button.
     * @param ActionEvent event
     */
    public void processAction(ActionEvent event)
    {
        if (event.getSource().equals(this.btAdd))
        {
            this.addComponent();
        }
        else if (event.getSource().equals(this.btRemove))
        {
            this.removeComponent();
        }
    }

    /**
     * Creates the panel newly according to the values in the ValueObject.
     */
    public static void createDynamicParentPanel(HtmlPanelGrid parentPanel, PubItemVO pubItemVO)
    {
        // remove all components
        parentPanel.getChildren().clear();

        // initialize Source if none is given
        if (pubItemVO.getMetadata().getSources().size() == 0)
        {
            SourceVO newSource = new SourceVO();
            PublishingInfoVO newPublishingInfo = new PublishingInfoVO();
            newSource.setPublishingInfo(newPublishingInfo);
            IdentifierVO newIdentifier = new IdentifierVO();
            newSource.getIdentifiers().add(newIdentifier);
            CreatorVO newCreatorVO = new CreatorVO();
            newSource.getCreators().add(newCreatorVO);
            pubItemVO.getMetadata().getSources().add(newSource);
        }
        else
        {
            // initialize all PublishingInfos, Identifiers and Creators if none are given
            for (int i=0; i<pubItemVO.getMetadata().getSources().size(); i++)
            {
                if (pubItemVO.getMetadata().getSources().get(i).getPublishingInfo() == null)
                {
                    PublishingInfoVO newPublishingInfo = new PublishingInfoVO();
                    pubItemVO.getMetadata().getSources().get(i).setPublishingInfo(newPublishingInfo);
                }

                if (pubItemVO.getMetadata().getSources().get(i).getIdentifiers().size() == 0)
                {
                    IdentifierVO newIdentifier = new IdentifierVO();
                    pubItemVO.getMetadata().getSources().get(i).getIdentifiers().add(newIdentifier);
                }

                if (pubItemVO.getMetadata().getSources().get(i).getCreators().size() == 0)
                {
                    CreatorVO newCreatorVO = new CreatorVO();
                    pubItemVO.getMetadata().getSources().get(i).getCreators().add(newCreatorVO);
                }
            }
        }                
        
        // add all Sources
        for (int i = 0; i < pubItemVO.getMetadata().getSources().size(); i++)
        {
            parentPanel.getChildren().add(new SourceUI(pubItemVO, i, parentPanel));
        }
    }

    /**
     * Eventmethod that is being called whenever the user chooses to add a component.
     * The method adds a component to the ValueObject and adds new UI to the enclosing panel.
     */
    public void addComponent()
    {
        int indexNewComponent = this.indexComponent + 1;
        
        // add new component to VO
        SourceVO newSource = new SourceVO();
        PublishingInfoVO newSourcePublishingInfo = new PublishingInfoVO(); 
        newSource.setPublishingInfo(newSourcePublishingInfo);
        IdentifierVO newIdentifier = new IdentifierVO();
        newSource.getIdentifiers().add(newIdentifier);

        CreatorVO newSourceCreator = new CreatorVO();
        // create a new Organization for this person
        PersonVO newPerson = new PersonVO();
        OrganizationVO newPersonOrganization = new OrganizationVO();
        newPersonOrganization.setName(new TextVO());
        newPerson.getOrganizations().add(newPersonOrganization);        
        newSourceCreator.setOrganization(null);
        newSourceCreator.setPerson(newPerson);
        newSource.getCreators().add(newSourceCreator);        
        
        this.getPubItem().getMetadata().getSources().add(indexNewComponent, newSource);
        
        // add new Source to enclosing panel
        SourceUI newSourceUI = new SourceUI(this.getPubItem(), indexNewComponent, this.panDynamicParentPanel);
        this.panDynamicParentPanel.getChildren().add(indexNewComponent, newSourceUI);

        // reindex following UIs
        for (int i=(indexNewComponent + 1); i<this.panDynamicParentPanel.getChildCount(); i++)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Reindexing component " + this.getClass() + " with old index " + ((SourceUI)this.panDynamicParentPanel.getChildren().get(i)).getIndexComponent() + " to new index " + i);
            }
            
            ((SourceUI)this.panDynamicParentPanel.getChildren().get(i)).setIndexComponent(i);
        }
        
        this.refreshVisibilityOfRemoveButtons();
    }

    /**
     * Eventmethod that is being called whenever the user chooses to remove a Source.
     * The method removes a Source from the ValueObject and removes the UI from the enclosing panel.
     */
    public void removeComponent()
    {
        //store all values to VO
        for (int i=0; i<this.panDynamicParentPanel.getChildCount(); i++)
        {
            SourceUI sourceUI = (SourceUI)this.panDynamicParentPanel.getChildren().get(i);
            sourceUI.storeValues();
        }
                
        // remove Source from VO
        this.getPubItem().getMetadata().getSources().remove(this.indexComponent);        
        
        // recreate the panel
        SourceUI.createDynamicParentPanel(this.panDynamicParentPanel, this.pubItem);
    }

    /**
     * Sets the valueBinding for all values of the UI to the corresponding VO. 
     */    
    private void createValueBinding()
    {   
        this.cboGenre.setValueBinding("value", application.createValueBinding("#{editItem$EditItem.pubItem.metadata.sources[" + this.indexComponent + "].genre}"));
        this.txtaPublisher.setValueBinding("value", this.application.createValueBinding("#{editItem$EditItem.pubItem.metadata.sources[" + this.indexComponent + "].publishingInfo.publisher}"));
        this.txtPlace.setValueBinding("value", this.application.createValueBinding("#{editItem$EditItem.pubItem.metadata.sources[" + this.indexComponent + "].publishingInfo.place}"));
        this.txtEdition.setValueBinding("value", this.application.createValueBinding("#{editItem$EditItem.pubItem.metadata.sources[" + this.indexComponent + "].publishingInfo.edition}"));
        
        this.txtVolume.setValueBinding("value", this.application.createValueBinding("#{editItem$EditItem.pubItem.metadata.sources[" + this.indexComponent + "].volume}"));
        this.txtIssue.setValueBinding("value", this.application.createValueBinding("#{editItem$EditItem.pubItem.metadata.sources[" + this.indexComponent + "].issue}"));
        this.txtStartpage.setValueBinding("value", this.application.createValueBinding("#{editItem$EditItem.pubItem.metadata.sources[" + this.indexComponent + "].startPage}"));
        this.txtEndpage.setValueBinding("value", this.application.createValueBinding("#{editItem$EditItem.pubItem.metadata.sources[" + this.indexComponent + "].endPage}"));
        this.txtSequenceNumber.setValueBinding("value", this.application.createValueBinding("#{editItem$EditItem.pubItem.metadata.sources[" + this.indexComponent + "].sequenceNumber}"));
    }

    /**
     * Refresh the visibility of all remove buttons as their visibility may change when adding a new component (see PUBMAN-110). 
     */
    protected void refreshVisibilityOfRemoveButtons()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Refresh visibility of remove buttons...");
        }

        for (int i=0; i<this.panDynamicParentPanel.getChildCount(); i++)
        {
            // refresh visibility of all the remove buttons (see PUBMAN-110)
            ((SourceUI)this.panDynamicParentPanel.getChildren().get(i)).btRemove.setVisible(this.isRemoveButtonVisible());
        }
    }

    /**
     * Returns the ApplicationBean.
     * 
     * @return a reference to the scoped data bean (ApplicationBean)
     */
    protected ApplicationBean getApplicationBean()
    {
        Application application = FacesContext.getCurrentInstance().getApplication();
        return (ApplicationBean)application.getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), ApplicationBean.BEAN_NAME);
    }    
    
    /**
     * Determines if the remove button should be visible. 
     * The remove button should only be invisble if there is only one component (it should be possible to remove the 
     * first component, too if there are others following (see PUBMAN-110)).
     * As SourceUI does not inherit AbstractUI so far, this method has to implemented seperatly.
     * @return true if the remove button should be visible, otherwise false
     */
    protected boolean isRemoveButtonVisible()
    {       
        return (this.indexComponent != 0 || this.getPubItem().getMetadata().getSources().size() > 1);        
    }

    public PubItemVO getPubItem()
    {
        return pubItem;
    }

    public void setPubItem(PubItemVO pubItem)
    {
        this.pubItem = pubItem;
    }

    public int getIndexComponent()
    {
        return this.indexComponent;
    }

    public void setIndexComponent(int indexSource)
    {
        this.indexComponent = indexSource;

        // ValueBinding has to be set with the new index
        this.createValueBinding();
        
        for (int i=0; i<this.panDynamicTitle.getChildCount(); i++)
        {
            ((TitleUI)this.panDynamicTitle.getChildren().get(i)).setParentValueBinding("editItem$EditItem.pubItem.metadata.sources[" + this.indexComponent + "]");
        }

        for (int i=0; i<this.panDynamicIdentifier.getChildCount(); i++)
        {
            ((IdentifierUI)this.panDynamicIdentifier.getChildren().get(i)).setParentValueBinding("editItem$EditItem.pubItem.metadata.sources[" + this.indexComponent + "].identifiers");
        }

        for (int i=0; i<this.panDynamicCreator.getChildCount(); i++)
        {
            ((CreatorUI)this.panDynamicIdentifier.getChildren().get(i)).setParentValueBinding("editItem$EditItem.pubItem.metadata.sources[" + this.indexComponent + "].creators");
        }
    }

    public HtmlPanelGrid getPanDynamicTitle()
    {
        if (this.panDynamicTitle.getChildren().size() == 0)
        {
            TitleUI.createDynamicParentPanel(this.panDynamicTitle, this.getPubItem().getMetadata().getSources().get(this.indexComponent), "editItem$EditItem.pubItem.metadata.sources[" + this.indexComponent + "]");
        }
        return panDynamicTitle;
    }

    public void setPanDynamicTitle(HtmlPanelGrid panDynamicTitle)
    {
        this.panDynamicTitle = panDynamicTitle;
    }

    public HtmlPanelGrid getPanDynamicIdentifier()
    {
        if (this.panDynamicIdentifier.getChildren().size() == 0)
        {
            IdentifierUI.createDynamicParentPanel(this.panDynamicIdentifier, this.getPubItem().getMetadata().getSources().get(this.indexComponent).getIdentifiers(), "editItem$EditItem.pubItem.metadata.sources[" + this.indexComponent + "].identifiers");
        }
        return panDynamicIdentifier;
    }

    public void setPanDynamicIdentifier(HtmlPanelGrid panDynamicIdentifier)
    {
        this.panDynamicIdentifier = panDynamicIdentifier;
    }

    public HtmlPanelGrid getPanDynamicCreator()
    {
        if (this.panDynamicCreator.getChildren().size() == 0)
        {
            CreatorUI.createDynamicParentPanel(this.panDynamicCreator, this.getPubItem().getMetadata().getSources().get(this.indexComponent).getCreators(), "editItem$EditItem.pubItem.metadata.sources[" + this.indexComponent + "].creators", false);
        }
        return panDynamicCreator;
    }

    public void setPanDynamicCreator(HtmlPanelGrid panDynamicCreator)
    {
        this.panDynamicCreator = panDynamicCreator;
    }
}
