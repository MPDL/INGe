package de.mpg.escidoc.pubman.editItem.ui;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.PubFileVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;

/**
 * UI component for editing files. 
 * 
 * @author: Thomas Diebäcker, created 10.01.2007
 * @version: $Revision: 20 $ $LastChangedDate: 2007-12-05 10:51:55 +0100 (Mi, 05 Dez 2007) $
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
    ResourceBundle bundle = ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle());
    
    // GUI components
    protected HtmlPanelGrid filePanel = new HtmlPanelGrid();
    protected HtmlPanelGrid attributesPanel = new HtmlPanelGrid();
    protected HtmlPanelGrid sizePanel = new HtmlPanelGrid();
    protected HtmlOutputLabel lblFile = new HtmlOutputLabel();
    protected HtmlOutputLabel lblSpace = new HtmlOutputLabel();
    protected HtmlCommandButton btAdd = new HtmlCommandButton();
    protected HtmlCommandButton btRemove = new HtmlCommandButton();
    protected HtmlOutputLabel lblPath = new HtmlOutputLabel();
    // FIXME protected Upload fileUpload = new Upload();
    protected HtmlCommandButton btUpload = new HtmlCommandButton();
    protected HtmlOutputLabel lblName = new HtmlOutputLabel();
    protected HtmlInputText txtName = new HtmlInputText();
    protected HtmlOutputLabel lblSize = new HtmlOutputLabel();
    protected HtmlInputText txtSize = new HtmlInputText();
    protected HtmlOutputLabel lblDimension = new HtmlOutputLabel();
    protected HtmlOutputLabel lblContentType = new HtmlOutputLabel();
    protected HtmlSelectOneMenu cboContentType = new HtmlSelectOneMenu();
    protected HtmlOutputLabel lblMimeType = new HtmlOutputLabel();
    protected HtmlSelectOneMenu cboMimeType = new HtmlSelectOneMenu();
    protected HtmlOutputLabel lblVisibility = new HtmlOutputLabel();
    protected HtmlSelectOneMenu cboVisibility = new HtmlSelectOneMenu();
    protected HtmlOutputLabel lblDescription = new HtmlOutputLabel();
    protected HtmlInputTextarea txtaDescription = new HtmlInputTextarea();
   
    // constants for comboBoxes
    protected SelectItem NO_ITEM_SET = new SelectItem("", bundle.getString("EditItem_NO_ITEM_SET"));
    protected SelectItem CONTENTTYPE_ABSTRACT = new SelectItem(PubFileVO.ContentType.ABSTRACT.toString(), bundle.getString("EditItem_CONTENTTYPE_ABSTRACT"));
    protected SelectItem CONTENTTYPE_PRE_PRINT = new SelectItem(PubFileVO.ContentType.PRE_PRINT.toString(), bundle.getString("EditItem_CONTENTTYPE_PRE_PRINT"));
    protected SelectItem CONTENTTYPE_POST_PRINT = new SelectItem(PubFileVO.ContentType.POST_PRINT.toString(), bundle.getString("EditItem_CONTENTTYPE_POST_PRINT"));
    protected SelectItem CONTENTTYPE_PUBLISHER_VERSION = new SelectItem(PubFileVO.ContentType.PUBLISHER_VERSION.toString(), bundle.getString("EditItem_CONTENTTYPE_PUBLISHER_VERSION"));
    // removed according to JIRA, PUBMAN-12
    //protected SelectItem CONTENTTYPE_CORRESPONDENCE = new SelectItem(PubFileVO.ContentType.CORRESPONDENCE.toString(), bundleLabel.getString("EditItem_CONTENTTYPE_CORRESPONDENCE"));
    //protected SelectItem CONTENTTYPE_COPYRIGHT_TRANSFER_AGREEMENT = new SelectItem(PubFileVO.ContentType.COPYRIGHT_TRANSFER_AGREEMENT.toString(), bundleLabel.getString("EditItem_CONTENTTYPE_COPYRIGHT_TRANSFER_AGREEMENT"));
    protected SelectItem CONTENTTYPE_SUPPLEMENTARY_MATERIAL = new SelectItem(PubFileVO.ContentType.SUPPLEMENTARY_MATERIAL.toString(), bundle.getString("EditItem_CONTENTTYPE_SUPPLEMENTARY_MATERIAL"));
    protected SelectItem[] CONTENTTYPE_OPTIONS = new SelectItem[]{NO_ITEM_SET, CONTENTTYPE_ABSTRACT, CONTENTTYPE_PRE_PRINT, CONTENTTYPE_POST_PRINT, CONTENTTYPE_PUBLISHER_VERSION, CONTENTTYPE_SUPPLEMENTARY_MATERIAL};
    protected final SelectItem MIMETYPE_APP_GZIP = new SelectItem("application/gzip", bundle.getString("EditItem_MIMETYPE_APP_GZIP"));    
    protected final SelectItem MIMETYPE_APP_DOC = new SelectItem("application/msword", bundle.getString("EditItem_MIMETYPE_APP_DOC"));
    protected final SelectItem MIMETYPE_APP_PDF = new SelectItem("application/pdf", bundle.getString("EditItem_MIMETYPE_APP_PDF"));
    protected final SelectItem MIMETYPE_APP_PPT = new SelectItem("application/ppt", bundle.getString("EditItem_MIMETYPE_APP_PPT"));
    protected final SelectItem MIMETYPE_APP_PS = new SelectItem("application/ps", bundle.getString("EditItem_MIMETYPE_APP_PS"));
    protected final SelectItem MIMETYPE_APP_RTF = new SelectItem("application/rtf", bundle.getString("EditItem_MIMETYPE_APP_RTF"));    
    protected final SelectItem MIMETYPE_APP_TEX = new SelectItem("application/x-tex", bundle.getString("EditItem_MIMETYPE_APP_TEX"));
    protected final SelectItem MIMETYPE_APP_XLATEX = new SelectItem("application/x-latex", bundle.getString("EditItem_MIMETYPE_APP_XLATEX"));
    protected final SelectItem MIMETYPE_APP_ZIP = new SelectItem("application/zip", bundle.getString("EditItem_MIMETYPE_APP_ZIP"));
    protected final SelectItem MIMETYPE_APP_EXCEL = new SelectItem("application/vnd.ms-excel", bundle.getString("EditItem_MIMETYPE_APP_EXCEL"));
    protected final SelectItem MIMETYPE_IMAGE_BMP = new SelectItem("image/bmp", bundle.getString("EditItem_MIMETYPE_IMAGE_BMP"));
    protected final SelectItem MIMETYPE_IMAGE_GIF = new SelectItem("image/gif", bundle.getString("EditItem_MIMETYPE_IMAGE_GIF"));
    protected final SelectItem MIMETYPE_IMAGE_JPEG = new SelectItem("image/jpeg", bundle.getString("EditItem_MIMETYPE_IMAGE_JPEG"));
    protected final SelectItem MIMETYPE_IMAGE_PNG = new SelectItem("image/png", bundle.getString("EditItem_MIMETYPE_IMAGE_PNG"));
    protected final SelectItem MIMETYPE_IMAGE_TIFF = new SelectItem("image/tiff", bundle.getString("EditItem_MIMETYPE_IMAGE_TIFF"));
    protected final SelectItem MIMETYPE_TEXT_HTML = new SelectItem("text/html", bundle.getString("EditItem_MIMETYPE_TEXT_HTML"));    
    protected final SelectItem MIMETYPE_TEXT_XML = new SelectItem("text/xml", bundle.getString("EditItem_MIMETYPE_TEXT_XML"));    
    protected final SelectItem MIMETYPE_APP_TEXT = new SelectItem("text/plain", bundle.getString("EditItem_MIMETYPE_APP_TEXT"));    
    protected final SelectItem MIMETYPE_VIDEO_AVI = new SelectItem("video/avi", bundle.getString("EditItem_MIMETYPE_VIDEO_AVI"));
    protected final SelectItem MIMETYPE_VIDEO_MPG = new SelectItem("video/mpeg", bundle.getString("EditItem_MIMETYPE_VIDEO_MPG"));
    protected final SelectItem[] MIMETYPE_OPTIONS = new SelectItem[]{NO_ITEM_SET, MIMETYPE_APP_GZIP, MIMETYPE_APP_DOC, MIMETYPE_APP_PDF, 
                 MIMETYPE_APP_TEXT, MIMETYPE_APP_PPT, MIMETYPE_APP_PS, MIMETYPE_APP_RTF, MIMETYPE_APP_TEX, MIMETYPE_APP_XLATEX, 
                 MIMETYPE_APP_ZIP, MIMETYPE_APP_EXCEL, MIMETYPE_IMAGE_BMP, MIMETYPE_IMAGE_GIF, MIMETYPE_IMAGE_JPEG, MIMETYPE_IMAGE_PNG, 
                 MIMETYPE_IMAGE_TIFF, MIMETYPE_TEXT_HTML,  MIMETYPE_TEXT_XML, MIMETYPE_VIDEO_AVI, MIMETYPE_VIDEO_MPG};
    protected SelectItem VISIBILITY_PUBLIC = new SelectItem(PubFileVO.Visibility.PUBLIC.toString(), bundle.getString("EditItem_VISIBILITY_PUBLIC"));
    protected SelectItem VISIBILITY_PRIVATE = new SelectItem(PubFileVO.Visibility.PRIVATE.toString(), bundle.getString("EditItem_VISIBILITY_PRIVATE"));
    protected SelectItem[] VISIBILITY_OPTIONS = new SelectItem[]{NO_ITEM_SET, VISIBILITY_PUBLIC, VISIBILITY_PRIVATE};

    public FileUI()
    {
    	super();
    }

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
        bundle = ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle());
        
        NO_ITEM_SET = new SelectItem("", bundle.getString("EditItem_NO_ITEM_SET"));
        CONTENTTYPE_ABSTRACT = new SelectItem(PubFileVO.ContentType.ABSTRACT.toString(), bundle.getString("EditItem_CONTENTTYPE_ABSTRACT"));
        CONTENTTYPE_PRE_PRINT = new SelectItem(PubFileVO.ContentType.PRE_PRINT.toString(), bundle.getString("EditItem_CONTENTTYPE_PRE_PRINT"));
        CONTENTTYPE_POST_PRINT = new SelectItem(PubFileVO.ContentType.POST_PRINT.toString(), bundle.getString("EditItem_CONTENTTYPE_POST_PRINT"));
        CONTENTTYPE_PUBLISHER_VERSION = new SelectItem(PubFileVO.ContentType.PUBLISHER_VERSION.toString(), bundle.getString("EditItem_CONTENTTYPE_PUBLISHER_VERSION"));
        // removed according to JIRA, PUBMAN-12
        //CONTENTTYPE_CORRESPONDENCE = new SelectItem(PubFileVO.ContentType.CORRESPONDENCE.toString(), bundleLabel.getString("EditItem_CONTENTTYPE_CORRESPONDENCE"));
        //CONTENTTYPE_COPYRIGHT_TRANSFER_AGREEMENT = new SelectItem(PubFileVO.ContentType.COPYRIGHT_TRANSFER_AGREEMENT.toString(), bundleLabel.getString("EditItem_CONTENTTYPE_COPYRIGHT_TRANSFER_AGREEMENT"));
        CONTENTTYPE_SUPPLEMENTARY_MATERIAL = new SelectItem(PubFileVO.ContentType.SUPPLEMENTARY_MATERIAL.toString(), bundle.getString("EditItem_CONTENTTYPE_SUPPLEMENTARY_MATERIAL"));
        CONTENTTYPE_OPTIONS = new SelectItem[]{NO_ITEM_SET, CONTENTTYPE_ABSTRACT, CONTENTTYPE_PRE_PRINT, CONTENTTYPE_POST_PRINT, CONTENTTYPE_PUBLISHER_VERSION, CONTENTTYPE_SUPPLEMENTARY_MATERIAL};
        VISIBILITY_PUBLIC = new SelectItem(PubFileVO.Visibility.PUBLIC.toString(), bundle.getString("EditItem_VISIBILITY_PUBLIC"));
        VISIBILITY_PRIVATE = new SelectItem(PubFileVO.Visibility.PRIVATE.toString(), bundle.getString("EditItem_VISIBILITY_PRIVATE"));
        VISIBILITY_OPTIONS = new SelectItem[]{NO_ITEM_SET, VISIBILITY_PUBLIC, VISIBILITY_PRIVATE};

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
        // FIXME this.lblFile.setLabelLevel(2);
        this.filePanel.getChildren().add(this.lblFile);

        this.lblSpace.setId(viewRoot.createUniqueId() + "_lblSpace" + Calendar.getInstance().getTimeInMillis());
        this.lblSpace.setValue("");
        this.filePanel.getChildren().add(this.lblSpace);

        this.btAdd.setId(viewRoot.createUniqueId() + "_btAdd" + Calendar.getInstance().getTimeInMillis());
        this.btAdd.setStyleClass("editDynamicButton");
        this.btAdd.setValue(bundle.getString("EditItem_btAdd"));
        this.btAdd.setImmediate(true);
        this.btAdd.setActionListener(application.createMethodBinding("#{EditItem.addFile}", new Class[]{ActionEvent.class}));
        this.filePanel.getChildren().add(this.btAdd);
        
        this.btRemove.setId(viewRoot.createUniqueId() + "_btRemove" + Calendar.getInstance().getTimeInMillis());
        this.btRemove.setStyleClass("editDynamicButton");
        this.btRemove.setValue(bundle.getString("EditItem_btRemove"));
        this.btRemove.setImmediate(true);
        this.btRemove.setActionListener(application.createMethodBinding("#{EditItem.removeFile}", new Class[]{ActionEvent.class}));
        this.filePanel.getChildren().add(this.btRemove);        
        
        this.lblPath.setId(viewRoot.createUniqueId() + "_lblPath" + Calendar.getInstance().getTimeInMillis());
        this.lblPath.setValue(bundle.getString("EditItem_lblPath"));        
        // FIXME this.lblPath.setLabelLevel(3);
        this.filePanel.getChildren().add(this.lblPath);

//        this.fileUpload.setId(viewRoot.createUniqueId() + "_fileUpload" + Calendar.getInstance().getTimeInMillis());        
//        this.fileUpload.setImmediate(true);
//        this.fileUpload.setStyleClass("editItemFileUpload");
//        this.filePanel.getChildren().add(this.fileUpload);

        this.btUpload.setId(viewRoot.createUniqueId() + "_btUpload" + Calendar.getInstance().getTimeInMillis());
        this.btUpload.setStyleClass("editDynamicButton");
        this.btUpload.setValue(bundle.getString("EditItem_btUpload"));
        this.btUpload.setImmediate(true);
        this.btUpload.setActionListener(application.createMethodBinding("#{EditItem.handleUploadFileButtonAction}", new Class[]{ActionEvent.class}));        
        this.filePanel.getChildren().add(this.btUpload);

        this.getChildren().add(this.filePanel);
        
        this.attributesPanel.setId(viewRoot.createUniqueId() + "_attributesPanel" + Calendar.getInstance().getTimeInMillis());
        this.attributesPanel.setColumns(2);
        this.attributesPanel.setCellspacing("0");
        this.attributesPanel.setCellpadding("0");
        this.attributesPanel.setColumnClasses("editItemLabelColumn, editItemFieldColumn"); // might be overwritten! see initializeComponents()
        
        this.lblName.setId(viewRoot.createUniqueId() + "_lblName" + Calendar.getInstance().getTimeInMillis());
        this.lblName.setValue(bundle.getString("EditItem_lblName"));
        // FIXME this.lblName.setLabelLevel(3);
        // FIXME this.lblName.setRequiredIndicator(true);
        this.attributesPanel.getChildren().add(this.lblName);

        this.txtName.setId(viewRoot.createUniqueId() + "_txtName" + Calendar.getInstance().getTimeInMillis());
        this.txtName.setStyleClass("editItemHtmlInputTextMedium");
        this.txtName.setValueBinding("value", application.createValueBinding("#{EditItem.pubItem.files[" + indexFile + "].name}"));
        this.txtName.setImmediate(true);
        this.attributesPanel.getChildren().add(this.txtName);        

        this.lblSize.setId(viewRoot.createUniqueId() + "_lblSize" + Calendar.getInstance().getTimeInMillis());
        this.lblSize.setValue(bundle.getString("EditItem_lblSize"));
        // FIXME this.lblSize.setLabelLevel(3);
        this.attributesPanel.getChildren().add(this.lblSize);

        this.sizePanel.setId(viewRoot.createUniqueId() + "_sizePanel" + Calendar.getInstance().getTimeInMillis());
        this.sizePanel.setColumns(2);
        this.sizePanel.setBorder(0);
        this.sizePanel.setCellspacing("0");
        this.sizePanel.setCellpadding("0");
        this.attributesPanel.getChildren().add(this.sizePanel);

        this.txtSize.setId(viewRoot.createUniqueId() + "_txtSize" + Calendar.getInstance().getTimeInMillis());
        this.txtSize.setStyleClass("editItemHtmlInputTextVeryShort");
        // fileSize is now calculated due to JIRA, PUBMAN-40
        BigDecimal fileSize = new BigDecimal(pubItem.getFiles().get(indexFile).getSize()).divide(new BigDecimal(1024), BigDecimal.ROUND_HALF_UP);
        this.txtSize.setValue(fileSize.toString());
        this.txtSize.setDisabled(true);
        this.sizePanel.getChildren().add(this.txtSize);

        this.lblDimension.setId(viewRoot.createUniqueId() + "_lblDimension" + Calendar.getInstance().getTimeInMillis());
        this.lblDimension.setValue(bundle.getString("EditItem_lblDimension"));
        // FIXME this.lblDimension.setLabelLevel(3);
        this.sizePanel.getChildren().add(this.lblDimension);

        this.lblContentType.setId(viewRoot.createUniqueId() + "_lblContentType" + Calendar.getInstance().getTimeInMillis());
        this.lblContentType.setValue(bundle.getString("EditItem_lblContentType"));
        // FIXME this.lblContentType.setLabelLevel(3);
        // FIXME this.lblContentType.setRequiredIndicator(true);
        this.attributesPanel.getChildren().add(this.lblContentType);

        this.cboContentType.setId(viewRoot.createUniqueId() + "_cboContentType" + Calendar.getInstance().getTimeInMillis());
        this.cboContentType.setStyleClass("editItemComboBoxShort");
        this.cboContentType.getChildren().clear();
        this.cboContentType.getChildren().addAll(CommonUtils.convertToSelectItemsUI(this.CONTENTTYPE_OPTIONS)); 
        this.cboContentType.setValueBinding("value", application.createValueBinding("#{EditItem.pubItem.files[" + indexFile + "].contentTypeString}"));
        this.cboContentType.setImmediate(true);
        this.attributesPanel.getChildren().add(this.cboContentType);
        
        this.lblMimeType.setId(viewRoot.createUniqueId() + "_lblMimeType" + Calendar.getInstance().getTimeInMillis());
        this.lblMimeType.setValue(bundle.getString("EditItem_lblMimeType"));
        // FIXME this.lblMimeType.setLabelLevel(3);
        // FIXME this.lblMimeType.setRequiredIndicator(true);
        this.attributesPanel.getChildren().add(this.lblMimeType);

        this.cboMimeType.setId(viewRoot.createUniqueId() + "_cboMimeType" + Calendar.getInstance().getTimeInMillis());
        this.cboMimeType.setStyleClass("editItemComboBoxShort");
        this.cboMimeType.getChildren().clear();
        this.cboMimeType.getChildren().addAll(CommonUtils.convertToSelectItemsUI(this.MIMETYPE_OPTIONS));        
        this.cboMimeType.setValueBinding("value", application.createValueBinding("#{EditItem.pubItem.files[" + indexFile + "].mimeType}"));
        this.cboMimeType.setImmediate(true);
        this.attributesPanel.getChildren().add(this.cboMimeType);
        
        this.lblVisibility.setId(viewRoot.createUniqueId() + "_lblVisibility" + Calendar.getInstance().getTimeInMillis());
        this.lblVisibility.setValue(bundle.getString("EditItem_lblVisibility"));
        // FIXME this.lblVisibility.setLabelLevel(3);
        // FIXME this.lblVisibility.setRequiredIndicator(true);
        this.attributesPanel.getChildren().add(this.lblVisibility);

        this.cboVisibility.setId(viewRoot.createUniqueId() + "_cboVisibility" + Calendar.getInstance().getTimeInMillis());
        this.cboVisibility.setStyleClass("editItemComboBoxShort");
        this.cboVisibility.getChildren().clear();
        this.cboVisibility.getChildren().addAll(CommonUtils.convertToSelectItemsUI(this.VISIBILITY_OPTIONS));        
        this.cboVisibility.setValueBinding("value", application.createValueBinding("#{EditItem.pubItem.files[" + indexFile + "].visibilityString}"));
        this.cboVisibility.setImmediate(true);
        this.attributesPanel.getChildren().add(this.cboVisibility);

        this.lblDescription.setId(viewRoot.createUniqueId() + "_lblDescription" + Calendar.getInstance().getTimeInMillis());
        this.lblDescription.setValue(bundle.getString("EditItem_lblDescription"));
        // FIXME this.lblDescription.setLabelLevel(3);
        this.attributesPanel.getChildren().add(this.lblDescription);
        
        this.txtaDescription.setId(viewRoot.createUniqueId() + "_txtaDescriptione" + Calendar.getInstance().getTimeInMillis());
        this.txtaDescription.setStyleClass("editItemHtmlInputTextarea");
        this.txtaDescription.setValueBinding("value", application.createValueBinding("#{EditItem.pubItem.files[" + indexFile + "].description}"));
        this.txtaDescription.setImmediate(true);
        this.attributesPanel.getChildren().add(this.txtaDescription);

        this.getChildren().add(this.attributesPanel);
        
        this.initializeComponents();
    }

    public Object processSaveState(FacesContext context) 
    {
        Object superState = super.processSaveState(context);
        return new Object[] {superState, new Integer(getChildCount())};
    }
    
    public void processRestoreState(FacesContext context, Object state) 
    {
        // At this point in time the tree has already been restored, but not before our ctor added the default children.
        // Since we saved the number of children in processSaveState, we know how many children should remain within this component. 
    	// We assume that the saved tree will have been restored 'behind' the children we put into it from within the ctor.
        Object[] values = (Object[]) state;
        Integer savedChildCount = (Integer) values[1];
        for (int i = getChildCount() - savedChildCount.intValue(); i > 0; i--) 
        {
            getChildren().remove(0);
        }
        super.processRestoreState(context, values[0]);
    }

    /**
     * Initializes all GUI components, hiding/showing fields and set/unset required fields
     */
    public void initializeComponents()
    {
        // determine if the file attributes should be shown or just the upload component
        boolean showFileAttributes = this.pubItem.getFiles().get(this.indexFile).getName() != null;
        
        // FIXME show/hide fields by css when needed
        this.lblName.setRendered(showFileAttributes);
        this.txtName.setRendered(showFileAttributes);
        this.lblSize.setRendered(showFileAttributes);
        this.txtSize.setRendered(showFileAttributes);
        this.lblDimension.setRendered(showFileAttributes);
        this.lblDescription.setRendered(showFileAttributes);
        this.txtaDescription.setRendered(showFileAttributes);
        this.lblContentType.setRendered(showFileAttributes);
        this.cboContentType.setRendered(showFileAttributes);
        this.lblMimeType.setRendered(showFileAttributes);
        this.cboMimeType.setRendered(showFileAttributes);
        this.lblVisibility.setRendered(showFileAttributes);
        this.cboVisibility.setRendered(showFileAttributes);
        
        this.btRemove.setRendered(showFileAttributes || this.indexFile == 0);
        String columnClasses = (showFileAttributes ? "editItemLabelColumn, editItemFieldColumn" : "");
        this.attributesPanel.setColumnClasses(columnClasses);
        
        //set/unset "required" according to visibility
        // FIXME this.lblName.setRequiredIndicator(showFileAttributes);
        // FIXME this.lblContentType.setRequiredIndicator(showFileAttributes);
        // FIXME this.lblMimeType.setRequiredIndicator(showFileAttributes);
        // FIXME this.lblVisibility.setRequiredIndicator(showFileAttributes);
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
        this.txtName.setValueBinding("value", application.createValueBinding("#{EditItem.pubItem.files[" + indexFile + "].name}"));
        this.cboContentType.setValueBinding("value", application.createValueBinding("#{EditItem.pubItem.files[" + indexFile + "].contentTypeString}"));
        this.cboMimeType.setValueBinding("value", application.createValueBinding("#{EditItem.pubItem.files[" + indexFile + "].mimeType}"));
        this.cboVisibility.setValueBinding("value", application.createValueBinding("#{EditItem.pubItem.files[" + indexFile + "].visibilityString}"));
        this.txtaDescription.setValueBinding("value", application.createValueBinding("#{EditItem.pubItem.files[" + indexFile + "].description}"));
    }
}
