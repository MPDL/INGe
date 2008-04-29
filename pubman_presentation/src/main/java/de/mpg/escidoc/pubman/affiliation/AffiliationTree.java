package de.mpg.escidoc.pubman.affiliation;

import java.util.List;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.AffiliationVOPresentation;
import de.mpg.escidoc.pubman.util.CommonUtils;

public class AffiliationTree extends FacesBean {

	List<AffiliationVOPresentation> affiliations;
	
	public AffiliationTree() throws Exception
	{
		affiliations = CommonUtils.convertToAffiliationVOPresentationList(getItemControllerSessionBean().retrieveTopLevelAffiliations());
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
		return getMessage("Affiliations_reloaded");
	}
}
