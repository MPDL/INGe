package de.mpg.escidoc.pubman.searchNew.criterions.standard;

import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase.SearchCriterion;


public class GenreSearchCriterion extends StandardSearchCriterion {

	

	@Override
	public String[] getCqlIndexes() {
		return new String[] {"escidoc.publication.type"};
	}

	@Override
	public SearchCriterion getSearchCriterion() {
		return SearchCriterion.GENRE;
	}

	

}
