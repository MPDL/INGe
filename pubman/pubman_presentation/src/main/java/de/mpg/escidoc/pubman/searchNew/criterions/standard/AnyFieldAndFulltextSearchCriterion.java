package de.mpg.escidoc.pubman.searchNew.criterions.standard;

import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase.SearchCriterion;


public class AnyFieldAndFulltextSearchCriterion extends StandardSearchCriterion {

	

	@Override
	public String[] getCqlIndexes() {
		return new String[] {"escidoc.metadata", "escidoc.fulltext"};
	}

	@Override
	public SearchCriterion getSearchCriterion() {
		return SearchCriterion.ANYFULLTEXT;
	}


}
