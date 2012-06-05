package de.mpg.escidoc.pubman.searchNew.criterions.operators;

import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase;

public class Parenthesis extends SearchCriterionBase {

	private Parenthesis partnerParenthesis;
	
	
	public Parenthesis(SearchCriterion type)
	{
		super(type);
		
	}
	
	@Override
	public String toCqlString() {
		switch (getSearchCriterion())
		{
			case OPENING_PARENTHESIS : return "(";
			case CLOSING_PARENTHESIS : return ")";
		}
		return "";
	}

	@Override
	public String toQueryString() {
		switch (getSearchCriterion())
		{
			case OPENING_PARENTHESIS : return "(";
			case CLOSING_PARENTHESIS : return ")";
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

	public Parenthesis getPartnerParenthesis() {
		return partnerParenthesis;
	}

	public void setPartnerParenthesis(Parenthesis partnerParenthesis) {
		this.partnerParenthesis = partnerParenthesis;
	}

	@Override
	public SearchCriterion getSearchCriterion() {
		return searchCriterion;
	}
	
	


}
