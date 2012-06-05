package de.mpg.escidoc.pubman.searchNew.criterions.stringOrHiddenId;

import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase.DisplayType;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;

public class PersonSearchCriterion extends StringOrHiddenIdSearchCriterion {

	
	private static String PERSON_ROLE_INDEX = "escidoc.publication.creator.role";
	
	
	
	
	
	public PersonSearchCriterion(SearchCriterion role)
	{
		this.searchCriterion = role;
	}
	
	@Override
	public String[] getCqlIndexForHiddenId() {
		
		return new String[] {"escidoc.publication.creator.person.identifier"};
	}

	@Override
	public String[] getCqlIndexForSearchString() {
		return new String[] {"escidoc.publication.creator.person.compound.person-complete-name"};
	}

	

	
	@Override
	public String toCqlString() {
		String superQuery = super.toCqlString();
		
		if(SearchCriterion.ANYPERSON.equals(getSearchCriterion()))
		{
			return superQuery;
		}
		else if(superQuery!=null)
		{
			
			String roleUri = CreatorVO.CreatorRole.valueOf(getSearchCriterion().name()).getUri();
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			sb.append(superQuery);
			sb.append(" and ");
			sb.append(PERSON_ROLE_INDEX);
			sb.append("=\"");
			sb.append(escapeForCql(roleUri) + "\")");
			return  sb.toString();
		}
		return null;
		
		
	}

	@Override
	public SearchCriterion getSearchCriterion() {
		return searchCriterion;
		
	}



	

}
