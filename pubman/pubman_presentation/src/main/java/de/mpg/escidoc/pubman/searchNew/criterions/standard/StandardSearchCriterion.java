package de.mpg.escidoc.pubman.searchNew.criterions.standard;


import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase;

public abstract class StandardSearchCriterion extends SearchCriterionBase{

	
	
	private String searchString;
	

	
	@Override
	public String toCqlString() {
		return baseCqlBuilder(getCqlIndexes(), searchString);
	}

	@Override
	public String toQueryString() {
		return getSearchCriterion().name() + "=\"" + escapeForQueryString(searchString) + "\""; 
	}

	@Override
	public void parseQueryStringContent(String content) {
		this.searchString = unescapeForQueryString(content);
	}
	
	

	public abstract String[] getCqlIndexes();

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
	
	@Override
	public boolean isEmpty()
	{
		return searchString==null || searchString.trim().isEmpty();
	}
	
	
}
