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
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.audience;

import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.GrantVOPresentation;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubFileVOPresentation;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.common.valueobjects.intelligent.grants.Grant;
import de.mpg.escidoc.services.common.valueobjects.intelligent.grants.GrantList;
import de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup.UserGroup;
import de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup.UserGroupList;

/**
 * Fragment class for editing the audience grants of files. 
 * This class provides all functionality for giving and revoking user group grants for files in request scope.
 *
 * @author: Tobias Schraut, 2009-05-20
 */
public class AudienceBean extends FacesBean
{
	private static Logger logger = Logger.getLogger(AudienceBean.class);
    // Faces navigation string
    public static final String LOAD_AUDIENCEPAGE = "loadAudiencePage";
    public static final String ESCIDOC_ROLE_AUDIENCE = "escidoc:role-audience";
    
    /**
     * Public constructor.
     */
    public AudienceBean()
    {
        this.init();
    }

    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation.
     */
    public final void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
        
        // fill the file list in the session bean
        if(this.getAudienceSessionBean().getFileListNew() == null || this.getAudienceSessionBean().getFileListNew().size() == 0)
        {
	        if(this.getItemControllerSessionBean().getCurrentPubItem().getFiles() != null)
	        {
	        	LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
	        	for(int i = 0; i < this.getItemControllerSessionBean().getCurrentPubItem().getFiles().size(); i++)
	        	{
	        		// only take files with visibility audience
	        		if(this.getItemControllerSessionBean().getCurrentPubItem().getFiles().get(i).getVisibility().equals(Visibility.AUDIENCE))
	        		{
	        			PubFileVOPresentation file = new PubFileVOPresentation(i, this.getItemControllerSessionBean().getCurrentPubItem().getFiles().get(i));
	        			
	        			// add the grants
	        			GrantList grantList = new GrantList();
	        			try 
	        			{
							grantList = GrantList.Factory.retrieveGrantsForObject(loginHelper.getESciDocUserHandle(), this.getItemControllerSessionBean().getCurrentPubItem().getFiles().get(i).getReference().getObjectId(), ESCIDOC_ROLE_AUDIENCE);
						} 
	        			catch (Exception e) 
						{
							logger.error("could not retrieve audience grants for files: ", e);
						}
	        			
	        			for(int j = 0; j < grantList.getGrants().size(); j++)
	        			{
	        				file.getGrantList().add(new GrantVOPresentation(grantList.getGrants().get(j), j, i));
	        			}
	        			this.getAudienceSessionBean().getFileListOld().add(file);
	        			this.getAudienceSessionBean().getFileListNew().add(file);
	        		}
	        	}
	        }
	        
	        // fill the user group list
	        if(this.getAudienceSessionBean().getUgl() == null)
	        {
	        	LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
	        	try 
	        	{
					this.getAudienceSessionBean().setUgl(UserGroupList.Factory.retrieveActiveUserGroups(loginHelper.getESciDocUserHandle()));
				} 
	        	catch (Exception e) 
	        	{
	        		logger.error("could not retrieve user groups for audience management: ", e);
				}
	        }
	        
	        // ensure that there is at least one grant for all files (for display purpose)
	        if(this.getAudienceSessionBean().getGrantsForAllFiles() != null && this.getAudienceSessionBean().getGrantsForAllFiles().size() == 0)
	        {
	        	this.getAudienceSessionBean().getGrantsForAllFiles().add(new GrantVOPresentation(new Grant(), this.getAudienceSessionBean().getGrantsForAllFiles().size()));
	        }
        }
    }
    
    /**
     * Returns the number of files with visibility audience (for presentation purpose)
     * @return number of files with visibility audience 
     */
    public int getNumberOfFiles()
    {
    	int numberOfFiles = 0;
    	numberOfFiles = this.getAudienceSessionBean().getFileListNew().size();
    	return numberOfFiles;
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
    
    /**
     * Returns all user groups
     * @return all user groups 
     */
    public SelectItem[] getUserGroups()
    {
    	SelectItem[] selectItems = new SelectItem[this.getUserGroupList().getUserGroupLists().size() +1];
    	// the first and empty list entry
    	SelectItem selectItem = new SelectItem("", "-");
        selectItems[0] = selectItem;
        
    	for(int i = 0; i < this.getUserGroupList().getUserGroupLists().size(); i++)
        {
        	selectItem = new SelectItem(this.getUserGroupList().getUserGroupLists().get(i).getObjid(), this.getUserGroupList().getUserGroupLists().get(i).getLabel());
            selectItems[i+1] = selectItem;
        }
    	
    	return selectItems;
    }
    
    public String addGrantForAllFiles()
    {
    	this.getGrantsForAllFiles().add(new GrantVOPresentation(new Grant(), this.getAudienceSessionBean().getGrantsForAllFiles().size()));
    	return AudienceBean.LOAD_AUDIENCEPAGE;
    }
    
    /**
     * Returns the AudienceSessionBean.
     * 
     * @return a reference to the scoped data bean (AudienceSessionBean)
     */
    protected AudienceSessionBean getAudienceSessionBean()
    {
        return (AudienceSessionBean)getSessionBean(AudienceSessionBean.class);
    }
    
    public UserGroupList getUserGroupList() 
    {
		return this.getAudienceSessionBean().getUgl();
	}

	public void setUserGroupList(UserGroupList ugl) 
	{
		this.getAudienceSessionBean().setUgl(ugl);
	}
	
	public List<PubFileVOPresentation> getFileList() {
		return this.getAudienceSessionBean().getFileListNew();
	}

	public void setFileList(List<PubFileVOPresentation> fileList) {
		this.getAudienceSessionBean().setFileListNew(fileList);
	}
	
	public List<GrantVOPresentation> getGrantsForAllFiles() {
		return this.getAudienceSessionBean().getGrantsForAllFiles();
	}

	public void setGrantsForAllFiles(List<GrantVOPresentation> grantsForAllFiles) {
		this.getAudienceSessionBean().setGrantsForAllFiles(grantsForAllFiles);
	}
	
    
}
