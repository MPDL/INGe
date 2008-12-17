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

import java.io.IOException;
import java.util.List;

import javax.faces.component.html.HtmlMessages;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.RightsManagementSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.breadcrumb.BreadcrumbItemHistorySessionBean;
import de.mpg.escidoc.pubman.search.SearchResultList;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO;
import de.mpg.escidoc.services.common.valueobjects.FileFormatVO;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO.FormatType;


/**
  * Fragment class for item exporting.
  * This class provides all functionality for exporting items according the selected export format 
  * (layout or structured) and the selected file format (PDF, TXT, etc..).  
  * @author:  Galina Stancheva, created 02.08.2007
  * @version: $Revision$ $LastChangedDate$
  *  Revised by StG: 28.09.2007
*/
public class ExportItems extends FacesBean
{
    private static Logger logger = Logger.getLogger(ExportItems.class);
    
    public static final String BEAN_NAME = "ExportItems";
 
    // constant for the function export to check the rights and/or if the function has to be disabled (DiT)
    private final String FUNCTION_EXPORT = "export";

    // binded components in JSP
    private HtmlMessages valMessage = new HtmlMessages();
    //private HtmlSelectOneMenu cboLayoutCitStyles = new HtmlSelectOneMenu();

    // constants for comboBoxes and HtmlSelectOneRadios
    public SelectItem EXPORTFORMAT_ENDNOTE = new SelectItem("ENDNOTE", getLabel("Export_ExportFormat_ENDNOTE"));
    public SelectItem EXPORTFORMAT_BIBTEX = new SelectItem("BIBTEX", getLabel("Export_ExportFormat_BIBTEX"));
    public SelectItem EXPORTFORMAT_APA = new SelectItem("APA", getLabel("Export_ExportFormat_APA"));
    public SelectItem EXPORTFORMAT_AJP = new SelectItem("AJP", getLabel("Export_ExportFormat_AJP"));
//    public SelectItemGroup CITATIONSTYLES_GROUP = new SelectItemGroup(getLabel("Export_CitationStyles_Group"), "", false, new SelectItem[]{EXPORTFORMAT_APA, EXPORTFORMAT_AJP});
//    public SelectItem[] EXPORTFORMAT_OPTIONS = new SelectItem[]{EXPORTFORMAT_ENDNOTE, EXPORTFORMAT_BIBTEX, CITATIONSTYLES_GROUP};
    public SelectItem[] EXPORTFORMAT_OPTIONS = new SelectItem[]{EXPORTFORMAT_ENDNOTE, EXPORTFORMAT_BIBTEX, EXPORTFORMAT_APA, EXPORTFORMAT_AJP};
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
    public static final String MESSAGE_EXPORT_EMAIL_RECIPIENTS_ARE_NOT_DEFINED = "exportItems_RecipientsAreNotDefined";
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
        return (RightsManagementSessionBean)getSessionBean(RightsManagementSessionBean.class);
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
            ((ErrorPage)getSessionBean(ErrorPage.class)).setException(e);
        
            return ErrorPage.LOAD_ERRORPAGE;
        }
         return "OK";
    }
    

    
    public SelectItem[] getEXPORTFORMAT_OPTIONS()
    {
        return this.EXPORTFORMAT_OPTIONS;
    }
 
    public SelectItem[] getFILEFORMAT_OPTIONS()
    {
        return this.FILEFORMAT_OPTIONS;
    }


    /*
     * Gets the session bean. 
     */
    
    public ExportItemsSessionBean getSessionBean()
    {
        return (ExportItemsSessionBean)getSessionBean(ExportItemsSessionBean.class);
    }

    /**
     * Returns a reference to the scoped data bean (the ItemControllerSessionBean). 
     * @return a reference to the scoped data bean
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean)getSessionBean(ItemControllerSessionBean.class);
    }

        
    
    /*
     * Updates the GUI relatively the selected export format. 
     */
    public void updateExportFormats(){
        
        // get the selected export format by the FacesBean
    	

    	ExportItemsSessionBean sb = this.getSessionBean(); 
//        String selExportFormat = sb.getExportFormatType(); 
        String selExportFormat = sb.getExportFormatName(); 
        
        if (logger.isDebugEnabled())
        {
            logger.debug(">>>  New export format: " + selExportFormat);                     
            logger.debug("curExportFormat:" + this.getSessionBean().getCurExportFormatVO());
        }

        sb.setExportFormatName(selExportFormat);
        
        if ( "APA".equals(selExportFormat) || "AJP".equals(selExportFormat) )
        {
            //set default fileFormat for APA or AJP to pdf 
            String fileFormat = sb.getFileFormat();  
            if ( fileFormat != null || fileFormat.trim().equals("") || 
            		fileFormat.trim().equals(FileFormatVO.TEXT_NAME)
            	)
            	sb.setFileFormat(FileFormatVO.PDF_NAME); 
        }
        else
        {
        	//txt for all other
            sb.setFileFormat(FileFormatVO.TEXT_NAME);       
        }
        
    }

     ///////////////////////////////////////////////////////////////////////////////////////
    ///////// next methods are used by EMailing

    
    /*
     * Disables the export components when the email page gets open. 
     */
    public void disableExportPanComps(boolean b){
    
    }
        
        
    /**
     * redirects the user to the list he came from
     * 
     * @return String nav rule for loading the page the user came from
     */
    public String backToList()
    {
    	ExportItemsSessionBean sb = this.getSessionBean();
    	cleanUpEmailFields();	
    	return sb.getNavigationStringToGoBack() != null ? 
    		sb.getNavigationStringToGoBack() : 
    		SearchResultList.LOAD_SEARCHRESULTLIST;
    }
    
    /**
     * Clean up some fields on the Email interface
     */
    private void cleanUpEmailFields() 
    {
    	ExportItemsSessionBean sb = this.getSessionBean();
    	//To
        sb.setEmailRecipients(null);
        //CC
        sb.setEmailCCRecipients(null);
        //ReplyTo
        sb.setExportEmailReplyToAddr(null);
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
            String recipientsCCAddressesStr = this.getSessionBean().getEmailCCRecipients();
            
            String[] recipientsAddresses = null;
            boolean OK = false;
            if ( recipientsAddressesStr != null && ! recipientsAddressesStr.trim().equals("") )
            {
            	recipientsAddresses = recipientsAddressesStr.split(",");
                FOR: for ( String ra : recipientsAddresses )
                {
                	if ( !ra.trim().equals("") )
                	{
                		OK = true;
                		break FOR;
                	}
                }

            }
            
            if ( !OK )
            {
            	error(getMessage(ExportItems.MESSAGE_EXPORT_EMAIL_RECIPIENTS_ARE_NOT_DEFINED));
            	return null;
            }
           
           String[] recipientsCCAddresses = recipientsCCAddressesStr.split(",");
           
            try { 
                 status =  this.getItemControllerSessionBean().sendEmail(smtpHost, usr, pwd,
                		 senderAddress, 
                		 recipientsAddresses, 
                		 recipientsCCAddresses,
                		 null,
                		 replyToAddresses, 
                		 subject, text, attachments);
                 cleanUpEmailFields();
            }
            catch (TechnicalException e)
            {
                logger.error("Could not ser the export formats." + "\n" + e.toString());
                ((ErrorPage)getRequestBean(ErrorPage.class)).setException(e);
                return ErrorPage.LOAD_ERRORPAGE;
            }
            
            if (status.equals("sent")){
                logger.debug(ExportItems.MESSAGE_EXPORT_EMAIL_SENT);
                SearchResultList searchResultList = (SearchResultList)getSessionBean(SearchResultList.class);                
                info(getMessage(ExportItems.MESSAGE_EXPORT_EMAIL_SENT));
                
                //redirect to last breadcrumb
                BreadcrumbItemHistorySessionBean bhsb = (BreadcrumbItemHistorySessionBean)getSessionBean(BreadcrumbItemHistorySessionBean.class);
                try
                {
                    getFacesContext().getExternalContext().redirect(bhsb.getPreviousItem().getPage());
                }
                catch (IOException e)
                {
                   error("Could not redirect!");
                }
                return "";
               
            } 
            return status;
      }
       
}
