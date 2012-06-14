package de.mpg.escidoc.pubman.searchNew.criterions.standard;

import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase.SearchCriterion;

public class JournalSearchCriterion extends StandardSearchCriterion {

	@Override
	public String[] getCqlIndexes() {
		return new String[] {"escidoc.publication.source.any.title"};
	}
	
	@Override
	public SearchCriterion getSearchCriterion() {
		return SearchCriterion.JOURNAL;
	}


}
