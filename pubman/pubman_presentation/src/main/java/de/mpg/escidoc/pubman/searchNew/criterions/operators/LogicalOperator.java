package de.mpg.escidoc.pubman.searchNew.criterions.operators;

import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase;

public class LogicalOperator extends SearchCriterionBase {

	
	
	public LogicalOperator (SearchCriterion type)
	{
		super(type);
	}
	
	
	@Override
	public String toCqlString() {
		
		SearchCriterion sc = getSearchCriterion();
		
		switch (sc)
		{
			case NOT_OPERATOR : return "not";
			case AND_OPERATOR : return "and";
			case OR_OPERATOR : return "or";
			
			
		}
		return "";
	}

	@Override
	public String toQueryString() {
		SearchCriterion sc = getSearchCriterion();
		switch (sc)
		{
			case NOT_OPERATOR : return "not";
			case AND_OPERATOR : return "and";
			case OR_OPERATOR : return "or";
			
			
		}
		return "";
	}

	@Override
	public void parseQueryStringContent(String content) {
		// TODO Auto-generated method stub

	}
	
	

	@Override
	public boolean isEmpty() {
		return false;
	}


	@Override
	public SearchCriterion getSearchCriterion() {
		return searchCriterion;
	}
	
	

}
