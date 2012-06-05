package de.mpg.escidoc.pubman.searchNew.criterions.standard;

import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase.SearchCriterion;

public class ComponentContentCategory extends StandardSearchCriterion {

	@Override
	public String[] getCqlIndexes() {
		return new String[]{"escidoc.component.content-category"};
	}

	
	@Override
	public SearchCriterion getSearchCriterion() {
		return SearchCriterion.COMPONENT_CONTENT_CATEGORY;
	}

}
