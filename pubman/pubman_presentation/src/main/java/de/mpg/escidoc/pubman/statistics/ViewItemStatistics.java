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
* f�r wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur F�rderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.statistics;


import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.myfaces.trinidad.component.UIXIterator;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.ViewItemStatisticsPage;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.PubFileVOPresentation;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.pubman.PubItemSimpleStatistics;
import de.mpg.escidoc.services.pubman.util.AdminHelper;

/**
 * Backing Bean for viewItemStatistics.jspf
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ViewItemStatistics extends FacesBean
{
    private static Logger logger = Logger.getLogger(ViewItemStatisticsPage.class);
    
    
    /** The object Id of the current item */
    private String itemId;
    
    /** A List with all PubFileVOPresentation objects representing all files of the current item.*/ 
    private List<PubFileVOPresentation> fileList;
    
    
    private UIXIterator fileIterator;
   
    /** The current pub item*/
    private PubItemVO pubItem;
    
   
    public ViewItemStatistics() {
        this.init();
        
    }
    
    
    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation.
     */
   public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
        
        
        
        //get current PubItem and its ID
        pubItem = getItemControllerSessionBean().getCurrentPubItem();
        itemId = pubItem.getVersion().getObjectId();
         
        //get all files, remove Locators, convert to presentation objects and add them to the list
        List<FileVO> files = pubItem.getFiles();
        List<FileVO> realFiles = new ArrayList<FileVO>();        
       
        for(FileVO fileVO : files) 
        {
            if (fileVO.getStorage() == FileVO.Storage.INTERNAL_MANAGED) realFiles.add(fileVO);
        }
        
        fileList = CommonUtils.convertToPubFileVOPresentationList(realFiles);
        
        //Get Statistics handler

       
    }
    
    public String getNumberOfItemRetrievalsAllUsers() throws Exception
    {
        
        return getItemControllerSessionBean().getStatisticValue(PubItemSimpleStatistics.REPORTDEFINITION_NUMBER_OF_ITEM_RETRIEVALS_ALL_USERS);
        
    }
    
    public String getNumberOfItemRetrievalsAnonymousUsers() throws Exception
    {
        return getItemControllerSessionBean().getStatisticValue(PubItemSimpleStatistics.REPORTDEFINITION_NUMBER_OF_ITEM_RETRIEVALS_ANONYMOUS);
        
    }
    
    public String getNumberOfFileDownloadsPerItemAllUsers() throws Exception
    {
        return getItemControllerSessionBean().getStatisticValue(PubItemSimpleStatistics.REPORTDEFINITION_FILE_DOWNLOADS_PER_ITEM_ALL_USERS);
       
    }
    
    public String getNumberOfFileDownloadsPerItemAnonymousUsers() throws Exception
    {
        return getItemControllerSessionBean().getStatisticValue(PubItemSimpleStatistics.REPORTDEFINITION_FILE_DOWNLOADS_PER_ITEM_ANONYMOUS);
        
    }
    
    

    public List<PubFileVOPresentation> getFileList()
    {
        return fileList;
    }

    public void setFileList(List<PubFileVOPresentation> fileList)
    {
        this.fileList = fileList;
    }

    public UIXIterator getFileIterator()
    {
        return fileIterator;
    }

    public void setFileIterator(UIXIterator fileIterator)
    {
        this.fileIterator = fileIterator;
    }

    
    /**
     * Returns the ItemControllerSessionBean.
     * 
     * @return a reference to the scoped data bean (ItemControllerSessionBean)
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean)getSessionBean(ItemControllerSessionBean.class);
    }


    public PubItemVO getPubItem()
    {
        return pubItem;
    }


    public void setPubItem(PubItemVO pubItem)
    {
        this.pubItem = pubItem;
    }


    public String getItemID()
    {
        return itemId;
    }


    public void setItemID(String itemID)
    {
        this.itemId = itemID;
    }
    
    public boolean getFilesAvailable() {
        return fileList.size() > 0;
    }
}
