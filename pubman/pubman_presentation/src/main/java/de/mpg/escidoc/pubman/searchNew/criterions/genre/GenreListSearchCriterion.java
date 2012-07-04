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
package de.mpg.escidoc.pubman.searchNew.criterions.genre;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase;
import de.mpg.escidoc.pubman.searchNew.criterions.operators.LogicalOperator;
import de.mpg.escidoc.pubman.searchNew.criterions.operators.Parenthesis;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.DegreeSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.GenreSearchCriterion;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.DegreeType;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.Genre;

public class GenreListSearchCriterion extends SearchCriterionBase{

	
	private Map<Genre, Boolean> genreMap = initGenreMap();
	
	private Map<DegreeType, Boolean> degreeMap = initDegreeMap();
	
	
	public Map<Genre, Boolean> initGenreMap()
	{
		
		//first create a map with genre as key and the label as value
		Map<Genre, String>genreLabelMap = new LinkedHashMap<Genre, String>();
		InternationalizationHelper iHelper = (InternationalizationHelper)FacesBean.getSessionBean(InternationalizationHelper.class);
		for(Genre g : Genre.values())
		{
			
			genreLabelMap.put(g, iHelper.getLabel("ENUM_GENRE_" + g.name()));
		}
		
		
		//Then create a list with the map entries and sort the list by the label 
		List<Map.Entry<Genre, String>> sortedGenreList = new LinkedList<Map.Entry<Genre, String>>( genreLabelMap.entrySet() );
	        Collections.sort( sortedGenreList, new Comparator<Map.Entry<Genre, String>>()
	        {
	            public int compare( Map.Entry<Genre, String> o1, Map.Entry<Genre, String> o2 )
	            {
	                return (o1.getValue()).compareTo( o2.getValue() );
	            }
	        } );

		
	    //now fill the genre map with the ordered genres    
		genreMap = new LinkedHashMap<Genre, Boolean>();
		for(Entry<Genre, String>  entry : sortedGenreList)
		{
			genreMap.put(entry.getKey(), false);
		}
		return genreMap;
	}
	
	private Map<DegreeType, Boolean> initDegreeMap() {
		
		degreeMap = new LinkedHashMap<DegreeType, Boolean>();
		for(DegreeType dt : DegreeType.values())
		{
			degreeMap.put(dt, false);
		}
		return degreeMap;
	}
	
	
	
	public Genre[] getGenreList()
	{
		Genre[] genreArray = new Genre[0];
		return genreMap.keySet().toArray(genreArray);
	}
	
	public DegreeType[] getDegreeList()
	{
		DegreeType[] degreeArray = new DegreeType[0];
		return degreeMap.keySet().toArray(degreeArray);
	}
	
	
	@Override
	public String toCqlString() {
		return scListToCql(getGenreSearchCriterions(), false);
	}

	@Override
	public String toQueryString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getSearchCriterion()+"=\"");
		
		boolean allGenres = true;
		boolean allDegrees = true;
		
		int i=0;
		for(Entry<Genre, Boolean> entry : genreMap.entrySet())
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
				allGenres = false;
			}
		}
		
		
		
		
		if(genreMap.get(Genre.THESIS))
		{
			sb.append("||");
			int j = 0;
			for(Entry<DegreeType, Boolean> entry : degreeMap.entrySet())
			{
				if(entry.getValue())
				{
					if(j>0)
					{
						sb.append("|");
					}
					
					sb.append(entry.getKey().name());
					j++;
				}
				else
				{
					allDegrees = false;
				}
			}
			
			
		}
		
		sb.append("\"");
		if(!allGenres || !allDegrees)
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
		//Split by '||', which have no backslash before
		String[] genreDegreeParts = content.split("(?<!\\\\)\\|\\|");
		
		
			
		for(Entry<Genre, Boolean> e : genreMap.entrySet())
		{
			e.setValue(false);
		}
		
		for(Entry<DegreeType, Boolean> e :degreeMap.entrySet())
		{
			e.setValue(false);
		}
		
		//Split by '|', which have no backslash before and no other '|' after
		String[] genreParts = genreDegreeParts[0].split("(?<!\\\\)\\|(?!\\|)");
		for(String genre : genreParts)
		{
			Genre g = Genre.valueOf(genre);
			if(g==null)
			{
				throw new RuntimeException("Invalid genre: " + genre);
			}
			genreMap.put(g, true);
		}
		
		if(genreDegreeParts.length>1 && !genreDegreeParts[1].trim().isEmpty())
		{
			String[] degreeParts = genreDegreeParts[1].split("(?<!\\\\)\\|(?!\\|)");
			for(String degree : degreeParts)
			{
				DegreeType d= DegreeType.valueOf(degree);
				if(d==null)
				{
					throw new RuntimeException("Invalid degree type: " + degree);
				}
				degreeMap.put(d, true);
			}
		}
			
			
			
		

	}

	/**
	 * List is empty if either all genres or degrees are selected or all are deselected
	 */
	@Override
	public boolean isEmpty() {
		
		boolean genreOrDegreeSelected = false;
		boolean genreOrDegreeDeselected = false;
		for(Entry<Genre, Boolean> entry : genreMap.entrySet())
		{
			if(entry.getValue())
			{
				genreOrDegreeSelected = true;
			}
			else
			{
				genreOrDegreeDeselected = true;
			}
		}
		
		if(genreMap.get(Genre.THESIS))
		{
			for(Entry<DegreeType, Boolean> entry : degreeMap.entrySet())
			{
				if(entry.getValue())
				{
					genreOrDegreeSelected = true;
				}
				else
				{
					genreOrDegreeDeselected = true;
				}
			}
		}
		
		
		return !(genreOrDegreeSelected && genreOrDegreeDeselected);
	}
	
	
	
	public List<SearchCriterionBase> getGenreSearchCriterions()
	{
		
		boolean genreSelected = false;
		boolean genreDeselected = false;
		
		boolean degreeSelected = false;
		boolean degreeDeselected = false;
		
//		boolean allGenres = true;
		
		
		//boolean allDegrees = true;
	
		List<SearchCriterionBase> returnList = new ArrayList<SearchCriterionBase>();
		List<SearchCriterionBase> degreeCriterionsList = new ArrayList<SearchCriterionBase>();
		returnList.add(new Parenthesis(SearchCriterion.OPENING_PARENTHESIS));
		int i=0;
		for(Entry<Genre, Boolean> entry : genreMap.entrySet())
		{
			if(entry.getValue() && i>0)
			{
				returnList.add(new LogicalOperator(SearchCriterion.OR_OPERATOR));
			}
			
			if (entry.getValue())
			{

				
				
				if(Genre.THESIS.equals(entry.getKey()))
				{

					degreeCriterionsList.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));
					degreeCriterionsList.add(new Parenthesis(SearchCriterion.OPENING_PARENTHESIS));

					int j = 0;
					for(Entry<DegreeType, Boolean> degreeEntry : degreeMap.entrySet())
					{
						if(degreeEntry.getValue() && j>0)
						{
							degreeCriterionsList.add(new LogicalOperator(SearchCriterion.OR_OPERATOR));
						}
						
						if(degreeEntry.getValue())
						{
							degreeSelected = true;
							DegreeSearchCriterion dsc = new DegreeSearchCriterion();
							dsc.setSearchString(degreeEntry.getKey().getUri());
							degreeCriterionsList.add(dsc);
							j++;
						}
						else
						{
							degreeDeselected = true;
						}
					}
					degreeCriterionsList.add(new Parenthesis(SearchCriterion.CLOSING_PARENTHESIS));
					
					
					
					
				}
				
				
				
				if(Genre.THESIS.equals(entry.getKey()) && (degreeSelected && degreeDeselected))
				{
					returnList.add(new Parenthesis(SearchCriterion.OPENING_PARENTHESIS));
					
					
				}
				
				genreSelected = true;
				GenreSearchCriterion gc = new GenreSearchCriterion();
				gc.setSearchString(entry.getKey().getUri());
				returnList.add(gc);
				i++;
				
				
				if(Genre.THESIS.equals(entry.getKey()) && (degreeSelected && degreeDeselected))
				{
					returnList.addAll(degreeCriterionsList);
					returnList.add(new Parenthesis(SearchCriterion.CLOSING_PARENTHESIS));
					
					
				}
	
			}
			else
			{
				genreDeselected = true;
				//allGenres = false;
			}
			
		}
		
		returnList.add(new Parenthesis(SearchCriterion.CLOSING_PARENTHESIS));
		
		
		if((genreSelected && genreDeselected) || (degreeSelected && degreeDeselected))
		{
			return returnList;
		}
		else 
		{
			return null;
		}
	}
	
	public Map<Genre, Boolean> getGenreMap() {
		return genreMap;
	}


	public void setGenreMap(Map<Genre, Boolean> genreMap) {
		this.genreMap = genreMap;
	}
	

	public Map<DegreeType, Boolean> getDegreeMap() {
		return degreeMap;
	}

	public void setDegreeMap(Map<DegreeType, Boolean> degreeMap) {
		this.degreeMap = degreeMap;
	}

	@Override
	public SearchCriterion getSearchCriterion() {
		return SearchCriterion.GENRE_DEGREE_LIST;
	}


}
