package de.mpg.escidoc.pubman.searchNew.criterions.stringOrHiddenId;

import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.StandardSearchCriterion;

public abstract class StringOrHiddenIdSearchCriterion extends SearchCriterionBase {

	private String hiddenId;
	
	private String searchString;

	
	
	
	public String getHiddenId() {
		return hiddenId;
	}

	public void setHiddenId(String hiddenId) {
		this.hiddenId = hiddenId;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
	
	@Override
	public boolean isEmpty()
	{
		return (searchString==null || searchString.trim().isEmpty()) && (hiddenId==null || hiddenId.trim().isEmpty());
	}
	
	@Override
	public String toCqlString() {
		if(hiddenId!=null && !hiddenId.trim().isEmpty())
		{
			return baseCqlBuilder(getCqlIndexForHiddenId(), hiddenId);
		}
		else
		{
			return baseCqlBuilder(getCqlIndexForSearchString(), searchString);
		}
	}
	
	

	@Override
	public String toQueryString() {
		
			return getSearchCriterion().name() + "=\"" + escapeForQueryString(searchString) + "|" + escapeForQueryString(hiddenId) + "\""; 
		
		
	}

	@Override
	public void parseQueryStringContent(String content) {
		//Split by '|', which have no backslash
		String[] parts = content.split("(?<!\\\\)\\|");
		
		this.searchString=unescapeForQueryString(parts[0]);
		if(parts.length>1)
		{
			this.hiddenId=unescapeForQueryString(parts[1]);
		}
		
		
	
			
		
		
		
		
	}
	
	
	public abstract String[] getCqlIndexForHiddenId();
	
	public abstract String[] getCqlIndexForSearchString();
	
	
	

}
