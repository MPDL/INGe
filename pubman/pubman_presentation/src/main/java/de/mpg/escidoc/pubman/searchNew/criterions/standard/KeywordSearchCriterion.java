package de.mpg.escidoc.pubman.searchNew.criterions.standard;

import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase.SearchCriterion;



public class KeywordSearchCriterion extends StandardSearchCriterion {

	public KeywordSearchCriterion() {
		
	}

	@Override
	public String[] getCqlIndexes() {
		return new String[] {"escidoc.publication.subject"};
	}
	
	@Override
	public SearchCriterion getSearchCriterion() {
		return SearchCriterion.KEYWORD;
	}


	

}
