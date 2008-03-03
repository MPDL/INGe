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

package de.mpg.escidoc.pubman.viewItem.ui.viewItemFullComponents;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ResourceBundle;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.component.html.HtmlOutputLink;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.ui.ContainerPanelUI;
import de.mpg.escidoc.pubman.ui.HTMLElementUI;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.services.common.referenceobjects.PubFileRO;
import de.mpg.escidoc.services.common.valueobjects.PubItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.SearchHitVO.SearchHitType;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * UI for creating the files section of a pubitem to be used in the ViewItemMediumUI.
 * 
 * @author: Tobias Schraut, created 27.09.2007
 * @version: $Revision: 1646 $ $LastChangedDate: 2007-12-05 17:48:05 +0100 (Mi, 05 Dez 2007) $
 */
public class ViewItemFileUI extends ContainerPanelUI implements ActionListener
{
    private static Logger logger = Logger.getLogger(ViewItemFileUI.class);
    private PubItemVO pubItem;
    private HtmlGraphicImage image;
    private HtmlGraphicImage downloadImage;
    private HTMLElementUI htmlElement = new HTMLElementUI();
    private HtmlCommandButton btnDownload = new HtmlCommandButton();
    private HtmlOutputLink downloadLink = new HtmlOutputLink();
    private HtmlOutputText downloadText = new HtmlOutputText();
    private HtmlOutputText withdrawnTitle = new HtmlOutputText();

    public ViewItemFileUI()
    {

    }

    public Object processSaveState(FacesContext context) 
    {
        Object superState = super.processSaveState(context);
        return new Object[] {superState, new Integer(getChildCount())};
    }

    public void processRestoreState(FacesContext context, Object state) 
    {
        // At this point in time the tree has already been restored, but not before our ctor added the default children.
        // Since we saved the number of children in processSaveState, we know how many children should remain within
        // this component. We assume that the saved tree will have been restored 'behind' the children we put into it
        // from within the ctor.
        Object[] values = (Object[]) state;
        Integer savedChildCount = (Integer) values[1];
        for (int i = getChildCount() - savedChildCount.intValue(); i > 0; i--) 
        {
            getChildren().remove(0);
        }
        super.processRestoreState(context, values[0]);
    }

    /**
     * Public constructor.
     * @param pubItemVO a pubitem
     */
    public ViewItemFileUI(PubItemVO pubItemVO)
    {
        this.pubItem = pubItemVO;

        this.getChildren().clear();
        this.setId(CommonUtils.createUniqueId(this));
        
        ApplicationBean applicationBean = (ApplicationBean) FacesContext
        .getCurrentInstance()
        .getExternalContext()
        .getApplicationMap()
        .get(ApplicationBean.BEAN_NAME);

        // *** HEADER ***
        // add an image to the page
        this.getChildren().add(htmlElement.getStartTag("h2"));
        this.image = new HtmlGraphicImage();
        this.image.setId(CommonUtils.createUniqueId(this.image));
        this.image.setUrl("./images/upload_queue_icon.png");
        this.image.setWidth("21");
        this.image.setHeight("25");
        this.getChildren().add(this.image);
        
        
        // add the subheader
        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemMedium_lblSubHeaderFile")));
        this.getChildren().add(htmlElement.getEndTag("h2"));
        
        if(this.pubItem.getFiles() != null && this.pubItem.getFiles().size() > 0)
        {
            for(int i = 0; i < this.pubItem.getFiles().size(); i++)
            {
                //set up the download image
                this.downloadImage = new HtmlGraphicImage();
                this.downloadImage.setId(CommonUtils.createUniqueId(this.image));
                this.downloadImage.setUrl("./images/docu_tiny.png");
                
                //Hidden download button and other fields due to
                // jsf bug (download action cannot be called by
                // hyperlink)
                this.btnDownload = new HtmlCommandButton();
                this.btnDownload.setId("_btnDownload_" + new Integer(i).toString());
                
                this.btnDownload.setValue("Download...");
                /*this.btnDownload.setActionListener(application.createMethodBinding(
                        "#{ViewItemSessionBean.processAction}",
                        new Class[] { ActionEvent.class }));*/
                /*this.btnDownload.setAction(application.createMethodBinding(
                        "#{ViewItemSessionBean.handleDownloadAction}",
                        null));*/
                this.btnDownload.addActionListener(this);
                this.btnDownload.setImmediate(true);
                this.btnDownload.setImage("./images/docu_tiny.png");
                
                // Deactivate this button if the item is withdrawn
                if(this.pubItem.getState() != null && this.pubItem.getState().equals(PubItemVO.State.WITHDRAWN))
                {
                    this.btnDownload.setDisabled(true);
                }
                
                this.downloadText = new HtmlOutputText();
                this.downloadText.setId(CommonUtils.createUniqueId(this.downloadText));
                this.downloadText.setValue(this.pubItem.getFiles().get(i).getName());
                
                this.downloadLink = new HtmlOutputLink();
                this.downloadLink.setId(CommonUtils.createUniqueId(this.downloadLink));
                this.downloadLink.setOnclick("downloadFileViewItem('" + this.btnDownload.getId()
                        + "'); return false");
                this.downloadLink.getChildren().add(this.downloadText);
                
                
                // *** SEARCH HITS (IF POSSIBLE) ***
                if(this.pubItem instanceof PubItemResultVO && this.pubItem.getState() != null && !this.pubItem.getState().equals(PubItemVO.State.WITHDRAWN))
                {
                    PubItemResultVO resultItem = (PubItemResultVO)this.pubItem;
                    // label
                    this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
                    
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblFileFullTxtSearchHits")));
                    
                    this.getChildren().add(htmlElement.getEndTag("div"));
                    
                    // value
                    addSearchResultHitsToPage(resultItem, this.pubItem.getFiles().get(i).getReference());
                }
                
                
                // *** FILE NAME ***
                if(this.pubItem.getState() != null && this.pubItem.getState().equals(PubItemVO.State.WITHDRAWN))
                {
                    this.withdrawnTitle = new HtmlOutputText();
                    this.withdrawnTitle.setId(CommonUtils.createUniqueId(this.withdrawnTitle));
                    this.withdrawnTitle.setValue(getLabel("ViewItemFull_lblWithdrawn") + "  " + getLabel("ViewItemFull_lblNoAccess"));
                    
                    // label
                    this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd withdrawn"));
                    
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemMedium_lblFileName")));
                    
                    this.getChildren().add(htmlElement.getEndTag("div"));
                    
                    // value
                    this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd withdrawn"));
                    
                    // Deactivate the download button
                    this.btnDownload.setDisabled(true);
                    this.getChildren().add(this.btnDownload);
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty("  "));
                    this.getChildren().add(this.withdrawnTitle);
                    
                    this.getChildren().add(htmlElement.getEndTag("div"));
                }
                else
                {
                    // label
                    this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
                    
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemMedium_lblFileName")));
                    
                    this.getChildren().add(htmlElement.getEndTag("div"));
                    
                    // value
                    this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
                    
                    /*this.getChildren().add(htmlElement.getStartTag("a href=&quot;#&quot; onclick=&quot;downloadFile('" + this.btnDownload.getId()
                            + "');&quot; return false"));*/
                    //this.form.getChildren().add(this.downloadImage);
                    this.getChildren().add(this.btnDownload);
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty("  "));
                    this.getChildren().add(this.downloadLink);
                    
                    this.getChildren().add(htmlElement.getEndTag("div"));
                }
                
                
                // *** FILE SIZE / MIME TYPE ***
                // label
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));

                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemMedium_lblFileMimeTypeSize")));

                this.getChildren().add(htmlElement.getEndTag("div"));

                // value
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));

                // DiT, 07.11.2007: calculate file size in KB
                String fileSize = computeFileSize(this.pubItem.getFiles().get(i).getSize());
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(this.pubItem.getFiles().get(i).getMimeType() 
                                        + " / " + fileSize ));

                this.getChildren().add(htmlElement.getEndTag("div"));

                // *** CONTENT CATEGORY ***
                // label
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));

                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemMedium_lblFileCategory")));

                this.getChildren().add(htmlElement.getEndTag("div"));

                // value
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));

                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel(applicationBean.convertEnumToString(this.pubItem.getFiles().get(i).getContentType()))));

                this.getChildren().add(htmlElement.getEndTag("div"));
                
                
                // *** DESCRIPTION ***
                // label
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemMedium_lblFileDescription")));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                // value
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(this.pubItem.getFiles().get(i).getDescription()));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                
                // *** VISIBILITY ***
                // label
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemMedium_lblFileVisibility")));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                // value
                // Check if item is withdrawn
                if(this.pubItem.getState() != null && this.pubItem.getState().equals(PubItemVO.State.WITHDRAWN))
                {
                    this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd withdrawn"));
                    
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblWithdrawn")));
                    
                    this.getChildren().add(htmlElement.getEndTag("div"));
                }
                else
                {
                    this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
                    
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel(applicationBean.convertEnumToString(this.pubItem.getFiles().get(i).getVisibility()))));
                    
                    this.getChildren().add(htmlElement.getEndTag("div"));
                }
                
                // add some empty rows
                this.getChildren().add(htmlElement.getStartTag("br"));
                this.getChildren().add(htmlElement.getStartTag("br"));
                this.getChildren().add(htmlElement.getStartTag("br"));
                this.getChildren().add(htmlElement.getStartTag("br"));
            }
        }
        else
        {
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblNoEntries")));
            this.getChildren().add(htmlElement.getEndTag("div"));
        }
    }

    /**
     * Added by FrM. compute a better result for values < 1024.
     * 
     * @param i
     * @return
     */
	private String computeFileSize(long i) {
		if (i < 1024)
		{
			return i + getLabel("ViewItemMedium_lblFileSizeB");
		}
		else if (i < 1024 * 1024)
		{
			return ((i - 1) / 1024 + 1) + getLabel("ViewItemMedium_lblFileSizeKB");
		}
		else
		{
			return ((i - 1) / 1024 * 1024 + 1) + getLabel("ViewItemMedium_lblFileSizeMB");
		}
	}

    /**
     * Adds the search result hits to the page
     * @param resultItem the search result item
     * @param fileRO the reference of the file where the full text was found
     */
    private void addSearchResultHitsToPage(PubItemResultVO resultItem, PubFileRO fileRO)
    {
        // browse through the list of files and examine which of the files is the one the search result hits where found in
        for(int i = 0; i < resultItem.getSearchHitList().size(); i++)
        {
            if(resultItem.getSearchHitList().get(i).getType() == SearchHitType.FULLTEXT)
            {    
                if(resultItem.getSearchHitList().get(i).getHitReference() != null)
                {
                    if(resultItem.getSearchHitList().get(i).getHitReference().equals(fileRO))
                    {
                        for(int j = 0; j < resultItem.getSearchHitList().get(i).getTextFragmentList().size(); j++)
                        {
                            int startPosition = 0;
                            int endPosition = 0;
                            
                            startPosition = resultItem.getSearchHitList().get(i).getTextFragmentList().get(j).getHitwordList().get(0).getStartIndex();
                            endPosition = resultItem.getSearchHitList().get(i).getTextFragmentList().get(j).getHitwordList().get(0).getEndIndex() + 1;
                            
                            // value
                            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
                            
                            this.getChildren().add(htmlElement.getStartTagWithStyleClass("span", "item"));
                            this.getChildren().add(htmlElement.getStartTag("p"));
                            this.getChildren().add(htmlElement.getStartTag("samp"));
                            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty("..." + resultItem.getSearchHitList().get(i).getTextFragmentList().get(j).getData().substring(0, startPosition)));
                            this.getChildren().add(htmlElement.getStartTag("em"));
                            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(resultItem.getSearchHitList().get(i).getTextFragmentList().get(j).getData().substring(startPosition, endPosition)));
                            this.getChildren().add(htmlElement.getEndTag("em"));
                            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(resultItem.getSearchHitList().get(i).getTextFragmentList().get(j).getData().substring(endPosition) + "..."));
                            this.getChildren().add(htmlElement.getEndTag("samp"));
                            this.getChildren().add(htmlElement.getEndTag("p"));
                            this.getChildren().add(htmlElement.getEndTag("span"));
                            
                            this.getChildren().add(htmlElement.getEndTag("div"));
                        }
                    }
                    else
                    {
                        this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
                        this.getChildren().add(htmlElement.getEndTag("div"));
                    }
                }
                else
                {
                    this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
                    this.getChildren().add(htmlElement.getEndTag("div"));
                }
            }
            else
            {
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
                this.getChildren().add(htmlElement.getEndTag("div"));
            }
        }
    }

    /**
     * Method to examine which file should be downloaded and to trigger the correct download
     * @param event the  event that calls the method and triggers the download
     * @throws AbortProcessingException
     */
    public void processAction(ActionEvent event) throws AbortProcessingException
    {
        // find the index of the the button the user has clicked
        HtmlCommandButton button = (HtmlCommandButton)(event.getSource());
        
        // then find the indexes of the item and the file the clicked button belongs to
        int indexFile = new Integer(button.getId().substring(13));
        int indexItem = 0;
        try
        {
            this.downloadFile(indexItem, indexFile);
        }
        catch (Exception e)
        {
            logger.debug("File Download Error: " + e.toString());
        }
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
        String fileLocation = ServiceLocator.getFrameworkUrl() + this.getItemControllerSessionBean().getCurrentPubItem().getFiles().get(filePosition).getContent();
        String filename = this.getItemControllerSessionBean().getCurrentPubItem().getFiles().get(filePosition).getName(); // Filename suggested in browser Save As dialog
        filename = filename.replace(" ", "_"); // replace empty spaces because they cannot be procesed by the http-response (filename will be cutted after the first empty space)
        String contentType = this.getItemControllerSessionBean().getCurrentPubItem().getFiles().get(filePosition).getMimeType(); // For dialog, try
        
        // application/x-download
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse)facesContext.getExternalContext().getResponse();
        response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
        response.setContentLength(new Long(this.getItemControllerSessionBean().getCurrentPubItem().getFiles().get(filePosition).getSize()).intValue());
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
                    buffer = new byte[new Long(this.getItemControllerSessionBean().getCurrentPubItem().getFiles().get(filePosition).getSize()).intValue()];
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
     * Returns the ItemControllerSessionBean.
     * 
     * @return a reference to the scoped data bean (ItemControllerSessionBean)
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        ItemControllerSessionBean itemControllerSessionBean = (ItemControllerSessionBean)FacesContext.getCurrentInstance().getApplication().getVariableResolver()
        .resolveVariable(FacesContext.getCurrentInstance(), ItemControllerSessionBean.BEAN_NAME);
        return itemControllerSessionBean;
    }

    public void processValueChange(ValueChangeEvent arg0) throws AbortProcessingException
    {
       
    }
}
