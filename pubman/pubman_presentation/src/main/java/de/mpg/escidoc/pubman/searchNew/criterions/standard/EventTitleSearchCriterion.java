package de.mpg.escidoc.pubman.searchNew.criterions.standard;

import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase.SearchCriterion;

public class EventTitleSearchCriterion extends StandardSearchCriterion {

	@Override
	public String[] getCqlIndexes() {
		return new String[]{"escidoc.any-event"};
	}

	@Override
	public SearchCriterion getSearchCriterion() {
		return SearchCriterion.EVENT;
	}

}
