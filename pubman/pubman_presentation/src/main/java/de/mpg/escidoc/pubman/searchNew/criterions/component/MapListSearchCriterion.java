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
package de.mpg.escidoc.pubman.searchNew.criterions.component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.mpg.escidoc.pubman.searchNew.SearchParseException;
import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase;
import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase.SearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.operators.LogicalOperator;
import de.mpg.escidoc.pubman.searchNew.criterions.operators.Parenthesis;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.FlexibleStandardSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.StandardSearchCriterion;

public abstract class MapListSearchCriterion extends SearchCriterionBase{

	
	private Map<String, Boolean> enumMap = new LinkedHashMap<String, Boolean>();
	
	private Map<String, String> valueMap;
	

	
	public MapListSearchCriterion(Map<String, String>  m)
	{
		this.valueMap = m;
		initEnumMap();
	}
	
	
	public Map<String, Boolean> initEnumMap()
	{

		for(String v : valueMap.keySet())
		{
			enumMap.put(v, false);
		}
		return enumMap;
		
	}
	
	
	

	public List<String> getEnumList()
	{
		List<String> list = new ArrayList<String>();
		for(String t : enumMap.keySet())
		{
			list.add(t);
		}
		return list;
	}
	
	
	
	@Override
	public String toCqlString()  throws SearchParseException{

		//StringBuffer sb = new StringBuffer();
		boolean enumSelected = false;
		boolean enumDeselected = false;
		
		
	
		List<SearchCriterionBase> returnList = new ArrayList<SearchCriterionBase>();
	
		returnList.add(new Parenthesis(SearchCriterion.OPENING_PARENTHESIS));
		//sb.append("(");
		
		int i=0;
		for(Entry<String, Boolean> entry : enumMap.entrySet())
		{
			if(entry.getValue() && i>0)
			{
				//sb.append(" OR ");
				returnList.add(new LogicalOperator(SearchCriterion.OR_OPERATOR));
			}
			
			if (entry.getValue())
			{

				
				enumSelected = true;
				
				SearchCriterionBase flexSc = new FlexibleStandardSearchCriterion(getCqlIndexes(), valueMap.get(entry.getKey()));
				
				//gc.setSearchString(entry.getKey().name().toLowerCase());
				returnList.add(flexSc);
				//sb.append(valueMap.get(entry.getKey()));
				i++;

	
			}
			else
			{
				enumDeselected = true;
				//allGenres = false;
			}
			
		}
		
		returnList.add(new Parenthesis(SearchCriterion.CLOSING_PARENTHESIS));
		//sb.append(")");
		
		if((enumSelected && enumDeselected))
		{
			return scListToCql(returnList, false);
		}
		else 
		{
			return null;
		}
	}

	@Override
	public String toQueryString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getSearchCriterion()+"=\"");
		
		boolean allChecked = true;
		
		
		int i=0;
		for(Entry<String, Boolean> entry : getEnumMap().entrySet())
		{
			if(entry.getValue())
			{
				if(i>0)
				{
					sb.append("|");
				}
				
				sb.append(entry.getKey());
				i++;
			}
			else
			{
				allChecked = false;
			}
		}
		

		sb.append("\"");
		if(!allChecked)
		{
			return sb.toString();
		}
		else
		{
			return null;
		}
		
		
		
	}

	@Override
	public void parseQueryStringContent(String content) {
		
		for(Entry<String, Boolean> e : getEnumMap().entrySet())
		{
			e.setValue(false);
		}
		
		
		//Split by '|', which have no backslash before and no other '|' after
		String[] enumParts = content.split("(?<!\\\\)\\|(?!\\|)");
		for(String part : enumParts)
		{
			/*
			T v = Enum.valueOf(enumClass, part);
			if(v==null)
			{
				throw new RuntimeException("Invalid visibility: " + part);
			}
			*/
			getEnumMap().put(part, true);
		}
		
	}

	/**
	 * List is empty if either all genres or degrees are selected or all are deselected
	 */
	@Override
	public boolean isEmpty() {
		
		boolean anySelected = false;
		boolean anyDeselected = false;
		for(Entry<String, Boolean> entry : getEnumMap().entrySet())
		{
			if(entry.getValue())
			{
				anySelected = true;
			}
			else
			{
				anyDeselected = true;
			}
		}
		
		
		
		
		return !(anySelected && anyDeselected);
	}
	
	


	public Map<String, Boolean> getEnumMap() {
		return enumMap;
	}

	public void setEnumMap(Map<String, Boolean> enumMap) {
		this.enumMap = enumMap;
	}
	


	public abstract String[] getCqlIndexes();


}
