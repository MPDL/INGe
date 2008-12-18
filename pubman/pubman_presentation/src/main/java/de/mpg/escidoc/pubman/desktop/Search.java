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

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.CommonSessionBean;
import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.appbase.InternationalizedImpl;
import de.mpg.escidoc.pubman.search.SearchResultList;
import de.mpg.escidoc.pubman.search.SearchResultListSessionBean;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.PubItemResultVO;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.ItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.interfaces.SearchResultElement;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.search.query.ItemContainerSearchResult;
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
            String cql = query.getCqlQuery();
            
            //redirect to SearchResultPage which processes the query
            getExternalContext().redirect("SearchResultListPage.jsp?cql="+URLEncoder.encode(cql,"UTF-8"));
            
            
        }
        catch (Exception e)
        {
            error("Error in search query!");
        }
        return "";
           
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
