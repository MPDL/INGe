package de.mpg.escidoc.pubman.searchNew.criterions.standard;

import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase.SearchCriterion;


public class AnyFieldSearchCriterion extends StandardSearchCriterion {

	

	@Override
	public String[] getCqlIndexes() {
		return new String[] {"escidoc.metadata"};
	}

	@Override
	public SearchCriterion getSearchCriterion() {
		return SearchCriterion.ANY;
	}

	
}
