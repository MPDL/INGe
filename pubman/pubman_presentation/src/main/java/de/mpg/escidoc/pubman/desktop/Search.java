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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.desktop;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion;
import de.mpg.escidoc.services.search.query.MetadataSearchQuery;


public class Search extends FacesBean
{
    private static final String PROPERTY_CONTENT_MODEL = "escidoc.framework_access.content-model.id.publication";
    
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(Search.class);

    private String searchString;
    private boolean includeFiles;
    
  
    public String startSearch()
    {

        String searchString = getSearchString();
        boolean includeFiles = getIncludeFiles();
        
        // check if the searchString contains useful data
        if( searchString.trim().equals("") ) {
            error(getMessage("search_NoCriteria"));
            return "";
        }
        
        try
        {
            getExternalContext().redirect("SearchResultListPage.jsp?cql="+generateUrlEncodedCQLRequest(searchString, includeFiles));
        }
        catch(Exception e ) 
        {
            logger.error("Technical problem while retrieving the search results", e);
            error(getMessage("search_TechnicalError"));
            return "";
        }
  
        return "";           
    }

    private String generateUrlEncodedCQLRequest(String searchString, boolean includeFiles)
    {
        String cql = "";
        try
        {
            ArrayList<MetadataSearchCriterion> criteria = new ArrayList<MetadataSearchCriterion>();
                        
            if( includeFiles == true ) {
                criteria.add( new MetadataSearchCriterion( MetadataSearchCriterion.CriterionType.ANY_INCLUDE, 
                        searchString ) );
                criteria.add( new MetadataSearchCriterion( MetadataSearchCriterion.CriterionType.IDENTIFIER, 
                        searchString, MetadataSearchCriterion.LogicalOperator.OR ) );
            }
            else {
                criteria.add( new MetadataSearchCriterion( MetadataSearchCriterion.CriterionType.ANY, 
                        searchString ) );
                criteria.add( new MetadataSearchCriterion( MetadataSearchCriterion.CriterionType.IDENTIFIER, 
                        searchString, MetadataSearchCriterion.LogicalOperator.OR ) );
            }
            criteria.add( new MetadataSearchCriterion( MetadataSearchCriterion.CriterionType.CONTEXT_OBJECTID, 
                    searchString, MetadataSearchCriterion.LogicalOperator.NOT ) );
            criteria.add( new MetadataSearchCriterion( MetadataSearchCriterion.CriterionType.CREATED_BY_OBJECTID, 
                    searchString, MetadataSearchCriterion.LogicalOperator.NOT ) );
            criteria.add( new MetadataSearchCriterion( MetadataSearchCriterion.CriterionType.OBJECT_TYPE, 
                    "item", MetadataSearchCriterion.LogicalOperator.AND ) );
            
            ArrayList<String> contentTypes = new ArrayList<String>();
            String contentTypeIdPublication = PropertyReader.getProperty( PROPERTY_CONTENT_MODEL );
            contentTypes.add( contentTypeIdPublication );
            
            MetadataSearchQuery query = new MetadataSearchQuery( contentTypes, criteria );
            cql = URLEncoder.encode(query.getCqlQuery(),"UTF-8");
        }
        catch( de.mpg.escidoc.services.search.parser.ParseException e) 
        {
            logger.error("Search criteria includes some lexical error", e);
            error(getMessage("search_ParseError"));
            return "";
        }
        catch(Exception e ) 
        {
            logger.error("Technical problem while retrieving the search results", e);
            error(getMessage("search_TechnicalError"));
            return "";
        }
        return cql;
    }

    public String getOpenSearchRequest()
    {
        String requestDummy = "dummyTermToBeReplaced";
        String openSearchRequest = "SearchResultListPage.jsp?cql="+generateUrlEncodedCQLRequest(requestDummy, false);
        return openSearchRequest.replaceAll(requestDummy, "{searchTerms}");
        
    }

    public void setSearchString(String searchString)
    {
        this.searchString = searchString;
    }

    public String getSearchString()
    {
        return searchString;
    }

    public void setIncludeFiles(boolean includeFiles)
    {
        this.includeFiles = includeFiles;
    }

    public boolean getIncludeFiles()
    {
        return includeFiles;
    }
}
