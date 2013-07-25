/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 
package de.mpg.escidoc.pubman.searchNew.criterions.stringOrHiddenId;

import de.mpg.escidoc.pubman.searchNew.SearchParseException;
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
	public String toCqlString()  throws SearchParseException{
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

	/*
	@Override
	public SearchCriterion getSearchCriterion() {
		return searchCriterion;
		
	}
	*/



	

}
