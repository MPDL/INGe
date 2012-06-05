package de.mpg.escidoc.pubman.searchNew.criterions.standard;



public class TitleSearchCriterion extends StandardSearchCriterion {

	
	@Override
	public String[] getCqlIndexes() {
		return new String[] {"escidoc.publication.title"};
	}

	@Override
	public SearchCriterion getSearchCriterion() {
		return SearchCriterion.TITLE;
	}

	

}
