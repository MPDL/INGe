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

package de.mpg.escidoc.pubman.search;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemList;
import de.mpg.escidoc.pubman.ItemListSessionBean;
import de.mpg.escidoc.pubman.depositorWS.DepositorWS;
import de.mpg.escidoc.pubman.export.ExportItems;
import de.mpg.escidoc.pubman.export.ExportItemsSessionBean;
import de.mpg.escidoc.pubman.itemList.ui.ItemListUI;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubItemVOWrapper;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.pubman.PubItemSearching;
import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO;


/**
 * Fragment class for the SearchResultList.
 * This class provides all functionality for choosing and viewing one or more items out of a list of SearchResults.  
 *
 * @author:  Tobias Schraut; Thomas Diebäcker, Hugo Niedermaier, created 10.01.2007
 * @version: $Revision: 1695 $ $LastChangedDate: 2007-12-18 14:25:56 +0100 (Di, 18 Dez 2007) $
 * Revised by DiT: 14.08.2007
 */
public class SearchResultList extends ItemList
{
    public static final String BEAN_NAME = "SearchResultList";
    private static Logger logger = Logger.getLogger(SearchResultList.class);
    
    // Faces navigation string
    public final static String LOAD_SEARCHRESULTLIST = "showSearchResults";
    public final static String LOAD_NO_ITEMS_FOUND = "noItemsFound";
    public final static String LOAD_AFFILIATIONSEARCHRESULTLIST = "showAffiliationSearchResults";
    
    // binded components in JSP
    private HtmlCommandLink lnkEdit = new HtmlCommandLink();
    private HtmlCommandLink lnkView = new HtmlCommandLink();
    private HtmlCommandLink lnkWithdraw = new HtmlCommandLink();
    private HtmlCommandLink lnkSubmit = new HtmlCommandLink();
    private HtmlCommandLink lnkDelete = new HtmlCommandLink();
    private HtmlCommandLink lnkNewSubmission = new HtmlCommandLink();
    private HtmlOutputText valNoItemsFoundMsg = new HtmlOutputText();
    private HtmlOutputText valQuery = new HtmlOutputText();
    private HtmlCommandLink lnkAdvancedSearch = new HtmlCommandLink();
    private HtmlCommandLink lnkBrowse = new HtmlCommandLink();
    /** show the back link */
    private boolean showBackLink = true;
 
    //NiH: static flag workaround ui:tree component (exception on doubleclick selection) 
    public static boolean isInSearch = false;
 
    //StG: String used for displaying of export items 
    private String displayExportData = null;
    
    /**
     * Public constructor.
     */
    public SearchResultList()
    {
    	super();
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
            logger.debug("Initializing SearchResultList... " + this.toString());
        }

        // Perform initializations inherited from our superclass
        super.init();

        // create the itemList
        //this.createDynamicItemList();
        
        // update the language specific data container
        try 
        {
        	InitialContext initialContext = new InitialContext();
        	PubItemSearching pubItemSearching = (PubItemSearching)initialContext.lookup(PubItemSearching.SERVICE_NAME);
        	valQuery.setValue(getMessage("searchResultList_QueryString") + pubItemSearching.getCqlQuery());
        }
        catch (NamingException e)
        {
            logger.error("PubItemSearchingBean Initialization Failure: \n" + e);
        }
    }

    /**
     * Shows an item identified by the itemID in the parameters of the FacesContext.
     * This method is called when a user directly clicks on a link of an item.
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String showItem() 
    {
        return this.showItem(SearchResultList.LOAD_SEARCHRESULTLIST);
    }
     
    /**
     * View the selected items.
     * This method is called when the user selects one or more items and then clicks on the view-link in the DepositorWS. 
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String viewItem() 
    {
        return this.viewItem(SearchResultList.LOAD_SEARCHRESULTLIST);
    }

    /**
     * Used by displaying the selected items for export.
     * It is called when the user selects one or more items and then clicks on the Display-Link 
     * in the Export-Items Panel. 
     *  
     * @author: StG
     * @return the export data
     * @throws IOException
     * @throws Exception
     */
    /* public String getDisplayExportData() throws IOException, Exception{
         logger.debug(" getDisplayExportData ");

         FacesContext fc = FacesContext.getCurrentInstance();
         HttpServletRequest request = (HttpServletRequest)fc.getExternalContext().getRequest();
         String appURL = "http://" + request.getLocalName() + ":" + request.getLocalPort() + request.getContextPath()
         + "/faces/DisplayExportItemsPage.jsp";
         logger.debug("URL to display export page: "+appURL );               
           
         String ret = "window.open('" +
          appURL
          + "','Title mynine','width=1024,height=768,top=0,left=0,resizable=yes,status=yes')"; 

         return ret;
   }*/
 
    
    
    /**
     * Returns the navigation string for loading the DisplayExportItemsPage.jsp .
     * @author:  StG
     */
     public String showDisplayExportData() 
    {
        //HtmlCommandButton result = (HtmlCommandButton)event.getSource();
        logger.debug("showDisplayExportData");
        
        this.displayExportData = getMessage(ExportItems.MESSAGE_NO_ITEM_FOREXPORT_SELECTED);
        
        ExportItemsSessionBean sb = (ExportItemsSessionBean)getBean(ExportItemsSessionBean.class);
        //after the sepcificiation the file format of the displayed export data has to be html
        sb.setFileFormat("html");
 
        // set the currently selected items in the FacesBean
        //this.setSelectedItemsAndCurrentItem();
        if (this.getItemListSessionBean().getSelectedPubItems().size() != 0)
        {
            // export format and file format.
            ExportFormatVO curExportFormat = sb.getCurExportFormatVO();                        
           
            byte[] exportDataStream ;
            try 
            {
                exportDataStream = this.getItemControllerSessionBean().retrieveExportData(curExportFormat, CommonUtils.convertToPubItemVOList(this.getItemListSessionBean().getSelectedPubItems()));
            } catch(TechnicalException e)
            {
                logger.error("Could not get export data." + "\n" + e.toString());
                ((ErrorPage)this.getBean(ErrorPage.class)).setException(e);
            
                return ErrorPage.LOAD_ERRORPAGE;
            }
            
            if (curExportFormat.getFormatType() ==  ExportFormatVO.FormatType.LAYOUT){
            
                this.displayExportData = new String(exportDataStream);            

            } else {
                this.displayExportData = new String(exportDataStream);   
                this.displayExportData = HTMLEntityEncode(this.displayExportData);
                this.displayExportData = "<html><head><title>Export Data</title></head><body scroll=no bgcolor=#FFFFFC><br/><p style=font-family:verdana,arial;font-size:12px><pre>"
                + "<table><tr>"
                + this.displayExportData
                + "</tr></table>"
                + "</pre></p><p style=font-family:verdana,arial;font-size:12px>"
                + "</p></body></html>";
            }
            logger.debug("prepareDisplayExportData set FULL data to session bean ");
             
            sb.setExportDisplayData(this.displayExportData);
            return "showDisplayExportItemsPage";
        }
        else
        {            
            logger.warn("No item selected.");
            this.showMessage(ExportItems.MESSAGE_NO_ITEM_FOREXPORT_SELECTED);
            sb.setExportDisplayData(this.displayExportData);
            return "";
        }
   }

    /*
     * Delivers HTML encoded String form the input String.
     * @author:  StG
     */
    public static String HTMLEntityEncode( String s )
    {
        StringBuffer buf = new StringBuffer();
        for ( int i = 0; i < s.length(); i++ )
        {
            char c = s.charAt( i );
            if ( c>='a' && c<='z' || c>='A' && c<='Z' || c>='0' && c<='9' )
            {
                buf.append( c );
            }
            else
            {
                buf.append( "&#" + (int)c + ";" );
            }
        }
        return buf.toString();
    }

    
    /**
     * Invokes the email service to send per email the the page with the selected items as attachment.
     * This method is called when the user selects one or more items and then clicks on the EMail-Button 
     * in the Export-Items Panel. 
     * @author:  StG
     */
     public String showExportEmailPage()
    {
            if (logger.isDebugEnabled())
            {
                logger.debug("showExportEmailPage");
            }
            //this.setSelectedItemsAndCurrentItem();
            ExportItemsSessionBean sb = (ExportItemsSessionBean) getSessionBean(ExportItemsSessionBean.class);
            
            if (this.getItemListSessionBean().getSelectedPubItems().size() != 0)
            {
                // gets the export format VO that holds the data.
                ExportFormatVO curExportFormat = sb.getCurExportFormatVO();
                byte[] exportFileData;
                try
                {
                    exportFileData = this.getItemControllerSessionBean().retrieveExportData(curExportFormat, CommonUtils.convertToPubItemVOList(this.getItemListSessionBean().getSelectedPubItems()));
                } catch (TechnicalException e)
                {
                    logger.error("Errors retrieving export data.", e);
                    ((ErrorPage) getRequestBean(ErrorPage.class)).setException(e);
                
                    return ErrorPage.LOAD_ERRORPAGE;
                }
                
                if ( (exportFileData == null) || (new String(exportFileData)).trim().equals("") ){ 
                    logger.debug("No export data was delivered!");
                    this.showMessage(ExportItems.MESSAGE_NO_EXPORTDATA_DELIVERED);
                    return "";
                }
                
                //YEAR + MONTH + DAY_OF_MONTH
                Calendar rightNow = Calendar.getInstance();
                String date = rightNow.get(Calendar.YEAR)+"-"+rightNow.get(Calendar.DAY_OF_MONTH)+"-"+rightNow.get(Calendar.MONTH)+"_";
                //create an attachment temp file from the byte[] stream
                File exportAttFile;
                try
                {
                    exportAttFile = File.createTempFile(
                    		"eSciDoc_Export_" 
                    		+ curExportFormat.getName()
                    		+ "_"
                    		+ date,
                    		
                    		"."
                    		+ curExportFormat.getSelectedFileFormat().getName()
                    );                
                    FileOutputStream fos = new FileOutputStream(exportAttFile);
                    fos.write(exportFileData);
                    fos.close();
                }
                catch (IOException e1)
                {
                    logger.debug("IO Error by writing the export data: " + e1.toString());
                    ((ErrorPage) getRequestBean(ErrorPage.class)).setException(e1);
                    
                    return ErrorPage.LOAD_ERRORPAGE;
                }
    
                sb.setExportEmailTxt(getMessage(ExportItems.MESSAGE_EXPORT_EMAIL_TEXT));
                sb.setAttExportFileName(exportAttFile.getName());
                sb.setAttExportFile(exportAttFile);
                sb.setExportEmailSubject(getMessage(ExportItems.MESSAGE_EXPORT_EMAIL_SUBJECT_TEXT) +
                		": " + exportAttFile.getName());
                
                //hier call set the values on the exportEmailView - attachment file, subject, .... 
                return "displayExportEmailPage";
            } else
            {            
                logger.warn("No item selected.");
                this.showMessage(ExportItems.MESSAGE_NO_ITEM_FOREXPORT_SELECTED);
                return "";
            }          
    }
 
     
    /**
     * Downloads the page with the selected items as export.
     * This method is called when the user selects one or more items and then clicks on the Download-Button 
     * in the Export-Items Panel. 
     * @author:  StG
      */
        public String downloadExportFile(ActionEvent event)
    {
            if (logger.isDebugEnabled())
            {
                logger.debug("downloadExportFile");
            }
 
          // set the currently selected items in the FacesBean
         //this.setSelectedItemsAndCurrentItem();
         ExportItemsSessionBean sb = (ExportItemsSessionBean)getBean(ExportItemsSessionBean.class);
         
         if (this.getItemListSessionBean().getSelectedPubItems().size() != 0)
         {
            // export format and file format.
            ExportFormatVO curExportFormat = sb.getCurExportFormatVO();  
            byte[] exportFileData; 
            try
            {
                exportFileData = this.getItemControllerSessionBean().retrieveExportData(curExportFormat, CommonUtils.convertToPubItemVOList(this.getItemListSessionBean().getSelectedPubItems()));
            } catch (TechnicalException e)
            {
                logger.error("Errors retrieving export data.", e);
                ((ErrorPage)this.getBean(ErrorPage.class)).setException(e);
            
                return ErrorPage.LOAD_ERRORPAGE;
            }
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse)facesContext.getExternalContext().getResponse();
            String contentType = curExportFormat.getSelectedFileFormat().getMimeType();
            
            //ToDo MaV: this dependency between file format and content type
            // should happen automatically using ExportFormatVO and ExportItemsSessionBean. 
            // This can be realised only when the explainStyles() returns the file formats with 
            // their corresponding content types in the outpu xml. So  this should be done FIRST!
            /*if (curExportFormat.getSelectedFileFormat().equals("pdf"))
                {contentType = "application/pdf";}
            else if (curExportFormat.getSelectedFileFormat().equals("rtf"))
                {contentType = "text/rtf";}
            else if (curExportFormat.getSelectedFileFormat().equals("html"))
                {contentType = "text/html";}
            else if (curExportFormat.getSelectedFileFormat().equals("odt"))
                {contentType = "application/vnd.oasis.opendocument.text";}*/
            response.setContentType(contentType);
            
             try
            {
               response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode("ExportFile", "UTF-8"));
               OutputStream out = response.getOutputStream();               
               out.write(exportFileData);
               out.flush();
               facesContext.responseComplete();
               out.close();
            }
            catch (IOException e1)
            {
                logger.debug("IO Error by writing the export data: " + e1.toString());
                ((ErrorPage)this.getBean(ErrorPage.class)).setException(e1);
                
                return ErrorPage.LOAD_ERRORPAGE;
            }
        } else
        {            
            logger.warn("No item selected.");
            this.showMessage(ExportItems.MESSAGE_NO_ITEM_FOREXPORT_SELECTED);
        }
         return "OK";
    }

     
    /**
     * Withdraws the selected item.
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String withdrawSelectedItem() 
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Withdraw selected item");
        }

        // set the currently selected items in the ItemController
        //this.setSelectedItemsAndCurrentItem();
        
        if (this.getItemListSessionBean().getSelectedPubItems().size() == 1)
        {
            return withdrawItem(SearchResultList.LOAD_SEARCHRESULTLIST);
        }
        else if (this.getItemListSessionBean().getSelectedPubItems().size() > 1)
        {
            this.showMessage(DepositorWS.MESSAGE_MANY_ITEMS_SELECTED);
            return null;
        }
        else
        {
            this.showMessage(DepositorWS.MESSAGE_NO_ITEM_SELECTED);
            return null;
        }
    }

    /**
     * Creates the panel newly according to the values in the itemArray.
     */
    protected void createDynamicItemList2()
    {

        List<PubItemVO> list = CommonUtils.convertToPubItemVOList(this.getItemListSessionBean().getCurrentPubItemList());
        
        if(this.getItemListSessionBean().getCurrentPubItemList() != null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Creating dynamic item list with " + this.getItemListSessionBean().getCurrentPubItemList().size() + " entries.");
            }
            
            // create an ItemListUI for all PubItems
            List<PubItemVO> pubItemList = CommonUtils.convertToPubItemVOList(this.getItemListSessionBean().getCurrentPubItemList());
            List<PubItemVOWrapper> pubItemWrapperList = CommonUtils.convertToWrapperList(pubItemList);
            ItemListUI itemListUI = new ItemListUI(pubItemWrapperList, "#{SearchResultList.showItem}");

        }

        // enable or disable the action links according to item state and availability of items
        this.enableLinks(this.getItemListSessionBean().getCurrentPubItemList().size());
    }

    /**
     * Enables or disables the action links according to availability of items.
     * @param itemList the list displayed
     */
    protected void enableLinks(int itemListSize)
    {
        boolean enableView = (itemListSize > 0); 
        boolean enableNoItemMsg = (itemListSize <= 0);

        this.lnkView.setRendered(enableView);
        this.lnkWithdraw.setRendered(enableView);
        this.valNoItemsFoundMsg.setRendered(enableNoItemMsg);
        
        LoginHelper loginHelper = (LoginHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), "LoginHelper");

        // FrM: enable or disable the action links according to the login state
        if (loginHelper.getESciDocUserHandle() != null)
        {
            this.lnkWithdraw.setRendered(enableView);
        } 
        else 
        {
            this.lnkWithdraw.setRendered(false);
        }
    }
    
    /**
     * Evaluates the fields for searchString and includeFiles and calls the search method in SearchResultList. 
     * @return string, identifying the page that should be navigated to after this methodcall
     */    
    public String startSearch()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Starting a new search...");
        }
  
        lnkAdvancedSearch.setRendered(false);
        lnkBrowse.setRendered(false);
        valQuery.setRendered(false);
        this.showBackInNoResultPage( false );
        // reset some error message from last request
        this.deleteMessage();

        String searchString = this.getSessionBean().getSearchString();
        boolean includeFiles = this.getSessionBean().getIncludeFiles();
        
        try
        {
            List<PubItemVO> itemsFound = this.getItemControllerSessionBean().searchItems(searchString, includeFiles);
            this.getItemListSessionBean().setCurrentPubItemList(CommonUtils.convertToPubItemVOPresentationList(itemsFound));
            
            getItemListSessionBean().setListDirty(false);
            getItemListSessionBean().setType("SearchResultList");
            getItemListSessionBean().setCurrentPubItemListPointer(0);
        }
        catch (Exception e)
        {
            logger.error("Could not search for items.", e);
            ((ErrorPage)this.getBean(ErrorPage.class)).setException(e);
            
            return ErrorPage.LOAD_ERRORPAGE;
        }
        
        // sort the items and force the UI to update
        this.sortItemList();
        this.createDynamicItemList2();
        
        if(this.getItemListSessionBean().getCurrentPubItemList().size() < 1)
        {
            return (SearchResultList.LOAD_NO_ITEMS_FOUND);
        }
        
        getViewItemSessionBean().setNavigationStringToGoBack(SearchResultList.LOAD_SEARCHRESULTLIST);
        return (SearchResultList.LOAD_SEARCHRESULTLIST);
    }
    
    /**
     * Calls the advanced search method in ItemControllerSessionBean.
     * @author Hugo Niedermaier 
     * @return string, identifying the page that should be navigated to after this methodcall
     */    
    public String startAdvancedSearch(ArrayList<CriterionVO> criterionVOList, String language)
    {
        int result;
        if (logger.isDebugEnabled())
        {
            logger.debug("Starting a new advancedSearch...");
        }
        
        lnkAdvancedSearch.setRendered(true);
        lnkBrowse.setRendered(false);
        valQuery.setRendered(true);
        this.showBackInNoResultPage( true );
//      reset some error message from last request
        this.deleteMessage();

        try
        {
            ArrayList<PubItemVO> itemsFound = this.getItemControllerSessionBean().advancedSearchItems(criterionVOList, language);
            result = itemsFound.size();
            getItemListSessionBean().setListDirty(false);
            getItemListSessionBean().setType("AdvancedSearchResultList");
            getItemListSessionBean().setCurrentPubItemListPointer(0);
            getItemListSessionBean().setCurrentPubItemList(CommonUtils.convertToPubItemVOPresentationList(itemsFound));
        }
        catch (Exception e)
        {
            logger.error("Could not search for items." + "\n" + e.toString());
            ((ErrorPage) getRequestBean(ErrorPage.class)).setException(e);
            
            return ErrorPage.LOAD_ERRORPAGE;
        }
        
        // sort the items and force the UI to update
        this.sortItemList();
        //this.createDynamicItemList2();
        
        try
        {
            InitialContext initialContext = new InitialContext();
            PubItemSearching pubItemSearching = (PubItemSearching)initialContext.lookup(PubItemSearching.SERVICE_NAME);
            valQuery.setValue(getMessage("searchResultList_QueryString") + pubItemSearching.getCqlQuery());
            if (result > 0)
            {
            	getViewItemSessionBean().setNavigationStringToGoBack(SearchResultList.LOAD_SEARCHRESULTLIST);
                return (SearchResultList.LOAD_SEARCHRESULTLIST);
            }
            else
            {
                return (SearchResultList.LOAD_NO_ITEMS_FOUND);            
            }
        }
        catch (NamingException e)
        {
            logger.error("PubItemSearchingBean Initialization Failure: \n" + e);
            return (SearchResultList.LOAD_NO_ITEMS_FOUND);
        }
    }
    
    /**
     * Searches Items by Affiliation 
     * @return string, identifying the page that should be navigated to after this methodcall
     */    
    public String startSearchForAffiliation(AffiliationRO affiliationRO)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Starting a new Search for Affiliation...");
        }
        
        lnkBrowse.setRendered(true);
        lnkAdvancedSearch.setRendered(false);
        valQuery.setRendered(false);
        this.showBackInNoResultPage( true );
//      reset some error message from last request
        this.deleteMessage();
        
        
        ArrayList<PubItemVO> itemsFound = null;
        try
        {
            itemsFound = this.getItemControllerSessionBean().searchItemsByAffiliation(affiliationRO);
            
            getItemListSessionBean().setListDirty(false);
            getItemListSessionBean().setType("AffiliationSearchResultList");
            getItemListSessionBean().setCurrentPubItemListPointer(0);
            this.getItemListSessionBean().setCurrentPubItemList(CommonUtils.convertToPubItemVOPresentationList(itemsFound));
        }
        catch (Exception e)
        {
            logger.error("Could not search for items." + "\n" + e.toString());
            ((ErrorPage)this.getBean(ErrorPage.class)).setException(e);
            
            return ErrorPage.LOAD_ERRORPAGE;
        }
     
        // if no items could be found, display the NoItemsFoundPage
        if( itemsFound == null || itemsFound.size() == 0 ) 
        {
        	return (SearchResultList.LOAD_NO_ITEMS_FOUND); 
        }
        
        // sort the items and force the UI to update
        this.sortItemList();
        //this.createDynamicItemList2();
        
        //NiH: static flag workaround ui:tree component (exception on doubleclick selection)
        isInSearch = false;
        
        getViewItemSessionBean().setNavigationStringToGoBack(SearchResultList.LOAD_SEARCHRESULTLIST);
        return (SearchResultList.LOAD_AFFILIATIONSEARCHRESULTLIST);
    }
    
    /**
     * Prepares the file the user wants to download
     * @author Tobias Schraut
     * @throws IOException
     * @throws Exception
     */
    public void downloadFile(int itemPosition, int filePosition) throws IOException, Exception
    {
        LoginHelper loginHelper = (LoginHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), "LoginHelper");

        // extract the location of the file
        String fileLocation = ServiceLocator.getFrameworkUrl() + this.getItemListSessionBean().getCurrentPubItemList().get(itemPosition).getFiles().get(filePosition).getContent();
        String filename = this.getItemListSessionBean().getCurrentPubItemList().get(itemPosition).getFiles().get(filePosition).getName(); // Filename suggested in browser Save As dialog
        filename = filename.replace(" ", "_"); // replace empty spaces because they cannot be procesed by the http-response (filename will be cutted after the first empty space)
        String contentType = this.getItemListSessionBean().getCurrentPubItemList().get(itemPosition).getFiles().get(filePosition).getMimeType(); // For dialog, try
        
        // application/x-download
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse)facesContext.getExternalContext().getResponse();
        response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
        response.setContentLength(new Long(this.getItemListSessionBean().getCurrentPubItemList().get(itemPosition).getFiles().get(filePosition).getSize()).intValue());
        response.setContentType(contentType);

        byte[] buffer = null;
        if (filePosition != -1)
        {
            try
            {
                GetMethod method = new GetMethod(fileLocation);
                method.setFollowRedirects(false);
                if (loginHelper.getESciDocUserHandle() != null)
                {
                    // downloading by account user
                    addHandleToMethod(method, loginHelper.getESciDocUserHandle());
                }
                
                // Execute the method with HttpClient.
                HttpClient client = new HttpClient();
                client.executeMethod(method);
                OutputStream out = response.getOutputStream();
                InputStream input = method.getResponseBodyAsStream();
                try
                {
                    buffer = new byte[new Long(this.getItemListSessionBean().getCurrentPubItemList().get(itemPosition).getFiles().get(filePosition).getSize()).intValue()];
                    int numRead;
                    long numWritten = 0;
                    while ((numRead = input.read(buffer)) != -1) {
                        out.write(buffer, 0, numRead);
                        out.flush();
                        numWritten += numRead;
                    }
                    facesContext.responseComplete();
                }
                catch (IOException e1)
                {
                    logger.debug("Download IO Error: " + e1.toString());
                }
                input.close();
                out.close();
            }
            catch (FileNotFoundException e)
            {
                logger.debug("File not found: " + e.toString());
            }
        }
    }

    /**
     * Method is called in jsp. Triggers download action
     * @author Tobias Schraut
     * @return String nav rule
     * @throws Exception
     */
    public String handleDownloadAction(ActionEvent event) throws Exception
    {
        //find the index of the the button the user has clicked
        HtmlOutputText result = (HtmlOutputText)((HtmlCommandButton)event.getSource()).getParent();
        int indexButton = result.getChildren().indexOf(event.getSource());
        
        // then find the indexes of the item and the file the clicked button belongs to
        int indexFile = new Integer(((HtmlOutputText)result.getChildren().get(indexButton-1)).getValue().toString());
        int indexItem = new Integer(((HtmlOutputText)result.getChildren().get(indexButton-2)).getValue().toString());
        try
        {
            this.downloadFile(indexItem, indexFile);
        }
        catch (IOException e)
        {
            logger.debug("File Download Error: " + e.toString());
        }
        
        return "";
    }
    
    /**
     * Adds a cookie named "escidocCookie" that holds the eScidoc user handle to the provided http method object.
     * @author Tobias Schraut
     * @param method The http method to add the cookie to.
     */
    private void addHandleToMethod(final HttpMethod method, String eSciDocUserHandle)
    {
        // Staging file resource is protected, access needs authentication and
        // authorization. Therefore, the eSciDoc user handle must be provided.
        // Put the handle in the cookie "escidocCookie"
        method.setRequestHeader("Cookie", "escidocCookie=" + eSciDocUserHandle);
    }
    
    /**
     * Show the back link in the NoResultPage or not.
     * @param show true means show link, false don't show
     */
    private void showBackInNoResultPage( boolean show )
    {
    	this.showBackLink = show;
    }

    /**
     * Returns the ItemListSessionBean.
     * 
     * @return a reference to the scoped data bean (ItemListSessionBean)
     */
    protected ItemListSessionBean getItemListSessionBean()
    {
        return (ItemListSessionBean)getSessionBean(ItemListSessionBean.class);
    }

    /**
     * Returns the SearchResultListSessionBean.
     * 
     * @return a reference to the scoped data bean (SearchResultListSessionBean)
     */
    protected SearchResultListSessionBean getSessionBean()
    {
        return (SearchResultListSessionBean)getSessionBean(SearchResultListSessionBean.class);
    }

    public HtmlOutputText getValNoItemsFoundMsg()
    {
        return valNoItemsFoundMsg;
    }

    public void setValNoItemsFoundMsg(HtmlOutputText valNoItemsFoundMsg)
    {
        this.valNoItemsFoundMsg = valNoItemsFoundMsg;
    }

    public HtmlCommandLink getLnkDelete()
    {
        return lnkDelete;
    }

    public void setLnkDelete(HtmlCommandLink lnkDelete)
    {
        this.lnkDelete = lnkDelete;
    }

    public HtmlCommandLink getLnkEdit()
    {
        return lnkEdit;
    }

    public void setLnkEdit(HtmlCommandLink lnkEdit)
    {
        this.lnkEdit = lnkEdit;
    }

    public HtmlCommandLink getLnkNewSubmission()
    {
        return lnkNewSubmission;
    }

    public void setLnkNewSubmission(HtmlCommandLink lnkNewSubmission)
    {
        this.lnkNewSubmission = lnkNewSubmission;
    }

    public HtmlCommandLink getLnkSubmit()
    {
        return lnkSubmit;
    }

    public void setLnkSubmit(HtmlCommandLink lnkSubmit)
    {
        this.lnkSubmit = lnkSubmit;
    }

    public HtmlCommandLink getLnkView()
    {
        return lnkView;
    }

    public void setLnkView(HtmlCommandLink lnkView)
    {
        this.lnkView = lnkView;
    }

    public HtmlCommandLink getLnkWithdraw() {
        return lnkWithdraw;
    }

    public void setLnkWithdraw(HtmlCommandLink lnkWithdraw) {
        this.lnkWithdraw = lnkWithdraw;
    }

    public HtmlOutputText getValQuery()
    {
        return valQuery;
    }

    public void setValQuery(HtmlOutputText valQuery)
    {
        this.valQuery = valQuery;
    }

    public HtmlCommandLink getLnkAdvancedSearch()
    {
        return lnkAdvancedSearch;
    }

    public void setLnkAdvancedSearch(HtmlCommandLink lnkAdvancedSearch)
    {
        this.lnkAdvancedSearch = lnkAdvancedSearch;
    }

    public HtmlCommandLink getLnkBrowse()
    {
        return lnkBrowse;
    }

    public void setLnkBrowse(HtmlCommandLink lnkBrowse)
    {
        this.lnkBrowse = lnkBrowse;
    }

	public boolean isShowBackLink() {
		return showBackLink;
	}
}
