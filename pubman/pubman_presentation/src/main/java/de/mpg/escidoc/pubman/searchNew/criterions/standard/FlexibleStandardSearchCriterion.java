package de.mpg.escidoc.pubman.searchNew.criterions.standard;


public class FlexibleStandardSearchCriterion extends StandardSearchCriterion {

	private String[] cqlIndexes;
	
	
	public FlexibleStandardSearchCriterion(String[] cqlIndexes, String searchString)
	{
		this.cqlIndexes = cqlIndexes;
		this.setSearchString(searchString);
		
	}
	
	@Override
	public String[] getCqlIndexes() {
		return cqlIndexes;
	}
	
	@Override
	public SearchCriterion getSearchCriterion()
	{
		return SearchCriterion.FLEXIBLE;
	}

}
