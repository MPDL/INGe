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

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.search.bean.criterion.OrganizationCriterion;
import de.mpg.escidoc.pubman.searchNew.SearchParseException;
import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase;
import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase.DisplayType;
import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase.SearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.operators.LogicalOperator;
import de.mpg.escidoc.pubman.searchNew.criterions.operators.Parenthesis;
import de.mpg.escidoc.pubman.util.AffiliationVOPresentation;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.Genre;


public class OrganizationSearchCriterion extends
		StringOrHiddenIdSearchCriterion {
	
	//private Logger logger = Logger.getLogger(OrganizationSearchCriterion.class);
	
	private boolean includePredecessorsAndSuccessors;

	@Override
	public String[] getCqlIndexForHiddenId() {
		return new String[] {"escidoc.any-organization-pids"};
	}

	@Override
	public String[] getCqlIndexForSearchString() {
		return new String[] {"escidoc.any-organizations"};
	}

	/*
	@Override
	public SearchCriterion getSearchCriterion() {
		return SearchCriterion.ORGUNIT;
	}
	*/

	
	
	
	
	@Override
	public String toCqlString()  throws SearchParseException{
		if(!includePredecessorsAndSuccessors)
		{
			return super.toCqlString();
		}
		else
		{

			try {
				List<SearchCriterionBase> scList = new ArrayList<SearchCriterionBase>();
				int i=0;
				scList.add(new Parenthesis(SearchCriterion.OPENING_PARENTHESIS));
				for(AffiliationVO aff : retrievePredecessorsAndSuccessors(getHiddenId()))
				{
					if(i>0)
					{
						scList.add(new LogicalOperator(SearchCriterion.OR_OPERATOR));
					}
					
					OrganizationSearchCriterion ous = new OrganizationSearchCriterion();
					ous.setSearchString(aff.getDefaultMetadata().getName());
					ous.setHiddenId(aff.getReference().getObjectId());
					scList.add(ous);
					i++;
				}
				scList.add(new Parenthesis(SearchCriterion.CLOSING_PARENTHESIS));

				return scListToCql(scList, false);
			} catch (Exception e) {
				System.out.println("Error while retrieving affiliation from id" + e + ": " + e.getMessage());
		        //logger.error("Error while retrieving affiliation from id", e);
		        return super.toCqlString();
			}
		}
	}
	
	

	@Override
	public String toQueryString() {
		if(!includePredecessorsAndSuccessors)
		{
			 return super.toQueryString();
		}
		else
		{
			return getSearchCriterion().name() + "=\"" + escapeForQueryString(getSearchString()) + "|" + escapeForQueryString(getHiddenId()) + "|" + "includePresSuccs" + "\""; 
		}
		
		
	}

	@Override
	public void parseQueryStringContent(String content) {
		//Split by '|', which have no backslash
		String[] parts = content.split("(?<!\\\\)\\|");
		
		this.setSearchString(unescapeForQueryString(parts[0]));
		if(parts.length>1)
		{
			this.setHiddenId(unescapeForQueryString(parts[1]));
		}
		
		if(parts.length>2)
		{
			
			if(parts[2].equals("includePresSuccs"))
			{
				includePredecessorsAndSuccessors = true;
			}
			
		
		}
	}
	
	
	
	
	
	
	public boolean isIncludePredecessorsAndSuccessors() {
		return includePredecessorsAndSuccessors;
	}

	public void setIncludePredecessorsAndSuccessors(
			boolean includePredecessorsAndSuccessors) {
		this.includePredecessorsAndSuccessors = includePredecessorsAndSuccessors;
	}
	
	
	private List<AffiliationVO> retrievePredecessorsAndSuccessors(String id) throws Exception
	{
		
		List<AffiliationVO> allAffs = new ArrayList<AffiliationVO>();
		
		
        AffiliationVO affiliation = ItemControllerSessionBean.retrieveAffiliation(getHiddenId());
        allAffs.add(affiliation);

        AffiliationVOPresentation affiliationPres = new AffiliationVOPresentation(affiliation);

        List<AffiliationVO> sucessorsVO = affiliationPres.getSuccessors();
        
        for (AffiliationVO affiliationVO : sucessorsVO)
        {
            allAffs.add(affiliationVO);
        }
        
        List<AffiliationVO> predecessorsVO = affiliationPres.getPredecessors();
        
        for (AffiliationVO affiliationVO : predecessorsVO)
        {
            allAffs.add(affiliationVO);
        }
		return allAffs;
		
		
	}
	


}
