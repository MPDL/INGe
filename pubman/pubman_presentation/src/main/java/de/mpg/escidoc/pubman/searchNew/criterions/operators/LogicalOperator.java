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
			case NOT_OPERATOR : return "NOT";
			case AND_OPERATOR : return "AND";
			case OR_OPERATOR : return "OR";
			
			
		}
		return "";
	}

	@Override
	public String toQueryString() {
		SearchCriterion sc = getSearchCriterion();
		switch (sc)
		{
			case NOT_OPERATOR : return "NOT";
			case AND_OPERATOR : return "AND";
			case OR_OPERATOR : return "OR";
			
			
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
