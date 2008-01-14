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

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.apache.log4j.Logger;
import com.sun.rave.web.ui.component.Button;
import com.sun.rave.web.ui.component.DropDown;
import com.sun.rave.web.ui.component.Label;
import com.sun.rave.web.ui.component.TextArea;
import com.sun.rave.web.ui.component.TextField;
import com.sun.rave.web.ui.component.Upload;
import com.sun.rave.web.ui.model.Option;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.PubFileVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;

/**
 * UI component for editing files. 
 * 
 * @author: Thomas Diebäcker, created 10.01.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Tue, 20 Nov 2007) $
 * Revised by DiT: 14.08.2007
 */
public class FileUI extends HtmlPanelGrid
{
    private static Logger logger = Logger.getLogger(FileUI.class);
    protected PubItemVO pubItem = null;
    protected int indexFile = -1;
    
    // for handling the resource bundles (i18n)
    Application application = FacesContext.getCurrentInstance().getApplication();
    // get the selected language...
    InternationalizationHelper i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
    // ... and set the refering resource bundle 
    ResourceBundle bundle = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
    
    // GUI components
    protected HtmlPanelGrid filePanel = new HtmlPanelGrid();
    protected HtmlPanelGrid attributesPanel = new HtmlPanelGrid();
    protected HtmlPanelGrid sizePanel = new HtmlPanelGrid();
    protected Label lblFile = new Label();
    protected Label lblSpace = new Label();
    protected Button btAdd = new Button();
    protected Button btRemove = new Button();
    protected Label lblPath = new Label();
    protected Upload fileUpload = new Upload();
    protected Button btUpload = new Button();
    protected Label lblName = new Label();
    protected TextField txtName = new TextField();
    protected Label lblSize = new Label();
    protected TextField txtSize = new TextField();
    protected Label lblDimension = new Label();
    protected Label lblContentType = new Label();
    protected DropDown cboContentType = new DropDown();
    protected Label lblMimeType = new Label();
    protected DropDown cboMimeType = new DropDown();
    protected Label lblVisibility = new Label();
    protected DropDown cboVisibility = new DropDown();
    protected Label lblDescription = new Label();
    protected TextArea txtaDescription = new TextArea();
   
    // constants for comboBoxes
    protected Option NO_ITEM_SET = new Option("", bundle.getString("EditItem_NO_ITEM_SET"));
    protected Option CONTENTTYPE_ABSTRACT = new Option(PubFileVO.ContentType.ABSTRACT.toString(), bundle.getString("EditItem_CONTENTTYPE_ABSTRACT"));
    protected Option CONTENTTYPE_PRE_PRINT = new Option(PubFileVO.ContentType.PRE_PRINT.toString(), bundle.getString("EditItem_CONTENTTYPE_PRE_PRINT"));
    protected Option CONTENTTYPE_POST_PRINT = new Option(PubFileVO.ContentType.POST_PRINT.toString(), bundle.getString("EditItem_CONTENTTYPE_POST_PRINT"));
    protected Option CONTENTTYPE_PUBLISHER_VERSION = new Option(PubFileVO.ContentType.PUBLISHER_VERSION.toString(), bundle.getString("EditItem_CONTENTTYPE_PUBLISHER_VERSION"));
    // removed according to JIRA, PUBMAN-12
    //protected Option CONTENTTYPE_CORRESPONDENCE = new Option(PubFileVO.ContentType.CORRESPONDENCE.toString(), bundleLabel.getString("EditItem_CONTENTTYPE_CORRESPONDENCE"));
    //protected Option CONTENTTYPE_COPYRIGHT_TRANSFER_AGREEMENT = new Option(PubFileVO.ContentType.COPYRIGHT_TRANSFER_AGREEMENT.toString(), bundleLabel.getString("EditItem_CONTENTTYPE_COPYRIGHT_TRANSFER_AGREEMENT"));
    protected Option CONTENTTYPE_SUPPLEMENTARY_MATERIAL = new Option(PubFileVO.ContentType.SUPPLEMENTARY_MATERIAL.toString(), bundle.getString("EditItem_CONTENTTYPE_SUPPLEMENTARY_MATERIAL"));
    protected Option[] CONTENTTYPE_OPTIONS = new Option[]{NO_ITEM_SET, CONTENTTYPE_ABSTRACT, CONTENTTYPE_PRE_PRINT, CONTENTTYPE_POST_PRINT, CONTENTTYPE_PUBLISHER_VERSION, CONTENTTYPE_SUPPLEMENTARY_MATERIAL};
    protected final Option MIMETYPE_APP_GZIP = new Option("application/gzip", bundle.getString("EditItem_MIMETYPE_APP_GZIP"));    
    protected final Option MIMETYPE_APP_DOC = new Option("application/msword", bundle.getString("EditItem_MIMETYPE_APP_DOC"));
    protected final Option MIMETYPE_APP_PDF = new Option("application/pdf", bundle.getString("EditItem_MIMETYPE_APP_PDF"));
    protected final Option MIMETYPE_APP_PPT = new Option("application/ppt", bundle.getString("EditItem_MIMETYPE_APP_PPT"));
    protected final Option MIMETYPE_APP_PS = new Option("application/ps", bundle.getString("EditItem_MIMETYPE_APP_PS"));
    protected final Option MIMETYPE_APP_RTF = new Option("application/rtf", bundle.getString("EditItem_MIMETYPE_APP_RTF"));    
    protected final Option MIMETYPE_APP_TEX = new Option("application/x-tex", bundle.getString("EditItem_MIMETYPE_APP_TEX"));
    protected final Option MIMETYPE_APP_XLATEX = new Option("application/x-latex", bundle.getString("EditItem_MIMETYPE_APP_XLATEX"));
    protected final Option MIMETYPE_APP_ZIP = new Option("application/zip", bundle.getString("EditItem_MIMETYPE_APP_ZIP"));
    protected final Option MIMETYPE_APP_EXCEL = new Option("application/vnd.ms-excel", bundle.getString("EditItem_MIMETYPE_APP_EXCEL"));
    protected final Option MIMETYPE_IMAGE_BMP = new Option("image/bmp", bundle.getString("EditItem_MIMETYPE_IMAGE_BMP"));
    protected final Option MIMETYPE_IMAGE_GIF = new Option("image/gif", bundle.getString("EditItem_MIMETYPE_IMAGE_GIF"));
    protected final Option MIMETYPE_IMAGE_JPEG = new Option("image/jpeg", bundle.getString("EditItem_MIMETYPE_IMAGE_JPEG"));
    protected final Option MIMETYPE_IMAGE_PNG = new Option("image/png", bundle.getString("EditItem_MIMETYPE_IMAGE_PNG"));
    protected final Option MIMETYPE_IMAGE_TIFF = new Option("image/tiff", bundle.getString("EditItem_MIMETYPE_IMAGE_TIFF"));
    protected final Option MIMETYPE_TEXT_HTML = new Option("text/html", bundle.getString("EditItem_MIMETYPE_TEXT_HTML"));    
    protected final Option MIMETYPE_TEXT_XML = new Option("text/xml", bundle.getString("EditItem_MIMETYPE_TEXT_XML"));    
    protected final Option MIMETYPE_APP_TEXT = new Option("text/plain", bundle.getString("EditItem_MIMETYPE_APP_TEXT"));    
    protected final Option MIMETYPE_VIDEO_AVI = new Option("video/avi", bundle.getString("EditItem_MIMETYPE_VIDEO_AVI"));
    protected final Option MIMETYPE_VIDEO_MPG = new Option("video/mpeg", bundle.getString("EditItem_MIMETYPE_VIDEO_MPG"));
    protected final Option[] MIMETYPE_OPTIONS = new Option[]{NO_ITEM_SET, MIMETYPE_APP_GZIP, MIMETYPE_APP_DOC, MIMETYPE_APP_PDF, 
                 MIMETYPE_APP_TEXT, MIMETYPE_APP_PPT, MIMETYPE_APP_PS, MIMETYPE_APP_RTF, MIMETYPE_APP_TEX, MIMETYPE_APP_XLATEX, 
                 MIMETYPE_APP_ZIP, MIMETYPE_APP_EXCEL, MIMETYPE_IMAGE_BMP, MIMETYPE_IMAGE_GIF, MIMETYPE_IMAGE_JPEG, MIMETYPE_IMAGE_PNG, 
                 MIMETYPE_IMAGE_TIFF, MIMETYPE_TEXT_HTML,  MIMETYPE_TEXT_XML, MIMETYPE_VIDEO_AVI, MIMETYPE_VIDEO_MPG};
    protected Option VISIBILITY_PUBLIC = new Option(PubFileVO.Visibility.PUBLIC.toString(), bundle.getString("EditItem_VISIBILITY_PUBLIC"));
    protected Option VISIBILITY_PRIVATE = new Option(PubFileVO.Visibility.PRIVATE.toString(), bundle.getString("EditItem_VISIBILITY_PRIVATE"));
    protected Option[] VISIBILITY_OPTIONS = new Option[]{NO_ITEM_SET, VISIBILITY_PUBLIC, VISIBILITY_PRIVATE};

    
    /**
     * Public constructor.
     * Initializes the UI.
     * @param pubItem the pubItem that is being edited
     * @param indexFile index of the file in the ValueObject
     */
    public FileUI(PubItemVO pubItem, int indexFile)
    {
        this.indexFile = indexFile;
        this.pubItem = pubItem;
        UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
        Application application = FacesContext.getCurrentInstance().getApplication();
        
        // re-init the combo-boxes due to direct language switch
        i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance().getApplication()
        .getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
        bundle = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
        
        NO_ITEM_SET = new Option("", bundle.getString("EditItem_NO_ITEM_SET"));
        CONTENTTYPE_ABSTRACT = new Option(PubFileVO.ContentType.ABSTRACT.toString(), bundle.getString("EditItem_CONTENTTYPE_ABSTRACT"));
        CONTENTTYPE_PRE_PRINT = new Option(PubFileVO.ContentType.PRE_PRINT.toString(), bundle.getString("EditItem_CONTENTTYPE_PRE_PRINT"));
        CONTENTTYPE_POST_PRINT = new Option(PubFileVO.ContentType.POST_PRINT.toString(), bundle.getString("EditItem_CONTENTTYPE_POST_PRINT"));
        CONTENTTYPE_PUBLISHER_VERSION = new Option(PubFileVO.ContentType.PUBLISHER_VERSION.toString(), bundle.getString("EditItem_CONTENTTYPE_PUBLISHER_VERSION"));
        // removed according to JIRA, PUBMAN-12
        //CONTENTTYPE_CORRESPONDENCE = new Option(PubFileVO.ContentType.CORRESPONDENCE.toString(), bundleLabel.getString("EditItem_CONTENTTYPE_CORRESPONDENCE"));
        //CONTENTTYPE_COPYRIGHT_TRANSFER_AGREEMENT = new Option(PubFileVO.ContentType.COPYRIGHT_TRANSFER_AGREEMENT.toString(), bundleLabel.getString("EditItem_CONTENTTYPE_COPYRIGHT_TRANSFER_AGREEMENT"));
        CONTENTTYPE_SUPPLEMENTARY_MATERIAL = new Option(PubFileVO.ContentType.SUPPLEMENTARY_MATERIAL.toString(), bundle.getString("EditItem_CONTENTTYPE_SUPPLEMENTARY_MATERIAL"));
        CONTENTTYPE_OPTIONS = new Option[]{NO_ITEM_SET, CONTENTTYPE_ABSTRACT, CONTENTTYPE_PRE_PRINT, CONTENTTYPE_POST_PRINT, CONTENTTYPE_PUBLISHER_VERSION, CONTENTTYPE_SUPPLEMENTARY_MATERIAL};
        VISIBILITY_PUBLIC = new Option(PubFileVO.Visibility.PUBLIC.toString(), bundle.getString("EditItem_VISIBILITY_PUBLIC"));
        VISIBILITY_PRIVATE = new Option(PubFileVO.Visibility.PRIVATE.toString(), bundle.getString("EditItem_VISIBILITY_PRIVATE"));
        VISIBILITY_OPTIONS = new Option[]{NO_ITEM_SET, VISIBILITY_PUBLIC, VISIBILITY_PRIVATE};

        // set attributes for all GUI components
        this.setId(viewRoot.createUniqueId() + "_panel" + Calendar.getInstance().getTimeInMillis());
        this.setColumns(1);
        this.setCellspacing("0");
        
        this.filePanel.setId(viewRoot.createUniqueId() + "_filePanel" + Calendar.getInstance().getTimeInMillis());
        this.filePanel.setColumns(4);
        this.filePanel.setCellspacing("0");
        this.filePanel.setCellpadding("0");
        this.filePanel.setColumnClasses("editItemLabelColumn, editItemFieldColumn, editItemButtonColumn, editItemButtonColumn");

        this.lblFile.setId(viewRoot.createUniqueId() + "_lblFile" + Calendar.getInstance().getTimeInMillis());
        this.lblFile.setValue(bundle.getString("EditItem_lblFile")); 
        this.lblFile.setLabelLevel(2);
        this.filePanel.getChildren().add(this.lblFile);

        this.lblSpace.setId(viewRoot.createUniqueId() + "_lblSpace" + Calendar.getInstance().getTimeInMillis());
        this.lblSpace.setValue("");
        this.filePanel.getChildren().add(this.lblSpace);

        this.btAdd.setId(viewRoot.createUniqueId() + "_btAdd" + Calendar.getInstance().getTimeInMillis());
        this.btAdd.setStyleClass("editDynamicButton");
        this.btAdd.setValue(bundle.getString("EditItem_btAdd"));
        this.btAdd.setImmediate(true);
        this.btAdd.setActionListener(application.createMethodBinding("#{editItem$EditItem.addFile}", new Class[]{ActionEvent.class}));
        this.filePanel.getChildren().add(this.btAdd);
        
        this.btRemove.setId(viewRoot.createUniqueId() + "_btRemove" + Calendar.getInstance().getTimeInMillis());
        this.btRemove.setStyleClass("editDynamicButton");
        this.btRemove.setValue(bundle.getString("EditItem_btRemove"));
        this.btRemove.setImmediate(true);
        this.btRemove.setActionListener(application.createMethodBinding("#{editItem$EditItem.removeFile}", new Class[]{ActionEvent.class}));
        this.filePanel.getChildren().add(this.btRemove);        
        
        this.lblPath.setId(viewRoot.createUniqueId() + "_lblPath" + Calendar.getInstance().getTimeInMillis());
        this.lblPath.setValue(bundle.getString("EditItem_lblPath"));        
        this.lblPath.setLabelLevel(3);
        this.filePanel.getChildren().add(this.lblPath);

        this.fileUpload.setId(viewRoot.createUniqueId() + "_fileUpload" + Calendar.getInstance().getTimeInMillis());        
        this.fileUpload.setImmediate(true);
        this.fileUpload.setStyleClass("editItemFileUpload");
        this.filePanel.getChildren().add(this.fileUpload);

        this.btUpload.setId(viewRoot.createUniqueId() + "_btUpload" + Calendar.getInstance().getTimeInMillis());
        this.btUpload.setStyleClass("editDynamicButton");
        this.btUpload.setValue(bundle.getString("EditItem_btUpload"));
        this.btUpload.setImmediate(true);
        this.btUpload.setActionListener(application.createMethodBinding("#{editItem$EditItem.handleUploadFileButtonAction}", new Class[]{ActionEvent.class}));        
        this.filePanel.getChildren().add(this.btUpload);

        this.getChildren().add(this.filePanel);
        
        this.attributesPanel.setId(viewRoot.createUniqueId() + "_attributesPanel" + Calendar.getInstance().getTimeInMillis());
        this.attributesPanel.setColumns(2);
        this.attributesPanel.setCellspacing("0");
        this.attributesPanel.setCellpadding("0");
        this.attributesPanel.setColumnClasses("editItemLabelColumn, editItemFieldColumn"); // might be overwritten! see initializeComponents()
        
        this.lblName.setId(viewRoot.createUniqueId() + "_lblName" + Calendar.getInstance().getTimeInMillis());
        this.lblName.setValue(bundle.getString("EditItem_lblName"));
        this.lblName.setLabelLevel(3);
        this.lblName.setRequiredIndicator(true);
        this.attributesPanel.getChildren().add(this.lblName);

        this.txtName.setId(viewRoot.createUniqueId() + "_txtName" + Calendar.getInstance().getTimeInMillis());
        this.txtName.setStyleClass("editItemTextFieldMedium");
        this.txtName.setValueBinding("value", application.createValueBinding("#{editItem$EditItem.pubItem.files[" + indexFile + "].name}"));
        this.txtName.setImmediate(true);
        this.attributesPanel.getChildren().add(this.txtName);        

        this.lblSize.setId(viewRoot.createUniqueId() + "_lblSize" + Calendar.getInstance().getTimeInMillis());
        this.lblSize.setValue(bundle.getString("EditItem_lblSize"));
        this.lblSize.setLabelLevel(3);
        this.attributesPanel.getChildren().add(this.lblSize);

        this.sizePanel.setId(viewRoot.createUniqueId() + "_sizePanel" + Calendar.getInstance().getTimeInMillis());
        this.sizePanel.setColumns(2);
        this.sizePanel.setBorder(0);
        this.sizePanel.setCellspacing("0");
        this.sizePanel.setCellpadding("0");
        this.attributesPanel.getChildren().add(this.sizePanel);

        this.txtSize.setId(viewRoot.createUniqueId() + "_txtSize" + Calendar.getInstance().getTimeInMillis());
        this.txtSize.setStyleClass("editItemTextFieldVeryShort");
        // fileSize is now calculated due to JIRA, PUBMAN-40
        BigDecimal fileSize = new BigDecimal(pubItem.getFiles().get(indexFile).getSize()).divide(new BigDecimal(1024), BigDecimal.ROUND_HALF_UP);
        this.txtSize.setText(fileSize.toString());
        this.txtSize.setDisabled(true);
        this.sizePanel.getChildren().add(this.txtSize);

        this.lblDimension.setId(viewRoot.createUniqueId() + "_lblDimension" + Calendar.getInstance().getTimeInMillis());
        this.lblDimension.setValue(bundle.getString("EditItem_lblDimension"));
        this.lblDimension.setLabelLevel(3);
        this.sizePanel.getChildren().add(this.lblDimension);

        this.lblContentType.setId(viewRoot.createUniqueId() + "_lblContentType" + Calendar.getInstance().getTimeInMillis());
        this.lblContentType.setValue(bundle.getString("EditItem_lblContentType"));
        this.lblContentType.setLabelLevel(3);
        this.lblContentType.setRequiredIndicator(true);
        this.attributesPanel.getChildren().add(this.lblContentType);

        this.cboContentType.setId(viewRoot.createUniqueId() + "_cboContentType" + Calendar.getInstance().getTimeInMillis());
        this.cboContentType.setStyleClass("editItemComboBoxShort");
        this.cboContentType.setItems(this.CONTENTTYPE_OPTIONS); 
        this.cboContentType.setValueBinding("value", application.createValueBinding("#{editItem$EditItem.pubItem.files[" + indexFile + "].contentTypeString}"));
        this.cboContentType.setImmediate(true);
        this.attributesPanel.getChildren().add(this.cboContentType);
        
        this.lblMimeType.setId(viewRoot.createUniqueId() + "_lblMimeType" + Calendar.getInstance().getTimeInMillis());
        this.lblMimeType.setValue(bundle.getString("EditItem_lblMimeType"));
        this.lblMimeType.setLabelLevel(3);
        this.lblMimeType.setRequiredIndicator(true);
        this.attributesPanel.getChildren().add(this.lblMimeType);

        this.cboMimeType.setId(viewRoot.createUniqueId() + "_cboMimeType" + Calendar.getInstance().getTimeInMillis());
        this.cboMimeType.setStyleClass("editItemComboBoxShort");
        this.cboMimeType.setItems(this.MIMETYPE_OPTIONS);        
        this.cboMimeType.setValueBinding("value", application.createValueBinding("#{editItem$EditItem.pubItem.files[" + indexFile + "].mimeType}"));
        this.cboMimeType.setImmediate(true);
        this.attributesPanel.getChildren().add(this.cboMimeType);
        
        this.lblVisibility.setId(viewRoot.createUniqueId() + "_lblVisibility" + Calendar.getInstance().getTimeInMillis());
        this.lblVisibility.setValue(bundle.getString("EditItem_lblVisibility"));
        this.lblVisibility.setLabelLevel(3);
        this.lblVisibility.setRequiredIndicator(true);
        this.attributesPanel.getChildren().add(this.lblVisibility);

        this.cboVisibility.setId(viewRoot.createUniqueId() + "_cboVisibility" + Calendar.getInstance().getTimeInMillis());
        this.cboVisibility.setStyleClass("editItemComboBoxShort");
        this.cboVisibility.setItems(this.VISIBILITY_OPTIONS);        
        this.cboVisibility.setValueBinding("value", application.createValueBinding("#{editItem$EditItem.pubItem.files[" + indexFile + "].visibilityString}"));
        this.cboVisibility.setImmediate(true);
        this.attributesPanel.getChildren().add(this.cboVisibility);

        this.lblDescription.setId(viewRoot.createUniqueId() + "_lblDescription" + Calendar.getInstance().getTimeInMillis());
        this.lblDescription.setValue(bundle.getString("EditItem_lblDescription"));
        this.lblDescription.setLabelLevel(3);
        this.attributesPanel.getChildren().add(this.lblDescription);
        
        this.txtaDescription.setId(viewRoot.createUniqueId() + "_txtaDescriptione" + Calendar.getInstance().getTimeInMillis());
        this.txtaDescription.setStyleClass("editItemTextArea");
        this.txtaDescription.setValueBinding("value", application.createValueBinding("#{editItem$EditItem.pubItem.files[" + indexFile + "].description}"));
        this.txtaDescription.setImmediate(true);
        this.attributesPanel.getChildren().add(this.txtaDescription);

        this.getChildren().add(this.attributesPanel);
        
        this.initializeComponents();
    }
    
    /**
     * Initializes all GUI components, hiding/showing fields and set/unset required fields
     */
    public void initializeComponents()
    {
        // determine if the file attributes should be shown or just the upload component
        boolean showFileAttributes = this.pubItem.getFiles().get(this.indexFile).getName() != null;
        
        //show/hide fields when needed
        this.lblName.setVisible(showFileAttributes);
        this.txtName.setVisible(showFileAttributes);
        this.lblSize.setVisible(showFileAttributes);
        this.txtSize.setVisible(showFileAttributes);
        this.lblDimension.setVisible(showFileAttributes);
        this.lblDescription.setVisible(showFileAttributes);
        this.txtaDescription.setVisible(showFileAttributes);
        this.lblContentType.setVisible(showFileAttributes);
        this.cboContentType.setVisible(showFileAttributes);
        this.lblMimeType.setVisible(showFileAttributes);
        this.cboMimeType.setVisible(showFileAttributes);
        this.lblVisibility.setVisible(showFileAttributes);
        this.cboVisibility.setVisible(showFileAttributes);
        this.btRemove.setVisible(showFileAttributes || this.indexFile > 0);
        String columnClasses = (showFileAttributes ? "editItemLabelColumn, editItemFieldColumn" : "");
        this.attributesPanel.setColumnClasses(columnClasses);
        
        //set/unset "required" according to visibility
        this.lblName.setRequiredIndicator(showFileAttributes);
        this.lblContentType.setRequiredIndicator(showFileAttributes);
        this.lblMimeType.setRequiredIndicator(showFileAttributes);
        this.lblVisibility.setRequiredIndicator(showFileAttributes);
    }

    /**
     * Stores all values of the UI in the corresponding VO. 
     */
    public void storeValues()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Storing values of file: " + this.indexFile);
        }
        
        PubFileVO fileVO = this.pubItem.getFiles().get(this.indexFile);
        
        fileVO.setName(this.getName());
        fileVO.setContentTypeString(this.getContentType());
        fileVO.setMimeType(this.getMimeType());
        fileVO.setVisibilityString(this.getVisibility());
        fileVO.setDescription(this.getDescription());
    }
    
    /**
     * Returns the name of the file that is currently set in the UI.
     * @return the current name of the file
     */
    public String getName()
    {
        return CommonUtils.getUIValue(this.txtName);
    }

    /**
     * Returns the size of the file that is currently set in the UI.
     * @return the current size of the file
     */
    public String getSize()
    {
        return CommonUtils.getUIValue(this.txtSize);
    }

    /**
     * Returns the contentType of the file that is currently set in the UI.
     * @return the current contentType of the file
     */
    public String getContentType()
    {
        return CommonUtils.getUIValue(this.cboContentType);
    }

    /**
     * Returns the mimeType of the file that is currently set in the UI.
     * @return the current mimeType of the file
     */
    public String getMimeType()
    {
        return CommonUtils.getUIValue(this.cboMimeType);
    }

    /**
     * Returns the visibility of the file that is currently set in the UI.
     * @return the current visibility of the file
     */
    public String getVisibility()
    {
        return CommonUtils.getUIValue(this.cboVisibility);
    }

    /**
     * Returns the description of the file that is currently set in the UI.
     * @return the current description of the file
     */
    public String getDescription()
    {
        return CommonUtils.getUIValue(this.txtaDescription);
    }

    /**
     * Returns the index of the current file.
     * @return the index of the file
     */
    public int getIndexFile()
    {
        return indexFile;
    }

    /**
     * Sets the index of the current file and renews the value binding.
     * @param indexFile the new index of the file
     */
    public void setIndexFile(int indexFile)
    {
        this.indexFile = indexFile;

        // ValueBinding has to be set with the new index
        this.txtName.setValueBinding("value", application.createValueBinding("#{editItem$EditItem.pubItem.files[" + indexFile + "].name}"));
        this.cboContentType.setValueBinding("value", application.createValueBinding("#{editItem$EditItem.pubItem.files[" + indexFile + "].contentTypeString}"));
        this.cboMimeType.setValueBinding("value", application.createValueBinding("#{editItem$EditItem.pubItem.files[" + indexFile + "].mimeType}"));
        this.cboVisibility.setValueBinding("value", application.createValueBinding("#{editItem$EditItem.pubItem.files[" + indexFile + "].visibilityString}"));
        this.txtaDescription.setValueBinding("value", application.createValueBinding("#{editItem$EditItem.pubItem.files[" + indexFile + "].description}"));
    }
}
