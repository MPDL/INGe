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
package de.mpg.escidoc.services.test.search;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.services.common.util.ObjectComparator;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.TaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.common.valueobjects.interfaces.SearchResultElement;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.FormatVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsFileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PublishingInfoVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.search.Search;
import de.mpg.escidoc.services.search.bean.SearchBean;
import de.mpg.escidoc.services.search.parser.ParseException;
import de.mpg.escidoc.services.search.query.ExportSearchQuery;
import de.mpg.escidoc.services.search.query.ExportSearchResult;
import de.mpg.escidoc.services.search.query.MetadataDateSearchCriterion;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion;
import de.mpg.escidoc.services.search.query.MetadataSearchQuery;
import de.mpg.escidoc.services.search.query.OrgUnitsSearchResult;
import de.mpg.escidoc.services.search.query.SearchQuery;
import de.mpg.escidoc.services.search.query.ItemContainerSearchResult;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.BooleanOperator;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.CriterionType;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.LogicalOperator;

/**
 * 
 * Integration tests for search service, using Metadata Search.
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class TestMetadataSearch extends TestSearchBase
{
    private static Logger logger = Logger.getLogger(TestMetadataSearch.class);
    

    
    /**
     * Searches for the title, using the ANY criterion (simple search without file).
     * @throws Exception
     */
    @Test
    public void testSimpleSearchForAny() throws Exception
    {
        
        //search the item
        MetadataSearchQuery query = getStandardQuery();
        query.addCriterion(new MetadataSearchCriterion(CriterionType.ANY, itemTitle));
        
        searchAndCompareResults(query, testItem);

    }
    
    /**
     * Searches for an item with file (simple search with file).
     * @throws Exception
     */
    @Test
    //@Ignore
    public void testSimpleSearchWithFile() throws Exception
    {
        
        //search the item
        MetadataSearchQuery query = getStandardQuery();
        query.addCriterion(new MetadataSearchCriterion(CriterionType.ANY_INCLUDE, itemTitle + " " + "\"Antoine de Saint-Exupery\""));
        
        searchAndCompareResults(query, testItem);

    }
    
    /**
     * Searches for 2 date ranges in 2 different dates.
     * @throws Exception
     */
    @Test
    public void testSearchForConcreteDates() throws Exception
    {
       
        //search the item
        
        MetadataSearchQuery query = getStandardQuery();
        
        ArrayList<CriterionType> criterionList = new ArrayList<CriterionType>();
        criterionList.add(CriterionType.DATE_ACCEPTED);
        query.addCriterion(new MetadataDateSearchCriterion(criterionList, "2008-05-01", "2008-05-31"));
        
        
        /*
        ArrayList<CriterionType> criterionList2 = new ArrayList<CriterionType>();
        criterionList2.add(CriterionType.DATE_CREATED);
        MetadataDateSearchCriterion mdsc = new MetadataDateSearchCriterion(criterionList2, "2005-04-01", "2005-04-31");
        //mdsc.setLogicalOperator(LogicalOperator.AND);
        query.addCriterion(mdsc);
        */
        /*
        ArrayList<CriterionType> criterionList2 = new ArrayList<CriterionType>();
        criterionList2.add(CriterionType.DATE_PUBLISHED_ONLINE);
        MetadataDateSearchCriterion mdsc = new MetadataDateSearchCriterion(criterionList2, "2008-01-01", "2008-01-01");
        mdsc.setLogicalOperator(LogicalOperator.AND);
        query.addCriterion(mdsc);
        */
        query.addCriterion(new MetadataSearchCriterion(CriterionType.TITLE, itemTitle, LogicalOperator.AND));
        
        searchAndCompareResults(query, testItem);

    }
    
    /**Searches for one date range in all dates.
     * 
     * @throws Exception
     */
    @Test
    public void testSearchForAnyDates() throws Exception
    {
       
        
        //search the item
        MetadataSearchQuery query = getStandardQuery();
        ArrayList<CriterionType> criterionList = new ArrayList<CriterionType>();
        criterionList.add(CriterionType.DATE_ANY);
        query.addCriterion(new MetadataDateSearchCriterion(criterionList, "2007-05-01", "2008-05-31"));
        
        query.addCriterion(new MetadataSearchCriterion(CriterionType.TITLE, itemTitle, LogicalOperator.AND));

        
        searchAndCompareResults(query, testItem);

    }
    
    
    /**
     * Searches for creator's name and role.
     * @throws Exception
     */
    @Test
    public void testSearchForCreator() throws Exception
    {

        //search the item for creator
        MetadataSearchQuery query = getStandardQuery();
        query.addCriterion(new MetadataSearchCriterion(CriterionType.PERSON, creatorsFamilyName));
        //query.addCriterion(new MetadataSearchCriterion(CriterionType.PERSON_ROLE, creatorsRole.toString(), LogicalOperator.AND));
        searchAndCompareResults(query, testItem);
        
        
    }
    
    /**
     * Searches for the source's title.
     * @throws Exception
     */
    @Test
    public void testSearchForSource() throws Exception
    {
        //search the item for source
        MetadataSearchQuery query = getStandardQuery();
        query.addCriterion(new MetadataSearchCriterion(CriterionType.SOURCE, sourceTitle));
        searchAndCompareResults(query, testItem);
        
    }
        

    /**
     * Searches for the event's title.
     * @throws Exception
     */
    @Test
    public void testSearchForEvent() throws Exception
    {
        //search the item for source
        MetadataSearchQuery query = getStandardQuery();
        query.addCriterion(new MetadataSearchCriterion(CriterionType.EVENT, eventTitle));
        searchAndCompareResults(query, testItem);
        
    }
    
    /**
     * Searches for the topic.
     * @throws Exception
     */
    @Test
    public void testSearchForTopic() throws Exception
    {
        //search the item for source
        MetadataSearchQuery query = getStandardQuery();
        query.addCriterion(new MetadataSearchCriterion(CriterionType.TOPIC, abstractText));
        searchAndCompareResults(query, testItem);
        
    }
    
    /**
     * Searches for the PID Identifier.
     * @throws Exception
     */
    @Test
    public void testSearchForPidIdentifier() throws Exception
    {
        //search the item for source
        MetadataSearchQuery query = getStandardQuery();
        query.addCriterion(new MetadataSearchCriterion(CriterionType.IDENTIFIER, itemPid));
        searchAndCompareResults(query, testItem);
        
    }
    
    /**
     * Searches for the escidoc Identifier.
     * @throws Exception
     */
    @Test
    public void testSearchForEscidocIdentifier() throws Exception
    {
        //search the item for source
        MetadataSearchQuery query = getStandardQuery();
        query.addCriterion(new MetadataSearchCriterion(CriterionType.IDENTIFIER, testItem.getVersion().getObjectId(), LogicalOperator.AND));
        searchAndCompareResults(query, testItem);
        
    }
    
    /**
     * Searches for the organization.
     * @throws Exception
     */
    @Test
    public void testSearchForOrganization() throws Exception
    {
        //search the item for source
        MetadataSearchQuery query = getStandardQuery();
        query.addCriterion(new MetadataSearchCriterion(CriterionType.ORGANIZATION, organizationName));
        
        
        searchAndCompareResults(query, testItem);
        
    }
    
    /**
     * Searches for the genre.
     * @throws Exception
     */
    @Test
    public void testSearchForGenre() throws Exception
    {
        //search the item for source
        MetadataSearchQuery query = getStandardQuery();
        query.addCriterion(new MetadataSearchCriterion(CriterionType.GENRE, genreName));
        query.addCriterion(new MetadataSearchCriterion(CriterionType.TITLE, itemTitle, LogicalOperator.AND));
        
        searchAndCompareResults(query, testItem);
        
    }
    

    /**
     * Searches for the item with multiple metadata in the query (Advanced Search).
     * @throws Exception
     */
    @Test
    //@Ignore
    public void testSearchForMultipleMetadata() throws Exception
    {
        //search the item for source
        MetadataSearchQuery query = getStandardQuery();
        query.addCriterion(new MetadataSearchCriterion(CriterionType.TITLE, itemTitle));
        query.addCriterion(new MetadataSearchCriterion(CriterionType.PERSON, creatorsFamilyName, LogicalOperator.AND));
        ArrayList<CriterionType> criterionList = new ArrayList<CriterionType>();
        criterionList.add(CriterionType.DATE_PUBLISHED_ONLINE);
        MetadataDateSearchCriterion mdsc = new MetadataDateSearchCriterion(criterionList, "2008-01-01", "2008-01-01");
        mdsc.setLogicalOperator(LogicalOperator.AND);
        query.addCriterion(mdsc);
        
        
        searchAndCompareResults(query, testItem);
        
    }
    
    /**
     * Creates an item with special characters and escaped characters and searches for it.
     * @throws Exception
     */
    //TODO: Get the test running after release 3.8.2
    @Test
    @Ignore
    public void testSearchForSpecialCharacters() throws Exception
    {
        AccountUserVO user = getUserTestDepLibWithHandle();
        //create new test item 
        
        PubItemVO myItem = getNewPubItemWithoutFiles();
        
        //title with special characters äöüß/@ §$%=`´'_
        String title = "Tößt. Itäm für @MPDL §23'1_2/5=6 àé 30,%" + System.nanoTime();
        myItem.getMetadata().getTitle().setValue(title);

        //creator name with escape characters
        String givenName = "Hans" + System.nanoTime();
        String familyName = "Meier-Müller (von{und&}|[evtl.]) zu^ Lauen+stein!" + System.nanoTime();
        myItem.getMetadata().getCreators().get(0).getPerson().setGivenName(givenName);
        myItem.getMetadata().getCreators().get(0).getPerson().setFamilyName(familyName);
        myItem.getMetadata().getCreators().get(0).getPerson().setCompleteName(givenName + " " +familyName);
        
        ItemVO createdItem = createItem(myItem, user);
        assertNotNull(createdItem);
          
        ItemVO releasedItem = submitAndReleaseItem(createdItem, user);
        assertNotNull(testItem);
        
        logger.info("Test Item for special characters created: object id=" + testItem.getVersion().getObjectId());
        
        // wait a little bit for indexing...
        // if test fails, the time given for indexing might be too short
        //(with Thread.sleep(2000) the test sometimes failed.
        Thread.sleep(5000);
        
        //search for title
        MetadataSearchQuery query = getStandardQuery();
        query.addCriterion(new MetadataSearchCriterion(CriterionType.TITLE, title));
        
        searchAndCompareResults(query, releasedItem);
        
        
        //search for creator
        query = getStandardQuery();
        query.addCriterion(new MetadataSearchCriterion(CriterionType.PERSON, givenName + " " + familyName));
        
        searchAndCompareResults(query, releasedItem);
        
        
        
    }
    
    /**
     * Searches for an item title using logical operator *. 
     * @throws Exception
     */
    @Test
    public void testSearchWithAsteriskLogicalOperators() throws Exception
    {
        
        //remvove last characters from title and replace with logical operator
        String searchTerm = itemTitle.substring(0, itemTitle.length() - 3);
        searchTerm = searchTerm + "*";
        MetadataSearchQuery query = getStandardQuery();
        query.addCriterion(new MetadataSearchCriterion(CriterionType.TITLE, searchTerm));
        
        searchAndCompareResults(query, testItem);

    }
    
    
    /**
     * Searches for an item title using logical operator ?. 
     * @throws Exception
     */
    @Test
    public void testSearchWithQuestionmarkLogicalOperator() throws Exception
    {
        
        //remvove last characters from title and replace with logical operator
        String searchTermStart = itemTitle.substring(0, itemTitle.length() - 5);
        String searchTermEnd = itemTitle.substring(itemTitle.length() - 4, itemTitle.length());
        String searchTerm = searchTermStart + "?" + searchTermEnd;

        System.out.println("SearchTerm: " + searchTerm);
        System.out.println("NormalTerm: " + itemTitle);
        
        MetadataSearchQuery query = getStandardQuery();
        query.addCriterion(new MetadataSearchCriterion(CriterionType.TITLE, searchTerm));
        
        searchAndCompareResults(query, testItem);

    }
    
    
    /**
     * Searches for an title that should not exist.
     * @throws Exception
     */
    @Test
    public void testSearchNoResults() throws Exception
    {
        //search the item for source
        String testTitle = "Der kleine Prinz" + System.nanoTime();
        MetadataSearchQuery query = getStandardQuery();
        query.addCriterion(new MetadataSearchCriterion(CriterionType.TITLE, testTitle, LogicalOperator.AND));
        
        ItemContainerSearchResult result = itemContainerSearch.searchForItemContainer(query);
        
        List<SearchResultElement> resultList = result.getResultList();
        
        assertEquals("Wrong number of search results", 0, resultList.size());   
    }
    
    
    /**
     * Searches for an title that should not exist.
     * @throws Exception
     */
    @Test
    public void testSearchEmptyString() throws Exception
    {
        //search the item for source
        ItemContainerSearchResult result;
        try
        {
            String testTitle = "";
            MetadataSearchQuery query = getStandardQuery();
            query.addCriterion(new MetadataSearchCriterion(CriterionType.TITLE, testTitle, LogicalOperator.AND));
            
            result = itemContainerSearch.searchForItemContainer(query);
            fail("ParseException expected");
        }
        catch (ParseException e)
        {
        }
        
       
    }
    
    @Test
    public void testOrganizationalSearch() throws Exception
    {
        OrgUnitsSearchResult result = null;
        try
        {
            String testTitle = "MPS";
            MetadataSearchQuery query = getStandardQuery();
            query.addCriterion(new MetadataSearchCriterion(CriterionType.ANY, testTitle, LogicalOperator.AND));
            
            result = itemContainerSearch.searchForOrganizationalUnits(query);
        }
        catch (ParseException e)
        {
        }
        
       
    }
    
    /**
     * Searches for the query and compares the result with the given item object for equality.
     * @param query The query for which is searched
     * @param itemToCompare The item to which the result is compared
     * @throws Exception
     */
    private void searchAndCompareResults(MetadataSearchQuery query, ItemVO itemToCompare) throws Exception
    {
        ItemContainerSearchResult result = itemContainerSearch.searchForItemContainer(query);
        List<SearchResultElement> resultList = result.getResultList();
        assertEquals("Wrong number of search results", 1, resultList.size());
        ItemVO resultItem = (ItemVO)resultList.get(0);
        ObjectComparator oc = new ObjectComparator(new ItemVO(itemToCompare), new ItemVO(resultItem));
        assertTrue(oc.toString(), oc.isEqual());
    }
    
    
    
    
  
    
    
    
    
    
    
    
}
