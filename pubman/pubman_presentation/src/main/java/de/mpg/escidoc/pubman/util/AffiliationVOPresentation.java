package de.mpg.escidoc.pubman.util;

import java.util.List;

import javax.faces.context.FacesContext;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;

public class AffiliationVOPresentation extends AffiliationVO
{

	public AffiliationVOPresentation(AffiliationVO affiliation)
	{
		super(affiliation);
	}

	public List<AffiliationVOPresentation> getChildren() throws Exception
	{
		List<AffiliationVOPresentation> result = ((ItemControllerSessionBean)FacesContext
		        .getCurrentInstance()
		        .getExternalContext()
		        .getSessionMap()
		        .get("ItemControllerSessionBean"))
		        .retrieveChildAffiliations(this);
		
		return result;
		
//		return ((ItemControllerSessionBean)FacesContext
//		        .getCurrentInstance()
//		        .getExternalContext()
//		        .getSessionMap()
//		        .get("ItemControllerSessionBean"))
//		        .retrieveChildAffiliations(this);
	}
}
