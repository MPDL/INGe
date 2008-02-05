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
import javax.faces.component.html.HtmlMessages;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.component.html.HtmlSelectOneRadio;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.RightsManagementSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.search.SearchResultList;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO;
import de.mpg.escidoc.services.common.valueobjects.FileFormatVO;


/**
  * Fragment class for item exporting.
  * This class provides all functionality for exporting items according the selected export format 
  * (layout or structured) and the selected file format (PDF, TXT, etc..).  
  * @author:  Galina Stancheva, created 02.08.2007
  * @version: $Revision:  $ $LastChangedDate:  $
  *  Revised by StG: 28.09.2007
*/
public class ExportItems extends FacesBean
{
    private static Logger logger = Logger.getLogger(ExportItems.class);
 
    // constant for the function export to check the rights and/or if the function has to be disabled (DiT)
    private final String FUNCTION_EXPORT = "export";

    // binded components in JSP
    private HtmlMessages valMessage = new HtmlMessages();
    private HtmlSelectOneMenu cboLayoutCitStyles = new HtmlSelectOneMenu();
    private HtmlSelectOneRadio rbgExportFormats = new HtmlSelectOneRadio();
    private HtmlSelectOneRadio rbgFileFormats = new HtmlSelectOneRadio();
    private HtmlCommandButton btnDisplayItems = new HtmlCommandButton();
    private HtmlCommandButton btnExportDownload = new HtmlCommandButton();
    private HtmlCommandButton btnExportEMail = new HtmlCommandButton();

    // constants for comboBoxes and HtmlSelectOneRadios
    public SelectItem EXPORTFORMAT_STRUCTURED = new SelectItem("STRUCTURED", getLabel("Export_ExportFormat_STRUCTURED"));
    public SelectItem EXPORTFORMAT_LAYOUT = new SelectItem("LAYOUT", getLabel("Export_ExportFormat_LAYOUT"));
    public SelectItem[] EXPORTFORMAT_OPTIONS = new SelectItem[]{EXPORTFORMAT_STRUCTURED, EXPORTFORMAT_LAYOUT};
    public SelectItem LAYOUTCITATIONSTYLE_APA = new SelectItem("APA", getLabel("Export_LayoutCitationStyle_APA"));
    public SelectItem[] LAYOUTCITATIONSTYLE_OPTIONS = new SelectItem[]{LAYOUTCITATIONSTYLE_APA};
    public SelectItem FILEFORMAT_PDF = new SelectItem("pdf", getLabel("Export_FileFormat_PDF"));
    public SelectItem FILEFORMAT_ODT = new SelectItem("odt", getLabel("Export_FileFormat_ODT"));
    public SelectItem FILEFORMAT_RTF = new SelectItem("rtf", getLabel("Export_FileFormat_RTF"));
    public SelectItem FILEFORMAT_HTML = new SelectItem("html", getLabel("Export_FileFormat_HTML"));
    public SelectItem[] FILEFORMAT_OPTIONS = new SelectItem[]{FILEFORMAT_PDF, FILEFORMAT_ODT, FILEFORMAT_RTF, FILEFORMAT_HTML};
 
    // constants for error and status messages
    public static final String MESSAGE_NO_ITEM_FOREXPORT_SELECTED = "exportItems_NoItemSelected";
    // constants for error and status messages
    public static final String MESSAGE_NO_EXPORTDATA_DELIVERED = "exportItems_NoDataDelivered";
    public static final String MESSAGE_EXPORT_EMAIL_SENT = "exportItems_EmailSent";
    public static final String MESSAGE_EXPORT_EMAIL_NOTSENT = "exportItems_EmailNotSent";
    public static final String MESSAGE_EXPORT_EMAIL_TEXT = "exportItems_EmailText";
    public static final String MESSAGE_EXPORT_EMAIL_SUBJECT_TEXT = "exportItems_EmailSubjectText";
    
    /**
     * Default constructor.
     */
    public ExportItems()
    {
        this.init();
    }
    
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
        return (RightsManagementSessionBean)getBean(RightsManagementSessionBean.class);
    }

    /**
     * Returns true if the AdvancedSearch should be disabled by the escidoc properties file.
     * @author StG
     * @return
     */
    public boolean getVisibleExport()
    {
        return !this.getRightsManagementSessionBean().isDisabled(getRightsManagementSessionBean().PROPERTY_PREFIX_FOR_DISABLEING_FUNCTIONS + "." + this.FUNCTION_EXPORT);
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
            logger.error("Could not ser the export formats." + "\n" + e.toString(), e);
            ((ErrorPage)getRequestBean(ErrorPage.class)).setException(e);
        
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
               SelectItem tempOpt = new SelectItem(layoutStyleName.toUpperCase());   
                       
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
                        
                        tempOpt = new SelectItem(ff);
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
        
    public HtmlSelectOneMenu getCboLayoutCitStyles()
    {
        return cboLayoutCitStyles;
    }

    public void setCboLayoutCitStyles(HtmlSelectOneMenu cboLayoutCitStyles)
    {
        this.cboLayoutCitStyles = cboLayoutCitStyles;
    }

    public HtmlSelectOneRadio getRbgExportFormats()
    {
        return rbgExportFormats;
    }

    public void setRbgExportFormats(HtmlSelectOneRadio rbgExportFormats)
    {
        this.rbgExportFormats = rbgExportFormats;
    }

    public HtmlSelectOneRadio getRbgFileFormats()
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
   
     public void setRbgFileFormats(HtmlSelectOneRadio rbgFileFormats)
    {
        this.rbgFileFormats = rbgFileFormats;
    }
    
    public SelectItem[] getEXPORTFORMAT_OPTIONS()
    {
        return this.EXPORTFORMAT_OPTIONS;
    }
 
    public SelectItem[] getFILEFORMAT_OPTIONS()
    {
        return this.FILEFORMAT_OPTIONS;
    }

    public SelectItem[] getLAYOUTCITATIONSTYLE_OPTIONS()
    {
        return this.LAYOUTCITATIONSTYLE_OPTIONS;
    }    


    /*
     * Gets the session bean. 
     */
    
    public ExportItemsSessionBean getSessionBean()
    {
        return (ExportItemsSessionBean)getBean(ExportItemsSessionBean.class);
    }

    /**
     * Returns a reference to the scoped data bean (the ItemControllerSessionBean). 
     * @return a reference to the scoped data bean
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean)getBean(ItemControllerSessionBean.class);
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
        
        // get the selected export format by the FacesBean
    	
    	
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
    public HtmlMessages getValMessage()
    {
        return valMessage;
    }

    public void setValMessage(HtmlMessages valMessage)
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
               String message = getMessage(ExportItems.MESSAGE_EXPORT_EMAIL_NOTSENT);
               info(message);
               valMessage.setRendered(true);
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
                ((ErrorPage)getBean(ErrorPage.class)).setException(e);
                return ErrorPage.LOAD_ERRORPAGE;
            }
            
            if (status.equals("sent")){
                logger.debug(ExportItems.MESSAGE_EXPORT_EMAIL_SENT);
                SearchResultList searchResultList = (SearchResultList)getBean(SearchResultList.class);                
                searchResultList.showMessage(ExportItems.MESSAGE_EXPORT_EMAIL_SENT);
                
                return (SearchResultList.LOAD_SEARCHRESULTLIST);
               
            } 
            return status;
      }
       
}
