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
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.pubman;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.editItem.EditItemSessionBean;
import de.mpg.escidoc.pubman.util.OrganizationVOPresentation;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.search.Search;
import de.mpg.escidoc.services.search.query.OrgUnitsSearchResult;
import de.mpg.escidoc.services.search.query.PlainCqlQuery;
import de.mpg.escidoc.services.search.query.SearchQuery;

/**
 * @author franke
 *
 */
public class OrganizationSuggest extends FacesBean
{

	Logger logger = Logger.getLogger(OrganizationSuggest.class);
	
	List<OrganizationVOPresentation> result = new ArrayList<OrganizationVOPresentation>();
	Search search;
	
	public OrganizationSuggest() throws Exception
	{
		// Get query from URL parameters
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> parameters = context.getExternalContext().getRequestParameterMap();
		String query = parameters.get("q"); 
		
		// Initialize search service
		try
        {
            InitialContext initialContext = new InitialContext();
            this.search = (Search) initialContext.lookup(Search.SERVICE_NAME);
        }
        catch (NamingException ne)
        {
            throw new RuntimeException("Search service not initialized", ne);
        }
        
        // Perform search request
        if (query != null)
        {
        	String queryString = "";
        	for (String snippet : query.split(" "))
        	{
        		if (!"".equals(queryString))
        		{
        			queryString += " and ";
        		}
        		queryString += "(escidoc.title=\"" + snippet + "*\")";
        	}
        	SearchQuery searchQuery = new PlainCqlQuery(queryString);
        	OrgUnitsSearchResult searchResult = this.search.searchForOrganizationalUnits(searchQuery);
        	for (AffiliationVO affiliationVO : searchResult.getResults())
        	{
        		List<AffiliationVO> initList = new ArrayList<AffiliationVO>();
        		initList.add(affiliationVO);
        		List<List<AffiliationVO>> pathList = getPaths(initList);
        		for (List<AffiliationVO> path : pathList)
        		{
        			OrganizationVOPresentation organizationVOPresentation = new OrganizationVOPresentation();
            		organizationVOPresentation.setIdentifier(affiliationVO.getReference().getObjectId());
            		
            		String city = affiliationVO.getDefaultMetadata().getCity();
            		String countryCode = affiliationVO.getDefaultMetadata().getCountryCode();
            		
            		String address = "";
            		if (city != null)
            		{
            			address += city;
            		}
            		if (city != null && countryCode != null)
            		{
            			address += ", ";
            		}
            		if (countryCode != null)
            		{
            			address += countryCode;
            		}
            		
            		// TODO: remove this if address is wanted
            		//address = "";
            		
            		organizationVOPresentation.setAddress(address);
            		String name = "";
            		for (AffiliationVO affVO : path)
            		{
            			
            			if (!"".equals(name))
            			{
            				name = name + ", ";
            			}
            			
            			name = name + affVO.getDefaultMetadata().getName();

            		}
            		organizationVOPresentation.setName(new TextVO(name));
            		EditItemSessionBean editItemSessionBean = (EditItemSessionBean) getSessionBean(EditItemSessionBean.class);
            		organizationVOPresentation.setBean(editItemSessionBean);
            		
            		result.add(organizationVOPresentation);
        		}
        		
        	}
        }
		
	}

	private List<List<AffiliationVO>> getPaths(List<AffiliationVO> currentPath) throws Exception
	{
		
		List<List<AffiliationVO>> result = new ArrayList<List<AffiliationVO>>();
		AffiliationVO affiliationVO = currentPath.get(currentPath.size() - 1);
		if (affiliationVO.getParentAffiliations().isEmpty())
		{
			result.add(currentPath);
		}
		else
		{
			for (AffiliationRO parent : affiliationVO.getParentAffiliations())
			{
				List<AffiliationVO> list = new ArrayList<AffiliationVO>();
				list.addAll(currentPath);
				AffiliationVO parentVO = getAffiliation(parent);
				list.add(parentVO);
				result.addAll(getPaths(list));
			}
		}
		return result;
	}

	private AffiliationVO getAffiliation(AffiliationRO affiliationRO) throws Exception
	{
		for (AffiliationVO element : ((ApplicationBean) getApplicationBean(ApplicationBean.class)).getOuList())
		{
			if (element.getReference().equals(affiliationRO))
			{
				return element;
			}
		}
		SearchQuery searchQuery = new PlainCqlQuery("(escidoc.objid=\"" + affiliationRO.getObjectId() + "\")");
    	OrgUnitsSearchResult searchResult = this.search.searchForOrganizationalUnits(searchQuery);
    	List<AffiliationVO> resultList = searchResult.getResults();
    	if (resultList.size() == 0)
    	{
    		logger.warn("'" + affiliationRO.getObjectId() + "' was declared as a parent ou but it was not found.");
    	}
    	else if (resultList.size() > 1)
    	{
    		logger.warn("Unexpectedly more than one ou with the id '" + affiliationRO.getObjectId() + "' was found.");
    	}
    	else
    	{
    		((ApplicationBean) getApplicationBean(ApplicationBean.class)).getOuList().add(resultList.get(0));
    		return resultList.get(0);
    	}
		return null;
	}

	public List<OrganizationVOPresentation> getResult()
	{
		return result;
	}

}
