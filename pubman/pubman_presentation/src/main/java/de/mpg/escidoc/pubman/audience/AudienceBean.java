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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.GrantVOPresentation;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubFileVOPresentation;
import de.mpg.escidoc.pubman.viewItem.ViewItemFull;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.common.valueobjects.intelligent.grants.Grant;
import de.mpg.escidoc.services.common.valueobjects.intelligent.grants.GrantList;
import de.mpg.escidoc.services.common.valueobjects.intelligent.grants.CurrentGrants.UserType;
import de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup.UserGroup;
import de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup.UserGroupList;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * Fragment class for editing the audience grants of files. 
 * This class provides all functionality for giving and revoking user group grants for files in request scope.
 *
 * @author: Tobias Schraut, 2009-05-20
 */
public class AudienceBean extends FacesBean
{
	private static Logger logger = Logger.getLogger(AudienceBean.class);
	public static final String BEAN_NAME = "AudienceBean";
    // Faces navigation string
    public static final String LOAD_AUDIENCEPAGE = "loadAudiencePage";
    public static final String DUMMY_REVOKE_COMMENT = "grant revoked";
    public static final String DUMMY_CREATE_COMMENT = "grant created";
    
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
        AudienceSessionBean asb = this.getAudienceSessionBean();
        // fill the file list in the session bean
        if(this.getAudienceSessionBean().getFileListNew() == null || this.getAudienceSessionBean().getFileListNew().size() == 0)
        {
	        if(this.getItemControllerSessionBean().getCurrentPubItem().getFiles() != null)
	        {
	        	LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
	        	int fileIndex = 0;
	        	for(int i = 0; i < this.getItemControllerSessionBean().getCurrentPubItem().getFiles().size(); i++)
	        	{
	        		// only take files with visibility audience
	        		if(this.getItemControllerSessionBean().getCurrentPubItem().getFiles().get(i).getVisibility().equals(Visibility.AUDIENCE))
	        		{
	        			PubFileVOPresentation fileForNewList = new PubFileVOPresentation(fileIndex, this.getItemControllerSessionBean().getCurrentPubItem().getFiles().get(i));
	        			PubFileVOPresentation fileForOldList = new PubFileVOPresentation(fileIndex, this.getItemControllerSessionBean().getCurrentPubItem().getFiles().get(i));
	        			
	        			// add the grants
	        			GrantList grantList = new GrantList();
	        			try 
	        			{
							grantList = GrantList.Factory.retrieveGrantsForObject(loginHelper.getESciDocUserHandle(), this.getItemControllerSessionBean().getCurrentPubItem().getFiles().get(i).getReference().getObjectId(), Grant.CoreserviceRole.AUDIENCE.getRoleId());
						} 
	        			catch (Exception e) 
						{
							logger.error("could not retrieve audience grants for files: ", e);
						}
	        			
	        			for(int j = 0; j < grantList.getGrants().size(); j++)
	        			{
	        				fileForNewList.getGrantList().add(new GrantVOPresentation(grantList.getGrants().get(j), j, fileIndex));
	        				fileForOldList.getGrantList().add(new GrantVOPresentation(grantList.getGrants().get(j), j, fileIndex));
	        			}
	        			
	        			// ensure that at least one grant is in the list (for presentation)
	        			if(fileForNewList.getGrantList().size() == 0)
	        			{
	        				Grant newGrant = new Grant();
	        				newGrant.setObjid("");
	        				newGrant.setAssignedOn(this.getItemControllerSessionBean().getCurrentPubItem().getFiles().get(i).getReference().getObjectId());
	        		    	newGrant.setGrantType(GrantVOPresentation.GRANT_TYPE_USER_GROUP);
	        		    	newGrant.setRole(Grant.CoreserviceRole.AUDIENCE.getRoleId());
	        				fileForNewList.getGrantList().add(new GrantVOPresentation(newGrant, 0, 0));
	        			}
	        			
	        			this.getAudienceSessionBean().getFileListOld().add(fileForOldList);
	        			this.getAudienceSessionBean().getFileListNew().add(fileForNewList);
	        			
	        			fileIndex ++;
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
	        	Grant newGrant = new Grant();
				newGrant.setObjid("");
		    	newGrant.setGrantType(GrantVOPresentation.GRANT_TYPE_USER_GROUP);
		    	newGrant.setRole(Grant.CoreserviceRole.AUDIENCE.getRoleId());
	        	this.getAudienceSessionBean().getGrantsForAllFiles().add(new GrantVOPresentation(newGrant, this.getAudienceSessionBean().getGrantsForAllFiles().size()));
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
    	Grant newGrant = new Grant();
    	newGrant.setObjid("");
    	newGrant.setGrantType(GrantVOPresentation.GRANT_TYPE_USER_GROUP);
    	newGrant.setRole(Grant.CoreserviceRole.AUDIENCE.getRoleId());
    	this.getGrantsForAllFiles().add(new GrantVOPresentation(newGrant, this.getAudienceSessionBean().getGrantsForAllFiles().size()));
    	return AudienceBean.LOAD_AUDIENCEPAGE;
    }
    
    /**
     * This method applies all grants to every file listed
     * @return String navigation string
     */
    public String applyForAll()
    {
    	AudienceSessionBean asb = this.getAudienceSessionBean();
    	//if(this.getAudienceSessionBean().getGrantsForAllFiles().size() > 0 && !this.getAudienceSessionBean().getGrantsForAllFiles().get(0).getGrant().getGrantedTo().trim().equals(""))
    	//{
    		for(int i = 0; i < this.getAudienceSessionBean().getFileListNew().size(); i++)
    		{
    			// first remove all existing grants
    			this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().clear();
    			// then add the new grants
    			for(int j = 0; j < this.getAudienceSessionBean().getGrantsForAllFiles().size(); j++)
    			{
    				if(this.getAudienceSessionBean().getGrantsForAllFiles().get(j).getGrant().getGrantedTo() != null && !this.getAudienceSessionBean().getGrantsForAllFiles().get(j).getGrant().getGrantedTo().trim().equals(""))
    				{
    					Grant newGrant = new Grant();
    					newGrant.setAssignedOn(this.getAudienceSessionBean().getFileListNew().get(i).getFile().getReference().getObjectId());
    					newGrant.setGrantedTo(this.getAudienceSessionBean().getGrantsForAllFiles().get(j).getGrant().getGrantedTo());
    					newGrant.setGrantType(this.getAudienceSessionBean().getGrantsForAllFiles().get(j).getGrant().getGrantType());
    					newGrant.setRole(this.getAudienceSessionBean().getGrantsForAllFiles().get(j).getGrant().getRole());
    					newGrant.setGrantedTo(this.getAudienceSessionBean().getGrantsForAllFiles().get(j).getGrant().getGrantedTo());
    					this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().add(new GrantVOPresentation(newGrant, this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().size(), i));
    				}
    			}
    			for (int k = 0; k < this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().size(); k++)
    			{
    				if(this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().get(k).getGrant().getGrantedTo() == null 
    						|| this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().get(k).getGrant().getGrantedTo().trim().equals(""))
    				{
    					this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().remove(k);
    				}
    			}
    		}
    	//}
    	return AudienceBean.LOAD_AUDIENCEPAGE;
    }
    
    /**
     * This method saves new grants and revokes grants according to the changes made by the user 
     * @return String navigation string to the View item page
     */
    public String save()
    {
    	LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
    	AudienceSessionBean asb = this.getAudienceSessionBean();
    	
    	// first clean up unnecessary grants (empty grants for presentation)
    	// old list
    	for(int i = 0; i < this.getAudienceSessionBean().getFileListOld().size(); i++)
    	{
    		for (int j = this.getAudienceSessionBean().getFileListOld().get(i).getGrantList().size();  j > 0; j--)
    		{
    			if(this.getAudienceSessionBean().getFileListOld().get(i).getGrantList().get(j-1).getGrant().getAssignedOn() == null || this.getAudienceSessionBean().getFileListOld().get(i).getGrantList().get(j-1).getGrant().getAssignedOn().trim().equals(""))
    			{
    				this.getAudienceSessionBean().getFileListOld().get(i).getGrantList().remove(j-1);
    			}
    		}
    	}
    	
    	// new list
    	for(int i = 0; i < this.getAudienceSessionBean().getFileListNew().size(); i++)
    	{
    		for (int j = this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().size();  j > 0; j--)
    		{
    			if(this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().get(j-1).getGrant().getAssignedOn() == null || this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().get(j-1).getGrant().getAssignedOn().trim().equals(""))
    			{
    				this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().remove(j-1);
    			}
    		}
    	}
	
    	// First look for grants to be revoked (which are available in the  old list but do not exist in the new list anymore) or changed
    	for(int i = 0; i < this.getAudienceSessionBean().getFileListOld().size(); i++)
    	{
    		List<GrantVOPresentation> grants  = this.getAudienceSessionBean().getFileListOld().get(i).getGrantList();
    		List<GrantVOPresentation> grantsToRevoke  = this.getAudienceSessionBean().getFileListOld().get(i).getGrantList();
    		List<GrantVOPresentation> grantsToCreate  = new ArrayList<GrantVOPresentation>();
    		// go through the grants
    		for(int j = 0; j < this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().size(); j++)
    		{
    			// check if there is an object id (if not, the grant MUST be completely new!)
    			if(this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().get(j).getGrant().getObjid() != null 
    					&& !this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().get(j).getGrant().getObjid().trim().equals(""))
    			{
	    			// compare with the grants in the new list (corresponding file)
	    			for(int k = 0; k < grants.size(); k++)
	    			{
	    				if(grants.get(k).getGrant().getObjid().equals(this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().get(j).getGrant().getObjid()))
	    				{
	    					// check if user group has been changed 
	    					if(grants.get(k).getGrant().getGrantedTo().equals(this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().get(j).getGrant().getGrantedTo()))
	    					{
	    						grantsToRevoke.remove(k);
	    					}
	    					else
	    					{
	    						//grantsToRevoke.remove(k);
	    						grantsToCreate.add(this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().get(j));
	    					}
	    				}
	    			}
    			}
    			else
    			{
    				/*if(grantsToRevoke != null && grantsToRevoke.size() > 0)
    				{
    					grantsToRevoke.remove(j);
    				}*/
    				
    				grantsToCreate.add(this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().get(j));
    			}
    		}
    		
    		// revoke the grants to be revoked
    		if(grantsToRevoke != null)
    		{
    			for(int l = 0; l < grantsToRevoke.size(); l++)
    			{
    				grantsToRevoke.get(l).getGrant().revokeInCoreservice(loginHelper.getESciDocUserHandle(), DUMMY_REVOKE_COMMENT);
    			}
    		}
    		// create grants that have been changed
    		if(grantsToCreate != null)
    		{
    			for(int m = 0; m < grantsToCreate.size(); m++)
    			{
    				try
    				{
    					grantsToCreate.get(m).getGrant().createInCoreservice(loginHelper.getESciDocUserHandle(), DUMMY_CREATE_COMMENT);
    				}
    				catch (RuntimeException rE)
    				{
    					// just do nothing if grant already exists
    				}
    			}
    		}
    	}
    	this.getAudienceSessionBean().cleanUp();
    	return ViewItemFull.LOAD_VIEWITEM;
    }
    
    public String cancel()
    {
    	this.getAudienceSessionBean().cleanUp();
    	return ViewItemFull.LOAD_VIEWITEM;
    }
    
    /**
     * Returns the URL of the coreservice this PubMan instance is currently working with
     * @return String URL of the coreservice
     */
    public String getFwUrl()
    {
    	String fwUrl = "";
    	
    	// populate the core service Url
        try 
        {
			fwUrl = PropertyReader.getProperty("escidoc.framework_access.framework.url");
		} 
        catch (IOException ioE) 
		{
			throw new RuntimeException("Could  not read the Property file for property 'escidoc.framework_access.framework.url'", ioE);
		} 
        catch (URISyntaxException uE) 
		{
			throw new RuntimeException("Syntax of property 'escidoc.framework_access.framework.url' not correct", uE);
		}
    	return fwUrl;
    }
    
    public String getItemPattern()
    {
    	String itemPattern = "";
    	
    	String pubmanUrl = "";
		try 
		{
			pubmanUrl = PropertyReader.getProperty("escidoc.pubman.instance.url") + PropertyReader.getProperty("escidoc.pubman.instance.context.path");
		} 
		catch (IOException ioE) 
		{
			throw new RuntimeException("Could  not read the Property file for property 'escidoc.pubman.instance.url' or 'escidoc.pubman.instance.context.path'", ioE);
		} 
		catch (URISyntaxException uE) 
		{
			throw new RuntimeException("Syntax of property 'escidoc.pubman.instance.url' or 'escidoc.pubman.instance.context.path' not correct", uE);
		}
        
        try 
        {
			itemPattern = PropertyReader.getProperty("escidoc.pubman.item.pattern").replaceAll("\\$1", this.getItemControllerSessionBean().getCurrentPubItem().getVersion().getObjectIdAndVersion());
		} 
        catch (IOException ioE) 
		{
        	throw new RuntimeException("Could  not read the Property file for property 'escidoc.pubman.item.pattern'", ioE);
		} 
        catch (URISyntaxException uE) 
        {
        	throw new RuntimeException("Syntax of property 'escidoc.pubman.item.pattern' not correct", uE);
		}
        
        
        if(!pubmanUrl.endsWith("/")) pubmanUrl = pubmanUrl + "/";
        if (itemPattern.startsWith("/")) itemPattern = itemPattern.substring(1, itemPattern.length());
        
    	return itemPattern;
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
