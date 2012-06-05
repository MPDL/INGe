package de.mpg.escidoc.pubman.searchNew.criterions.checkbox;

import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase;

public class EmbargoDateAvailableSearchCriterion extends SearchCriterionBase {

	private boolean withEmbargoDate = false;
	
	@Override
	public String toCqlString() {
		if(withEmbargoDate)
		{
			return "escidoc.component.file.available>\"''\"";
		}
		else
		{
			return null;
		}
	}

	@Override
	public String toQueryString() {
		return getSearchCriterion() + "=\"" + withEmbargoDate + "\"";
	}

	@Override
	public void parseQueryStringContent(String content) {
		this.withEmbargoDate = Boolean.parseBoolean(content);
		
	}

	@Override
	public boolean isEmpty() {
		return !withEmbargoDate;
	}

	public boolean isWithEmbargoDate() {
		return withEmbargoDate;
	}

	public void setWithEmbargoDate(boolean withEmbargoDate) {
		this.withEmbargoDate = withEmbargoDate;
	}

	@Override
	public SearchCriterion getSearchCriterion() {
		return SearchCriterion.EMBARGO_DATE_AVAILABLE;
	}
	
	

}
