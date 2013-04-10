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
		//return (searchString==null || searchString.trim().isEmpty()) && (hiddenId==null || hiddenId.trim().isEmpty());
		return (searchString==null || searchString.trim().isEmpty());
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
