package de.mpg.escidoc.pubman.util;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.audience.AudienceSessionBean;
import de.mpg.escidoc.services.common.valueobjects.intelligent.grants.Grant;

public class GrantVOPresentation extends FacesBean 
{
	private Grant grant;
	private int index;
	private int fileIndex;
	
	/**
	 * Public constructor
	 */
	public GrantVOPresentation()
	{
		
	}
	
	/**
	 * Public constructor with parameters
	 * @param grant the grant
	 * @param index the index of the grant within the file
	 */
	public GrantVOPresentation(Grant grant, int index)
	{
		this.grant = grant;
		this.index = index;
	}
	
	/**
	 * 
	 * @param grant the grant
	 * @param index the index of the grant within the file
	 * @param fileIndex the index of the file in the item
	 */
	public GrantVOPresentation(Grant grant, int index, int fileIndex)
	{
		this.grant = grant;
		this.index = index;
		this.fileIndex = fileIndex;
	}
	
	public void remove()
	{
		AudienceSessionBean asb = this.getAudienceSessionBean();
		asb.getFileListNew().get(this.fileIndex).getGrantList().remove(this);
		if(asb.getFileListNew().get(this.fileIndex).getGrantList().size() < 1)
    	{
			asb.getFileListNew().get(this.fileIndex).getGrantList().add(new GrantVOPresentation(new Grant(), asb.getFileListNew().get(this.fileIndex).getGrantList().size(), this.fileIndex));
    	}
	}
	
	public void removeGrantForAllFiles()
    {
    	this.getAudienceSessionBean().getGrantsForAllFiles().remove(this);
    	if(this.getAudienceSessionBean().getGrantsForAllFiles().size() < 1)
    	{
    		this.getAudienceSessionBean().getGrantsForAllFiles().add(new GrantVOPresentation(new Grant(), this.getAudienceSessionBean().getGrantsForAllFiles().size()));
    	}
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
     * Returns the AudienceSessionBean.
     * 
     * @return a reference to the scoped data bean (AudienceSessionBean)
     */
    protected AudienceSessionBean getAudienceSessionBean()
    {
        return (AudienceSessionBean)getSessionBean(AudienceSessionBean.class);
    }

	public Grant getGrant() {
		return grant;
	}

	public void setGrant(Grant grant) {
		this.grant = grant;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	
}
