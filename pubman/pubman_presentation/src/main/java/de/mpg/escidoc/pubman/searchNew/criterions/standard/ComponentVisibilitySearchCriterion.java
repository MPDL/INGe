package de.mpg.escidoc.pubman.searchNew.criterions.standard;

import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase.SearchCriterion;

public class ComponentVisibilitySearchCriterion extends StandardSearchCriterion {

	@Override
	public String[] getCqlIndexes() {
		
		return new String[]{"escidoc.component.visibility"};
	}
	
	@Override
	public SearchCriterion getSearchCriterion() {
		return SearchCriterion.COMPONENT_VISIBILITY;
	}


}
