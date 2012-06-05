package de.mpg.escidoc.pubman.searchNew.criterions.component;

public class LocatorAvailableSearchCriterion extends ComponentAvailableSearchCriterion {

	@Override
	public String getStorageType() {
		return "external-url";
	}

	@Override
	public SearchCriterion getSearchCriterion() {
		return SearchCriterion.LOCATOR_AVAILABLE;
	}

}
