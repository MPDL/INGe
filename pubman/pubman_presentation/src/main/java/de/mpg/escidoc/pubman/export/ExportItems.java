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

package de.mpg.escidoc.pubman.export;

import java.util.List;
import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;
import com.sun.rave.web.ui.appbase.AbstractFragmentBean;
import com.sun.rave.web.ui.component.DropDown;
import com.sun.rave.web.ui.component.MessageGroup;
import com.sun.rave.web.ui.component.RadioButtonGroup;
import com.sun.rave.web.ui.model.Option;
import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.RightsManagementSessionBean;
import de.mpg.escidoc.pubman.search.SearchResultList;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO;
import de.mpg.escidoc.services.common.valueobjects.FileFormatVO;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;


/**
  * Fragment class for item exporting.
  * This class provides all functionality for exporting items according the selected export format 
  * (layout or structured) and the selected file format (PDF, TXT, etc..).  
  * @author:  Galina Stancheva, created 02.08.2007
  * @version: $Revision:  $ $LastChangedDate:  $
  *  Revised by StG: 28.09.2007
*/
public class ExportItems extends AbstractFragmentBean
{
    private static Logger logger = Logger.getLogger(ExportItems.class);
 
    // constant for the function export to check the rights and/or if the function has to be disabled (DiT)
    private final String FUNCTION_EXPORT = "export";
 
    //For handling the resource bundles (i18n)
    protected Application application = FacesContext.getCurrentInstance().getApplication();
    //get the selected language...
    protected InternationalizationHelper i18nHelper = (InternationalizationHelper)application
    .getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
    //... and set the refering resource bundle 
    protected ResourceBundle bundleLabel = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
    protected ResourceBundle bundleMessage = ResourceBundle.getBundle(i18nHelper.getSelectedMessagesBundle());

    // binded components in JSP
    private MessageGroup valMessage = new MessageGroup();
    private DropDown cboLayoutCitStyles = new DropDown();
    private RadioButtonGroup rbgExportFormats = new RadioButtonGroup();
    private RadioButtonGroup rbgFileFormats = new RadioButtonGroup();
    private HtmlCommandButton btnDisplayItems = new HtmlCommandButton();
    private HtmlCommandButton btnExportDownload = new HtmlCommandButton();
    private HtmlCommandButton btnExportEMail = new HtmlCommandButton();

    // constants for comboBoxes and RadioButtonGroups
    public Option EXPORTFORMAT_STRUCTURED = new Option("STRUCTURED", bundleLabel.getString("Export_ExportFormat_STRUCTURED"));
    public Option EXPORTFORMAT_LAYOUT = new Option("LAYOUT", bundleLabel.getString("Export_ExportFormat_LAYOUT"));
    public Option[] EXPORTFORMAT_OPTIONS = new Option[]{EXPORTFORMAT_STRUCTURED, EXPORTFORMAT_LAYOUT};
    public Option LAYOUTCITATIONSTYLE_APA = new Option("APA", bundleLabel.getString("Export_LayoutCitationStyle_APA"));
    public Option[] LAYOUTCITATIONSTYLE_OPTIONS = new Option[]{LAYOUTCITATIONSTYLE_APA};
    public Option FILEFORMAT_PDF = new Option("pdf", bundleLabel.getString("Export_FileFormat_PDF"));
    public Option FILEFORMAT_ODT = new Option("odt", bundleLabel.getString("Export_FileFormat_ODT"));
    public Option FILEFORMAT_RTF = new Option("rtf", bundleLabel.getString("Export_FileFormat_RTF"));
    public Option FILEFORMAT_HTML = new Option("html", bundleLabel.getString("Export_FileFormat_HTML"));
    public Option[] FILEFORMAT_OPTIONS = new Option[]{FILEFORMAT_PDF, FILEFORMAT_ODT, FILEFORMAT_RTF, FILEFORMAT_HTML};
 
    // constants for error and status messages
    public static final String MESSAGE_NO_ITEM_FOREXPORT_SELECTED = "exportItems_NoItemSelected";
    // constants for error and status messages
    public static final String MESSAGE_NO_EXPORTDATA_DELIVERED = "exportItems_NoDataDelivered";
    public static final String MESSAGE_EXPORT_EMAIL_SENT = "exportItems_EmailSent";
    public static final String MESSAGE_EXPORT_EMAIL_NOTSENT = "exportItems_EmailNotSent";
    public static final String MESSAGE_EXPORT_EMAIL_TEXT = "exportItems_EmailText";
    public static final String MESSAGE_EXPORT_EMAIL_SUBJECT_TEXT = "exportItems_EmailSubjectText";
    
    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation. 
     */
    public void init()
    {
        if (logger.isDebugEnabled())
        {
            logger.info(" init ExportItems >>>");                     
        }   
       super.init();
       setExportFormats();

    }
   
    
    /**
     * Returns the RightsManagementSessionBean.
     * @author StG
     * @return a reference to the scoped data bean (RightsManagementSessionBean)
     */
    protected RightsManagementSessionBean getRightsManagementSessionBean()
    {
        return (RightsManagementSessionBean)getBean(RightsManagementSessionBean.BEAN_NAME);
    }

    /**
     * Returns true if the AdvancedSearch should be disabled by the escidoc properties file.
     * @author StG
     * @return
     */
    public boolean getVisibleExport()
    {
        return !this.getRightsManagementSessionBean().isDisabled(RightsManagementSessionBean.PROPERTY_PREFIX_FOR_DISABLEING_FUNCTIONS + "." + this.FUNCTION_EXPORT);
    }    
    
    public String setExportFormats(){
        logger.debug(">>> setExportFormats "); 
        try
        {
            //get the existing export formats from the external service 
            List<ExportFormatVO> listExportFormatVO = this.getItemControllerSessionBean().retrieveExportFormats();
            this.getSessionBean().setListExportFormatVO(listExportFormatVO);
       }        
        catch (TechnicalException e)
        {
            logger.error("Could not ser the export formats." + "\n" + e.toString());
            ((ErrorPage)this.getBean(ErrorPage.BEAN_NAME)).setException(e);
        
            return ErrorPage.LOAD_ERRORPAGE;
        }
         return "OK";
    }
    
    /**
     * Gets the export citation layout styles and the export file formats from the external service 
     * and sets them. 
     * ToDo StG. TO be finished and used.
     */
    /*private void fillExportFormats(){
        logger.debug(">>> setFormats "); 
        //get the existing export formats from the external services calling the itemExporting interface in 
        // pubman_logic. 
         ArrayList<ExportFormatVO> listExportFormatVO = this.getItemControllerSessionBean().retrieveExportFormats();
         
         if (logger.isDebugEnabled())
         {
             logger.debug(">>> listExportFormatVO.size: " + listExportFormatVO.size());                     
         }         
        ArrayList<Option>  layoutStylesOptionsAL = new ArrayList();
        ArrayList<Option>  fileFormatsAL = new ArrayList();

        //set the options in order to fill the dropdown list and the radion buttons
        for (int i=0; i<listExportFormatVO.size(); i++){
           
            ExportFormatVO tempExportFormatVO = listExportFormatVO.get(i);
 
            if (tempExportFormatVO.getFormatType() == ExportFormatVO.FormatType.STRUCTURED){
                this.getItemControllerSessionBean().setCurrentExportFormat(tempExportFormatVO);   
            }
            
            //if this is a layout format
            if (tempExportFormatVO.getFormatType() == ExportFormatVO.FormatType.LAYOUT){
                
                String layoutStyleName = tempExportFormatVO.getName();
               Option tempOpt = new Option(layoutStyleName.toUpperCase());   
                       
                if (logger.isDebugEnabled())
                {
                    logger.debug(">>> Added export layout format: " + tempOpt.getValue());                     
                }
                                
                layoutStylesOptionsAL.add(tempOpt);
                for (int j=0; j < tempExportFormatVO.getFileFormats().size(); j++){
                    
                    // extract the right string cause the file format comes as mimetype form the xml-source. e.g. text/html 
                    String ff = (String)tempExportFormatVO.getFileFormats().get(j);
                    ff = ff.substring(ff.indexOf("/"));
                    logger.debug(">>> the actual file format: " + ff);
                    logger.debug(">>> Added export layout format: " + tempOpt.getValue());
                    if ( !fileFormatsAL.contains(ff) ){
                        
                        tempOpt = new Option(ff);
                        fileFormatsAL.add(tempOpt);
                    }
                }
            }
         }
 
        LAYOUTCITATIONSTYLE_OPTIONS = (Option[])layoutStylesOptionsAL.toArray();
        FILEFORMAT_OPTIONS =  (Option[])fileFormatsAL.toArray();
        if (logger.isDebugEnabled())
        {
            logger.debug(">>> LAYOUTCITATIONSTYLE_OPTIONS: " + LAYOUTCITATIONSTYLE_OPTIONS.toString());                     
            logger.debug(">>> FILEFORMAT_OPTIONS: " + FILEFORMAT_OPTIONS.toString());                     
        }
        
    }*/
        
    public DropDown getCboLayoutCitStyles()
    {
        return cboLayoutCitStyles;
    }

    public void setCboLayoutCitStyles(DropDown cboLayoutCitStyles)
    {
        this.cboLayoutCitStyles = cboLayoutCitStyles;
    }

    public RadioButtonGroup getRbgExportFormats()
    {
        return rbgExportFormats;
    }

    public void setRbgExportFormats(RadioButtonGroup rbgExportFormats)
    {
        this.rbgExportFormats = rbgExportFormats;
    }

    public RadioButtonGroup getRbgFileFormats()
    {
        return rbgFileFormats;
    }

    public void setBtnDisplayItems(HtmlCommandButton b)
    {
        this.btnDisplayItems = b;
    }

    public HtmlCommandButton getBtnDisplayItems()
    {
        return btnDisplayItems;
    }
    public void setBtnExportDownload(HtmlCommandButton b)
    {
        this.btnExportDownload = b;
    }

    public HtmlCommandButton getBtnExportDownload()
    {
        return btnExportDownload;
    }

    public void setBtnExportEMail(HtmlCommandButton b)
    {
        this.btnExportEMail = b;
    }

    public HtmlCommandButton getBtnExportEMail()
    {
        return btnExportEMail;
    }
   
     public void setRbgFileFormats(RadioButtonGroup rbgFileFormats)
    {
        this.rbgFileFormats = rbgFileFormats;
    }
    
    public Option[] getEXPORTFORMAT_OPTIONS()
    {
        return this.EXPORTFORMAT_OPTIONS;
    }
 
    public Option[] getFILEFORMAT_OPTIONS()
    {
        return this.FILEFORMAT_OPTIONS;
    }

    public Option[] getLAYOUTCITATIONSTYLE_OPTIONS()
    {
        return this.LAYOUTCITATIONSTYLE_OPTIONS;
    }    


    /*
     * Gets the session bean. 
     */
    
    public ExportItemsSessionBean getSessionBean()
    {
        return (ExportItemsSessionBean)this.getBean(ExportItemsSessionBean.BEAN_NAME);
    }

    /**
     * Returns a reference to the scoped data bean (the ItemControllerSessionBean). 
     * @return a reference to the scoped data bean
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean)getBean(ItemControllerSessionBean.BEAN_NAME);
    }

        
    /*
     * Set up the current export format and its allowed file format. 
     * (Now there is only one structured format. When more then the method should be adjusted.)
     */
    /*public void updateExportFileFormat(){
        
        String selExportFormat = this.getSessionBean().getExportFormatType();        
        
        ArrayList<ExportFormatVO> listExportFormatVO  = this.getSessionBean().getListExportFormatVO();
 
        //Vorschalg: ist besser wenn in die GUI bzw. ein Listener nach der richtigen kombination
        // "name - file format name" aufpasst. Dann kann man die werde von der Oberfäche holen
        // und den curExportFormat setzen.
        //name des exports holen 
        //cboLayoutCitStyles.getSelected()
        //von der Liste aller Formate den ausgewählten ExportVormatVO hole.
        //entsprechende file formate selektierbar setzten. die andere deselektierbar.
        //das erste als default set nehmen.
        
        //ToDo StG. so mit loop über alle exprt formate ist sehr aufwändig. TO BE REMOVED. siehe vorschlag oben.
        for (int j=0; j < listExportFormatVO.size(); j++){
            ExportFormatVO  tempEFVO = (ExportFormatVO)listExportFormatVO.get(j);
            
            if ( selExportFormat.equals((String)ExportItems.EXPORTFORMAT_STRUCTURED.getValue()) && 
                    tempEFVO.equals(selExportFormat)  )
            {
                this.getItemControllerSessionBean().setCurrentExportFormat(tempEFVO);
            }
 
            else if ( selExportFormat.equals((String)ExportItems.EXPORTFORMAT_LAYOUT.getValue()) ){
                 if ( tempEFVO.getName().equals(this.getSessionBean().getExportFormatName()) && 
                     tempEFVO.getFileFormats().contains( this.getSessionBean().getFileFormat()) ){
                    
                    tempEFVO.setSelectedFileFormat(this.getSessionBean().getFileFormat());
                    this.getItemControllerSessionBean().setCurrentExportFormat(tempEFVO);    
                    
                } else { //error message "selected combination is not valid"
                }
            } 
        }                     
    };*/
    
    
    /*
     * Updates the GUI relatively the selected export format. 
     */
    public void updateExportFormats(){
        
        // get the selected export format by the SessionBean
    	
    	
        String selExportFormat = this.getSessionBean().getExportFormatType(); 
        
        if (logger.isDebugEnabled())
        {
            logger.debug(">>>  New export format: " + selExportFormat);                     
            logger.debug(selExportFormat + "; " + (String)this.EXPORTFORMAT_STRUCTURED.getValue());
            logger.debug(selExportFormat + "; " + (String)this.EXPORTFORMAT_LAYOUT.getValue());
            
            logger.debug("curExportFormat:" + this.getSessionBean().getCurExportFormatVO());
        }
 

        // change the GUI according to the values
        if (selExportFormat.equals((String)this.EXPORTFORMAT_STRUCTURED.getValue()))
        {    
             cboLayoutCitStyles.setDisabled(true);  
             this.getSessionBean().setExportFormatName("ENDNOTE");
             
             this.getSessionBean().setFileFormat(FileFormatVO.TEXT_NAME);
             rbgFileFormats.setDisabled(true);         
        }
        else if (selExportFormat.equals((String)this.EXPORTFORMAT_LAYOUT.getValue()))
        {
            cboLayoutCitStyles.setDisabled(false);  
            this.getSessionBean().setExportFormatName("APA");
            //set default fileFormat of APA to PDF 
            String fileFormat = this.getSessionBean().getFileFormat();  
            if ( fileFormat != null | fileFormat.trim().equals("") || 
            		fileFormat.trim().equals(FileFormatVO.TEXT_NAME)
            	)
            	this.getSessionBean().setFileFormat(FileFormatVO.PDF_NAME);
            rbgFileFormats.setDisabled(false);  
         }
        
    }

     ///////////////////////////////////////////////////////////////////////////////////////
    ///////// next methods are used by EMailing

    
    /*
     * Disables the export components when the email page gets open. 
     */
    public void disableExportPanComps(boolean b){
        rbgFileFormats.setDisabled(b);   
        cboLayoutCitStyles.setDisabled(b);
        rbgExportFormats.setDisabled(b);
        btnDisplayItems.setDisabled(b);
        btnExportDownload.setDisabled(b);
        btnExportEMail.setDisabled(b);        
    }
        
        
    /**
     * redirects the user to the list he came from
     * 
     * @return String nav rule for loading the page the user came from
     */
    public String backToList()
    {
       return (this.getSessionBean().getNavigationStringToGoBack() != null ? 
                this.getSessionBean().getNavigationStringToGoBack() : SearchResultList.LOAD_SEARCHRESULTLIST);
    }
    public MessageGroup getValMessage()
    {
        return valMessage;
    }

    public void setValMessage(MessageGroup valMessage)
    {
        this.valMessage = valMessage;
    }
    /**
     * Adds and removes messages concerning item lists.
     */
    
    /**
     * redirects the user to the list he came from
     * 
     * @return String nav rule for loading the page the user came from
     */

      public String sendEMail() 
    {        
            logger.debug(">>>  sendEMail");
            String status = "not sent";
            String smtpHost = this.getSessionBean().getEmailServernameProp();
            String usr = this.getSessionBean().getEmailAuthUserProp();
            String pwd = this.getSessionBean().getEmailAuthPwdProp();
            String senderAddress =this.getSessionBean().getEmailSenderProp();//someone@web.de
            String subject = this.getSessionBean().getExportEmailSubject();
            String text = this.getSessionBean().getExportEmailTxt();
            String[] replyToAddresses = new String[] {this.getSessionBean().getExportEmailReplyToAddr()};
            String[] attachments = new String[]{this.getSessionBean().getAttExportFile().getPath()};
            String recipientsAddressesStr = this.getSessionBean().getEmailRecipients();
            
             if ( recipientsAddressesStr == null || recipientsAddressesStr.trim().equals("") )
            {
               String message = bundleMessage.getString(ExportItems.MESSAGE_EXPORT_EMAIL_NOTSENT);
               info(message);
               valMessage.setVisible(true);
                return null;
            }
           String[] recipientsAddresses = recipientsAddressesStr.split(","); //somereceiver@web.de
            
            try { 
                 status =  this.getItemControllerSessionBean().sendEmail(smtpHost, usr, pwd,
                                                          senderAddress, recipientsAddresses, replyToAddresses, 
                                                          subject, text, attachments);
            }catch (TechnicalException e)
            {
                logger.error("Could not ser the export formats." + "\n" + e.toString());
                ((ErrorPage)this.getBean(ErrorPage.BEAN_NAME)).setException(e);
                return ErrorPage.LOAD_ERRORPAGE;
            }
            
            if (status.equals("sent")){
                logger.debug(ExportItems.MESSAGE_EXPORT_EMAIL_SENT);
                SearchResultList searchResultList = (SearchResultList)getBean(SearchResultList.BEAN_NAME);                
                searchResultList.showMessage(ExportItems.MESSAGE_EXPORT_EMAIL_SENT);
                
                return (SearchResultList.LOAD_SEARCHRESULTLIST);
               
            } 
            return status;
      }
       
}
