package de.mpg.escidoc.pubman.searchNew.criterions.standard;

import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase.SearchCriterion;



public class ClassificationSearchCriterion extends StandardSearchCriterion {

	public ClassificationSearchCriterion() {
		
	}

	@Override
	public String[] getCqlIndexes() {
		return new String[] {"escidoc.publication.subject"};
	}
	
	@Override
	public SearchCriterion getSearchCriterion() {
		return SearchCriterion.CLASSIFICATION;
	}


	

}
