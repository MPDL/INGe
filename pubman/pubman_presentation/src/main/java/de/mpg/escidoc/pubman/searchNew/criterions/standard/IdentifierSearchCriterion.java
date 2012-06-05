package de.mpg.escidoc.pubman.searchNew.criterions.standard;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase.SearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.stringOrHiddenId.StringOrHiddenIdSearchCriterion;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;

public class IdentifierSearchCriterion extends StandardSearchCriterion {

	@Override
	public String[] getCqlIndexes() {
		return new String[] {"escidoc.any-identifier", "escidoc.property.latest-release.objid"};
	}
	
	@Override
	public SearchCriterion getSearchCriterion() {
		return SearchCriterion.IDENTIFIER;
	}

	
	

}
