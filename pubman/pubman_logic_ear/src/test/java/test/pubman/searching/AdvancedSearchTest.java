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

package test.pubman.searching;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import test.pubman.TestBase;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.PubItemRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.MdsPublicationVO.Genre;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PersonVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.escidoc.services.pubman.PubItemDepositing;
import de.mpg.escidoc.services.pubman.PubItemSearching;
import de.mpg.escidoc.services.pubman.valueobjects.AnyFieldCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.DateCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.EventCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.GenreCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.IdentifierCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.OrganizationCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.PersonCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.SourceCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.TitleCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.TopicCriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO.LogicOperator;
import de.mpg.escidoc.services.pubman.valueobjects.DateCriterionVO.DateType;

/**
 * Test class for advanced search.
 * 
 * @author Hugo Niedermaier
 */
public class AdvancedSearchTest extends TestBase
{
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(AdvancedSearchTest.class);

    private PubItemSearching pubSearching;
    private XmlTransforming xmlTransforming;
    private PubItemDepositing pubItemDepositing;

    /**
     * @throws Exception
     */
    @Before
    public void setUpBefore() throws Exception
    {
        pubSearching = (PubItemSearching)getService(PubItemSearching.SERVICE_NAME);
        xmlTransforming = (XmlTransforming)getService(XmlTransforming.SERVICE_NAME);
        pubItemDepositing = (PubItemDepositing)getService(PubItemDepositing.SERVICE_NAME);
        
        
        
    }

    /**
     * Test method for Person
     * all Creator.Person.CompleteName with Creator.CreatorType = "person"
     * {@link de.mpg.escidoc.services.pubman.searching.PubItemSearchingBean#advancedSearch(java.util.List)}.
     * 
     * @throws Exception
     */
    @Test
    public void testPersonSearch() throws Exception
    {   
        AccountUserVO user = getUserTestDepScientistWithHandle();

        // create PubItem and submit (automatically releases the pubItem)
        PubItemVO myItem = getNewPubItemWithoutFiles();
        PersonVO personVO = new PersonVO();
        personVO.setFamilyName("Niedermaier");
        personVO.setGivenName("Hugo");
        personVO.setCompleteName( "Hugo Niedermaier" );
        CreatorVO creator = new CreatorVO();
        creator.setRole(CreatorRole.PAINTER);
        creator.setPerson(personVO);
        myItem.getMetadata().getCreators().add( creator );

        String itemXML = xmlTransforming.transformToItem(myItem);
        logger.debug("itemXML for testPersonSearch():");
        logger.debug(itemXML);

        PubItemRO myItemRef = pubItemDepositing.submitPubItem(myItem, "Test Submit", user).getReference();
        logger.info("Item '" + myItemRef.getObjectId() + "' submitted.");

        // wait a little bit for indexing...
        logger.debug("Waiting 15 seconds to let the framework indexing happen...");
        Thread.sleep(15000);        

        ArrayList<CriterionVO> criterionVOList = new ArrayList<CriterionVO>();

        PersonCriterionVO personCriterionVO = new PersonCriterionVO();
        personCriterionVO.setSearchString("Niedermaier");
        criterionVOList.add(personCriterionVO);
        List<CreatorRole> role = new ArrayList<CreatorRole>();
        role.add(CreatorRole.PAINTER);
        personCriterionVO.setCreatorRole(role);
        
        List<PubItemResultVO> searchResultList = pubSearching.advancedSearch(criterionVOList, null);
        assertNotNull(searchResultList);
        boolean itemFound = false;
        for (PubItemResultVO item:searchResultList)
        {
            logger.info("Found item '"+item.getReference().getObjectId()+"'.");
            if (item.getReference().equals(myItemRef))
            {
                itemFound = true;
            }
        }
        assertTrue("Could not find the created item!", itemFound);
    }
    
    /**
     * Test method for Identifier
     * ID and PID of item, PID of files
     * {@link de.mpg.escidoc.services.pubman.searching.PubItemSearchingBean#advancedSearch(java.util.List)}.
     * 
     * @throws Exception
     */
    @Test
    public void testIdentifierSearch() throws Exception
    {   
        AccountUserVO user = getUserTestDepScientistWithHandle();
   
        // create PubItem and submit (automatically releases the pubItem)
        PubItemVO myItem = getNewPubItemWithoutFiles();
        OrganizationVO creatorOrg = new OrganizationVO();
        TextVO textVO = new TextVO( "The creating affiliation" );
        creatorOrg.setName(textVO);
        creatorOrg.setIdentifier(MPG_TEST_AFFILIATION);
        myItem.getMetadata().getCreators().add(new CreatorVO(creatorOrg, CreatorRole.AUTHOR));
   
        String itemXML = xmlTransforming.transformToItem(myItem);
        logger.debug("itemXML for testIdentifierSearch():");
        logger.debug(itemXML);
   
        PubItemRO myItemRef = pubItemDepositing.submitPubItem(myItem, "Test Submit", user).getReference();
        
        logger.info("Item '" + myItemRef.getObjectId() + "' submitted.");
   
        // wait a little bit for indexing...
        logger.debug("Waiting 15 seconds to let the framework indexing happen...");
        Thread.sleep(15000);        
   
        ArrayList<CriterionVO> criterionVOList = new ArrayList<CriterionVO>();
   
        IdentifierCriterionVO identifierCriterionVO = new IdentifierCriterionVO();
        identifierCriterionVO.setSearchString(myItemRef.getObjectId());
        criterionVOList.add(identifierCriterionVO);
        
        List<PubItemResultVO> searchResultList = pubSearching.advancedSearch(criterionVOList, null );
        assertNotNull(searchResultList);
        boolean itemFound = false;
        for (PubItemResultVO item:searchResultList)
        {
            logger.info("Found item '"+item.getReference().getObjectId()+"'.");
            if (item.getReference().equals(myItemRef))
            {
                itemFound = true;
            }
        }
        assertTrue("Could not find the created item!", itemFound);
    }
    
    /**
     * Test method for Topic
     * Publication.Title, Publication.AlternativeTitle, Publication.TableOfContents, Publication.Abstract and Publication.Subject
     * for each language separately and for all languages at once
     * {@link de.mpg.escidoc.services.pubman.searching.PubItemSearchingBean#advancedSearch(java.util.List)}.
     * 
     * @throws Exception
     */
    @Test
    public void testTopicSearch() throws Exception
    {   
        AccountUserVO user = getUserTestDepScientistWithHandle();
   
        // create PubItem and submit (automatically releases the pubItem)
        PubItemVO myItem = getNewPubItemWithoutFiles();
        OrganizationVO creatorOrg = new OrganizationVO();
        TextVO textVO = new TextVO( "The creating affiliation" );
        creatorOrg.setName(textVO);
        creatorOrg.setIdentifier(MPG_TEST_AFFILIATION);
        myItem.getMetadata().getCreators().add(new CreatorVO(creatorOrg, CreatorRole.AUTHOR));
        TextVO subject = new TextVO( "testing" );
        myItem.getMetadata().setTitle( subject );
   
        String itemXML = xmlTransforming.transformToItem(myItem);
        logger.debug("itemXML for testTopicSearch():");
        logger.debug(itemXML);
   
        PubItemRO myItemRef = pubItemDepositing.submitPubItem(myItem, "Test Submit", user).getReference();
        logger.info("Item '" + myItemRef.getObjectId() + "' submitted.");
   
        // wait a little bit for indexing...
        logger.debug("Waiting 15 seconds to let the framework indexing happen...");
        Thread.sleep(15000);        
   
        ArrayList<CriterionVO> criterionVOList = new ArrayList<CriterionVO>();
   
        TopicCriterionVO topicCriterionVO = new TopicCriterionVO();
        topicCriterionVO.setSearchString("testing");
        criterionVOList.add(topicCriterionVO);
        
        List<PubItemResultVO> searchResultList = pubSearching.advancedSearch(criterionVOList, null );
        assertNotNull(searchResultList);
        boolean itemFound = false;
        for (PubItemResultVO item:searchResultList)
        {
            logger.info("Found item '"+item.getReference().getObjectId()+"'.");
            if (item.getReference().equals(myItemRef))
            {
                itemFound = true;
            }
        }
        assertTrue("Could not find the created item!", itemFound);
    }

      /**
      * Test method for Title
      * Publication.Title and Publication.AlternativeTitle for each language separately and for all languages at once
      * {@link de.mpg.escidoc.services.pubman.searching.PubItemSearchingBean#advancedSearch(java.util.List)}.
      * 
      * @throws Exception
      */
     @Test
     public void testTitleSearch() throws Exception
     {   
         AccountUserVO user = getUserTestDepScientistWithHandle();
    
         // create PubItem and submit (automatically releases the pubItem)
         PubItemVO myItem = getNewPubItemWithoutFiles();
         OrganizationVO creatorOrg = new OrganizationVO();
         TextVO textVO = new TextVO( "The creating affiliation" );
         creatorOrg.setName( textVO );
         creatorOrg.setIdentifier(MPG_TEST_AFFILIATION);
         myItem.getMetadata().getCreators().add(new CreatorVO(creatorOrg, CreatorRole.AUTHOR));
         myItem.getMetadata().setTitle(new TextVO("TitelVO"));
    
         String itemXML = xmlTransforming.transformToItem(myItem);
         logger.debug("itemXML for testTitleSearch():");
         logger.debug(itemXML);
    
         PubItemRO myItemRef = pubItemDepositing.submitPubItem(myItem, "Test Submit", user).getReference();
         logger.info("Item '" + myItemRef.getObjectId() + "' submitted.");
    
         // wait a little bit for indexing...
         logger.debug("Waiting 15 seconds to let the framework indexing happen...");
         Thread.sleep(15000);        
    
         ArrayList<CriterionVO> criterionVOList = new ArrayList<CriterionVO>();
    
         TitleCriterionVO titleCriterionVO = new TitleCriterionVO();
         titleCriterionVO.setSearchString("TitelVO");
         criterionVOList.add(titleCriterionVO);
         
         List<PubItemResultVO> searchResultList = pubSearching.advancedSearch(criterionVOList, null );
         assertNotNull(searchResultList);
         boolean itemFound = false;
         for (PubItemResultVO item:searchResultList)
         {
             logger.info("Found item '"+item.getReference().getObjectId()+"'.");
             if (item.getReference().equals(myItemRef))
             {
                 itemFound = true;
             }
         }
         assertTrue("Could not find the created item!", itemFound);
     }

    /**
     * Test method for Event
     * all Event.Title,Event.AlternativeTitle and Event.Place for each language separately and for all languages at once
     * {@link de.mpg.escidoc.services.pubman.searching.PubItemSearchingBean#advancedSearch(java.util.List)}.
     * 
     * @throws Exception
     */
    @Test
    public void testEventSearch() throws Exception
    {   
        AccountUserVO user = getUserTestDepScientistWithHandle();

        // create PubItem and submit (automatically releases the pubItem)
        PubItemVO myItem = getNewPubItemWithoutFiles();
        OrganizationVO creatorOrg = new OrganizationVO();
        TextVO textVO = new TextVO( "The creating affiliation" );
        creatorOrg.setName(textVO);
        creatorOrg.setIdentifier(MPG_TEST_AFFILIATION);
        myItem.getMetadata().getCreators().add(new CreatorVO(creatorOrg, CreatorRole.AUTHOR));
        EventVO eventVO = new EventVO();
        eventVO.setTitle(new TextVO("TitelEventVO"));
        myItem.getMetadata().setEvent(eventVO);

        String itemXML = xmlTransforming.transformToItem(myItem);
        logger.debug("itemXML for testEventSearch():");
        logger.debug(itemXML);

        PubItemRO myItemRef = pubItemDepositing.submitPubItem(myItem, "Test Submit", user).getReference();
        logger.info("Item '" + myItemRef.getObjectId() + "' submitted.");

        // wait a little bit for indexing...
        logger.debug("Waiting 15 seconds to let the framework indexing happen...");
        Thread.sleep(15000);        

        ArrayList<CriterionVO> criterionVOList = new ArrayList<CriterionVO>();

        EventCriterionVO eventCriterionVO = new EventCriterionVO();
        eventCriterionVO.setSearchString("TitelEventVO");
        criterionVOList.add(eventCriterionVO);
        
        List<PubItemResultVO> searchResultList = pubSearching.advancedSearch(criterionVOList, null);
        assertNotNull(searchResultList);
        boolean itemFound = false;
        for (PubItemResultVO item:searchResultList)
        {
            logger.info("Found item '"+item.getReference().getObjectId()+"'.");
            if (item.getReference().equals(myItemRef))
            {
                itemFound = true;
            }
        }
        assertTrue("Could not find the created item!", itemFound);
    }

    /**
     * Test method for Source
     * all Source.Title and Source.AlternativeTitle for each language separately and for all languages at once
     * {@link de.mpg.escidoc.services.pubman.searching.PubItemSearchingBean#advancedSearch(java.util.List)}.
     * 
     * @throws Exception
     */
    @Test
    public void testSourceSearch() throws Exception
    {   
        AccountUserVO user = getUserTestDepScientistWithHandle();

        // create PubItem and submit (automatically releases the pubItem)
        PubItemVO myItem = getNewPubItemWithoutFiles();
        OrganizationVO creatorOrg = new OrganizationVO();
        TextVO textVO = new TextVO( "The creating affiliation" );
        creatorOrg.setName(textVO);
        creatorOrg.setIdentifier(MPG_TEST_AFFILIATION);
        myItem.getMetadata().getCreators().add(new CreatorVO(creatorOrg, CreatorRole.AUTHOR));
        SourceVO sourceVO = new SourceVO();
        sourceVO.setTitle(new TextVO("TitelSourceVO"));
        myItem.getMetadata().getSources().add(sourceVO);

        String itemXML = xmlTransforming.transformToItem(myItem);
        logger.debug("itemXML for testSourceSearch():");
        logger.debug(itemXML);

        PubItemRO myItemRef = pubItemDepositing.submitPubItem(myItem, "Test Submit", user).getReference();
        logger.info("Item '" + myItemRef.getObjectId() + "' submitted.");

        // wait a little bit for indexing...
        logger.debug("Waiting 15 seconds to let the framework indexing happen...");
        Thread.sleep(15000);        

        ArrayList<CriterionVO> criterionVOList = new ArrayList<CriterionVO>();

        SourceCriterionVO sourceCriterionVO = new SourceCriterionVO();
        sourceCriterionVO.setSearchString("TitelSourceVO");
        criterionVOList.add(sourceCriterionVO);
        
        List<PubItemResultVO> searchResultList = pubSearching.advancedSearch(criterionVOList, null);
        assertNotNull(searchResultList);
        boolean itemFound = false;
        for (PubItemResultVO item:searchResultList)
        {
            logger.info("Found item '"+item.getReference().getObjectId()+"'.");
            if (item.getReference().equals(myItemRef))
            {
                itemFound = true;
            }
        }
        assertTrue("Could not find the created item!", itemFound);
    }

    /**
     * Test method for Organization
     * all Organization.Name for each language separately and for all languages at once
     * {@link de.mpg.escidoc.services.pubman.searching.PubItemSearchingBean#advancedSearch(java.util.List)}.
     * 
     * @throws Exception
     */
    @Test
    public void testOrganizationSearch() throws Exception
    {   
        AccountUserVO user = getUserTestDepScientistWithHandle();

        // create PubItem and submit (automatically releases the pubItem)
        PubItemVO myItem = getNewPubItemWithoutFiles();
        OrganizationVO creatorOrg = new OrganizationVO();
        TextVO textVO = new TextVO( "testaffilliation" );
        creatorOrg.setName(textVO);
        creatorOrg.setIdentifier(MPG_TEST_AFFILIATION);
        myItem.getMetadata().getCreators().add(new CreatorVO(creatorOrg, CreatorRole.AUTHOR));

        String itemXML = xmlTransforming.transformToItem(myItem);
        logger.debug("itemXML for testOrganizationSearch():");
        logger.debug(itemXML);

        PubItemRO myItemRef = pubItemDepositing.submitPubItem(myItem, "Test Submit", user).getReference();
        logger.info("Item '" + myItemRef.getObjectId() + "' submitted.");

        // wait a little bit for indexing...
        logger.debug("Waiting 15 seconds to let the framework indexing happen...");
        Thread.sleep(15000);        

        ArrayList<CriterionVO> criterionVOList = new ArrayList<CriterionVO>();

        OrganizationCriterionVO organizationCriterionVO = new OrganizationCriterionVO();
        organizationCriterionVO.setSearchString("testaffilliation");
        criterionVOList.add(organizationCriterionVO);
        
        List<PubItemResultVO> searchResultList = pubSearching.advancedSearch(criterionVOList, null );
        assertNotNull(searchResultList);
        boolean itemFound = false;
        for (PubItemResultVO item:searchResultList)
        {
            logger.info("Found item '"+item.getReference().getObjectId()+"'.");
            if (item.getReference().equals(myItemRef))
            {
                itemFound = true;
            }
        }
        assertTrue("Could not find the created item!", itemFound);
    }

    /**
     * Test method for Genre
     * Publication.Genre
     * {@link de.mpg.escidoc.services.pubman.searching.PubItemSearchingBean#advancedSearch(java.util.List)}.
     * 
     * @throws Exception
     */
    @Test
    public void testGenreSearch() throws Exception
    {   
        AccountUserVO user = getUserTestDepScientistWithHandle();

        // create PubItem and submit (automatically releases the pubItem)
        PubItemVO myItem = getNewPubItemWithoutFiles();
        OrganizationVO creatorOrg = new OrganizationVO();
        TextVO textVO = new TextVO( "The creating affiliation" );
        creatorOrg.setName(textVO);
        creatorOrg.setIdentifier(MPG_TEST_AFFILIATION);
        myItem.getMetadata().getCreators().add(new CreatorVO(creatorOrg, CreatorRole.AUTHOR));
        myItem.getMetadata().setGenre(Genre.MANUSCRIPT);

        String itemXML = xmlTransforming.transformToItem(myItem);
        logger.debug("itemXML for testGenreSearch():");
        logger.debug(itemXML);

        PubItemRO myItemRef = pubItemDepositing.submitPubItem(myItem, "Test Submit", user).getReference();
        logger.info("Item '" + myItemRef.getObjectId() + "' submitted.");

        // wait a little bit for indexing...
        logger.debug("Waiting 5 seconds to let the framework indexing happen...");
        Thread.sleep(5000);        

        ArrayList<CriterionVO> criterionVOList = new ArrayList<CriterionVO>();

        GenreCriterionVO genreCriterionVO = new GenreCriterionVO();
        List<Genre> list = new ArrayList<Genre>();
        list.add(Genre.MANUSCRIPT);                
        genreCriterionVO.setGenre(list);
        criterionVOList.add(genreCriterionVO);
        
        List<PubItemResultVO> searchResultList = pubSearching.advancedSearch(criterionVOList, null);
        assertNotNull(searchResultList);
        boolean itemFound = false;
        for (PubItemResultVO item:searchResultList)
        {
            logger.info("Found item '"+item.getReference().getObjectId()+"'.");
            if (item.getReference().equals(myItemRef))
            {
                itemFound = true;
            }
        }
        assertTrue("Could not find the created item!", itemFound);
    }

    /**
     * Test method for Date
     * Publication.Date
     * {@link de.mpg.escidoc.services.pubman.searching.PubItemSearchingBean#advancedSearch(java.util.List)}.
     * 
     * @throws Exception
     */
    @Test
    public void testDateSearch() throws Exception
    {   
        AccountUserVO user = getUserTestDepScientistWithHandle();

        // create PubItem and submit (automatically releases the pubItem)
        PubItemVO myItem = getNewPubItemWithoutFiles();
        OrganizationVO creatorOrg = new OrganizationVO();
        TextVO textVO = new TextVO( "The creating affiliation" );
        creatorOrg.setName(textVO);
        creatorOrg.setIdentifier( MPG_TEST_AFFILIATION );
        myItem.getMetadata().getCreators().add(new CreatorVO(creatorOrg, CreatorRole.AUTHOR));
        myItem.getMetadata().setDateCreated("1999-01-02");

        String itemXML = xmlTransforming.transformToItem(myItem);
        logger.debug("itemXML for testDateSearch():");
        logger.debug(itemXML);

        PubItemRO myItemRef = pubItemDepositing.submitPubItem(myItem, "Test Submit", user).getReference();
        logger.info("Item '" + myItemRef.getObjectId() + "' submitted.");

        // wait a little bit for indexing...
        logger.debug("Waiting 15 seconds to let the framework indexing happen...");
        Thread.sleep(15000);        

        ArrayList<CriterionVO> criterionVOList = new ArrayList<CriterionVO>();

        DateCriterionVO dateCriterionVO = new DateCriterionVO();
        dateCriterionVO.setFrom("1999-01-02");
        dateCriterionVO.setTo("2007-10-08");
        List<DateType> dateType = new ArrayList<DateType>();
        // dateType.add(DateType.CREATED);                
        dateCriterionVO.setDateType(dateType);
        criterionVOList.add(dateCriterionVO);
        
        List<PubItemResultVO> searchResultList = pubSearching.advancedSearch(criterionVOList, null);
        assertNotNull(searchResultList);
        boolean itemFound = false;
        for (PubItemResultVO item:searchResultList)
        {
            logger.info("Found item '"+item.getReference().getObjectId()+"'.");
            if (item.getReference().equals(myItemRef))
            {
                itemFound = true;
            }
        }
        assertTrue("Could not find the created item!", itemFound);
    }
    
    /**
     * Test method for the Search Logic with all Criteria and all Operators between
     * {@link de.mpg.escidoc.services.pubman.searching.PubItemSearchingBean#advancedSearch(java.util.List)}.
     * 
     * @throws Exception
     */
    @Test
    public void testSearch() throws Exception
    {   
        ArrayList<CriterionVO> criterionVOList = new ArrayList<CriterionVO>();

        TitleCriterionVO titleCriterionVO = new TitleCriterionVO();
        titleCriterionVO.setSearchString("testTitle");
        titleCriterionVO.setLogicOperator(LogicOperator.AND);                    
        criterionVOList.add(titleCriterionVO);        
        
        AnyFieldCriterionVO anyFieldCriterionVO = new AnyFieldCriterionVO();
        anyFieldCriterionVO.setSearchString("testAnyFieldFTS");
        anyFieldCriterionVO.setIncludeFiles(true);
        anyFieldCriterionVO.setLogicOperator(LogicOperator.OR);                    
        criterionVOList.add(anyFieldCriterionVO);

        PersonCriterionVO personCriterionVO = new PersonCriterionVO();
        personCriterionVO.setSearchString("testPerson");
        personCriterionVO.setLogicOperator(LogicOperator.NOT);
        List<CreatorRole> role = new ArrayList<CreatorRole>();
        role.add(CreatorRole.AUTHOR);                
        role.add(CreatorRole.EDITOR);                
        personCriterionVO.setCreatorRole(role);
        criterionVOList.add(personCriterionVO);        

        GenreCriterionVO genreCriterionVO = new GenreCriterionVO();
        List<Genre> genres = new ArrayList<Genre>();
        genres.add(Genre.ARTICLE);                
        genres.add(Genre.BOOK);                
        genreCriterionVO.setGenre(genres);
        genreCriterionVO.setLogicOperator(LogicOperator.AND);
        criterionVOList.add(genreCriterionVO);

        DateCriterionVO dateCriterionVO = new DateCriterionVO();
        dateCriterionVO.setFrom("1964-11-16");
        dateCriterionVO.setTo("2007-10-08");
        List<DateType> dateType = new ArrayList<DateType>();
        dateType.add(DateType.ACCEPTED);                
        dateType.add(DateType.CREATED);                
        dateCriterionVO.setDateType(dateType);
        dateCriterionVO.setLogicOperator(LogicOperator.OR);
        criterionVOList.add(dateCriterionVO);

        OrganizationCriterionVO organizationCriterionVO = new OrganizationCriterionVO();
        organizationCriterionVO.setSearchString("testOrganization");
        organizationCriterionVO.setLanguage("de");
        organizationCriterionVO.setLogicOperator(LogicOperator.NOT);
        criterionVOList.add(organizationCriterionVO);

        SourceCriterionVO sourceCriterionVO = new SourceCriterionVO();
        sourceCriterionVO.setSearchString("testSource");
        sourceCriterionVO.setLanguage("de");
        sourceCriterionVO.setLogicOperator(LogicOperator.AND);
        criterionVOList.add(sourceCriterionVO);

        EventCriterionVO eventCriterionVO = new EventCriterionVO();
        eventCriterionVO.setSearchString("testEvent");
        eventCriterionVO.setLanguage("de");
        eventCriterionVO.setLogicOperator(LogicOperator.OR);
        criterionVOList.add(eventCriterionVO);

        IdentifierCriterionVO identifierCriterionVO = new IdentifierCriterionVO();
        identifierCriterionVO.setSearchString("testIdentifier");
        identifierCriterionVO.setLogicOperator(null);
        criterionVOList.add(identifierCriterionVO);        
        
        List<PubItemResultVO> searchResultList = null;
        
        
        searchResultList = pubSearching.advancedSearch( criterionVOList, null);
        	
        assertNotNull(searchResultList);
    }
}
