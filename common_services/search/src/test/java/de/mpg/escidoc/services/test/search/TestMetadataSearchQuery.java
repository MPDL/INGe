package de.mpg.escidoc.services.test.search;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import org.junit.Test;

import de.mpg.escidoc.services.search.query.MetadataDateSearchCriterion;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion;
import de.mpg.escidoc.services.search.query.MetadataSearchQuery;
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
        String expected = " ( escidoc.any-title=\"test\" )  and  ( escidoc.content-model.objid=\"escidoc:persistent4\" ) ";
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
        String expected = " ( escidoc.any-title=\"test\" )  and  ( escidoc.created-by.objid=\"user1234\" )  or  ( escidoc.language=\"de\" )  not  ( escidoc.any-genre=\"journal\" )  and  ( escidoc.content-model.objid=\"escidoc:persistent4\" ) ";
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
        String expected = " (  ( escidoc.dateAccepted>=\"2008\\-05\\-15\" and escidoc.dateAccepted<=\"2008\\-10\\-08\" )  )  and  ( escidoc.content-model.objid=\"escidoc:persistent4\" ) ";
        assertNotNull(query);
        assertEquals(expected, query);
        
    }
}
