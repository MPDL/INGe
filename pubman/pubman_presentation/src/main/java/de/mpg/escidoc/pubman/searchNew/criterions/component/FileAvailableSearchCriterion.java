package de.mpg.escidoc.pubman.searchNew.criterions.component;

public class FileAvailableSearchCriterion extends
		ComponentAvailableSearchCriterion {

	@Override
	public String getStorageType() {
		
		return "internal-managed";
	}

	@Override
	public SearchCriterion getSearchCriterion() {
		return SearchCriterion.FILE_AVAILABLE;
	}
	
	

}
