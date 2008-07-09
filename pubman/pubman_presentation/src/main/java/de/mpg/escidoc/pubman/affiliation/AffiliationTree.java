package de.mpg.escidoc.pubman.affiliation;

import java.util.Date;
import java.util.List;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.AffiliationVOPresentation;
import de.mpg.escidoc.pubman.util.CommonUtils;

public class AffiliationTree extends FacesBean {

    public static final String BEAN_NAME = "AffiliationTree";
	List<AffiliationVOPresentation> affiliations;
	long timestamp;
	
	public AffiliationTree() throws Exception
	{
		affiliations = CommonUtils.convertToAffiliationVOPresentationList(getItemControllerSessionBean().retrieveTopLevelAffiliations());
		timestamp = new Date().getTime();
	}

	public List<AffiliationVOPresentation> getAffiliations() {
		return affiliations;
	}

	public void setAffiliations(List<AffiliationVOPresentation> affiliations) {
		this.affiliations = affiliations;
	}
	
	private ItemControllerSessionBean getItemControllerSessionBean()
	{
		return (ItemControllerSessionBean) getSessionBean(ItemControllerSessionBean.class);
	}

	public String getResetMessage() throws Exception
	{
		affiliations = CommonUtils.convertToAffiliationVOPresentationList(getItemControllerSessionBean().retrieveTopLevelAffiliations());
		timestamp = new Date().getTime();
		return getMessage("Affiliations_reloaded");
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
}
