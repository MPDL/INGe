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
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bouncycastle.voms.VOMSAttribute;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase;
import de.mpg.escidoc.pubman.searchNew.criterions.enums.GenreSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.operators.LogicalOperator;
import de.mpg.escidoc.pubman.searchNew.criterions.operators.Parenthesis;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.ComponentVisibilitySearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.DegreeSearchCriterion;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.DegreeType;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.Genre;

public abstract class EnumListSearchCriterion<T extends Enum<T>> extends SearchCriterionBase{

	
	private Map<T, Boolean> enumMap = new LinkedHashMap<T, Boolean>();
	
	private Class<T> enumClass;
	
	public EnumListSearchCriterion(Class<T> clazz )
	{
		this.enumClass = clazz;
		initEnumMap();
	}
	
	
	public Map<T, Boolean> initEnumMap()
	{

		for(T v : enumClass.getEnumConstants())
		{
			enumMap.put(v, false);
		}
		return enumMap;
		
	}
	
	
	

	public List<T> getEnumList()
	{
		List<T> list = new ArrayList<T>();
		for(T t : enumMap.keySet())
		{
			list.add(t);
		}
		return list;
	}
	
	
	
	@Override
	public String toCqlString() {

		StringBuffer sb = new StringBuffer();
		boolean enumSelected = false;
		boolean enumDeselected = false;
		
		
	
		//List<SearchCriterionBase> returnList = new ArrayList<SearchCriterionBase>();
	
		//returnList.add(new Parenthesis(SearchCriterion.OPENING_PARENTHESIS));
		sb.append("(");
		
		int i=0;
		for(Entry<T, Boolean> entry : enumMap.entrySet())
		{
			if(entry.getValue() && i>0)
			{
				sb.append(" OR ");
				///returnList.add(new LogicalOperator(SearchCriterion.OR_OPERATOR));
			}
			
			if (entry.getValue())
			{

				
				enumSelected = true;
				//ComponentVisibilitySearchCriterion gc = new ComponentVisibilitySearchCriterion();
				//gc.setSearchString(entry.getKey().name().toLowerCase());
				sb.append(getSearchValue(entry.getKey()));
				i++;

	
			}
			else
			{
				enumDeselected = true;
				//allGenres = false;
			}
			
		}
		
		//returnList.add(new Parenthesis(SearchCriterion.CLOSING_PARENTHESIS));
		sb.append(")");
		
		if((enumSelected && enumDeselected))
		{
			return sb.toString();
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
		for(Entry<T, Boolean> entry : getEnumMap().entrySet())
		{
			if(entry.getValue())
			{
				if(i>0)
				{
					sb.append("|");
				}
				
				sb.append(entry.getKey().name());
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
		
		for(Entry<T, Boolean> e : getEnumMap().entrySet())
		{
			e.setValue(false);
		}
		
		
		//Split by '|', which have no backslash before and no other '|' after
		String[] enumParts = content.split("(?<!\\\\)\\|(?!\\|)");
		for(String part : enumParts)
		{
			
			T v = Enum.valueOf(enumClass, part);
			if(v==null)
			{
				throw new RuntimeException("Invalid visibility: " + part);
			}
			getEnumMap().put(v, true);
		}
		
	}

	/**
	 * List is empty if either all genres or degrees are selected or all are deselected
	 */
	@Override
	public boolean isEmpty() {
		
		boolean anySelected = false;
		boolean anyDeselected = false;
		for(Entry<T, Boolean> entry : getEnumMap().entrySet())
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
	
	


	public Map<T, Boolean> getEnumMap() {
		return enumMap;
	}

	public void setEnumMap(Map<T, Boolean> enumMap) {
		this.enumMap = enumMap;
	}
	
	public abstract String getSearchValue(T enumConstant);



}
