package de.mpg.escidoc.pubman.searchNew.criterions.stringOrHiddenId;

import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase.DisplayType;


public class OrganizationSearchCriterion extends
		StringOrHiddenIdSearchCriterion {

	@Override
	public String[] getCqlIndexForHiddenId() {
		return new String[] {"escidoc.any-organization-pids"};
	}

	@Override
	public String[] getCqlIndexForSearchString() {
		return new String[] {"escidoc.any-organizations"};
	}

	@Override
	public SearchCriterion getSearchCriterion() {
		return SearchCriterion.ORGUNIT;
	}
	


}
