package de.mpg.escidoc.pubman.searchNew.criterions.standard;

import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase.SearchCriterion;


public class DegreeSearchCriterion extends StandardSearchCriterion {

	

	@Override
	public String[] getCqlIndexes() {
		return new String[] {"escidoc.publication.degree"};
	}
	
	@Override
	public SearchCriterion getSearchCriterion() {
		return SearchCriterion.DEGREE;
	}


	

}
