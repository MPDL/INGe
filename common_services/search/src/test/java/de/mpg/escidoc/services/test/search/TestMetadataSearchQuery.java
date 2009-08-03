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
* Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 
package de.mpg.escidoc.services.test.search;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import org.junit.Test;

import de.mpg.escidoc.services.search.query.MetadataDateSearchCriterion;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion;
import de.mpg.escidoc.services.search.query.MetadataSearchQuery;
import de.mpg.escidoc.services.search.query.SearchDate;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.CriterionType;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.LogicalOperator;

/**
 * 
 * Unit testing class for MetadataSearchQuery.
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class TestMetadataSearchQuery
{
    private Logger logger = Logger.getLogger(TestMetadataSearchQuery.class);
       
    @Test
    public void testBuildSimpleMetadataQuery() throws Exception
    {
        logger.info("Testing simple metadata search query transformation");
        
        ArrayList<String> contentTypes = new ArrayList<String>();
        contentTypes.add("escidoc:persistent4");
        MetadataSearchQuery msq = new MetadataSearchQuery(contentTypes);
        msq.addCriterion(new MetadataSearchCriterion(CriterionType.TITLE, "test"));
        String query = msq.getCqlQuery();
        // ( escidoc.any-title=\"test\" )  and  ( escidoc.content-model.objid=\"escidoc:persistent4\" ) 
        String expected = "( ( escidoc.any-title=\"test\" ) ) and  ( escidoc.content-model.objid=\"escidoc:persistent4\" ) ";
        assertNotNull(query);
        assertEquals(expected, query);
        
    }
    
    @Test
    public void testBuildMetadataQueryWithLogicalOperators() throws Exception
    {
        logger.info("Testing simple metadata search query transformation with logical operator");
        
        ArrayList<String> contentTypes = new ArrayList<String>();
        contentTypes.add("escidoc:persistent4");
        MetadataSearchQuery msq = new MetadataSearchQuery(contentTypes);
        msq.addCriterion(new MetadataSearchCriterion(CriterionType.TITLE, "test"));
        msq.addCriterion(new MetadataSearchCriterion(CriterionType.CREATED_BY_OBJECTID, "user1234", LogicalOperator.AND));
        msq.addCriterion(new MetadataSearchCriterion(CriterionType.LANGUAGE, "de", LogicalOperator.OR));
        msq.addCriterion(new MetadataSearchCriterion(CriterionType.GENRE, "journal", LogicalOperator.NOT));
        String query = msq.getCqlQuery();
        String expected = "( ( escidoc.any-title=\"test\" )  and  ( escidoc.created-by.objid=\"user1234\" )  or  ( escidoc.publication.language=\"de\" )  not  ( escidoc.publication.type=\"journal\" ) ) and  ( escidoc.content-model.objid=\"escidoc:persistent4\" ) ";
        assertNotNull(query);
        assertEquals(expected, query);
        
    }
    
    @Test
    public void testBuildMetadataDateQuery() throws Exception
    {
        logger.info("Testing simple metadata search query transformation with dates and boolean operators");
        
        ArrayList<String> contentTypes = new ArrayList<String>();
        contentTypes.add("escidoc:persistent4");
        MetadataSearchQuery msq = new MetadataSearchQuery(contentTypes);
        
        ArrayList<CriterionType> criterions = new ArrayList<CriterionType>();
        criterions.add(CriterionType.DATE_ACCEPTED);
        msq.addCriterion(new MetadataDateSearchCriterion(criterions, "2008-05-15", "2008-10-08"));
        
        String query = msq.getCqlQuery();
        logger.debug(query);
        String expected = "( (  ( escidoc.publication.dateAccepted>=\"2008\\-05\\-15\" and escidoc.publication.dateAccepted<=\"2008\\-10\\-08\" )  ) ) and  ( escidoc.content-model.objid=\"escidoc:persistent4\" ) ";
        assertNotNull(query);
        assertEquals(expected, query);
        
    }
    @Test
    public void testBuildMetadataComponentQuery() throws Exception
    {
        logger.info("Testing simple metadata search for components");
        
        ArrayList<String> contentTypes = new ArrayList<String>();
        contentTypes.add("escidoc:persistent4");
        MetadataSearchQuery msq = new MetadataSearchQuery(contentTypes);
        
        ArrayList<CriterionType> criterions = new ArrayList<CriterionType>();
        criterions.add(CriterionType.COMPONENT_VISIBILITY);
        criterions.add(CriterionType.COMPONENT_ACCESSABILITY);
        criterions.add(CriterionType.COMPONENT_CONTENT_CATEGORY);
        msq.addCriterion(new MetadataSearchCriterion(criterions, "argument"));
        
        String query = msq.getCqlQuery();
        logger.debug(query);
        String expected = "( ( (escidoc.component.visibility=\"argument\" or escidoc.component.creation-date=\"argument\" or escidoc.component.content-category=\"argument\") ) ) and  ( escidoc.content-model.objid=\"escidoc:persistent4\" ) ";
        assertNotNull(query);
        assertEquals(expected, query);
        
    }
    
    
    @Test
    public void testBuildMetadataQueryWithEmptySearchString() throws Exception
    {
        logger.info("Testing simple metadata search query transformation with dates and boolean operators");
        
        ArrayList<String> contentTypes = new ArrayList<String>();
        contentTypes.add("escidoc:persistent4");
        MetadataSearchQuery msq = new MetadataSearchQuery(contentTypes);
        
        msq.addCriterion(new MetadataSearchCriterion(CriterionType.COMPONENT_ACCESSABILITY));
        
        String query = msq.getCqlQuery();
        logger.debug(query);
        String expected = "( ( escidoc.component.creation-date>\"''\" ) ) and  ( escidoc.content-model.objid=\"escidoc:persistent4\" ) ";
        assertNotNull(query);
        assertEquals(expected, query);
        
    }
    
    @Test
    public void testMetadataQueryWithSubCriteria() throws Exception
    {
        logger.info("Testing simple metadata search query transformation with dates and boolean operators");
        
        ArrayList<String> contentTypes = new ArrayList<String>();
        contentTypes.add("escidoc:persistent4");
        MetadataSearchQuery msq = new MetadataSearchQuery(contentTypes);
        
        msq.addCriterion(new MetadataSearchCriterion(CriterionType.COMPONENT_ACCESSABILITY, "test1"));
        
        MetadataSearchCriterion crit = new MetadataSearchCriterion(CriterionType.COMPONENT_VISIBILITY, "test2", LogicalOperator.OR);
        
        MetadataSearchCriterion crit2 = new MetadataSearchCriterion(CriterionType.ANY, "subtest1", LogicalOperator.AND);
        MetadataSearchCriterion crit3 = new MetadataSearchCriterion(CriterionType.ANY, "subtest2", LogicalOperator.OR);
        
        MetadataSearchCriterion crit4 = new MetadataSearchCriterion(CriterionType.ANY, "subsubtest1", LogicalOperator.AND);
        MetadataSearchCriterion crit5 = new MetadataSearchCriterion(CriterionType.ANY, "subsubtest2", LogicalOperator.OR);
        
        crit2.addSubCriteria(crit4);
        crit2.addSubCriteria(crit5);
        
        crit.addSubCriteria(crit2);
        crit.addSubCriteria(crit3);
          
        msq.addCriterion(crit);
        
        String query = msq.getCqlQuery();
        logger.debug(query);
        String expected = "( ( escidoc.component.creation-date=\"test1\" )  or  ( escidoc.component.visibility=\"test2\" ) and (  ( escidoc.metadata=\"subtest1\" ) and (  ( escidoc.metadata=\"subsubtest1\" )  or  ( escidoc.metadata=\"subsubtest2\" )  )  or  ( escidoc.metadata=\"subtest2\" )  ) ) and  ( escidoc.content-model.objid=\"escidoc:persistent4\" ) ";
        assertNotNull(query);
        assertEquals(expected, query);
        
    }
    
    @Test
    public void testMetadataQueryWithSubCriteriaRightTree() throws Exception
    {
        logger.info("Testing simple metadata search query transformation with dates and boolean operators");
        
        ArrayList<String> contentTypes = new ArrayList<String>();
        contentTypes.add("escidoc:persistent4");
        MetadataSearchQuery msq = new MetadataSearchQuery(contentTypes);
        
        msq.addCriterion(new MetadataSearchCriterion(CriterionType.COMPONENT_ACCESSABILITY, "test1"));
        
        MetadataSearchCriterion crit = new MetadataSearchCriterion(CriterionType.COMPONENT_VISIBILITY, "test2", LogicalOperator.OR);
        
        MetadataSearchCriterion crit2 = new MetadataSearchCriterion(CriterionType.ANY, "subtest1", LogicalOperator.AND);
        
        MetadataSearchCriterion crit4 = new MetadataSearchCriterion(CriterionType.ANY, "subsubtest1", LogicalOperator.AND);
        
        crit2.addSubCriteria(crit4);
        
        crit.addSubCriteria(crit2);
          
        msq.addCriterion(crit);
        
        String query = msq.getCqlQuery();
        logger.debug(query);
        String expected = "( ( escidoc.component.creation-date=\"test1\" )  or  ( escidoc.component.visibility=\"test2\" ) and (  ( escidoc.metadata=\"subtest1\" ) and (  ( escidoc.metadata=\"subsubtest1\" )  )  ) ) and  ( escidoc.content-model.objid=\"escidoc:persistent4\" ) ";
        assertNotNull(query);
        assertEquals(expected, query);
        
    }
    
    @Test
    public void testCopyrightIndex() throws Exception
    {
        logger.info("Testing copyright index");
        
        ArrayList<String> contentTypes = new ArrayList<String>();
        contentTypes.add("escidoc:persistent4");
        MetadataSearchQuery msq = new MetadataSearchQuery(contentTypes);
        
        msq.addCriterion(new MetadataSearchCriterion(CriterionType.COPYRIGHT_DATE, "testing" ));
        
        String query = msq.getCqlQuery();
        logger.debug(query);
        String expected = "( ( escidoc.component.file.dateCopyrighted=\"testing\" ) ) and  ( escidoc.content-model.objid=\"escidoc:persistent4\" ) ";
        assertNotNull(query);
        assertEquals(expected, query);       
    }
    
    @Test
    public void testSearchDateParsing() throws Exception {
        logger.info("Testing SearchDateParsing");
        SearchDate searchdate = new SearchDate("2007");
        assertEquals(SearchDate.DateType.Year, searchdate.getDateType());
        assertEquals("2007", searchdate.toString());
        searchdate = new SearchDate("2007-12");
        assertEquals(SearchDate.DateType.Year_Month, searchdate.getDateType());
        assertEquals("2007-12", searchdate.toString());
        searchdate = new SearchDate("2007-12-11");
        assertEquals(SearchDate.DateType.Year_Month_Day, searchdate.getDateType());
        assertEquals("2007-12-11", searchdate.toString());
    }
    
    @Test (expected=java.text.ParseException.class)
    public void testSearchDateParsingError() throws Exception {
        logger.info("Testing SearchDateParsingError"); 
        SearchDate searchdate = new SearchDate("abc");
        assertNull( searchdate );
    }  
}
