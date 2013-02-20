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
package de.mpg.escidoc.pubman.searchNew.criterions;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jasperreports.charts.util.SvgChartRendererFactory;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.searchNew.criterions.checkbox.EmbargoDateAvailableSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.checkbox.EventInvitationSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.component.FileAvailableSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.component.LocatorAvailableSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.dates.DateSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.genre.GenreListSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.operators.LogicalOperator;
import de.mpg.escidoc.pubman.searchNew.criterions.operators.Parenthesis;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.AnyFieldAndFulltextSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.AnyFieldSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.ClassificationSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.CollectionSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.ComponentContentCategory;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.ComponentVisibilitySearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.DegreeSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.EventTitleSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.FlexibleStandardSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.GenreSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.IdentifierSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.JournalSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.KeywordSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.LanguageSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.LocalTagSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.SourceSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.TitleSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.stringOrHiddenId.OrganizationSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.stringOrHiddenId.PersonSearchCriterion;
import de.mpg.escidoc.services.framework.PropertyReader;

public abstract class SearchCriterionBase {

	
	
	public enum SearchCriterion
	{
		TITLE (TitleSearchCriterion.class, DisplayType.STANDARD), 
		KEYWORD (KeywordSearchCriterion.class, DisplayType.STANDARD), 
		CLASSIFICATION (ClassificationSearchCriterion.class, null),
		ANY (AnyFieldSearchCriterion.class, DisplayType.STANDARD), 
		ANYFULLTEXT (AnyFieldAndFulltextSearchCriterion.class, DisplayType.STANDARD),  
		ANYPERSON (PersonSearchCriterion.class, DisplayType.PERSON),  
		
		//Person enum names should be the sam as role names in CreatorVO.CreatorRole
		AUTHOR (PersonSearchCriterion.class, DisplayType.PERSON),  
		EDITOR (PersonSearchCriterion.class, DisplayType.PERSON),  
		ADVISOR (PersonSearchCriterion.class, DisplayType.PERSON),  
		ARTIST (PersonSearchCriterion.class, DisplayType.PERSON),  
		COMMENTATOR (PersonSearchCriterion.class, DisplayType.PERSON),  
		CONTRIBUTOR (PersonSearchCriterion.class, DisplayType.PERSON),  
		ILLUSTRATOR (PersonSearchCriterion.class, DisplayType.PERSON),  
		PAINTER (PersonSearchCriterion.class, DisplayType.PERSON),  
		PHOTOGRAPHER (PersonSearchCriterion.class, DisplayType.PERSON),  
		TRANSCRIBER (PersonSearchCriterion.class, DisplayType.PERSON),  
		TRANSLATOR (PersonSearchCriterion.class, DisplayType.PERSON),  
		HONOREE (PersonSearchCriterion.class, DisplayType.PERSON),  
		INVENTOR (PersonSearchCriterion.class, DisplayType.PERSON),  
		APPLICANT (PersonSearchCriterion.class, DisplayType.PERSON),  
		ORGUNIT (OrganizationSearchCriterion.class, null),
		ANYDATE (DateSearchCriterion.class, DisplayType.DATE),  
		PUBLISHEDPRINT (DateSearchCriterion.class, DisplayType.DATE),
		PUBLISHED (DateSearchCriterion.class, DisplayType.DATE),
		ACCEPTED (DateSearchCriterion.class, DisplayType.DATE),
		SUBMITTED(DateSearchCriterion.class, DisplayType.DATE),
		MODIFIED (DateSearchCriterion.class, DisplayType.DATE),  
		CREATED (DateSearchCriterion.class, DisplayType.DATE),  
		LANG (LanguageSearchCriterion.class, null), 
		EVENT (EventTitleSearchCriterion.class, DisplayType.STANDARD),
		EVENT_STARTDATE(DateSearchCriterion.class, DisplayType.DATE),
		EVENT_ENDDATE(DateSearchCriterion.class, DisplayType.DATE),
		EVENT_INVITATION(EventInvitationSearchCriterion.class, null),
		SOURCE (SourceSearchCriterion.class, DisplayType.STANDARD), 
		JOURNAL (JournalSearchCriterion.class, null),
		LOCAL (LocalTagSearchCriterion.class, DisplayType.STANDARD), 
		IDENTIFIER (IdentifierSearchCriterion.class, DisplayType.STANDARD), 
		COLLECTION (CollectionSearchCriterion.class, null),
		
		GENRE_DEGREE_LIST(GenreListSearchCriterion.class, null),
		GENRE(GenreSearchCriterion.class, null),
		DEGREE(DegreeSearchCriterion.class, null),
		FILE_AVAILABLE(FileAvailableSearchCriterion.class, null),
		LOCATOR_AVAILABLE(LocatorAvailableSearchCriterion.class, null),
		EMBARGO_DATE_AVAILABLE(EmbargoDateAvailableSearchCriterion.class, null),
		COMPONENT_CONTENT_CATEGORY(ComponentContentCategory.class, null),	
		COMPONENT_VISIBILITY(ComponentVisibilitySearchCriterion.class, null),
		
		AND_OPERATOR(LogicalOperator.class, DisplayType.OPERATOR),
		OR_OPERATOR(LogicalOperator.class, DisplayType.OPERATOR),
		NOT_OPERATOR(LogicalOperator.class, DisplayType.OPERATOR),
		
		OPENING_PARENTHESIS(Parenthesis.class, DisplayType.PARENTHESIS),
		CLOSING_PARENTHESIS(Parenthesis.class, DisplayType.PARENTHESIS), 
		
		FLEXIBLE(FlexibleStandardSearchCriterion.class, null);
				
		
		
		
		private Class relatedClass;
		private DisplayType displayType;
		
		
		SearchCriterion(Class classToInstantiate, DisplayType dt)
		{
			this.relatedClass = classToInstantiate;
			this.displayType = dt;
		}


		public Class getRelatedClass() {
			return relatedClass;
		}


		public void setRelatedClass(Class relatedClass) {
			this.relatedClass = relatedClass;
		}


		public DisplayType getDisplayType() {
			return displayType;
		}


		public void setDisplayType(DisplayType displayType) {
			this.displayType = displayType;
		}

	}

	public enum DisplayType
	{
		STANDARD, DATE, PERSON, OPERATOR, PARENTHESIS;
	}
	
	

	
	
	
	
	private static Logger logger = Logger.getLogger(SearchCriterionBase.class);
	
	private static final String INDEX_CONTENT_MODEL = "escidoc.content-model.objid";
	private static final String INDEX_OBJECTTYPE = "escidoc.objecttype";
	
	private static final String PROPERTY_CONTENT_MODEL = "escidoc.framework_access.content-model.id.publication";
	
	
	private String queryValue;
	
	protected SearchCriterion searchCriterion;
	

	public abstract String toCqlString();
	
	public abstract String toQueryString();
	
	public abstract void parseQueryStringContent(String content);
	
	public abstract boolean isEmpty();
	
	private boolean parenthesisCanBeOpened;
	
	private boolean parenthesisCanBeClosed;
	
	private int level = 0;


	public SearchCriterionBase()
	{
		
	}
	
	public SearchCriterionBase(SearchCriterion type)
	{
		this.searchCriterion = type;
	}
	
	
	public void setSearchCriterion(SearchCriterion sc)
	{
		this.searchCriterion = sc;
	}
	
	public abstract SearchCriterion getSearchCriterion();
	

	public String getQueryValue() {
		return queryValue;
	}

	public void setQueryValue(String queryValue) {
		this.queryValue = queryValue;
	}
	
	
	
	
	
	
	
	public static SearchCriterionBase initSearchCriterion(SearchCriterion sc)
	{
			try {
			    Constructor ctor = sc.getRelatedClass().getDeclaredConstructor(SearchCriterion.class);
			    ctor.setAccessible(true);
				return (SearchCriterionBase)ctor.newInstance(sc);
				
			} catch (Exception e) 
			{
				logger.debug("No one-argument constructor with SearchCriterion found for " + sc.getRelatedClass(), e);
				//return search criterion with default constructor
				
			}
			    
			    	  
			try {
				return (SearchCriterionBase)sc.getRelatedClass().newInstance();
			} catch (Exception e) {
				logger.debug("Problem while instantiating class " + sc.getRelatedClass());
				
			} 
			
			return null;
	}
	
	protected static String escapeForCql(String escapeMe) {
	  	String result = escapeMe.replace( "<", "\\<" );
	  	result = result.replace( ">", "\\>" );
	  	result = result.replace( "+", "\\+" );
	  	result = result.replace( "-", "\\-" );
	  	result = result.replace( "&", "\\&" );
	  	result = result.replace( "%", "\\%" );
	  	result = result.replace( "|", "\\|" );
	  	result = result.replace( "(", "\\(" );
	  	result = result.replace( ")", "\\)" );
	  	result = result.replace( "[", "\\[" );
	  	result = result.replace( "]", "\\]" );
	  	result = result.replace( "^", "\\^" );
	  	result = result.replace( "~", "\\~" );
	  	result = result.replace( "!", "\\!" );
	  	result = result.replace( "{", "\\{" );
	  	result = result.replace( "}", "\\}" );
		result = result.replace( "\"", "\\\"" );
	  	return result;
	  }
	
	

	protected static String escapeForQueryString(String escapeMe) {
		String result = escapeMe.replace( "\\", "\\\\" );
		result = result.replace( "=", "\\=" );
	  	result = result.replace( "|", "\\|" );
	  	result = result.replace( "(", "\\(" );
	  	result = result.replace( ")", "\\)" );
		result = result.replace( "\"", "\\\"" );
	  	return result;
	  }
	
	protected static String unescapeForQueryString(String escapeMe) {
	  	String result = escapeMe.replace( "\\=", "=" );
	  	result = result.replace( "\\\"", "\"" );
	  	result = result.replace( "\\|", "|" );
	  	result = result.replace( "\\(", "(" );
	  	result = result.replace( "\\)", ")" );
		result = result.replace( "\\\\", "\\" );
	  	return result;
	  }
	
	
	/**
	 * Creates a cql string out of one or several search indexes and an search string. The search string is splitted into single words, except they are in quotes.
	 * The special characters of the search string parts are escaped.
	 * 
	 * Example:
	 * cqlIndexes={escidoc.title, escidoc.fulltext}
	 * searchString = book "john grisham"
	 * 
	 * Resulting cql string:
	 * escidoc.title=("book" and "john grisham") or escioc.fulltext=("book" and "john grisham")
	 * 
	 * @param cqlIndexes
	 * @param searchString
	 * @return the cql string or null, if no search string or indexes are given
	 */
	protected String baseCqlBuilder(String[] cqlIndexes, String searchString)
	{

		if(searchString!=null && !searchString.trim().isEmpty())
		{

			//split the search string into single words, except if they are in quotes
			List<String> splittedSearchStrings = new ArrayList<String>();
			List<String> splittedOperators = new ArrayList<String>();
			
			Pattern pattern = Pattern.compile("(?<=\\s|^)\"(.*?)\"(?=\\s|$)|(\\S+)");
			Matcher m = pattern.matcher(searchString);
			
			while(m.find())
			{
				String subSearchString = m.group();
				
				if(subSearchString!=null && !subSearchString.trim().isEmpty())
				{
					subSearchString = subSearchString.trim();

					//Remove quotes at beginning and end
					if(subSearchString.startsWith("\""))
					{
						subSearchString = subSearchString.substring(1, subSearchString.length());
					}
					
					if(subSearchString.endsWith("\""))
					{
						subSearchString = subSearchString.substring(0, subSearchString.length()-1);
					}
				}
				if(!subSearchString.trim().isEmpty())
				{
					splittedSearchStrings.add(subSearchString.trim());
				}
				
				
			}
			
			
			StringBuilder cqlStringBuilder = new StringBuilder();

			if(cqlIndexes.length > 1)
			{
				cqlStringBuilder.append("(");
			}
			
			for(int j=0; j< cqlIndexes.length; j++)
			{
				cqlStringBuilder.append(cqlIndexes[j]);
				cqlStringBuilder.append("=");
				
				if(splittedSearchStrings.size()>1)
				{
					cqlStringBuilder.append("(");
				}
				
				for(int i=0; i<splittedSearchStrings.size(); i++)
				{
					String subSearchString = splittedSearchStrings.get(i);
					cqlStringBuilder.append("\"");
					cqlStringBuilder.append(escapeForCql(subSearchString));
					cqlStringBuilder.append("\"");
					
					if(splittedSearchStrings.size() > i+1 )
					{
						if(splittedSearchStrings.get(i+1).matches("AND|OR|NOT"))
						{
							cqlStringBuilder.append(" " + splittedSearchStrings.get(i+1) +" ");
							i++;
						}
						else
						{
							cqlStringBuilder.append(" AND ");
						}
						
					}
					
				}
				if(splittedSearchStrings.size()>1)
				{
					cqlStringBuilder.append(")");
				}

				
				
				if(cqlIndexes.length > j+1)
				{
					cqlStringBuilder.append(" OR ");
				}
			}
			
			if(cqlIndexes.length > 1)
			{
				cqlStringBuilder.append(")");
			}

			return cqlStringBuilder.toString();
		}
		
		return null;
		
		
	}
	
	
	/**
	 * Creates a CQL query string out of a list of search criteria. Before, it removes empty search criterions. Adds parenthesis around every single search criterion object.
	 * @param criterionList
	 * @return
	 */
	public static String scListToCql(List<SearchCriterionBase> criterionList, boolean appendStandardCriterions)
	{
		
		List<SearchCriterionBase> removedList = removeEmptyFields(criterionList);
		
		
		String appendOperator = "AND";
		
		StringBuilder sb = new StringBuilder();
		for(int i=0; i< removedList.size(); i++)
		{
			
			SearchCriterionBase criterion = removedList.get(i);
			
			//if first in list is an operator, use it as concatenator to append standard criteria below, else use default "AND"
			if(i==0 &&  DisplayType.OPERATOR.equals(criterion.getSearchCriterion().getDisplayType()))
			{
				appendOperator = criterion.toCqlString();
			}
			else
			{
				String cql = criterion.toCqlString();
				if(cql!=null && !cql.trim().isEmpty())
				{
					
					
					
					if(!DisplayType.OPERATOR.equals(criterion.getSearchCriterion().getDisplayType()) && !DisplayType.PARENTHESIS.equals(criterion.getSearchCriterion().getDisplayType()))
					{
						sb.append("(");
					}
					
					sb.append(cql);
					
					if(!DisplayType.OPERATOR.equals(criterion.getSearchCriterion().getDisplayType()) && !DisplayType.PARENTHESIS.equals(criterion.getSearchCriterion().getDisplayType()))
					{
						sb.append(")");
					}
					
					sb.append(" ");
				}
			}
			
			
			
		}
		
		
		
		
		if(appendStandardCriterions)
		{
			try {
				
				
				
				String contentModelId = PropertyReader.getProperty(PROPERTY_CONTENT_MODEL);
				String standardCriterions = INDEX_OBJECTTYPE + "=\"item\" and " + INDEX_CONTENT_MODEL + "=\"" + escapeForCql(contentModelId) + "\"";
				
				
				
				if(!sb.toString().isEmpty() )
				{
					
					standardCriterions = standardCriterions + " " + appendOperator + " " + sb.toString() + "";
				}
				return standardCriterions;
			} catch (Exception e) {
				logger.error("Could not read property " + PROPERTY_CONTENT_MODEL, e);
			}
		}
		
		return sb.toString();
		
	}
	
	
	
	
	public static String scListToQueryString(List<SearchCriterionBase> criterionList)
	{
		
		List<SearchCriterionBase> removedList = removeEmptyFields(criterionList);
		
		StringBuilder sb = new StringBuilder();
		for(int i=0; i< removedList.size(); i++)
		{
			
			SearchCriterionBase criterion = removedList.get(i);
		
			String query = criterion.toQueryString();
			if(query!=null)
			{

				sb.append(query);
				
				sb.append(" ");
			}
		
			
			
			
		}

		return sb.toString();
		
	}
	
	public static List<SearchCriterionBase> queryStringToScList(String queryString) throws RuntimeException
	{
		List<SearchCriterionBase> scList = new ArrayList<SearchCriterionBase>();
		
		
		StringReader sr = new StringReader(queryString);
		
		int ch;
		try {
			
			StringBuffer substringBuffer = new StringBuffer();
			SearchCriterion currentSearchCriterionName = null;
			SearchCriterionBase currentSearchCriterion = null;
			Stack<Parenthesis> parenthesisStack = new Stack<Parenthesis>();
			while((ch=sr.read()) != -1)
			{

				
				if(ch=='=' && substringBuffer.length()>0 && substringBuffer.charAt(substringBuffer.length()-1)!='\\')
				{
					currentSearchCriterionName = SearchCriterion.valueOf(substringBuffer.toString());
					
					if(sr.read()!='"')
					{
						throw new RuntimeException("Search criterion name must be followed by an '=' and '\"' ");
					}
					
					int contentChar;
					StringBuffer contentBuffer = new StringBuffer();
					while((contentChar = sr.read()) != -1)
					{
						
						if(contentChar == '"' &&  contentBuffer.length()>0 && contentBuffer.charAt(contentBuffer.length()-1)!='\\')
						{
							//end of content
							currentSearchCriterion = initSearchCriterion(currentSearchCriterionName);
							try {
								currentSearchCriterion.parseQueryStringContent(contentBuffer.toString());
							} catch (Exception e) {
								throw new RuntimeException("Error while parsing query string content: " +  contentBuffer.toString(), e);
							}
							scList.add(currentSearchCriterion);
							break;
						}
						else
						{
							contentBuffer.append((char)contentChar);
						}
						
					}
					
					//empty the buffer
					substringBuffer.setLength(0);
				}
				
				
				//Logical Operators
				else if(ch==' ')
				{
					if(substringBuffer.length()>0)
					{
						if(substringBuffer.toString().toLowerCase().equals("and"))
						{
							scList.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));
							substringBuffer.setLength(0);
						}
						else if(substringBuffer.toString().toLowerCase().equals("or"))
						{
							scList.add(new LogicalOperator(SearchCriterion.OR_OPERATOR));
							substringBuffer.setLength(0);
						}
						else if(substringBuffer.toString().toLowerCase().equals("not"))
						{
							scList.add(new LogicalOperator(SearchCriterion.NOT_OPERATOR));
							substringBuffer.setLength(0);
						}
						

					}
					
				}
				
				else if(ch=='(')
				{
					Parenthesis p = new Parenthesis(SearchCriterion.OPENING_PARENTHESIS);
					scList.add(p);
					parenthesisStack.push(p);
				}
				
				else if(ch==')')
				{
					Parenthesis p = new Parenthesis(SearchCriterion.CLOSING_PARENTHESIS);
					scList.add(p);
					Parenthesis openingParenthesis;
					try {
						openingParenthesis = parenthesisStack.pop();
						openingParenthesis.setPartnerParenthesis(p);
						p.setPartnerParenthesis(openingParenthesis);
					} catch (EmptyStackException e) {
						throw new RuntimeException("Parenthesis in query string are not balanced");
					}
					
				}
				
				else
				{
					substringBuffer.append((char)ch);
				}

			}

		} catch (Exception e) {
			throw new RuntimeException("Error while parsing query string", e);
		}
		
		return scList;
	}
	
	
	public static String queryStringToCqlString(String query, boolean appendStandardCqlCriteria)
	{
		List<SearchCriterionBase> scList = queryStringToScList(query);
		return scListToCql(scList, appendStandardCqlCriteria);
	}
	
	

	public static List<SearchCriterionBase> removeEmptyFields(List<SearchCriterionBase> criterionList)
	{
		if(criterionList==null)
		{
			return new ArrayList<SearchCriterionBase>();
		}
		else
		{
			
		
			List<SearchCriterionBase> copyForRemoval = new ArrayList<SearchCriterionBase>(criterionList);
			List<SearchCriterionBase> copyForIteration = new ArrayList<SearchCriterionBase>(criterionList);
			//Collections.copy(copy, criterionList);
			
			for(SearchCriterionBase sc : copyForIteration)
			{
				if(sc.isEmpty())
				{
					removeSearchCriterionWithOperator(copyForRemoval, sc);
					logger.info("Remove " + sc);
					
				}
			}
			
			//if first in list is an operator except "NOT", remove it
			if(copyForRemoval.size()>0 && DisplayType.OPERATOR.equals(copyForRemoval.get(0).getSearchCriterion().getDisplayType()) && 
					!SearchCriterion.NOT_OPERATOR.equals(copyForRemoval.get(0).getSearchCriterion()))
			{
				copyForRemoval.remove(0);
			}
			return copyForRemoval;
		}
	}
	
	public static void removeSearchCriterionWithOperator(List<SearchCriterionBase> criterionList, SearchCriterionBase criterion)
	{
		
		int position = criterionList.indexOf(criterion);
		//try to delete
		boolean deleteBefore = true;
		if(position == 0)
		{
			deleteBefore = false;
		}
		else if(position - 1 >= 0)
		{
			SearchCriterionBase scBefore = criterionList.get(position-1);
			
			deleteBefore = !scBefore.getSearchCriterion().equals(SearchCriterion.OPENING_PARENTHESIS);
			
			if(!deleteBefore && position+1 < criterionList.size())
			{ 	SearchCriterionBase scAfter = criterionList.get(position+1);
				deleteBefore= scAfter.getSearchCriterion().equals(SearchCriterion.CLOSING_PARENTHESIS);
			}
		}
		

			
		if(deleteBefore)
		{
			for (int i = position; i>=0; i--)
			{
				SearchCriterion sci = criterionList.get(i).getSearchCriterion();
				if(DisplayType.OPERATOR.equals(sci.getDisplayType()))
				{
					criterionList.remove(position);
					criterionList.remove(i);
					break;
					
				}
			}
		}
		else
		{
			//delete logical operator after
			for (int i = position; i<criterionList.size(); i++)
			{
				SearchCriterion sci = criterionList.get(i).getSearchCriterion();
				if(DisplayType.OPERATOR.equals(sci.getDisplayType()))
				{
					criterionList.remove(i);
					criterionList.remove(position);
					break;
					
				}
			}
		}
		
		//if none was found, just remove the criteria itself
		criterionList.remove(criterion);
		
		
		List<SearchCriterionBase> parenthesisToRemove = new ArrayList<SearchCriterionBase>();
		//now remove empty parenthesis
		for(int i=0; i<criterionList.size();i++)
		{
			SearchCriterionBase sc = criterionList.get(i);
			if(SearchCriterion.OPENING_PARENTHESIS.equals(sc.getSearchCriterion()))
			{
				if(i+1 < criterionList.size())
				{
					SearchCriterionBase next = criterionList.get(i+1);
					if(SearchCriterion.CLOSING_PARENTHESIS.equals(next.getSearchCriterion()))
					{
						parenthesisToRemove.add(sc);
						parenthesisToRemove.add(next);
					}
				}
				
			}
		}
		
		criterionList.removeAll(parenthesisToRemove);
		
	}
	
	
	public static void updateParenthesisStatus(List<SearchCriterionBase> criterionList)
	{
		SearchCriterionBase lastOpenedParenthesis;
		for(SearchCriterionBase sc : criterionList)
		{
			if(SearchCriterion.OPENING_PARENTHESIS.equals(sc.getSearchCriterion()))
			{
				
			}
			else if(SearchCriterion.CLOSING_PARENTHESIS.equals(sc.getSearchCriterion()))
			{
				
			}
			else
			{
				
			}
		}
		
	}

	public boolean isParenthesisCanBeOpened() {
		return parenthesisCanBeOpened;
	}

	public void setParenthesisCanBeOpened(boolean parenthesisCanBeOpened) {
		this.parenthesisCanBeOpened = parenthesisCanBeOpened;
	}

	public boolean isParenthesisCanBeClosed() {
		return parenthesisCanBeClosed;
	}

	public void setParenthesisCanBeClosed(boolean parenthesisCanBeClosed) {
		this.parenthesisCanBeClosed = parenthesisCanBeClosed;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	
	
	
	
	
}

